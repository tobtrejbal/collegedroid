package tobous.collegedroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.functions.plans.encapsulation.Room;

/**
 * Created by Tob on 22. 12. 2015.
 */
public class ImageLoader {

    public static List<Bitmap> loadImages(Building building) throws IOException {

        AppCore mAppCore = AppCore.getInstance();

        List<Bitmap> floorMaps = new ArrayList<Bitmap>();
        for(int j = 0; j<building.getFloorCount();j++) {
            //Log.v("nahravam","");
            InputStream ims = mAppCore.getAssets().open(building.getFloorMaps().get(j));
            // load image as Drawable
            //Log.v("nahrano","");
            Bitmap bitmap = null;
            //BitmapFactory is an Android graphics utility for images
            bitmap = BitmapFactory.decodeStream(ims);

            floorMaps.add(bitmap);

        }

        return floorMaps;

    }

    public static Bitmap loadImage(Building building, int floor) throws IOException {

        AppCore mAppCore = AppCore.getInstance();

            //Log.v("nahravam","");
        InputStream ims = mAppCore.getAssets().open(building.getFloorMaps().get(floor));
            // load image as Drawable
            //Log.v("nahrano","");
        Bitmap bitmap = null;
            //BitmapFactory is an Android graphics utility for images
        bitmap = BitmapFactory.decodeStream(ims);

        return bitmap;

    }

}
