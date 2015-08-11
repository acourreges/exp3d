package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Timer;

public class LocalMoveDefault extends LocalMove {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new LocalMoveDefault();} } );
    
    public static LocalMoveDefault newInstance() { return (LocalMoveDefault)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    private float rotX, rotY, rotZ;
    
    public static void applyTo(Entity entity) {
        LocalMoveDefault move = LocalMoveDefault.newInstance();
        bindTogether(entity, move);
    }
    
    public void updateMove() {      
        //updateEntity(this.entity);
        
        rotX = entity.rotX + entity.rotDirX * Timer.delta * entity.rotationSpeed;
        rotY = entity.rotY + entity.rotDirY * Timer.delta * entity.rotationSpeed;
        rotZ = entity.rotZ + entity.rotDirZ * Timer.delta * entity.rotationSpeed;
        
    }
    
    public static void updateEntity(Entity entity) {
        entity.rotX = entity.rotX + entity.rotDirX * Timer.delta * entity.rotationSpeed;
        entity.rotY = entity.rotY + entity.rotDirY * Timer.delta * entity.rotationSpeed;
        entity.rotZ = entity.rotZ + entity.rotDirZ * Timer.delta * entity.rotationSpeed;
    }
    
    public void affectEntity(Entity entity) {
        entity.rotX = rotX;
        entity.rotY = rotY;
        entity.rotZ = rotZ;
    }

}
