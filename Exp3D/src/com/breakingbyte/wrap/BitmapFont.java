package com.breakingbyte.wrap;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.util.MathUtil;

public class BitmapFont extends Texture {
    
    private static final String TAG = "BitmapFont";
    
    private String fontName;
    
    public ArrayList<float[]> glyphRegion; // left top right bottom

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;   
    
    private char charMin;
    @SuppressWarnings("unused")
    private char charMax;
    
    public int pixelWidth = 512, pixelHeight = 512;
    
    public static void main(String[] args) {
        BitmapFont f = new BitmapFont("Skir.ttf");
        //f.generate(256, 256);
        f.generate(512, 512);
        
    }
    
    public BitmapFont(String fontName) {
        super(fontName);
        glyphRegion = new ArrayList<float[]>(40);
        this.fontName = fontName;
    }
    
    @Override
    public void load() {
        if (isLoaded) {
            Log.e(TAG, "Image already loaded, ignoring. " + fontName);
            return;
        }
        generate(512, 512);
        isLoaded = true;
    }
    
    private void generate(int resolutionX, int resolutionY) {
        generate(resolutionX, resolutionY, ' ', '~');
    }
    
    private void generate(int resolutionX, int resolutionY, char charMin, char charMax) {
        
        this.charMin = charMin;
        this.charMax = charMax;
        
        //Initialize glyph regions
        int nbGlyph = charMax - charMin + 1;
        while (glyphRegion.size() < nbGlyph) {
            glyphRegion.add( new float[]{0f, 0f, 0f, 0f} );
        }
        
        //Initialize image
        bitmap = Bitmap.createBitmap(resolutionX, resolutionY, Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setAntiAlias(true);

        
        /*
            paint.setColor(Color.GREEN);
            canvas.drawRect(0, 0, resolutionX, resolutionY, paint);
        */
        
        //Initialize font
        Typeface font = getFont(fontName);
        
        int foregroundColor = 0xffffffff;
        int backgroundColor = 0xaa000000;
        float fontSize = resolutionY  * 0.09f;
        
        paint.setTypeface(font);
        paint.setColor(foregroundColor);
        paint.setTextSize(fontSize);

        
        float shadowOffset = resolutionX * 0.004f;

               
        float spacer = resolutionX * 0.018f;
        float lineHeight = Math.abs(paint.ascent()) + Math.abs(paint.descent());
        int i = 0;
        char glyph = charMin;
        
        int currentX = MathUtil.roundedUpInt(spacer);
        int currentY = MathUtil.roundedUpInt(spacer + lineHeight);
        
        for (; glyph <= charMax; glyph++, i++) {
            String chString = Character.toString(glyph);
            float charWidth = paint.measureText(chString);
            if (currentX + charWidth + spacer > resolutionX) {
                currentX = MathUtil.roundedUpInt(spacer);
                currentY = MathUtil.roundedUpInt(currentY + lineHeight + spacer);
            }
            
            //Shadow
            paint.setColor(backgroundColor);
            canvas.drawText(chString, currentX + shadowOffset, currentY + shadowOffset, paint);
            
            paint.setColor(foregroundColor);
            canvas.drawText(chString, currentX, currentY, paint);
            //Log.d(TAG, "Printing: " + glyph + " x: " + currentX + " y: " + currentY + " ch: " + chString);
            
            /*
            //Save UV
            float[] uv = glyphRegion.get(i);
            uv[0] = (currentX - spacer * 0.5f) / (float)resolutionX; //left
            uv[1] = (currentY - lineHeight - spacer * 0.5f) / (float)resolutionY; //top
            uv[2] = (currentX + charWidth + spacer * 0.5f) / (float)resolutionX; //left
            uv[3] = (currentY + spacer * 0.5f) / (float)resolutionY; //bottom
            */
            //Save UV
            float[] uv = glyphRegion.get(i);
            uv[0] = (currentX - spacer * 0.5f) / (float)resolutionX; //left
            uv[1] = (currentY - lineHeight + spacer * 0.5f) / (float)resolutionY; //top
            uv[2] = (currentX + charWidth + spacer * 0.5f) / (float)resolutionX; //right
            uv[3] = (currentY + 2 * spacer /** 0.5f*/) / (float)resolutionY; //bottom
            
            currentX = MathUtil.roundedUpInt(currentX + charWidth + spacer);
        }
        
        nativeTexture = new NativeTexture("Font-Texture");
        nativeTexture.fetched = true;
        nativeTexture.glTextureID = TextureUtil.loadMipMappedTexture(bitmap);
    }
    
    public float[] getGlyphRegion(char c) {
        return glyphRegion.get(c - charMin);
    }
    
    private static HashMap<String, Typeface> fontCache = new HashMap<String, Typeface>();
    
    public static Typeface getFont(String fontName) {
        Typeface font = fontCache.get(fontName);
        
        if (font == null) {
            font = Typeface.createFromAsset(BaseActivity.instance.getAssets(), "fonts/" + fontName);
            fontCache.put(fontName, font);
        }
        return font;
    }
    
    public void saveToFile(String fileName) {
       //NOP
    }
    
}
