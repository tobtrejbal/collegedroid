package tobous.collegedroid.gui.dialogs.roomdetail;

import tobous.collegedroid.functions.plans.encapsulation.Room;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 23. 12. 2015.
 */
public interface RoomDetailDialogListener {

        public void cancel();
        public void itemClicked(ScheduleAction scheduleAction);
        public void startNavigation(Room room);


}
