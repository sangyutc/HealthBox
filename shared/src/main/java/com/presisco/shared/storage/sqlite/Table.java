package com.presisco.shared.storage.sqlite;

/**
 * Created by presisco on 2017/4/17.
 */
public class Table {
    public String name;
    public Column[] columns;

    public Table(String _name, Column[] _columns) {
        name = _name;
        columns = _columns;
    }

    public static Table create(String _name, Column[] _columns) {
        return new Table(_name, _columns);
    }

}
