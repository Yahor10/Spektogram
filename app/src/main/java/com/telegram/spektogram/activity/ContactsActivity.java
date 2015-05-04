package com.telegram.spektogram.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.contacts.Contact;
import com.telegram.spektogram.contacts.ContactFetcher;
import com.telegram.spektogram.contacts.ContactsAdapter;

import java.util.ArrayList;

public class ContactsActivity extends ActionBarActivity {
    ArrayList<Contact> listContacts;
    ListView lvContacts;

    public static Intent buildStartIntent(Context context){
        return new Intent(context,ContactsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        listContacts = new ContactFetcher(this).fetchAll();
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts);
        lvContacts.setAdapter(adapterContacts);

        getActionBar().setIcon(R.mipmap.ic_launcher);
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

}
