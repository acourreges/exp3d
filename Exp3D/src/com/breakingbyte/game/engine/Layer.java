package com.breakingbyte.game.engine;

import java.util.ArrayList;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.EntityGroup;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.wrap.shared.Renderer;

public class Layer {
    
    private ArrayList<Entity> entities;

    public Layer() {
        entities = new ArrayList<Entity>(100);
    }
    
    public boolean isEmpty() {
        return entities.isEmpty();
    }
    
    public void addEntity(Entity e) {
        entities.add(e);
    }
    
    public void clear() {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).free();
        }
        entities.clear();
    }
    
    public void update() {
        for (int i = entities.size()-1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update();
            
            if (entity.canBeCleared()) {
                entities.remove(i);
                entity.free();
            }
        }
    }
    
    public void render() {
        
        for (int i = 0; i < entities.size(); i++) {
            Renderer.pushMatrix();
            entities.get(i).render();
            Renderer.popMatrix();
        }
        
    }
    
    public void renderBoundingBox() {
        for (int i = 0; i < entities.size(); i++) {
            Renderer.pushMatrix();
            entities.get(i).renderBoundingBox();
            Renderer.popMatrix();
        }
    }
    
    private ArrayList<Entity> tmpFullEntityList = new ArrayList<Entity>();
    private ArrayList<Entity> getAliveEntities() {
        
        tmpFullEntityList.clear();

        for (int i = 0; i < entities.size(); i++) {
            
            Entity entity = entities.get(i);
            if (entity instanceof EntityGroup) {
                EntityGroup group = (EntityGroup)entity;
                for (int j = 0; j < group.entities.size(); j++) {
                    Entity subEntity = group.entities.get(j);
                    if (subEntity.lifeRemaining > 0) tmpFullEntityList.add(subEntity);
                }
            } else {
                if (entity.lifeRemaining > 0) tmpFullEntityList.add(entity);
            }
        }
        return tmpFullEntityList;
    }
    
    public void killMemberInSquaredRadius(float centerX, float centerY, float squaredRadius) {
        ArrayList<Entity> members = getAliveEntities();
        
        for (int i = 0; i < members.size(); i++) {
            Entity e = members.get(i);
            float squaredDistance = (e.posX - centerX)*(e.posX - centerX) + (e.posY - centerY)*(e.posY - centerY);
            if (squaredDistance <= squaredRadius) e.lifeRemaining = 0;
        }
    }
    
    public Entity getRandomAliveMember() {
        
        ArrayList<Entity> potentialTargets = getAliveEntities();
        
        if (potentialTargets.isEmpty()) return null;
        
        return potentialTargets.get(MathUtil.getRandomInt(0, potentialTargets.size() - 1));
        
    }
    
    private ArrayList<Entity> collisionCurrentEntities = new ArrayList<Entity>();
    private ArrayList<Entity> collisionOtherEntities = new ArrayList<Entity>();
    public void collisionWith(Layer otherLayer) {
        
        //TODO It is a bit ugly, might need to improve collision detection
        
        collisionOtherEntities.clear();
        
        for (int currentIdx = entities.size() - 1; currentIdx >= 0; currentIdx--){
 
            Entity currentEntity = entities.get(currentIdx);
            
            collisionCurrentEntities.clear();
            if (currentEntity instanceof EntityGroup) {
                // It is a group, we just consider the member of the group
                
                // addAll() allocates objects!
                // collisionCurrentEntities.addAll(((EntityGroup)currentEntity).entities);
                ArrayList<Entity> entityGroup = ((EntityGroup)currentEntity).entities;
                for (int i = 0; i < entityGroup.size(); i++){
                    collisionCurrentEntities.add(entityGroup.get(i));
                }
                
            } else {
                collisionCurrentEntities.add(currentEntity);
            }
            
            for (int currentSubEntityIndex = collisionCurrentEntities.size() - 1; currentSubEntityIndex >= 0; currentSubEntityIndex--) {
                
                Entity currentSubEntity = collisionCurrentEntities.get(currentSubEntityIndex);
                   
                if (currentSubEntity.immuneToCollision) continue;
                
                // Cache top/bottom/left/right
                float currBottom = currentSubEntity.posY + currentSubEntity.lowerHeight;
                float currTop    = currentSubEntity.posY + currentSubEntity.upperHeight;
                float currLeft   = currentSubEntity.posX + currentSubEntity.leftWidth;
                float currRight  = currentSubEntity.posX + currentSubEntity.rightWidth;
                
                //--------- Other layer -----------
                for (int otherIdx = otherLayer.entities.size() - 1; otherIdx >= 0; otherIdx--){
                    
                    Entity otherEntity = otherLayer.entities.get(otherIdx);
                    
                    if (otherEntity.immuneToCollision) continue;
                    
                    collisionOtherEntities.clear();
                    if (otherEntity instanceof EntityGroup ) {
                        // It is a group, we just consider the member of the group
                        // collisionOtherEntities.addAll(((EntityGroup)otherEntity).entities);
                        ArrayList<Entity> entityGroup = ((EntityGroup)otherEntity).entities;
                        for (int i = 0; i < entityGroup.size(); i++){
                            collisionOtherEntities.add(entityGroup.get(i));
                        }
                        
                        
                    } else {
                        collisionOtherEntities.add(otherEntity);
                    }
                    
                    for (int otherSubEntityIndex = collisionOtherEntities.size() - 1; otherSubEntityIndex >= 0; otherSubEntityIndex--) {
                        
                        Entity otherSubEntity = collisionOtherEntities.get(otherSubEntityIndex);
                      
                        if (otherSubEntity.immuneToCollision) continue;
                        
                        float otherBottom = otherSubEntity.posY + otherSubEntity.lowerHeight;
                        float otherTop    = otherSubEntity.posY + otherSubEntity.upperHeight;
                        float otherLeft   = otherSubEntity.posX + otherSubEntity.leftWidth;
                        float otherRight  = otherSubEntity.posX + otherSubEntity.rightWidth;
                        
                        if (
                                currentSubEntity.lifeRemaining <= 0 ||
                                otherSubEntity.lifeRemaining <= 0 ||
                                currTop < otherBottom ||
                                currBottom > otherTop ||
                                currRight < otherLeft ||
                                currLeft >  otherRight
      
                        ) continue;
                        
                        // Collision!
                        
                        // int currEnergy = currentSubEntity.lifeRemaining;
                        // int otherEnergy = otherSubEntity.lifeRemaining;
                        int currAttack = currentSubEntity.attackPower;
                        int otherAttack = otherSubEntity.attackPower;
                        
                        currentSubEntity.receiveDamageFrom(otherSubEntity, otherAttack);
                        otherSubEntity.receiveDamageFrom(currentSubEntity, currAttack);
                        
                        
                    }
                    
                    
                }
                
                
            }
            
           
        }
    }
    
}
