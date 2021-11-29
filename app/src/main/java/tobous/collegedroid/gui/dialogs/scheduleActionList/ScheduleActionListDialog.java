package tobous.collegedroid.gui.dialogs.scheduleActionList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.asynctask.scheduleActionRemove.ScheduleActionRemoveAsyncTask;
import tobous.collegedroid.functions.asynctask.scheduleActionRemove.ScheduleActionRemoveAsyncTaskListener;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
import tobous.collegedroid.gui.dialogs.scheduleActionDetail.ScheduleActionDetailDialog;
import tobous.collegedroid.gui.dialogs.scheduleActionDetail.ScheduleActionDetailDialogListener;
import tobous.collegedroid.gui.mainScreen.MainActivity;
import tobous.collegedroid.gui.utils.RecyclerAdapterListener;
import tobous.collegedroid.gui.utils.RecyclerItemTouchListener;

/**
 * Created by Tob on 11. 1. 2016.
 */
public class ScheduleActionListDialog extends DialogFragment implements RecyclerItemTouchListener, ScheduleActionRemoveAsyncTaskListener, ScheduleActionDetailDialogListener, ScheduleActionListDialogAdapterListener {

    RecyclerAdapterListener recyclerAdapterListener;
    ScheduleActionListDialogAdapter mScheduleActionAdapter;
    AppState mAppState = AppState.getInstance();
    Handler mHandler;
    Runnable mRunnableRefresh;

    ScheduleActionListDialogListener listener;

    public void setListener(ScheduleActionListDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final AppCore mAppCore = AppCore.getInstance();

        mHandler = new Handler();
        mRunnableRefresh = new Runnable() {
            @Override
            public void run() {
                mScheduleActionAdapter.setDataset(mAppCore.getScheduleActionList());
                mScheduleActionAdapter.notifyDataSetChanged();
            }
        };

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_schedule_action_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogView);

        recyclerAdapterListener = new RecyclerAdapterListener(getContext(), this);

        final RecyclerView scheduleActionList = (RecyclerView) dialogView.findViewById(R.id.schedule_action_list);

        mScheduleActionAdapter = new ScheduleActionListDialogAdapter(this);

        scheduleActionList.setAdapter(mScheduleActionAdapter);

        scheduleActionList.setHasFixedSize(true);

        scheduleActionList.addOnItemTouchListener(recyclerAdapterListener);

        // use a linear layout manager
        scheduleActionList.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnOk = (Button) dialogView.findViewById(R.id.schedule_action_list_button_ok);
        Button btnAdd = (Button) dialogView.findViewById(R.id.schedule_action_list_button_add_action);

        final Dialog dialog = builder.create();

        // if button is clicked, close the custom dialog
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                ScheduleActionDetailDialog newFragment = new ScheduleActionDetailDialog();
                args.putBoolean("editable", true);
                args.putBoolean("editMode", true);
                newFragment.setListener(ScheduleActionListDialog.this);
                newFragment.setArguments(args);
                newFragment.setTargetFragment(getTargetFragment(), 1);
                newFragment.show(getActivity().getSupportFragmentManager(), "date picker");
            }
        });

        mScheduleActionAdapter.notifyDataSetChanged();

        return dialog;
    }

    @Override
    public void itemDetail(final ScheduleAction scheduleAction) {

        Bundle args = new Bundle();
        args.putSerializable("scheduleAction", scheduleAction);
        args.putBoolean("editable", true);
        args.putBoolean("editMode", false);
        ScheduleActionDetailDialog newFragment = new ScheduleActionDetailDialog();
        newFragment.setListener(ScheduleActionListDialog.this);
        newFragment.setArguments(args);
        newFragment.setTargetFragment(getTargetFragment(), 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "date picker");

    }

    @Override
    public void deleteItem(final ScheduleAction scheduleAction) {

        final AppCore mAppCore = AppCore.getInstance();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                (MainActivity) getActivity());

        // set title
        alertDialogBuilder.setTitle(R.string.title_delete_confirm);

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.msg_confirm_delete)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        ScheduleActionRemoveAsyncTask scheduleActionAsyncTask = new ScheduleActionRemoveAsyncTask(mAppCore, ScheduleActionListDialog.this);
                        scheduleActionAsyncTask.execute(scheduleAction, mAppState.getUserId());
                    }
                })
                .setNegativeButton(R.string.btn_cancel,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void removed() {
        Log.v("alalala", "sdd");
        mHandler.post(mRunnableRefresh);
        listener.refresh();
    }

    @Override
    public void edited() {
        mHandler.post(mRunnableRefresh);
        listener.refresh();
    }

    @Override
    public void onLongTouch(int position) {

    }

    @Override
    public void onShortTouch(int position) {

    }
}