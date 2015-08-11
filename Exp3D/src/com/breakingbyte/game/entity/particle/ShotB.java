package com.breakingbyte.game.entity.particle;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;

public class ShotB extends Bullet {
    
    //Settings at the pool extraction
    public static ShotB settings = new ShotB();  
    
    //Default settings
    static { 
        settings.lifeStart = 1;
        settings.width = 2.5f;
        settings.height = 2.5f;
        settings.zoomTarget = 3f;
        settings.attackPower = 100;
    }
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 200;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new ShotB();} } );
    
    public static ShotB newInstance() { return (ShotB)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public static float texCoords[] = {
        0.5f,  0.5f,  //bottom left
        1.0f,  0.5f,  //bottom right
        0.5f,  0.0f,  //top left
        1.0f,  0.0f,  //top right
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
