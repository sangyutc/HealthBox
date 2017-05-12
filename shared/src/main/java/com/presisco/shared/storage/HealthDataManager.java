package com.presisco.shared.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.presisco.shared.storage.sqlite.SQLiteOpener;
import com.presisco.shared.storage.sqlite.Table;

/**
 * Created by presisco on 2017/4/27.
 */

/**
 * 健康信息管理工具基类
 *
 * @param <EVENT>      测量事件类型
 * @param <EVENT_DATA> 测量数据类型
 */
public abstract class HealthDataManager<EVENT, EVENT_DATA> {
    protected static final int READ_DATABASE = 0;
    protected static final int WRITE_DATABASE = 1;

    private SQLiteOpener mDBOpener = null;

    protected HealthDataManager(Context context, String db_name, Table[] tables) {
        mDBOpener = new SQLiteOpener(context, db_name, 1, tables);
    }

    protected abstract EVENT[] getEvents(Cursor cursor);

    public abstract EVENT[] getAllEvents();

    public abstract EVENT_DATA[] getAllDataInEvent(long event_id);

    public abstract EVENT[] getEventsByType(String type);

    public abstract long addEvent(EVENT base_event);

    public abstract void addDataToEvent(EVENT_DATA base_event_data);

    public abstract void addDataToEvent(EVENT_DATA[] base_event_data);

    public abstract void clearAllData();

    public abstract void deleteEvent(long event_id);

    public abstract EVENT[] getEventsAfter(long event_id);

    protected SQLiteDatabase getDatabase(int mode) {
        if (mode == READ_DATABASE) {
            return mDBOpener.getReadableDatabase();
        } else {
            return mDBOpener.getWritableDatabase();
        }
    }
}
