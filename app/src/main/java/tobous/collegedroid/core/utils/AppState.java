package tobous.collegedroid.core.utils;

import java.util.Calendar;

import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.utils.DateUtils;

/**
 * Created by Tobous on 5. 11. 2015.
 */
public class AppState {

    public static final int DEFAUL_VALUE_SCHEDULE_START_MINUTE = 00;
    public static final int DEFAUL_VALUE_SCHEDULE_START_HOUR = 7;
    public static final int DEFAUL_VALUE_SCHEDULE_END_MINUTE = 00;
    public static final int DEFAUL_VALUE_SCHEDULE_END_HOUR = 19;
    public static final int DEFAUL_VALUE_SCHEDULE_FIRST_DAY = 2;
    public static final int DEFAUL_VALUE_CHANGES_TIMER = 60000;
    public static final int DEFAUL_VALUE_GEOFENCE_TIMER = 1000;
    public static final double DEFAUL_VALUE_CITY_LON = 15.83222;
    public static final double DEFAUL_VALUE_CITY_LAT = 50.20917;

    private int scheduleStartMinute;
    private int scheduleStartHour;
    private int scheduleEndMinute;
    private int scheduleEndHour;
    private double lat;
    private double lon;
    private double cityLon;
    private double cityLat;
    private boolean measureLocation;
    private boolean horizontalMenu;
    private boolean geofenceTimer;
    private boolean changesTimer;
    private Calendar selectedDate;
    private Building selectedBuilding;
    private int scheduleFirstDay = 2;
    private String userId = "tobik";

    private static AppState sInstance;

    public static AppState getInstance() {

        if(sInstance == null) {
            sInstance = new AppState();
        }

        return sInstance;

    }

    private AppState() {}

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isHorizontalMenu() {
        return horizontalMenu;
    }

    public void setHorizontalMenu(boolean horizontalMenu) {
        this.horizontalMenu = horizontalMenu;
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedDay() {
        return DateUtils.getDayStringFromCalendar(selectedDate.get(Calendar.DAY_OF_WEEK));
    }

    public String getSelectedHour() {
        return String.valueOf(selectedDate.get(Calendar.HOUR_OF_DAY));
    }

    public String getSelectedYear() {
        return String.valueOf(selectedDate.get(Calendar.YEAR));
    }

    public String getSelectedSemester() {
        if((selectedDate.get(Calendar.MONTH) > 1 ) && (selectedDate.get(Calendar.MONTH) < 8)) {
            return "LS";
        } else {
            return "ZS";
        }
    }

    public Building getSelectedBuilding() {
        return selectedBuilding;
    }

    public void setSelectedBuilding(Building selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
    }

    public String getSelectedDateString() {

        return DateUtils.getDateStringFromCalendar(selectedDate, 0);

    }

    public int getScheduleFirstDay() {
        return scheduleFirstDay;
    }

    public void setScheduleFirstDay(int scheduleFirstDay) {
        this.scheduleFirstDay = scheduleFirstDay;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isMeasureLocation() {
        return measureLocation;
    }

    public void setMeasureLocation(boolean measureLocation) {
        this.measureLocation = measureLocation;
    }

    public double getCityLon() {
        return cityLon;
    }

    public void setCityLon(double cityLon) {
        this.cityLon = cityLon;
    }

    public double getCityLat() {
        return cityLat;
    }

    public void setCityLat(double cityLat) {
        this.cityLat = cityLat;
    }

    public int getScheduleStartMinute() {
        return scheduleStartMinute;
    }

    public void setScheduleStartMinute(int scheduleStartMinute) {
        this.scheduleStartMinute = scheduleStartMinute;
    }

    public int getScheduleEndHour() {
        return scheduleEndHour;
    }

    public void setScheduleEndHour(int scheduleEndHour) {
        this.scheduleEndHour = scheduleEndHour;
    }

    public int getScheduleEndMinute() {
        return scheduleEndMinute;
    }

    public void setScheduleEndMinute(int scheduleEndMinute) {
        this.scheduleEndMinute = scheduleEndMinute;
    }

    public int getScheduleStartHour() {
        return scheduleStartHour;
    }

    public void setScheduleStartHour(int scheduleStartHour) {
        this.scheduleStartHour = scheduleStartHour;
    }

    public boolean isGeofenceTimer() {
        return geofenceTimer;
    }

    public void setGeofenceTimer(boolean geofenceTimer) {
        this.geofenceTimer = geofenceTimer;
    }

    public boolean isChangesTimer() {
        return changesTimer;
    }

    public void setChangesTimer(boolean changesTimer) {
        this.changesTimer = changesTimer;
    }
}
