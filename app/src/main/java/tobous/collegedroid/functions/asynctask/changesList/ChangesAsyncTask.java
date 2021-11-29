package tobous.collegedroid.functions.asynctask.changesList;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.changes.ChangesManager;
import tobous.collegedroid.functions.changes.encapsulation.Change;

/**
 * Created by ondra on 31.1.16.
 */
public class ChangesAsyncTask extends AsyncTask {


    List<Change> changeList;
    AppCore mAppCore;

    ChangesAsyncTaskListener listener;

    public ChangesAsyncTask(ChangesAsyncTaskListener listener){

        this.listener = listener;
        mAppCore = AppCore.getInstance();

    }


    @Override
    protected Object doInBackground(Object[] params) {

        try {
            changeList = ChangesManager.getChanges("https://www.uhk.cz/cs-CZ/FIM/Studium/Rozvrhy/Zmeny-ve-vyuce");

            if (mAppCore.getChangeList() != null) {
                for (Change oldChange : mAppCore.getChangeList()) {
                    for (Change newChange : changeList) {
                        if (oldChange.getName().equals(newChange.getName()) && oldChange.getStartDate().equals(newChange.getStartDate())){
                            newChange.setIsViewed(oldChange.isViewed());
                        }
                    }
                }
            }

            mAppCore.setChangeList(changeList);

            listener.updateChangesList();

        } catch (IOException e) {

            //TODO internet connection error
            e.printStackTrace();
            return null;

        }

        return null;
    }


}
