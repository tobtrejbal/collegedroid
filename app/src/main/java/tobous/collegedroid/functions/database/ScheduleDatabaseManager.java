package tobous.collegedroid.functions.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tob on 11. 1. 2016.
 */
public class ScheduleDatabaseManager {

    DatabaseCore dbsManager;

    SQLiteDatabase db;

    ReentrantLock databaseLock;

    Context context;

    public ScheduleDatabaseManager(Context context) {

        dbsManager = DatabaseCore.getInstance(context);

        databaseLock = new ReentrantLock();

        this.context = context;

        open();

        //log.info("classes initialized");

    }

    public void open() {

        db = dbsManager.open();

    }

    public void close() {

        dbsManager.close();

    }

    private ScheduleAction getScheduleAction(Cursor cursor) {

        ScheduleAction scheduleAction = new ScheduleAction();

        scheduleAction.setId(cursor.getString(0));
        scheduleAction.setUser(cursor.getString(1));
        scheduleAction.setPlace(cursor.getString(2));
        scheduleAction.setName(cursor.getString(3));
        scheduleAction.setFullName(cursor.getString(4));
        scheduleAction.setStartMinute(cursor.getInt(5));
        scheduleAction.setStartHour(cursor.getInt(6));
        scheduleAction.setEndMinute(cursor.getInt(7));
        scheduleAction.setEndHour(cursor.getInt(8));
        scheduleAction.setDayOfWeek(cursor.getInt(9));
        scheduleAction.setTeacher(cursor.getString(10));
        scheduleAction.setCapacity(cursor.getInt(11));
        scheduleAction.setOccupancy(cursor.getInt(12));
        scheduleAction.setActionType(cursor.getInt(13));
        scheduleAction.setStartWeek(cursor.getInt(14));
        scheduleAction.setEndWeek(cursor.getInt(15));
        scheduleAction.setWeekType(cursor.getInt(16));

        return scheduleAction;

    }

    private void insertScheduleAction(ScheduleAction scheduleAction) {

        Log.v("updating", "");

        ContentValues values = new ContentValues();
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_ID, scheduleAction.getId());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_USER_ID, scheduleAction.getUser());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_PLACE_ID, scheduleAction.getPlace());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_NAME, scheduleAction.getName());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_FULL_NAME, scheduleAction.getFullName());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_START_MINUTE, scheduleAction.getStartMinute());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_START_HOUR, scheduleAction.getStartHour());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_END_MINUTE, scheduleAction.getEndMinute());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_END_HOUR, scheduleAction.getEndHour());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_DAY, scheduleAction.getDayOfWeek());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_TEACHER, scheduleAction.getTeacher());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_CAPACITY, scheduleAction.getCapacity());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_OCCUPANCY, scheduleAction.getOccupancy());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_ACTION_TYPE, scheduleAction.getActionType());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_START_WEEK, scheduleAction.getStartWeek());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_END_WEEK, scheduleAction.getEndWeek());
        values.put(DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_WEEK_TYPE, scheduleAction.getWeekType());

        db.insertWithOnConflict(DatabaseCore.DATABASE_TABLE_SCHEDULE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public void removeItem(ScheduleAction scheduleAction) throws InterruptedException {

        if (databaseLock.tryLock(5000, TimeUnit.MILLISECONDS)) {

            try {


                db.delete(DatabaseCore.DATABASE_TABLE_SCHEDULE, DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_ID + "=?", new String[] { scheduleAction.getId() });

            } finally {

                databaseLock.unlock();

            }

        } else {


        }
    }

    public void insertUpdate(ScheduleAction scheduleAction) throws InterruptedException {

        if (databaseLock.tryLock(5000, TimeUnit.MILLISECONDS)) {

            try {


                insertScheduleAction(scheduleAction);

            } finally {

                databaseLock.unlock();

            }

        } else {


        }

    }

    public List<ScheduleAction> getUsersActions(String userId) throws InterruptedException {

        if(databaseLock.tryLock(5000, TimeUnit.MILLISECONDS)) {

            try {

                String query = "SELECT * FROM " + DatabaseCore.DATABASE_TABLE_SCHEDULE + " WHERE " +
                        DatabaseCore.DATABASE_TABLE_SCHEDULE_COLUMN_USER_ID + " = ?";

                Cursor cursor = db.rawQuery(query,new String[] { userId });

                //log.info("got data with count : " + cursor.getCount());
                //log.info("got data with count : " + cursor.getCount());
                List<ScheduleAction> scheduleActions = new ArrayList<ScheduleAction>(cursor.getCount());

                cursor.moveToFirst();

                for(int i = 0; !cursor.isAfterLast(); cursor.moveToNext(), i++) {

                    scheduleActions.add(getScheduleAction(cursor));

                }

                //log.info("data retrieved");

                return scheduleActions;

            } finally {

                databaseLock.unlock();

            }

        } else {

            return null;

        }



    }


    public void updateOrInsertActions(List<ScheduleAction> actions) throws InterruptedException {

        if (databaseLock.tryLock(5000, TimeUnit.MILLISECONDS)) {

            try {

                Log.v("updating events, size ", actions.size() + "");

                //log.info("inserting or updating events");

                db.beginTransaction();

                for (int i = 0; i < actions.size(); i++) {

                    insertScheduleAction(actions.get(i));

                }

                db.setTransactionSuccessful();

                db.endTransaction();

                //log.info("data successfully saved");

            } finally {

                databaseLock.unlock();

            }

        }
    }



    public void saveScheduleActions(List<ScheduleAction> scheduleActionList) {



    }

}
