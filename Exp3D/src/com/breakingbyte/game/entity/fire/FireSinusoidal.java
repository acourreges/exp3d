package com.breakingbyte.game.entity.fire;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.shared.Timer;

public class FireSinusoidal extends FireCyclic {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new FireSinusoidal();} } );
    
    public static FireSinusoidal newInstance() { return (FireSinusoidal)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    
    float sinusAmplitude, sinusSpeed;
    public FireSinusoidal sinus(float amplitude, float speed) { sinusAmplitude = amplitude; sinusSpeed = speed; return this; }
    
    float phase;
    public FireSinusoidal phase(float value) { phase = value; return this; }
    
    private float time;
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        sinusAmplitude = sinusSpeed = 0;
        time = phase = 0;
    }
    
    public static FireSinusoidal applyTo(Entity entity) {
        FireSinusoidal fire = FireSinusoidal.newInstance();
        bindTogether(entity, fire);
        return fire;
    }
    
    public void update() {
        super.update();
        
        time += Timer.delta;
        
        //currentAngle += angleSpeed * Timer.delta;
        
        if (currentBullet != null) {
            //Time to shoot!
            
            float perpendicularX = - currentBullet.movY;
            float perpendicularY = currentBullet.movX;
            
            final float offset = (float)Math.cos(sinusSpeed * time + phase) * sinusAmplitude;
            
            currentBullet.posX += offset  * perpendicularX;
            currentBullet.posY += offset * perpendicularY;
        }
        
    }
    
}
