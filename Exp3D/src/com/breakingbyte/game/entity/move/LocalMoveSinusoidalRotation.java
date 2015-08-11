package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Timer;

public class LocalMoveSinusoidalRotation extends LocalMove {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new LocalMoveSinusoidalRotation();} } );
    
    public static LocalMoveSinusoidalRotation newInstance() { return (LocalMoveSinusoidalRotation)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    private float t;
    
    private float speed;
    public LocalMoveSinusoidalRotation speed(float value) { speed = value; return this; }
    
    private float amplitudeX, amplitudeY, amplitudeZ;
    public LocalMoveSinusoidalRotation amplitude(float x, float y, float z) {
        amplitudeX = x;
        amplitudeY = y;
        amplitudeZ = z;
        return this;
    }
    
    private float idleX, idleY, idleZ;
    public LocalMoveSinusoidalRotation idle(float x, float y, float z) {
        idleX = x;
        idleY = y;
        idleZ = z;
        return this;
    }
    
    private float phase;
    public LocalMoveSinusoidalRotation phase(float value) { phase = value; return this; }

    @Override
    public void toInitValues() {
        super.toInitValues();
        t = 0;
        amplitudeX = amplitudeY = amplitudeZ = 0f;
        idleX = idleY = idleZ = 0;
        phase = 0;
    }
    
    public static LocalMoveSinusoidalRotation applyTo(Entity entity)
    {
        LocalMoveSinusoidalRotation move = LocalMoveSinusoidalRotation.newInstance();
        bindTogether(entity, move);
        return move;
    }
    
    public void updateMove() {
        t += speed * Timer.delta;
    }
    
    public void affectEntity(Entity entity) {
        entity.rotX = idleX + (float)(amplitudeX * Math.sin(t + phase));
        entity.rotY = idleY + (float)(amplitudeY * Math.sin(t + phase));
        entity.rotZ = idleZ + (float)(amplitudeZ * Math.sin(t + phase));

        //Log.d("Sinus", "Angle: " + entity.rotY + " amplitude " + amplitude + " t " + t);
    }
    
}
