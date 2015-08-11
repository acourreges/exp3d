package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.QuadBatch;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.wrap.BitmapFont;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.shared.Renderer;

public class DynamicText extends Widget {
    
    @SuppressWarnings("unused")
    private static final String TAG = "DynamicText";
    
    public static enum Align {
        LEFT,
        CENTER,
        RIGHT
    }
    
    public float textSize;
    
    public float spacer;
    
    private Align alignment; 
    
    protected BitmapFont font;
    
    protected float currentLineY;
    
    protected QuadBatch quadBatch;
    
    protected static final float defaultLineHeight = 0.7f;
    
    public DynamicText(int maxCharacters) {
        this(maxCharacters, TextureManager.defaultFont);
    }
    
    public DynamicText(int maxCharacters, BitmapFont font) {
        
        width = 0;
        height = defaultLineHeight; 
        
        quadBatch = new QuadBatch(maxCharacters);
        
        this.font = font;
        
        textSize = 10f;
        alignment = Align.LEFT;
        spacer = -0.1f;
        reset();
    }
    
    public DynamicText reset() {
        currentX = 0;
        currentLineY = 0;
        currentLineWidth = 0;
        width = 0;
        height = defaultLineHeight; 
        firstCharOfLine = true;
        quadBatch.clearBatch();
        return this;
    }
    
    public static DynamicText generateCenteredString(String text) {
        DynamicText result = new DynamicText(text.length());
        result.setAlignment(Align.CENTER);
        result.printString(text);
        result.updateBuffers();
        return result;
    }
    
    public void setAlignment(Align align) {
        alignment = align;
    }
    
    /*
    public static void test() {
        BitmapFont font = new BitmapFont("Skir.ttf");
        font.generate(512, 512);
        float[] region = font.getGlyphRegion('i');
        Log.d(TAG, "Region " + region[0] + " " + region[1] + " " + region[2] + " " + region[3] + " ");
    }
    */
    
    public DynamicText printString(String string) {
        firstCharOfLine = true;
        return printString(string, spacer);
    }
    
    public DynamicText printCharArray(char[] array, int count) {
        for (int i = 0; i < count; i++) {
            char c = array[i];
            if (c == '\n') newLine();
            else printChar(c);
        }
        return this;
    }
    
    public DynamicText newLine() {
        return newLine(-defaultLineHeight);
    }
    
    public DynamicText newLine(float offsetY) {
        currentLineY += offsetY;
        currentLineWidth = 0f;
        currentX = 0;
        height += offsetY;
        return this;
    }
    
    @Override
    public float getWidth() {
        return textSize * width;
    }
    
    @Override
    public float getHeight() {
        return - textSize * height;
    }
    
    float currentLineWidth = 0f;
    float currentX = 0;
    boolean firstCharOfLine = true;//mcga
    public DynamicText printString(String string, float horizontalSpacer) {
        
        if (alignment == Align.CENTER) currentX = - measureLineLength(string, horizontalSpacer) /2f ;
        else if (alignment == Align.RIGHT) currentX = - measureLineLength(string, horizontalSpacer);
        
        for (int i = 0; i < string.length(); i++) {
            printChar(string.charAt(i), horizontalSpacer);
        }
        
        return this;
    }
    
    public DynamicText printChar(char c) {
        return printChar(c, spacer);
    }
    
    public DynamicText printChar(char c, float horizontalSpacer) {

        boolean putSpace = false;
        if (firstCharOfLine) {
            firstCharOfLine = false;
        } else {
            currentX += horizontalSpacer;
            putSpace = true;
        }
        
        float[] region = font.getGlyphRegion(c);
        
        float charWidth = region[2] - region[0];
        float charHeight = region[3] - region[1];
        
        //height always 1
        charWidth = charWidth / charHeight;

        quadBatch.addQuadWithUV(
                currentX, 
                currentX + charWidth, 
                currentLineY, 
                currentLineY - 1, 
                region[0], 
                region[2], 
                region[1], 
                region[3]);
        
        currentX += charWidth;
        
        currentLineWidth += charWidth;
        if (putSpace) currentLineWidth += horizontalSpacer;
        if (width < currentLineWidth) width = currentLineWidth;
        
        return this;
    }
    
    public DynamicText printInteger(int value) {
        return printInteger(value, spacer);
    }
    
    private int[] tmpDigits = new int[10];
    public DynamicText printInteger(int value, float horizontalSpacer){
        int numberCharPrinted = 0;
        while (value > 0 || numberCharPrinted < 1) {        
            int digit = value % 10;
            tmpDigits[numberCharPrinted] = digit;
            numberCharPrinted++;
            value = value / 10;
        } 
        for (int i = numberCharPrinted - 1; i >= 0; i--)
            printChar( (char)('0'+tmpDigits[i]), horizontalSpacer);
        
        return this;
    }
    
    public void updateBuffers() {
        quadBatch.updateBuffers();
    }
    
    /*
    @Override
    public void update() {
        super.update();
        width = getWidth();
        height = getHeight();
    }
    */
    
    @Override
    public boolean isOutOfScreen() {
        boolean invisible =  - getWidth() / 2f > renderRight
                            || getWidth() / 2f < renderLeft;
        invisible = false;
        if (alignment == Align.LEFT) {
            invisible  = 0 > renderRight
                        || width < renderLeft;
        } 
        else if (alignment == Align.CENTER) {
            invisible  = -width * 0.5f > renderRight
                    || width * 0.5f < renderLeft;
        }
        return invisible;            
    }
    
    @Override
    public float convertToLocalX(float inX) {
        return (inX - posX) / scale / textSize;
    }
    
    @Override
    public float convertToLocalY(float inY) {
        return (inY - posY) / scale / textSize;
    }
    
    public void render() {
        super.render();
        
        if (!isVisibleOnScreen()) return;
        
        Renderer.pushMatrix();
        
        Renderer.unbindVBOs();
        Renderer.bindTexture(font);
        
        if (posX == FontTexture.ALIGN_CENTER) {
            posX = Screen.ARENA_WIDTH / 2f;
        }
        
        Renderer.translate(posX, posY, 0);
        
        Renderer.scale(textSize * scale, textSize * scale, 1f);
        
        applyColor();
        
        quadBatch.render();
        
        Renderer.resetColor();
        
        Renderer.popMatrix();
    }
    
    public float measureLineLength(String string, float spacer) {
        float result = 0;
        for (int i = 0; i < string.length(); i++) {
            
            float[] region = font.getGlyphRegion(string.charAt(i));
            float charWidth = region[2] - region[0];
            float charHeight = region[3] - region[1];
            
            //height always 1
            charWidth = charWidth / charHeight;
            
            result += charWidth;
            result += spacer;
        }
        if (result > 0) result -= spacer;
        return result;
    }
    
    public float measureCharWidth(char c) {
        float[] region = font.getGlyphRegion(c);
        return region[2] - region[0];
    }
    
    public void recenterVertically() {
        setPosY(getHeight() * 0.5f);
    }
    
    public void reAlignToRight() {
        setPosX(getWidth());
    }
    

}
