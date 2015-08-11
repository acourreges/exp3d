package com.breakingbyte.game.entity.enemy;


import com.breakingbyte.game.entity.move.LocalMoveFacePlayer;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;

public class Byrol extends EntityEnemy {

    //Settings at the pool extraction
    public static Byrol settings = new Byrol();    
    
    //Default settings
    static { 
        settings.width = 14;
        settings.height = 14;
        settings.attackPower = 400;
        settings.lifeStart = 2500;
    }
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Byrol();} } );
    
    public static Byrol newInstance() { return (Byrol)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this);}

    @Override
    public void toInitValues() {
        super.toInitValues();
        setValuesFrom(settings);
    }
    
    public static Byrol spawn() {
        Byrol entity = Byrol.newInstance();
        LocalMoveFacePlayer.applyTo(entity).speed(6f);
        entity.registerInLayer();
        return entity;
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.byrol.mesh;
    }
    
    @Override
    public void render()
    {
        bindBuffers();
        renderDrawOnly();
    }
    
    public void bindBuffers() 
    {
        TextureManager.byrol.bind();   
        getMesh().bindVBOs();
    }
    
    public void renderDrawOnly() 
    {
        preDraw();
        
        Renderer.translate(posX, posY, posZ);
        
        Renderer.rotate(45/*20*/+rotX, 1, 0, 0);
        Renderer.rotate(rotY, 0, 1, 0);
        Renderer.rotate(rotZ, 0, 0, 1);
        
        Renderer.rotate(-90, 1, 0, 0);
        
        getMesh().renderDrawOnly();
        
        postDraw();
    }

    
}
