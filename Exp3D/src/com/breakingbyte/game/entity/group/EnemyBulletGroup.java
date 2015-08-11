package com.breakingbyte.game.entity.group;

import com.breakingbyte.game.entity.EntityGroupParticle;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;

/**
 * Displays all the enemy bullets. 
 * Different bullet type are mapped to different UV in 
 * the texture atlas.
 */

public class EnemyBulletGroup extends EntityGroupParticle {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 1;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new EnemyBulletGroup();} } );
    
    public static EnemyBulletGroup newInstance() { return (EnemyBulletGroup)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }

    public EnemyBulletGroup() {
        MAX_CAPACITY = 500;
        
        postConstructor();
    }
    
    @Override
    public void render() {
        
        if (entities.isEmpty()) return;
        
        TextureManager.enemyBullet.bind();
        
        //Render all the group
        Renderer.setColor(1f,1,1,0.8f);
        super.render();
        Renderer.resetColor();
        
    }
    
}
