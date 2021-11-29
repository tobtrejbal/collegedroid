package tobous.collegedroid.functions.plans.encapsulation;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

import tobous.collegedroid.functions.graphs.encapsulation.Graph;

/**
 * Created by Tobous on 5. 11. 2015.
 */
public class Building implements Serializable{

    double lat;
    double lon;
    String name;
    String picture;
    List<Room> rooms;
    List<String> floorMaps;
    List<Graph> floorGraphs;
    int floorCount;
    int[] widths;
    int[] heights;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<String> getFloorMaps() {
        return floorMaps;
    }

    public void setFloorMaps(List<String> floorMaps) {
        this.floorMaps = floorMaps;
    }

    public int[] getWidths() {
        return widths;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
    }

    public int[] getHeights() {
        return heights;
    }

    public void setHeights(int[] heights) {
        this.heights = heights;
    }

    public int getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    public List<Graph> getFloorGraphs() {
        return floorGraphs;
    }

    public void setFloorGraphs(List<Graph> floorGraphs) {
        this.floorGraphs = floorGraphs;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
