package com.breakingbyte.game.entity.enemy;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class Drakol extends EntityEnemy {
    
    //Settings at the pool extraction
    public static Drakol settings = new Drakol();    
    
    private float elapsed = 0f;
    
    //Default settings
    static { 
        settings.width = 10;
        settings.height = 20;
        settings.attackPower = 400;
        settings.lifeStart = 5000;
    }
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Drakol();} } );
    
    public static Drakol newInstance() { return (Drakol)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    @Override
    public void toInitValues(){
        super.toInitValues();
        setValuesFrom(settings);
        elapsed = MathUtil.getRandomInt(0, 5);
    }
    
    public static Drakol spawn() {
        Drakol entity = Drakol.newInstance();
        entity.registerInLayer();
        return entity;
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.drakol.mesh;
    }
    
    @Override
    public void update() {
        super.update();
        elapsed += Timer.delta;
    }
    
    @Override
    public void render()
    {
        bindBuffers();
        renderDrawOnly();
    }
    
    public void bindBuffers() 
    {
        TextureManager.drakol.bind();     
        getMesh().bindVBOs();
    }
    
    public void renderDrawOnly() 
    {
        preDraw();
        
        Renderer.translate(posX, posY, posZ);
        
        //rotZ = 30;
        
        Renderer.rotate(30+rotX, 1, 0, 0);
        Renderer.rotate(rotZ, 0, 0, 1);
        Renderer.rotate(rotY, 0, 1, 0);
        
        Renderer.rotate(15f * (float)Math.sin(3.2f * elapsed), 0, 1, 0);
        
        Renderer.rotate(-90, 1, 0, 0);
        //Renderer.rotate(-180, 0, 1, 0);
        float s = 2.2f;
        Renderer.scale(s, s, s);
        getMesh().renderDrawOnly();
        
        postDraw();
    }
    
}
