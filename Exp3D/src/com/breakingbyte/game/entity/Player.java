package com.breakingbyte.game.entity;


import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.content.PowerUpContent;
import com.breakingbyte.game.content.PowerUpContent.PowerUpItem;
import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.bonus.Bonus;
import com.breakingbyte.game.entity.bonus.Bonus.BonusType;
import com.breakingbyte.game.entity.bonus.PlusOne;
import com.breakingbyte.game.entity.group.ColorFireGroup;
import com.breakingbyte.game.entity.particle.Explosion;
import com.breakingbyte.game.entity.particle.ExplosionFireRing;
import com.breakingbyte.game.entity.particle.SimpleBlast;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.Vibration;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;
import com.breakingbyte.wrap.shared.Timer;

public class Player extends Entity {
    
    public static int LIFE_NUMBER_START = 3;
    public int lifeNumber;
    
    protected static MeshVBOs mesh;
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 1;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Player();} } );
    
    public static Player newInstance() { return (Player)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    //Weapon
    public EntityGroup primaryWeapon;
    public boolean autoFire;
    
    public static final float DELAY_FIRE_BASE = 0.12f;
    public static float DELAY_FIRE = 0;
    private float cumulDelta;
    
    //Position target
    public float posTargetX;
    public float posTargetY;
    
    //Move speed
    private static final float MAX_VELOCITY = 400.f; 
    
    //Rotation target
    private float rotTargetX;
    private float rotTargetY;
    
    //Rotation speed
    private static final float ROTATION_SPEED_X = 400.f;
    private static final float ROTATION_SPEED_Y = 400.f;
    private static final float MAX_ROTATION_X = 20.f; 
    private static final float MAX_ROTATION_Y = 60.f; 
    
    //Revive
    private float reviveTimer;
    private SmoothJoin revivePositioner;
    
    private boolean autoPilotMode;
    
    public static float SPECIAL_WEAPON_DELAY_BASE = 15f;
    public static float SPECIAL_WEAPON_DELAY;
    public float specialWeaponLastShot;
    
    private EMWave emWave;
    public static boolean EM_WAVE_ON = false;
    
    private Hellfire hellfireLeft, hellfireRight;
    public EntityGroup hellFireGroupColor1, hellFireGroupColor2;
    
    private SmoothJoin softTiltAmplitude;
    
    public SmoothJoin visualShieldAlpha;
    
    public boolean superShieldOn = false;
    public float superShieldRemaining = 0f;
    
    public static boolean centerFire = false;
    public static boolean leftFire = true;
    public static boolean rightFire = true;
    
    public static int DAMAGE_KEEPER_PERCENT = 100;
    
    public boolean blockSpecialWeaponReload;
    
    public Player() {
        entityType = EntityType.PLAYER;
        width = 6;
        height = 11;
        attackPower = Integer.MAX_VALUE;
        lifeStart = 2000;
        moveSpeed = 10f;
        autoPilotMode = false;
        specialWeaponLastShot = 0;
        
        emWave = new EMWave();
        revivePositioner = new SmoothJoin();
        revivePositioner.setInterpolator(Interpolator.LINEAR);
        
        hellfireLeft = new Hellfire();
        hellfireRight = new Hellfire();
        hellfireRight.reverseAngle = true;
        
        softTiltAmplitude = new SmoothJoin();
        softTiltAmplitude.init(0f);
        softTiltAmplitude.setTarget(0f, 0f);
        
        visualShieldAlpha = new SmoothJoin();
        visualShieldAlpha.init(0f);
        visualShieldAlpha.setTarget(0f, 0f);
    }
    
    public void setAutoPilot(boolean autoPilotOn) {
        autoPilotMode = autoPilotOn;
        softTiltAmplitude.setTarget(autoPilotOn? 0f : 20f, 2f);
    }
    
    public boolean getAutoPilot() {
        return autoPilotMode;
    }
    
    @Override
    public void toInitValues(){
        super.toInitValues();
        rotTargetX = rotTargetY = 0;
        cumulDelta = 0;
        reviveTimer = 0f;
        revivePositioner.init(0);
        revivePositioner.setTarget(0f, 0f);
    }
    
    public void reset() {
        lifeStart = 2000;
        lifeRemaining = lifeStart;
        lifeNumber = LIFE_NUMBER_START;
        rotTargetX = rotTargetY = 0;
        cumulDelta = 0;
        reviveTimer = 0f;
        revivePositioner.init(0);
        revivePositioner.setTarget(0f, 0f);
        hellfireLeft.stopFire();
        hellfireRight.stopFire();
        visualShieldAlpha.init(0f);
        visualShieldAlpha.setTarget(0f, 0f);
        softTiltAmplitude.setTarget(0f, 2f);
        superShieldOn = false;
        blockSpecialWeaponReload = false;
    }
    
    public static void init() {
        ColorFireGroup simpleBlast = ColorFireGroup.newInstance();
        simpleBlast.setColor(0.5f,0.7f,0.99f,1f);
        simpleBlast.setTexture(TextureManager.simpleBlast);
        Engine.player.primaryWeapon = simpleBlast;
        
        ColorFireGroup hellFireG = ColorFireGroup.newInstance();
        hellFireG.setColor(1f,0.5f,0.3f,1f);
        hellFireG.setTexture(TextureManager.simpleBlast);
        Engine.player.hellFireGroupColor1 = hellFireG;
        
        hellFireG = ColorFireGroup.newInstance();
        hellFireG.setColor(0.6f,1f,0.3f,1f);
        hellFireG.setTexture(TextureManager.simpleBlast);
        Engine.player.hellFireGroupColor2 = hellFireG;
    }
    
    @Override
    public void setToBeCleared(boolean toBeCleared){
        super.setToBeCleared(toBeCleared);
        //Tell the weapon to be cleared (done when all the bullets are cleared)
        if (toBeCleared = true) {
            primaryWeapon.setToBeCleared(true);
        }
    }
    
    public static MeshVBOs getMesh() {
        return ModelManager.ship.mesh;
    }
    
    public void depleteSpecialWeapon() {
        specialWeaponLastShot = 0;
    }
    
    public void fillSpecialWeapon() {
        specialWeaponLastShot = 999;
    }
    
    public float getSpecialWeaponProgress() {
        return Math.min(1f, specialWeaponLastShot / SPECIAL_WEAPON_DELAY);
    }
    
    @Override
    public void update() {
        super.update();
        
        elapsed += Timer.delta;
        
        if (lifeNumber <= 0) {
            posX = 50;
            posY = -30;
            return;
        }
        
        if (blockSpecialWeaponReload) specialWeaponLastShot = 0f;
        
        if (superShieldOn) {
            superShieldRemaining -= Timer.delta;
            if (superShieldRemaining <= 0) {
                superShieldRemaining = 0;
                visualShieldAlpha.setTarget(0f, 2f);
                if (visualShieldAlpha.get() <= 0.1f) superShieldOn = false;
            }
        }
        
        softTiltAmplitude.update();
        visualShieldAlpha.update();
        
        ((ColorFireGroup)hellFireGroupColor1).setColor(1f,0.5f,0.3f,1f); //TODO decide and move the color code to init
        ((ColorFireGroup)hellFireGroupColor2).setColor(0.4f,0.8f,0.3f,0.9f);
        
        if (!autoPilotMode) {
            
            boolean positioningAfterRevive = revivePositioner.update();
            
            specialWeaponLastShot = specialWeaponLastShot + Timer.delta;
            UI.setSpecialWeaponProgress(getSpecialWeaponProgress());
            
            emWave.update();
            
            
            
            if (positioningAfterRevive) {
                posTargetX = Screen.ARENA_WIDTH * 0.5f;
                posTargetY = revivePositioner.get();
            }
            
            //Update position to reach user's finger
            float newX = posTargetX;
            float newY = posTargetY;
            
            float dX = posTargetX - posX;
            float dY = posTargetY - posY;
                
            float norm = (float)Math.sqrt(dX*dX + dY*dY);
            float maxNorm = MAX_VELOCITY * Timer.delta;
                
            if (norm > maxNorm) {
                float ratio = norm / maxNorm;
                dX = (posTargetX - posX) / ratio;
                dY = (posTargetY - posY) / ratio;
            }
            
            newX = posX + dX;
            newY = posY + dY;
            
            posX = newX; 
            posY = newY;
            
            //Tilt the ship according to the move direction
            
            final float threshold = 0.9f;
    
            
            //Y tilting
            if (dX > threshold || dX < -threshold) {
                this.rotTargetY += (dX > 0 ? -1 : 1) * ROTATION_SPEED_Y * Timer.delta;
                if (this.rotTargetY > MAX_ROTATION_Y) this.rotTargetY = MAX_ROTATION_Y;
                if (this.rotTargetY < -MAX_ROTATION_Y) this.rotTargetY = -MAX_ROTATION_Y;
                this.rotY = this.rotTargetY;
            }
            else 
            {
                this.rotY -= 2.0f * rotY * Timer.delta;
                this.rotTargetY = this.rotY;
            }
            
            //X tilting
            if (dY > threshold || dY < -threshold) {
                this.rotTargetX += (dY < 0 ? -1 : 1) * ROTATION_SPEED_X * Timer.delta;
                if (this.rotTargetX > MAX_ROTATION_X) this.rotTargetX = MAX_ROTATION_X;
                if (this.rotTargetX < -MAX_ROTATION_X) this.rotTargetX = -MAX_ROTATION_X;
                this.rotX = this.rotTargetX;
            }
            else 
            {
                this.rotX -= 2.0f * rotX * Timer.delta;
                this.rotTargetX = this.rotX;
            }
            
            
            
            if (autoFire && !toBeCleared && !positioningAfterRevive) {
                
                //hellfire.startFire();
                hellfireLeft.setPosition(posX - 6f, posY + 2f);
                hellfireLeft.update();
                hellfireRight.setPosition(posX + 6f, posY + 2f);
                hellfireRight.update();
                
                cumulDelta += Timer.delta;
                
                if (cumulDelta > DELAY_FIRE) { //Fire weapon
                    
                    if (centerFire) {
                        //Spawn new bullet
                        SimpleBlast blast = SimpleBlast.newInstance();
                        blast.clearWhenLeaveScreen = true;
                        blast.posX = posX;
                        blast.posY = posY + 6.5f;
                        blast.moveSpeed = 200f;
                        blast.movY = 1;
                        primaryWeapon.addMember(blast);
                    }
                    
                    float bulletOffsetX = 3f;
                    float bulletOffsetY = 2f;
                    
                    if (leftFire) {
                        //Spawn new bullet
                        SimpleBlast blast = SimpleBlast.newInstance();
                        blast.clearWhenLeaveScreen = true;
                        blast.posX = posX - bulletOffsetX;
                        blast.posY = posY - bulletOffsetY;
                        blast.moveSpeed = 200f;
                        blast.movY = 1;
                        primaryWeapon.addMember(blast);
                    }
                    
                    if (rightFire) {
                        //Spawn new bullet
                        SimpleBlast blast = SimpleBlast.newInstance();
                        blast.clearWhenLeaveScreen = true;
                        blast.posX = posX + bulletOffsetX;
                        blast.posY = posY - bulletOffsetY;
                        blast.moveSpeed = 200f;
                        blast.movY = 1;
                        primaryWeapon.addMember(blast);
                    }
                    

                    cumulDelta = 0;
                }
                
            }    
        
        }
        
        if (reviveTimer > 0) {
            reviveTimer -= Timer.delta * 1000f;
            if (reviveTimer <= 0) {
                reviveTimer = 0;
                immuneToCollision = false;
                UI.displayShipFocuser(false);
            }
            else {
                immuneToCollision = true;
            }
        }
    }
    
    public void setPositionTarget(float targetX, float targetY) {
        
        this.posTargetX = targetX;
        this.posTargetY = targetY;
        
    }
    
    @Override
    public void registerInLayer(){
        Engine.layer_player.addEntity(this);
        Engine.layer_playerBullets.addEntity(primaryWeapon);
        Engine.layer_playerBullets.addEntity(hellFireGroupColor1);
        Engine.layer_playerBullets.addEntity(hellFireGroupColor2);
    }
    
   public void fireHolyBlast() {
        
       if (getSpecialWeaponProgress() < 1f) return;
        
        EngineState.GeneralStats.specialFired++;
        AudioManager.playSound(SoundId.SPECIAL_WEAPON);
       
        int nb_holy = 6;
        
        Entity target = Engine.layer_enemies.getRandomAliveMember();
        
        for (int i = 0; i < nb_holy; i++) {
            float angle = (float)(2f * Math.PI / nb_holy * i);
            HolyBlast blast = HolyBlast.newInstance();
            blast.initializeWith(posX, posY, (float)Math.cos(angle), (float)Math.sin(angle));
            blast.target = target;
            Engine.layer_playerBullets.addEntity(blast);  
        }
        
        ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=posX; ex.posY = posY;
        ex.setup(0.9f, 20f, 500f, 0.3f, 0f, 0.5f);
        ex.setColor(1f,0.65f,0.65f);
        
        if (EM_WAVE_ON) {
            fireEMWave();
        }
        
        depleteSpecialWeapon();
        
        ArenaState.instance.stats.setSpecialFired();
    }
   
   public void fireEMWave() {
       emWave.setUp(posX, posY, 250f, 200);
   }

   public void putSuperShield(float duration) {
       superShieldOn = true;
       superShieldRemaining = duration;
       visualShieldAlpha.setTarget(1f, 2f);
   }
   
   public void lightUpShield() {
       if (superShieldOn) return;
       visualShieldAlpha.init(2f);
       visualShieldAlpha.setTarget(0, 5f);
   }
   
   public void renderShield() {
       if (visualShieldAlpha.get() <= 0.05f) return;
       
       if (!superShieldOn) {
           float alpha = visualShieldAlpha.get();
           if (alpha > 1f) alpha = 1f;
           Renderer.setColor(0.6f, 0.8f, 1f, alpha);
           TextureManager.shield.bind();
           QuadVBO.drawQuad(posX, posY, 22f, 22f);
           Renderer.unbindVBOs();
           Renderer.resetColor();
       } else {
           float alpha = visualShieldAlpha.get();
           TextureManager.shield.bind();
           for (int i = 0; i < 2; i++) {
               if (i == 0) 
                   Renderer.setColor(0.1f, 1f, 0.4f, 0.7f * alpha ); 
               else if (i == 1) 
                   Renderer.setColor(0.1f, 1f, 1.0f, 0.2f * alpha);
               
               float scaler = 22f + i*12;
               float ampl = (i==0)? 1.5f : 6;
               float modifier = (float)(ampl * Math.sin(5f*elapsed + i * 2f));
               //if (modifier < 0) modifier = 0;
               scaler += modifier;
               QuadVBO.drawQuad(posX, posY, scaler, scaler);
           }
           Renderer.unbindVBOs();
           Renderer.resetColor();
       }
   }
    
   static final float flameUVs[] = {
           0.25f + 0.2f*0.25f, 0.999f,  //bottom left
           0.25f + 0.8f*0.25f, 0.999f,  //bottom right
           0.25f + 0.2f*0.25f, 0.4f,  //top left
           0.25f + 0.8f*0.25f, 0.4f   //top right
   };
   
    public void render()
    {
        if (reviveTimer > 0) {
            //int nbPeriods = (int)(reviveTimer / 70);
            //if (nbPeriods % 5 > 2) return;
            int nbPeriods = (int)(reviveTimer / 150);
            if (nbPeriods % 2 > 0) return;
        }

        renderEngineFlame();
        
        Renderer.translate(posX, posY, posZ);
        
        Renderer.rotate(20+rotX/2f, 1, 0, 0);
        Renderer.rotate(/*180+*/rotY, 0, 1, 0);
        if (!autoPilotMode)
            Renderer.rotate(-rotY/3f, 0, 0, 1);
        
        //Soft tilt
        Renderer.rotate(softTiltAmplitude.get() * (float)Math.sin(2.2f * elapsed), 0, 1, 0);
        
        //Renderer.rotate(40, 1f, 0, 0);
        //scale = 5f;
        Renderer.scale(scale, scale, scale);
        
        TextureManager.ship.bind();
        Renderer.rotate(180f, 0, 0, 1);
        Renderer.rotate(-90f, 1, 0, 0);
        getMesh().render();
    }
    
    public float elapsed = 0f;
    public void renderEngineFlame() {
        Renderer.pushMatrix();
        
        Renderer.translate(posX, posY, posZ);
        
        Renderer.rotate(20+rotX/2f, 1, 0, 0);
        Renderer.rotate(0, 0, 1, 0);
        if (!autoPilotMode)
            Renderer.rotate(-rotY/6f, 0, 0, 1);
        
        //Renderer.rotate(40, 1f, 0, 0);
        
        Renderer.scale(scale, scale, scale);
        
        //----
        Renderer.translate(0, -7.5f, 0f);
        Renderer.Lighting.disable();
        Renderer.unbindVBOs();
        Renderer.Blending.enable();
        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        Renderer.DepthTest.disable();
        Renderer.setColor(1f, 0.6f, 0.5f, 1f);
        
        TextureManager.simpleBlast.bind();
        
        float scaler = MathUtil.getCyclicValue(0.5f, 1f, 10*elapsed);
        scaler += MathUtil.getCyclicValue(0.5f, 1.0f, 23*elapsed);
        scaler *= MathUtil.getCyclicValue(0.7f, 1f, 53*elapsed);
        scaler *= MathUtil.getCyclicValue(1f, 1.5f, 6*elapsed);
        //scaler += MathUtil.getCyclicValue(0.5f, 1f, 50*flameElapsed);
        
        scaler *= 6f;
        Renderer.scale(3.5f, scaler, 1f);
        Renderer.translate(/*-rotY * 0.007f*/ 0f, -0.5f, 0f);
        QuadVBO.drawQuadImmediate(flameUVs);
        
        Renderer.resetColor();
        Renderer.Blending.disable();
        Renderer.Lighting.enable();
        Renderer.DepthTest.enable();
        
        
        Renderer.popMatrix();
    }
    
    @Override
    public void receiveDamageFrom(Entity entity, int amount) {
        
        if (lifeNumber <= 0) {
            return;
        }
        
        if (entity.entityType == EntityType.BONUS) {
            Bonus bonus = (Bonus) entity;

            //Visual feedback
            PlusOne plusOne = PlusOne.spawn();
            plusOne.posX = posX;
            plusOne.posY = posY;
            plusOne.setUp(bonus.bonusType, posX, posY);
            
            //Bonus effect
            switch (bonus.bonusType) {
                case ORB:
                    //We grabbed an orb
                    //ArenaState.instance.stats.addOrbCollected();
                    //UI.synchronizeOrbCounter();
                    break;
                case TIME_WARP:
                    EngineState.GeneralStats.timeWarp++;
                    ArenaState.instance.startTimeWarp(4f);
                    break;
                case HELLFIRE:
                    EngineState.GeneralStats.hellfire++;
                    hellfireLeft.startFire();
                    hellfireRight.startFire();
                    break;
                case SUPER_SHIELD:
                    EngineState.GeneralStats.superShield++;
                    putSuperShield(5f);
                    break;
            }
            
            if (bonus.bonusType != BonusType.ORB) {
                PowerUpItem item = PowerUpContent.getPowerUpItemFromEnum(bonus.bonusType);
                ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=posX; ex.posY = posY;
                ex.setup(0.7f, 20f, 500f, 0.6f, 0.2f, 0.1f);
                ex.setColor(item.red, item.green, item.blue,  0.8f, 0.8f, 1f);
                if (item.soundId != null) AudioManager.playSound(item.soundId);
            }

            return;
        }
        
        if (superShieldOn) {
            if (entity.entityType == EntityType.BULLET) return;
        }
        
        int finalAmount = amount;
        finalAmount = (finalAmount * DAMAGE_KEEPER_PERCENT) / 100;
        this.lifeRemaining -= finalAmount;
        ArenaState.instance.stats.addDamageTaken(finalAmount);
        boolean deathHappens = false;
        if (this.lifeRemaining <= 0) {
            
            deathHappens = true;
            EngineState.GeneralStats.deaths++;
            
            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=posX; ex.posY = posY;
            ex.setup(1.5f, 20f, 500f, 1f, 0f, 0.5f);
            ex.setColor(1f, 1f, 198f/255f,  1f, 149f/255f, 38f/255f);
            reviveTimer = 4000f;
            
            AudioManager.playExplosion();
            Explosion explosion = Explosion.newInstance();
            explosion.setDimension(130, 130);
            explosion.posX = posX;
            explosion.posY = posY;
            Engine.explosions.addMember(explosion);
            
            //Lose bonus
            hellfireLeft.stopFire();
            hellfireRight.stopFire();
            superShieldRemaining = 0f;
            ArenaState.instance.endTimeWarp();
            
            if (this.lifeNumber > 1) {
                //Revive
                this.lifeRemaining = lifeStart;
                this.lifeNumber--;
                UI.setLifeNumber(this.lifeNumber);
                UI.displayShipFocuser(true);
                
                final float reviveYStart = -30f;
                final float reviveYEnd = Screen.ARENA_HEIGHT * 0.4f;
                posX = Screen.ARENA_WIDTH * 0.5f;
                posY = reviveYStart;
                revivePositioner.init(reviveYStart);
                revivePositioner.setTarget(reviveYEnd, 40f);
                AudioManager.playSound(SoundId.RESPAWN);
            }
            else
            {
                //death
                if (Debug.godMode) this.lifeRemaining = lifeStart;
                else {
                    this.lifeNumber--;
                    UI.setLifeNumber(this.lifeNumber);
                    this.lifeRemaining = 1;
                    immuneToCollision = true;
                    ArenaState.instance.onPlayerDied();
                }
            }
        }
        
        UI.shake();
        Engine.currentLevel.shake();
        Vibration.vibrate(deathHappens? 700 :70);
        lightUpShield();
        
        //Update life
        UI.setLifeAmount((float)lifeRemaining / lifeStart);
    }

}
