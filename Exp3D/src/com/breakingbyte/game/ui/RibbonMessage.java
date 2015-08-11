package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;

public class RibbonMessage extends Widget {
    
    public static final String TAG = "RibbonMessage";

    protected float height;
    
    protected SmoothJoin zoomer;
    
    protected float appearSpeed, disappearSpeed;
    
    //Background RGBA
    protected float[] bgColor;
    protected int bgRepeat;
    protected float bgSpeedAnimation;
    
    protected float t;
    
    protected boolean appearing;
    protected boolean finishedAppear;
    
    float bgTexCoords[] = {
            0.0f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            0.0f, 0.0f,  //top left
            1.0f, 0.0f   //top right
    };
    
    public RibbonMessage() {
        zoomer = new SmoothJoin();
        zoomer.init((float)1e-9);
        bgColor = new float[4];
        t = 0f; 
        appearing = false;
        
        posY = Screen.ARENA_HEIGHT * 0.7f;
        bgColor[0] = 1f;
        bgColor[1] = 0f;
        bgColor[2] = 0f;
        bgColor[3] = 1f;
        height = 40f;
        appearSpeed = 5f;
        disappearSpeed = 6f;
        bgSpeedAnimation = 8f;
        bgRepeat = 2;
    }
    
    public void reset() {
        appearing = false;
        zoomer.init((float)1e-9);
        zoomer.setTarget((float)1e-9, 0f);
    }
    
    public boolean isVisible() {
        return zoomer.get() > 0.001f;
    }
    
    public void startAppearAnimation() {
        t = 0f;
        finishedAppear = false;
        appearing = true;
        zoomer.init((float)1e-9);
        zoomer.setTarget(1.0f, appearSpeed);
    }
    
    public void startDisappearAnimation() {
        appearing = false;
        zoomer.init(zoomer.get());
        zoomer.setTarget((float)1e-9, disappearSpeed);
    }
    
    public void update() {
        if (!appearing && !isVisible()) return;
        zoomer.update();
        t += Timer.delta;
        if (appearing && !finishedAppear && zoomer.get() > 0.6f) {
            finishedAppear = true;
            t = 0;
        }
        
        float offset = -bgSpeedAnimation*t;
        
        bgTexCoords[0] = offset + bgRepeat;         bgTexCoords[1] = 1f; //bottom left
        bgTexCoords[2] = offset + bgRepeat;         bgTexCoords[3] = 0.9f; //bottom right
        bgTexCoords[4] = offset;                    bgTexCoords[5] = 1f; //top left
        bgTexCoords[6] = offset;                    bgTexCoords[7] = 0.9f; //top right
        
    }
    
    public void render() {
        
        if (!isVisible()) return;
        
        Renderer.pushMatrix();
        
        Renderer.setColor(bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
        
        Renderer.unbindVBOs();
        //Renderer.setColor(1f,0f,0f,1f);
        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        TextureManager.ribbon.bind();
        QuadVBO.drawQuadImmediate(Screen.ARENA_WIDTH / 2f, posY, Screen.ARENA_WIDTH, height * zoomer.get(), bgTexCoords);
        Renderer.Blending.resetMode();
        Renderer.resetColor();
        
        Renderer.popMatrix();
    }
    
}
