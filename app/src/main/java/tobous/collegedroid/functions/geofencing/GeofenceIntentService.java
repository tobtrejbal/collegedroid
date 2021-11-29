package tobous.collegedroid.functions.geofencing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.gui.mainScreen.MainActivity;

/**
 * Created by ondra on 13.2.16.
 */
public class GeofenceIntentService extends IntentService {


    protected static final String TAG = "GeofenceIntentService";

    public GeofenceIntentService() {
        super("GeofenceIntentService");
        Log.v(TAG, "GeofenceIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.v(TAG, "onHandleIntent");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {

            Log.e(TAG, "HasError");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Send notification and log the transition details.
            sendNotification(getTransitionString(geofenceTransition), geofencingEvent.getTriggeringGeofences());
        } else {
            // Log the error.
            Log.e(TAG, "error");
        }

    }

    private void sendNotification(String message,  List<Geofence> triggeringGeofences) {
        // Create an explicit content Intent that starts the main Activity.
        Intent intentMessage = new Intent("mainBroadcast");
        intentMessage.putExtra("message", message);
        String requests = "";
        for(Geofence geofence : triggeringGeofences){
            requests = requests + " " + geofence.getRequestId();
        }
        intentMessage.putExtra("requests", requests);
        sendBroadcast(intentMessage);
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entering school";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exiting school";
            default:
                return "unknown";
        }
    }

}
