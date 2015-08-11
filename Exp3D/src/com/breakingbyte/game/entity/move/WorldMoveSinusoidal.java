package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.shared.Timer;

public class WorldMoveSinusoidal extends WorldMove {
  
    //Object pool
    private static final int POOL_INIT_CAPACITY = 50;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new WorldMoveSinusoidal();} } );
    
    public static WorldMoveSinusoidal newInstance() { return (WorldMoveSinusoidal)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    float t;
    
    float speed;
    public WorldMoveSinusoidal speed(float value) { speed = value; return this; }
    
    boolean isVertical;
    public WorldMoveSinusoidal vertical(boolean value) { isVertical = value; return this; }
    
    float nbOscillations;
    public WorldMoveSinusoidal nbOsc(float value) { nbOscillations = value; return this; }
    
    float p0_x;
    float p0_y;
    public WorldMoveSinusoidal pt0(float pt0x, float pt0y) { p0_x = pt0x; p0_y = pt0y; return this; }
    
    float p1_x;
    float p1_y;
    public WorldMoveSinusoidal pt1(float pt1x, float pt1y) { p1_x = pt1x; p1_y = pt1y; return this; }
    
    public static WorldMoveSinusoidal applyTo(Entity entity)
    {
        WorldMoveSinusoidal move = WorldMoveSinusoidal.newInstance();
        bindTogether(entity, move);
        
        return move;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        t = 0f;
    }
    
    public boolean updateMove() {
        
        if (t >= 1f) return false;
        
        t += speed * Timer.delta;
        if (t >= 1f) t = 1f;
        
        final float t_ = t;
        
        if (isVertical) {
            posX = p0_x + 
                           (p1_x - p0_x)/2f *
                           (float)(1 + Math.sin(-MathUtil.PI * 0.5f + MathUtil.TWO_PI * nbOscillations * t_));
            posY = p0_y + t_ * (p1_y - p0_y);
        } else {
            posX = p0_x + t_ * (p1_x - p0_x);
            posY = p0_y + 
                           (p1_y - p0_y)/2f *
                           (float)(1 + Math.sin(-MathUtil.PI * 0.5f + MathUtil.TWO_PI * nbOscillations * t_));
        }
        
        return true;
    }

}
