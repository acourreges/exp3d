package com.breakingbyte.wrap;

import com.breakingbyte.game.render.Texture;


public class FontTexture {
    
    @SuppressWarnings("unused")
    private static final String TAG = "FontTexture";
    
    public static final float ALIGN_CENTER = Float.NEGATIVE_INFINITY;
    
    public int textureID;
    
    public boolean useMipMap;
    
    public int width, height;
    
    public boolean useShadow;
    public int shadowColor;
    public float shadowOffsetX, shadowOffsetY;
    
    public FontTexture(int width, int height, boolean useMipMap) {

    }
    
    public void useFont(String fontName, float fontSize, int fontColor) {


    }
    
    public void enableShadow(int shadowColor, float offsetX, float offsetY) {
        this.useShadow = true;
        this.shadowColor = shadowColor;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
    }
    
    public void disableShadow() {
        useShadow = false;
    }
    
    public float getStringLength(String string) {
        return 0f;
    }
    
    public void drawString(String text, float x, float y) {
        

    }
    
    public Texture generateTexture() {
        
        return null;
    }
    
    public void saveToFile(String fileName) {
        //Only for the desktop version
    }
    
    public void recycle() {

    }
    


}
