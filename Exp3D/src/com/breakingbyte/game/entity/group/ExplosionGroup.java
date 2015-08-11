package com.breakingbyte.game.entity.group;

import com.breakingbyte.game.entity.EntityGroupParticle;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;

public class ExplosionGroup extends EntityGroupParticle {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 1;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new ExplosionGroup();} } );
    
    public static ExplosionGroup newInstance() { return (ExplosionGroup)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public ExplosionGroup() {
        MAX_CAPACITY = 20;
        
        postConstructor();
    }
    
    @Override
    public void render() {
        
        if (entities.isEmpty()) return;
        
        TextureManager.explosion.bind();
        
        //Render all the group
        Renderer.resetColor();
        super.render();
        Renderer.resetColor();
        
    }

}
