package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.Player;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Timer;

public class WorldMoveFollowPlayer extends WorldMove {
  
    //Object pool
    private static final int POOL_INIT_CAPACITY = 50;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new WorldMoveFollowPlayer();} } );
    
    public static WorldMoveFollowPlayer newInstance() { return (WorldMoveFollowPlayer)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    float t;
    boolean began;
    
    
    SmoothJoin speed = new SmoothJoin();
    public WorldMoveFollowPlayer speed(float initSpeed, float finalSpeed, Interpolator interpolator, float speed) 
    {  
        this.speed.init(initSpeed);
        this.speed.setTarget(finalSpeed, speed);
        this.speed.setInterpolator(interpolator);
        return this; 
    }
    
    public WorldMoveFollowPlayer speed(float speed)
    {
        speed(speed, speed, Interpolator.ASYMPTOTIC, 0f);
        return this;
    }
    
    private float timeFollowing;
    private float maxAngle;
    public WorldMoveFollowPlayer followFor(float duration, float maxAngle) { 
        timeFollowing = duration; 
        this.maxAngle = maxAngle;
        return this; 
    }

    public WorldMoveFollowPlayer initialAngle(float initialAngle) {
        angle = initialAngle;
        began = true;
        return this;
    }
    

    public static WorldMoveFollowPlayer applyTo(Entity entity)
    {
        WorldMoveFollowPlayer move = WorldMoveFollowPlayer.newInstance();
        bindTogether(entity, move);
        
        return move;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        t = 0f;
        angle = 0f;
        timeFollowing = 0f;
        began = false;
        speed(0);
        maxAngle = 0f;
    }
    
    private float angle;
    public boolean updateMove() {
        
        Player player = Engine.player;
        
        if (!began) {
            angle = MathUtil.getAngleBetween(entity.posX, entity.posY, 0f, player.posX, player.posY);
        } 
        
        if (timeFollowing > 0) {
            
            timeFollowing -= Timer.delta;

            float candidateAngle = MathUtil.getAngleBetween(entity.posX, entity.posY, entity.movX, entity.movY, player.posX, player.posY);
            
            if (maxAngle != 0) {
                if (candidateAngle >= 0) {
                    candidateAngle = Math.min(candidateAngle, maxAngle*Timer.delta);
                } else {
                    candidateAngle = Math.max(candidateAngle, -maxAngle*Timer.delta);
                }
            }
            
            if (began) angle += candidateAngle;
        }

        speed.update();
        
        began =true;
        
        return true;
    }
    
    public void affectEntity(Entity e)
    {
        e.clearWhenLeaveScreen = (timeFollowing <= 0);
        e.setDirAngle(MathUtil.radiansToDegrees*angle);
        e.moveSpeed = speed.get();
    }

}
