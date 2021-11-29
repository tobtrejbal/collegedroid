package tobous.collegedroid.functions.plans.encapsulation;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tobous on 5. 11. 2015.
 */
public class Room implements Serializable {

    int coordinateX;
    int coordinateY;
    int floor;
    String name;
    List<ScheduleAction> actionList;
    Bitmap empty;
    Bitmap full;
    Building building;

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScheduleAction> getActionList() {
        return actionList;
    }

    public void setActionList(List<ScheduleAction> actionList) {
        this.actionList = actionList;
    }

    public Bitmap getEmpty() {
        return empty;
    }

    public void setEmpty(Bitmap empty) {
        this.empty = empty;
    }

    public Bitmap getFull() {
        return full;
    }

    public void setFull(Bitmap full) {
        this.full = full;
    }
}
