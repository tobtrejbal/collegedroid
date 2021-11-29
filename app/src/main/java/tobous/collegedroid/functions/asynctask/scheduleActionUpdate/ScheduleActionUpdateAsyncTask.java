package tobous.collegedroid.functions.asynctask.scheduleActionUpdate;

import android.content.Context;
import android.os.AsyncTask;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.database.ScheduleDatabaseManager;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 12. 1. 2016.
 */
public class ScheduleActionUpdateAsyncTask extends AsyncTask {

    ScheduleDatabaseManager scheduleDatabaseManager;

    Context context;

    AppCore mAppCore;

    ScheduleActionUpdateAsyncTaskListener listener;

    public ScheduleActionUpdateAsyncTask(Context context, ScheduleActionUpdateAsyncTaskListener listener) {


        scheduleDatabaseManager = new ScheduleDatabaseManager(context);

        mAppCore = AppCore.getInstance();

        this.listener = listener;

    }

    @Override
    protected Object doInBackground(Object[] objects) {

        ScheduleAction scheduleAction = (ScheduleAction) objects[0];
        String userId = (String) objects[1];

        try {

            scheduleDatabaseManager.insertUpdate(scheduleAction);
            mAppCore.setScheduleActionList(scheduleDatabaseManager.getUsersActions(userId));
            listener.scheduleActionUpdated(scheduleAction);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return null;

    }


    protected void onProgressUpdate(Integer... progress) {

        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
    }

}