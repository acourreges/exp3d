package com.breakingbyte.game.entity.move;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;

public class LocalMoveZoomRotation extends LocalMove {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 10;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new LocalMoveZoomRotation();} } );
    
    public static LocalMoveZoomRotation newInstance() { return (LocalMoveZoomRotation)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }

    
    private SmoothJoin smoothers;
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        smoothers.initAt(0, 1f);
        smoothers.setTargetAt(0, 1f, 0f);
        
        smoothers.initAt(1, 0f);
        smoothers.setTargetAt(1, 0f, 0f);
    }
    
    public LocalMoveZoomRotation() {
        smoothers = new SmoothJoin(2);
        smoothers.setInterpolator(Interpolator.SINUSOIDAL_SLOW_START);
    }
    
    public LocalMoveZoomRotation setZoomRot(
            float speed,
            float zoomStart,
            float zoomEnd,
            float rotStart,
            float rotEnd
            )
    {
        smoothers.initAt(0, zoomStart);
        smoothers.setTargetAt(0, zoomEnd, speed);
        
        smoothers.initAt(1, rotStart);
        smoothers.setTargetAt(1, rotEnd, speed);
        return this;
    }

    public static LocalMoveZoomRotation applyTo(Entity entity)
    {
        LocalMoveZoomRotation move = LocalMoveZoomRotation.newInstance();
        bindTogether(entity, move);
        
        return move;
    }
    
    public void updateMove() {
        smoothers.update();
    }
    
    public void affectEntity(Entity entity) {
        entity.scale = smoothers.get(0);
        entity.rotY = smoothers.get(1);
        //Log.d("SMOOTH", "scale " + smoothers.get(0));
        //Log.d("SMOOTH", "rot " + smoothers.get(1));
    }
    
}
