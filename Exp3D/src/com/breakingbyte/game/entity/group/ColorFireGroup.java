package com.breakingbyte.game.entity.group;

import com.breakingbyte.game.entity.EntityGroupParticle;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;


public class ColorFireGroup extends EntityGroupParticle {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 3;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new ColorFireGroup();} } );
    
    public static ColorFireGroup newInstance() { return (ColorFireGroup)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public float red, green, blue, alpha; 
    public Texture texture;
    
    public ColorFireGroup() {
        MAX_CAPACITY = 90;
        
        postConstructor();
    }
    
    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    @Override
    public void render() {
        
        if (entities.isEmpty()) return;
        texture.bind();
        
        Renderer.setColor(red, green, blue, alpha);
        
        //Render all the group
        super.render();
        Renderer.resetColor();
        
    }
    


}
