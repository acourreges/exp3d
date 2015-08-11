package com.breakingbyte.game.entity.move;

import java.util.ArrayList;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;

public class WorldMoveSequence extends WorldMove {

    //Object pool
    private static final int POOL_INIT_CAPACITY = 50;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new WorldMoveSequence();} } );
    
    public static WorldMoveSequence newInstance() { return (WorldMoveSequence)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    
    private ArrayList<WorldMove> moves = new ArrayList<WorldMove>();
    private int current;
    
    @Override
    public void toInitValues() {
        for (int i = 0; i < moves.size(); i++) moves.get(i).free();
        moves.clear();
        current = 0;
    }
    
    @Override
    public boolean updateMove() {
        
        if (current >= moves.size()) return false;
        
        if (!moves.get(current).update()) {
            //Go on to the next move. (We also need to update it otherwise "affectEntity" might have undefined behavior)
            current++;
            updateMove();
        }

        return true;
    }
    
    @Override
    public void affectEntity(Entity e) {
        if (current < moves.size()) moves.get(current).affectEntity(e);
    }
    
    public WorldMoveSequence append(WorldMove move) {
        move.entity = entity;
        moves.add(move);
        return this;
    }
    
    public static WorldMoveSequence applyTo(Entity entity) {
        WorldMoveSequence move = WorldMoveSequence.newInstance();
        bindTogether(entity, move);
        return move;
    }

}
