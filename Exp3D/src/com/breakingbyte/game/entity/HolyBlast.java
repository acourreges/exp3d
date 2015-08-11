package com.breakingbyte.game.entity;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.render.QuadBatch;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;


public class HolyBlast extends Entity {
    
    public static final String TAG = "HolyBlast";
    
    //Time To Live in second
    public float TTL = 0.6f;
    
    private float age = 0;
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 12;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new HolyBlast();} } );
    
    public static HolyBlast newInstance() { return (HolyBlast)pool.getFreeInstance(); }
    public void free() { freeTail(); pool.returnToPool(this); }
    
    //Tail composed of control points (linked list)
    private HolyBlastCtrl tail = null;
    
    private static final float BEAM_INITIAL_WIDTH = 3;
    
    private static final float TAIL_SEGMENT_LENGTH = 10;
    
    private static final float SPEED = 270;
    private static final float ANGULAR_VELOCITY = 10;//13;
    
    public Entity target = null;
    float targetX, targetY;
    
    
    public static final int MAX_CAPACITY = 60;
    
    private SmoothJoin color;
    
    private QuadBatch quadBatch;
    
    public HolyBlast() {
        entityType = EntityType.PLAYER_SPECIAL_WEAPON;
        width = height = 5;
        
        color = new SmoothJoin(4);
        quadBatch = new QuadBatch(MAX_CAPACITY);
        
        lifeStart = 1;
    }
    
    @Override
    public void toInitValues() {
        super.toInitValues();
        attackPower = 10000;
        color.init(1f,0.65f,0.65f,1f);
        color.setTarget(1f,0.6f,0.6f,0f, 5f);
        target = null;
    }
    
    public void freeTail() {
        HolyBlastCtrl ctrl = tail;
        while (ctrl != null) {
            HolyBlastCtrl newCtrl = ctrl.olderCtrl;
            ctrl.free();
            ctrl = newCtrl;
        }
    }
    
    public void initializeWith(float positionX, 
                               float positionY, 
                               float directionX, 
                               float directionY) {
        posX = positionX;
        posY = positionY;
        
        movX = directionX;
        movY = directionY;
        moveSpeed = SPEED;
        
        HolyBlastCtrl ctrlPoint = HolyBlastCtrl.newInstance();
        ctrlPoint.initWith(this, BEAM_INITIAL_WIDTH);
        tail = ctrlPoint;
        
        age = 0;
    }
    
    @Override
    public void receiveDamageFrom(Entity entity, int amount) {
        //No, don't die
        //lifeRemaining = 0;
        //this.moveSpeed = 0;
    }
    
    float lastDistance = 0f;
    @Override
    public void update() {
        
        age += Timer.delta;
        
        if (age >= TTL) {
            this.moveSpeed = 0;
            attackPower = 0;
            color.update();
            if (age > 2*TTL) {
                setToBeCleared(true);
            }
            return;
        }
        
        //Move
        posX = posX +  movX * Timer.delta * moveSpeed;
        posY = posY +  movY * Timer.delta * moveSpeed;


        //--- Update direction toward target

        if (target == null || target.lifeRemaining <= 0) {
            target = Engine.layer_enemies.getRandomAliveMember();
        }
        
        if (target != null) {
            targetX = target.posX; //0
            targetY = target.posY; //100
        }
        
        //Ideally, how much should we rotate?
        float angleBetweenTargetAndDirection = 
            target == null?
                    0f :
                    MathUtil.getAngleBetween(
                            posX, posY, 
                            movX, movY, 
                            targetX, targetY);
        
        
        //final rotation value (with constraint)
        float toRotate;
        if (angleBetweenTargetAndDirection > 0) {
            toRotate = Math.min(angleBetweenTargetAndDirection, ANGULAR_VELOCITY*Timer.delta);
        } else {
            toRotate = Math.max(angleBetweenTargetAndDirection, -ANGULAR_VELOCITY*Timer.delta);
        }
        
        //apply rotation
        final float cos = (float)Math.cos(toRotate);
        final float sin = (float)Math.sin(toRotate);
        
        float newDirX = movX * cos - movY * sin;
        float newDirY = movX * sin + movY * cos;
        
        movX = newDirX;
        movY = newDirY;
        
        //--- End update direction
                
        

        //Update tail
        HolyBlastCtrl part = tail;
        while (part != null) {
            part.update();
            part = part.olderCtrl;
        }
        
        //Extend tail
        float lastDistanceX = posX - tail.posX;
        float lastDistanceY = posY - tail.posY;
        float lastDistance2 = lastDistanceX*lastDistanceX + lastDistanceY*lastDistanceY;
        
        if (lastDistance2 > TAIL_SEGMENT_LENGTH) {
            HolyBlastCtrl newCtrl = HolyBlastCtrl.newInstance();
            newCtrl.initWith(this, BEAM_INITIAL_WIDTH);
            newCtrl.olderCtrl = tail;
            tail = newCtrl;
            mustUpdateVertices = true;
            //Log.d("TAG", "CTRL Left" + tail.leftGrowthX + ", " + tail.leftGrowthY);
            //Log.d("TAG", "CTRL Right" + tail.rightGrowthX + ", " + tail.rightGrowthY);
        }

    }
    
    static final float texCoords[] = {
            0.25f + 0.2f *0.25f, 0.52f,  //bottom left
            0.25f + 0.8f*0.25f, 0.52f,  //bottom right
            0.25f + 0.2f*0.25f, 0.52f,  //top left
            0.25f + 0.8f*0.25f, 0.52f   //top right
        };
    static final float texCoordsHead[] = {
            0.25f + 0.2f*0.25f, 0.999f,  //bottom left
            0.25f + 0.8f*0.25f, 0.999f,  //bottom right
            0.25f + 0.2f*0.25f, 0.5f,  //top left
            0.25f + 0.8f*0.25f, 0.5f   //top right
        };
    
    @SuppressWarnings("unused")
    private int lastNumberOfCtrlPoints = 0;
    @SuppressWarnings("unused")
    private boolean mustUpdateVertices = true;
    public void render()
    {
        
        int nbSegments = 0;
        
        HolyBlastCtrl currentCtrl = tail;
        
        float prevLeftX = currentCtrl.leftGrowthX;
        float prevLeftY = currentCtrl.leftGrowthY;
        float prevRightX = currentCtrl.rightGrowthX;
        float prevRightY = currentCtrl.rightGrowthY;
        
        quadBatch.clearBatch();
        
        while (currentCtrl != null && nbSegments < MAX_CAPACITY) {
                       
            currentCtrl.updateVerticesBuffer(prevLeftX, prevLeftY, prevRightX, prevRightY);
            quadBatch.addQuadWithUV(currentCtrl.vertexData, nbSegments == 1? texCoordsHead : texCoords);
            
            nbSegments++;
            
            prevLeftX = currentCtrl.leftGrowthX;
            prevLeftY = currentCtrl.leftGrowthY;
            prevRightX = currentCtrl.rightGrowthX;
            prevRightY = currentCtrl.rightGrowthY;
            
            currentCtrl = currentCtrl.olderCtrl;
        }
        
        quadBatch.updateBuffers();
        
        //Draw
        TextureManager.simpleBlast.bind();
        
        Renderer.setColor(color.get(0), color.get(1), color.get(2), color.get(3));
        
        quadBatch.render();
        
        Renderer.resetColor();
    }
    
}
