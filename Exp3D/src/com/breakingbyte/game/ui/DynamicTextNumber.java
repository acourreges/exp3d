package com.breakingbyte.game.ui;

import java.util.ArrayList;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.BitmapFont;

public class DynamicTextNumber extends DynamicText {

    //Just to avoid creating a lot of int->char conversion
    private static ArrayList<Character> digitString; 
    
    static {
        digitString = new ArrayList<Character>(10);
        for (int i = 0; i < 10; i++) digitString.add((""+i).charAt(0));
    }
    
    //The value displayed by the counter
    public SmoothJoin value;

    //Monospace characters, looks better when the digits are "rolling"
    public boolean monospaceDigits;
    
    public int minimumNumberOfDigits;
    
    private int lastDisplayedValue;
    
    public DynamicTextNumber(int maxCharacters) {
        this(maxCharacters, TextureManager.defaultFont);
    }
    
    public DynamicTextNumber(int maxCharacters, BitmapFont font) {
        super(maxCharacters, font);
        value = new SmoothJoin();
        value.setInterpolator(Interpolator.SINUSOIDAL);
        value.init(0);
        lastDisplayedValue = -1;
        spacer = -0.06f;
        monospaceDigits = true;
        minimumNumberOfDigits = 1;
    }
    
    @Override
    public void update() {
        super.update();
        value.update();
    }
    
    @Override
    public void render() {
        int intValue = MathUtil.roundedUpInt(value.get());
        if (intValue != lastDisplayedValue) {
            updateBufferToMatchValue(intValue);
            lastDisplayedValue = intValue;
        }
        super.render();
    }
    
    private void updateBufferToMatchValue(int value) {
        reset();
        currentX = 0;
        currentY = 0;
        firstCharOfLine = true;
        numberCharPrinted = 0;
        while (value > 0 || numberCharPrinted < minimumNumberOfDigits) {        
            int digit = value % 10;
            printDigit( (char)('0'+digit), spacer);
            numberCharPrinted++;
            value = value / 10;
        }  
        updateBuffers();
    }
    
    float currentX = 0;
    float currentY = 0;
    float numberCharPrinted = 0;
    boolean firstCharOfLine = true;
    
    private void printDigit(Character c, float horizontalSpacer) {
        
        if (monospaceDigits) {
            //Dirty hack, but monospace is really needed to be visually good
            currentX = - numberCharPrinted * 0.5f;
            if (c == '1') currentX -= 0.15f;
            width = (numberCharPrinted + 1) * 0.5f;
        } else {
            if (firstCharOfLine) {
                firstCharOfLine = false;
            } else {
                currentX -= horizontalSpacer;
            }
        }
        
        float[] region = font.getGlyphRegion(c);
        
        float charWidth = region[2] - region[0];
        float charHeight = region[3] - region[1];
        
        //height always 1
        charWidth = charWidth / charHeight;

        quadBatch.addQuadWithUV(
                currentX - charWidth, 
                currentX, 
                currentY, 
                currentY - 1, 
                region[0], 
                region[2], 
                region[1], 
                region[3]);
        
        currentX -= charWidth;
        if (!monospaceDigits) width = -currentX;
    }
    
}
