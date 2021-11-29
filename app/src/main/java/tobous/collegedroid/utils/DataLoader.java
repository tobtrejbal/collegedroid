package tobous.collegedroid.utils;

import java.util.List;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tobous on 7. 11. 2015.
 */
public class DataLoader {

    AppCore mAppCore = AppCore.getInstance();

    public void uploadData(List<ScheduleAction> actions) {

        mAppCore.setScheduleActionList(actions);

    }



}
