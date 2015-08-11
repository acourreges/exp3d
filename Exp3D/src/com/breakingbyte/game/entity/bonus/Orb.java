package com.breakingbyte.game.entity.bonus;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.move.WorldMoveSmooth;
import com.breakingbyte.game.render.QuadBatch;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;

public class Orb extends Bonus {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new Orb();} } );

    public static Orb newInstance() { return (Orb)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }

    private QuadBatch qb;
    
    private boolean hasBeenCollected;
    
    static final float texCoords[] = {
        0.0f, 1.0f,  //bottom left
        1.0f, 1.0f,  //bottom right
        0.0f, 0.0f,  //top left
        1.0f, 0.0f   //top right
    };
    
    static final float texCoordsBis[] = {
        1.0f, 1.0f,  //bottom left
        0.0f, 1.0f,  //bottom right
        1.0f, 0.0f,  //top left
        0.0f, 0.0f   //top right
    };
    
    public Orb() {
        width = height = 15;
        bonusType = BonusType.ORB;
        qb = new QuadBatch(3);
        width = height = 15;
        setColor(1f, 0.6f, 1f);
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        hasBeenCollected = false;
    }
    
    public static Orb spawn() {
        Orb result = newInstance();
        Engine.layer_bonus.addEntity(result);
        result.clearWhenLeaveScreen = true;
        result.arenaAnimation = true;
        result.movY = -1f;
        result.moveSpeed = 20f;
        result.scale = 0.7f; //to make it easier to grab
        return result;
    }
    
    @Override
    public void renderImpl() {
        //if (true) return;
        updateBuffers();
        
        TextureManager.orbIn.bind();
        
        Renderer.pushMatrix();
        Renderer.translate(posX, posY, 0);
        Renderer.scale(width * scale, height * scale, 0);
        Renderer.unbindVBOs();
        {
            Renderer.pushMatrix();
            Renderer.rotate(timer * 54f, 0, 0, 1);
            QuadVBO.drawQuadImmediate(texCoords);
            Renderer.popMatrix();
        }

        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        TextureManager.orbInWhiter.bind();
        Renderer.setColor(red, green, blue, globalAlpha * 0.8f);
        qb.render();
        
        TextureManager.orbGloss.bind();
        Renderer.setColor(1f, 1f, 1f, globalAlpha * 0.5f);
        //Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        QuadVBO.drawQuadImmediate(texCoords);
        
        Renderer.popMatrix();
    }
    
    private void updateBuffers() {
        qb.clearBatch();
        
        qb.addQuadWithUV(0, 0, -0.5f, 0.5f, 0.5f, -0.5f, 1f, -timer * 48f, texCoordsBis);
        qb.addQuadWithUV(0, 0, -0.5f, 0.5f, 0.5f, -0.5f, 1f, timer * 20f, texCoords);
        //qb.addQuadWithUV(0, 0, -0.5f, 0.5f, 0.5f, -0.5f, 1f, -timer * 24f, texCoordsBis);

        //qb.addQuadWithUV(posX, posY, -0.5f, 0.5f, 0.5f, -0.5f, scale, 0, glassCoords);
        
        qb.updateBuffers();        
    }
    
    @Override
    public void handleToBeClear() {
        
        if (this.hasLeftArenaLimits()) {
            this.toBeCleared = true;
            return;
        }
        
        // Must die, just let it do a little before dying
        float destX = 10;
        float destY = Screen.ARENA_HEIGHT - 7;
        if (!hasBeenCollected) {
            WorldMoveSmooth.applyTo(this).speed(0.4f).pt0(this.posX, this.posY).pt1(destX, destY).smoother.setInterpolator(Interpolator.QUADRATIC_START);
            hasBeenCollected = true;
        }
        lifeRemaining = 0; // so we won't count as a collision
        toBeCleared = (this.posX == destX) && (this.posY == destY);
        if (toBeCleared) {
            AudioManager.playSound(SoundId.CHIME_1);
            ArenaState.instance.stats.addOrbCollected();
            UI.synchronizeOrbCounter();
        }
    }
    
}
