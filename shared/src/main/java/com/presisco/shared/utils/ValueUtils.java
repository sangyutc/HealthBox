package com.presisco.shared.utils;

import android.graphics.Color;

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

    public static boolean inLimit(int var, int roof, int floor) {
        return var < roof && var > floor;
    }

    public static double[] convertStringArray2DoubleArray(String[] array) {
        double[] converted = new double[array.length];
        for (int i = 0; i < array.length; ++i) {
            converted[i] = Double.parseDouble(array[i]);
        }
        return converted;
    }

    public static int[] convertStringArray2ColorArray(String[] array) {
        int[] converted = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            converted[i] = Color.parseColor(array[i]);
        }
        return converted;
    }
}
