package com.presisco.boxmeter.Service;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.presisco.shared.service.BaseDBService;

public class DBService extends BaseDBService {
    private final IBinder mBinder = new DBServiceBinder();

    public DBService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return null;
    }

    public class DBServiceBinder extends Binder {
        public DBService getService() {
            return DBService.this;
        }
    }
}
