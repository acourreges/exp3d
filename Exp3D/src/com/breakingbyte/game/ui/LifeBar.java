package com.breakingbyte.game.ui;

import com.breakingbyte.game.render.QuadBatch;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;

/**
 * Like a 9-patch image, but only with the central row.
 */
public class LifeBar extends Widget {
    
    public static final String TAG = "LifeBar";
    
    private static final float GROW_SPEED = 3f;
    
    private float width, height, endingWidth; //Life bar dimension
    
    private float innerWidth;
    
    private float lifeValue; // Amount of life, between 0 and 1
    private SmoothJoin lifeValueDisplay; // Tends to reach lifeValue
    
    //Color
    private SmoothJoin lifeColor;
        
    private QuadBatch quadBatch;
    private QuadBatch quadBatchBG;
    
    public LifeBar() {
        
        quadBatch = new QuadBatch(3);
        quadBatchBG = new QuadBatch(3);
        
        // Default color
        lifeColor = new SmoothJoin(4);
        lifeColor.init(UI.defaultColor[0], UI.defaultColor[1], UI.defaultColor[2], UI.defaultColor[3]);
        lifeColor.setTarget(UI.defaultColor[0], UI.defaultColor[1], UI.defaultColor[2], UI.defaultColor[3], GROW_SPEED);
        
        lifeValueDisplay = new SmoothJoin();
        lifeValueDisplay.init(0f);
        setLife(1f);

    }
    
    public void reset() {
        lifeValueDisplay.init(0f);
    }
    
    public void setup(float width, float height, float endingWidth){
        this.width = width;
        this.height = height;
        this.endingWidth = endingWidth;
        this.innerWidth = width - 2*endingWidth;
        updateBatches(false);
        updateBatches(true);
    }
    
    public void setLife(float life) {
        if (life > 1.f)  {
            Log.e(TAG, "Life value " + life);
            life = 1f;
        }
        if (life < 0.f)  {
            Log.e(TAG, "Life value " + life);
            life = 0f;
        }
        
        if (lifeValue == life) return;
        boolean loss = lifeValue > life;
        lifeValue = life;
        lifeValueDisplay.setTarget(lifeValue, GROW_SPEED);
        if (loss) lifeColor.init(1f, 0.25f, 0.25f, 0.85f);
        else lifeColor.init(0f, 1f, 0f, 0.85f);
    }
    
    
    public void update() {
        
        lifeColor.update();
        
        if (lifeValueDisplay.update()) {
            updateBatches(true);
        }
    
    }
    
    public void render() {
        Renderer.pushMatrix();
        Renderer.unbindVBOs();
        
        TextureManager.uiLife.bind();
        
        Renderer.translate(posX, posY, 0f);
        
        Renderer.setColor(lifeColor.get(0), lifeColor.get(1), lifeColor.get(2), lifeColor.get(3));
        
        quadBatchBG.render();
        
        Renderer.resetColor();
        quadBatch.render();
    
        Renderer.popMatrix();
    }
    
    private void updateBatches(boolean isBG) {
        
        QuadBatch qb = isBG? quadBatchBG : quadBatch;
        
        final float startingOffset = isBG? innerWidth * (1 - lifeValueDisplay.get()) : 0f;
        
        final float rightDelimX = width - endingWidth;
        
        float leftDelimX = startingOffset + endingWidth;
        if (leftDelimX > rightDelimX) leftDelimX = rightDelimX;
        
        final float yShift = isBG? 0.5f : 0f;
        
        final float rightDelimU = 0.3f;
        
        float leftDelimU = 0.2f;
        
        qb.clearBatch();
        
        //Left square
        qb.addQuadWithUV(
                startingOffset, 
                leftDelimX, 
                height, 
                0, 
                0, 
                leftDelimU, 
                yShift, 
                yShift + 0.5f);
        
        //Middle square - adapts the width if necessary
        qb.addQuadWithUV(
                leftDelimX, 
                rightDelimX, 
                height, 
                0, 
                leftDelimU, 
                rightDelimU, 
                yShift, 
                yShift + 0.5f);
        
        //Right square
        qb.addQuadWithUV(
                rightDelimX, 
                width, 
                height, 
                0, 
                rightDelimU, 
                0.5f, 
                yShift, 
                yShift + 0.5f);
        
        qb.updateBuffers();
    }

}
