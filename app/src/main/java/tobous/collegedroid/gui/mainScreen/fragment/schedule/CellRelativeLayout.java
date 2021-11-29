package tobous.collegedroid.gui.mainScreen.fragment.schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Tob on 16. 12. 2015.
 */
public class CellRelativeLayout extends RelativeLayout{

    private int scheduleId = -1;

    public CellRelativeLayout(Context context) {
        super(context);
    }

    public CellRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }
}
