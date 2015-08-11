package com.breakingbyte.game.util;

public class Security {

    //Simple djb2 hash function
    public static final String hashString(String str) {
        
        long hash = 5381;
        int c;

        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
        }

        return ""+hash;
    }
    
    //Create a string using an array of ascii values
    public static String decode(Integer[] inputArray) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < inputArray.length; i++) {
            int intValue = inputArray[i];
            result.append((char)intValue);
        }
        return result.toString();
    }
}
