package tobous.collegedroid.functions.setting;

import android.content.Context;
import android.content.SharedPreferences;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.plans.encapsulation.Building;

/**
 * Created by Tob on 2. 3. 2016.
 */
public class SettingManager {

    public static final String SHARED_PREF_ID_GPS_LAT = "lat";
    public static final String SHARED_PREF_ID_GPS_LON = "lon";
    public static final String SHARED_PREF_ID_GPS_CITY_LAT = "cityLat";
    public static final String SHARED_PREF_ID_GPS_CITY_LON = "cityLon";
    public static final String SHARED_PREF_SCHEDULE_START_MINUTE = "startMinute";
    public static final String SHARED_PREF_SCHEDULE_START_HOUR = "startHour";
    public static final String SHARED_PREF_SCHEDULE_END_MINUTE = "endMinute";
    public static final String SHARED_PREF_SCHEDULE_END_HOUR = "endHour";
    public static final String SHARED_PREF_MEASURE_LOCATION = "measureLocation";
    public static final String SHARED_PREF_HORIZONTAL_MENU = "horizontalMenu";
    public static final String SHARED_PREF_GEOFENCE_TIMER = "geofenceTimer";
    public static final String SHARED_PREF_CHANGES_TIMER = "changesTimer";
    public static final String SHARED_PREF_FIRST_DAY = "firstDay";
    public static final String SHARED_PREF_SELECTED_BUILDING = "selectedBuilding";

    public static final String PREFERENCE_NAME = "myPref";

    public static void saveSetting(AppCore appCore, AppState appState) {
        SharedPreferences sharedPreferences = appCore.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SHARED_PREF_ID_GPS_LAT, Double.doubleToRawLongBits(appState.getLat()));
        editor.putLong(SHARED_PREF_ID_GPS_LON, Double.doubleToRawLongBits(appState.getLon()));
        editor.putLong(SHARED_PREF_ID_GPS_CITY_LAT, Double.doubleToRawLongBits(appState.getCityLat()));
        editor.putLong(SHARED_PREF_ID_GPS_CITY_LON, Double.doubleToRawLongBits(appState.getCityLon()));
        editor.putInt(SHARED_PREF_SCHEDULE_START_MINUTE, appState.getScheduleStartMinute());
        editor.putInt(SHARED_PREF_SCHEDULE_START_HOUR, appState.getScheduleStartHour());
        editor.putInt(SHARED_PREF_SCHEDULE_END_MINUTE, appState.getScheduleEndMinute());
        editor.putInt(SHARED_PREF_SCHEDULE_END_HOUR, appState.getScheduleEndHour());
        editor.putBoolean(SHARED_PREF_MEASURE_LOCATION, appState.isMeasureLocation());
        editor.putBoolean(SHARED_PREF_HORIZONTAL_MENU, appState.isHorizontalMenu());
        editor.putBoolean(SHARED_PREF_GEOFENCE_TIMER, appState.isGeofenceTimer());
        editor.putBoolean(SHARED_PREF_CHANGES_TIMER, appState.isChangesTimer());
        editor.putInt(SHARED_PREF_FIRST_DAY, appState.getScheduleFirstDay());
        editor.putString(SHARED_PREF_SELECTED_BUILDING, getBuildingName(appCore, appState.getSelectedBuilding()));
    }

    public static void loadSetting(AppCore appCore, AppState appState) {
        SharedPreferences sharedPreferences = appCore.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        appState.setLat(Double.longBitsToDouble(sharedPreferences.getLong(SHARED_PREF_ID_GPS_LAT, Double.doubleToLongBits(0))));
        appState.setLon(Double.longBitsToDouble(sharedPreferences.getLong(SHARED_PREF_ID_GPS_LON, Double.doubleToLongBits(0))));
        appState.setCityLat(Double.longBitsToDouble(sharedPreferences.getLong(SHARED_PREF_ID_GPS_CITY_LAT, Double.doubleToLongBits(AppState.DEFAUL_VALUE_CITY_LAT))));
        appState.setCityLon(Double.longBitsToDouble(sharedPreferences.getLong(SHARED_PREF_ID_GPS_CITY_LON, Double.doubleToLongBits(AppState.DEFAUL_VALUE_CITY_LON))));
        appState.setScheduleStartMinute(sharedPreferences.getInt(SHARED_PREF_SCHEDULE_START_MINUTE, AppState.DEFAUL_VALUE_SCHEDULE_START_MINUTE));
        appState.setScheduleStartHour(sharedPreferences.getInt(SHARED_PREF_SCHEDULE_START_HOUR, AppState.DEFAUL_VALUE_SCHEDULE_START_HOUR));
        appState.setScheduleEndMinute(sharedPreferences.getInt(SHARED_PREF_SCHEDULE_END_MINUTE, AppState.DEFAUL_VALUE_SCHEDULE_END_MINUTE));
        appState.setScheduleEndHour(sharedPreferences.getInt(SHARED_PREF_SCHEDULE_END_HOUR, AppState.DEFAUL_VALUE_SCHEDULE_END_HOUR));
        appState.setMeasureLocation(sharedPreferences.getBoolean(SHARED_PREF_MEASURE_LOCATION, false));
        appState.setHorizontalMenu(sharedPreferences.getBoolean(SHARED_PREF_HORIZONTAL_MENU, false));
        appState.setGeofenceTimer(sharedPreferences.getBoolean(SHARED_PREF_GEOFENCE_TIMER, true));
        appState.setChangesTimer(sharedPreferences.getBoolean(SHARED_PREF_CHANGES_TIMER, true));
        appState.setScheduleFirstDay(sharedPreferences.getInt(SHARED_PREF_FIRST_DAY, AppState.DEFAUL_VALUE_SCHEDULE_FIRST_DAY));
        appState.setSelectedBuilding(getBuildingByName(appCore, sharedPreferences.getString(SHARED_PREF_SELECTED_BUILDING,"J")));
    }

    private static String getBuildingName(AppCore appCore, Building building) {
        for(Building b : appCore.getBuildings()) {
            if(b.getName().equals(building.getName())) {
                return b.getName();
            }
        }
        return null;
    }

    private static Building getBuildingByName(AppCore appCore, String name) {
        for(Building b : appCore.getBuildings()) {
            if(b.getName().equals(name)) {
                return b;
            }
        }
        return null;
    }

}
