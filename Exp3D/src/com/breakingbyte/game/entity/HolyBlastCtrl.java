package com.breakingbyte.game.entity;

import com.breakingbyte.game.util.ObjectPool;
import com.breakingbyte.game.util.ObjectPool.Constructor;
import com.breakingbyte.game.util.Poolable;

public class HolyBlastCtrl extends Entity {
    
    //Object pool
    private static final int POOL_INIT_CAPACITY = 400;
    private static ObjectPool pool = new ObjectPool(POOL_INIT_CAPACITY,
            new Constructor() { public Poolable newObject(){return new HolyBlastCtrl();} } );
    
    public static HolyBlastCtrl newInstance() { return (HolyBlastCtrl)pool.getFreeInstance(); }
    public void free() { pool.returnToPool(this); }
    
    //The "older" control point of the tail (Used as linked list)
    public HolyBlastCtrl olderCtrl = null;
    
    //The width of the beam at this control point, represented by 2 points
    public float leftGrowthX, leftGrowthY;
    public float rightGrowthX, rightGrowthY;
    
    //The normalized vector for the width growth direction
    public float leftVectorX, leftVectorY;
    public float rightVectorX, rightVectorY;
    
    public float[] vertexData = new float[4*3]; //for the previous segment actually
    
    public HolyBlastCtrl() {
        lifeStart = 1;
    }
    
    @Override
    public void toInitValues(){
        super.toInitValues();
        olderCtrl = null;
    }
    
    public void initWith(HolyBlast parent, float initialWidth) {
        posX = parent.posX;
        posY = parent.posY;
        
        //Left part
        final float cos = 0; // PI/2
        float sin = 1; // PI/2
        
        //Vector
        leftVectorX = parent.movX * cos - parent.movY * sin;
        leftVectorY = parent.movX * sin + parent.movY * cos;
        
        //Initial position
        leftGrowthX = posX + initialWidth * leftVectorX;
        leftGrowthY = posY + initialWidth * leftVectorY; //leftVectorX for funny effect!
        
        //Right part
        sin = -1; // -PI/2
        
        rightVectorX = parent.movX * cos - parent.movY * sin;
        rightVectorY = parent.movX * sin + parent.movY * cos;
        
        //Initial position
        rightGrowthX = posX + initialWidth * rightVectorX;
        rightGrowthY = posY + initialWidth * rightVectorY;
        
        
    }
    
    public void updateVerticesBuffer(float prevLeftX, float prevLeftY, float prevRightX, float prevRightY) {
        //vertex bottom left
        vertexData[  0 ] = prevLeftX;
        vertexData[  1 ] = prevLeftY;
        vertexData[  2 ] = 0;
        
        //vertex bottom right
        vertexData[  3 ] = prevRightX;
        vertexData[  4 ] = prevRightY;
        vertexData[  5 ] = 0;
        
        //vertex top left
        vertexData[  6 ] = leftGrowthX;
        vertexData[  7 ] = leftGrowthY;
        vertexData[  8 ] = 0;
        
        //vertex top right
        vertexData[  9 ] = rightGrowthX;
        vertexData[ 10 ] = rightGrowthY;
        vertexData[ 11 ] = 0;
    }

}
