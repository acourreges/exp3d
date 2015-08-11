package com.breakingbyte.game.entity;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.particle.SimpleBlast;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.wrap.shared.Timer;

public class Hellfire {
    
    private float posX, posY;

    private static final float DURATION = 5f;
    
    private float timeRemaining = 0f;
    
    public float shotPeriod = 0.065f;
    public float toWait = 0;

    private float elapsed;
    private boolean colorGroupAlternate;
    
    public boolean reverseAngle = false;
    
    public void startFire() {
        colorGroupAlternate = true;
        timeRemaining = DURATION;
    }
    
    public void stopFire() {
        timeRemaining = 0f;
    }
    
    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }
    
    public void update() {
        if (timeRemaining <= 0) return;
        
        timeRemaining -= Timer.delta;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            return;
        }
        
        if (toWait > 0) toWait -= Timer.delta;
        if (toWait > 0) return;
        
        toWait += shotPeriod;
        elapsed += Timer.delta;
        
        float rootDiff = MathUtil.getCyclicValue(-20, 20, 40f * elapsed);
        float rootAngle = 90 + rootDiff * (reverseAngle? 1f : -1f);
        
        for (float offset = -6; offset <= 6; offset+= 6) {
            colorGroupAlternate = !colorGroupAlternate;
            float angle = rootAngle + offset;
            SimpleBlast bullet = SimpleBlast.newInstance();
            bullet.clearWhenLeaveScreen = true;
            if (colorGroupAlternate) Engine.player.hellFireGroupColor1.addMember(bullet);
            else Engine.player.hellFireGroupColor2.addMember(bullet);
            bullet.posX = posX; bullet.posY = posY;
            bullet.rotZ = -90 + angle;
            bullet.movX = (float)Math.cos(angle*MathUtil.degreesToRadians);
            bullet.movY = (float)Math.sin(angle*MathUtil.degreesToRadians);
            bullet.moveSpeed = 200f;
        }
    }

}
