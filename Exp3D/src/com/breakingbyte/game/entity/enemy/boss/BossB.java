package com.breakingbyte.game.entity.enemy.boss;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.bonus.Bonus.BonusType;
import com.breakingbyte.game.entity.enemy.EntityEnemy;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.script.ScriptInterpreter;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.Matrix4;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class BossB extends EntityEnemy {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 1;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new BossB();} } );
    
    public static BossB newInstance() { return (BossB)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    public ScriptInterpreter scriptInterpreter;
    
    public SmoothJoin rotationSpeedZ, rotationSpeedY;
    public float timer;
    public float waveAmplitude;
    public boolean rotateAttack;
    private SmoothJoin rotZShaker;
    
    public static float lastAliveX, lastAliveY;
    
    public BossArm armLeft, armRight;
    
    private static final int bossLife = 60000;
    private static final int armLife = 20000;
    
    public BossB() {
        entityType = EntityType.BOSS;
        width = 20;
        height = 20;
        attackPower = 400;
        lifeStart = bossLife;
        scriptInterpreter = new ScriptInterpreter();
        scriptInterpreter.entity = this;
        rotationSpeedZ = new SmoothJoin();
        rotationSpeedY = new SmoothJoin();
        rotZShaker = new SmoothJoin();
    }
    
    float elapsed = 0f;
    @Override
    public void toInitValues(){
        super.toInitValues();
        scale = 4.0f;
        scriptInterpreter.resetState();
        rotationSpeedZ.init(0f);
        rotationSpeedY.init(0f);
        rotZShaker.init(0f);
        elapsed = 0f;
    }
    
    public static BossB spawn() {
        BossB entity = BossB.newInstance();
        //entity.rotDirY = 1;
        //entity.rotationSpeed = -150.0f;
        entity.registerInLayer();
        entity.spawnArms();
        return entity;
    }
    
    public void spawnArms() {
        armLeft = BossArm.spawn();
        armLeft.carryPowerUp(BonusType.TIME_WARP); 
        armRight = BossArm.spawn();
        armRight.carryPowerUp(BonusType.TIME_WARP); 
        armRight.rotationSpeed = -armRight.rotationSpeed;
        armLeft.parent = this;
        armRight.parent = this;
    }
    
    public void setArmsImmuneToCollision(boolean immune) {
        armLeft.immuneToCollision = armRight.immuneToCollision = immune;
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.bossb.mesh;
    }
    
    float angleShot = 0;
    
    public float getLifePercent() {
        return (float)lifeRemaining / lifeStart;
    }
    
    private static Matrix4 matrix = new Matrix4();
    
    @Override
    public void update() {
        super.update();
        elapsed += Timer.delta;
        
        immuneToCollision = armLeft != null || armRight != null;
        
        scriptInterpreter.updateRunScript();
        rotationSpeedZ.update();
        rotationSpeedY.update();
        rotZShaker.update();
        
        rotY = MathUtil.getCyclicValue(-20f, 20f, 3.2f*elapsed);
        rotZ = 0 + MathUtil.getCyclicValue(-8f, 8f, 2*elapsed);
        
        rotZ += rotZShaker.get();
        
        //Parent transform
        matrix.idt();
        matrix.translateGL(posX, posY, posZ);
        matrix.rotateGL(20+rotX, 1, 0, 0);
        matrix.rotateGL(rotZ, 0, 0, 1);
        matrix.rotateGL(rotY, 0, 1, 0);
        matrix.rotateGL(-90, 1, 0, 0);
        matrix.scaleGL(scale, scale, scale);
        
        
        if (armLeft != null) {
            float x = -5.3f; float y = 0f; float z = 0.4f;
            armLeft.posX = matrix.val[Matrix4.M00] * x + matrix.val[Matrix4.M01] * y + matrix.val[Matrix4.M02] * z + matrix.val[Matrix4.M03];
            armLeft.posY = matrix.val[Matrix4.M10] * x + matrix.val[Matrix4.M11] * y + matrix.val[Matrix4.M12] * z + matrix.val[Matrix4.M13];
            armLeft.posZ = matrix.val[Matrix4.M20] * x + matrix.val[Matrix4.M21] * y + matrix.val[Matrix4.M22] * z + matrix.val[Matrix4.M23];
        }
        if (armRight != null) {
            float x = 5.3f; float y = 0f; float z = 0.4f;
            armRight.posX = matrix.val[Matrix4.M00] * x + matrix.val[Matrix4.M01] * y + matrix.val[Matrix4.M02] * z + matrix.val[Matrix4.M03];
            armRight.posY = matrix.val[Matrix4.M10] * x + matrix.val[Matrix4.M11] * y + matrix.val[Matrix4.M12] * z + matrix.val[Matrix4.M13];
            armRight.posZ = matrix.val[Matrix4.M20] * x + matrix.val[Matrix4.M21] * y + matrix.val[Matrix4.M22] * z + matrix.val[Matrix4.M23];
        }
        
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
        updateBossLife();
    }
    
    public void bindBuffers() 
    {
        TextureManager.bossb.bind();     
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
        //Renderer.rotate(-180, 0, 1, 0);

        scale = 4f;
        Renderer.scale(scale, scale, scale);
        getMesh().renderDrawOnly();
        
        postDraw();
    }
    
    public void onArmDied(BossArm arm) {
        boolean isLeftArm = (arm == armLeft);
        if (isLeftArm) armLeft = null; else armRight = null; 
        
        shake(isLeftArm);
        
    }
    
    public void shake(boolean toTheLeft) {
        rotZShaker.setInterpolator(Interpolator.BACK_END);
        rotZShaker.backAmplitude = 2f;
        rotZShaker.init(rotZShaker.get() + (toTheLeft? 1f : -1f) * 360f);
        rotZShaker.setTarget(0f, 3f);
    }
    
    public void makeEntrance() {
        rotZShaker.setInterpolator(Interpolator.BACK_END);
        rotZShaker.backAmplitude = 2f;
        rotZShaker.init(rotZShaker.get() + 0.5f * 360f);
        rotZShaker.setTarget(0f, 2.5f);
    }
   
    public void updateBossLife() {
        float totalLife = bossLife + 2 * armLife;
        float nowLife = lifeRemaining;
        if (armLeft != null) nowLife += armLeft.lifeRemaining;
        if (armRight != null) nowLife += armRight.lifeRemaining;
        UI.setBossLifeAmount(nowLife / totalLife);
    }
    
    private static class BossArm extends EntityEnemy {
        
        //Object pool
        private static final int POOL_INIT_CAPACITY = 2;
        private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
                new Constructor() { public Poolable newObject(){return new BossArm();} } );
        
        public static BossArm newInstance() { return (BossArm)pool.getFreeInstance(); }
        public void free() { pool.returnToPool(this); }
        
        public BossB parent;
        
        public BossArm() {
            entityType = EntityType.BOSS;
            width = 10;
            height = 10;
            attackPower = 400;
            lifeStart = armLife;
        }
        
        
        @Override
        public void toInitValues(){
            super.toInitValues();
            scale = 4.0f;
            parent = null;
        }
        
        public static BossArm spawn() {
            BossArm entity = BossArm.newInstance();
            entity.rotDirY = 1f;
            entity.rotDirX = 0.1f;
            entity.rotationSpeed = -1000.0f;
            entity.registerInLayer();
            return entity;
        }
        
        public static MeshVBOs getMesh() {
            return ModelManager.bossb_arm.mesh;
        }
        
        public void update() {
            super.update();
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
            
            Renderer.Lighting.disable();
            
            Renderer.rotate(rotX, 1, 0, 0);
            Renderer.rotate(rotZ, 0, 0, 1);
            Renderer.rotate(rotY, 0, 1, 0);
            
            Renderer.rotate(-90, 1, 0, 0);
            //Renderer.rotate(-180, 0, 1, 0);

            scale = parent.scale * 1.5f;// * MathUtil.getCyclicValue(0.7f, 1.0f, 3f * elapsed);
            Renderer.scale(scale, scale, scale);
            
            bindBuffers(); //if missing, cause a bug on GWT (texture coordinate are messed up...)
            getMesh().renderDrawOnly();
            
            Renderer.Lighting.enable();
            
            postDraw();
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
            parent.updateBossLife();
        }
        
        @Override
        public void render()
        {
            bindBuffers();
            renderDrawOnly();
        }
        
        @Override
        public void explode() {
            super.explode();
            parent.onArmDied(this);
        }
    }
    
}


