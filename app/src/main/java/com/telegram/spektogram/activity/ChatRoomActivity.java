package com.telegram.spektogram.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.fragment.NavigationDrawerFragment;
import com.telegram.spektogram.preferences.PreferenceUtils;


public class ChatRoomActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    protected void onResume() {
        super.onResume();
        PreferenceUtils.setChatBackground(this,false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceUtils.setChatBackground(this,true);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

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

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.top_shape));
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





}
