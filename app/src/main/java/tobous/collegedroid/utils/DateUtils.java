package tobous.collegedroid.utils;

import android.util.Log;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 24. 12. 2015.
 */
public class DateUtils {

    public static final String DATE_FORMAT_FULL = "HH:mm , dd.MM.yyyy";
    public static final String DATE_FORMAT_TIME = "HH:mm";
    public static final String DATE_FORMAT_DATE = "dd.MM.yyyy";

    public static String[] getDateAsStrings(Date date) {

        String[] dateString = new String[3];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        dateString[0] = String.valueOf(calendar.get(Calendar.YEAR));
        dateString[1] = getDayStringFromCalendar(calendar.get(Calendar.DAY_OF_WEEK));
        dateString[2] = String.valueOf(calendar.get(Calendar.YEAR));

        return dateString;

    }

    public static String getDateStringFromCalendar(Calendar calendar, int type) {

        SimpleDateFormat sdf;

        switch (type) {

            case 0:

                sdf = new SimpleDateFormat(DATE_FORMAT_FULL);
                return sdf.format(calendar.getTime());

            case 1:

                sdf = new SimpleDateFormat(DATE_FORMAT_TIME);
                return sdf.format(calendar.getTime());

            case 2:

                sdf = new SimpleDateFormat(DATE_FORMAT_DATE);
                return sdf.format(calendar.getTime());

            default:

                return null;

        }
    }

    public static String getDayStringFromCalendar(int day) {

        String dayString = "";

        switch (day) {

            case 1:

                dayString = "Ne";

                break;

            case 2:

                dayString = "Po";

                break;

            case 3:

                dayString = "Út";

                break;

            case 4:

                dayString = "St";

                break;

            case 5:

                dayString = "Čt";

                break;

            case 6:

                dayString = "Pá";

                break;

            case 7:

                dayString = "So";

                break;

            default:

                break;

        }

        return dayString;
    }

    public static int getDayIntFromString(String dayString) {

        int day = 0;

        if(dayString.equals("Po")) {
            day = 2;
        }
        if(dayString.equals("Út")) {
            day = 3;
        }
        if(dayString.equals("St")) {
            day = 4;
        }
        if(dayString.equals("Čt")) {
            day = 5;
        }
        if(dayString.equals("Pá")) {
            day = 6;
        }
        if(dayString.equals("So")) {
            day = 7;
        }
        if(dayString.equals("Ne")) {
            day = 1;
        }

        return day;

    }

    public static int getDayFromCalendar(Calendar calendar) {

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {

            case 1:

               return 2;

            case 2:

               return 1;

            case 3:

               return 1;

            case 4:

               return 1;

            case 5:

               return 1;

            case 6:

               return 1;

            case 7:

               return 1;

            default:

               return 0;

        }

    }

    public static Calendar makeDateFromNumbers(int minute, int hour, int day, int month, int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        return calendar;

    }

    public static boolean isActionDuringDate(ScheduleAction scheduleAction, Calendar toCompare) {

        int startMinute = scheduleAction.getStartMinute();
        int startHour = scheduleAction.getStartHour();
        int endMinute = scheduleAction.getEndMinute();
        int endHour = scheduleAction.getEndHour();
        int startDay = scheduleAction.getDayOfWeek();
        int dayToCompare = toCompare.get(Calendar.DAY_OF_WEEK);
        int middleMinute = toCompare.get(Calendar.MINUTE);
        int middleHour = toCompare.get(Calendar.HOUR_OF_DAY);

        if(startDay == dayToCompare) {

            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarMiddle = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();

            calendarStart.set(Calendar.MINUTE, startMinute);
            calendarStart.set(Calendar.HOUR_OF_DAY, startHour);
            calendarMiddle.set(Calendar.MINUTE, middleMinute);
            calendarMiddle.set(Calendar.HOUR_OF_DAY, middleHour);
            calendarEnd.set(Calendar.MINUTE, endMinute);
            calendarEnd.set(Calendar.HOUR_OF_DAY, endHour);

            return isBetween(calendarStart, calendarEnd, calendarMiddle);

        }

        return false;

    }

    public static boolean isBetween(Calendar start, Calendar end, Calendar tested) {
        DateTime dateTimeStart = new DateTime(start);
        DateTime dateTimeEnd = new DateTime(end);
        DateTime dateTimeTested = new DateTime(tested);

        if((dateTimeStart.isBefore(dateTimeTested) || dateTimeStart.isEqual(dateTimeTested)) &&
                (dateTimeEnd.isAfter(dateTimeTested) || dateTimeEnd.isEqual(dateTimeTested))) {
            //Log.v("trrue","");
            return true;

        } else {
            return false;
        }
    }

    public static boolean isBefore(int minuteA, int hourA, int minuteB, int hourB) {

        Calendar calendarA = Calendar.getInstance();
        Calendar calendarB = Calendar.getInstance();

        calendarA.set(Calendar.MINUTE, minuteA);
        calendarA.set(Calendar.HOUR_OF_DAY, hourA);
        calendarB.set(Calendar.MINUTE, minuteB);
        calendarB.set(Calendar.HOUR_OF_DAY, hourB);

        DateTime dateTimeStart = new DateTime(calendarA);
        DateTime dateTimeEnd = new DateTime(calendarB);

        return dateTimeEnd.isBefore(dateTimeStart);

    }

    public static boolean isAfter(int minuteA, int hourA, int minuteB, int hourB) {

        Calendar calendarA = Calendar.getInstance();
        Calendar calendarB = Calendar.getInstance();

        calendarA.set(Calendar.MINUTE, minuteA);
        calendarA.set(Calendar.HOUR_OF_DAY, hourA);
        calendarB.set(Calendar.MINUTE, minuteB);
        calendarB.set(Calendar.HOUR_OF_DAY, hourB);

        DateTime dateTimeStart = new DateTime(calendarA);
        DateTime dateTimeEnd = new DateTime(calendarB);

        return dateTimeEnd.isAfter(dateTimeStart);

    }

    public static String getCurrentSemester() {
        Calendar calendarA = Calendar.getInstance();
        if((calendarA.get(Calendar.MONTH) > 1 ) && (calendarA.get(Calendar.MONTH) < 8)) {
            return "LS";
        } else {
            return "ZS";
        }
    }

}
