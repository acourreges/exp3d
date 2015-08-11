package com.breakingbyte.game.entity.particle;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.PackingTexture;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.wrap.shared.Timer;

public class Explosion extends Entity {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 30;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Explosion();} } );
    
    public static Explosion newInstance() { return (Explosion)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    private PackingTexture packingTexture;
        
    public float texCoords[] = {
        0.0f, 0.0f,  //bottom left
        0.0f, 0.0f,  //bottom right
        0.0f, 0.0f,  //top left
        0.0f, 0.0f   //top right
    };
    
    public Explosion(){          
        width = 80;
        height = 80;
        lifeStart = 1;
        packingTexture = new PackingTexture(4, 4, 15);
    }
    
    public static Explosion spawn() {
        Explosion explosion = Explosion.newInstance();
        explosion.setDimension(80, 80);
        Engine.explosions.addMember(explosion);
        return explosion;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        frameRemain = 0;
        packingTexture.resetAnimation();
    }
    
    @Override
    public float[] getTextureArray() {
        return texCoords;
    }
    

    public static void setSlowExplosions(boolean slow){
        frameDuration = slow? 0.07f : 0.03f;
    }
    
    private static float frameDuration = 0.03f;
    private float frameRemain = 0;
    @Override
    public void update() {
        super.update();
        //if (posY + lowerHeight > Screen.ARENA_HEIGHT) setToBeCleared(true);
        
        if (frameRemain > 0) {
            frameRemain -= Timer.delta;
        } else {
            
            //How many frames to jump to
            int nb = 1;
            
            if (-frameRemain > frameDuration) { 
                nb += (int) (-frameRemain / frameDuration);
            }
            
            frameRemain += frameDuration;
            
            for (int i = 0; i < nb; i++) {
                //Move to the next frame
                if (!packingTexture.prepareNextTexCoord(texCoords)) {
                    //Reached the last frame
                    setToBeCleared(true);
                    return;
                    //packingTexture.setCurrentId(0);
                    //packingTexture.prepareNextTexCoord(texCoords);
                }
            }
            

        }
        
    }

}
