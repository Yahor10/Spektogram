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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.telegram.spektogram.contacts.AllContactsFragment;
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

public class ContactsActivity extends ActionBarActivity implements Client.ResultHandler, AdapterView.OnItemClickListener, ViewTreeObserver.OnGlobalLayoutListener {
    ArrayList<Contact> listContacts;
    ListView lvContacts;
    private Map<String, TdApi.User> userMap = null;
    private ContactsAdapter adapterContacts;
    public static String EXTRA_TELEGRAM = "EXTRA_TELEGRAM";
    public static String EXTRA_NEW_GROUP = "EXTRA_NEW_GROUP";
    public static String EXTRA_NEW_MESSAGE = "EXTRA_NEW_MESSAGE";


    private final BroadcastReceiver updateStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int id = intent.getIntExtra(ApplicationSpektogram.EXTRA_UPDATE_USER_ID, -1);
            final TdApi.GetUserFull func = new TdApi.GetUserFull(id);
            ApplicationSpektogram.getApplication(context).sendFunction(func, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    if (object instanceof TdApi.User) {
                        TdApi.User user = (TdApi.User) object;
                        userMap.remove(user.phoneNumber);
                        userMap.put(user.phoneNumber, user);
                        loadContacts();
                    }
                }
            });
        }
    };

    private final BroadcastReceiver updateUserNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int id = intent.getIntExtra(ApplicationSpektogram.EXTRA_UPDATE_USER_ID, -1);
            final TdApi.GetUserFull func = new TdApi.GetUserFull(id);
            ApplicationSpektogram.getApplication(context).sendFunction(func, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    if (object instanceof TdApi.User) {
                        TdApi.User user = (TdApi.User) object;
                        userMap.remove(user.phoneNumber);
                        userMap.put(user.phoneNumber, user);
                        loadContacts();
                    }
                }
            });
        }
    };
    private ViewPager mPager;
    private FragmentManager fm;

    public static Intent buildStartIntent(Context context, boolean onlyTelegram) {
        final Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra(EXTRA_TELEGRAM, onlyTelegram);
        return intent;
    }

    public static Intent buildStartIntent(Context context, boolean onlyTelegram, boolean newGroup,boolean newMessage) {
        final Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra(EXTRA_TELEGRAM, onlyTelegram);
        intent.putExtra(EXTRA_NEW_GROUP, newGroup);
        intent.putExtra(EXTRA_NEW_MESSAGE, newMessage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        restoreActionBar();
        mPager = (ViewPager) findViewById(R.id.pager);

        /** Getting a reference to FragmentManager */
        fm = getSupportFragmentManager();


//        lvContacts = (ListView) findViewById(R.id.lvContacts);
//        lvContacts.setOnItemClickListener(this);
//        lvContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//
        boolean onlyTelegram = getIntent().getBooleanExtra(EXTRA_TELEGRAM, false);
//
        if (PreferenceUtils.isOfflineMode(getBaseContext())) {
            if (onlyTelegram) {
                loadTelegramContacts();
            } else {
                loadContacts();
            }
        } else {
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
            unregisterReceiver(updateUserNameReceiver);
        } catch (Exception e) {
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setCustomView(R.layout.ab_main);


        String s = getString(R.string.app_name);

        final boolean newGroup = getIntent().getBooleanExtra(EXTRA_NEW_GROUP, false);
        final boolean newMewssage = getIntent().getBooleanExtra(EXTRA_NEW_MESSAGE, false);
        final TextView tv = (TextView) findViewById(R.id.title);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableString ss1 = new SpannableString(s);

        if (newGroup) {
            final String string = getString(R.string.new_group);
            ss1 = new SpannableString(string);
            ss1.setSpan(bss, 0, 3, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, 0);// set color
        } else if (newMewssage) {
            final String string = getString(R.string.new_message);
            ss1 = new SpannableString(string);
            ss1.setSpan(bss, 0, 3, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, 0);// set color
        } else {
            ss1 = new SpannableString(s);
            ss1.setSpan(bss, 0, 6, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 6, 0);// set color
        }
        tv.setText(ss1);
    }

    private void loadContacts() {
        final ContactFetcher contactFetcher = new ContactFetcher(ContactsActivity.this, userMap);

        List<Contact> actions = new ArrayList<Contact>(3);
        final Contact object = new Contact("-1", getString(R.string.create_new_group), ContactType.Action);
        actions.add(object);

        listContacts = contactFetcher.fetchAll(actions);
        adapterContacts = new ContactsAdapter(ContactsActivity.this, listContacts);
        lvContacts.setAdapter(adapterContacts);

    }

    private void loadTelegramContacts() {
        boolean createNewGroup = getIntent().getBooleanExtra(EXTRA_NEW_GROUP, false);

        List<Contact> actions = new ArrayList<Contact>(3);

        if (createNewGroup) {
            actions.clear();
        }

        final ContactFetcher contactFetcher = new ContactFetcher(this, userMap);
        ArrayList<Contact> listContacts = contactFetcher.fetchTelegramContacts(null);
        ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts);
        lvContacts.setAdapter(adapterContacts);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.action_accept:
                SparseBooleanArray checked = lvContacts.getCheckedItemPositions();
                int ids[] = new int[checked.size()];
                if (checked != null) {
                    for (int i = 0; i < checked.size(); i++) {
                        final int keyAt = checked.keyAt(i);
                        ContactsAdapter adapter = (ContactsAdapter) getLvContacts().getAdapter();
                        final Contact contact = adapter.getItem(keyAt);
                        final TdApi.User user = contact.getUser();
                        ids[i] = user.id;
                    }

                    ApplicationSpektogram.getApplication(this).getClient().send(new TdApi.CreateGroupChat(ids, "test group chat"), new Client.ResultHandler() {
                        @Override
                        public void onResult(TdApi.TLObject object) {
                            Log.v(Constants.LOG_TAG, "CreateGroupChat" + object);
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

        for (TdApi.User user : users) {
            userMap.put(user.phoneNumber, user);
        }

        Log.v(null, "hash map" + userMap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed()) {

                    MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(fm);
                    /** Setting the FragmentPagerAdapter object to the viewPager object */
                    mPager.setAdapter(fragmentPagerAdapter);
//                    boolean onlyTelegram = getIntent().getBooleanExtra(EXTRA_TELEGRAM, false);
//                    if (onlyTelegram) {
//                        loadTelegramContacts();
//                    } else {
//                        loadContacts();
//                    }
                }

            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ContactsAdapter adapter = (ContactsAdapter) getLvContacts().getAdapter();

        final Contact item = adapter.getItem(position);
        if (item.getType() == ContactType.Action && item.name.equals(getString(R.string.create_new_group))) {
            startActivity(ContactsActivity.buildStartIntent(this, true, true,false));
        }
        adapter.notifyDataSetChanged();
    }

    public ListView getLvContacts() {
        return lvContacts;
    }

    @Override
    public void onGlobalLayout() {

    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;

        /** Constructor of the class */
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /** This method will be invoked when a page is requested to create */
        @Override
        public Fragment getItem(int arg0) {
            Bundle data = new Bundle();
            switch(arg0){

                /** tab1 is selected */
                case 0:
                    AllContactsFragment fragment1 = new AllContactsFragment(userMap);
                    return fragment1;

                /** tab2 is selected */
                case 1:
                    AllContactsFragment fragment2 = new AllContactsFragment(userMap);
                    return fragment2;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All contacts";
                case 1:
                    return "Tab Two";
            }

            return null;
        }

        /** Returns the number of pages */
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}
