package tobous.collegedroid.gui.mainScreen.fragment.changes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.asynctask.changesList.ChangesAsyncTask;
import tobous.collegedroid.functions.asynctask.changesList.ChangesAsyncTaskListener;
import tobous.collegedroid.gui.dialogs.changes.ChangesDialog;
import tobous.collegedroid.gui.mainScreen.fragment.FragmentListener;
import tobous.collegedroid.functions.changes.encapsulation.Change;
import tobous.collegedroid.gui.mainScreen.fragment.changes.adapter.ChangesAdapter_Recycle;
import tobous.collegedroid.gui.utils.RecyclerAdapterListener;
import tobous.collegedroid.gui.utils.RecyclerItemTouchListener;

/**
 * Created by Ondra on 31. 1. 2016.
 */
public class Changes extends Fragment implements RecyclerItemTouchListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, FragmentListener, ChangesAsyncTaskListener {

    AppCore mAppCore;
    AppState mAppState;
    Handler mHandler;
    RecyclerAdapterListener mRecyclerAdapterListener;

    private Runnable mRefreshScreen;

    LayoutInflater inflater;

    Button mBtnChangeFaculty;

    //ListView mListViewChanges;
    private RecyclerView mRecyclerView;

    //ChangesAdapter changesAdapter;
    ChangesAdapter_Recycle changesAdapter;

    List<Change> changes;

    public Changes() {

        mAppCore = AppCore.getInstance();

        mAppState = AppState.getInstance();

        mHandler = new Handler();

        mRecyclerAdapterListener = new RecyclerAdapterListener(getContext(), this);

        mRefreshScreen = new Runnable() {
            @Override
            public void run() {
                //changesAdapter.clear();
                changesAdapter.setChanges(mAppCore.getChangeList());
                changesAdapter.notifyDataSetChanged();
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

        View V = inflater.inflate(R.layout.fragment_changes, container, false);

        //this.inflater = inflater;
        //mListViewChanges = (ListView) V.findViewById(R.id.fragment_changes_list);

        mRecyclerView = (RecyclerView) V.findViewById(R.id.fragment_changes_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //mBtnChangeFaculty = (Button) V.findViewById(R.id.fragment_changes_btn_faculty);

        changesAdapter = new ChangesAdapter_Recycle(getContext());
        mRecyclerView.setAdapter(changesAdapter);


        mRecyclerView.addOnItemTouchListener(mRecyclerAdapterListener);


        loadData();
        return V;
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

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void refreshScreen() {
        mHandler.post(mRefreshScreen);
    }

    public void loadData() {
        ChangesAsyncTask task = new ChangesAsyncTask(this);
        task.execute();

    }

    @Override
    public void updateChangesList() {
        refreshScreen();
    }

    public void createChangesDialog(final Change change) {

        Bundle args = new Bundle();
        args.putSerializable("change", change);

        ChangesDialog changesDialog = new ChangesDialog();
        changesDialog.setArguments(args);
        changesDialog.show(getActivity().getSupportFragmentManager(), "room detail");

    }

    @Override
    public void onLongTouch(int position) {
        createChangesDialog(mAppCore.getChangeList().get(position));
    }

    @Override
    public void onShortTouch(int position) {
        createChangesDialog(mAppCore.getChangeList().get(position));
    }

    /*public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }*/

}
