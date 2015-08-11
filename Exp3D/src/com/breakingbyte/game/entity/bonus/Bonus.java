package com.breakingbyte.game.entity.bonus;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class Bonus extends Entity {
    
    public static enum BonusType {
        ORB,
        TIME_WARP,
        HELLFIRE,
        SUPER_SHIELD
    }
    
    public float scale = 1f;

    public float globalAlpha = 1f;
    
    protected float timer = 0f;
    
    float red = 1f, green = 1f, blue = 1f;
    
    boolean arenaAnimation;
    SmoothJoin appearAlphaJoin, appearScaleJoin;
    private final float appearInit = 0.001f;
    
    public BonusType bonusType;
    
    static final float texCoords[] = {
        0.0f, 1.0f,  //bottom left
        1.0f, 1.0f,  //bottom right
        0.0f, 0.0f,  //top left
        1.0f, 0.0f   //top right
    };
    
    public Bonus() {
        entityType = EntityType.BONUS;
        width = height = 1;
        lifeStart = 1;
        appearAlphaJoin = new SmoothJoin();
        appearScaleJoin = new SmoothJoin();
        setColor(1f, 1f, 1f);
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        timer = 0f;
        clearWhenLeaveScreen = true;
        scale = 1f;
        arenaAnimation = false;
        appearAlphaJoin.init(appearInit);
        appearAlphaJoin.setTarget(appearInit, 0);
        appearScaleJoin.init(appearInit);
        appearScaleJoin.setTarget(appearInit, 0);
    }
    
    @Override
    public void update() {
        if (arenaAnimation) super.update();
        timer += Timer.delta; // * 20f;
        
        appearAlphaJoin.update();
        appearScaleJoin.update();
        
        if (appearScaleJoin.get() == appearInit) {
            appearScaleJoin.setTarget(1f, 15f);
            appearAlphaJoin.setTarget(1f, 50f);
        }
        
        if (arenaAnimation && toBeCleared) {
            handleToBeClear();
        }        
    }
    
    public void handleToBeClear() {
        // Must die, just let it do a little before dying
        lifeRemaining = 0; // so we won't count as a collision
        moveSpeed = 0f;
        appearAlphaJoin.setTarget(0f, 5f);
        appearScaleJoin.setTarget(2f, 5f);
        if (appearAlphaJoin.get() > 0.2f) toBeCleared = false;
    }
    
    public void setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    @Override
    public void render() {
        preRender();
        renderImpl();
        postRender();
    }
    
    public void preRender() {
        Renderer.Blending.resetMode();
        
        if (arenaAnimation) {
            // Special animation for the arena
            globalAlpha = appearAlphaJoin.get(); // * MathUtil.getCyclicValue(0.65f, 0.95f, timer * 5f);
            scale = appearScaleJoin.get() * MathUtil.getCyclicValue(0.6f, 0.75f, timer * 5f);
        }
        
        Renderer.setColor(red, green, blue, globalAlpha);
    }
    
    public void renderImpl() {
        // Overridden by child for actual rendering
    }
    
    public void postRender() {
        Renderer.resetColor();
        Renderer.Blending.resetMode();
    }
    
    
}
