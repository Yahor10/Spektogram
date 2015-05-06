package com.telegram.spektogram.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.contacts.Contact;
import com.telegram.spektogram.contacts.ContactFetcher;
import com.telegram.spektogram.contacts.ContactsAdapter;
import com.telegram.spektogram.preferences.PreferenceUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactsActivity extends ActionBarActivity implements Client.ResultHandler {
    ArrayList<Contact> listContacts;
    ListView lvContacts;
    private  Map<String,TdApi.User>userMap =null;
    private ContactsAdapter adapterContacts;

    public static Intent buildStartIntent(Context context){
        return new Intent(context,ContactsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        lvContacts = (ListView) findViewById(R.id.lvContacts);

        if(PreferenceUtils.isOfflineMode(getBaseContext())){
            loadContacts();
        }else{
            ApplicationSpektogram.getApplication(this).sendFunction(new TdApi.GetContacts(), this);
        }
    }

    private void loadContacts() {
        final ContactFetcher contactFetcher = new ContactFetcher(ContactsActivity.this, userMap);
        listContacts = contactFetcher.fetchAll();
        adapterContacts = new ContactsAdapter(ContactsActivity.this, listContacts);
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
        if (id == R.id.action_settings) {
            return true;
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
                if(!isDestroyed()) {
                    loadContacts();
                }
            }
        });
    }
}
