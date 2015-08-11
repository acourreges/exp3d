package com.breakingbyte.game.ui;

import com.breakingbyte.game.render.QuadBatch;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.wrap.shared.Renderer;

/**
 * Like a 9-patch image, but only with the central row.
 */
public class LoadingBar extends Widget {
    
    public static final String TAG = "LoadBar";
    
    private float endingWidth; //Life bar dimension
    
    private float innerWidth;
    
    private float loadValue; // Amount of life, between 0 and 1
        
    private QuadBatch quadBatch;
    private QuadBatch quadBatchBG;
    
    private float[] bgColor = new float[]{1f, 1f, 1f};
    
    public LoadingBar() {
        
        quadBatch = new QuadBatch(3);
        quadBatchBG = new QuadBatch(3);
        
        setBgColor(0.0f,0.36f,0.566f);
        
        setLoading(1f);

    }
    
    public void setBgColor(float red, float green, float blue) {
        bgColor[0] = red;
        bgColor[1] = green;
        bgColor[2] = blue;
    }

    
    public void setup(float width, float height, float endingWidth){
        this.width = width;
        this.height = height;
        this.endingWidth = endingWidth;
        this.innerWidth = width - 2*endingWidth;
        updateBatches(false);
        updateBatches(true);
    }
    
    public void setLoading(float life) {
        loadValue = life;
        updateBatches(true);
    }
    
    @Override
    public boolean isOutOfScreen() {
        boolean invisible =    0 > renderRight
                            || width < renderLeft
                            || height < renderBottom
                            || 0 > renderTop;
        //invisible = false;
        return invisible;
                
    }
    
    public void render() {
        
        if (!isVisibleOnScreen()) return;
        
        Renderer.pushMatrix();
        Renderer.unbindVBOs();
        
        TextureManager.uiLife.bind();
        
        Renderer.translate(posX, posY, 0f);
        
        setColor(bgColor);
        setAlpha(0.85f);
        applyColor();
        
        quadBatchBG.render();
        
        setColor(1f, 1f, 1f);
        setAlpha(1f);
        applyColor();
        quadBatch.render();
    
        Renderer.resetColor();
        
        Renderer.popMatrix();
    }
    
    private void updateBatches(boolean isBG) {
        
        QuadBatch qb = isBG? quadBatchBG : quadBatch;
        
        final float startingOffset = isBG? innerWidth * (1 - loadValue) : 0f;
        
        final float rightDelimX = width - endingWidth;
        
        float leftDelimX = startingOffset + endingWidth;
        if (leftDelimX > rightDelimX) leftDelimX = rightDelimX;
        
        final float yShift = isBG? 0.5f : 0f;
        
        final float rightDelimU = 0.3f;
        
        float leftDelimU = 0.2f;
        
        qb.clearBatch();
        
        //Left square
        qb.addQuadWithUV(
                0,//startingOffset, 
                endingWidth, //leftDelimX, 
                height, 
                0, 
                0, 
                leftDelimU, 
                yShift, 
                yShift + 0.5f);
        
        //Middle square - adapts the width if necessary
        qb.addQuadWithUV(
                endingWidth, //leftDelimX, 
                width - endingWidth - startingOffset,//rightDelimX, 
                height, 
                0, 
                leftDelimU, 
                rightDelimU, 
                yShift, 
                yShift + 0.5f);
        
        //Right square
        qb.addQuadWithUV(
                width - endingWidth - startingOffset,//rightDelimX, 
                width - startingOffset, //width, 
                height, 
                0, 
                rightDelimU, 
                0.5f, 
                yShift, 
                yShift + 0.5f);
        
        qb.updateBuffers();
    }

}
