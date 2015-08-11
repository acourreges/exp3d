package com.breakingbyte.game.entity.group;

import java.util.ArrayList;


import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.EntityGroup;
import com.breakingbyte.game.entity.enemy.Astrol;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class AstrolRing extends EntityGroup {
    
    //Max number of branches
    //public static final int NB_BRANCHES_MAX = 30;
    
    public float BRANCH_GROW_SPEED;
    public float BRANCH_ROTATION_SPEED;
    
    //Actual number
    private int NB_BRANCHES;
    
    //X,Y of the branches
    private ArrayList<Float> branchX;
    private ArrayList<Float> branchY;
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 5;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new AstrolRing();} } );
    
    public static AstrolRing newInstance() { return (AstrolRing)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
     
    @Override
    public void registerInLayer(){
        Engine.layer_enemies.addEntity(this);
    }
    
    public AstrolRing() {  
        branchX = new ArrayList<Float>();
        branchY = new ArrayList<Float>();
        postConstructor();
    }
    
    @Override
    public void toInitValues(){
        super.toInitValues();
        branchX.clear();
        branchY.clear();
        BRANCH_GROW_SPEED = -0.3f;
        BRANCH_ROTATION_SPEED = -2.2f;
    }
    
    @Override
    public void removeMember(int index) {
        super.removeMember(index);
        branchX.remove(index);
        branchY.remove(index);
    }
    
    public void populateChildren(int number) {
        NB_BRANCHES = number;
        
        //Initial positions
        float posInitX = 100f;
        float posInitY = 0f;
        
        float anglePortion = MathUtil.TWO_PI / NB_BRANCHES;
        
        //Initialize units and assign position
        for (int i = 0; i < NB_BRANCHES; i++) {
            Astrol entity = Astrol.newInstance();
            addMember(entity);
                        
            entity.rotationSpeed = 100f;
            entity.rotDirZ = 10f;
            entity.rotDirX = 0.4f;
            entity.rotDirY = 0.4f;
            
            float angle = anglePortion * i;
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            
            float posX = posInitX * cos - posInitY * sin;
            float posY = posInitX * sin + posInitY * cos;
            
            branchX.add(posX);
            branchY.add(posY);
    
        } 
        
        synchronizeMembersPosition();
    }
    
    public void synchronizeMembersPosition() {        
        for (int i = 0; i < entities.size(); i++) {
            Astrol entity = (Astrol)entities.get(i);            
            entity.posX = this.posX + branchX.get(i);
            entity.posY = this.posY + branchY.get(i);

            if ( Math.abs(entity.posX - posX) < 6f && Math.abs(entity.posY - posY) < 6f) {
                entity.explode();
                entity.setToBeCleared(true);
            }
            
        }
    }
    
    @Override
    public void update() {
        
        //Remove dead bodies
        super.update();
        
        if (entities.isEmpty()) {
            //Log.d("AstrolRing", "No member -> Dying!");
            toBeCleared = true;
            return;
        }
        
        //Mother position
        posX = posX +  movX * Timer.delta * moveSpeed;
        posY = posY +  movY * Timer.delta * moveSpeed;
        
        //Children
        float rotateAngle = BRANCH_ROTATION_SPEED * Timer.delta;
        
        for (int i = 0; i < entities.size(); i++ ) {
            
            //Update branches
            float bX = branchX.get(i);
            float bY = branchY.get(i);
            
            //Retract
            bX = bX + bX * BRANCH_GROW_SPEED * Timer.delta;
            bY = bY + bY * BRANCH_GROW_SPEED * Timer.delta;
            
            //Rotate
            float cos = (float)Math.cos(rotateAngle);
            float sin = (float)Math.sin(rotateAngle);
            
            branchX.set(i, bX * cos - bY * sin);
            branchY.set(i, bX * sin + bY * cos);
 
        }
        
        synchronizeMembersPosition();  
    }
    

    @Override
    public void render() {
        
        if (entities.isEmpty()) return;
        
        TextureManager.astrol.bind();
        Astrol.getMesh().bindVBOs();
        
        for (int i = 0; i < entities.size(); i++) {
            Renderer.pushMatrix();
            ((Astrol)entities.get(i)).renderDrawOnly();
            Renderer.popMatrix();
        }        
    }


}
