package com.breakingbyte.game.entity.fire;


import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.particle.Bullet;
import com.breakingbyte.game.entity.particle.ShotA;
import com.breakingbyte.game.entity.particle.ShotB;
import com.breakingbyte.game.entity.particle.ShotC;
import com.breakingbyte.game.entity.particle.ShotLong;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.Log;

public abstract class Fire implements Poolable {
    
    public static final String TAG = "Fire";
    
    protected Entity entity;

    protected Bullet currentBullet;
    
    public static enum Type
    {
        NONE,
        SHOTA,
        SHOTB,
        SHOTC,
        SHOTLONG,
    };
    
    
    protected Type bulletType;
    public Fire bullet(Type bulletType) {this.bulletType = bulletType; return this;}
    
    protected boolean overrideBulletShape = false;
    protected float bulletWidth, bulletHeight, bulletAttackPowerMultplier;
    
    @Override
    public void resetState() {
        toInitValues();
    }
    
    public void toInitValues() {
        entity = null;
        overrideBulletShape = false;
    }
    
    abstract public void update();
    
    public void free() { 
        Log.e(TAG, "No pool implemented for " + this.getClass().getName());
    }
    
    public static void bindTogether(Entity entity, Fire fire){
        //if (entity.fireBehavior != null) entity.fireBehavior.free();
        entity.fireBehaviors.add(fire);
        fire.entity = entity;
    }
    
    public void reshapeBullets(float width, float height, float attackMultiplier) {
        overrideBulletShape = true;
        bulletWidth = width;
        bulletHeight = height;
        bulletAttackPowerMultplier = attackMultiplier;
    }
    
    public Bullet spawnBullet() {
        Bullet result = null;
        switch (bulletType) {
            case NONE: return null;
            case SHOTA: result = ShotA.newInstance();
                break;
            case SHOTB: result = ShotB.newInstance();
            break;
            case SHOTC: result = ShotC.newInstance();
            break;
            case SHOTLONG: result = ShotLong.newInstance();
            break;
            default: 
                Log.e(TAG, "Bullet not supported! " + bulletType);
                return null;
        }
        
        if (overrideBulletShape) {
            result.setDimension(bulletWidth, bulletHeight);
            result.attackPower *= bulletAttackPowerMultplier;
        }
        
        result.registerInLayer();
        currentBullet = result;
        return result;
    }
    

}
