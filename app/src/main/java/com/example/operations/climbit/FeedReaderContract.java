package com.example.operations.climbit;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @version 1.0
 * Created by Operations on 7/24/2017.
 */

public final class FeedReaderContract {
    private FeedReaderContract() {
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Route";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_GRADE = "grade";
        public static final String COLUMN_NAME_SETTER = "setter";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_FINISH = "finish";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_FELT_LIKE = "felt_like";
        public static final String COLUMN_NAME_LOCATION = "location";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_NAME + " TEXT," +
                    FeedEntry.COLUMN_NAME_GRADE + " TEXT," +
                    FeedEntry.COLUMN_NAME_SETTER + " TEXT," +
                    FeedEntry.COLUMN_NAME_START + " TEXT," +
                    FeedEntry.COLUMN_NAME_FINISH + " TEXT," +
                    FeedEntry.COLUMN_NAME_RATING + " TEXT," +
                    FeedEntry.COLUMN_NAME_FELT_LIKE + " TEXT," +
                    FeedEntry.COLUMN_NAME_LOCATION + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "ClimbIt.db";
        public final SQLiteDatabase db_writer = this.getWritableDatabase();

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        /**
         * This method grabs all of the information pertinent to a Route and uses
         * a SQLiteDatabase to insert it.
         *
         * @param name      Name field of a Route.
         * @param grade     Grade field of a Route.
         * @param setter    Setter field of a Route.
         * @param start     Start date field of a Route.
         * @param finish    Finish date field of a Route.
         * @param rating    Rating field of a Route.
         * @param felt_like Felt Like field of a Route.
         * @param location  Location field of a Route.
         * @return
         */
        public long insertNewRoute(String name, String grade, String setter, String start,
                                   String finish, float rating, String felt_like, String location) {
            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_NAME_NAME, name);
            values.put(FeedEntry.COLUMN_NAME_GRADE, grade);
            values.put(FeedEntry.COLUMN_NAME_SETTER, setter);
            values.put(FeedEntry.COLUMN_NAME_START, start);
            values.put(FeedEntry.COLUMN_NAME_FINISH, finish);
            values.put(FeedEntry.COLUMN_NAME_RATING, rating);
            values.put(FeedEntry.COLUMN_NAME_FELT_LIKE, felt_like);
            values.put(FeedEntry.COLUMN_NAME_LOCATION, location);

            return db_writer.insert(FeedEntry.TABLE_NAME, null, values);
        }
    }
}


