package com.breakingbyte.game.entity;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.particle.Sparkle;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;


public class SparkleGroup extends EntityGroupParticle {
       
    //Object pool
    private static final int POOL_INIT_CAPACITY = 20;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject() {return new SparkleGroup(); } } );
    
    public static SparkleGroup newInstance() { return (SparkleGroup)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    private SmoothJoin alphaJoin = new SmoothJoin();
    private float red, green, blue;
    
    public SparkleGroup() {
        this.MAX_CAPACITY = 16;
        this.postConstructor();
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        red = green = blue = 1f;
    }
    
    public void setLightDuration(float duration) {
        alphaJoin.setInterpolator(Interpolator.LINEAR_TIMED);
        alphaJoin.init(1f);
        alphaJoin.setTarget(0f, duration);
    }
    
    public void setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    @Override
    public boolean canBeCleared() {
        return toBeCleared;
    }
    
    @Override
    public void update() {
        
        alphaJoin.update();
        
        for (int i = entities.size()-1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update();
        }
        
        toBeCleared = (alphaJoin.get() <= 0.01f);
    }
    
    @Override
    public void render() {
        Renderer.pushMatrix();
        Renderer.translate(posX, posY, posZ);
        TextureManager.star.bind();
        Renderer.setColor(red, green, blue, alphaJoin.get());
        super.render();
        Renderer.resetColor();
        Renderer.popMatrix();
    }
    
    public final void addSparkle(float x, float y, float scale, float force, float angle, float gravity) {
        Sparkle s = Sparkle.newInstance(); 
        s.setup(x, y, scale, force, angle, gravity);
        addMember(s);
    }
    
    public void setDefaultColor() {
        setColor(170/255f, 255/255f, 255/255f);
    }
    
    public void populatePattern(boolean phase) {
        if (phase) populatePattern1(); else populatePattern2();
    }
    
    public void populatePattern1() {
        setLightDuration(1.2f);
        
        float s = 0.6f;
        setDefaultColor();
        
        addSparkle(0, 0,        s * 0.8f, 5, 3.14f, 50);
        addSparkle(0, 0,        s * 0.7f, 3, 2.0f, 20);
        addSparkle(0, 0,        s * 0.5f, 5, 2.5f, 20);
        addSparkle(-0.5f, 0,    s * 0.8f, 8, 2.5f, 40);
        addSparkle(0, 0,        s * 0.5f, 3, 0f, 20);
        addSparkle(-1, 0,       s * 0.6f, 4, 2.3f, 20);
    }
    
    public void populatePattern2() {
        setLightDuration(1.2f);
        
        float s = 0.6f;
        setDefaultColor();
        
        addSparkle(0, 0,        s * 0.8f, 5, 1.5f, 30);
        addSparkle(0, 0,        s * 0.7f, 3, 6.0f, 20);
        addSparkle(0, 0,        s * 0.5f, 5, 3.5f, 20);
        addSparkle(-0.5f, 0,    s * 0.8f, 8, 2.5f, 30);
        addSparkle(0, 0,        s * 0.5f, 3, 4f, 20);
        addSparkle(-1, 0,       s * 0.6f, 4, 2.3f, 20);
        addSparkle(-1, 0,       s * 0.8f, 9, 1.8f, 30);
    }

}
