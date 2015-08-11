package com.breakingbyte.game.entity.bonus;

import com.breakingbyte.game.content.PowerUpContent;
import com.breakingbyte.game.content.PowerUpContent.PowerUpItem;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.bonus.Bonus.BonusType;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Renderer;

public class PlusOne extends Entity {

    // Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new PlusOne();} } );
    
    public static PlusOne newInstance() { return (PlusOne)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    SmoothJoin disappearJoin;
    
    
    private DynamicText text;
    
    public PlusOne() {
        width = height = 1f;
        lifeStart = 1;
        disappearJoin = new SmoothJoin();
        disappearJoin.setInterpolator(Interpolator.LINEAR);
    }
    
    public static PlusOne spawn() {
        PlusOne result = newInstance();
        Engine.layer_bonus.addEntity(result);
        result.startAnimation();
        return result;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        disappearJoin.init(0f);
        immuneToCollision = true;
        scale = 1.6f;
        setUp(BonusType.ORB, 0, 0);
    }
    
    public void setUp(BonusType bonusType, float x, float y) {
        this.bonusType = bonusType;
        switch (bonusType) {
            case ORB: 
                text = TextureManager.txt_oneMorOrb;
                text.setColor(255, 231, 252);
                break;
            default:
                PowerUpItem item = PowerUpContent.getPowerUpItemFromEnum(bonusType);
                text = item.getDynamicText();
                text.setColor(item.red, item.green, item.blue);
                break;
        }
        
        y += 15f;
        if (y > Screen.ARENA_HEIGHT) y = Screen.ARENA_HEIGHT;
        float halfWidth = scale * text.getWidth() * 0.5f;
        if (x - halfWidth <= 0) x = halfWidth;
        else if (x + halfWidth >= Screen.ARENA_WIDTH) x = Screen.ARENA_WIDTH - halfWidth;
        posX = x;
        posY = y;
        
    }
    
    @Override
    public void update() {
        super.update();
        
        disappearJoin.update();
        
        if (disappearJoin.get() > 0.9f) setToBeCleared(true);
    }
    
    public void startAnimation() {
        disappearJoin.init(0);
        disappearJoin.setTarget(1f, 0.8f);
    }
    
    @Override
    public void render() {
        Renderer.pushMatrix();

        
        Renderer.translate(posX, posY + 8f + 13f * disappearJoin.get(), 0);
        
        Renderer.scale(scale, scale, scale);
        
        text.setAlpha((1f - disappearJoin.get()) * 0.8f);
        text.render();
        
        Renderer.resetColor();
        
        Renderer.popMatrix();
    }
    
}
