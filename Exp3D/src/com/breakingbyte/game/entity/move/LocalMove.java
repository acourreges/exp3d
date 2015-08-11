package com.breakingbyte.game.entity.move;


import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Timer;

public abstract class LocalMove implements Poolable {

    public static final String TAG = "LocalMove";
    
    protected Entity entity;
    
    abstract public void updateMove();

    public void free() { 
        Log.e(TAG, "No pool implemented for " + this.getClass().getName());
    }
    
    protected float delay, duration;
    
    public static void bindTogether(Entity entity, LocalMove move){
        if (entity.localMoveBehavior != null) entity.localMoveBehavior.free();
        entity.localMoveBehavior = move;
        move.entity = entity;
    }
    
    @Override
    public void resetState() {
        toInitValues();
    }
    
    public void toInitValues() {
        entity = null;
        delay = 0f;
        duration = 0f;
    }
    
    public boolean update() {
        if (delay < 0) {
            delay -= Timer.delta;
        } else updateMove();
        affectEntity(entity);
        
        duration -= Timer.delta;
        return (duration >= 0);
        
    }
    
    public void affectEntity(Entity entity) {}
    
}
