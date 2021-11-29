package tobous.collegedroid.core;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.core.utils.LruBitmap;
import tobous.collegedroid.functions.changes.encapsulation.Change;
import tobous.collegedroid.functions.graphs.encapsulation.Graph;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.functions.plans.encapsulation.Room;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
import tobous.collegedroid.functions.setting.SettingManager;
import tobous.collegedroid.utils.XmlManager;

/**
 * Created by Tobous on 1. 3. 2015.
 */
public class AppCore extends Application {

    private static AppCore sInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private AppState mAppState;
    private List<ScheduleAction> scheduleActionList;
    private List<Change> changeList; //list zmen
    private List<Building> buildings = new ArrayList<Building>();
    private static String[] days = {"Neděle", "Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota"};
    private static String[] daysShort = {"Ne", "Po", "Út", "St", "Čt", "Pá", "So"};
    private static String[] actionTypes = {"Cvičení", "Přednáška", "Seminář", "Ostatní"};
    private static String[] actionTypesShort = {"Cv", "Př", "Se", "Os"};
    private static String[] colors = {"#1ABC9C", "#E74C3C", "#2ECC71", "#9B59B6", "#F1C40F", "#E67E22", "#2C3E50", "#F1C40F", "#8E44AD", "#D35400", "#16A085"};
    private static String[] weekTypesShort = {"K", "S", "L", "J"};
    private static String[] weekTypes = {"Každý", "Sudý", "Lichý", "Jiný"};
    private Bitmap fullRoom;
    private Bitmap emptyRoom;
    private Bitmap stairs;
    private Bitmap sourceRoom;
    private Bitmap targetRoom;

    String PATH_BUILDINGS = "buildings/";
    String PATH_PLANS = "plans/";
    String PATH_ROOMS = "rooms/";
    String PATH_FLOOR_PLANS = "graphs/";
    String PATH_FONTS = "fonts/";

    String PATH_TYPEFACE_ROBOTO_REGULAR = PATH_FONTS + "Roboto-Regular.ttf";
    String PATH_TYPEFACE_ROBOTO_BOLD = PATH_FONTS + "Roboto-Bold.ttf";
    String PATH_TYPEFACE_ROBOTO_MEDIUM = PATH_FONTS + "Roboto-Medium.ttf";

    public static AppCore getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        setCrashLog();

        Log.v(AppCore.this.getClass().getName() + "bubaaaak", "");

        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        //loadRooms();
        loadBuildings();
        loadAppState();


    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmap());
        }
        return this.mImageLoader;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private static Typeface typeFaceRobotoRegular;
    private static Typeface typeFaceRobotoBold;
    private static Typeface typeFaceRobotoMedium;

    public Typeface getTypeFaceRobotoRegular() {
        if (typeFaceRobotoRegular == null) {
            //Only do this once for each typeface used
            //or we will leak unnecessary memory.
            typeFaceRobotoRegular = Typeface.createFromAsset(getAssets(), PATH_TYPEFACE_ROBOTO_REGULAR);
        }
        return typeFaceRobotoRegular;

    }

    public Typeface getTypeFaceRobotoBold() {
        if (typeFaceRobotoBold == null) {
            //Only do this once for each typeface used
            //or we will leak unnecessary memory.
            typeFaceRobotoBold = Typeface.createFromAsset(getAssets(), PATH_TYPEFACE_ROBOTO_BOLD);
        }
        return typeFaceRobotoBold;

    }

    public Typeface getTypeFaceRobotoMedium() {
        if (typeFaceRobotoMedium == null) {
            //Only do this once for each typeface used
            //or we will leak unnecessary memory.
            typeFaceRobotoMedium = Typeface.createFromAsset(getAssets(), PATH_TYPEFACE_ROBOTO_MEDIUM);
        }
        return typeFaceRobotoMedium;

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void loadAppState() {

        mAppState = AppState.getInstance();

        SettingManager.loadSetting(this, mAppState);

        Calendar cal = Calendar.getInstance();
        //cal.setTime(new Date());
        mAppState.setSelectedDate(cal);

        setScheduleActionList(loadData());

    }

    public int getScreenSize() {

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            return 0;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            return 1;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return 2;
        } else {
            return 3;
        }

    }

    public void loadRooms() {
        String[] roomNumbers = new String[]{"J1", "J2", "J3", "J4", "J5", "J6", "J7", "J8", "J9", "J10", "J11", "J12", "J13", "J14", "J15", "J16", "J17"};
        for (int i = 0; i < roomNumbers.length; i++) {
            Room room = new Room();
            room.setName(roomNumbers[i]);

        }
    }

    public List<ScheduleAction> getScheduleActionList() {
        Collections.sort(scheduleActionList);
        return scheduleActionList;
    }

    public void setScheduleActionList(List<ScheduleAction> scheduleActionList) {
        this.scheduleActionList = scheduleActionList;
        Intent intentMessage = new Intent("mainBroadcast");
        intentMessage.putExtra("message", "ScheduleActionListUpdated");
        sendBroadcast(intentMessage);
    }

    //Getters and Setters for changes
    public List<Change> getChangeList() {
        return changeList;
    }

    public void setChangeList(List<Change> changeList) {
        this.changeList = changeList;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public void loadBuildings() {

        try {

            loadPictures();

            buildings = XmlManager.loadBuildings(IOUtils.toString(getAssets().open(PATH_BUILDINGS + "buildings.xml"), StandardCharsets.UTF_8));

            for (Building building : buildings) {
                //Log.v("jmeno",building.getName());
                for (Room room : building.getRooms()) {
                    //Log.v("jmenoPokoje",room.getName());
                }
            }

            for (Building building : buildings) {
                List<String> floorMaps = new ArrayList<String>();
                for (int j = 0; j < building.getFloorCount(); j++) {
                    //Log.v("nahravam","");
                    floorMaps.add(PATH_PLANS + building.getName() + "_" + (j + 1) + ".jpg");
                }
                List<Room> rooms = building.getRooms();
                building.setFloorMaps(floorMaps);
                /*for(int j = 0; j < rooms.size(); j++) {
                    InputStream ims = getAssets().open(PATH_ROOMS+rooms.get(j).getName()+"_"+ "full" +".jpg");
                    // load image as Drawable
                    rooms.get(j).setFull(BitmapFactory.decodeStream(ims));

                    //Log.v("loaaaded"+rooms.get(j).getName(),rooms.size()+"");

                    ims = getAssets().open(PATH_ROOMS+rooms.get(j).getName()+"_"+ "empty" +".jpg");
                    // load image as Drawable
                    //BitmapFactory is an Android graphics utility for images;
                    rooms.get(j).setEmpty(BitmapFactory.decodeStream(ims));
                }*/
                List<Graph> graphs = new ArrayList<Graph>();
                Log.v("floorCount" + building.getFloorCount(), "");
                if (building.getRooms().size() > 0) {
                    try {
                        for (int j = 0; j < building.getFloorCount(); j++) {
                            graphs.add(XmlManager.loadGraph(
                                    IOUtils.toString(getAssets().open(
                                            PATH_FLOOR_PLANS + building.getName() + "_" + (j + 1) + ".xml"), StandardCharsets.UTF_8)));//building.getName()+"_"+(j+1)+".xml"), StandardCharsets.UTF_8)));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    building.setFloorGraphs(graphs);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("vypisuju", "budovy");
    }

    public void addScheduleActionsToRoom() {

        for (Room room : buildings.get(0).getRooms()) {

            Log.v("building match" + room.getName(), "");

            if (room.getName().equals("J20")) {

                List<ScheduleAction> scheduleActions = new ArrayList<ScheduleAction>();

                for (ScheduleAction scheduleAction : scheduleActionList) {

                    scheduleActions.add(scheduleAction);


                }

                room.setActionList(scheduleActions);
            }

        }

    }

    public String[] getDays() {
        return days;
    }

    public String[] getDaysShort() {
        return daysShort;
    }

    public String getPATH_PLANS() {
        return PATH_PLANS;
    }

    public String getDay(int position) {

        position += -1;
        String[] tempDays = new String[days.length];

        int index = mAppState.getScheduleFirstDay() - 1;

        for (int i = 0; i < days.length; i++) {
            tempDays[i] = days[index];
            index++;
            if (index >= (days.length)) {
                index = 0;
            }
        }

        Log.v("start", "");
        for (int i = 0; i < tempDays.length; i++) {
            Log.v(tempDays[i], "");
        }


        return tempDays[position - 1];


        /*position -= 1;
        int firstDay = mAppState.getScheduleFirstDay()-1;
        if((position+firstDay) >= days.length) {
            return days[position+firstDay-days.length];
        } else {
            return days[position + firstDay];
        }*/
    }

    public List<ScheduleAction> loadData() {

        List<ScheduleAction> actions = new ArrayList<ScheduleAction>();

        /*actions.add(createAction(10, 45, 2, 11, 35, "numa"));
        actions.add(createAction(12, 25, 2, 14, 55, "ZT"));
        actions.add(createAction(15, 45, 2, 16, 35, "hehe"));
        actions.add(createAction(11, 00, 2, 14, 00, "bubu"));
        actions.add(createAction(14, 05, 2, 15, 35, "OMO"));
        //actions.add(createAction(10,45,2,15,35,"lulu"));
        actions.add(createAction(13, 15, 2, 15, 35, "Lada"));
        actions.add(createAction(17, 50, 2, 18, 35, "Cool"));*/

        return actions;

    }

    public ScheduleAction createAction(int startHour, int startMinute, int day, int endHour, int endMinute, String name) {

        ScheduleAction scheduleAction = new ScheduleAction();
        scheduleAction.setStartMinute(startMinute);
        scheduleAction.setStartHour(startHour);
        scheduleAction.setEndMinute(endMinute);
        scheduleAction.setEndHour(endHour);
        scheduleAction.setName(name);
        scheduleAction.setDayOfWeek(day);

        return scheduleAction;


    }

    public List<ScheduleAction> getScheduleListByDay(int day) {

        List<ScheduleAction> filtered = new ArrayList<>();
        for (ScheduleAction action : getScheduleActionList()) {

            if (action.getDayOfWeek() == day) {
                filtered.add(action);
            }
        }

        return filtered;

    }

    public void loadPictures() throws IOException {

        InputStream ims = getAssets().open(PATH_ROOMS+ "full" +".png");
        // load image as Drawable
        setFullRoom(BitmapFactory.decodeStream(ims));

        //Log.v("loaaaded"+rooms.get(j).getName(),rooms.size()+"");

        ims = getAssets().open(PATH_ROOMS + "empty" +".png");
        // load image as Drawable
        //BitmapFactory is an Android graphics utility for images;
        setEmptyRoom(BitmapFactory.decodeStream(ims));

        ims = getAssets().open(PATH_ROOMS + "stairs" +".png");
        // load image as Drawable
        //BitmapFactory is an Android graphics utility for images;
        setStairs(BitmapFactory.decodeStream(ims));



    }

    public void setCrashLog() {

        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
            }
        });
    }

    public void handleUncaughtException (Thread thread, Throwable e) {
        /*Toast toast = Toast.makeText(this, "spadlo to",Toast.LENGTH_LONG);
        toast.show();*/
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        /*Intent intent = new Intent ();
        intent.setAction ("com.mydomain.SEND_LOG"); // see step 5.
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity (intent);*/

        System.exit(1); // kill off the crashed app
    }

    public Bitmap getFullRoom() {
        return fullRoom;
    }

    public void setFullRoom(Bitmap fullRoom) {
        this.fullRoom = fullRoom;
    }

    public Bitmap getEmptyRoom() {
        return emptyRoom;
    }

    public void setEmptyRoom(Bitmap emptyRoom) {
        this.emptyRoom = emptyRoom;
    }

    public Bitmap getSourceRoom() {
        return sourceRoom;
    }

    public void setSourceRoom(Bitmap sourceRoom) {
        this.sourceRoom = sourceRoom;
    }

    public Bitmap getTargetRoom() {
        return targetRoom;
    }

    public void setTargetRoom(Bitmap targetRoom) {
        this.targetRoom = targetRoom;
    }

    public String getPATH_BUILDINGS() {
        return PATH_BUILDINGS;
    }

    public String getPATH_ROOMS() {
        return PATH_ROOMS;
    }

    public String getPATH_FLOOR_PLANS() {
        return PATH_FLOOR_PLANS;
    }

    public String getPATH_FONTS() {
        return PATH_FONTS;
    }

    public String getPATH_TYPEFACE_ROBOTO_REGULAR() {
        return PATH_TYPEFACE_ROBOTO_REGULAR;
    }

    public String getPATH_TYPEFACE_ROBOTO_BOLD() {
        return PATH_TYPEFACE_ROBOTO_BOLD;
    }

    public String getPATH_TYPEFACE_ROBOTO_MEDIUM() {
        return PATH_TYPEFACE_ROBOTO_MEDIUM;
    }

    public String[] getActionTypes() {
        return actionTypes;
    }

    public String[] getActionTypesShort() {
        return actionTypesShort;
    }

    public Bitmap getStairs() {
        return stairs;
    }

    public void setStairs(Bitmap stairs) {
        this.stairs = stairs;
    }

    public String[] getColors() {
        return colors;
    }

    public static String[] getWeekTypesShort() {
        return weekTypesShort;
    }

    public static String[] getWeekTypes() {
        return weekTypes;
    }
}