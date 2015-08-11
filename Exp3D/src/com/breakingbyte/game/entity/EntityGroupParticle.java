package com.breakingbyte.game.entity;

import java.util.ArrayList;

import com.breakingbyte.game.render.QuadBatch;


/**
 * Used mainly for drawing many entities in one draw call. 
 * Used for bullets of the ship, particle systems.
 */

public class EntityGroupParticle extends EntityGroup {
    
    public int MAX_CAPACITY;

    private QuadBatch quadBatch;
    
    public void postConstructor() {
        
        entities = new ArrayList<Entity>(MAX_CAPACITY);
        
        quadBatch = new QuadBatch(MAX_CAPACITY);
    }

    
    @Override
    public void render() {
        
        int nbEntities =  entities.size();
        if (nbEntities == 0) return;
        
        //Build data in the JVM
        updateJVMBuffer();
        
        quadBatch.render();
        
    }
    

    public void updateJVMBuffer() {
        
        quadBatch.clearBatch();
        
        int nbEntities =  entities.size();
        
        for (int i = 0; i < nbEntities; i++) {
            
            if (i >= MAX_CAPACITY) break;
            
            Entity entity = entities.get(i);
            
            quadBatch.addQuad(entity);
        } 
        quadBatch.updateBuffers();
    }

    
}
