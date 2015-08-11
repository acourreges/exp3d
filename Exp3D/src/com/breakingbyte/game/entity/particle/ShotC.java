package com.breakingbyte.game.entity.particle;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;

public class ShotC extends Bullet {
    
    //Settings at the pool extraction
    public static ShotC settings = new ShotC();  
    
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
            new Constructor() { public Poolable newObject(){return new ShotC();} } );
    
    public static ShotC newInstance() { return (ShotC)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    

    public static float texCoords[] = {
         0.0f, 0.25f,  //bottom left
        0.25f, 0.25f,  //bottom right
         0.0f,  0.0f,  //top left
        0.25f,  0.0f   //top right
    };
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        setValuesFrom(settings);

        clearWhenLeaveScreen = true;
        rotDirZ = 1;
        rotationSpeed = 1500f;
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
