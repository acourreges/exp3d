package com.breakingbyte.game.entity.particle;


import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.shared.Timer;

/**
 * Simple particle with an original strength applied to it, and then only affected by gravity.
 */

public class Sparkle extends Entity {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 120;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Sparkle();} } );
    
    public static Sparkle newInstance() { return (Sparkle)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
        
    public static float texCoords[] = {
        0.0f,  1.0f,  //bottom left
        1.0f,  1.0f,  //bottom right
        0.0f,  0.0f,  //top left
        1.0f,  0.0f   //top right
    };
    
    private float elapsed;

    public float initialSpeedY;
    
    public float gravity;
    
    public Sparkle(){          
        width = 1;
        height = 1;
        attackPower = 200;
        lifeStart = 1;
        
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        scale = 1;
        
        elapsed = 0;
        initialSpeedY = 0;
        gravity = 0f;
    }
    
    public void setup(float x, float y, float scale, float force, float angle, float gravity) {
        this.posX = x;
        this.posY = y;
        this.scale = scale;
        
        this.movX = (float)Math.cos(angle) * force;
        this.movY = (float)Math.sin(angle) * force;
        
        this.gravity = gravity;
        this.initialSpeedY = this.movY;
        this.moveSpeed = 1f;
    }
    
    @Override
    public float[] getTextureArray() {
        return texCoords;
    }
    
    @Override
    public void update() {
        elapsed += Timer.delta;
        
        movY = -gravity * elapsed + initialSpeedY;
        super.update();
    }
    


}
