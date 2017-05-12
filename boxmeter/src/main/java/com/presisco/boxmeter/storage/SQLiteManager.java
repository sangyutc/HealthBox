package com.presisco.boxmeter.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.presisco.boxmeter.Data.Event;
import com.presisco.boxmeter.Data.EventData;
import com.presisco.shared.storage.HealthDataManager;
import com.presisco.shared.storage.sqlite.Column;
import com.presisco.shared.storage.sqlite.Table;

/**
 * Created by presisco on 2017/4/17.
 */

public class SQLiteManager extends HealthDataManager<Event, EventData> {
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
            + "( " + COLUMN_EVENT_TYPE + "," + COLUMN_ANALYSE_RATE + "," + COLUMN_START_TIME
            + " ) values(?,?,?)";
    private static final String STATEMENT_INSERT_DATA = "insert into " + TABLE_SPO2H
            + "( " + COLUMN_EVENT_ID + "," + COLUMN_SPO2H + "," + COLUMN_OFFSET_TIME
            + ") values(?,?,?)";
    private static final String STATEMENT_DELETE_EVENT = "delete from " + TABLE_EVENT
            + " where " + COLUMN_EVENT_ID + " = ?";
    private static final String STATEMENT_DELETE_DATA = "delete from " + TABLE_SPO2H
            + " where " + COLUMN_EVENT_ID + " = ?";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, TABLES);
    }

    @Override
    protected Event[] getEvents(Cursor cursor) {
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

    @Override
    public Event[] getAllEvents() {
        Cursor cursor = getDatabase(READ_DATABASE).query(
                TABLE_EVENT, COLUMNS_EVENT, null, null, null, null, null);
        return getEvents(cursor);
    }

    @Override
    public Event[] getEventsByType(String type) {
        Cursor cursor = getDatabase(READ_DATABASE).query(
                TABLE_EVENT, COLUMNS_EVENT, COLUMN_EVENT_TYPE + " = ?", new String[]{type}, null, null, null);
        return getEvents(cursor);
    }

    @Override
    public Event[] getEventsAfter(long event_id) {
        Cursor cursor = getDatabase(READ_DATABASE).query(
                TABLE_EVENT, COLUMNS_EVENT, COLUMN_EVENT_ID + " >= ?", new String[]{event_id + ""}, null, null, null);
        return getEvents(cursor);
    }

    @Override
    public EventData[] getAllDataInEvent(long event_id) {
        Cursor cursor = getDatabase(READ_DATABASE).query(
                TABLE_SPO2H, new String[]{COLUMN_SPO2H, COLUMN_OFFSET_TIME},
                COLUMN_EVENT_ID + "=?", new String[]{event_id + ""},
                null, null, COLUMN_OFFSET_TIME);
        EventData[] data = new EventData[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            data[index] = new EventData();
            data[index].event_id = event_id;
            data[index].spo2h = (int) cursor.getLong(cursor.getColumnIndex(COLUMN_SPO2H));
            data[index].offset_time = (int) cursor.getLong(cursor.getColumnIndex(COLUMN_OFFSET_TIME));
            index++;
        }
        return data;
    }

    @Override
    public long addEvent(Event event) {
        SQLiteStatement statement = getDatabase(WRITE_DATABASE).compileStatement(STATEMENT_INSERT_EVENT);
        statement.bindString(1, event.type);
        statement.bindLong(2, event.analyse_rate);
        statement.bindString(3, event.start_time);
        long event_id = statement.executeInsert();
        return event_id;
    }

    @Override
    public void addDataToEvent(EventData base_event_data) {
        SQLiteStatement statement = getDatabase(WRITE_DATABASE).compileStatement(STATEMENT_INSERT_DATA);
        statement.bindLong(1, base_event_data.event_id);
        statement.bindLong(2, base_event_data.spo2h);
        statement.bindLong(3, base_event_data.offset_time);
        statement.executeInsert();
    }

    @Override
    public void addDataToEvent(EventData[] base_event_data) {
        for (EventData eventData : base_event_data) {
            addDataToEvent(eventData);
        }
    }

    @Override
    public void deleteEvent(long event_id) {
        SQLiteStatement statement = getDatabase(WRITE_DATABASE).compileStatement(STATEMENT_DELETE_DATA);
        statement.bindLong(1, event_id);
        statement.executeUpdateDelete();
        statement = getDatabase(WRITE_DATABASE).compileStatement(STATEMENT_DELETE_EVENT);
        statement.bindLong(1, event_id);
        statement.executeUpdateDelete();
    }

    @Override
    public void clearAllData() {
        String sql_delete = "delete from ?";
        SQLiteDatabase db = getDatabase(WRITE_DATABASE);
        for (Table table : TABLES) {
            db.execSQL(sql_delete, new String[]{table.name});
        }
    }

}
