package com.breakingbyte.game.entity.enemy;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;

public class Crystol extends EntityEnemy {
    
    //Settings at the pool extraction
    public static Crystol settings = new Crystol();    
    
    //Default settings
    static { 
        settings.width = 20;
        settings.height = 20;
        settings.attackPower = 400;
        settings.lifeStart = 5000;
    }
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Crystol();} } );
    
    public static Crystol newInstance() { return (Crystol)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    @Override
    public void toInitValues(){
        super.toInitValues();
        setValuesFrom(settings);
    }
    
    public static Crystol spawn() {
        Crystol entity = Crystol.newInstance();
        entity.rotDirY = 1;
        entity.rotationSpeed = -350.0f;
        entity.registerInLayer();
        return entity;
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.crystol.mesh;
    }
    
    @Override
    public void render()
    {
        bindBuffers();
        renderDrawOnly();
    }
    
    public void bindBuffers() 
    {
        TextureManager.crystol.bind();     
        getMesh().bindVBOs();
    }
    
    public void renderDrawOnly() 
    {
        preDraw();
        
        Renderer.translate(posX, posY, posZ);
        
        Renderer.rotate(45+rotX, 1, 0, 0);
        Renderer.rotate(rotZ, 0, 0, 1);
        Renderer.rotate(rotY, 0, 1, 0);
        
        Renderer.rotate(-90, 1, 0, 0);
        Renderer.rotate(-180, 0, 1, 0);
        getMesh().renderDrawOnly();
        
        postDraw();
    }
    
}
