package com.telegram.spektogram.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lamerman.FileDialog;
import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.fragment.NavigationDrawerFragment;
import com.telegram.spektogram.views.PopupMenu;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;


public class ChatRoomActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, PopupMenu.OnItemSelectedListener {

    private static final int SEND_PHOTO = 1;

    private static final int SEND_GEO_LOCATION = 2;

    private static final int SEND_VIDEO = 3;

    public static Intent buildStartIntent(Context context) {
        return new Intent(context, ChatRoomActivity.class);
    }

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    long mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setTitle("");


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu menu = new PopupMenu(ChatRoomActivity.this);
                Resources resources = getResources();

                menu.setHeaderTitle(resources.getString(R.string.message));

                menu.add(SEND_PHOTO, R.string.title_activity_chat).setIcon(
                        resources.getDrawable(R.drawable.ic_drawer));
                menu.show();

                menu.setOnItemSelectedListener(ChatRoomActivity.this);

                Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, "/sdcard");

                //can user select directories or not
                intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

                //alternatively you can set file filter
                //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

//                startActivityForResult(intent, 1);

//                startActivity(ContactsActivity.buildStartIntent(getApplicationContext(),true,false,true));

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

                break;
            case 2:

                break;
            case 3:

                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.top_shape));
        actionBar.setCustomView(R.layout.ab_main);

        String s = getString(R.string.app_name);

        final TextView tv = (TextView) findViewById(R.id.title);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableString ss1;
        ss1 = new SpannableString(s);

        ss1.setSpan(bss, 0, 6, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 6, 0);// set color

        tv.setText(ss1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.chat, menu);
            restoreActionBar();

//            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//
//            final MenuItem item = menu.findItem(R.id.search);
//            SearchView searchView = (SearchView) item.getActionView();
//
//            searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
//
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//                @Override
//                public boolean onQueryTextSubmit(String s) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String s) {
//                    getSupportActionBar().getCustomView().setVisibility(View.GONE);
//                    return false;
//                }
//            });

//            MenuItemCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
//                    | MenuItem.SHOW_AS_ACTION_ALWAYS);
//            MenuItemCompat.expandActionView(item);

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


    @Override
    public void onItemSelected(com.telegram.spektogram.views.MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case SEND_PHOTO:
                final TdApi.InputMessagePhoto photo = new TdApi.InputMessagePhoto("");
                ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.SendMessage(1, photo), new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.TLObject object) {
                        Log.v(null, "result test:" + object.toString());
                    }
                });
                break;
            case SEND_GEO_LOCATION:

                break;
        }
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
