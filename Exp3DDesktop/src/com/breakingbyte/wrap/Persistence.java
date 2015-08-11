package com.breakingbyte.wrap;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class Persistence {
    
    private static final String FILE_PATH = "./preferences.txt";
    
    public static final void init() {
    }
    
    public static final String readPreferences() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return "";

        String result = "";
        try {
            FileInputStream fstream = new FileInputStream(FILE_PATH);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine = br.readLine();
            if (strLine != null) result = strLine;
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static final void writePreferences(String str) {
        File file = new File(FILE_PATH);
        if (file.exists()) file.delete();
        
        PrintWriter out;
        try {
            out = new PrintWriter(FILE_PATH);
            out.write(str);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
