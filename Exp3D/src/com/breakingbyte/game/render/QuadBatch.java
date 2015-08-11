package com.breakingbyte.game.render;

import java.util.ArrayList;

import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.wrap.shared.ImmediateBuffer;

/**
 * Immediate rendering of a set a quads/sprites in a one-shot draw-call. 
 * Each quad supports translation, rotation, scale.
 */

public class QuadBatch {
    
    public int MAX_CAPACITY;
    
    ImmediateBuffer immediateBuffers;
    
    private class Quad {
        
        //Used if vertArray is null
        public float posX = 0f;
        public float posY = 0f;
        public float posZ = 0f;
        public float leftWidth = 1f;
        public float rightWidth = 1f;
        public float upperHeight = 1f;
        public float lowerHeight = 1f;
        public float scale = 1f;
        public float angle = 0f; //in degree
        
        public float[] vertArray = null;
        
        //If texArray is null, texCoords will be used
        public TexCoord[] texCoords = new TexCoord[4];
        public float[] texArray = null;
        
        public Quad() {
            for (int i = 0; i < 4; i++) texCoords[i] = new TexCoord();
        }
    }
    
    private class TexCoord {
        public float u = 0;
        public float v = 0;
    }
    
    private ArrayList<Quad> quads;
    private int nbQuads;
    
    //Store all the vertices
    protected float[]       vertexData;
    protected int           vertexStride;
    
    //Store all the texture coordinates
    protected float[]       textureData;
    protected int           textureStride;
    
    //Store all the index
    protected short[]       indexData;
    protected int           indexStride;
    
    public QuadBatch(int capacity) {
        
        MAX_CAPACITY = capacity;
        
        nbQuads = 0;
        
        quads = new ArrayList<QuadBatch.Quad>(capacity);
        for (int i = 0; i < capacity; i++) quads.add(new Quad());
        
        immediateBuffers = new ImmediateBuffer(capacity);
        
        vertexStride = 3*4;
        textureStride = 2*4;
        indexStride = 6;
        
        vertexData = new float[MAX_CAPACITY*vertexStride];
        
        textureData = new float[MAX_CAPACITY*textureStride];
        
        indexData = new short[MAX_CAPACITY*indexStride];
    }
    
    public void clearBatch() {
        nbQuads = 0;
    }
    
    static final float texCoords[] = {
        0.0f, 1.0f,  //bottom left
        1.0f, 1.0f,  //bottom right
        0.0f, 1.0f,  //top left
        1.0f, 1.0f   //top right
    };
    
    public void addQuad(
            float left, 
            float right, 
            float top, 
            float bottom)
    {
        Quad quad = quads.get(nbQuads++);
        quad.leftWidth = left;
        quad.rightWidth = right;
        quad.upperHeight = top;
        quad.lowerHeight = bottom;
        quad.vertArray = null;
        quad.texArray = texCoords;
    }
    
    public void addQuadWithUV(
            float left, 
            float right, 
            float top, 
            float bottom,
            float uLeft,
            float uRight,
            float vTop,
            float vBottom
            )
    {
        Quad quad = quads.get(nbQuads++);
        quad.leftWidth = left;
        quad.rightWidth = right;
        quad.upperHeight = top;
        quad.lowerHeight = bottom;
        quad.texCoords[0].u = uLeft;
        quad.texCoords[0].v = vBottom;
        quad.texCoords[1].u = uRight;
        quad.texCoords[1].v = vBottom;
        quad.texCoords[2].u = uLeft;
        quad.texCoords[2].v = vTop;
        quad.texCoords[3].u = uRight;
        quad.texCoords[3].v = vTop;
        quad.vertArray = null;
        quad.texArray = null;
    }
    
    public void addQuad(
            float posX, 
            float posY, 
            float leftWidth, 
            float rightWidth,
            float upperHeight,
            float lowerHeight,
            float scale,
            float angle)
    {
        Quad quad = quads.get(nbQuads++);
        quad.posX = posX;
        quad.posY = posY;
        quad.leftWidth = leftWidth;
        quad.rightWidth = rightWidth;
        quad.upperHeight = upperHeight;
        quad.lowerHeight = lowerHeight;
        quad.scale = scale;
        quad.angle = angle;
        quad.vertArray = null;
        quad.texArray = null;
    }
    
    public void addQuadWithUV(
            float posX, 
            float posY, 
            float leftWidth, 
            float rightWidth,
            float upperHeight,
            float lowerHeight,
            float scale,
            float angle,
            float bottomLeftU,
            float bottomLeftV,
            float bottomRightU,
            float bottomRightV,
            float topLeftU,
            float topLeftV,
            float topRightU,
            float topRightV)
    {
        Quad quad = quads.get(nbQuads++);
        quad.posX = posX;
        quad.posY = posY;
        quad.leftWidth = leftWidth;
        quad.rightWidth = rightWidth;
        quad.upperHeight = upperHeight;
        quad.lowerHeight = lowerHeight;
        quad.scale = scale;
        quad.angle = angle;
        quad.texCoords[0].u = bottomLeftU;
        quad.texCoords[0].v = bottomLeftV;
        quad.texCoords[1].u = bottomRightU;
        quad.texCoords[1].v = bottomRightV;
        quad.texCoords[2].u = topLeftU;
        quad.texCoords[2].v = topLeftV;
        quad.texCoords[3].u = topRightU;
        quad.texCoords[3].v = topRightV;
        quad.vertArray = null;
        quad.texArray = null;
    }
    
    public void addQuadWithUV(
            float posX, 
            float posY, 
            float leftWidth, 
            float rightWidth,
            float upperHeight,
            float lowerHeight,
            float scale,
            float angle,
            float[] texture)
    {
        Quad quad = quads.get(nbQuads++);
        quad.posX = posX;
        quad.posY = posY;
        quad.leftWidth = leftWidth;
        quad.rightWidth = rightWidth;
        quad.upperHeight = upperHeight;
        quad.lowerHeight = lowerHeight;
        quad.scale = scale;
        quad.angle = angle;
        quad.vertArray = null;
        quad.texArray = texture;
    }
    
    public void addQuad(Entity entity) {
        Quad quad = quads.get(nbQuads++);
        quad.posX = entity.posX;
        quad.posY = entity.posY;
        quad.leftWidth = entity.leftWidth;
        quad.rightWidth = entity.rightWidth;
        quad.upperHeight = entity.upperHeight;
        quad.lowerHeight = entity.lowerHeight;
        quad.scale = entity.scale;
        quad.angle = entity.rotZ;
        quad.vertArray = null;
        quad.texArray = entity.getTextureArray();
    }
    
    public void addQuadWithUV(float[] vertices, float[] texture) {
        Quad quad = quads.get(nbQuads++);
        quad.vertArray = vertices;
        quad.texArray = texture;
    }
    
    public void updateBuffers() {
        //Build data in the JVM
        updateJVMBuffer();
        
        //Transfer through JNI
        immediateBuffers.updateBuffers(vertexData, textureData, indexData);
    }
    
    public void render() {
        
        if (nbQuads == 0) return;
        
        int nbToDraw = nbQuads;
        if (nbToDraw >= MAX_CAPACITY) nbToDraw = MAX_CAPACITY;
        
        //Draw
        immediateBuffers.render(nbToDraw);
        
    }
    
    public void updateJVMBuffer() {
        
        int nbEntities = nbQuads;
        
        for (int i = 0; i < nbEntities; i++) {
            
            if (i >= MAX_CAPACITY) break;
            
            final Quad quad = quads.get(i);
            
            //------ Vertices ------
            final int indexV = i * vertexStride;
            
            if (quad.vertArray != null) {
                //System.arraycopy(quad.vertArray, 0, vertexData, indexV, 12);
                vertexData[0 + indexV]  = quad.vertArray[0];
                vertexData[1 + indexV]  = quad.vertArray[1];
                vertexData[2 + indexV]  = quad.vertArray[2];
                vertexData[3 + indexV]  = quad.vertArray[3];
                vertexData[4 + indexV]  = quad.vertArray[4];
                vertexData[5 + indexV]  = quad.vertArray[5];
                vertexData[6 + indexV]  = quad.vertArray[6];
                vertexData[7 + indexV]  = quad.vertArray[7];
                vertexData[8 + indexV]  = quad.vertArray[8];
                vertexData[9 + indexV]  = quad.vertArray[9];
                vertexData[10 + indexV] = quad.vertArray[10];
                vertexData[11 + indexV] = quad.vertArray[11];
                
            } else {
                //Cache cos and sin
                final float cos;
                final float sin;
                if (quad.angle != 0) {
                    float angle = quad.angle * MathUtil.degreesToRadians;
                    cos = (float)Math.cos(angle);
                    sin = (float)Math.sin(angle);
                } else {
                    cos = 1f;
                    sin = 0f;
                }
                
                //Cache rendering dimensions
                final float entityLeftWidth   = quad.leftWidth   * quad.scale;
                final float entityRightWidth  = quad.rightWidth  * quad.scale;
                final float entityLowerHeight = quad.lowerHeight * quad.scale;
                final float entityUpperHeight = quad.upperHeight * quad.scale;
                
                
                //vertex bottom left
                vertexData[ indexV      ] 
                           = quad.posX + (entityLeftWidth * cos - entityLowerHeight * sin);
                vertexData[ indexV + 1  ] 
                           = quad.posY + (entityLeftWidth * sin + entityLowerHeight * cos);
                vertexData[ indexV + 2  ] 
                           = quad.posZ;
                
                //vertex bottom right
                vertexData[ indexV + 3  ] 
                           = quad.posX + (entityRightWidth * cos - entityLowerHeight * sin);
                vertexData[ indexV + 4  ] 
                           = quad.posY + (entityRightWidth * sin + entityLowerHeight * cos);
                vertexData[ indexV + 5  ] 
                           = quad.posZ;
                
                //vertex top left
                vertexData[ indexV + 6  ] 
                           = quad.posX + (entityLeftWidth * cos - entityUpperHeight * sin);
                vertexData[ indexV + 7  ] 
                           = quad.posY + (entityLeftWidth * sin + entityUpperHeight * cos);
                vertexData[ indexV + 8  ] 
                           = quad.posZ;
                
                //vertex top right
                vertexData[ indexV + 9  ] 
                           = quad.posX + (entityRightWidth * cos - entityUpperHeight * sin);
                vertexData[ indexV + 10 ] 
                           = quad.posY + (entityRightWidth * sin + entityUpperHeight * cos);
                vertexData[ indexV + 11 ] 
                           = quad.posZ;
            
            }
            //------ Textures ------
            final int indexT = i * textureStride;
            
            if (quad.texArray != null) {
                //System.arraycopy(quad.texArray, 0, textureData, indexT, 8);
                textureData[0 + indexT]  = quad.texArray[0];
                textureData[1 + indexT]  = quad.texArray[1];
                textureData[2 + indexT]  = quad.texArray[2];
                textureData[3 + indexT]  = quad.texArray[3];
                textureData[4 + indexT]  = quad.texArray[4];
                textureData[5 + indexT]  = quad.texArray[5];
                textureData[6 + indexT]  = quad.texArray[6];
                textureData[7 + indexT]  = quad.texArray[7];
            } else {
                textureData[ indexT     ] = quad.texCoords[0].u;
                textureData[ indexT + 1 ] = quad.texCoords[0].v;
                
                textureData[ indexT + 2 ] = quad.texCoords[1].u;
                textureData[ indexT + 3 ] = quad.texCoords[1].v;
                
                textureData[ indexT + 4 ] = quad.texCoords[2].u;
                textureData[ indexT + 5 ] = quad.texCoords[2].v;
                
                textureData[ indexT + 6 ] = quad.texCoords[3].u;
                textureData[ indexT + 7 ] = quad.texCoords[3].v;
            }
            
            //------ Indices ------
            final int indexI = i * indexStride;
            final int vertIndex = i * 4;
            
            indexData[ indexI     ] = (short)(vertIndex);
            indexData[ indexI + 1 ] = (short)(vertIndex + 1);
            indexData[ indexI + 2 ] = (short)(vertIndex + 2);
            
            indexData[ indexI + 3 ] = (short)(vertIndex + 2);
            indexData[ indexI + 4 ] = (short)(vertIndex + 3);
            indexData[ indexI + 5 ] = (short)(vertIndex + 1);
        } 
    }
    

}
