package com.breakingbyte.game.entity.bonus;

import com.breakingbyte.game.content.PowerUpContent;
import com.breakingbyte.game.content.PowerUpContent.PowerUpItem;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class PowerUp extends Bonus {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new PowerUp();} } );
    
    public static PowerUp newInstance() { return (PowerUp)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }

    private Texture texture;
    
    public PowerUpItem powerUpItem;
    
    private float elapsed = 0f;
    
    public PowerUp() {
        width = height = 15;
        width = height = 15;
        setColor(0.6f, 0.6f, 1f);
    }
    
    public static PowerUp spawn(BonusType bonus) {
        
        PowerUp result = newInstance();
        result.powerUpItem = PowerUpContent.getPowerUpItemFromEnum(bonus);
        result.setUp();
        Engine.layer_bonus.addEntity(result);
        result.clearWhenLeaveScreen = true;
        result.arenaAnimation = true;
        result.movY = -1f;
        result.moveSpeed = 20f;
        result.scale = 0.7f; //to make it easier to grab
        return result;
    }
    
    public void setUp() {
        bonusType = powerUpItem.bonusType;
        texture = powerUpItem.getTexture();
        setColor(powerUpItem.red, powerUpItem.green, powerUpItem.blue);
        elapsed = 0;
    }
    
    @Override
    public void update() {
        super.update();
        elapsed += Timer.delta;
    }
    
    @Override
    public void renderImpl() {
        
       
        //texture.bind();
        
        Renderer.pushMatrix();
        Renderer.translate(posX, posY, 0);
        Renderer.scale(width * scale, height * scale, 0);
        
        TextureManager.powerUpBg.bind();
        //Renderer.setColor(104 /255f, 172f / 255f, 1f, 1f);
        //Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        QuadVBO.drawQuadImmediate(texCoords);
        
        texture.bind();
        Renderer.setColor(0.9f, 0.9f, 0.9f, 0.8f);
        Renderer.rotate(elapsed * 50f, 0, 0, 1);
        QuadVBO.drawQuadImmediate(texCoords);
        
        Renderer.popMatrix();
    }
    
    
}
