package tobous.collegedroid.gui.mainScreen.navdrawer;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.gui.mainScreen.MainActivity;

/**
 * Created by Tobous on 5. 2. 2015.
 */
public class DrawerNavAdapter extends RecyclerView.Adapter<DrawerNavAdapter.MainViewHolder> {

    private AppCore mAppCore;
    private List<DrawerItem> mDataset;

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public MainViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolderHeader extends MainViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public ViewHolderHeader(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.main_nav_drawer_list_view_item_header_title);
        }
    }

    public static class ViewHolderItem extends MainViewHolder {
        // each data item is just a string in this case
        ImageView imgView;
        TextView titleView;
        public ViewHolderItem(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.main_nav_drawer_list_view_item_img);
            titleView = (TextView) itemView.findViewById(R.id.main_nav_drawer_list_view_item_txt_title);
            itemView.setClickable(true);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DrawerNavAdapter(List<DrawerItem> mDataset) {
        this.mDataset = mDataset;
        this.mAppCore = AppCore.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DrawerNavAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case 0:

                return new ViewHolderHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_nav_list_header, parent, false));

            case 1:

                return new ViewHolderItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_nav_list_item, parent, false));

            default:

                return null;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        int type = getItemViewType(position);

        switch(type) {

            case 0:

                ViewHolderHeader holderHeader = (ViewHolderHeader) holder;
                holderHeader.titleView.setText(mDataset.get(position).getTitle());
                holderHeader.titleView.setTypeface(mAppCore.getTypeFaceRobotoRegular());

                break;

            case 1:

                ViewHolderItem holderItem = (ViewHolderItem) holder;
                holderItem.titleView.setText(mDataset.get(position).getTitle());
                holderItem.imgView.setImageResource(mDataset.get(position).getIcon());
                holderItem.titleView.setTypeface(mAppCore.getTypeFaceRobotoRegular());


                break;

            default:

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {

        return  mDataset.get(position).getType();
    }


}