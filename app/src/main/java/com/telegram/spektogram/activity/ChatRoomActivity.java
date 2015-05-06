package com.telegram.spektogram.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.telegram.spektogram.db.SpectrDBHandler;
import com.telegram.spektogram.fragment.NavigationDrawerFragment;
import com.telegram.spektogram.R;
import com.telegram.spektogram.views.PopupMenu;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;


public class ChatRoomActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static Intent buildStartIntent(Context context){
        return new Intent(context,ChatRoomActivity.class);
    }
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        // Set Listener

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                TdApi.Chat chat = new TdApi.Chat();
//                chat.id = 123;
//                chat.type =  new  TdApi.PrivateChatInfo();
//                ((TdApi.PrivateChatInfo)chat.type).user = new TdApi.User();
//                ((TdApi.PrivateChatInfo)chat.type).user.firstName = "Alexandr";
//                ((TdApi.PrivateChatInfo)chat.type).user.lastName= "Alexeevich";
//                ((TdApi.PrivateChatInfo)chat.type).user.id = 321;
//                ((TdApi.PrivateChatInfo)chat.type).user.username = "alex-pers";
//                ((TdApi.PrivateChatInfo)chat.type).user.phoneNumber = "+375292044134";
//
//
//                TdApi.Message message = new TdApi.Message();
//                message.id = 999;
//                message.message=new TdApi.MessageText();
//                ((TdApi.MessageText)message.message).text = "THIS IS SPARTA!";
//                message.chatId = chat.id;
//                message.fromId = ((TdApi.PrivateChatInfo)chat.type).user.id;
//                message.date= (int) System.currentTimeMillis();

//                SpectrDBHandler spectrDBHandler = new SpectrDBHandler(getApplicationContext());
//                spectrDBHandler.addUser(((TdApi.PrivateChatInfo)chat.type).user);
//                spectrDBHandler.addChat(chat);
//                spectrDBHandler.addMessage(message,chat.id,((TdApi.PrivateChatInfo)chat.type).user.id);

                PopupMenu menu = new PopupMenu(ChatRoomActivity.this);
                Resources resources = getResources();

                menu.setHeaderTitle(resources.getString(R.string.messege));

                menu.add(1,R.string.title_activity_chat).setIcon(
                        resources.getDrawable(R.drawable.ic_drawer));
                menu.show();
            }
        });


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.chat, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ChatRoomActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
