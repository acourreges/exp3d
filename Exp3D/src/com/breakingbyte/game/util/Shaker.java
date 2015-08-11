package com.breakingbyte.game.util;

import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class Shaker {
    
    private float time = 0f;
    
    private SmoothJoin dampingJoin;
    
    public float shakingSpeed = 4f;
    
    public float xAmplitude = 0, 
                 yAmplitude = 0, 
                 zAmplitude = 0;
    
    public float xOscill = 0, 
                 yOscill = 0, 
                 zOscill = 0;
    
    public Shaker() {
        dampingJoin = new SmoothJoin();
        dampingJoin.init(0f);
    }
    
    public void shake() {
        time = 0f;
        dampingJoin.init(1f);
        dampingJoin.setTarget(0f, shakingSpeed);
    }
    
    public void update() {
        time += Timer.delta;
        dampingJoin.update();
    }
    
    public void applyTransformation() {
        
        if (dampingJoin.get() < 0.005) return;
        
        float xShake = dampingJoin.get() * MathUtil.getCyclicValue(-xAmplitude, xAmplitude, time * xOscill);
        float yShake = dampingJoin.get() * MathUtil.getCyclicValue(-yAmplitude, yAmplitude, time * yOscill);
        float zShake = dampingJoin.get() * MathUtil.getCyclicValue(-zAmplitude, zAmplitude, time * zOscill);
        
        Renderer.translate(xShake, yShake, zShake);
    }
    
    public void reset() {
        time = 0f;
        dampingJoin.init(0f);
        dampingJoin.setTarget(0f, 0f);
    }

}
