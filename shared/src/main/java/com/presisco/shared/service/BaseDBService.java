package com.presisco.shared.service;

import android.app.Service;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.presisco.shared.utils.LCAT;

import java.util.Hashtable;
import java.util.List;

public abstract class BaseDBService extends Service {

    public BaseDBService() {
    }

    private SQLiteDatabase initDatabase(String name, List<Table> tables) {
        DBOpenHelper openHelper = new DBOpenHelper(this, name, tables);
        return openHelper.getWritableDatabase();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public abstract SQLiteDatabase getDatabase();

    public static class Table {
        public String table;
        public Hashtable<String, String> columnType;
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public static final String TABLE_DATA = "data";
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
        private List<Table> mTables;

        public DBOpenHelper(Context context, String name, List<Table> tables) {
            super(context, name, null, 1);
            mTables = tables;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (Table table : mTables) {
                StringBuilder sb = new StringBuilder("CREATE TABLE ");
                sb.append(table.table);
                sb.append(" ( ");
                for (String column : table.columnType.keySet()) {
                    sb.append(column + " " + table.columnType.get(column) + " , ");
                }
                int tail = sb.lastIndexOf(", ");
                sb.delete(tail, tail + 1);
                sb.append(");");
                String sql = sb.toString();
                LCAT.d(this, sql);
                db.execSQL(sql);
            }
            db.execSQL(
                    "CREATE TABLE " + TABLE_DATA + " (" +
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


}
