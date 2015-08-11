package com.breakingbyte.game.entity.enemy;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.particle.Explosion;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class EntityEnemy extends Entity {
    
    @Override
    public void registerInLayer(){
        Engine.layer_enemies.addEntity(this);
    }
    
    private boolean shot = false;
    private final float shotDuration = 0.05f;
    private float shotRemain = 0;
    

    @Override
    public void toInitValues() {
        super.toInitValues();
        shot = false;
        shotRemain = 0;
    }
    
    @Override 
    public void update(){
        super.update();
        
        if (shot) { 
            shotRemain = shotDuration;
            shot = false;
        }
        
        if (shotRemain > 0) {
            shotRemain -= Timer.delta;
        }
    }
    
    public void preDraw() {

        if (shotRemain > 0) {
            Renderer.Lighting.disable();
            Renderer.setColor(1, 0, 0, 1);
            bindBuffers();
        }
    }
    
    public void bindBuffers() {
        
    }
    
    public void postDraw(){
        if (shotRemain > 0) {
            Renderer.Lighting.enable();
            bindBuffers();
        }
        Renderer.resetColor();
    }
    
    @Override
    public void receiveDamageFrom(Entity entity, int amount) {
        super.receiveDamageFrom(entity, amount);
        shot = true;
    }
    
    @Override
    public void explode() {
        AudioManager.playExplosion();
        Explosion explosion = Explosion.newInstance();
        explosion.setDimension(80, 80);
        explosion.posX = posX;
        explosion.posY = posY;
        Engine.explosions.addMember(explosion);        
    }
    
    @Override
    public void onKilledByPlayer() {
        super.onKilledByPlayer();
        ArenaState.instance.stats.addEnemyKilled();
        EngineState.GeneralStats.kills++;
    }
    
    

}
