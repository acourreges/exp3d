package com.breakingbyte.game.entity;

import java.util.ArrayList;

import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.bonus.Bonus.BonusType;
import com.breakingbyte.game.entity.bonus.Orb;
import com.breakingbyte.game.entity.bonus.PowerUp;
import com.breakingbyte.game.entity.fire.Fire;
import com.breakingbyte.game.entity.move.LocalMove;
import com.breakingbyte.game.entity.move.LocalMoveDefault;
import com.breakingbyte.game.entity.move.WorldMove;
import com.breakingbyte.game.entity.move.WorldMoveDefault;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.QuadVBO;


public class Entity implements Poolable {
    
    private static String TAG = "Entity";
	
    //Position, move direction
    public float posX, posY, posZ;
    public float movX, movY, movZ;
	
    //Rotation, rotation direction
    public float rotX, rotY, rotZ;
    public float rotDirX, rotDirY, rotDirZ;
    
    //Velocity
    public float moveSpeed = 0;
    public float rotationSpeed = 0;
    
    //Life
    public int lifeStart;
    public int lifeRemaining;
    public int attackPower;
	
    //Bounding box
    public float height;
    public float width;
    public float upperHeight; //top 
    public float lowerHeight; //bottom
    public float leftWidth;   //left
    public float rightWidth;  //right
    
    //Rendering scale
    public float scale = 1.0f;
    
    public boolean immuneToCollision = false;
    
    protected boolean toBeCleared = false;
    
    public boolean couldMove = true;
    
    public boolean clearWhenLeaveScreen;
    
    //Move behavior
    public WorldMove worldMoveBehavior = null;
    public LocalMove localMoveBehavior = null;
    
    public boolean surviveMoveEnd;
    
    //Firing behavior
    public ArrayList<Fire> fireBehaviors = new ArrayList<Fire>(5);
    
    public enum EntityType {
        DEFAULT,
        BOSS,
        PLAYER,
        PLAYER_SPECIAL_WEAPON,
        BONUS,
        BULLET
    }
    
    public EntityType entityType = EntityType.DEFAULT;
    
    public boolean carryOrb = false;
    private boolean carryPowerUp = false;
    public BonusType bonusType;
    
    public final void resetState() {
        toInitValues();
        postInit();
    }
    
    public void toInitValues() {
        posX = posY = posZ = 0;
        movX = movY = movZ = 0;
        rotX = rotY = rotZ = 0;
        rotDirX = rotDirY = rotDirZ = 0;
        moveSpeed = 0;
        rotationSpeed = 0;
        scale = 1.0f;
        immuneToCollision = false;
        toBeCleared = false;
        clearWhenLeaveScreen = false;
        couldMove = true;

        surviveMoveEnd = false;
        carryOrb = false;
        carryPowerUp = false;
        
        clearMoveBehavior();
        clearFireBehaviors();
        
    }
    
    protected void postInit()
    {
        bakeDimensions();
        lifeRemaining = lifeStart;
    }
    
    protected void bakeDimensions() {
        //Auto-calculate coordinates for rendering
        upperHeight = height/2;
        lowerHeight = -height/2;
        leftWidth = -width/2;
        rightWidth = width/2;
    }
    
    public void setValuesFrom(Entity other) {
        this.width = other.width;
        this.height = other.height;
        this.lifeStart = other.lifeStart;
        this.attackPower = other.attackPower;
    }
    
    public void setDimension(float width, float height){
        this.width = width;
        this.height = height;
        bakeDimensions();
    }
    
    public void setToBeCleared(boolean toBeCleared) {
        this.toBeCleared = toBeCleared;
    }
    
    public boolean canBeCleared() {
        return this.toBeCleared;
    }
    
    public void free() { 
        Log.e(TAG, "No pool implemented for " + this.getClass().getName());
    }
    
    public void registerInLayer() {
        Log.e(TAG, "registerInLayer() not overridden for " + this.getClass().getName());
    }
    
    public float[] getTextureArray() {
        Log.e(TAG, "getTextureArray() not overridden for " + this.getClass().getName());
        return null;
    }
    
    public final void clearMoveBehavior() {
        
        if (worldMoveBehavior != null) {
            worldMoveBehavior.free();
            worldMoveBehavior = null;
        }
        
        if (localMoveBehavior != null) {
            localMoveBehavior.free();
            localMoveBehavior = null;
        }
    }
    
    public final void clearFireBehaviors() {
        if (fireBehaviors.size() > 0) {
            for (int i = 0; i < fireBehaviors.size(); i++) {
                fireBehaviors.get(i).free();
            }
            fireBehaviors.clear();
        }
    }
    
    public void update()
    {
        LocalMoveDefault.updateEntity(this);
        if (localMoveBehavior != null) localMoveBehavior.update(); 
        
        WorldMoveDefault.updateEntity(this);
        couldMove = true;
        
        if (worldMoveBehavior != null) couldMove = worldMoveBehavior.update();
        
        if (!couldMove && !surviveMoveEnd) {
            onExitScreenAlive();
            setToBeCleared(true);
            return;
        }
        
        for (int i = 0; i < fireBehaviors.size(); i++) {
            fireBehaviors.get(i).update();
        }
        
        if (lifeRemaining <= 0) {
            onKilledByPlayer();
            setToBeCleared(true);
        }
        else if (clearWhenLeaveScreen && hasLeftArenaLimits()) {
            onExitScreenAlive();
            setToBeCleared(true);
        }
        
    }
    
    public void render() {
        Log.e(TAG, "render() not overriden for " + this.getClass().getName()); 
    }
    
    public void renderBoundingBox() {
        TextureManager.blank.bind();
        
        Renderer.setColor(1f,0f,0f,0.3f);
        QuadVBO.drawQuad(posX, posY, width, height);

        Renderer.resetColor();
    }
    
    public void receiveDamageFrom(Entity entity, int amount) {
        this.lifeRemaining -= amount;
    }
    
    public void explode() {}
    
    public final boolean hasLeftArenaLimits() {
        final float padding = 5;
        if (movX == 0 && movY == 0) return false;
        if (posX + leftWidth - padding > Screen.ARENA_WIDTH)      return movX >= 0;
        if (posX + rightWidth + padding < 0)                      return movX <= 0;
        if (posY + lowerHeight - padding > Screen.ARENA_HEIGHT)   return movY >= 0;
        if (posY + upperHeight + padding < 0)                     return movY <= 0;
        return false;
    }
    
    public final void setDirAngle(float angle) {
        float angleRadian = angle * MathUtil.degreesToRadians;
        movX = (float)Math.cos(angleRadian);
        movY = (float)Math.sin(angleRadian);
    }

    private void onExitScreenAlive() {
        if (carryOrb) ArenaState.instance.stats.addOrbSpawned();
    }
    
    public void carryPowerUp(BonusType bonus) {
        carryPowerUp = true;
        bonusType = bonus;
    }
    
    protected void onKilledByPlayer() {
        explode();
        if (carryOrb) {
            ArenaState.instance.stats.addOrbSpawned();
            Orb orb = Orb.spawn();
            orb.posX = posX; 
            if (orb.posX < 0) orb.posX = 0;
            else if (orb.posX > Screen.ARENA_WIDTH) orb.posX = Screen.ARENA_WIDTH;
            orb.posY = posY;
            if (orb.posY < 0) orb.posY = 0;
            else if (orb.posY > Screen.ARENA_HEIGHT) orb.posY = Screen.ARENA_HEIGHT;
        } 
        else if (carryPowerUp) {
            PowerUp powerUp = PowerUp.spawn(bonusType);
            powerUp.posX = posX;
            powerUp.posY = posY;
        } else {
            //if (entityType == EntityType.BOSS) 
            //    carryPowerUp = carryOrb;
        }
    }
}
