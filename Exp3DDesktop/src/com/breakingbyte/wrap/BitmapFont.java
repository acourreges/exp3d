package com.breakingbyte.wrap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.util.MathUtil;

public class BitmapFont extends Texture {
    
    private static final String TAG = "BitmapFont";
    
    private String fontName;
    
    public ArrayList<float[]> glyphRegion; // left top right bottom

    private BufferedImage image;
    private Graphics2D graphics;    
    
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
        image = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        /*
        {
            graphics.setColor(Color.GREEN);
            graphics.fillRect(0, 0, resolutionX, resolutionY);
        }
        */
        
        //Initialize font
        Font font = getFont(fontName);
        float fontSize = resolutionY  * 0.09f;
        
        Color foregroundColor = new Color(0xffffffff, true);
        Color backgroundColor = new Color(0xaa000000, true);
        
        float shadowOffset = resolutionX * 0.004f;
        
        Font theFont = font.deriveFont(fontSize);
        graphics.setFont(theFont);
        graphics.setColor(foregroundColor);
        
        FontMetrics measure = graphics.getFontMetrics();
        
        
        float spacer = resolutionX * 0.018f;
        float lineHeight = measure.getAscent() + measure.getDescent();
        int i = 0;
        char glyph = charMin;
        
        int currentX = MathUtil.roundedUpInt(spacer);
        int currentY = MathUtil.roundedUpInt(spacer + lineHeight);
        
        for (; glyph <= charMax; glyph++, i++) {
            String chString = Character.toString(glyph);
            float charWidth = measure.stringWidth(chString);
            if (currentX + charWidth + spacer > resolutionX) {
                currentX = MathUtil.roundedUpInt(spacer);
                currentY = MathUtil.roundedUpInt(currentY + lineHeight + spacer);
            }
            
            //Shadow
            graphics.setColor(backgroundColor);
            graphics.drawString(chString, currentX + shadowOffset, currentY + shadowOffset);
            
            graphics.setColor(foregroundColor);
            graphics.drawString(chString, currentX, currentY);
            
            //Save UV
            float[] uv = glyphRegion.get(i);
            uv[0] = (currentX - spacer * 0.4f) / (float)resolutionX; //left
            uv[1] = (currentY - lineHeight + spacer * 0.5f) / (float)resolutionY; //top
            uv[2] = (currentX + charWidth + spacer * 0.4f) / (float)resolutionX; //right
            uv[3] = (currentY + 2 * spacer /** 0.5f*/) / (float)resolutionY; //bottom
            
            currentX = MathUtil.roundedUpInt(currentX + charWidth + spacer);
        }
        
        /*
        for (i = 0; i < glyphRegion.size(); i++) {
            float[] uv = glyphRegion.get(i);
            System.out.print("glyphRegion.add( new float[] { ");
            for (int j = 0; j < 4; j++) {
                System.out.print(uv[j] + "f");
                if (j != 3) System.out.print(", ");
            }
            System.out.print("} );\n");
        }
        
        
        saveToFile("dump_bitmap_font.png");
        */
        
        nativeTexture = new NativeTexture("Font-Texture");
        nativeTexture.fetched = true;
        nativeTexture.glTextureID = TextureUtil.loadMipMappedTexture(image);  
    }
    
    public float[] getGlyphRegion(char c) {
        return glyphRegion.get(c - charMin);
    }
    
    private static HashMap<String, Font> fontCache = new HashMap<String, Font>();
    
    public static Font getFont(String fontName) {
        String fontFile = DesktopApplication.ANDROID_ROOT;
        if (!fontFile.endsWith("/")) fontFile += "/";
        fontFile += "assets/fonts/" + fontName;
        
        Font font = Font.getFont(fontName);
        
        if ((font = fontCache.get(fontName)) == null) {
            Log.d(TAG, "Loading font from: " + fontFile);
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile));
                fontCache.put(fontName, font);
            } catch (Exception e) {
                Log.e(TAG, "Could not load font " + fontFile + "! Using default.", e);
                font = new Font("serif", Font.PLAIN, 16);
            }
        }
        return font;
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
    
}
