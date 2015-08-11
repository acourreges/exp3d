package com.breakingbyte.wrap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.render.Texture;

public class FontTexture {
    
    private static final String TAG = "FontTexture";
    
    public static final float ALIGN_CENTER = Float.NEGATIVE_INFINITY;
    
    public int textureID;
    
    public boolean useMipMap;
    
    public int width, height;
    
    public boolean useShadow;
    public Color shadowColor;
    public float shadowOffsetX, shadowOffsetY;
    
    public FontTexture(int width, int height, boolean useMipMap) {
        
        this.useMipMap = useMipMap;
        this.width = width;
        this.height = height;
        useShadow = false;
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               
        if (Debug.fontTextureDebugBG) {
            graphics.setColor(Color.GREEN);
            graphics.fillRect(0, 0, width, height);
        }
        
    }
    
    public void useFont(String fontName, float fontSize, int fontColor) {
        
        Font font = BitmapFont.getFont(fontName);
        
        Font theFont = font.deriveFont(fontSize);
        graphics.setFont(theFont);
        graphics.setColor(new Color(fontColor, true));
        
    }
    
    public void enableShadow(int shadowColor, float offsetX, float offsetY) {
        this.useShadow = true;
        this.shadowColor = new Color(shadowColor, true);
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
    }
    
    public void disableShadow() {
        useShadow = false;
    }
    
    public float getStringLength(String string) {
        return graphics.getFontMetrics().stringWidth(string);
    }
    
    public void drawString(String text, float x, float y) {
        
        if (x == ALIGN_CENTER) {
            x = (width - getStringLength(text)) / 2f; 
        }
        
        if (useShadow) {
            Color colorBackup = graphics.getColor();
            graphics.setColor(shadowColor);
            graphics.drawString(text, x + shadowOffsetX, y + shadowOffsetY);
            graphics.setColor(colorBackup);
        }
        
        graphics.drawString(text, x, y);
    }
    
    public Texture generateTexture() {
        textureID = useMipMap? TextureUtil.loadMipMappedTexture(image) : TextureUtil.loadTexture(image);
        
        NativeTexture nativetexture = new NativeTexture("auto-generated-by-FontTexture");
        nativetexture.fetched = true;
        nativetexture.glTextureID = textureID;
        
        Texture result = new Texture("auto-generated-by-FontTexture");
        result.nativeTexture = nativetexture;
        result.isLoaded = true;
        
        return result;
    }
    
    public void saveToFile(String fileName) {
        File file = new File("./" + fileName + ".png");
        try {            
            ImageIO.write(image, "png", file);      
        } catch (IOException e) {       
            Log.e(TAG, "Cannot save to file " + fileName, e);
            return;
        }
        Log.d(TAG, "Saved " + file.getAbsolutePath());
    }
    
    public void recycle() {
        graphics.dispose();
        graphics = null;
        image = null;
    }
    
    private BufferedImage image;
    private Graphics2D graphics;
    


}
