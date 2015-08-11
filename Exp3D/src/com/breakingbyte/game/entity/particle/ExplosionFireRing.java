package com.breakingbyte.game.entity.particle;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;
import com.breakingbyte.wrap.shared.Timer;

public class ExplosionFireRing extends Entity {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 30;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new ExplosionFireRing();} } );
    
    public static ExplosionFireRing newInstance() { return (ExplosionFireRing)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }

    private float timeElapsed;
    
    private float duration, 
                  zoomStart, 
                  zoomEnd,
                  alphaStart,
                  alphaEnd,
                  alphaDelay;
    
    private float colorR, colorG, colorB;
    private float colorREnd, colorGEnd, colorBEnd;

    
    public ExplosionFireRing(){
        width = 1;
        height = 1;
        lifeStart = 1;
        toInitValues();
    }
    
    public static ExplosionFireRing spawn() {
        ExplosionFireRing explosion = ExplosionFireRing.newInstance();
        Engine.layer_explosions.addEntity(explosion);
        return explosion;
    }
    
    public void setup(float duration, 
                      float zoomStart, 
                      float zoomEnd,
                      float alphaStart,
                      float alphaEnd,
                      float alphaDelay) 
    {
        this.duration = duration;
        this.zoomStart = zoomStart;
        this.zoomEnd = zoomEnd;
        this.alphaStart = alphaStart;
        this.alphaEnd = alphaEnd;
        this.alphaDelay = alphaDelay;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        timeElapsed = 0;
        colorR = colorG = colorB = 1f;
        colorREnd = colorGEnd = colorBEnd = 1f;
    }
    
    public void setColor(float red, 
                         float green, 
                         float blue,
                         float redEnd,
                         float greenEnd,
                         float blueEnd) {
        this.colorR = red;
        this.colorG = green;
        this.colorB = blue;
        this.colorREnd = redEnd;
        this.colorGEnd = greenEnd;
        this.colorBEnd = blueEnd;
    }

    public void setColor(float red, float green, float blue) {
        setColor(red, green, blue, red, green, blue);
    }    
    
    @Override
    public void update() {
        super.update();
        
        timeElapsed += Timer.delta;
        
        if (timeElapsed > duration) {
            setToBeCleared(true);
            return;
        }
    }
    
    @Override
    public void render() {
        
        if (timeElapsed > duration) return;
        
        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        
        TextureManager.fireRing.bind();
        
        //zoom
        float progress = timeElapsed / duration;
        float actualZoom = zoomStart + progress * (zoomEnd - zoomStart);
        
        //color
        float red   = colorR + progress * (colorREnd - colorR);
        float green = colorG + progress * (colorGEnd - colorG);
        float blue  = colorB + progress * (colorBEnd - colorB);
        
        //alpha
        progress = timeElapsed - alphaDelay;
        if (progress < 0) progress = 0;
        progress /= duration;
        float actualAlpha = alphaStart + progress * (alphaEnd - alphaStart);
        
        Renderer.setColor(red, green, blue, actualAlpha);
        
        QuadVBO.drawQuad(posX, posY, actualZoom, actualZoom);
        
        Renderer.resetColor();
        Renderer.Blending.resetMode();
    }

}
