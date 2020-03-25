package fr.android.quentin.my_curling_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class managerSQLI  {
    private managerSQLI() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "DATA_CURLING";
        public static final String COLUMN_NAME_MATCH_NAME = "name";
        public static final String COLUMN_NAME_MATCH_DATE = "date";
        public static final String COLUMN_NAME_MATCH_TIME = "time";
        public static final String COLUMN_NAME_MATCH_STATUS = "status";
        public static final String COLUMN_NAME_MATCH_PICTURE = "picture";
        public static final String COLUMN_NAME_MATCH_SCORE = "score";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_MATCH_NAME + " TEXT," +
                    FeedEntry.COLUMN_NAME_MATCH_DATE + " TEXT," +
                    FeedEntry.COLUMN_NAME_MATCH_TIME + " TEXT," +
                    FeedEntry.COLUMN_NAME_MATCH_STATUS + " NUMBER," +
                    FeedEntry.COLUMN_NAME_MATCH_PICTURE + " BLOB," +
                    FeedEntry.COLUMN_NAME_MATCH_SCORE + " BLOB)";

    private static final String SQL_DELETE_ENTRIES =

            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 3;
        public static final String DATABASE_NAME = "FeedReader.db";

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
    }

}
