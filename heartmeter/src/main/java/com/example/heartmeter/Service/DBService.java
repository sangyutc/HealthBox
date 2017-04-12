package com.example.heartmeter.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.IBinder;

public class DBService extends Service {
    private final IBinder mBinder = new DBServiceBinder();
    SQLiteDatabase mDatabase = null;

    public DBService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DBOpenHelper openHelper = new DBOpenHelper(this);
        mDatabase = openHelper.getWritableDatabase();
    }

    @Override
    public void onDestroy() {
        mDatabase.releaseReference();
        mDatabase = null;
        super.onDestroy();
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {

        public static final String TABLE_HEART = "heart_data";
        public static final String TABLE_EVENT = "event";
        public static final String TABLE_USER = "user";

        public static final String KEY_TYPE = "type";
        public static final String KEY_START_DATE = "start_date";
        public static final String KEY_END_DATE = "end_date";
        public static final String KEY_RATE = "rate";
        public static final String KEY_EVENT_ID = "event_id";
        public static final String KEY_DATE = "date";

        public static final String KEY_NAME = "name";
        public static final String KEY_MD5 = "md5";
        public static final String KEY_SEX = "sex";
        public static final String KEY_OCC = "occupation";
        public static final String KEY_AGE = "age";

        public DBOpenHelper(Context context) {
            super(context, "app_data", null, 1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_HEART + " (" +
                            KEY_EVENT_ID + " TEXT, " +
                            KEY_RATE + " TEXT);");
            db.execSQL(
                    "CREATE TABLE " + TABLE_EVENT + " (" +
                            KEY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                            KEY_TYPE + " TEXT , " +
                            KEY_START_DATE + " TEXT , " +
                            KEY_END_DATE + " TEXT );");
            db.execSQL(
                    "CREATE TABLE " + TABLE_USER + " (" +
                            KEY_NAME + " TEXT PRIMARY KEY , " +
                            KEY_MD5 + " TEXT , " +
                            KEY_SEX + " TEXT , " +
                            KEY_OCC + " TEXT , " +
                            KEY_AGE + " TEXT );");
        }
    }

    public class DBServiceBinder extends Binder {
        public DBService getService() {
            return DBService.this;
        }
    }
}
