package com.breakingbyte.game.entity.particle;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;

public class ShotA extends Bullet {
    
    //Settings at the pool extraction
    public static ShotA settings = new ShotA();    
    
    //Default settings
    static { 
        settings.lifeStart = 1;
        settings.width = 3;
        settings.height = 3;
        settings.zoomTarget = 1.2f;
        settings.attackPower = 100;
    }
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 300;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new ShotA();} } );
    
    public static ShotA newInstance() { return (ShotA)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    

    public static float texCoords[] = {
        0.0f, 0.5f,  //bottom left
       0.25f, 0.5f,  //bottom right
        0.0f,  0.25f,  //top left
       0.25f,  0.25f   //top right
   };

    
    @Override
    public void toInitValues() {
        super.toInitValues();
        setValuesFrom(settings);
        
        clearWhenLeaveScreen = true;
        rotDirZ = 1;
        rotationSpeed = 4000f;
    }
    
    @Override
    public void registerInLayer(){
        Engine.enemyBullets.addMember(this);
    }
       
    @Override
    public float[] getTextureArray() {
        return texCoords;
    }

}
