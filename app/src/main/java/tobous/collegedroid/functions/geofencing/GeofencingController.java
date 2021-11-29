package tobous.collegedroid.functions.geofencing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.plans.encapsulation.Building;

/**
 * Created by ondra on 13.2.16.
 */
public class GeofencingController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private Context mContext;
    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private AppCore mAppCore;
    private boolean status;

    public GeofencingController(Context context) {
        // Empty list for storing geofences.

        mAppCore = AppCore.getInstance();
        mContext = context;
        mGeofenceList = new ArrayList<>();
        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;
        buildGoogleApiClient();
    }

    public void startGeofencing() {

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    public void stopGeofencing() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public boolean getStatus() {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("GeofencingController", "onConnected");
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.v("CoreService", "ACCESS_FINE_LOCATION error");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("GeofencingController", "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("GeofencingController", "onConnectionFailed");

    }

    @Override
    public void onResult(Status status) {
        Log.v("GeofencingController", "onResult");

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(mGeofenceList);

        return builder.build();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void populateGeofenceList(Building building, int minutes ) {

        /**TODO not all geofence is needed
         * only one with parameters
         */
        Geofence geofence = new Geofence.Builder()
                .setRequestId(building.getName())
                .setCircularRegion(
                        building.getLat(),
                        building.getLon(),
                        100
                )
                .setExpirationDuration( 1000*60* minutes )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        /*Geofence geofence = new Geofence.Builder()
                .setRequestId(building.getName())
                .setCircularRegion(
                        50.150921,
                        15.690715,
                        1000
                )
                .setExpirationDuration( 1000*60* minutes )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();*/

        mGeofenceList.add(geofence);
    }



    private PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceIntentService.class);

        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
