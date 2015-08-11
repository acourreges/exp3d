package com.breakingbyte.game.entity.particle;


import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;

public class SimpleBlast extends Entity {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 100;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new SimpleBlast();} } );
    
    public static SimpleBlast newInstance() { return (SimpleBlast)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public static final int BASE_DAMAGE = 200;
    public static int CURRENT_DAMAGE = 0; //Modified by bonus purchased in shop
    
    public static float texCoords[] = {
        0.0f,  1.0f,  //bottom left
        0.25f, 1.0f,  //bottom right
        0.0f,  0.0f,  //top left
        0.25f, 0.0f   //top right
    };
    
    public SimpleBlast(){          
        width = 5;
        height = 20;
        attackPower = BASE_DAMAGE;
        lifeStart = 1;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        this.attackPower = CURRENT_DAMAGE;
    }
       
    @Override
    public float[] getTextureArray() {
        return texCoords;
    }
    
    @Override
    public void update() {
        super.update();
    }
    
    @Override
    public void receiveDamageFrom(Entity entity, int amount) {
        super.receiveDamageFrom(entity, amount);
        ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=posX; ex.posY = posY + height * 0.35f;
        ex.setup(0.2f, 3f, 9f, 0.8f, 0f, 0.0f);
        ex.setColor(0.65f,0.65f,1f);
        AudioManager.playSound(SoundId.BULLET_IMPACT);
    }

}
