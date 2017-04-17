package com.presisco.shared.storage.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by presisco on 2017/4/17.
 */

public class SQLiteOpener extends SQLiteOpenHelper {
    private static final String HOLDER_NAME = "#NAME";
    private static final String HOLDER_COLUMN = "#COLUMN";
    private static final String TABLE_DEFINE = "create table " + HOLDER_NAME + " ( " + HOLDER_COLUMN + " );";
    private Table[] mTables;

    public SQLiteOpener(Context context, String name, int version, Table[] tables) {
        super(context, name, null, version);
        mTables = tables;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table table : mTables) {
            StringBuilder sb = new StringBuilder("");
            Column[] columns = table.columns;
            int index = 0;
            for (; index < table.columns.length - 1; ++index) {
                sb.append(columns[index].name + " " + columns[index].type + ", ");
            }
            sb.append(columns[index].name + " " + columns[index].type);
            db.execSQL(TABLE_DEFINE
                    .replace(HOLDER_NAME, table.name)
                    .replace(HOLDER_COLUMN, sb.toString()));
        }
    }

}
