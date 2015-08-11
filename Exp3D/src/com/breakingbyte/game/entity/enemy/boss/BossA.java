package com.breakingbyte.game.entity.enemy.boss;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.enemy.EntityEnemy;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.script.ScriptInterpreter;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;

public class BossA extends EntityEnemy {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 1;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new BossA();} } );
    
    public static BossA newInstance() { return (BossA)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public ScriptInterpreter scriptInterpreter;
    
    public SmoothJoin rotationSpeedZ, rotationSpeedY;
    public float timer;
    public float waveAmplitude;
    public boolean rotateAttack;
    
    public int subState = 0;
    
    public static float lastAliveX, lastAliveY;
    
    public BossA() {
        entityType = EntityType.BOSS;
        width = 20;
        height = 20;
        attackPower = 400;
        lifeStart = 60000;
        scriptInterpreter = new ScriptInterpreter();
        scriptInterpreter.entity = this;
        rotationSpeedZ = new SmoothJoin();
        rotationSpeedY = new SmoothJoin();
    }
    
    @Override
    public void toInitValues(){
        super.toInitValues();
        scale = 4.0f;
        scriptInterpreter.resetState();
        rotationSpeedZ.init(0f);
        rotationSpeedY.init(0f);
        subState = 0;
    }
    
    public static BossA spawn() {
        BossA entity = BossA.newInstance();
        //entity.rotDirY = 1;
        //entity.rotationSpeed = -150.0f;
        entity.registerInLayer();
        return entity;
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.bossa.mesh;
    }
    
    float angleShot = 0;
    
    public float getLifePercent() {
        return (float)lifeRemaining / lifeStart;
    }
    
    @Override
    public void update() {
        super.update();
        scriptInterpreter.updateRunScript();
        rotationSpeedZ.update();
        rotationSpeedY.update();
    }
    
    @Override
    public void render()
    {
        bindBuffers();
        renderDrawOnly();
    }
    
    @Override
    public void explode() {
        //super.explode();
        lastAliveX = posX;
        lastAliveY = posY;
        UI.displayBossLifeBar(false);
    }
    
    @Override
    public void receiveDamageFrom(Entity entity, int amount) {
        
        if (entity.entityType == EntityType.PLAYER) {
            return;
        }
        
        if (entity.entityType == EntityType.PLAYER_SPECIAL_WEAPON) {
            if (amount > 0) {
                amount = 1000;
                entity.attackPower = 0;
            }
        }
        
        super.receiveDamageFrom(entity, amount);
        UI.setBossLifeAmount((float)lifeRemaining / lifeStart);
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
        
        Renderer.rotate(20+rotX, 1, 0, 0);
        Renderer.rotate(rotZ, 0, 0, 1);
        Renderer.rotate(rotY, 0, 1, 0);
        
        Renderer.rotate(-90, 1, 0, 0);
        Renderer.rotate(-180, 0, 1, 0);

        Renderer.scale(scale, scale, scale);
        getMesh().renderDrawOnly();
        
        postDraw();
    }
    
}
