package com.breakingbyte.wrap;

import com.breakingbyte.game.engine.Debug;

public class Log {
    
    public static final String ROOT_TAG = "Exp3D";
    
    public static final String getFinalTag(String subTag) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        buffer.append(ROOT_TAG);
        buffer.append(" ");
        buffer.append(subTag);
        buffer.append("]");
        return buffer.toString();
    }

    //Debug
    public static void d(String tag, String msg, Throwable tr) {
        if (Debug.isRelease) return;
        android.util.Log.d(getFinalTag(tag), msg, tr) ;
    }
    
    public static void d(String tag, String msg) {
        if (Debug.isRelease) return;
        android.util.Log.d(getFinalTag(tag), msg) ;
    }
    
    //Info
    public static void i(String tag, String msg, Throwable tr) {
        if (Debug.isRelease) return;
        android.util.Log.i(getFinalTag(tag), msg, tr) ;
    }
    
    public static void i(String tag, String msg) {
        if (Debug.isRelease) return;
        android.util.Log.i(getFinalTag(tag), msg) ;
    }
    
    //Warning
    public static void w(String tag, String msg, Throwable tr) {
        if (Debug.isRelease) return;
        android.util.Log.w(getFinalTag(tag), msg, tr) ;
    }
    
    public static void w(String tag, String msg) {
        if (Debug.isRelease) return;
        android.util.Log.w(getFinalTag(tag), msg) ;
    }
    
    //Error
    public static void e(String tag, String msg, Throwable tr) {
        if (Debug.isRelease) return;
        android.util.Log.e(getFinalTag(tag), msg, tr) ;
    }
    
    public static void e(String tag, String msg) {
        if (Debug.isRelease) return;
        android.util.Log.e(getFinalTag(tag), msg) ;
    }

}
