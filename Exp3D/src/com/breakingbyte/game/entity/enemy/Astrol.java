package com.breakingbyte.game.entity.enemy;


import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;

public class Astrol extends EntityEnemy {
    
    //Settings at the pool extraction
    public static Astrol settings = new Astrol();    
    
    //Default settings
    static { 
        settings.width = 10;
        settings.height = 10;
        settings.attackPower = 100;
        settings.lifeStart = 500;
    }
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 50;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Astrol();} } );
    
    public static Astrol newInstance() { return (Astrol)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    


    @Override
    public void toInitValues() {
        super.toInitValues();
        setValuesFrom(settings);
    }
    
    
    public static Astrol spawn() {
        Astrol entity = Astrol.newInstance();
        entity.rotDirX = 0;
        entity.rotDirY = 0;
        entity.rotDirZ = -1;
        entity.rotationSpeed = 600f;
        entity.registerInLayer();
        return entity;
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.astrol.mesh;
    }
    
    
    @Override
    public void render()
    {
        bindBuffers();
        renderDrawOnly();
    }
    

    public void bindBuffers() {
        TextureManager.astrol.bind();
        getMesh().bindVBOs();
    }
    
    public void renderDrawOnly() {
        
        preDraw();
        
        Renderer.translate(posX, posY, posZ);
        
        Renderer.rotate(35+rotX, 1, 0, 0);
        Renderer.rotate(rotY, 0, 1, 0);
        Renderer.rotate(rotZ, 0, 0, 1);
        
        Renderer.rotate(90, 1, 0, 0);
        getMesh().renderDrawOnly();
        
        
        postDraw();
    }

}
