package tobous.collegedroid.functions.position;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import tobous.collegedroid.core.utils.AppState;

/**
 * Created by Tob on 20. 12. 2015.
 */
public class Location implements LocationListener {

    LocationManager locationManager;

    AppState mAppState;

    Context context;

    double[] values;

    double lon;

    double lat;

    public Location(Context context) {

        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.values = new double[2];

        mAppState = AppState.getInstance();

    }

    public void stopListening() {

        Log.v("Location", "stopListening");

        locationManager.removeUpdates(this);

    }

    public void startListening() {

        Log.v("Location", "startListening");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {

        Log.v("Location", "onLocationChanged");
        mAppState.setLat(location.getLatitude());
        mAppState.setLon(location.getLongitude());

        Log.v("Location", String.valueOf(location.getLatitude()));
        Log.v("Location", String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //Log.v("Location", "onStatusChanged");

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.v("Location", "onProviderEnabled");
    }


    @Override
    public void onProviderDisabled(String s) {
        Log.v("Location", "onProviderDisabled");
    }


}
