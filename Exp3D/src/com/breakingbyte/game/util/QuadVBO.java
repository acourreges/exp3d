package com.breakingbyte.game.util;

import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.VBOEnum.Format;
import com.breakingbyte.game.render.VBOEnum.Type;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.shared.ImmediateBuffer;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.VBO;

public class QuadVBO {
    
    private static VBO vertex_VBO;
    private static VBO texture_VBO;
    private static VBO index_VBO;
    
    
    private static ImmediateBuffer immediateBuffer;
    private static ImmediateBuffer immediateBufferCustom;
    
    
    public static final float[] vertexData = {
            -0.5f, -0.5f,  0f,
             0.5f, -0.5f,  0f,
            -0.5f,  0.5f,  0f,
             0.5f,  0.5f,  0f
    };
    
    public static final short[]  indexData = {
            0, 1, 2,   
            2, 3, 1
    };
    
    
    
    public static void initialize() {
        
        vertex_VBO = new VBO(Type.POSITION);
        vertex_VBO.fillBuffer(vertexData);
        vertex_VBO.createGL();
        vertex_VBO.upload();
        
        index_VBO = new VBO(Type.INDEX);
        index_VBO.fillBuffer(indexData);
        index_VBO.createGL();
        index_VBO.upload();
        
        float texCoords[] = {
                0.0f, 1.0f,  //bottom left
                1.0f, 1.0f,  //bottom right
                0.0f, 0.0f,  //top left
                1.0f, 0.0f   //top right
        };
        
        texture_VBO = new VBO(Type.TEXTURE);
        texture_VBO.fillBuffer(texCoords);
        texture_VBO.createGL();
        texture_VBO.upload();

        immediateBuffer = new ImmediateBuffer(1);
        immediateBufferCustom = new ImmediateBuffer(1);
        
        immediateBuffer.updateBuffers(vertexData, texCoords, indexData);
        immediateBufferCustom.updateBuffers(vertexData, texCoords, indexData);
        
    }
    
    public static void drawQuad() {
        
        if (!Debug.noVBO) {
            //Bind vertex        
            vertex_VBO.bindWithFormat(3, Format.FLOAT, 0, 0); 
    
            //Bind texture coordinates
            texture_VBO.bindWithFormat(2, Format.FLOAT, 0, 0); 
            
            //Bind indexes
            index_VBO.bindAsIndex();
            
            VBO.render(6, Format.UNSIGNED_SHORT);
        } else {
            //Bind vertex        
            vertex_VBO.bindImmediate(3, Format.FLOAT, 0); 
    
            //Bind texture coordinates
            texture_VBO.bindImmediate(2, Format.FLOAT, 0); 
            
            //Bind indexes
            index_VBO.renderImmediate(6, Format.UNSIGNED_SHORT);
        }
    }
    
    public static void drawQuad(float x, float y, float zoomX, float zoomY) {
        drawQuadPre(x, y, zoomX, zoomY);
        
        drawQuad();
        
        drawQuadPost();
    }
    
    private static void drawQuadPre(float x, float y, float zoomX, float zoomY) {
        if (x == FontTexture.ALIGN_CENTER) {
            x = Screen.ARENA_WIDTH / 2f;
        }
        
        ////Renderer.unbindVBOs();
        ////Renderer.loadOrtho();
        
        Renderer.pushMatrix();

        Renderer.translate(x, y, 0f); 
        Renderer.scale(zoomX, zoomY, 0f);
    }
    
    private static void drawQuadPost() {
        Renderer.popMatrix();
    }
    
    public static void drawQuadImmediate(float[] vertices, float[] texUVs) {
        
        immediateBufferCustom.updateBuffers(vertices, texUVs);
        
        immediateBufferCustom.render(1);
    }
    
    public static void drawQuadImmediate(float[] texUVs) {
        
        immediateBuffer.updateTextureBuffer(texUVs);
        immediateBuffer.render(1);        
    }
    
    public static void drawQuadImmediate(float x, float y, float zoomX, float zoomY, float[] texUVs) {
        drawQuadPre(x, y, zoomX, zoomY);
        
        immediateBuffer.updateTextureBuffer(texUVs);
        immediateBuffer.render(1);   
        
        drawQuadPost();
    }
    
    

}
