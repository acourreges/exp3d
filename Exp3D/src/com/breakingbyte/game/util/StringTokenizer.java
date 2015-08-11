package com.breakingbyte.game.util;

public class StringTokenizer {
    
    public String str;
    public String[] splitted;
    public int currentIndex;
    
    public StringTokenizer(String str){
        this(str, " ");
    }
    
    public void reinit(String str) {
        reinit(str, " ");
    }
    
    public void reinit(String str, String delim) {
        this.str = str;
        splitted = str.split(delim);
        currentIndex = 0;
    }
    
    public StringTokenizer(String str, String delim) {
        reinit(str, delim);
    }
    
    public String nextToken() {
        if (currentIndex >= splitted.length) return null;
        return splitted[currentIndex++];
    }
}