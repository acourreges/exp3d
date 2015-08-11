package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Timer;

public class WorldMoveBezierQuadratic extends WorldMove {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 40;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new WorldMoveBezierQuadratic();} } );
    
    public static WorldMoveBezierQuadratic newInstance() { return (WorldMoveBezierQuadratic)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
   
    float t;
    
    float speed;
    public WorldMoveBezierQuadratic speed(float value) { speed = value; return this; }
    
    float p0_x;
    float p0_y;
    public WorldMoveBezierQuadratic pt0(float pt0x, float pt0y) { p0_x = pt0x; p0_y = pt0y; return this; }
    
    float p1_x;
    float p1_y;
    public WorldMoveBezierQuadratic pt1(float pt1x, float pt1y) { p1_x = pt1x; p1_y = pt1y; return this; }
    
    float p2_x;
    float p2_y;
    public WorldMoveBezierQuadratic pt2(float pt2x, float pt2y) { p2_x = pt2x; p2_y = pt2y; return this; }
    
    public static WorldMoveBezierQuadratic applyTo( Entity entity )
    {
        WorldMoveBezierQuadratic move = WorldMoveBezierQuadratic.newInstance();
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
        
               posX = (1 - t_) 
                        * ( (1-t_) * p0_x + (t_) * p1_x )
                      +
                      ( t_ )
                        * ( (1-t_) * p1_x + (t_) * p2_x  )
                      ;
        
               posY = (1 - t_) 
                        * ( (1-t_) * p0_y + (t_) * p1_y )
                      +
                      ( t_ )
                        * ( (1-t_) * p1_y + (t_) * p2_y  )
                      ;
        
        return true;  
    }
    
}
