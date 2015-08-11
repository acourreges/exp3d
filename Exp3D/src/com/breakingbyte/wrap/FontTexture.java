package com.breakingbyte.wrap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.breakingbyte.game.engine.Debug;
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
        this.useMipMap = useMipMap;
        this.width = width;
        this.height = height;
        useShadow = false;
        
        bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setAntiAlias(true);
        
        if (Debug.fontTextureDebugBG) {
            paint.setColor(Color.GREEN);
            canvas.drawRect(0, 0, width, height, paint);
        }
    }
    
    public void useFont(String fontName, float fontSize, int fontColor) {

        Typeface font = BitmapFont.getFont(fontName);
        
        paint.setTypeface(font);
        paint.setColor(fontColor);
        paint.setTextSize(fontSize);
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
        return paint.measureText(string);
    }
    
    public void drawString(String text, float x, float y) {
        
        if (x == ALIGN_CENTER) {
            x = (width - getStringLength(text)) / 2f; 
        }
        
        if (useShadow) {
            int colorBackup = paint.getColor();
            paint.setColor(shadowColor);
            canvas.drawText(text, x + shadowOffsetX, y + shadowOffsetY, paint);
            paint.setColor(colorBackup);
        }
        canvas.drawText(text, x, y, paint);
    }
    
    public Texture generateTexture() {
        textureID = useMipMap? TextureUtil.loadMipMappedTexture(bitmap) : TextureUtil.loadTexture(bitmap);
        
        NativeTexture nativetexture = new NativeTexture("auto-generated-by-FontTexture");
        nativetexture.fetched = true;
        nativetexture.glTextureID = textureID;
        
        Texture result = new Texture("auto-generated-by-FontTexture");
        result.nativeTexture = nativetexture;
        result.isLoaded = true;
        
        return result;
    }
    
    public void saveToFile(String fileName) {
        //Only for the desktop version
    }
    
    public void recycle() {
        paint = null;
        canvas = null;
        bitmap.recycle();
        bitmap = null;
    }
    
    
    
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

}
