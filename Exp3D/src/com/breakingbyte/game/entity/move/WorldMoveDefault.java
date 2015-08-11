package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Timer;

public class WorldMoveDefault extends WorldMove {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new WorldMoveDefault();} } );
    
    public static WorldMoveDefault newInstance() { return (WorldMoveDefault)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
   
    
    public static void applyTo(Entity entity) {
        WorldMoveDefault move = WorldMoveDefault.newInstance();
        move.entity = entity;
        bindTogether(entity, move);
    }
    
    public boolean updateMove() {
        //return updateEntity(entity);
        
        posX = entity.posX +  entity.movX * Timer.delta * entity.moveSpeed;
        posY = entity.posY +  entity.movY * Timer.delta * entity.moveSpeed;
        posZ = entity.posZ +  entity.movZ * Timer.delta * entity.moveSpeed;

        return true;
    }
    
    public static boolean updateEntity(Entity entity) {
        
        entity.posX = entity.posX +  entity.movX * Timer.delta * entity.moveSpeed;
        entity.posY = entity.posY +  entity.movY * Timer.delta * entity.moveSpeed;
        entity.posZ = entity.posZ +  entity.movZ * Timer.delta * entity.moveSpeed;

        return true;
    }
    
}
