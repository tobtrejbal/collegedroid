package tobous.collegedroid.gui.mainScreen.fragment.schedule;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.app.AlertDialog;
        import android.text.InputType;
        import android.util.Base64;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AbsListView;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.GridLayout;
        import android.widget.HorizontalScrollView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.support.v4.app.Fragment;
        import android.widget.ScrollView;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;

        import java.io.File;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Random;

        import tobous.collegedroid.R;
        import tobous.collegedroid.core.AppCore;
        import tobous.collegedroid.core.utils.AppState;
        import tobous.collegedroid.core.utils.UrlUtils;
        import tobous.collegedroid.functions.asynctask.scheduleActionUpdateInsertGet.ScheduleActionListAsyncTask;
        import tobous.collegedroid.functions.asynctask.scheduleActionUpdateInsertGet.ScheduleActionListAsyncTaskListener;
        import tobous.collegedroid.functions.asynctask.scheduleActionUpdate.ScheduleActionUpdateAsyncTaskListener;
        import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
        import tobous.collegedroid.gui.dialogs.scheduleActionDetail.ScheduleActionDetailDialog;
        import tobous.collegedroid.gui.dialogs.scheduleActionDetail.ScheduleActionDetailDialogListener;
        import tobous.collegedroid.gui.dialogs.scheduleActionList.ScheduleActionListDialog;
        import tobous.collegedroid.gui.dialogs.scheduleActionList.ScheduleActionListDialogListener;
        import tobous.collegedroid.gui.mainScreen.fragment.FragmentListener;
        import tobous.collegedroid.functions.schedule.ScheduleUtils;
        import tobous.collegedroid.utils.XmlManager;

/**
 * Created by Tobous on 5. 11. 2015.
 */
public class ScheduleFragment extends Fragment implements ListView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, FragmentListener, ScheduleActionDetailDialogListener, ScheduleActionListDialogListener, ScheduleActionListAsyncTaskListener, ScheduleActionUpdateAsyncTaskListener {

    AppCore mAppCore;
    AppState mAppState;
    Handler mHandler;
    List<ScheduleAction> mScheduleActionList;

    final static int columnWidth = 2;
    final static int rowHeight = 150;
    final static int daySize = 100;
    final static int marginBottom = 10;

    HorizontalScrollView hScroll;
    ScrollView vScroll;
    Button mButtonEditScheduleList;
    Button mButtonLoadSchedule;
    Runnable mRefreshScreen;
    Spinner mSpinnerWeekType;

    LayoutInflater inflater;

    GridLayout grid_layout;

    boolean started = false;


    public ScheduleFragment() {

        mAppCore = AppCore.getInstance();

        mAppState = AppState.getInstance();

        mHandler = new Handler();

        mRefreshScreen = new Runnable() {
            @Override
            public void run() {
                makeTable();
            }
        };

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_schedule, container, false);

        grid_layout = (GridLayout) V.findViewById(R.id.grid_layout_schedule);

        this.inflater = inflater;

        /*hScroll = (HorizontalScrollView) V.findViewById(R.id.horizontalScroll);
        vScroll = (ScrollView) V.findViewById(R.id.verticalScroll);
        vScroll.setOnTouchListener(new View.OnTouchListener() { //inner scroll listener
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        hScroll.setOnTouchListener(new View.OnTouchListener() { //outer scroll listener
            private float mx
                    ,
                    my
                    ,
                    curX
                    ,
                    curY;
            private boolean started = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                curX = event.getX();
                curY = event.getY();
                int dx = (int) (mx - curX);
                int dy = (int) (my - curY);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (started) {
                            vScroll.scrollBy(0, dy);
                            hScroll.scrollBy(dx, 0);
                        } else {
                            started = true;
                        }
                        mx = curX;
                        my = curY;
                        break;
                    case MotionEvent.ACTION_UP:
                        vScroll.scrollBy(0, dy);
                        hScroll.scrollBy(dx, 0);
                        started = false;
                        break;
                }
                return true;
            }
        });*/

        mButtonEditScheduleList = (Button) V.findViewById(R.id.button_edit_schedule);
        mButtonLoadSchedule = (Button) V.findViewById(R.id.button_load_schedule);
        mSpinnerWeekType = (Spinner) V.findViewById(R.id.spinner_week_type);
        mButtonEditScheduleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createScheduleListDialog();
            }
        });

        mButtonLoadSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();

            }
        });
        mSpinnerWeekType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshScreen();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fillSpinner();
        loadData();

        started = true;

        return V;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void makeTable() {

        grid_layout.removeAllViews();

        int length = ScheduleUtils.getMinutesLength();
        int tableLength = length + daySize;

        int[] dayMax = ScheduleUtils.getDaysMax(mScheduleActionList);
        int[][] actionTable = ScheduleUtils.makeScheduleTable(mScheduleActionList, dayMax);
        generateCsvFile(null, actionTable);
        int rowCount = 1;

        for(int i = 0; i < dayMax.length; i++) {
            rowCount += dayMax[i];
        }

        grid_layout.setRowCount(rowCount);
        grid_layout.setColumnCount(tableLength);
        grid_layout.setUseDefaultMargins(false);

        addRowHour(grid_layout, ScheduleUtils.getStartMinute()
                ,ScheduleUtils.getStartHour(), ScheduleUtils.getEndMinute(),
                ScheduleUtils.getEndHour(), length, 0);
        addDayColumn(grid_layout, 1, actionTable);

        int maxAdded = 0;

        for(int m = 0; m < dayMax.length; m++) {

            for(int i = 0; i < dayMax[m]; i++) {

                //addInvisibleRow(table_layout, tableLength);
                addScheduleRow(grid_layout, 1, i, maxAdded, actionTable);

            }

            maxAdded += dayMax[m];
        }

        int childCount = grid_layout.getChildCount();

        for (int i= 0; i < childCount; i++){
            final CellRelativeLayout cell = (CellRelativeLayout) grid_layout.getChildAt(i);
            cell.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    CharSequence text = "Hello toast!";
                    int duration = Toast.LENGTH_SHORT;
                    int id = (Integer) cell.getScheduleId();
                    if(id >= 0) {
                        createScheduleActionDialog(mScheduleActionList.get(id));
                    }
                }
            });
        }


    }

    public void fillSpinner() {

        List<String> weekTypes = new ArrayList<String>();

        for(int i = 0; i < mAppCore.getWeekTypes().length-1; i++) {

            weekTypes.add(mAppCore.getWeekTypes()[i]);

        }
        weekTypes.add("Dnes");

        ArrayAdapter<String> dataAdapterWeekTypes = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, weekTypes);
        dataAdapterWeekTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerWeekType.setAdapter(dataAdapterWeekTypes);

    }

    public void makeRequestData(final String userName, final String password, final String id) {

        StringRequest req = new StringRequest(Request.Method.GET,
                UrlUtils.getURLScheduleByStudent(id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.v("responsak", "");

                        saveData(XmlManager.parseScheduleActions(response, false, true));

                        Toast toast = Toast.makeText(getContext(), "nahrano", Toast.LENGTH_LONG);
                        toast.show();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast toast = Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG);
                        toast.show();

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                /*params.put("username",userName);
                params.put("password",password);*/
                params.put("Content-Type","text/xml");
                params.put("Accept","text/xml");
                String creds = String.format("%s:%s",userName,password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };

        // add the request object to the queue to be executed
        mAppCore.getRequestQueue().add(req);


    }

    private static void generateCsvFile(File sFileName, int[][]data)
    {

            for(int i = data.length-1; i >= 0; i--) {
                String line = "";
                for(int j = 0; j < data[0].length; j++) {
                    line += (data[i][j] + ";");
                }
                Log.v(line, "kkkkkk");
            }
    }

    @Override
    public void onPause() {

        super.onPause();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onPauseFragment() {


    }

    @Override
    public void onResumeFragment() {


    }

    @Override
    public void refreshFragment() {
        if(started) {
            refreshScreen();
        }
    }

    public void refreshScreen() {
        mScheduleActionList = mAppCore.getScheduleActionList();
        mScheduleActionList = ScheduleUtils.filterByWeekType(mScheduleActionList, mSpinnerWeekType.getSelectedItemPosition());
        mHandler.post(mRefreshScreen);

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void addRowHour(GridLayout gridLayout, int startMinute, int startHour, int endMinute, int endHour, int length, int row) {


        int previousCellEnd = 0;

        previousCellEnd = addEmptyItem(gridLayout, 0, 1, 0, daySize);

        if(startMinute != 0) {
            previousCellEnd = addHourItem(gridLayout, row, previousCellEnd, startMinute, startHour, startMinute);
            startHour = startHour+1;
        }

        int start = startMinute;
        int end = endMinute;
        for(int i = start; i < length-end; i++) {
            if(startMinute % 60 == 0) {
                previousCellEnd = addHourItem(gridLayout, row, previousCellEnd, 60, startHour, 0);
                startHour++;
            }
            startMinute++;
        }

        if(endMinute != 0) {
            previousCellEnd = addHourItem(gridLayout, row, previousCellEnd, endMinute,endHour, endMinute);
        }

    }

    public void addDayColumn(GridLayout gridLayout, int rowStart, int[][] actionTable) {

        int previousCellEnd = rowStart;

        int[] count = ScheduleUtils.countDays(actionTable);
        int added = 0;

        for(int i = 0; i < count.length; i++) {
            previousCellEnd = addDayItem(gridLayout, previousCellEnd, count[i], 0,daySize , actionTable[added][0]);
            added += count[i];

        }





    }

    public void addScheduleRow(GridLayout gridLayout, int rowStart, int i, int maxAdded, int[][] actionTable) {

        String rowResult = "60:";

        int count = 0;
        int previousCellEnd = daySize;
        int id;
        int previousId = actionTable[i+maxAdded][1];
        int addedNumber = 0;
        for (int j = 1; j < actionTable[0].length; j++) {
            id = actionTable[i+maxAdded][j];
            if (id == previousId) {
                count++;
            } else {
                addedNumber++;
                if (previousId < 0) {
                    previousCellEnd = addEmptyItem(gridLayout, i+maxAdded+rowStart, 1 ,previousCellEnd, count);
                    rowResult += previousCellEnd + ":";
                } else {
                    previousCellEnd = addScheduleItem(gridLayout, i+maxAdded+rowStart ,previousCellEnd, count,previousId);
                    rowResult += previousCellEnd + ":";

                }
                count = 1;

            }
            previousId = actionTable[i+maxAdded][j];
        }
        if(addedNumber == 0) {
            if(previousId < 0) {
                previousCellEnd = addEmptyItem(gridLayout, i+maxAdded+rowStart, 1 ,previousCellEnd, count);
                rowResult += previousCellEnd + ":";
            } else {
                previousCellEnd = addScheduleItem(gridLayout, i+maxAdded+rowStart ,previousCellEnd, count,previousId);
                rowResult += previousCellEnd + ":";
            }

        } else {
            if (previousId < 0) {
                previousCellEnd = addEmptyItem(gridLayout, i+maxAdded+rowStart, 1 ,previousCellEnd, count);
                rowResult += previousCellEnd + ":";


            } else {
                previousCellEnd = addScheduleItem(gridLayout, i+maxAdded+rowStart ,previousCellEnd, count,previousId);
                rowResult += previousCellEnd + ":";

            }
        }

    }

    public int addHourItem(GridLayout gridLayout, int rowStart, int columnStart, int columnSpan, int hour, int minute) {

        DecimalFormat twoDigitFormat = new DecimalFormat("00");

        GridLayout.LayoutParams layoutParams = createLayoutParams(rowStart, 1, columnStart, columnSpan);

        int color;

        if(hour % 2 == 1) {
            color = getResources().getColor(R.color.hour_cells_odd_color);
        } else {
            color = getResources().getColor(R.color.hour_cells_even_color);
        }

        CellRelativeLayout item = (CellRelativeLayout) inflater.inflate(R.layout.fragment_schedule_table_item, null, false);
        item.setLayoutParams(layoutParams);
        item.setBackgroundColor(color);
        TextView txt = (TextView) item.findViewById(R.id.fragment_schedule_table_item_txt);
        txt.setText(twoDigitFormat.format(hour)+":"+twoDigitFormat.format(minute));
        txt.setTextColor(getResources().getColor(R.color.primary_text_color));

        gridLayout.addView(item);

        return columnStart+columnSpan;

    }

    public int addScheduleItem(GridLayout gridLayout, int rowStart, int columnStart, int columnSpan, int id) {
        GridLayout.LayoutParams layoutParams = createLayoutParams(rowStart, 1, columnStart, columnSpan);

        Random rnd = new Random();
        int color = Color.parseColor(mAppCore.getColors()[id % mAppCore.getColors().length]);

        CellRelativeLayout item = (CellRelativeLayout) inflater.inflate(R.layout.fragment_schedule_table_item, null, false);
        item.setLayoutParams(layoutParams);
        item.setBackgroundColor(color);
        TextView txt = (TextView) item.findViewById(R.id.fragment_schedule_table_item_txt);
        txt.setText(mScheduleActionList.get(id).getName());
        txt.setTextColor(getResources().getColor(R.color.primary_text_color));
        item.setScheduleId(id);

        gridLayout.addView(item);

        return columnStart+columnSpan;
    }

    public int addDayItem(GridLayout gridLayout, int rowStart, int rowSpan, int columnStart, int columnSpan, int day) {

        GridLayout.LayoutParams layoutParams = createLayoutParams(rowStart, rowSpan, columnStart, columnSpan);

        CellRelativeLayout item = (CellRelativeLayout) inflater.inflate(R.layout.fragment_schedule_table_item, null, false);
        item.setLayoutParams(layoutParams);
        item.setBackgroundColor(getResources().getColor(R.color.day_cells_color));
        TextView txt = (TextView) item.findViewById(R.id.fragment_schedule_table_item_txt);
        txt.setTextColor(getResources().getColor(R.color.primary_text_color));
        txt.setText(mAppCore.getDays()[day-1]);

        gridLayout.addView(item);

        return rowStart+rowSpan;

    }

    public int addEmptyItem(GridLayout gridLayout, int rowStart, int rowEnd, int columnStart, int columnSpan) {

        GridLayout.LayoutParams layoutParams = createLayoutParams(rowStart, rowEnd, columnStart, columnSpan);

        int color = getResources().getColor(R.color.empty_cells);

        CellRelativeLayout item = (CellRelativeLayout) inflater.inflate(R.layout.fragment_schedule_table_item, null, false);
        item.setLayoutParams(layoutParams);
        item.setBackgroundColor(color);
        gridLayout.addView(item);

        return columnStart+columnSpan;

    }

    public GridLayout.LayoutParams createLayoutParams(int rowStart, int rowSpan, int columnSpan, int columnLength) {
        GridLayout.Spec rowSpec = GridLayout.spec(rowStart,rowSpan);
        GridLayout.Spec columnSpec = GridLayout.spec(columnSpan,columnLength);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        layoutParams.width = columnLength*columnWidth;
        layoutParams.height = rowHeight*rowSpan;
        layoutParams.setGravity(ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.bottomMargin = marginBottom;

        return layoutParams;
    }

    public void createScheduleActionDialog(ScheduleAction scheduleAction) {

        Bundle args = new Bundle();
        args.putSerializable("scheduleAction", scheduleAction);
        args.putBoolean("editable", true);
        args.putBoolean("editMode", false);

        Log.v("starting","");
        ScheduleActionDetailDialog newFragment = new ScheduleActionDetailDialog();
        newFragment.setListener(this);
        newFragment.setArguments(args);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "room detail");

    }

    public void createScheduleListDialog() {

        Bundle args = new Bundle();

        ScheduleActionListDialog newFragment = new ScheduleActionListDialog();
        newFragment.setListener(this);
        newFragment.setArguments(args);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "schedule list");

    }

    @Override
    public void refresh() {
        refreshScreen();
    }

    public void loadData() {

        ScheduleActionListAsyncTask scheduleActionAsyncTask = new ScheduleActionListAsyncTask(mAppCore, this);

        scheduleActionAsyncTask.execute(null, mAppState.getUserId());

    }

    public void saveData(List<ScheduleAction> scheduleActionList) {

        ScheduleActionListAsyncTask scheduleActionAsyncTask = new ScheduleActionListAsyncTask(mAppCore, this);;

        scheduleActionAsyncTask.execute(scheduleActionList, mAppState.getUserId());

    }

    @Override
    public void dataUpdated() {
        refreshScreen();
    }

    @Override
    public void scheduleActionUpdated(ScheduleAction scheduleAction) {
        refreshScreen();
    }

    @Override
    public void edited() {
        refreshScreen();
    }

    public void load() {

        Context context = getContext();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(getResources().getString(R.string.load_data_title));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText login = new EditText(context);
        login.setHint(getResources().getString(R.string.load_data_hint_login));
        layout.addView(login);

        final EditText password = new EditText(context);
        password.setHint(getResources().getString(R.string.load_data_hint_password));
        password.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(password);

        final EditText id = new EditText(context);
        id.setHint(getResources().getString(R.string.load_data_hint_stag));
        layout.addView(id);

        alertDialogBuilder.setView(layout);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.load_data_btn_load), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeRequestData(login.getText().toString(),
                                password.getText().toString(),id.getText().toString());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.load_data_btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
}
