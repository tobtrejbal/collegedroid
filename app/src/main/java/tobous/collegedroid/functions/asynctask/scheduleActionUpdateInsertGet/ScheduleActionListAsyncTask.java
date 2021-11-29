package tobous.collegedroid.functions.asynctask.scheduleActionUpdateInsertGet;

        import android.content.Context;
        import android.os.AsyncTask;

        import java.util.ArrayList;
        import java.util.List;

        import tobous.collegedroid.core.AppCore;
        import tobous.collegedroid.functions.database.ScheduleDatabaseManager;
        import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 11. 1. 2016.
 */
public class ScheduleActionListAsyncTask extends AsyncTask {

    ScheduleDatabaseManager scheduleDatabaseManager;

    Context context;

    AppCore mAppCore;

    ScheduleActionListAsyncTaskListener listener;

    public ScheduleActionListAsyncTask(Context context, ScheduleActionListAsyncTaskListener listener) {


        scheduleDatabaseManager = new ScheduleDatabaseManager(context);

        mAppCore = AppCore.getInstance();

        this.listener = listener;

    }

    @Override
    protected Object doInBackground(Object[] objects) {

        List<ScheduleAction> newData = (ArrayList) objects[0];
        String userId = (String) objects[1];

        try {

            if (newData != null) {

                scheduleDatabaseManager.updateOrInsertActions(newData);


            }

            mAppCore.setScheduleActionList(scheduleDatabaseManager.getUsersActions(userId));
            listener.dataUpdated();


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
