package com.presisco.shared.storage.sqlite;

/**
 * Created by presisco on 2017/4/17.
 */
public class Column {
    public String name;
    public String type;

    public Column(String _name, String _type) {
        name = _name;
        type = _type;
    }

    public static Column create(String _name, String _type) {
        return new Column(_name, _type);
    }
}
