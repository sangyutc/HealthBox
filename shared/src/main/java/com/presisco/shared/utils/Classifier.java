package com.presisco.shared.utils;

/**
 * Created by presisco on 2017/6/3.
 */

public class Classifier {
    public static String classify(double data, double[] intervals, String[] classes) {
        for (int i = 0; i < intervals.length; ++i) {
            if (data < intervals[i]) {
                return classes[i];
            }
        }
        return classes[intervals.length];
    }

    public static int classify(double data, double[] intervals) {
        for (int i = 0; i < intervals.length; ++i) {
            if (data < intervals[i]) {
                return i;
            }
        }
        return intervals.length;
    }
}
