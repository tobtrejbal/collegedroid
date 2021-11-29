package tobous.collegedroid.core.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import java.io.IOException;
import java.security.Permission;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.asynctask.changesList.ChangesAsyncTask;
import tobous.collegedroid.functions.asynctask.changesList.ChangesAsyncTaskListener;
import tobous.collegedroid.functions.changes.encapsulation.Change;
import tobous.collegedroid.functions.geofencing.GeofencingController;
import tobous.collegedroid.functions.position.Location;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
import tobous.collegedroid.gui.mainScreen.MainActivity;

/**
 * Created by Ondra on 1. 2. 2016.
 */
public class CoreService extends Service implements ChangesAsyncTaskListener {

    final int NOTIFICATION_GEOFENCE_ID = 100;
    final int NOTIFICATION_CHANGE_ID = 110;

    private List<PendingIntent> listAlarmsIntents = new ArrayList<>();
    private int runningEndTimmers = 0;
    private Handler changesHandler;
    boolean isChangeRunning = false;

    AppCore mAppCore;
    AppState mAppState;
    Location location;
    GeofencingController geofencingController;
    private AlarmManager alarmManager;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            //Geofence on Enter
            if (extras.getString("message", "").equals("Entering school")) {
                Log.v("CoreService", extras.getString("message", ""));
                createGeofenceNotification(extras.getString("message", ""), extras.getString("requests", ""));
                setRingTone(false);
            }

            //Geofence on Exit
            if (extras.getString("message", "").equals("Exiting school")) {
                Log.v("CoreService", extras.getString("message", ""));
                cancelNotification(NOTIFICATION_GEOFENCE_ID);
                setRingTone(true);
            }

            //Update Setting
            if (extras.getString("message", "").equals("update")) {
                Log.v("CoreService", extras.getString("message", ""));
                if (mAppState.isChangesTimer() && !isChangeRunning) {
                    changesHandler.post(runnable);
                    isChangeRunning = true;

                }
                if (!mAppState.isChangesTimer() && isChangeRunning) {
                    changesHandler.removeCallbacks(runnable);
                    isChangeRunning = false;
                }

                if (mAppState.isGeofenceTimer()) {
                    createPendingScheduleActionIntents();
                    if (runningEndTimmers > 0) {
                        geofencingController.startGeofencing();

                    }
                } else {
                    deletePendingScheduleActionIntents();
                    cancelNotification(NOTIFICATION_GEOFENCE_ID);
                    geofencingController.stopGeofencing();
                    setRingTone(true);
                }
            }

            //Create Schedule Timers
            if (extras.getString("message", "").equals("ScheduleActionListUpdated")) {
                Log.v("CoreService", extras.getString("message", ""));
                createPendingScheduleActionIntents();
            }

            //Create Schedule Timers
            if (extras.getString("message", "").equals("scheduleEndAction")) {
                Log.v("CoreService", extras.getString("message", ""));
                runningEndTimmers = runningEndTimmers - 1;

                if (runningEndTimmers == 0) {
                    cancelNotification(NOTIFICATION_GEOFENCE_ID);
                    geofencingController.stopGeofencing();
                    setRingTone(true);
                }
            }

            //ScheduleAction timer triggered
            if (extras.getString("message", "").equals("scheduleAction")) {
                Log.v("CoreService", extras.getString("message", ""));
                ScheduleAction action = (ScheduleAction) extras.get("action");

                if (mAppState.isGeofenceTimer()) {
                    geofencingController.populateGeofenceList(mAppCore.getBuildings().get(0), calculateMinutesOfScheduleAction(action));
                    geofencingController.startGeofencing();
                }
                Intent intentMessage = new Intent("mainBroadcast");
                intentMessage.putExtra("message", "scheduleEndAction");
                intentMessage.putExtra("action", action);
                PendingIntent pendingIntentScheduleActionEvent = PendingIntent.getBroadcast(getApplication(), action.hashCode(), intentMessage, PendingIntent.FLAG_UPDATE_CURRENT);

                runningEndTimmers = runningEndTimmers + 1;

                Calendar timeOff = Calendar.getInstance();
                timeOff.set(Calendar.HOUR_OF_DAY, action.getEndHour());
                timeOff.set(Calendar.MINUTE, action.getEndMinute());
                timeOff.set(Calendar.SECOND, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeOff.getTimeInMillis(), pendingIntentScheduleActionEvent);
            }

        }
    };


    @Override
    public void onCreate() {
        Log.v("CoreService", "onCreate");

        mAppCore = AppCore.getInstance();
        mAppState = AppState.getInstance();
        location = new Location(mAppCore);

        registerReceiver(mBroadcastReceiver, new IntentFilter("mainBroadcast"));
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        geofencingController = new GeofencingController(this);
        start();

    }

    private void createPendingScheduleActionIntents() {

        List<ScheduleAction> scheduleActions = mAppCore.getScheduleActionList();
        deletePendingScheduleActionIntents();
        listAlarmsIntents = new ArrayList<>();


        for (ScheduleAction s : scheduleActions) {
            Intent intentMessage = new Intent("mainBroadcast");
            intentMessage.putExtra("message", "scheduleAction");
            //TODO get building from Schedule Action
            intentMessage.putExtra("action", s);

            PendingIntent pendingIntentScheduleActionEvent = PendingIntent.getBroadcast(this, s.hashCode(), intentMessage, PendingIntent.FLAG_UPDATE_CURRENT);
            listAlarmsIntents.add(pendingIntentScheduleActionEvent);

            Calendar timeOff = Calendar.getInstance();
            int days = s.getDayOfWeek() + (7 - timeOff.get(Calendar.DAY_OF_WEEK));

            if (days == 7 && s.getStartHour() >= timeOff.get(Calendar.HOUR_OF_DAY)) {

                if (s.getStartHour() == timeOff.get(Calendar.HOUR_OF_DAY) && s.getStartMinute() > timeOff.get(Calendar.MINUTE)) {
                    days = 0;
                }

                if (s.getStartHour() > timeOff.get(Calendar.HOUR_OF_DAY)) {
                    days = 0;
                }
            }

            timeOff.add(Calendar.DATE, days);
            timeOff.set(Calendar.HOUR_OF_DAY, s.getStartHour());
            timeOff.set(Calendar.MINUTE, s.getStartMinute());
            timeOff.set(Calendar.SECOND, 0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeOff.getTimeInMillis(), pendingIntentScheduleActionEvent);
        }
    }

    private void deletePendingScheduleActionIntents() {

        for (PendingIntent intent : listAlarmsIntents) {
            alarmManager.cancel(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        Log.v("CoreService", "onStartCommand");
        return START_NOT_STICKY;
    }

    public void start() {
        Log.v("CoreService", "start");

        if (mAppState.isChangesTimer()) {
            changesHandler = new Handler();
            changesHandler.post(runnable);
            isChangeRunning = true;
        }
    }

    public void stop() {
        Log.v("CoreService", "stop");

        location.stopListening();
        geofencingController.stopGeofencing();

        if (isChangeRunning)
            changesHandler.removeCallbacks(runnable);

    }

    @Override
    public void onDestroy() {
        Log.v("CoreService", "onDestroy");

        stop();
        //log.info("killing app");

    }

    public void createChangeNotification(Change change) {
        Log.v("CoreService", "createChangeNotification");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.support.v7.appcompat.R.drawable.notification_template_icon_bg)
                        .setContentTitle(change.getName())
                        .setContentText(change.getStartDate());
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_CHANGE_ID, mBuilder.build());

    }

    public void createGeofenceNotification(String message, String requests) {
        Log.v("CoreService", "createGeofenceNotification");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.support.v7.appcompat.R.drawable.notification_template_icon_bg)
                        .setContentTitle("Smart school mode")
                        .setContentText("You are in " + requests)
                        .setContentIntent(pIntent)
                        .build();

        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_GEOFENCE_ID, notification);
    }

    public void cancelNotification(int id) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.v("CoreService", "runnable");
            try {
                manageChanges();
            } catch (IOException e) {
                e.printStackTrace();
            }
            changesHandler.postDelayed(this, 1000 * 60 * 15);
        }
    };

    public void manageChanges() throws IOException {
        Log.v("CoreService", "manageChanges");
        ChangesAsyncTask task = new ChangesAsyncTask(this);
        task.execute();
    }


    @Override
    public void updateChangesList() {

        List<Change> changesList = mAppCore.getChangeList();
        List<ScheduleAction> scheduleList = mAppCore.getScheduleActionList();


        for (Change change : changesList) {

            for (ScheduleAction scheduleAction : scheduleList) {

                if (change.getName().toLowerCase().contains(scheduleAction.getName().toLowerCase()) && !change.isViewed()) {
                    createChangeNotification(change);
                    change.setIsViewed(true);
                }
            }
        }
    }

    private void setRingTone(boolean isEnabled) {

        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        if (isEnabled) {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else {
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", true);
            sendBroadcast(intent);

        }
    }

    private void turnGPSOff() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is disabled
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", false);
            sendBroadcast(intent);

        }
    }

    private int calculateMinutesOfScheduleAction(ScheduleAction scheduleAction) {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(scheduleAction.getStartHour() + ":" + scheduleAction.getStartMinute());
            d2 = format.parse(scheduleAction.getEndHour() + ":" + scheduleAction.getEndHour());

        } catch (ParseException e) {
            return 0;
        }
        long diff = d2.getTime() - d1.getTime();

        return (int) diff / (60 * 1000);


    }
}
