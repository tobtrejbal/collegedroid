package tobous.collegedroid.gui.activities.setting;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.service.CoreService;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.utils.DateUtils;

/**
 * Created by Tob on 12. 1. 2016.
 */
public class SettingActivity extends AppCompatActivity  {

    Spinner mSpinnerFirstDay;
    CheckBox mCheckBoxHorizontalMenu;
    CheckBox mCheckBoxChanges;
    CheckBox mCheckBoxGeofence;
    Button mButtonStartTime;
    Button mButtonEndTime;
    ListView mListViewSetting;

    Handler mHandler = new Handler();

    private final AppCore mAppCore;
    AppState mAppState;
    Runnable mRefresh;

    DecimalFormat twoDigitFormat = new DecimalFormat("00");

    public SettingActivity() {

        mAppCore = AppCore.getInstance();
        mAppState = AppState.getInstance();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_setting);

        final AppState mAppState = AppState.getInstance();
        // get the action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.menu_title_setting);

        mSpinnerFirstDay = (Spinner) findViewById(R.id.setting_item_spinner_first_day);
        mCheckBoxHorizontalMenu = (CheckBox) findViewById(R.id.setting_item_checkbox_horizontal_menu);
        mButtonStartTime = (Button) findViewById(R.id.setting_item_button_schedule_start);
        mButtonEndTime = (Button) findViewById(R.id.setting_item_button_schedule_end);
        mCheckBoxChanges = (CheckBox) findViewById(R.id.setting_item_checkbox_changes_timer);
        mCheckBoxGeofence = (CheckBox) findViewById(R.id.setting_item_checkbox_geofence_timer);

        mButtonStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String[] endTime = mButtonEndTime.getText().toString().split(":");
                        Log.v(endTime[0] + endTime[1], hourOfDay + minute + "aaaa");
                        if (DateUtils.isBefore(Integer.parseInt(endTime[1]), Integer.parseInt(endTime[0]), minute, hourOfDay)) {
                            mButtonStartTime.setText(twoDigitFormat.format(hourOfDay) + ":" + twoDigitFormat.format(minute));
                        } else {
                            Toast toast = Toast.makeText(SettingActivity.this, "Pocatecni cas musi byt mensi nez konecny", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                },
                        mAppState.getScheduleStartHour(), mAppState.getScheduleStartMinute(), true);

                timePickerDialog.show();
            }
        });

        mButtonEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String[] startTime = mButtonStartTime.getText().toString().split(":");
                        Log.v(startTime[0] + startTime[1], hourOfDay + minute + "aaaa");
                        if (DateUtils.isAfter(Integer.parseInt(startTime[1]), Integer.parseInt(startTime[0]), minute, hourOfDay)) {
                            mButtonEndTime.setText(twoDigitFormat.format(hourOfDay) + ":" + twoDigitFormat.format(minute));
                        } else {
                            Toast toast = Toast.makeText(SettingActivity.this, "Pocatecni cas musi byt mensi nez konecny", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                },
                        mAppState.getScheduleStartHour(), mAppState.getScheduleStartMinute(), true);

                timePickerDialog.show();
            }
        });

        mButtonStartTime.setText(twoDigitFormat.format(mAppState.getScheduleStartHour()) + ":" + twoDigitFormat.format(mAppState.getScheduleStartMinute()));
        mButtonEndTime.setText(twoDigitFormat.format(mAppState.getScheduleEndHour()) + ":" + twoDigitFormat.format(mAppState.getScheduleEndMinute()));
        fillSpinners();

        mSpinnerFirstDay.setSelection(mAppState.getScheduleFirstDay() - 1);
        mCheckBoxHorizontalMenu.setChecked(mAppState.isHorizontalMenu());
        mCheckBoxChanges.setChecked(mAppState.isChangesTimer());
        mCheckBoxGeofence.setChecked(mAppState.isGeofenceTimer());

        // Enabling Back navigation on Action Bar icon
        //actionBar.setDisplayHomeAsUpEnabled(true);
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fillSpinners() {

        List<String> listDays = new ArrayList<String>();

        for(int i = 0; i < mAppCore.getDays().length; i++) {

            listDays.add(mAppCore.getDays()[i]);

        }

        ArrayAdapter<String> dataAdapterFloors = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, listDays);
        dataAdapterFloors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerFirstDay.setAdapter(dataAdapterFloors);
    }

    @Override
    public void onPause() {
        saveSetting();
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void saveSetting() {
        String[] startTime = mButtonStartTime.getText().toString().split(":");
        String[] endTime = mButtonEndTime.getText().toString().split(":");
        mAppState.setScheduleStartMinute(Integer.parseInt(startTime[1]));
        mAppState.setScheduleStartHour(Integer.parseInt(startTime[0]));
        mAppState.setScheduleEndMinute(Integer.parseInt(endTime[1]));
        mAppState.setScheduleEndHour(Integer.parseInt(endTime[0]));
        mAppState.setScheduleFirstDay(DateUtils.getDayIntFromString(mAppCore.getDaysShort()[mSpinnerFirstDay.getSelectedItemPosition()]));
        mAppState.setHorizontalMenu(mCheckBoxHorizontalMenu.isChecked());
        mAppState.setChangesTimer(mCheckBoxChanges.isChecked());
        mAppState.setGeofenceTimer(mCheckBoxGeofence.isChecked());
        updateCore();
    }

    public void updateCore() {

        Intent intentMessage = new Intent("mainBroadcast");
        intentMessage.putExtra("update", true);
        intentMessage.putExtra("message", "update");
        sendBroadcast(intentMessage);
    }


}
