package tobous.collegedroid.gui.mainScreen.fragment.changes.adapter;

import android.app.Activity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.changes.encapsulation.Change;

/**
 * Created by Ondra on 31. 1. 2016.
 */
public class ChangesAdapter extends ArrayAdapter<Change> {

    private final Activity activity;

    static class ViewHolder{

        TextView txtName;
        TextView txtReason;

    }

    public ChangesAdapter(Activity activity) {

        super(activity, R.layout.fragment_changes_item);

        this.activity = activity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {

            LayoutInflater inflater = activity.getLayoutInflater();
            try{
                convertView = inflater.inflate(R.layout.dialog_room_detail_item, parent, false);
            } catch
                    (InflateException ex) {
                ex.printStackTrace();
            }

            mViewHolder = new ViewHolder();

            mViewHolder.txtName = (TextView) convertView.findViewById(R.id.adapter_item_schedule_action_name);
            mViewHolder.txtReason = (TextView) convertView.findViewById(R.id.adapter_item_schedule_action_time);

            // store the holder with the view.
            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder) convertView.getTag();

        }

        Change item = getItem(position);

        if (item != null) {

            mViewHolder.txtName.setText(item.getName());
            mViewHolder.txtReason.setText(item.getReason());

        }

        return convertView;


    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEnabled(int position) {

        return true;

    }
}