package com.breakingbyte.wrap;

import java.io.InputStream;

public class Resource {
    
    public static InputStream getResInputStream(int file) {
        //return GameSurfaceView.context.getResources().openRawResource(file);
        return BaseActivity.instance.getResources().openRawResource(file);
    }
    
    public static InputStream getResInputStream(String filePath) {
        try {
            InputStream result = BaseActivity.instance.getAssets().open(filePath);
            return result;
        } catch (Exception e) {
            Log.e("FileSystem", "Could not open: " + filePath);
        }
        return null;
    }

}
