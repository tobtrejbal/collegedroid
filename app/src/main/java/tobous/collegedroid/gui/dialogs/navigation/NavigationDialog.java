package tobous.collegedroid.gui.dialogs.navigation;

import android.app.ActionBar;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.graphs.GraphUtils;
import tobous.collegedroid.functions.graphs.encapsulation.Vertex;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.functions.plans.encapsulation.Room;

/**
 * Created by Tob on 23. 12. 2015.
 */
public class NavigationDialog  extends DialogFragment {

    NavigationDialogListener listener;

    public void setListener(NavigationDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final AppCore mAppCore = AppCore.getInstance();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_navigate, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogView);

        final Spinner spinnerSource = (Spinner) dialogView.findViewById(R.id.navigate_detail_spin_source);
        final Spinner spinnerTarget = (Spinner) dialogView.findViewById(R.id.navigate_detail_spin_target);
        final Spinner spinnerBuilding = (Spinner) dialogView.findViewById(R.id.navigate_detail_spin_building);

        fillSpinnerBuilding(mAppCore, spinnerBuilding);

        Button btnCancel = (Button) dialogView.findViewById(R.id.navigate_detail_btn_cancel);
        // if button is clicked, close the custom
        Button btnNavigate = (Button) dialogView.findViewById(R.id.navigate_detail_btn_navigate);
        // if button is clicked, close the custom dialog

        final Room room = (Room) getArguments().getSerializable("room");

        final Building building = (Building) getArguments().getSerializable("building");

        if(building != null) {

            fillSpinnerVertex(mAppCore, spinnerSource, building);
            fillSpinnerVertex(mAppCore, spinnerTarget, building);

        } else {

            fillSpinnerVertex(mAppCore, spinnerSource, mAppCore.getBuildings().get(0));
            fillSpinnerVertex(mAppCore, spinnerTarget, mAppCore.getBuildings().get(0));

        }

        final Dialog dialog = builder.create();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, 800);

        if(building != null) {
            for(int i = 0; i < spinnerBuilding.getAdapter().getCount();i++) {
                if(building.getName().equals(spinnerBuilding.getAdapter().getItem(i))) {
                    spinnerBuilding.setSelection(i);
                }
            }
        } else {

        }

        // if button is clicked, close the custom dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sourceName = (String) spinnerSource.getSelectedItem();
                String targetName = (String) spinnerTarget.getSelectedItem();
                if(sourceName == "vchod") {
                    sourceName = "Entrance";
                }
                if(targetName == "vchod") {
                    targetName = "Entrance";
                }
                if(sourceName == targetName) {
                    Toast toast = Toast.makeText(getContext(), "Místo počátku nesmí odpovídat cílovému", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Building building = mAppCore.getBuildings().get(spinnerBuilding.getSelectedItemPosition());
                    Vertex source = GraphUtils.getVertexByName(building, sourceName);
                    Vertex target = GraphUtils.getVertexByName(building, targetName);
                    listener.navigate(building, source, target);
                    dialog.dismiss();
                }
            }
        });

        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count = 0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(count > 0) {
                    fillSpinnerVertex(mAppCore, spinnerSource, mAppCore.getBuildings().get(position));
                    fillSpinnerVertex(mAppCore, spinnerTarget, mAppCore.getBuildings().get(position));
                    Log.v("just hapenned","");
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(room != null) {

            //Log.v("haha", "");

            for(int i = 0; i < spinnerTarget.getAdapter().getCount();i++) {
                //Log.v("i:"+i,"");
                if(room.getName().equals(spinnerTarget.getAdapter().getItem(i))) {
                    spinnerTarget.setSelection(i, true);
                    Log.v("true","");
                }
            }

        }

        return dialog;
    }

    public void fillSpinnerBuilding(AppCore mAppCore, Spinner spinner) {

        List<String> listBuildings = new ArrayList<String>();

        for(Building building : mAppCore.getBuildings()) {

            if(building.getFloorGraphs() != null) {

                listBuildings.add(building.getName());

            }

        }


        ArrayAdapter<String> dataAdapterBuildings = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, listBuildings);
        dataAdapterBuildings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapterBuildings);

    }

    public void fillSpinnerVertex(AppCore mAppCore, Spinner spinner, Building building) {

        List<String> rooms = new ArrayList<String>();

        rooms.add("vchod");

        for(Room room : building.getRooms()) {

            rooms.add(room.getName());

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mAppCore,
                R.layout.spinner_item, rooms);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }

    public Room getRoomByName(Building building, String name) {

        for (Room room : building.getRooms()) {

            if (room.getName().equals(name)) {

                return room;

            }

        }

        return null;

    }

}
