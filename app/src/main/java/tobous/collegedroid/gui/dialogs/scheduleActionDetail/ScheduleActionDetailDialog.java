package tobous.collegedroid.gui.dialogs.scheduleActionDetail;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.asynctask.scheduleActionUpdate.ScheduleActionUpdateAsyncTask;
import tobous.collegedroid.functions.asynctask.scheduleActionUpdate.ScheduleActionUpdateAsyncTaskListener;
import tobous.collegedroid.functions.schedule.ScheduleUtils;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
import tobous.collegedroid.utils.DateUtils;

/**
 * Created by Tob on 23. 12. 2015.
 */
public class ScheduleActionDetailDialog extends DialogFragment implements ScheduleActionUpdateAsyncTaskListener {

    AppState mAppState = AppState.getInstance();
    AppCore mAppCore = AppCore.getInstance();

    DecimalFormat twoDigitFormat = new DecimalFormat("00");

    TextView mTxtTitle;
    EditText mTxtTitleEdit;
    TextView mTxtName;
    EditText mTxtNameEdit;
    TextView mTxtTeacher;
    EditText mTxtTeacherEdit;
    TextView mTxtPlace;
    EditText mTxtPlaceEdit;
    TextView mTxtCapacity;
    EditText mTxtCapacityEdit;
    TextView mTxtOccupancy;
    EditText mTxtOccupancyEdit;
    TextView mTxtActionType;
    Spinner mSpinnerActionType;
    TextView mTxtStartTime;
    Button mBtnStartTime;
    TextView mTxtEndTime;
    Button mBtnEndTime;
    TextView mTxtDay;
    Spinner mSpinnerDay;
    TextView mTxtWeekType;
    Spinner mSpinnerWeekType;

    boolean editMode;
    boolean editable;

    Button btnLeft;
    Button btnRight;

    Handler mHandler = new Handler();
    Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    ScheduleAction scheduleAction;

    ScheduleActionDetailDialogListener listener;

    public void setListener(ScheduleActionDetailDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_schedule_action_detail, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogView);

        mTxtTitle = (TextView) dialogView.findViewById(R.id.schedule_action_detail_title);
        mTxtTitleEdit = (EditText) dialogView.findViewById(R.id.schedule_action_detail_title_edit);
        mTxtName = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_name);
        mTxtNameEdit = (EditText) dialogView.findViewById(R.id.schedule_action_detail_txt_name_edit);
        mTxtTeacher = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_teacher);
        mTxtTeacherEdit = (EditText) dialogView.findViewById(R.id.schedule_action_detail_txt_teacher_edit);
        mTxtPlace = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_place);
        mTxtPlaceEdit = (EditText) dialogView.findViewById(R.id.schedule_action_detail_txt_place_edit);
        mTxtCapacity = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_capacity);
        mTxtCapacityEdit = (EditText) dialogView.findViewById(R.id.schedule_action_detail_txt_capacity_edit);
        mTxtOccupancy = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_occupancy);
        mTxtOccupancyEdit = (EditText) dialogView.findViewById(R.id.schedule_action_detail_txt_occupancy_edit);
        mTxtActionType = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_action_type);
        mSpinnerActionType = (Spinner) dialogView.findViewById(R.id.schedule_action_detail_action_type_spinner);
        mTxtStartTime = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_start_time);
        mBtnStartTime = (Button)  dialogView.findViewById(R.id.schedule_action_detail_start_time_button);
        mTxtEndTime = (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_end_time);
        mBtnEndTime = (Button)  dialogView.findViewById(R.id.schedule_action_detail_end_time_button);
        mTxtDay =  (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_day);
        mSpinnerDay = (Spinner) dialogView.findViewById(R.id.schedule_action_detail_spinner_day);
        mTxtWeekType =  (TextView) dialogView.findViewById(R.id.schedule_action_detail_txt_week_type);
        mSpinnerWeekType = (Spinner) dialogView.findViewById(R.id.schedule_action_detail_spinner_week_type);

        scheduleAction = (ScheduleAction) getArguments().getSerializable("scheduleAction");
        editable = getArguments().getBoolean("editable");
        editMode = getArguments().getBoolean("editMode");

        btnLeft = (Button) dialogView.findViewById(R.id.schedule_action_detail_btn_left);
        btnRight = (Button) dialogView.findViewById(R.id.schedule_action_detail_btn_right);

        final Dialog dialog = builder.create();

        mTxtCapacityEdit.setText("0");
        mTxtOccupancyEdit.setText("0");

        mBtnStartTime.setText(twoDigitFormat.format(0) +":"+twoDigitFormat.format(0));
        mBtnEndTime.setText(twoDigitFormat.format(23)+":"+twoDigitFormat.format(59));

        fillSpinners();
        refresh();

        // if button is clicked, close the custom dialog
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode) {
                    if(scheduleAction==null) {
                        dialog.dismiss();
                    } else {
                        editMode = false;
                        refresh();
                    }
                } else {
                    editMode = true;
                    refresh();
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode) {
                    editMode = false;
                    saveScheduleAction();
                } else {
                    dialog.dismiss();
                }
            }
        });

        mBtnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String[] endTime = mBtnEndTime.getText().toString().split(":");
                        Log.v(endTime[0]+endTime[1],hourOfDay+minute+"aaaa");
                        if(DateUtils.isAfter(minute, hourOfDay, Integer.parseInt(endTime[1]), Integer.parseInt(endTime[0]))) {
                            mBtnStartTime.setText(twoDigitFormat.format(hourOfDay)+":"+twoDigitFormat.format(minute));
                        } else {
                            Toast toast = Toast.makeText(getContext(), "Pocatecni cas musi byt mensi nez konecny", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                },
                        mAppState.getSelectedDate().get(Calendar.HOUR_OF_DAY), mAppState.getSelectedDate().get(Calendar.MINUTE),true);

                timePickerDialog.show();
            }
        });

        mBtnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String[] startTime = mBtnStartTime.getText().toString().split(":");
                        if(!DateUtils.isAfter(minute, hourOfDay, Integer.parseInt(startTime[1]), Integer.parseInt(startTime[0]))) {
                            mBtnEndTime.setText(twoDigitFormat.format(hourOfDay)+":"+twoDigitFormat.format(minute));
                        } else {
                            Toast toast = Toast.makeText(getContext(), "Pocatecni cas musi byt mensi nez konecny", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                },
                        mAppState.getSelectedDate().get(Calendar.HOUR_OF_DAY), mAppState.getSelectedDate().get(Calendar.MINUTE),true);

                timePickerDialog.show();
            }
        });

        return dialog;
    }

    @Override
    public void scheduleActionUpdated(ScheduleAction scheduleAction) {
        this.scheduleAction = scheduleAction;
        mHandler.post(mRefresh);
        listener.edited();
    }

    public void refresh() {

        if(!editable) {
            btnLeft.setEnabled(false);
        }

        if(editMode) {

            showEditableItems();

            btnLeft.setText(R.string.btn_cancel);
            btnRight.setText(R.string.btn_save);

        } else {

            btnLeft.setText(R.string.btn_edit);
            btnRight.setText(R.string.btn_ok);

            showInfoItems();

        }

        if(scheduleAction != null) {
            fillDetails();
        }
    }

    public void fillDetails() {

        if(editMode) {
            mTxtTitleEdit.setText(scheduleAction.getFullName());
            mTxtNameEdit.setText(scheduleAction.getName());
            mTxtTeacherEdit.setText(scheduleAction.getTeacher());
            mTxtPlaceEdit.setText(scheduleAction.getPlace());
            mTxtCapacityEdit.setText(scheduleAction.getCapacity()+"");
            mTxtOccupancyEdit.setText(scheduleAction.getOccupancy()+"");
            mSpinnerActionType.setSelection(scheduleAction.getActionType());
            mBtnStartTime.setText(twoDigitFormat.format(scheduleAction.getStartHour()) + ":" + twoDigitFormat.format(scheduleAction.getStartMinute()));
            mBtnEndTime.setText(twoDigitFormat.format(scheduleAction.getEndHour())+":"+twoDigitFormat.format(scheduleAction.getEndMinute()));
            mSpinnerDay.setSelection(scheduleAction.getDayOfWeek() - 1);
            mSpinnerWeekType.setSelection(scheduleAction.getWeekType());
        } else {
            mTxtTitle.setText(scheduleAction.getFullName());
            mTxtName.setText(scheduleAction.getName());
            mTxtTeacher.setText(scheduleAction.getTeacher());
            mTxtPlace.setText(scheduleAction.getPlace());
            mTxtCapacity.setText(scheduleAction.getCapacity()+"");
            mTxtOccupancy.setText(scheduleAction.getOccupancy()+"");
            mTxtActionType.setText(mAppCore.getActionTypes()[scheduleAction.getActionType()]);
            mTxtStartTime.setText(twoDigitFormat.format(scheduleAction.getStartHour())+":"+twoDigitFormat.format(scheduleAction.getStartMinute()));
            mTxtEndTime.setText(twoDigitFormat.format(scheduleAction.getEndHour())+":" + twoDigitFormat.format(scheduleAction.getEndMinute()));
            mTxtDay.setText(mAppCore.getDays()[scheduleAction.getDayOfWeek() - 1]);
            mTxtWeekType.setText(mAppCore.getWeekTypes()[scheduleAction.getWeekType()]);
        }

    }

    public void saveScheduleAction() {
        if(scheduleAction == null) {
            scheduleAction = new ScheduleAction();
        }
        scheduleAction.setFullName(mTxtTitleEdit.getText().toString());
        scheduleAction.setName(mTxtNameEdit.getText().toString());
        scheduleAction.setTeacher(mTxtTeacherEdit.getText().toString());
        scheduleAction.setPlace(mTxtPlaceEdit.getText().toString());
        scheduleAction.setCapacity(Integer.parseInt(mTxtCapacityEdit.getText().toString()));
        scheduleAction.setOccupancy(Integer.parseInt(mTxtOccupancyEdit.getText().toString()));
        scheduleAction.setActionType(mSpinnerActionType.getSelectedItemPosition());
        String[] startTime = mBtnStartTime.getText().toString().split(":");
        scheduleAction.setStartMinute(Integer.parseInt(startTime[1]));
        scheduleAction.setStartHour(Integer.parseInt(startTime[0]));
        String[] endTime = mBtnEndTime.getText().toString().split(":");
        scheduleAction.setEndMinute(Integer.parseInt(endTime[1]));
        scheduleAction.setEndHour(Integer.parseInt(endTime[0]));
        scheduleAction.setDayOfWeek(mSpinnerDay.getSelectedItemPosition()+1);
        scheduleAction.setWeekType(mSpinnerWeekType.getSelectedItemPosition());
        if(scheduleAction.getId() ==  null) {
            scheduleAction.setId(ScheduleUtils.makeId(scheduleAction));
        }
        scheduleAction.setUser(mAppState.getUserId());
        ScheduleActionUpdateAsyncTask asyncTask = new ScheduleActionUpdateAsyncTask(mAppCore, ScheduleActionDetailDialog.this);
        asyncTask.execute(scheduleAction, mAppState.getUserId());
    }

    public void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public void setInvisible(View view) {
        view.setVisibility(View.GONE);
    }

    public void showEditableItems() {

        setVisible(mTxtTitleEdit);
        setVisible(mTxtNameEdit);
        setVisible(mTxtTeacherEdit);
        setVisible(mTxtPlaceEdit);
        setVisible(mTxtCapacityEdit);
        setVisible(mTxtOccupancyEdit);
        setVisible(mSpinnerActionType);
        setVisible(mBtnStartTime);
        setVisible(mBtnEndTime);
        setVisible(mSpinnerDay);
        setVisible(mSpinnerWeekType);

        setInvisible(mTxtTitle);
        setInvisible(mTxtName);
        setInvisible(mTxtTeacher);
        setInvisible(mTxtPlace);
        setInvisible(mTxtCapacity);
        setInvisible(mTxtOccupancy);
        setInvisible(mTxtActionType);
        setInvisible(mTxtStartTime);
        setInvisible(mTxtEndTime);
        setInvisible(mTxtDay);
        setInvisible(mTxtWeekType);

    }

    public void showInfoItems() {

        setInvisible(mTxtTitleEdit);
        setInvisible(mTxtNameEdit);
        setInvisible(mTxtTeacherEdit);
        setInvisible(mTxtPlaceEdit);
        setInvisible(mTxtCapacityEdit);
        setInvisible(mTxtOccupancyEdit);
        setInvisible(mSpinnerActionType);
        setInvisible(mBtnStartTime);
        setInvisible(mBtnEndTime);
        setInvisible(mSpinnerDay);
        setInvisible(mSpinnerWeekType);

        setVisible(mTxtTitle);
        setVisible(mTxtName);
        setVisible(mTxtTeacher);
        setVisible(mTxtPlace);
        setVisible(mTxtCapacity);
        setVisible(mTxtOccupancy);
        setVisible(mTxtActionType);
        setVisible(mTxtStartTime);
        setVisible(mTxtEndTime);
        setVisible(mTxtDay);
        setVisible(mTxtWeekType);

    }

    public void fillSpinners() {
        List<String> days = new ArrayList<String>();

        for(int i = 0; i < mAppCore.getDays().length; i++) {

            days.add(mAppCore.getDays()[i]);

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, days);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDay.setAdapter(dataAdapter);

        List<String> actions = new ArrayList<String>();

        for(int i = 0; i < mAppCore.getActionTypes().length; i++) {

            actions.add(mAppCore.getActionTypes()[i]);

        }

        ArrayAdapter<String> dataAdapterType = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, actions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerActionType.setAdapter(dataAdapterType);

        List<String> weekTypes = new ArrayList<String>();

        for(int i = 0; i < mAppCore.getWeekTypes().length-1; i++) {

            weekTypes.add(mAppCore.getWeekTypes()[i]);

        }

        ArrayAdapter<String> dataAdapterWeekTypes = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, weekTypes);
        dataAdapterWeekTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerWeekType.setAdapter(dataAdapterWeekTypes);

    }
}
