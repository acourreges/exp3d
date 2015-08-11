package com.breakingbyte.wrap;

import com.breakingbyte.game.resource.Resource.ResourceLevel;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;

public class Platform {
    
    public static String name = "Desktop";
    
    public static GraphicsAPI renderingAPI = GraphicsAPI.GLES1;
    
    public static boolean canGenerateTextTexture = true;
    
    public static int engineStartLevel = ResourceLevel.ENGINE;
    
    public static final void exitApplication() {
        System.exit(0);
    }
    
    public static final long getNanoTime() {
        return System.nanoTime();
    }
    
    public static final double getMillisecondTime() {
        return System.nanoTime() / (double)1000000;
    }
    
    public static final String getDeviceModel() {
        return "model";
    }
    
    public static final boolean supportInAppPurchase() {
        return false;
    }
    
    public static final void garbageCollect() {
        System.gc();
    }
    
    public static final void displayLoadingDialog(final boolean display) {
        
    }
    
    public static final void toastNotify(String message) {
    
    }
    
    public static final void openURL(final String url) {
        Log.d("Platforn", "Request to open URL " + url);
    }
}
