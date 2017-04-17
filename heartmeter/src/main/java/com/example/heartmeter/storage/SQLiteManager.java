package com.example.heartmeter.storage;

import android.content.Context;

import com.presisco.shared.storage.sqlite.Column;
import com.presisco.shared.storage.sqlite.SQLiteOpener;
import com.presisco.shared.storage.sqlite.Table;

/**
 * Created by presisco on 2017/4/17.
 */

public class SQLiteManager {
    public static final String TABLE_HEART_RATE = "HEART_RATE";
    public static final String TABLE_EVENT = "EVENT";
    public static final String COLUMN_HEART_RATE = "rate";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_START_TIME = "start";
    public static final String COLUMN_END_TIME = "end";
    public static final String COLUMN_EVENT_TYPE = "event_type";
    public static final Table[] TABLES = {
            Table.create(
                    TABLE_EVENT
                    , new Column[]{
                            Column.create(COLUMN_EVENT_ID, "integer autoincrement"),
                            Column.create(COLUMN_EVENT_TYPE, "text"),
                            Column.create(COLUMN_START_TIME, "text"),
                            Column.create(COLUMN_END_TIME, "text")
                    }),
            Table.create(
                    TABLE_HEART_RATE
                    , new Column[]{
                            Column.create(COLUMN_EVENT_ID, "integer"),
                            Column.create(COLUMN_HEART_RATE, "integer")
                    }
            )
    };
    private static final String DATABASE_NAME = "app_data";
    private SQLiteOpener mDBOpener = null;

    public SQLiteManager(Context context) {
        mDBOpener = new SQLiteOpener(context, DATABASE_NAME, 1, TABLES);
    }

    public static SQLiteManager newInstance(Context context) {
        SQLiteManager newManager = new SQLiteManager(context);
        return newManager;
    }

}
