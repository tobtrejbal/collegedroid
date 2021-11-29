package tobous.collegedroid.gui.dialogs.roomMapParameters;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.utils.DateUtils;

/**
 * Created by Tob on 24. 12. 2015.
 */
public class RoomMapParametersDialog  extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Button btnTime;
    Button btnDate;
    int day;
    int year;
    int month;
    int minute;
    int hour;
    RoomMapParametersDialogListener listener;
    AppState mAppState;

    public void setListener(RoomMapParametersDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final AppCore mAppCore = AppCore.getInstance();
        mAppState = AppState.getInstance();
        final Calendar previousDate = mAppState.getSelectedDate();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_room_map_parameters, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogView);

        final Spinner spinnerBuilding = (Spinner) dialogView.findViewById(R.id.spinner_building);
        btnTime = (Button) dialogView.findViewById(R.id.btn_time);
        btnDate = (Button) dialogView.findViewById(R.id.btn_date);

        btnDate.setText(DateUtils.getDateStringFromCalendar(mAppState.getSelectedDate(),2));
        btnTime.setText(DateUtils.getDateStringFromCalendar(mAppState.getSelectedDate(),1));

        fillSpinnerBuilding(mAppCore, spinnerBuilding);

        Button btnCancel = (Button) dialogView.findViewById(R.id.dialog_room_parameters_btn_cancel);
        // if button is clicked, close the custom
        Button btnOk = (Button) dialogView.findViewById(R.id.dialog_room_parameters_btn_ok);
        // if button is clicked, close the custom dialog

        setDate();

        final Dialog dialog = builder.create();

        dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT,800);

        // if button is clicked, close the custom dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppState.setSelectedDate(previousDate);
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppState.setSelectedBuilding(mAppCore.getBuildings().get(spinnerBuilding.getSelectedItemPosition()));
                mAppState.setSelectedDate(DateUtils.makeDateFromNumbers(minute, hour, day, month, year));
                listener.parametersSaved();
                dialog.dismiss();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), RoomMapParametersDialog.this,
                        mAppState.getSelectedDate().get(Calendar.HOUR_OF_DAY), mAppState.getSelectedDate().get(Calendar.MINUTE),true);

                timePickerDialog.show();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), RoomMapParametersDialog.this,
                        mAppState.getSelectedDate().get(Calendar.YEAR), mAppState.getSelectedDate().get(Calendar.MONTH),
                        mAppState.getSelectedDate().get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        return dialog;
    }

    public void fillSpinnerBuilding(AppCore mAppCore, Spinner spinner) {

        List<String> listBuildings = new ArrayList<String>();

        for(Building building : mAppCore.getBuildings()) {

            if(building.getFloorGraphs() != null) {

                listBuildings.add(getResources().getString(R.string.building_label)+" "+building.getName());

            }

        }


        ArrayAdapter<String> dataAdapterBuildings = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, listBuildings);
        dataAdapterBuildings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapterBuildings);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        btnDate.setText(DateUtils.getDateStringFromCalendar(calendar,2));
        Log.v(this.minute+":"+this.hour+":"+this.day+":"+this.month+":"+this.year,"");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        btnTime.setText(DateUtils.getDateStringFromCalendar(calendar,1));
        Log.v(this.minute+":"+this.hour+":"+this.day+":"+this.month+":"+this.year,"");
    }

    public void setDate() {

        this.minute = mAppState.getSelectedDate().get(Calendar.MINUTE);
        this.hour = mAppState.getSelectedDate().get(Calendar.HOUR_OF_DAY);
        this.day = mAppState.getSelectedDate().get(Calendar.DAY_OF_MONTH);
        this.month = mAppState.getSelectedDate().get(Calendar.MONTH);
        this.year = mAppState.getSelectedDate().get(Calendar.YEAR);
    }
}
