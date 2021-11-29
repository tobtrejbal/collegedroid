package tobous.collegedroid.gui.activities.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import tobous.collegedroid.R;
import tobous.collegedroid.core.AppCore;

/**
 * Created by Tob on 12. 1. 2016.
 */
public class AboutActivity extends AppCompatActivity {

    private final AppCore mAppCore;

    TextView txtVersion;

    public AboutActivity() {

        mAppCore = AppCore.getInstance();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // get the action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.menu_title_about);

        txtVersion = (TextView) findViewById(R.id.about_txt_version);

        String version = "version ";

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version += pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        txtVersion.setText(version);

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

    /*public void compare() {

        ArrayList<Integer> integers = new ArrayList<Integer>(10000);

        ArrayList<String> strings = new ArrayList<String>(10000);

        int b = 50000000;

        Integer[] arrayInt = {50000000, 50000001, 50000002, 50000003, 50000004, 50000005, 50000006, 50000007, 50000008, 50000000};
        String[] arrayStr = {"50000000", "50000001", "50000002", "50000003", "50000004", "50000005", "50000006", "50000007", "50000008", "50000000"};

        for(int i = 0; i< 100000; i++) {

            b++;

            integers.add(b);
            strings.add(String.valueOf(b));

        }

        long startTime = System.currentTimeMillis();

        for (Integer ich : integers) {

            eventLoop:
            for (Integer ich2 : arrayInt) {

                if (ich == ich2) {

                    break eventLoop;

                }
            }
        }

        Log.v("duration of filter integer: ", (System.currentTimeMillis() - startTime)+"ms");


        startTime = System.currentTimeMillis();

        for (String ich : strings) {

            eventLoop:
            for (String ich2 : arrayStr) {

                if (ich == ich2) {

                    break eventLoop;

                }
            }
        }

        Log.v("duration of filter String: ", (System.currentTimeMillis() - startTime)+"ms");

    }*/

}