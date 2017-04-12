package com.presisco.shared.utils;

import android.util.Log;

/**
 * Created by presisco on 2017/4/9.
 */

public class LCAT {
    public static void d(Object object, String content) {
        Log.d(object.getClass().getName(), content);
    }
}
