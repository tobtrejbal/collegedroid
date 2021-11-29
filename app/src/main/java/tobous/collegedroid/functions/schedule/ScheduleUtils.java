package tobous.collegedroid.functions.schedule;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
import tobous.collegedroid.utils.DateUtils;

/**
 * Created by Tobous on 6. 11. 2015.
 */
public class ScheduleUtils {

    public static int[] getDaysMax(List<ScheduleAction> scheduleActions) {

        int[][] maxMatrix = new int[7][getMinutesLength()];

        for(int z = 0; z < scheduleActions.size(); z++) {
            ScheduleAction scheduleAction = scheduleActions.get(z);
            //if(isBetween(scheduleAction)) {
                int[] startEnd = getStartEnd(scheduleAction);
                int day = scheduleAction.getDayOfWeek();
                int start = startEnd[0];
                int end = startEnd[1];
                if(end > 0 && start < getMinutesLength()+1) {
                    for(int i = 0; i < 7; i++) {
                        if(day == i+1) {
                            for (int m = start; m < end; m++) {
                                maxMatrix[i][m]++;
                            }
                        }

                    }
                }
            //}

        }

        int[] maxDays = new int[7];

        for(int i = 0; i < maxMatrix.length; i++) {
            int max = 0;
            for(int j = 0; j < maxMatrix[0].length; j++) {
                if(max < maxMatrix[i][j]) {
                    max = maxMatrix[i][j];
                }
            }
            if(max == 0) {
                max = 1;
            }
            maxDays[i]  = max;
        }

        return maxDays;

    }

    public static int[][] makeScheduleTable(List<ScheduleAction> scheduleActions, int[] dayMax) {

        int complete = 0;

        for (int i = 0; i < dayMax.length; i++) {
            complete += dayMax[i];
        }
        //Log.v("complete", complete+"");

        int[][] actionTable = new int[complete][getMinutesLength() + 1];

        for (int i = 0; i < actionTable.length; i++) {
            for (int j = 1; j < actionTable[0].length; j++) {
                actionTable[i][j] = -1;
            }
        }

        int added = 0;
        for (int m = 0; m < dayMax.length; m++) {
            for (int i = 0; i < dayMax[m]; i++) {
                actionTable[i + added][0] = m + 1;
            }
            added += dayMax[m];
        }
        for (int[] k : actionTable) {
        }

        for (int z = 0; z < scheduleActions.size(); z++) {
            ScheduleAction scheduleAction = scheduleActions.get(z);

            int[] startEnd = getStartEnd(scheduleAction);
            int day = scheduleAction.getDayOfWeek();
            int start = startEnd[0]+1;
            int end = startEnd[1];
            //Log.v(end + ":" + start, "asd");
            if(end > 0 && start < getMinutesLength()+1) {
                int addedMax = 0;
                    for (int i = 0; i < 7; i++) {
                        if (day == i + 1) {
                            boolean freeSpace = false;
                            int k = 0;
                            int startPosition = 0;
                            int endPosition = 0;
                            while (!freeSpace) {
                                for (int m = 1; m < getMinutesLength()+1; m++) {
                                    if (start == m) {
                                        startPosition = m;
                                    }
                                    if (end == m) {
                                        endPosition = m;
                                    }
                                }
                                int totalMinutes = 0;
                                int freeMinutes = 0;
                                //Log.v(k + ":", "asd");
                                for (int m = startPosition; m <= endPosition; m++) {
                                    totalMinutes++;
                                    if (actionTable[i + k + addedMax][m] < 0) {
                                        freeMinutes++;
                                    }
                                }
                                if (totalMinutes == freeMinutes) {
                                    freeSpace = true;
                                } else {
                                    k++;
                                }
                            }
                            for (int m = startPosition; m <= endPosition; m++) {
                                actionTable[i + addedMax + k][m] = z;
                            }


                        }

                        addedMax += (dayMax[i] - 1);

                }
            }
        }

        return switchDays(actionTable, dayMax);

    }

    public static int calculateTimeDifferenceInMinutes(int minuteA, int hourA, int minuteB, int hourB) {

        int result = 0;
        result += minuteB;
        result -=(minuteA);
        result += hourB*60 - hourA*60;

        return result;

    }

    public static int getMinutesLength() {
        AppState mAppState = AppState.getInstance();
        return calculateTimeDifferenceInMinutes(mAppState.getScheduleStartMinute(), mAppState.getScheduleStartHour(),
                mAppState.getScheduleEndMinute(), mAppState.getScheduleEndHour());
    }

    public static int getDifference(int minuteA, int hourA) {
        AppState mAppState = AppState.getInstance();
        return calculateTimeDifferenceInMinutes(mAppState.getScheduleStartMinute(), mAppState.getScheduleStartHour(), minuteA, hourA);

    }

    public static List<ScheduleAction> filterByDay(int day, List<ScheduleAction> actions) {

        List<ScheduleAction> scheduleActions = new ArrayList<ScheduleAction>();

        for(ScheduleAction action : actions) {

            if(action.getDayOfWeek() == day) {
                scheduleActions.add(action);
                Log.v("filtered", "");
            }

        }

        return scheduleActions;

    }

    public static int getStartMinute() {
        AppState mAppState = AppState.getInstance();
        return mAppState.getScheduleStartMinute();
    }

    public static int getStartHour() {
        AppState mAppState = AppState.getInstance();
        return mAppState.getScheduleStartHour();
    }

    public static int getEndMinute() {
        AppState mAppState = AppState.getInstance();
        return mAppState.getScheduleEndMinute();
    }

    public static int getEndHour() {
        AppState mAppState = AppState.getInstance();
        return mAppState.getScheduleEndHour();
    }

    private static int[][] switchDays(int[][] table, int[] dayMax) {

        AppState mAppState = AppState.getInstance();

        int[][] tableTemp = new int[table.length][table[0].length];

        int added = 0;

        int index = 0;

        for(int i = 0; i < mAppState.getScheduleFirstDay()-1; i++) {
            index += dayMax[i];
        }

        int rowCount = 0;

        for(int i = 0; i < dayMax.length; i++) {
            rowCount += dayMax[i];
        }

        for(int m = 0; m < dayMax.length; m++) {
            for(int i = 0; i < dayMax[m]; i++) {
                tableTemp[i+added] = table[index];
                index++;
                if(index >= (rowCount)) {
                    index = 0;
                }
            }
            added += dayMax[m];
        }
        return tableTemp;

    }

    public static int[] countDays(int[][] days) {
        int[] daysMax = new int[7];
        int current = days[0][0];
        int count = 1;
        int added = 0;
        for(int i = 0; i < days.length;i++) {
            if(i == (days.length - 1)) {
                daysMax[added] = count;
            } else {
                if(current == days[i+1][0]) {
                    count++;
                } else {
                    current = days[i+1][0];
                    daysMax[added] = count;
                    added++;
                    count = 1;
                }
            }
        }

        return daysMax;
    }

    public static String makeId(ScheduleAction scheduleAction) {

        String id = "";
        id += scheduleAction.getName();
        id += "-";
        id += scheduleAction.getStartMinute();
        id += ":";
        id += scheduleAction.getStartHour();
        id += "+";
        id += scheduleAction.getEndMinute();
        id += ":";
        id += scheduleAction.getEndHour();
        id += "-";
        id += scheduleAction.getDayOfWeek();
        id += "-";
        id += scheduleAction.getPlace();

        return id;

    }

    public static boolean isBetween(ScheduleAction scheduleAction) {

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarStartAction = Calendar.getInstance();
        Calendar calendarEndAction = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        calendarStart.set(Calendar.MINUTE, getStartMinute());
        calendarStart.set(Calendar.HOUR_OF_DAY, getStartHour());
        calendarEnd.set(Calendar.MINUTE, getEndMinute());
        calendarEnd.set(Calendar.HOUR_OF_DAY, getEndHour());
        calendarStartAction.set(Calendar.MINUTE, scheduleAction.getStartMinute());
        calendarStartAction.set(Calendar.HOUR_OF_DAY, scheduleAction.getStartHour());
        calendarEndAction.set(Calendar.MINUTE, scheduleAction.getEndMinute());
        calendarEndAction.set(Calendar.HOUR_OF_DAY, scheduleAction.getEndHour());

        if(DateUtils.isBetween(calendarStart, calendarEnd, calendarStartAction) &&
                DateUtils.isBetween(calendarStart, calendarEnd, calendarEndAction)) {
            return true;
        }

        return false;
    }

    public static int[] getStartEnd(ScheduleAction scheduleAction) {
        int[] startEnd = new int[2];
        int start = getDifference(scheduleAction.getStartMinute(), scheduleAction.getStartHour());
        int end = getDifference(scheduleAction.getEndMinute(), scheduleAction.getEndHour());

        if(start < 1) {
            start = 0;
        }
        if(end > getMinutesLength()) {
            end = getMinutesLength();
        }

        startEnd[0] = start;
        startEnd[1] = end;

        return startEnd;
    }

    public static List<ScheduleAction> filterByWeekType(List<ScheduleAction> scheduleActions, int weekType) {
        List<ScheduleAction> filteredActions = new ArrayList<>();
        for (ScheduleAction scheduleAction : scheduleActions) {
            Calendar calendar = Calendar.getInstance();
            int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            if((scheduleAction.getStartWeek() <= currentWeek) && (scheduleAction.getEndWeek() >= currentWeek)){
                switch(weekType) {
                    case 0:
                        filteredActions.add(scheduleAction);
                        break;

                    case 1:
                        if(scheduleAction.getWeekType() == 1 || scheduleAction.getWeekType() == 0) {
                            filteredActions.add(scheduleAction);
                        }
                        break;

                    case 2:
                        if(scheduleAction.getWeekType() == 2 || scheduleAction.getWeekType() == 0) {
                            filteredActions.add(scheduleAction);
                        }
                        break;

                    case 3:
                        if(scheduleAction.isThisWeek()) {
                            filteredActions.add(scheduleAction);
                        }
                        break;

                    default:

                        break;
                }
            }
        }

        return filteredActions;

    }

}

