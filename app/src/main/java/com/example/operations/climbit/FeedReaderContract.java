package com.example.operations.climbit;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

final class FeedReaderContract {
    private FeedReaderContract() {
    }

    static class FeedEntry implements BaseColumns {
        static final String TABLE_NAME = "Route";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_GRADE = "grade";
        static final String COLUMN_NAME_SETTER = "setter";
        static final String COLUMN_NAME_START = "start";
        static final String COLUMN_NAME_FINISH = "finish";
        static final String COLUMN_NAME_RATING = "rating";
        static final String COLUMN_NAME_FELT_LIKE = "felt_like";
        static final String COLUMN_NAME_LOCATION = "location";
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

    static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "ClimbIt.db";
        final SQLiteDatabase db_writer = this.getWritableDatabase();

        FeedReaderDbHelper(Context context) {
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
         * @return The result code of the insert transaction.
         */
        long insertNewRoute(String name, String grade, String setter, String start,
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


