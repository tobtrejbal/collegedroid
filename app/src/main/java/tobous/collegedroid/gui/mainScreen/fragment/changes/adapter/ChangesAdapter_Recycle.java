package tobous.collegedroid.gui.mainScreen.fragment.changes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.functions.changes.encapsulation.Change;

/**
 * Created by ondra on 31.1.16.
 */
public class ChangesAdapter_Recycle extends RecyclerView.Adapter<ChangesAdapter_Recycle.ViewHolderChanges> {

    private List<Change> changes = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public ChangesAdapter_Recycle(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    public void setChanges(List<Change> changes){
        this.changes = changes;
    }

    @Override
    public ViewHolderChanges onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.fragment_changes_item, parent, false);
        ViewHolderChanges holderChanges = new ViewHolderChanges(v);
        return holderChanges;
    }

    @Override
    public void onBindViewHolder(ViewHolderChanges holder, int position) {

        Change change = changes.get(position);
        holder.author.setText(change.getAuthor());
        holder.name.setText(change.getName());
        holder.startDate.setText(change.getStartDate());


    }

    @Override
    public int getItemCount() {
        return changes.size();
    }

    static class ViewHolderChanges extends RecyclerView.ViewHolder{


        private TextView name;
        private TextView author;
        private TextView startDate;
        private TextView endDate;
        private TextView reason;


        public ViewHolderChanges(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.adapter_item_schedule_action_name);
            author = (TextView) itemView.findViewById(R.id.adapter_item_schedule_action_teacher);
            startDate = (TextView) itemView.findViewById(R.id.adapter_item_schedule_action_time);
            //endDate = (TextView) itemView.findViewById(R.id.txtUcitel);
            //reason = (TextView) itemView.findViewById(R.id.txtUcitel);
            itemView.setClickable(true);

        }
    }

}
