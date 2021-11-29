package tobous.collegedroid.gui.mainScreen.fragment.freeclassrooms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.core.utils.UrlUtils;
import tobous.collegedroid.functions.graphs.GraphUtils;
import tobous.collegedroid.functions.graphs.encapsulation.Edge;
import tobous.collegedroid.functions.graphs.encapsulation.Graph;
import tobous.collegedroid.functions.graphs.encapsulation.Vertex;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.functions.plans.encapsulation.Room;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;
import tobous.collegedroid.gui.dialogs.navigation.NavigationDialog;
import tobous.collegedroid.gui.dialogs.navigation.NavigationDialogListener;
import tobous.collegedroid.gui.dialogs.roomdetail.RoomDetailDialog;
import tobous.collegedroid.gui.dialogs.roomdetail.RoomDetailDialogListener;
import tobous.collegedroid.gui.dialogs.roomMapParameters.RoomMapParametersDialog;
import tobous.collegedroid.gui.dialogs.roomMapParameters.RoomMapParametersDialogListener;
import tobous.collegedroid.gui.dialogs.scheduleActionDetail.ScheduleActionDetailDialog;
import tobous.collegedroid.gui.mainScreen.fragment.FragmentListener;
import tobous.collegedroid.utils.DateUtils;
import tobous.collegedroid.utils.ImageLoader;
import tobous.collegedroid.utils.XmlManager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Tobous on 9. 11. 2015.
 */
public class ClassRoomMap extends Fragment implements ListView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, FragmentListener, View.OnTouchListener, RoomDetailDialogListener, NavigationDialogListener, RoomMapParametersDialogListener {

        AppCore mAppCore;
        AppState mAppState;
        Handler mHandler;
        Building mSelectedBuilding;
        int mSelectedFloor = 0;
        Bitmap currentFloor;
        List<List<Vertex>> path;
        Button mButtonClearPath;

        PhotoView mPlan;

        PhotoViewAttacher mPhotoViewAttacher;

        private Runnable mRefreshScreen;
        private Spinner mSpinnerBuildings;
        private Spinner mSpinnerFloors;
        private Button mButtonParameters;
        Canvas mCanvas;
        Bitmap bmp;
        Bitmap alteredBitmap;
        Paint paint;
        Matrix matrix;

        boolean started = false;

        LayoutInflater inflater;

        public ClassRoomMap() {

            mAppCore = AppCore.getInstance();

            mAppState = AppState.getInstance();

            mHandler = new Handler();

            mRefreshScreen = new Runnable() {
                @Override
                public void run() {
                    mButtonParameters.setText(getResources().getString(R.string.building_label)+" "
                            +mAppState.getSelectedBuilding().getName()+","+mAppState.getSelectedDateString());
                    paintFloor();
                }
            };

            mSelectedBuilding = mAppCore.getBuildings().get(0);

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

        View V = inflater.inflate(R.layout.fragment_class_room_map, container, false);

        this.inflater = inflater;

        mPlan = (PhotoView) V.findViewById(R.id.planView);

        mPlan.setOnTouchListener(this);

        //alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
         //           .getHeight(), bmp.getConfig());
        //canvas = new Canvas(alteredBitmap);
        //paint = new Paint();

        mButtonParameters = (Button) V.findViewById(R.id.btn_parameters);
        mButtonClearPath = (Button) V.findViewById(R.id.btn_clear_nav);
        //mSpinnerBuildings = (Spinner) V.findViewById(R.id.spinner_building);
        mSpinnerFloors = (Spinner) V.findViewById(R.id.spinner_floor);

        mSpinnerFloors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mSelectedFloor = position;
                    mHandler.post(mRefreshScreen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        /*mSpinnerBuildings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!mSelectedBuilding.getName().equals(mSpinnerBuildings.getSelectedItem())) {
                        for (Building b : mAppCore.getBuildings()) {
                            if (b.getName().equals(mSpinnerBuildings.getSelectedItem())) {
                                mSelectedBuilding = b;
                                mSelectedFloor = 1;
                                String[] date = DateUtils.getDateAsStrings(mSelectedDate);
                                makeRequestData(date[0], date[1], date[2], mSelectedBuilding.getName());
                                mSpinnerFloors.setSelection(0);
                            }
                        }
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        mButtonParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createParameterDialog();
                //paintPath(mAppCore.getmSelectedBuilding().getFloorGraphs().get(mAppCore.getmSelectedFloor() - 1).getVertexes());
                // makeRequestData(mAppCore.getSelectedYear(), mAppCore.getSelectedSemester(), mAppCore.getSelectedDay(), mAppCore.getmSelectedBuilding().getName());
            }
        });

        mButtonClearPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = null;
                refreshScreen();
            }
        });

        mPhotoViewAttacher = new PhotoViewAttacher(mPlan);

        mPhotoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                /*String text = "x: " + x + ",y: " + y + ",sirka : " + mPlan.getWidth();
                Toast toast = Toast.makeText(mAppCore, text, Toast.LENGTH_SHORT);
                toast.show();*/
                selectRoom(x,y);

            }
        });

        fillSpinners();

        refreshScreen();

        started = true;

        makeRequestData(mAppState.getSelectedYear(), mAppState.getSelectedSemester(), mAppState.getSelectedDay(), mAppState.getSelectedBuilding().getName());


        return V;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void makeRequestData(final String year, final String semester, final String day, final String building) {
        Log.v("y:" + year + "s:"+semester + "d:" + day + "b:"+building, "lali");
        StringRequest req = new StringRequest(Request.Method.GET,
                UrlUtils.getURLScheduleActions(year, semester, day, building),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.v(UrlUtils.getURLScheduleActions(year, semester, day, building), "lali");

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 10);
                        calendar.set(Calendar.MINUTE, 0);
                        XmlManager.parseScheduleActions(response,true, false);

                        refreshScreen();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.v("erus","");

                    }
                }) {


        };

        // add the request object to the queue to be executed
        mAppCore.getRequestQueue().add(req);


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


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void paintFloor() {

        Paint p=new Paint();
        p.setColor(Color.RED);

        try {
            this.currentFloor = ImageLoader.loadImage(mSelectedBuilding, this.mSelectedFloor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap mutableBitmap = currentFloor.copy(Bitmap.Config.ARGB_8888, true);

        mCanvas = new Canvas(mutableBitmap);
        List<Room> rooms = mAppState.getSelectedBuilding().getRooms();

        for(int i = 0; i<rooms.size();i++) {

            if(rooms.get(i).getFloor() == this.mSelectedFloor) {
                boolean full = false;

                Room room = rooms.get(i);

                if(room.getActionList() != null)  {
                    for(int j = 0; j< room.getActionList().size(); j++) {
                        ScheduleAction scheduleAction = room.getActionList().get(j);
                        Log.v(scheduleAction.isInWeek(mAppState.getSelectedDate().get(Calendar.WEEK_OF_YEAR))+"","sdada");
                        /*Log.v("timeeeeeSelected:","day:"+dateTimeSelected.getDayOfWeek()+" hod:" + dateTimeSelected.getHourOfDay() + " min:" + dateTimeSelected.getMinuteOfHour() + " uč:"+scheduleAction.getName()+" ucebna:"+room.getName());
                        Log.v("timeeeeeStart:","day:"+dateTimeStart.getDayOfWeek()+" hod:" + dateTimeStart.getHourOfDay() + " min:" + dateTimeStart.getMinuteOfHour() + " uč:"+scheduleAction.getName()+" ucebna:"+room.getName());
                        Log.v("timeeeeeEnd:","day:"+dateTimeEnd.getDayOfWeek()+" hod:" + dateTimeEnd.getHourOfDay() + " min:" + dateTimeEnd.getMinuteOfHour() + " uč:"+scheduleAction.getName()+" ucebna:"+room.getName());*/
                        if(DateUtils.isActionDuringDate(scheduleAction, mAppState.getSelectedDate()) && scheduleAction.isInWeek(mAppState.getSelectedDate().get(Calendar.WEEK_OF_YEAR))) {
                            //Log.v("TRUEEE","");
                            full = true;
                        }

                        //Log.v("______________________________________________________________","");
                    }
                }

                Vertex vertex = GraphUtils.getVertexFromRoom(room);

                if(full) {
                    int[] coord = coordinates(vertex.getCoordX(), vertex.getCoordY(), mAppCore.getFullRoom());
                    mCanvas.drawBitmap(mAppCore.getFullRoom(), coord[0], coord[1], p);
                } else {
                    int[] coord = coordinates(vertex.getCoordX(), vertex.getCoordY(), mAppCore.getEmptyRoom());
                    mCanvas.drawBitmap(mAppCore.getEmptyRoom(), coord[0], coord[1], p);
                }
            }
        }


        for(Vertex vertex : mSelectedBuilding.getFloorGraphs().get(this.mSelectedFloor).getVertexes()) {
            if(vertex.getType()==2) {
                int[] coord = coordinates(vertex.getCoordX(), vertex.getCoordY(), mAppCore.getStairs());
                mCanvas.drawBitmap(mAppCore.getStairs(), coord[0], coord[1],p);
            }
        }

        p.setStrokeWidth(10);

        if(path != null) {
            List<Vertex> currentPath = path.get(this.mSelectedFloor);
            if(currentPath != null) {
                //Log.v("paaaath SIZE " + currentPath.size(),"");
                for(int i = 1; i < currentPath.size(); i++) {
                        Vertex current = currentPath.get(i);
                        Vertex previous = currentPath.get(i-1);
                        //Log.v("first X: "+ current.getCoordX()+ "first Y" +current.getCoordY()+ "second X :"+ previous.getCoordX()+ "second Y: "+ previous.getCoordY(),"");
                        mCanvas.drawLine(current.getCoordX(), current.getCoordY(), previous.getCoordX(), previous.getCoordY(), p);

                }
            }

        }

        mPlan.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));

        //paintPath();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {

        }
        return true;
    }

    public void fillSpinners() {

       /* List<String> listBuildings = new ArrayList<String>();

        for(Building building : mAppCore.getBuildings()) {

            if(building.getFloorGraphs() != null) {

                listBuildings.add(building.getName());

            }

        }*/

        List<String> listFloors = new ArrayList<String>();

        for(int i = 0; i < mAppState.getSelectedBuilding().getFloorCount(); i++) {

            listFloors.add((i + 1) + ". " + getResources().getString(R.string.floor_label));

        }

        /*ArrayAdapter<String> dataAdapterBuildings = new ArrayAdapter<String>(mAppCore,
                android.R.layout.simple_spinner_item, listBuildings);
        dataAdapterBuildings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBuildings.setAdapter(dataAdapterBuildings);*/

        ArrayAdapter<String> dataAdapterFloors = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, listFloors);
        dataAdapterFloors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerFloors.setAdapter(dataAdapterFloors);

    }



    public void paintPath() {

        try {
            this.currentFloor = ImageLoader.loadImage(mSelectedBuilding, this.mSelectedFloor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap mutableBitmap = currentFloor.copy(Bitmap.Config.ARGB_8888, true);

        mCanvas = new Canvas(mutableBitmap);
        List<Room> rooms = mAppState.getSelectedBuilding().getRooms();

        Graph graph = mAppState.getSelectedBuilding().getFloorGraphs().get(mSelectedFloor);

        Log.v("size:"+graph.getVertexes().size(),"");

        for(int i = 0; i<graph.getVertexes().size();i++) {

            Vertex vertex = graph.getVertexes().get(i);

            Paint p=new Paint();
            p.setColor(Color.RED);

            //Log.v("painting:"+vertex.getId()+":"+vertex.getCoordX()+":"+vertex.getCoordY(),"");

            mCanvas.drawText(vertex.getId(), vertex.getCoordX(), vertex.getCoordY(), p);

        }

        List<Edge> edges = mAppState.getSelectedBuilding().getFloorGraphs().get(this.mSelectedFloor).getEdges();

        for(int i = 0; i<edges.size();i++) {

            Edge edge = edges.get(i);

            Paint p=new Paint();
            p.setColor(Color.RED);

            mCanvas.drawLine(edge.getSource().getCoordX(), edge.getSource().getCoordY(), edge.getDestination().getCoordX(), edge.getDestination().getCoordY(), p);
            mCanvas.drawText(edge.getSource().getId(), edge.getSource().getCoordX(), edge.getSource().getCoordY(), p);
        }

        mPlan.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));


    }

    public void findWay(Vertex origin, Vertex target) {

        //origin = mAppState.getSelectedBuilding().getFloorGraphs().get(2).getVertexes().get(0);
        this.path = GraphUtils.findPath(mAppState.getSelectedBuilding().getFloorGraphs(), origin, target);

    }

    public void selectRoom(float x, float y) {


        Building building = mAppState.getSelectedBuilding();

        /*String text2 = "width: "  + building.getWidths()[mAppCore.getmSelectedFloor()-1] + " height: " + building.getHeights()[mAppCore.getmSelectedFloor()-1] +"\n" + "x: " + x + " y: " + y;
        Toast toast = Toast.makeText(mAppCore, text2, Toast.LENGTH_SHORT);
        toast.show();*/

        Graph graph = building.getFloorGraphs().get(mSelectedFloor);



        for(Vertex vertex : graph.getVertexes()) {

            if (vertex.getType() == 1) {

                float coordX = (float) vertex.getCoordX() / (float) building.getWidths()[this.mSelectedFloor];
                float coordY = (float) vertex.getCoordY() / (float) building.getHeights()[this.mSelectedFloor];

              /*String text2 = "touch X:"  + x + " room X: " + coordX + " touch y: " + y + " room Y: " + coordY;
                Toast toast = Toast.makeText(mAppCore, text2, Toast.LENGTH_SHORT);
                toast.show();*/
                if (((coordX < (x + 0.05)) && (coordX > (x - 0.05)))
                        &&
                        ((coordY < (y + 0.05)) && (coordY > (y - 0.05)))) {
                    createRoomDialog(GraphUtils.getRoomFromVertex(vertex));
                }

            }
        }

    }

    public void createRoomDialog(final Room room) {

        Bundle args = new Bundle();
        args.putSerializable("room", room);

        RoomDetailDialog newFragment = new RoomDetailDialog();
        newFragment.setListener(this);
        newFragment.setArguments(args);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "room detail");

    }

    public void createScheduleActionDialog(ScheduleAction scheduleAction) {

        Bundle args = new Bundle();
        args.putSerializable("scheduleAction", scheduleAction);
        args.putBoolean("editMode", false);
        args.putBoolean("editable", false);

        DialogFragment newFragment = new ScheduleActionDetailDialog();
        newFragment.setArguments(args);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "room detail");

    }

    public void createNavigationDialog(final Room room, final Building building) {

        Bundle args = new Bundle();
        args.putSerializable("room", room);
        args.putSerializable("building", building);

        NavigationDialog newFragment = new NavigationDialog();
        newFragment.setListener(this);
        newFragment.setArguments(args);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "room detail");

    }

    public void createParameterDialog() {

        Bundle args = new Bundle();

        RoomMapParametersDialog newFragment = new RoomMapParametersDialog();
        newFragment.setArguments(args);
        newFragment.setListener(this);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "date picker");

    }

    @Override
    public void cancel() {

    }

    @Override
    public void itemClicked(ScheduleAction scheduleAction) {
        createScheduleActionDialog(scheduleAction);
    }

    @Override
    public void startNavigation(Room room) {
        createNavigationDialog(room, mSelectedBuilding);
    }

    @Override
    public void navigate(Building building, Vertex source, Vertex target) {
        findWay(source, target);
        if(mSelectedFloor == source.getGraphId()) {
            refreshScreen();
        } else {
            mSpinnerFloors.setSelection(source.getGraphId());
        }
    }

    public void refreshScreen() {

        mHandler.post(mRefreshScreen);

    }

    @Override
    public void parametersSaved() {
        mSpinnerFloors.setSelection(0);
        makeRequestData(mAppState.getSelectedYear(), mAppState.getSelectedSemester(), mAppState.getSelectedDay(), mAppState.getSelectedBuilding().getName());
    }

    public int[] coordinates(int x, int y, Bitmap bitmap) {

        x -= bitmap.getWidth()/2;
        y -= bitmap.getWidth()/2;

        if((x < 0) || (y < 0)) {
            return new int[] {0,0};
        } else {
            return new int[] {x,y};
        }

    }

}
