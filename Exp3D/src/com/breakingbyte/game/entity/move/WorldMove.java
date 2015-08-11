package com.breakingbyte.game.entity.move;


import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.Log;

public abstract class WorldMove implements Poolable {

    public static final String TAG = "WorldMove";
    
    protected Entity entity;
    
    abstract public boolean updateMove();
    
    protected float posX, posY, posZ;

    public void free() { 
        Log.e(TAG, "No pool implemented for " + this.getClass().getName());
    }
    
    public static void bindTogether(Entity entity, WorldMove move){
        if (entity.worldMoveBehavior != null) entity.worldMoveBehavior.free();
        entity.worldMoveBehavior = move;
        move.entity = entity;
    }
    
    @Override
    public void resetState() {
        toInitValues();
    }
    
    public void toInitValues() {
        entity = null; 
    }
    
    public boolean update() {
        boolean result = updateMove();
        affectEntity(entity);
        return result;
    }
    
    public void affectEntity(Entity e) {
        e.posX = posX;
        e.posY = posY;
        e.posZ = posZ; 
    }
    
}
