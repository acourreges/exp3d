package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.shared.Timer;

public class WorldMoveWait extends WorldMove {
  
    //Object pool
    private static final int POOL_INIT_CAPACITY = 50;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new WorldMoveWait();} } );
    
    public static WorldMoveWait newInstance() { return (WorldMoveWait)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    float duration;
    public WorldMoveWait duration(float value) { duration = value; return this; }
    
    public static WorldMoveWait applyTo(Entity entity)
    {
        WorldMoveWait move = WorldMoveWait.newInstance();
        bindTogether(entity, move);
        
        return move;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        duration = 0f;
    }
    
    public boolean updateMove() {
        
        posX = entity.posX;
        posY = entity.posY;
        
        if (duration <= 0f) return false;
        
        duration -= Timer.delta;

        return true;
    }

}
