package com.breakingbyte.game.entity.particle;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;

public class ShotLong extends Bullet {
    
    //Settings at the pool extraction
    public static ShotLong settings = new ShotLong();   
    
    //Default settings
    static { 
        settings.lifeStart = 1;
        settings.width = 5;
        settings.height = 10;
        settings.zoomTarget = 2.5f;
        settings.attackPower = 100;
    }
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 100;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new ShotLong();} } );
    
    public static ShotLong newInstance() { return (ShotLong)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public static float texCoords[] = {
       0.25f, 0.5f,  //bottom left
        0.5f, 0.5f,  //bottom right
       0.25f,   0f,  //top left
        0.5f,   0f   //top right
   };
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        setValuesFrom(settings);
        
        clearWhenLeaveScreen = true;
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
