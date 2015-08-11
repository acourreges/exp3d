package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.Player;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Timer;

public class LocalMoveFacePlayer extends LocalMove {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new LocalMoveFacePlayer();} } );
    
    public static LocalMoveFacePlayer newInstance() { return (LocalMoveFacePlayer)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    private float speed;
    public LocalMoveFacePlayer speed(float value) {speed = value; return this;}
    
    private float rotZ;
    public void initRotZ(float value) {rotZ = value;}

    @Override
    public void toInitValues() {
        super.toInitValues();
        rotZ = 0f;
    }
    
    public static LocalMoveFacePlayer applyTo(Entity entity)
    {
        LocalMoveFacePlayer move = LocalMoveFacePlayer.newInstance();
        bindTogether(entity, move);
        return move;
    }
    
    public void updateMove() {
        
        Player player = Engine.player;
        
        float targetX = player.posX;
        float targetY = player.posY;        
        
        //Entity angle        
        float currentAngle = (rotZ - 90) * MathUtil.degreesToRadians;
        currentAngle %= MathUtil.TWO_PI;
        
        float difference = MathUtil.getAngleBetween(
                entity.posX, entity.posY,
                currentAngle,
                targetX, 
                targetY);
        
        float part = Math.min(1f, Timer.delta * speed);
        float finalAngle = currentAngle + difference * part;
        
        rotZ = 90 + finalAngle * MathUtil.radiansToDegrees;
    }
    
    public void affectEntity(Entity entity) {
        entity.rotZ = rotZ;
    }
    
}
