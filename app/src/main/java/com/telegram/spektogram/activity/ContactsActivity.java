package com.telegram.spektogram.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.application.Constants;
import com.telegram.spektogram.contacts.AllContactsFragment;
import com.telegram.spektogram.contacts.Contact;
import com.telegram.spektogram.contacts.ContactsAdapter;
import com.telegram.spektogram.contacts.TelegramContactsFragment;
import com.telegram.spektogram.preferences.PreferenceUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ContactsActivity extends ActionBarActivity implements Client.ResultHandler, ViewTreeObserver.OnGlobalLayoutListener {
    
    private Map<String, TdApi.User> userMap = null;
    public static String EXTRA_TELEGRAM = "EXTRA_TELEGRAM";
    public static String EXTRA_NEW_GROUP = "EXTRA_NEW_GROUP";
    public static String EXTRA_NEW_MESSAGE = "EXTRA_NEW_MESSAGE";

   private final BroadcastReceiver fileDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(fragmentPagerAdapter != null){
                Log.v(Constants.LOG_TAG,"fileDownloadReceiver ...");
                reloadContacts(context);
            }
        }
   };

    private void reloadContacts(Context context) {
        final AllContactsFragment item1 = (AllContactsFragment) fragmentPagerAdapter.getItem(0);
        item1.loadContacts(context);

        final TelegramContactsFragment item2= (TelegramContactsFragment) fragmentPagerAdapter.getItem(1);
        item2.loadTelegramContacts(context);
    }

    private final BroadcastReceiver updateStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final int id = intent.getIntExtra(ApplicationSpektogram.EXTRA_UPDATE_USER_ID, -1);
            final TdApi.GetUser func = new TdApi.GetUser(id);
            ApplicationSpektogram.getApplication(context).sendFunction(func, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    if (object instanceof TdApi.User) {
                        TdApi.User user = (TdApi.User) object;
                        userMap.remove(user.phoneNumber);
                        userMap.put(user.phoneNumber, user);
                        reloadContacts(context);
                    }
                }
            });
        }
    };

    protected void showInputDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.alert_dialog_group_name, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String nameGroup = editText.getText().toString();
                        final TelegramContactsFragment item = (TelegramContactsFragment) fragmentPagerAdapter.getItem(1);
                        final ListView list = item.getList();
                        final SparseBooleanArray checked = list.getCheckedItemPositions();

                        if (checked != null) {
                            int ids[] = new int[checked.size()];
                            for (int i = 0; i < checked.size(); i++) {
                                final int keyAt = checked.keyAt(i);
                                ContactsAdapter adapter = (ContactsAdapter) list.getAdapter();
                                final Contact contact = adapter.getItem(keyAt);
                                final TdApi.User user = contact.getUser();
                                ids[i] = user.id;
                            }
                            ApplicationSpektogram.getApplication(ContactsActivity.this).getClient().send(new TdApi.CreateGroupChat(ids, nameGroup),
                                    new Client.ResultHandler() {
                                        @Override
                                        public void onResult(TdApi.TLObject object) {
                                            Log.v(Constants.LOG_TAG, "CreateGroupChat" + object);
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }



    private final BroadcastReceiver updateUserNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final int id = intent.getIntExtra(ApplicationSpektogram.EXTRA_UPDATE_USER_ID, -1);
            final TdApi.GetUser func = new TdApi.GetUser(id);
            ApplicationSpektogram.getApplication(context).sendFunction(func, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    if (object instanceof TdApi.User) {
                        TdApi.User user = (TdApi.User) object;
                        userMap.remove(user.phoneNumber);
                        userMap.put(user.phoneNumber, user);
                        reloadContacts(context);
                    }
                }
            });
        }
    };
    private ViewPager mPager;
    private FragmentManager fm;
    private MyFragmentPagerAdapter fragmentPagerAdapter;

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

        boolean onlyTelegram = getIntent().getBooleanExtra(EXTRA_TELEGRAM, false);
//
        /** Setting the FragmentPagerAdapter object to the viewPager object */
        if (PreferenceUtils.isOfflineMode(getBaseContext())) {
            mPager.setAdapter(fragmentPagerAdapter);
            if (onlyTelegram) {
                mPager.setCurrentItem(1);
            } else {
                mPager.setCurrentItem(0);
            }
        } else {
            Log.v(Constants.LOG_TAG,"launch contact request...");
            ApplicationSpektogram.getApplication(this).sendFunction(new TdApi.GetContacts(), this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateStatusReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_USER_STATUS));
        registerReceiver(updateUserNameReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_USER_NAME));
        registerReceiver(fileDownloadReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_FILE_DOWNLOADED));
        registerReceiver(fileDownloadReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_USER_PHOTO));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(updateStatusReceiver);
            unregisterReceiver(updateUserNameReceiver);
            unregisterReceiver(fileDownloadReceiver);
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
//    }

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
                showInputDialog();

                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onResult(TdApi.TLObject object) {
        Log.v(Constants.LOG_TAG, "TLObject onResult contacts:" + object.toString());
        if(object instanceof TdApi.Contacts) {
            TdApi.Contacts contacts = (TdApi.Contacts) object;
            final TdApi.User[] users = contacts.users;
            userMap = new HashMap<>(users.length);

            for (TdApi.User user : users) {
                userMap.put(user.phoneNumber, user);
            }

            final Collection<TdApi.User> values = userMap.values();
            Log.v(Constants.LOG_TAG, "hash map" + userMap);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentPagerAdapter = new MyFragmentPagerAdapter(fm);
                    mPager.setAdapter(fragmentPagerAdapter);
                    if (!isDestroyed()) {
                        boolean onlyTelegram = getIntent().getBooleanExtra(EXTRA_TELEGRAM, false);
                        if (onlyTelegram) {
                            mPager.setCurrentItem(1);
                        } else {
                            mPager.setCurrentItem(0);
                    }
                    }

                }
            });
        }


    }


    @Override
    public void onGlobalLayout() {

    }

    public final class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private AllContactsFragment fragment1;
        private TelegramContactsFragment fragment2;

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
                    if(fragment1 == null) {
                        fragment1 = new AllContactsFragment(userMap);
                    }
                    return fragment1;

                /** tab2 is selected */
                case 1:
                    if(fragment2 == null) {
                        fragment2 = new TelegramContactsFragment(userMap);
                    }
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
                    return "Only Telegram";
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
