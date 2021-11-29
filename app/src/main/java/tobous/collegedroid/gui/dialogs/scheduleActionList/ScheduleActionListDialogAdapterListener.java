package tobous.collegedroid.gui.dialogs.scheduleActionList;

import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 19. 1. 2016.
 */
public interface ScheduleActionListDialogAdapterListener {

    public void deleteItem(ScheduleAction scheduleAction);

    public void itemDetail(ScheduleAction scheduleAction);

}
