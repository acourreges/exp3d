package com.breakingbyte.wrap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;


public class StringFetcher {
    
    public boolean fetched = false;
    
    public String filePath = null;
    
    public String content;
    
    public StringFetcher(String filePath) {
        this.filePath = filePath;
    }

    public void fetch() {
        
        InputStream is = Resource.getResInputStream(filePath);
        
        Writer writer = new StringWriter();
        
        try {
        char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            content =  writer.toString();
        } catch (Exception e) {
            Log.e("StringFetcher", "Error reading model file " + filePath, e);
        }
        
        fetched = true;
    }
    
}
