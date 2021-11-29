package tobous.collegedroid.gui.dialogs.scheduleActionList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 22. 12. 2015.
 */
public class ScheduleActionListDialogAdapter extends RecyclerView.Adapter<ScheduleActionListDialogAdapter.MainViewHolder> {

    private AppCore mAppCore;
    private ScheduleActionListDialogAdapterListener mListener;
    private List<ScheduleAction> mDataset;

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public TextView txtTime;
        public Button btnDelete;
        public Button btnDetail;
        public TextView txtTypeTeacher;

        // each data item is just a string in this case
        public MainViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.dialog_schedule_list_item_name);
            txtTime = (TextView) itemView.findViewById(R.id.dialog_schedule_list_item_time);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            btnDetail = (Button) itemView.findViewById(R.id.btn_detail);
            txtTypeTeacher = (TextView) itemView.findViewById(R.id.dialog_schedule_list_item_type_teacher);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ScheduleActionListDialogAdapter(ScheduleActionListDialogAdapterListener listener) {
        this.mListener = listener;
        this.mAppCore = AppCore.getInstance();
        this.mDataset = mAppCore.getScheduleActionList();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ScheduleActionListDialogAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_schedule_action_list_item, parent, false));

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final ScheduleAction item = mDataset.get(position);

        DecimalFormat twoDigitFormat = new DecimalFormat("00");

        String time = "";
        time += twoDigitFormat.format(item.getStartHour()) + ":" + (twoDigitFormat.format(item.getStartMinute()));
        time += " - ";
        time += twoDigitFormat.format(item.getEndHour()) + ":" + (twoDigitFormat.format(item.getEndMinute()));


        holder.txtName.setText(item.getName());
        holder.txtTime.setText(mAppCore.getDays()[item.getDayOfWeek()-1] + ", "+ time);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteItem(item);
            }
        });
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.itemDetail(item);
            }
        });
        holder.txtTypeTeacher.setText(mAppCore.getActionTypes()[item.getActionType()] + " : " + item.getTeacher());

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

    public void setDataset(List<ScheduleAction> scheduleActions) {
      this.mDataset = scheduleActions;
    }

}