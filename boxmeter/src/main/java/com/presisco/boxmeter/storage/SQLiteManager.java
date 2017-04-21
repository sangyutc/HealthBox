package com.presisco.boxmeter.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.presisco.boxmeter.Data.Event;
import com.presisco.shared.storage.sqlite.Column;
import com.presisco.shared.storage.sqlite.SQLiteOpener;
import com.presisco.shared.storage.sqlite.Table;

/**
 * Created by presisco on 2017/4/17.
 */

public class SQLiteManager {
    public static final String TABLE_SPO2H = "HEART_RATE";
    public static final String TABLE_EVENT = "EVENT";
    public static final String COLUMN_ANALYSE_RATE = "analyse_rate";
    public static final String COLUMN_SPO2H = "rate";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_START_TIME = "start";
    public static final String COLUMN_OFFSET_TIME = "offset_time";
    public static final String COLUMN_EVENT_TYPE = "event_type";
    public static final String[] COLUMNS_EVENT = new String[]{COLUMN_EVENT_ID, COLUMN_EVENT_TYPE, COLUMN_ANALYSE_RATE, COLUMN_START_TIME};
    public static final Table[] TABLES = {
            Table.create(
                    TABLE_EVENT
                    , new Column[]{
                            Column.create(COLUMN_EVENT_ID, "integer primary key autoincrement"),
                            Column.create(COLUMN_EVENT_TYPE, "text"),
                            Column.create(COLUMN_ANALYSE_RATE, "integer"),
                            Column.create(COLUMN_START_TIME, "text")
                    }),
            Table.create(
                    TABLE_SPO2H
                    , new Column[]{
                            Column.create(COLUMN_EVENT_ID, "integer"),
                            Column.create(COLUMN_SPO2H, "integer"),
                            Column.create(COLUMN_OFFSET_TIME, "integer"),
                    }
            )
    };
    private static final String DATABASE_NAME = "app_data";

    private static final String STATEMENT_INSERT_EVENT = "insert into " + TABLE_EVENT
            + "( " + COLUMN_EVENT_TYPE + COLUMN_ANALYSE_RATE + COLUMN_START_TIME
            + " ) values(?,?,?)";
    private static final String STATEMENT_INSERT_DATA = "insert into " + TABLE_SPO2H
            + "( " + COLUMN_EVENT_ID + COLUMN_SPO2H + COLUMN_OFFSET_TIME
            + ") values(?,?,?)";

    private SQLiteOpener mDBOpener = null;

    public SQLiteManager(Context context) {
        mDBOpener = new SQLiteOpener(context, DATABASE_NAME, 1, TABLES);
    }

    public Event[] getAllEvents() {
        Cursor cursor = mDBOpener.getReadableDatabase().query(
                TABLE_EVENT, COLUMNS_EVENT, null, null, null, null, null);
        Event[] events = new Event[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            events[index] = new Event();
            events[index].id = cursor.getLong(cursor.getColumnIndex(COLUMN_EVENT_ID));
            events[index].analyse_rate = (int) cursor.getLong(cursor.getColumnIndex(COLUMN_ANALYSE_RATE));
            events[index].type = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TYPE));
            events[index].start_time = cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME));
            index++;
        }
        return events;
    }

    public int[] getAllDataInEvent(long event_id) {
        Cursor cursor = mDBOpener.getReadableDatabase().query(
                TABLE_SPO2H, new String[]{COLUMN_SPO2H, COLUMN_OFFSET_TIME},
                COLUMN_EVENT_ID + "=?", new String[]{event_id + ""},
                null, null, COLUMN_OFFSET_TIME);
        int[] data = new int[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            data[index++] = (int) cursor.getLong(cursor.getColumnIndex(COLUMN_SPO2H));
        }
        return data;
    }

    public long addEvent(Event event) {
        SQLiteStatement statement = mDBOpener.getWritableDatabase().compileStatement(STATEMENT_INSERT_EVENT);
        statement.bindString(1, event.type);
        statement.bindLong(2, event.analyse_rate);
        statement.bindString(3, event.start_time);
        long event_id = statement.executeInsert();
        return event_id;
    }

    public void addDataToEvent(long event_id, int spo2h, int offset_time) {
        SQLiteStatement statement = mDBOpener.getWritableDatabase().compileStatement(STATEMENT_INSERT_DATA);
        statement.bindLong(1, event_id);
        statement.bindLong(2, spo2h);
        statement.bindLong(3, offset_time);
        statement.executeInsert();
    }

    public void clearAllData() {
        String sql_delete = "delete from ?";
        SQLiteDatabase db = mDBOpener.getWritableDatabase();
        for (Table table : TABLES) {
            db.execSQL(sql_delete, new String[]{table.name});
        }
    }

}
