package tobous.collegedroid.gui.mainScreen.fragment.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.gui.mainScreen.fragment.FragmentListener;

/**
 * Created by Tobous on 4. 2. 2015.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, FragmentListener {

    private final int mRefreshTime = 10;
    Context mContext;
    GoogleMap mMap;
    SupportMapFragment mMapFragment;
    Button mBtnCenterPosition;
    Button mBtnCenterCity;
    Building mSelectedBuilding;
    Handler mHandler;
    Runnable mRunnableCenterMap;
    Runnable mRunnableRefresh;
    Runnable mRunnableRefreshPeriodically;
    private final AppCore mAppCore;
    private AppState mAppState;
    RelativeLayout mLayoutDetail;
    TextView mTxtBuilding;
    ImageView mImgBuilding;
    Button mBtnNavigate;

    public MapFragment() {

        mAppCore = AppCore.getInstance();

        mHandler = new Handler();

        mAppState = AppState.getInstance();

        mRunnableRefresh = new Runnable() {
            @Override
            public void run() {
                drawMarkers();
            }
        };

        mRunnableRefreshPeriodically = new Runnable() {
            @Override
            public void run() {
                drawMarkers();
                mHandler.postDelayed(mRunnableRefreshPeriodically, mRefreshTime * 1000);
            }
        };

    }


    public void centerMap(final LatLng latLng) {

            if(mMap != null) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    }
                });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Log.v(this.getClass().getName(), "on attach fragment");

        mContext = activity;

    }

    @Override
    public void onResume() {
        mHandler.post(mRunnableRefreshPeriodically);
        super.onResume();

        //Log.v(this.getClass().getName(), "fragment resumed");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_map, container, false);

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_layout_google_map, mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(MapFragment.this);

        mLayoutDetail = (RelativeLayout) V.findViewById(R.id.map_layout_detail);

        mLayoutDetail.setBackgroundColor(0);

        mLayoutDetail.setVisibility(View.INVISIBLE);

        mTxtBuilding = (TextView) V.findViewById(R.id.map_fragment_txt_building);
        mImgBuilding = (ImageView) V.findViewById(R.id.map_fragment_img_building);
        mBtnNavigate = (Button) V.findViewById(R.id.map_fragment_btn_nav);

        mBtnCenterPosition = (Button) V.findViewById(R.id.button_center_position);
        mBtnCenterCity = (Button) V.findViewById(R.id.button_center_school_city);

        mBtnCenterPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMap(new LatLng(mAppState.getLat(), mAppState.getLon()));
            }
        });

        mBtnCenterCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMap(new LatLng(mAppState.getCityLat(), mAppState.getCityLon()));
            }
        });

        mBtnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate();
            }
        });

        mLayoutDetail.setOnTouchListener(new SwipeListener(mContext) {

            public void onDouble() {

                /*Intent intent = new Intent(mContext, EventDetailActivity.class);

                intent.putExtra(Intents.INTENT_EXTRAS_EVENT_DETAIL_OBJECT+"", mSelectedEvent);

                startActivity(intent);*/


            }

            public void onSwipeTop() {


            }

            public void onSwipeRight() {

                hideDetail();
            }

            public void onSwipeLeft() {

                Animation slide = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.map_detail_slide_right);

                mLayoutDetail.startAnimation(slide);

                slide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Called when the Animation starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Called when the Animation ended
                        // Since we are fading a View out we set the visibility
                        // to GONE once the Animation is finished
                        mLayoutDetail.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // This is called each time the Animation repeats
                    }
                });

            }

            public void onSwipeBottom() {
            }

        });

        return V;
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mRunnableRefreshPeriodically);
        super.onPause();

        //Log.v(this.getClass().getName(), "fragment paused");

    }

    @Override
    public void onDetach() {
        super.onDetach();


    }

    public void drawMarkers() {

        mMap.clear();

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(mAppState.getLat(), mAppState.getLon()))
                .radius(5);

        Circle circle = mMap.addCircle(circleOptions);
        circle.setStrokeColor(mContext.getResources().getColor(R.color.day_cells_color));

           MarkerOptions marker = new MarkerOptions();
           marker.position(new LatLng(mAppState.getLat(), mAppState.getLon()));
           marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.abc_ab_share_pack_mtrl_alpha));

        mMap.addMarker(marker);

                for(Building b : mAppCore.getBuildings()) {

                    marker = new MarkerOptions();
                    marker.title(b.getName());
                    marker.position(new LatLng(b.getLat(), b.getLon()));

                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_app));

                    mMap.addMarker(marker);

                }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //if(!animating) {

        showMarkerDetails(marker);

      /*      if (previousMarker != null) {
                if (previousMarker.equals(marker)) {
                    hideDetail();
                } else {
                    showMarkerDetails(marker);
                }
            } else {
                showMarkerDetails(marker);
            }*/

        //}

        return true;
    }

    public void navigate() {

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +
                mSelectedBuilding.getLat() + "," +
                mSelectedBuilding.getLon()));
        startActivity(i);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        Log.v(this.getClass().getName(), "mapaReady");

        if(mAppState.getLat() == 0.0 && mAppState.getLon() == 0.0) {
            centerMap(new LatLng(mAppState.getCityLat(),mAppState.getCityLon()));
        } else {
            centerMap(new LatLng(mAppState.getLat(), mAppState.getLon()));
        }
        //Log.v(this.getClass().getName(), "mapaReady");

        refreshScreen();

    }

    public void showMarkerDetails(Marker marker) {

        for(int i = 0; i < mAppCore.getBuildings().size(); i++) {

            Building building = mAppCore.getBuildings().get(i);

            if(building.getName().equals(marker.getTitle())) {
                mSelectedBuilding = building;
                //navigate();
            }

        }

        showDetailBuilding();

    }

    public void showDetailBuilding() {

        if(mSelectedBuilding != null) {

            mTxtBuilding.setText("Budova " + mSelectedBuilding.getName());

            try {

                InputStream ims = getActivity().getAssets().open(mSelectedBuilding.getPicture());
                mImgBuilding.setImageDrawable(new BitmapDrawable(getResources(),BitmapFactory.decodeStream(ims)));

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mLayoutDetail.getVisibility() != View.VISIBLE) {

                Animation slide = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.map_detail_slide_left);

                mLayoutDetail.startAnimation(slide);

                mLayoutDetail.setBackgroundColor(getResources().getColor(R.color.primary_window_background));

                slide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Called when the Animation starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Called when the Animation ended
                        // Since we are fading a View out we set the visibility
                        // to GONE once the Animation is finished
                        mLayoutDetail.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // This is called each time the Animation repeats
                    }
                });

            }
        }

    }

    public void hideDetail() {

        Animation mSlide = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.map_detail_slide_right);

        mLayoutDetail.startAnimation(mSlide);

        mSlide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Called when the Animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Called when the Animation ended
                // Since we are fading a View out we set the visibility
                // to GONE once the Animation is finished
                mLayoutDetail.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // This is called each time the Animation repeats
            }
        });

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

    public void refreshScreen() {

        mHandler.post(mRunnableRefresh);

    }

}
