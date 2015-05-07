package com.telegram.spektogram.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class ContactsActivity extends ActionBarActivity implements Client.ResultHandler,AdapterView.OnItemClickListener {
    ArrayList<Contact> listContacts;
    ListView lvContacts;
    private  Map<String,TdApi.User>userMap =null;
    private ContactsAdapter adapterContacts;
    public static String EXTRA_TELEGRAM = "EXTRA_TELEGRAM";


    public static Intent buildStartIntent(Context context,boolean onlyTelegram){
        final Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra(EXTRA_TELEGRAM,onlyTelegram);
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

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setCustomView(R.layout.ab_main);

    }

    private void loadContacts() {
        final ContactFetcher contactFetcher = new ContactFetcher(ContactsActivity.this, userMap);

        List<Contact>actions = new ArrayList<Contact>(3);
        final Contact object = new Contact("-1", "create chat ", ContactType.Action);
        actions.add(object);

        listContacts = contactFetcher.fetchAll(actions);
        adapterContacts = new ContactsAdapter(ContactsActivity.this, listContacts);
        lvContacts.setAdapter(adapterContacts);

    }

    private void loadTelegramContacts() {
        boolean createNewGroup = false;

        List<Contact>actions = new ArrayList<Contact>(3);
        final Contact object = new Contact("-1", "new group ", ContactType.Action);
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

        getMenuInflater().inflate(R.menu.menu_contacts, menu);

        return true;
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
                Log.v(Constants.LOG_TAG,"ch" + checked.keyAt(0));
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
        Log.v(null,"hash map" + userMap);
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
}
