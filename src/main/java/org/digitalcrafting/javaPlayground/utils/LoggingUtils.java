package org.digitalcrafting.javaPlayground.utils;

import com.google.gson.Gson;

public class LoggingUtils {
    private static final Gson gson = new Gson();

    public static void log(Object obj) {
        System.out.println(gson.toJson(obj));
    }
}
