package com.breakingbyte.game.ui;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class RoundGlassProgress extends Widget {
    
    public static final String TAG = "RoundGlassProgress";
    
    //Store all the vertices
    protected float[]       vertexData;
    
    //Store all the texture coordinates
    protected float[]       textureData;
    
    private float progress;
    
    public float[] color;
    
    private float time;
    public float alpha;
    
    //Foreground
    float texCoords[] = {
            0.5f, 0.5f,  //bottom left
            1.0f, 0.5f,  //bottom right
            0.5f, 0.0f,  //top left
            1.0f, 0.0f   //top right
    };
    
    
    public RoundGlassProgress() {
        
        progress = 0;
        
        color = new float[] {0.5f, 0f, 0f};
        alpha = 0.6f;
        
        time = 0f;
        
        //Background
        
        vertexData = new float[] {
            -0.5f, -0.5f,  0f,
             0.5f, -0.5f,  0f,
            -0.5f,  0.5f,  0f,
             0.5f,  0.5f,  0f
        };
        
        textureData = new float[] {
                0.5f, 1.0f,  //bottom left
                1.0f, 1.0f,  //bottom right
                0.5f, 0.5f,  //top left
                1.0f, 0.5f   //top right
        };
        
    }
    
    public void setProgress(float newProgress) {
        if (newProgress > 1.f)  {
            Log.e(TAG, "Reload progress value " + newProgress);
            newProgress = 1f;
        }
        if (newProgress < 0.f)  {
            Log.e(TAG, "Reload progress value " + newProgress);
            newProgress = 0f;
        }
        
        if (progress < 1f && newProgress == 1f) time = 0f;
        
        progress = newProgress;
    }
    
    public void setColor(float red, float green, float blue) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
    }
    
    public void update() {
        
        //progress += 0.005f;
        //if (progress > 1f) progress = 1f;
        //progress = 1f;
        
        time += Timer.delta;
        
        float visualProgress = progress * 0.92f;
        
        vertexData[7] = vertexData[10] = -0.5f + visualProgress;
        
        final float textureHeight = 0.5f;
        
        textureData[5] = textureData[7] = 1f - visualProgress * textureHeight;
        
    }
    
    public void render() {
        
        Renderer.pushMatrix();
        
        Renderer.translate(posX, posY, 0f);
        
        Renderer.scale(width, height, 0f);
        
        color[0] = 0.8f;
        color[1] = 0.2f;
        color[2] = 0.2f;
        //progress = 0.9f;
        
        if (progress < 1f) {
            Renderer.setColor(color[0], color[1], color[2], alpha);
        } else {
            //Glowing
            float greenBlueExtra = 0.5f *  (float)(1 + Math.cos(20*time)) * 0.5f;
            Renderer.setColor(color[0] + greenBlueExtra, 
                         color[1] + greenBlueExtra, 
                         color[2] + greenBlueExtra, 
                         alpha);
        }
                
        Renderer.unbindVBOs();
        TextureManager.uiLife.bind();
        
        QuadVBO.drawQuadImmediate(vertexData, textureData);
        
        //Foreground
        Renderer.resetColor();

        QuadVBO.drawQuadImmediate(0, 0, 1f, 1f, texCoords);
        
        //Overlay icon
        Renderer.scale(1.00f, 0.75f, 0f);
        TextureManager.specialIcon.bind();        
        QuadVBO.drawQuad();
        
        Renderer.popMatrix();
    }
    
    

}
