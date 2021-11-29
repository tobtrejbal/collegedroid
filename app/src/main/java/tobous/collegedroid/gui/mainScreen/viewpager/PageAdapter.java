package tobous.collegedroid.gui.mainScreen.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import tobous.collegedroid.gui.mainScreen.fragment.changes.Changes;
import tobous.collegedroid.gui.mainScreen.fragment.freeclassrooms.ClassRoomMap;
import tobous.collegedroid.gui.mainScreen.fragment.map.MapFragment;
import tobous.collegedroid.gui.mainScreen.fragment.schedule.ScheduleFragment;

/**
 * Created by Tobous on 5. 2. 2015.
 */
public class PageAdapter extends FragmentPagerAdapter {

    String[] names;

    ScheduleFragment mScheduleFragment;
    ClassRoomMap mClassRoomMap;
    MapFragment mMapFragment;
    Changes mChanges;

    /*Fragment categoriesFragment;
    Fragment mapFragment;
    Fragment closestFragment;
    Fragment recommendedFragment;
    Activity activity;*/

        public PageAdapter(FragmentManager fm, String[] names) {//, Activity activity) {
        super(fm);
        this.names = names;
        //this.activity = activity;

    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                if (mScheduleFragment == null) {
                    mScheduleFragment = new ScheduleFragment();
                    Log.v("returning", "fragment " + index);
                    return mScheduleFragment;
                } else {
                    Log.v("returning", "fragment " + index);
                    return mScheduleFragment;
                }
            case 1:
                if (mClassRoomMap == null) {
                    mClassRoomMap = new ClassRoomMap();
                    Log.v("returning", "fragment " + index);
                    return mClassRoomMap;
                } else {
                    Log.v("returning", "fragment " + index);
                    return mClassRoomMap;
                }
            case 2:
                if (mMapFragment == null) {
                    mMapFragment = new MapFragment();
                    return mMapFragment;
                } else {
                    Log.v("returning", "fragment " + index);
                    return mMapFragment;
                }

            case 3:
                if (mChanges == null) {
                    mChanges = new Changes();
                    Log.v("returning", "fragment " + index);
                    return mChanges;
                } else {
                    Log.v("returning", "fragment " + index);
                    return mChanges;
                }
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position) {
            case 0:
                // Top Rated fragment activity
                return names[0];
            case 1:
                // Games fragment activity
                return names[1];
            case 2:
                // Movies fragment activity
                return names[2];

            case 3:
                // Movies fragment activity
                return names[3];
        }

        return null;
    }

}
