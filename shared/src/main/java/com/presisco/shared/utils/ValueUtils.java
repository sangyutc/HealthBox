package com.presisco.shared.utils;

/**
 * Created by presisco on 2017/4/17.
 */

public class ValueUtils {
    public static int limit(int var, int roof, int floor) {
        if (var < floor) {
            return floor;
        } else if (var > roof) {
            return roof;
        } else {
            return var;
        }
    }
}
