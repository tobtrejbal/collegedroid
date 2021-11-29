package tobous.collegedroid.functions.schedule.encapsulation;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;

import tobous.collegedroid.functions.plans.encapsulation.Room;

/**
 * Created by Tobous on 5. 11. 2015.
 */
public class ScheduleAction implements Serializable, Comparable{

    String id;
    int dayOfWeek;
    int startMinute;
    int startHour;
    int endMinute;
    int endHour;
    String name;
    String fullName;
    Room room;
    String place;
    String teacher;
    String user;
    int capacity;
    int occupancy;
    int actionType;
    int startWeek = 0;
    int endWeek = 54;
    int weekType = 0;

    public ScheduleAction() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getRoom() {
        return room;
    }

    void setRoom(Room room) {
        this.room = room;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int compareTo(Object another) {
        ScheduleAction action = (ScheduleAction) another;

        if(action.getDayOfWeek() < this.dayOfWeek) {
            return 1;
        }
        if(action.getDayOfWeek() > this.dayOfWeek) {
            return -1;
        }
        if(action.getDayOfWeek() == this.dayOfWeek) {
            if(action.getStartHour() < this.startHour) {
                return 1;
            } else if(action.getStartHour() > this.startHour) {
                return -1;
            } else if(action.getStartHour() == this.startHour) {
                if(action.getStartMinute() < this.startMinute) {
                    return 1;
                } else if(action.getStartMinute() > this.startMinute) {
                    return -1;
                } else if(action.getStartMinute() == this.startMinute) {
                    return 0;
                }
            }
        }

        return 0;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getWeekType() {
        return weekType;
    }

    public void setWeekType(int weekType) {
        this.weekType = weekType;
    }

    public boolean isInWeek(int week) {
        Log.v("week:" + week + "startWeek:" + startWeek + "endWeek:" + endWeek, "sddsad");
        Log.v("weektype:"+weekType,"df");
        if((weekType == 0) ||
                ((weekType == 1) && (week % 2 == 1)) ||
                ((weekType == 2) && (week % 2 == 0)) ||
                (weekType == 3)) {
            if(startWeek <= week && endWeek >= week) {
                return true;
            }
        }
        return false;
    }

    public boolean isThisWeek() {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return isInWeek(currentWeek);
    }
}
