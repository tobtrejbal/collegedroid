package tobous.collegedroid.gui.dialogs.roomdetail;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 22. 12. 2015.
 */
public class RoomDetailDialogAdapter extends RecyclerView.Adapter<RoomDetailDialogAdapter.MainViewHolder> {

    private AppCore mAppCore;
    private List<ScheduleAction> mDataset;

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public TextView txtTime;
        public TextView txtTypeTeacher;
        public TextView txtCapacity;

        // each data item is just a string in this case
        public MainViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.adapter_item_schedule_action_name);
            txtTime = (TextView) itemView.findViewById(R.id.adapter_item_schedule_action_time);
            txtTypeTeacher = (TextView) itemView.findViewById(R.id.adapter_item_schedule_action_type_teacher);
            txtCapacity = (TextView) itemView.findViewById(R.id.adapter_item_schedule_action_capacity);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RoomDetailDialogAdapter(List<ScheduleAction> mDataset) {
        Collections.sort(mDataset);
        this.mAppCore = AppCore.getInstance();
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RoomDetailDialogAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_room_detail_item, parent, false));

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final ScheduleAction item = mDataset.get(position);

        DecimalFormat twoDigitFormat = new DecimalFormat("00");

        String time = "";
        time += twoDigitFormat.format(item.getStartHour())+":"+(twoDigitFormat.format(item.getStartMinute()));
        time += " - ";
        time += twoDigitFormat.format(item.getEndHour())+":"+(twoDigitFormat.format(item.getEndMinute()));

        float capacityRatio = item.getOccupancy()/item.getCapacity();

        holder.txtName.setText(item.getName());
        holder.txtTime.setText(mAppCore.getDays()[item.getDayOfWeek()-1]+", "+ time);
        holder.txtTypeTeacher.setText(mAppCore.getActionTypes()[item.getActionType()] + ": " + item.getTeacher());
        holder.txtCapacity.setText("Kapacita: " + item.getOccupancy() + "/" + item.getCapacity());
        if(capacityRatio > 0 && capacityRatio < 1.0) {
            holder.txtCapacity.setTextColor(Color.GREEN);
        }
        if(capacityRatio >= 1.0) {
            holder.txtCapacity.setTextColor(Color.RED);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }
}