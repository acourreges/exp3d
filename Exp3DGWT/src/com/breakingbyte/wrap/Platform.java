package com.breakingbyte.wrap;

import com.breakingbyte.game.resource.Resource.ResourceLevel;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;
import com.google.gwt.core.client.Duration;
import com.google.gwt.user.client.Window;

public class Platform {
    
    public static String name = "WebGL";
    
    public static GraphicsAPI renderingAPI = GraphicsAPI.GLES2;
    
    public static boolean canGenerateTextTexture = false;
    
    public static int engineStartLevel = ResourceLevel.ALL;

    public static final void exitApplication() {
        //NOP
    }
    
    public static final long getNanoTime() {
        return (long)(Duration.currentTimeMillis() * 1000);
    }
    
    public static final double getMillisecondTime() {
        return Duration.currentTimeMillis();
    }
    
    public static final String getDeviceModel() {
        return "model";
    }
    
    public static final boolean supportInAppPurchase() {
        return false;
    }
    
    public static final void garbageCollect() {
        //NOP
    }
    
    public static final void displayLoadingDialog(final boolean display) {
        
    }
    
    public static final void toastNotify(String message) {
    
    }
    
    public static final void openURL(final String url) {
        Window.open(url, "_blank", "");
    }
}
