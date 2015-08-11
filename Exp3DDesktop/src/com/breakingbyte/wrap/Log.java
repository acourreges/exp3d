package com.breakingbyte.wrap;

public class Log {
    
    public static final String ROOT_TAG = "Exp3D";
    
    public static final boolean PRINT_LINE_NUMBER = true;
    
    public static final String getFinalTag(String subTag) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        buffer.append(ROOT_TAG);
        buffer.append(" ");
        buffer.append(subTag);
        
        if (PRINT_LINE_NUMBER) {
            int lineNumber = new Exception().getStackTrace()[2].getLineNumber();
            buffer.append(":" + lineNumber);
        }
        
        buffer.append("]");
        return buffer.toString();
    }

    //Debug
    public static void d(String tag, String msg, Throwable tr) {
        System.out.println(getFinalTag(tag) + " " + msg) ;
        tr.printStackTrace();
    }
    
    public static void d(String tag, String msg) {
    	System.out.println(getFinalTag(tag) + " " + msg) ;
    }
    
    //Info
    public static void i(String tag, String msg, Throwable tr) {
    	System.out.println(getFinalTag(tag) + " " + msg) ;
    	tr.printStackTrace();
    }
    
    public static void i(String tag, String msg) {
    	System.out.println(getFinalTag(tag) + " " + msg) ;
    }
    
    //Warning
    public static void w(String tag, String msg, Throwable tr) {
    	System.err.println(getFinalTag(tag) + " " + msg) ;
    	tr.printStackTrace();
    }
    
    public static void w(String tag, String msg) {
    	System.err.println(getFinalTag(tag) + " " + msg) ;
    }
    
    //Error
    public static void e(String tag, String msg, Throwable tr) {
    	System.err.println(getFinalTag(tag) + " " + msg) ;
    	tr.printStackTrace();
    }
    
    public static void e(String tag, String msg) {
    	System.err.println(getFinalTag(tag) + " " + msg) ;
    }

}
