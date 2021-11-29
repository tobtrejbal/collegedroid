package tobous.collegedroid.gui.mainScreen;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.service.CoreService;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.geofencing.GeofencingController;
import tobous.collegedroid.functions.graphs.encapsulation.Vertex;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.functions.plans.encapsulation.Room;
import tobous.collegedroid.functions.position.Location;
import tobous.collegedroid.functions.setting.SettingManager;
import tobous.collegedroid.gui.activities.about.AboutActivity;
import tobous.collegedroid.gui.activities.setting.SettingActivity;
import tobous.collegedroid.gui.dialogs.navigation.NavigationDialog;
import tobous.collegedroid.gui.dialogs.navigation.NavigationDialogListener;
import tobous.collegedroid.gui.mainScreen.fragment.FragmentListener;
import tobous.collegedroid.gui.mainScreen.fragment.freeclassrooms.ClassRoomMap;
import tobous.collegedroid.gui.mainScreen.navdrawer.DrawerItem;
import tobous.collegedroid.gui.mainScreen.navdrawer.DrawerNavAdapter;
import tobous.collegedroid.gui.mainScreen.slidingtab.SlidingTabLayout;
import tobous.collegedroid.gui.mainScreen.viewpager.PageAdapter;
import tobous.collegedroid.gui.utils.RecyclerItemTouchListener;
import tobous.collegedroid.gui.utils.RecyclerAdapterListener;
import tobous.collegedroid.utils.LoggingUtils;


public class MainActivity  extends AppCompatActivity implements RecyclerItemTouchListener, ViewPager.OnPageChangeListener, NavigationDialogListener {

    int currentPosition = 0;

    private static final String EXTRA_KEY_SELECTED_TAB = "selected_tab";

    private static final String GCM_ID = "aloha";

    private ActionBar actionBar;
    // Tab titles
    private String[] tabs;

    private final AppCore mAppCore = AppCore.getInstance();

    private ActionBarDrawerToggle toggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout drawerRelativeLayout;
    private RecyclerView mRecyclerViewNavDrawer;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerNavAdapter adapter;
    private PageAdapter pAdapter;
    private ViewPager vPager;
    private AppState mAppState;
    SlidingTabLayout slidingTabLayout;
    private ArrayList<DrawerItem> menuItems;
    private RecyclerAdapterListener adapterListener;

    private SharedPreferences mPrefs;

    private int previousPosition = 0;

    Location location;

    private String currentPage = "";
    private String regid;



    public MainActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeClasses();

        LoggingUtils.configure();

        tabs = new String[]{getString(R.string.main_tab_schedule), getString(R.string.main_tab_freerooms),
                getString(R.string.main_tab_map),getString(R.string.main_tab_changes)};

        pAdapter = new PageAdapter(
                getSupportFragmentManager(), tabs);
        vPager = (ViewPager) findViewById(R.id.main_view_pager);
        vPager.setAdapter(pAdapter);

        // Initilization
        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        adapterListener = new RecyclerAdapterListener(this, this);
        //actionBar.hide();

        menuItems = new ArrayList<DrawerItem>();

        adapter = new DrawerNavAdapter(menuItems);

        location = new Location(this);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.main_sliding_tab);
        slidingTabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                // TODO Auto-generated method stub
                return getResources().getColor(R.color.action_bar_selected_background);
            }
        });

        mAppCore.verifyStoragePermissions(this);

        /*AssetManager mg = getResources().getAssets();

        try {
            mg.open("Roboto-Bold.ttf");
            Log.v("fffffffff","fffff");

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        slidingTabLayout.set(mAppCore.getTypeFaceRobotoBold());

        drawerRelativeLayout = (RelativeLayout) findViewById(R.id.main_nav_drawer_layout);

        // Setting the ViewPager For the SlidingTabsLayout

        slidingTabLayout.setViewPager(vPager);

        slidingTabLayout.setOnPageChangeListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        mRecyclerViewNavDrawer = (RecyclerView) findViewById(R.id.main_nav_drawer_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewNavDrawer.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewNavDrawer.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mRecyclerViewNavDrawer.setAdapter(adapter);
        mRecyclerViewNavDrawer.addOnItemTouchListener(adapterListener);

        toggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.menu_drawer_open,  /* "open drawer" description */
                R.string.menu_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                Log.v("aaaaaaaaaop", "");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                Log.v("aaaaaaaaaop", "");
            }
        };
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(toggle);
        actionBar.setElevation(0);

        if (savedInstanceState != null) {
            Log.v("not nuuuuull", "");
            previousPosition = savedInstanceState.getInt(EXTRA_KEY_SELECTED_TAB);

        }

        vPager.setCurrentItem(previousPosition);
        vPager.addOnPageChangeListener(this);
//

        /*Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.main_theme_status_bar_color));*/

        initializeClasses();

        fillDrawer();

        location.startListening();

        startServices();

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshActivity();

    }

    @Override
    public void onPause() {
        super.onPause();

        saveAppState();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actionbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        /*MenuItem searchItem = menu.findItem(R.id.main_activity_actionbar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).
                setTextColor(getResources().getColor(R.color.action_bar_text));

        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).
                setHintTextColor(getResources().getColor(R.color.action_bar_search_hint));

        //searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        //final int backgroundColorExpanded = getResources().getColor(R.color.material_grey_600);
        //final int backgroundColorTraditional = getResources().getColor(R.color.action_bar_background);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                //searchView.setBackgroundColor(backgroundColorExpanded);
                Log.v("expanded", "");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                Log.v("collapsed", "");
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                //searchView.setBackgroundColor(backgroundColorTraditional);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                Log.v("aaaahoj", "aaa");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                return true;

            }

        });

        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click

        if (toggle.onOptionsItemSelected(item)) {
            Log.v("olalala", "");
            //toggle.onDrawerOpened(item.getActionView());
            return true;
        }


        switch (item.getItemId()) {

           /* case R.id.main_activity_actionbar_search:
                Log.v("aaaa", "aaaa");
                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {

        String title = menuItems.get(position).getTitle();

        if(title.equals(getResources().getString(R.string.main_tab_schedule))) {

            vPager.setCurrentItem(0);

        }

        if(title.equals(getResources().getString(R.string.main_tab_freerooms))) {

            vPager.setCurrentItem(1);

        }

        if(title.equals(getResources().getString(R.string.main_tab_map))) {

            vPager.setCurrentItem(2);

        }

        if(title.equals(getResources().getString(R.string.main_tab_changes))) {

            vPager.setCurrentItem(3);

        }

        if(title.equals(getResources().getString(R.string.nav_drawer_item_settings))) {

            startActivity(new Intent(this, SettingActivity.class));

        }

        if(title.equals(getResources().getString(R.string.nav_drawer_item_about))) {

            startActivity(new Intent(this, AboutActivity.class));

        }

        if(title.equals(getResources().getString(R.string.nav_drawer_item_navigate))) {

            createNavigationDialog(null, null);

        }

        /*if(title.equals(getResources().getString(R.string.menu_title_home))) {

            vPager.setCurrentItem(1);

            return;

        }*/

        mDrawerLayout.closeDrawer(drawerRelativeLayout);

    }


    private void fillDrawer() {

        menuItems.clear();

        if(!mAppState.isHorizontalMenu()) {

            menuItems.add(new DrawerItem(R.drawable.ic_launcher, 0, getResources().getString(R.string.nav_drawer_header_menu)));
            menuItems.add(new DrawerItem(R.drawable.ic_schedule, 1, getResources().getString(R.string.main_tab_schedule)));
            menuItems.add(new DrawerItem(R.drawable.ic_classroom, 1, getResources().getString(R.string.main_tab_freerooms)));
            menuItems.add(new DrawerItem(R.drawable.ic_map, 1, getResources().getString(R.string.main_tab_map)));
            menuItems.add(new DrawerItem(R.drawable.ic_changes, 1, getResources().getString(R.string.main_tab_changes)));

        }

        menuItems.add(new DrawerItem(R.drawable.ic_launcher, 0, getResources().getString(R.string.nav_drawer_header_app)));
        menuItems.add(new DrawerItem(R.drawable.ic_setting, 1, getResources().getString(R.string.nav_drawer_item_settings)));
        menuItems.add(new DrawerItem(R.drawable.ic_about, 1, getResources().getString(R.string.nav_drawer_item_about)));
        menuItems.add(new DrawerItem(R.drawable.ic_navigation, 1, getResources().getString(R.string.nav_drawer_item_navigate)));

        adapter.notifyDataSetChanged();

        Log.v("daaataa", adapter.getItemCount() + "lala");

    }

    public void startMyActivity(Intent intent) {

        startActivity(intent);

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_KEY_SELECTED_TAB, vPager.getCurrentItem());
        Log.v("aaaa", "saving state");

    }

    public void startServices() {
        Log.v("vypisuju","starting");
        Intent intentChanges = new Intent(this, CoreService.class);
        startService(intentChanges);

    }


    public void initializeClasses() {

        mAppState = AppState.getInstance();

        /*handler = new Handler();

        refreshPeriodic = new Runnable() {
            @Override
            public void run() {

                refreshData();
                handler.postDelayed(this, 60 * 1000);
            }
        };*/

        /*dataManager = new EventDataManager(this);
        dataManagerCategories = new CategoriesDataManager(this);
        dingleState = DingleState.getInstance();
        filterManager = new FilterManager(this);*/

        mPrefs = getSharedPreferences(MainActivity.class.getName(),
                Context.MODE_PRIVATE);

    }

    public void stop() {

        saveAppState();

        //handler.removeCallbacks(refreshPeriodic);

    }

    public void saveAppState() {

        SettingManager.saveSetting(mAppCore, mAppState);

        /*filterManager.saveFilter(dingleState.getFilter());

        filterManager.setCity(dingleState.getChosenCity());

        filterManager.setMode(dingleState.getMode());

        filterManager.setLocation(dingleState.getLat(),dingleState.getLon());

        filterManager.saveLastUpdateDate(dingleState.getLastRefreshTime());

        filterManager.saveConnected(dingleState.isConnected());

        if(dingleState.isConnected()) {filterManager.saveClientData(dingleState.getClient());}
*/

    }

    public void refreshActivity() {

        /*if(dingleState.isConnected()) {
            txtNavBar.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
        } else {
            txtNavBar.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
        }*/

        for(int i = 0; i<pAdapter.getCount();i++) {
            FragmentListener fragment = (FragmentListener) pAdapter.getItem(i);
            fragment.refreshFragment();
        }

        if(mAppState.isHorizontalMenu()) {

            slidingTabLayout.show();

        } else {

            slidingTabLayout.hide();

        }

        fillDrawer();

    }

    public void logOffUser() {

        //dingleState.setConnected(false);

        refreshActivity();

    }

    /*public void pickUpUser() {

        startActivityForResult(new Intent(this, ConnectActivity.class), 95);

    }*/

    public void search(String query) {

        //startActivity(new Intent(this, SearchResultsActivity.class));

    }


    private void writeToFile(String s) {
        try {
            String imageInSD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dingle/config.txt";
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(imageInSD)));
            outputStreamWriter.write(s);
            outputStreamWriter.close();
            Log.v("file writen", "");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void showText(String text) {

        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();

    }

    /*public void registerPushNotifications() {

        regid = getRegistrationId();

        if (regid.isEmpty()) {
            registerInBackground();
        }

    }*/

    /*public String getRegistrationId() {

        String registrationId = mPrefs.getString(SharedPreferencesConstants.STRING_GCM_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = mPrefs.getInt(SharedPreferencesConstants.STRING_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = mAppCore.getAppVersion();
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;

    }*/

    /*public void registerInBackground() {

        RegisterGCMAsyncTask mRegisterGCMAsyncTask =  new RegisterGCMAsyncTask(this, this, mPrefs, GCM_ID);
        mRegisterGCMAsyncTask.execute();

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


        }


    }

    public void createNavigationDialog(final Room room, final Building building) {

        Bundle args = new Bundle();

        NavigationDialog newFragment = new NavigationDialog();
        newFragment.setListener(this);
        newFragment.setArguments(args);
        newFragment.setTargetFragment(pAdapter.getItem(1), 1);
        newFragment.show(this.getSupportFragmentManager(), "room detail");

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

        FragmentListener fragmentToShow = (FragmentListener) pAdapter.getItem(i);
        fragmentToShow.onResumeFragment();

        FragmentListener fragmentToHide = (FragmentListener) pAdapter.getItem(currentPosition);
        fragmentToHide.onPauseFragment();

        currentPosition = i;

        actionBar.setTitle(pAdapter.getPageTitle(i));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void navigate(Building building, Vertex source, Vertex target) {
        vPager.setCurrentItem(1);
        ClassRoomMap classRoomMap = (ClassRoomMap) pAdapter.getItem(1);
        classRoomMap.navigate(building, source, target);
    }

    @Override
    public void onLongTouch(int position) {
        selectItem(position);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onShortTouch(int position) {
        selectItem(position);
        mDrawerLayout.closeDrawers();
    }
}