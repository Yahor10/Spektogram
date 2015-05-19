package com.telegram.spektogram.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.ContactsActivity;
import com.telegram.spektogram.activity.SettingsActivity;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.application.Constants;
import com.telegram.spektogram.views.CustomDrawerAdapter;
import com.telegram.spektogram.views.DrawerItem;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements AdapterView.OnItemClickListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private TextView name;
    private TextView phone;
    private ImageView image;


    private final List<DrawerItem> dataList = new ArrayList<>();
    CustomDrawerAdapter adapter;
    private Client.ResultHandler emptyHandler = new Client.ResultHandler() {
        @Override
        public void onResult(TdApi.TLObject object) {

        }
    };

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        getActivity().registerReceiver(fileDownloadReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_FILE_DOWNLOADED));
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        updateGetMeData();
    }

    private void updateGetMeData() {
        final ApplicationSpektogram application = ApplicationSpektogram.getApplication(getActivity());
        final TdApi.GetMe function = new TdApi.GetMe();
        application.sendFunction(function, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                Log.e("TAG", object.toString());
                if (object instanceof TdApi.User) {
                    final FragmentActivity activity = getActivity();
                    if (activity != null) {
                        final TdApi.User user = (TdApi.User) object;
                        Log.v(Constants.LOG_TAG, "get me " + user);
                        final TdApi.File photoSmall = user.photoSmall;
                        if (photoSmall instanceof TdApi.FileEmpty) {
                            TdApi.FileEmpty empty = (TdApi.FileEmpty) photoSmall;
                            if (empty.id != 0) {
                                ApplicationSpektogram.getApplication(activity).sendFunction(new TdApi.DownloadFile(((TdApi.FileEmpty) photoSmall).id), emptyHandler);
                            }
                        } else {
                            final TdApi.FileLocal local = (TdApi.FileLocal) photoSmall;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Bitmap bitmap = BitmapFactory.decodeFile(local.path);
                                    image.setImageBitmap(bitmap);
                                }
                            });
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(new StringBuilder().append(user.firstName).append(" ").append(user.lastName).toString());
                                phone.setText(new StringBuilder().append("+").append(user.phoneNumber).toString());

                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        name = (TextView) inflate.findViewById(R.id.userName);
        phone = (TextView) inflate.findViewById(R.id.userPhone);
        image  =(ImageView) inflate.findViewById(R.id.image);

        mDrawerListView = (ListView) inflate.findViewById(R.id.list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        dataList.add(new DrawerItem(getString(R.string.new_group), R.drawable.ic_drawer));
        dataList.add(new DrawerItem(getString(R.string.new_secret_chat), R.drawable.ic_drawer));
        dataList.add(new DrawerItem(getString(R.string.contacts), R.drawable.ic_drawer));
        dataList.add(new DrawerItem(getString(R.string.invite_friend), R.drawable.ic_drawer));
        dataList.add(new DrawerItem(getString(R.string.settings), R.drawable.ic_drawer));

        adapter = new CustomDrawerAdapter(getActivity(), R.layout.custom_drawer_item,
                dataList);

        mDrawerListView.setAdapter(adapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        mDrawerListView.setOnItemClickListener(this);

        return inflate;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(fileDownloadReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private final BroadcastReceiver fileDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            updateGetMeData();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {

            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        String s = getString(R.string.app_name);

        final ColorDrawable drawable = new ColorDrawable(Color.TRANSPARENT);
        getActionBar().setBackgroundDrawable(drawable);
        actionBar.setCustomView(R.layout.ab_main);

        final TextView tv = (TextView)actionBar.getCustomView(). findViewById(R.id.title);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableString ss1;
        ss1 = new SpannableString(s);

        ss1.setSpan(bss, 0, 6, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 6, 0);// set color

        tv.setText(ss1);

    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final FragmentActivity activity = getActivity();
        switch (position) {
            case 0:
                startActivity(ContactsActivity.buildStartIntent(activity, true, true, false));
                break;
            case 2:
                final Intent intent = ContactsActivity.buildStartIntent(activity, false);
                startActivity(intent);
                break;
            case 4:
                startActivity(SettingsActivity.buildStartIntent(getActivity()));
                break;
            case 3:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/html");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Join telegram");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Go to telegram!\n telegram.org");

                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                break;
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
