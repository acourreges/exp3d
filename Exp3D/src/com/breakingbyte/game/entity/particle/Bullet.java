package com.breakingbyte.game.entity.particle;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Timer;

public class Bullet extends Entity {
    
    private SmoothJoin zoomer;    
    public float zoomTarget = 1f;
    
    //Optional initial force when the bullet is spawned
    public boolean initialPulse = false;
    public float elapsed = 0;
    public float pulseX, pulseY, pulseStrength, destPtX, destPtY, destStrength; 
    private float lastDestX, lastDestY;
    private boolean keepHoming = true;
    
    public Bullet() {
        zoomer = new SmoothJoin();
    }
    
    private final float zoomInit = 0.001f;
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        scale = 0f;
        zoomer.init(zoomInit);
        initialPulse = false;
        elapsed = 0;
        pulseX = pulseY = pulseStrength = destPtX = destPtY = destStrength = lastDestX = lastDestY = 0;
        keepHoming = true;
        this.entityType = EntityType.BULLET;
    }
    
    public void setPulse(float pulseAngle, float pulseStrength, float destPtX, float destPtY, float destStrength) {
        initialPulse = true;
        pulseX = (float)Math.cos(pulseAngle);
        pulseY = (float)Math.sin(pulseAngle);
        this.pulseStrength = pulseStrength;
        this.destPtX = destPtX;
        this.destPtY = destPtY;
        this.destStrength = destStrength;
    }
    
    @Override
    public void update() {
        
        if (initialPulse) {
            elapsed += Timer.delta;
            //Update attraction vector
            if (elapsed > 2f) keepHoming = false;
            
            if (keepHoming) {
                lastDestX = this.destPtX - this.posX;
                lastDestY = this.destPtY - this.posY;
                float norm = (float)Math.sqrt(lastDestX*lastDestX + lastDestY*lastDestY) + 0.000000001f;
                lastDestX /= norm;
                lastDestY /= norm;
                if (norm <= 10) keepHoming = false;
            }
            movX = destStrength * lastDestX * elapsed + pulseX * pulseStrength;
            movY = destStrength * lastDestY * elapsed + pulseY * pulseStrength;
        }
        
        if (initialPulse && keepHoming) clearWhenLeaveScreen = false;
        else clearWhenLeaveScreen = true;
        
        super.update();
        
        zoomer.update();
        if (zoomer.get() == zoomInit) {
            //Just spawned, grow up
            zoomer.setTarget(zoomTarget, 10f);
        }
        
        if (toBeCleared) {
            //We must die
            lifeRemaining = 0; //so we won't count as a collision
            moveSpeed = 0f;
            zoomer.setTarget(0f, 20f); //fade-out by zooming-out
            if (zoomer.get() > 0.2f) toBeCleared = false; //display the time of the animation
        }
        
        scale = zoomer.get();
    }
    
    @Override
    public void setValuesFrom(Entity other) {
        super.setValuesFrom(other);
        zoomTarget = ((Bullet)other).zoomTarget;
    }
    
    /*
    public void reshape(Bullet reference) {
        width = reference.width;
        height = reference.height;
        zoomTarget = reference.zoomTarget;
        attackPower = reference.attackPower;
    }
    */

}
