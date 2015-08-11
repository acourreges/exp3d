package com.breakingbyte.game.entity;

import java.util.ArrayList;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.wrap.shared.Renderer;

/**
 * Group of entities.
 */

public abstract class EntityGroup extends Entity {
       
    //Current entities
    public ArrayList<Entity> entities;

    
    protected void postConstructor() {        
        entities = new ArrayList<Entity>();        
    }
    
    public void addMember(Entity e){
        entities.add(e);
    }
    
    public int getMembersNumber() {
        return entities.size();
    }
    
    //We can clear only when all the children entities die.
    @Override
    public boolean canBeCleared() {
        return toBeCleared && entities.isEmpty(); 
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).free();
        }
        entities.clear();
    }
    
    @Override
    public void update() {
        for (int i = entities.size()-1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update();
            
            if (entity.canBeCleared()) {
                removeMember(i);                
            }
        }
    }
    
    public void removeMember(int index) {
        Entity entity = entities.get(index);
        entities.remove(index);
        entity.free();
    }
    
    @Override
    public void render() {
        
        for (int i = 0; i < entities.size(); i++) {
            Renderer.pushMatrix();
            entities.get(i).render();
            Renderer.popMatrix();
        }
    }
    
    @Override
    public void renderBoundingBox() {
        
        for (int i = 0; i < entities.size(); i++) {
            Renderer.pushMatrix();
            entities.get(i).renderBoundingBox();
            Renderer.popMatrix();
        }
    }

}
