package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;

public class WorldMoveSmooth extends WorldMove {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 40;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new WorldMoveSmooth();} } );
    
    public static WorldMoveSmooth newInstance() { return (WorldMoveSmooth)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
   
    float t;
    
    float speed;
    public WorldMoveSmooth speed(float value) { speed = value; return this; }
    
    float p0_x;
    float p0_y;
    public WorldMoveSmooth pt0(float pt0x, float pt0y) { p0_x = pt0x; p0_y = pt0y; return this; }
    
    float p1_x;
    float p1_y;
    public WorldMoveSmooth pt1(float pt1x, float pt1y) { p1_x = pt1x; p1_y = pt1y; return this; }
    
    float delay;
    public WorldMoveSmooth delay(float value){ delay = value; return this; }
    
    public SmoothJoin smoother = new SmoothJoin(2);
    
    public static WorldMoveSmooth applyTo(Entity entity)
    {
        WorldMoveSmooth move = WorldMoveSmooth.newInstance();
        bindTogether(entity, move);
        
        return move;
    }
    
    public WorldMoveSmooth interp(Interpolator interpolator) {
        smoother.setInterpolator(interpolator);
        return this;
    }
    
    private boolean initialized = false;
    private void initIfNecessary() {
        if (initialized) return;
        initialized = true;
        smoother.initAt(0, p0_x);
        smoother.initAt(1, p0_y);
        
        smoother.setTargetAt(0, p1_x, speed, delay);
        smoother.setTargetAt(1, p1_y, speed, delay);
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        t = 0f;
        interp(Interpolator.ASYMPTOTIC);
        delay = 0;
        initialized = false;
    }
    
    public boolean updateMove() {
        
        initIfNecessary();
        
        if (!smoother.update()) return false;
        
        posX = smoother.get(0);
        posY = smoother.get(1);
        
        return true;  
    }
    
}
