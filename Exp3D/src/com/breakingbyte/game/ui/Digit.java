package com.breakingbyte.game.ui;


import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;

public class Digit extends Widget {
    
    private static final String TAG = "Digit";
    
    private int lifeValueDisplay;
    private int lifeValue;
    
    //Color
    private SmoothJoin lifeColor;
    private SmoothJoin zoomer;
    private static final float GROW_SPEED = 2f;
    
    //Store all the texture coordinates
    protected float[]       textureData;
    
    public Digit() {
        
        lifeValueDisplay = 0;
        lifeValue = 0;
        
        createTextureBuffer();        
        updateTextureBuffer(0);
        
        lifeColor = new SmoothJoin(4);
        lifeColor.init(0f, 1f, 1f, 1f);
        lifeColor.setTarget(1f, 1f, 1f, 1f, GROW_SPEED);
       
        zoomer = new SmoothJoin();
        zoomer.init(1.5f);
        zoomer.setTarget(1f, GROW_SPEED);
    }
    
    public void setLifeValue(int value, boolean animated) {
        if (value < 0 || value > 10) {
            Log.e(TAG, value + " is out of range [0,9]!");
            value = 0;
        }

        lifeValue = value;
        
        if (animated) {
            zoomer.init(1.5f);
            lifeColor.init(0f, 0f, 0f, 0f);
        }
    }
    
    public void update() {
        if (lifeValue != lifeValueDisplay) updateTextureBuffer(lifeValue);
        
        lifeColor.update();
        zoomer.update();
    }
    
    private void createTextureBuffer() {
        final int NB_POINTS = 4;
        textureData = new float[NB_POINTS*2];
    }
    
    public void render() {
        
        Renderer.pushMatrix();
        Renderer.unbindVBOs();
        
        Renderer.bindTexture(TextureManager.ui_digit);
        
        Renderer.setColor(lifeColor.get(0), lifeColor.get(1), lifeColor.get(2), lifeColor.get(3));
        
        QuadVBO.drawQuadImmediate(posX, posY, width * zoomer.get(), height * zoomer.get(), textureData);
        
        Renderer.resetColor();
        
        Renderer.popMatrix();
    }
    
    
    public void updateTextureBuffer(int digitValue) {
       
        int index = 0;
        
        int i = digitValue;
        
        float topLeftX = (i % 4) * 0.25f;
        float topLeftY = (i / 4) * 0.25f;
        float bottomRightX = topLeftX + 0.25f;
        float bottomRightY = topLeftY + 0.25f;
        
        //Left square
        {
            //Bottom left
            textureData[ index++ ] =  topLeftX;
            textureData[ index++ ] =  bottomRightY;
            
            //Bottom right
            textureData[ index++ ] = bottomRightX;
            textureData[ index++ ] = bottomRightY;
            
            //Top Left
            textureData[ index++ ] = topLeftX;
            textureData[ index++ ] = topLeftY;
            
            //Top right
            textureData[ index++ ] = bottomRightX;
            textureData[ index++ ] = topLeftY;
        }
        
        lifeValueDisplay = digitValue;
    }
    
    
}
