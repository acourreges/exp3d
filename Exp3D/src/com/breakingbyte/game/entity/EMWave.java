package com.breakingbyte.game.entity;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.wrap.shared.Timer;

public class EMWave {
    
    private float currentDistance;
    
    private float originX, originY;
    private float speed, distanceMax;
    
    public void setUp(float originX, float originY, float speed, float distanceMax) {
        currentDistance = 0;
        this.originX = originX;
        this.originY = originY;
        this.speed = speed;
        this.distanceMax = distanceMax;
    }
    
    public void update() {
        if (currentDistance == distanceMax) return;
        
        currentDistance = Math.min(currentDistance + speed * Timer.delta, distanceMax);
        
        Engine.layer_enemyBullets.killMemberInSquaredRadius(originX, originY, currentDistance * currentDistance);
    }

}
