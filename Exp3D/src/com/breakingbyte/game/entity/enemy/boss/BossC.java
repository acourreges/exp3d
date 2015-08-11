package com.breakingbyte.game.entity.enemy.boss;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.enemy.EntityEnemy;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.script.ScriptInterpreter;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class BossC extends EntityEnemy {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 1;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new BossC();} } );
    
    public static BossC newInstance() { return (BossC)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public ScriptInterpreter scriptInterpreter;
    
    public SmoothJoin rotationSpeedZ, rotationY, rotYShakeAmplitude;
    public float timer;
    public float waveAmplitude;
    public boolean rotateAttack;
    
    public static float lastAliveX, lastAliveY;
    
    public BossC() {
        entityType = EntityType.BOSS;
        width = 20;
        height = 20;
        attackPower = 400;
        lifeStart = 170000;
        scriptInterpreter = new ScriptInterpreter();
        scriptInterpreter.entity = this;
        rotationSpeedZ = new SmoothJoin();
        rotationY = new SmoothJoin();
        rotYShakeAmplitude = new SmoothJoin();
    }
    
    float elapsed = 0f;
    @Override
    public void toInitValues(){
        super.toInitValues();
        scale = 4.0f;
        scriptInterpreter.resetState();
        rotationSpeedZ.init(0f);
        rotationY.init(0f); 
        rotYShakeAmplitude.init(0f);
        rotationY.setInterpolator(Interpolator.ASYMPTOTIC);
        elapsed = 0f;
    }
    
    public static BossC spawn() {
        BossC entity = BossC.newInstance();
        entity.rotDirZ = 1;
        //entity.rotationSpeed = 150.0f;
        entity.registerInLayer();
        entity.rotationSpeedZ.setTarget(190f, 100);
        entity.rotationY.init(0f); entity.rotationY.setTarget(0f, 100);
        entity.rotYShakeAmplitude.init(10f); entity.rotYShakeAmplitude.setTarget(10f, 100);
        return entity;
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.bossc.mesh;
    }
    
    float angleShot = 0;
    
    public float getLifePercent() {
        return (float)lifeRemaining / lifeStart;
    }
    
    @Override
    public void update() {
        rotationSpeedZ.update();
        rotationY.update();
        rotYShakeAmplitude.update();
        
        rotationSpeed = rotationSpeedZ.get();
        
        super.update();
        elapsed += Timer.delta;
        scriptInterpreter.updateRunScript();

        //rotZ  = 30f;
        rotY = 
                rotationY.get()
                + MathUtil.getCyclicValue(-rotYShakeAmplitude.get(), rotYShakeAmplitude.get(), 2f*elapsed);
        
        if (rotY < -45) {
            width = 25;
            height = 40;
        } else {
            width = height = 40;
        }
        this.bakeDimensions();
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
        TextureManager.tunnel.bind();     
        getMesh().bindVBOs();
    }
    
    public void renderDrawOnly() 
    {
        preDraw();
        
        Renderer.translate(posX, posY, posZ);
        
        Renderer.rotate(20+rotX, 1, 0, 0);
        Renderer.rotate(rotY, 0, 1, 0);
        Renderer.rotate(rotZ, 0, 0, 1);
        
        Renderer.rotate(-90, 1, 0, 0);
        Renderer.rotate(-180, 0, 1, 0);

        Renderer.scale(scale, scale, scale);
        getMesh().renderDrawOnly();
        
        postDraw();
    }
    
}
