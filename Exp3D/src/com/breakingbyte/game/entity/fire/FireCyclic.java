package com.breakingbyte.game.entity.fire;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.Player;
import com.breakingbyte.game.entity.particle.Bullet;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Timer;

public class FireCyclic extends Fire {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 40;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new FireCyclic();} } );
    
    public static FireCyclic newInstance() { return (FireCyclic)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public static final float TARGET_PLAYER = Float.NEGATIVE_INFINITY;
    public static final float BYROL_LATERAL = Float.NEGATIVE_INFINITY + 1f;

    float delay;
    public FireCyclic delay(float value) { delay = value; toWait = delay; return this; }
    
    int nbShoots;
    float shootPeriod;    
    float repeatEvery;    
    public FireCyclic shoot(int number, float period, float repeatEvery) { 
        nbShoots = number; shootPeriod = period; this.repeatEvery = repeatEvery; return this; 
    }
    
    float bulletSpeed;
    public FireCyclic speed(float bulletSpeed) { this.bulletSpeed = bulletSpeed; return this; }
    
    float angleToShoot;
    boolean relativeAngle;
    public FireCyclic angle(float angleToShoot, boolean relativeAngle) { 
        this.angleToShoot = angleToShoot; this.relativeAngle = relativeAngle; return this; 
    }
    public FireCyclic angleToPlayer() { return angle(TARGET_PLAYER, false); } //target automatically the player
    
    boolean usePulse;
    float pulseAngle, pulseStrength;
    boolean pulseAngleRelative;
    public FireCyclic pulse(float initialAngle, boolean relativeAngle, float pulseStrength) {
        this.usePulse = true;
        this.pulseAngle = initialAngle; this.pulseAngleRelative = relativeAngle; this.pulseStrength = pulseStrength; return this;
    }
    
    float toWait;
    int shotDone;

    float offsetLateral = 0f;
    float offsetFront = 0f;
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        relativeAngle = false;
        angleToShoot = TARGET_PLAYER;
        shotDone = 0;
        offsetLateral = 0f;
        offsetFront = 5f;
        delay = toWait = 0;
        usePulse = false;
    }
    
    public static FireCyclic applyTo(Entity entity) {
        FireCyclic fire = FireCyclic.newInstance();
        bindTogether(entity, fire);
        return fire;
    }
    
    public FireCyclic setOffsetLateral(float value) {
        if (value == BYROL_LATERAL) value = 6f;
        else if (value == -BYROL_LATERAL) value = -6f;
        offsetLateral = value;
        return this; 
    }
    
    public FireCyclic setOffsetFront(float value)   { offsetFront = value; return this; }
    
    public FireCyclic offsets(float lateral, float front) {
        setOffsetLateral(lateral);
        setOffsetFront(front);
        return this;
    }
    
    public void update() {
        
        currentBullet = null;
        
        if (toWait > 0) toWait -= Timer.delta;
        if (toWait > 0) return;
        
        if (shotDone < nbShoots) {
            //Spawn bullet
            spawnBullet();
            
            shotDone += 1;
            if (shotDone < nbShoots) 
            {
                toWait += shootPeriod;
            }
            else { //last one
                toWait += repeatEvery;
                shotDone = 0;
            }
        }

        
    }
    
    public Bullet spawnBullet() {
        super.spawnBullet();
        Bullet bullet = currentBullet;
        
        bullet.moveSpeed = bulletSpeed;
        //Extend in the direction of the entity
        bullet.posX = entity.posX + offsetFront*(float)Math.cos((-90+entity.rotZ)*MathUtil.degreesToRadians) + -offsetLateral*(float)Math.cos((entity.rotZ)*MathUtil.degreesToRadians);
        bullet.posY = entity.posY + offsetFront*(float)Math.sin((-90+entity.rotZ)*MathUtil.degreesToRadians) + -offsetLateral*(float)Math.sin((entity.rotZ)*MathUtil.degreesToRadians);
        
        float directionAngle = angleToShoot;
        if (directionAngle == TARGET_PLAYER) {
            Player player = Engine.player;
            directionAngle = MathUtil.radiansToDegrees*MathUtil.getAngleBetween(bullet.posX, bullet.posY, 0f, player.posX, player.posY);
        }
        else if (relativeAngle) directionAngle += -90 + entity.rotZ;
        
        bullet.setDirAngle(directionAngle);
        
        if (usePulse){
            float initPulseAngle = this.pulseAngle;
            if (this.pulseAngleRelative) initPulseAngle += -90 + entity.rotZ;
            bullet.setPulse(MathUtil.degreesToRadians * initPulseAngle, this.pulseStrength, Engine.player.posX, Engine.player.posY, bulletSpeed);
            bullet.moveSpeed = 1f;
        }
        
        return bullet;
    }

}
