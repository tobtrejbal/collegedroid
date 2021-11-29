package tobous.collegedroid.functions.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tob on 11. 1. 2016.
 */
public class DatabaseCore extends SQLiteOpenHelper {

    //private final Logger log = LoggerFactory.getLogger(DatabaseCore.class);

    private static DatabaseCore sInstance;

    private SQLiteDatabase db = null;

    public static final String DATABASE_NAME = "collegeDroidDBS";
    public static final String DATABASE_TABLE_SCHEDULE = "schedule";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_ID = "id";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_USER_ID = "user_id";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_PLACE_ID = "place_id";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_NAME = "name";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_FULL_NAME = "full_name";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_START_MINUTE = "start_minute";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_START_HOUR = "start_hour";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_END_MINUTE = "end_minute";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_END_HOUR = "end_hour";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_DAY = "day";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_TEACHER = "teacher";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_CAPACITY = "capacity";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_OCCUPANCY = "occupancy";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_ACTION_TYPE = "action_type";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_START_WEEK = "start_week";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_END_WEEK = "end_week";
    public static final String DATABASE_TABLE_SCHEDULE_COLUMN_WEEK_TYPE = "week_type";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_CREATE_QUERY =
            "CREATE TABLE " + DATABASE_TABLE_SCHEDULE +
                    " (" + DATABASE_TABLE_SCHEDULE_COLUMN_ID + " TEXT PRIMARY KEY, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_USER_ID + " TEXT, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_PLACE_ID + " TEXT, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_NAME + " TEXT, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_FULL_NAME + " TEXT, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_START_MINUTE + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_START_HOUR + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_END_MINUTE + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_END_HOUR + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_DAY + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_TEACHER + " TEXT, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_CAPACITY + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_OCCUPANCY + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_ACTION_TYPE + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_START_WEEK + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_END_WEEK + " INTEGER, " +
                    DATABASE_TABLE_SCHEDULE_COLUMN_WEEK_TYPE + " INTEGER );";

    public static DatabaseCore getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseCore(context.getApplicationContext());
        }

        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseCore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //log.info("database created");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //log.info("creating database");

        sqLiteDatabase.execSQL(DATABASE_CREATE_QUERY);

        //log.info("table data created with query : " + DATABASE_CREATE_QUERY);

        //log.info("database successfully created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }


    public synchronized SQLiteDatabase open() {

        //log.info("database opened");

        return db = getWritableDatabase();

    }

    public synchronized void close() {

        //log.info("database closed");

        db.close();

    }



}