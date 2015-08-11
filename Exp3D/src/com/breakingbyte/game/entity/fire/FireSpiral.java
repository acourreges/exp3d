package com.breakingbyte.game.entity.fire;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.shared.Timer;

public class FireSpiral extends FireCyclic {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new FireSpiral();} } );
    
    public static FireSpiral newInstance() { return (FireSpiral)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    
    float angleSpeed;
    public FireSpiral angleSpeed(float angleSpeed) { this.angleSpeed = angleSpeed; return this; }
    
    int nbBranch;
    public FireSpiral branches(int number) { nbBranch = number; return this; }
    
    private float currentAngle;
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        currentAngle = 0;
    }
    
    public static FireSpiral applyTo(Entity entity) {
        FireSpiral fire = FireSpiral.newInstance();
        bindTogether(entity, fire);
        return fire;
    }
    
    public void update() {
        super.update();
        
        currentAngle += angleSpeed * Timer.delta;
        
        if (currentBullet != null) {
            //Time to shoot!
            
            for (int i = 0; i < nbBranch; i++) {
                
                //Make sure we're not short of bullet
                if (i > 0) spawnBullet();
                
                float branchAngle = currentAngle + i * 360 / nbBranch;

                currentBullet.setDirAngle(branchAngle);
            }

        }
        
    }
    
}
