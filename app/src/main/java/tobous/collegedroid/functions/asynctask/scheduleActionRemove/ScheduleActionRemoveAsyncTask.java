package tobous.collegedroid.functions.asynctask.scheduleActionRemove;

import android.content.Context;
import android.os.AsyncTask;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.database.ScheduleDatabaseManager;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 11. 1. 2016.
 */
public class ScheduleActionRemoveAsyncTask extends AsyncTask {

    ScheduleDatabaseManager scheduleDatabaseManager;

    Context context;

    AppCore mAppCore;

    ScheduleActionRemoveAsyncTaskListener listener;

    public ScheduleActionRemoveAsyncTask(Context context, ScheduleActionRemoveAsyncTaskListener listener) {


        scheduleDatabaseManager = new ScheduleDatabaseManager(context);

        mAppCore = AppCore.getInstance();

        this.listener = listener;

    }

    @Override
    protected Object doInBackground(Object[] objects) {

        ScheduleAction scheduleAction = (ScheduleAction) objects[0];
        String userId = (String) objects[1];

        try {

            scheduleDatabaseManager.removeItem(scheduleAction);
            mAppCore.setScheduleActionList(scheduleDatabaseManager.getUsersActions(userId));
            listener.removed();


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