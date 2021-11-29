package tobous.collegedroid.gui.dialogs.roomdetail;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.plans.encapsulation.Room;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
import tobous.collegedroid.gui.utils.RecyclerAdapterListener;
import tobous.collegedroid.gui.utils.RecyclerItemTouchListener;

/**
 * Created by Tob on 23. 12. 2015.
 */
public class RoomDetailDialog extends DialogFragment implements RecyclerItemTouchListener {

    RoomDetailDialogListener listener;
    AppCore mAppCore;
    AppState mAppState;
    RecyclerAdapterListener recyclerAdapterListener;
    Room room;

    public void setListener(RoomDetailDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        mAppCore = AppCore.getInstance();
        mAppState = AppState.getInstance();

        recyclerAdapterListener = new RecyclerAdapterListener(getContext(), this);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_room_detail, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogView);

        room = (Room) getArguments().getSerializable("room");

        TextView mTextView = (TextView) dialogView.findViewById(R.id.room_detail_title);
        final RecyclerView scheduleActionList = (RecyclerView) dialogView.findViewById(R.id.room_detail_list_schedule_actions);

        final RoomDetailDialogAdapter mScheduleActionAdapter = new RoomDetailDialogAdapter(filterActions(room.getActionList()));

        mTextView.setText(room.getName());

        scheduleActionList.setAdapter(mScheduleActionAdapter);

        scheduleActionList.setHasFixedSize(true);

        // use a linear layout manager
        scheduleActionList.setLayoutManager(new LinearLayoutManager(getContext()));

        mScheduleActionAdapter.notifyDataSetChanged();

        scheduleActionList.addOnItemTouchListener(recyclerAdapterListener);

        Button btnNavigate = (Button) dialogView.findViewById(R.id.room_detail_btn_navigate);
        // if button is clicked, close the custom dialog

        Button btnOk = (Button) dialogView.findViewById(R.id.room_detail_btn_ok);

        final Dialog dialog = builder.create();

        // if button is clicked, close the custom dialog
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.startNavigation(room);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public List<ScheduleAction> filterActions(List<ScheduleAction> actions) {

        List<ScheduleAction> filteredActions = new ArrayList<>();

        for(ScheduleAction action : actions) {
            //Log.v("filtering", action.getDayOfWeek()+"dd"+mAppState.getSelectedDate().get(Calendar.DAY_OF_WEEK));
            if(mAppState.getSelectedDate().get(Calendar.DAY_OF_WEEK) == action.getDayOfWeek() && action.isInWeek(mAppState.getSelectedDate().get(Calendar.WEEK_OF_YEAR))) {
                filteredActions.add(action);
            }
        }

        return filteredActions;
    }

    @Override
    public void onLongTouch(int position) {
        listener.itemClicked(filterActions(room.getActionList()).get(position));
    }

    @Override
    public void onShortTouch(int position) {
        listener.itemClicked(filterActions(room.getActionList()).get(position));
    }
}
