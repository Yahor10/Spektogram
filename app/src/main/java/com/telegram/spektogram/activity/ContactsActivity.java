package com.telegram.spektogram.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.application.Constants;
import com.telegram.spektogram.contacts.Contact;
import com.telegram.spektogram.contacts.ContactFetcher;
import com.telegram.spektogram.contacts.ContactsAdapter;
import com.telegram.spektogram.enums.ContactType;
import com.telegram.spektogram.preferences.PreferenceUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends ActionBarActivity implements Client.ResultHandler,AdapterView.OnItemClickListener, ViewTreeObserver.OnGlobalLayoutListener {
    ArrayList<Contact> listContacts;
    ListView lvContacts;
    private  Map<String,TdApi.User>userMap =null;
    private ContactsAdapter adapterContacts;
    public static String EXTRA_TELEGRAM = "EXTRA_TELEGRAM";
    public static String EXTRA_NEW_GROUP = "EXTRA_NEW_GROUP";


    private  final BroadcastReceiver updateStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int id = intent.getIntExtra(ApplicationSpektogram.EXTRA_UPDATE_USER_ID, -1);
            final TdApi.GetUserFull func = new TdApi.GetUserFull(id);
            ApplicationSpektogram.getApplication(context).sendFunction(func, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    if(object instanceof TdApi.User) {
                        TdApi.User user = (TdApi.User) object;
                        userMap.remove(user.phoneNumber);
                        userMap.put(user.phoneNumber, user);
                        loadContacts();
                    }
                }
            });
        }
    };

    private  final BroadcastReceiver updateUserNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int id = intent.getIntExtra(ApplicationSpektogram.EXTRA_UPDATE_USER_ID, -1);
            final TdApi.GetUserFull func = new TdApi.GetUserFull(id);
            ApplicationSpektogram.getApplication(context).sendFunction(func, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    if(object instanceof TdApi.User) {
                        TdApi.User user = (TdApi.User) object;
                        userMap.remove(user.phoneNumber);
                        userMap.put(user.phoneNumber, user);
                        loadContacts();
                    }
                }
            });
        }
    };

    public static Intent buildStartIntent(Context context,boolean onlyTelegram){
        final Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra(EXTRA_TELEGRAM,onlyTelegram);
        return intent;
    }

    public static Intent buildStartIntent(Context context,boolean onlyTelegram,boolean newGroup){
        final Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra(EXTRA_TELEGRAM, onlyTelegram);
        intent.putExtra(EXTRA_NEW_GROUP, newGroup);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        restoreActionBar();

        lvContacts = (ListView) findViewById(R.id.lvContacts);
        lvContacts.setOnItemClickListener(this);
        lvContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        boolean onlyTelegram = getIntent().getBooleanExtra(EXTRA_TELEGRAM,false);

        if(PreferenceUtils.isOfflineMode(getBaseContext())){
            if(onlyTelegram){
                loadTelegramContacts();
            }else{
                loadContacts();
            }
        }else{
            ApplicationSpektogram.getApplication(this).sendFunction(new TdApi.GetContacts(), this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateStatusReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_USER_STATUS));
        registerReceiver(updateUserNameReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_USER_NAME));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(updateStatusReceiver);
            unregisterReceiver(updateStatusReceiver);
        }catch (Exception e) {
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setCustomView(R.layout.ab_main);

        final View customView = actionBar.getCustomView();
        final View viewById = customView.findViewById(R.id.title);
        String s= getString(R.string.app_name);

        SpannableString ss1=  new SpannableString(s);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        ss1.setSpan(bss, 0, 6, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 6, 0);// set color

        TextView tv= (TextView) findViewById(R.id.title);
        tv.setText(ss1);
    }

    private void loadContacts() {
        final ContactFetcher contactFetcher = new ContactFetcher(ContactsActivity.this, userMap);

        List<Contact>actions = new ArrayList<Contact>(3);
        final Contact object = new Contact("-1", "invite friend", ContactType.Action);
        actions.add(object);

        listContacts = contactFetcher.fetchAll(actions);
        adapterContacts = new ContactsAdapter(ContactsActivity.this, listContacts);
        lvContacts.setAdapter(adapterContacts);

    }

    private void loadTelegramContacts() {
        boolean createNewGroup = getIntent().getBooleanExtra(EXTRA_NEW_GROUP, false);

        List<Contact>actions = new ArrayList<Contact>(3);
        final Contact object = new Contact("-1", "new group", ContactType.Action);
        actions.add(object);

        if(createNewGroup){
            actions.clear();
        }

        final ContactFetcher contactFetcher = new ContactFetcher(this, userMap);
        ArrayList<Contact> listContacts = contactFetcher.fetchTelegramContacts(actions);
        ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts);
        lvContacts.setAdapter(adapterContacts);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_contacts, menu);
//        getMenuInflater().inflate(R.menu.menu_contacts, menu);


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem item = menu.findItem(R.id.action_t);
        CharSequence menuTitle = item.getTitle();

        SpannableString styledMenuTitle = new SpannableString(menuTitle);
        styledMenuTitle.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, menuTitle.length(), 0);
        item.setTitle(styledMenuTitle);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id){
            case R.id.action_accept:
                SparseBooleanArray checked = lvContacts.getCheckedItemPositions();
                int ids [] = new int[checked.size()];
                if(checked != null) {
                    for(int i = 0; i < checked.size();i++) {
                        final int keyAt = checked.keyAt(i);
                        ContactsAdapter adapter = (ContactsAdapter) getLvContacts().getAdapter();
                        final Contact contact = adapter.getItem(keyAt);
                        final TdApi.User user = contact.getUser();
                        ids[i] = user.id;
                    }

                    ApplicationSpektogram.getApplication(this).getClient().send(new TdApi.CreateGroupChat(ids, "test group chat"), new Client.ResultHandler() {
                        @Override
                        public void onResult(TdApi.TLObject object) {
                            Log.v(Constants.LOG_TAG,"CreateGroupChat" + object);
                        }
                    });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onResult(TdApi.TLObject object) {
        TdApi.Contacts contacts = (TdApi.Contacts) object;
        final TdApi.User[] users = contacts.users;
        userMap = new HashMap<>(users.length);

        for(TdApi.User user : users){
            userMap.put(user.phoneNumber,user);
        }

        Log.v(null, "hash map" + userMap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed()) {
                    boolean onlyTelegram = getIntent().getBooleanExtra(EXTRA_TELEGRAM, false);
                    if (onlyTelegram) {
                        loadTelegramContacts();
                    } else {
                        loadContacts();
                    }
                }

            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final SparseBooleanArray checkedItemPositions = lvContacts.getCheckedItemPositions();
        ContactsAdapter adapter = (ContactsAdapter) getLvContacts().getAdapter();
        adapter.notifyDataSetChanged();
    }

    public ListView getLvContacts() {
        return lvContacts;
    }

    @Override
    public void onGlobalLayout() {

    }
}
