package com.breakingbyte.wrap.shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.wrap.GL;
import com.breakingbyte.wrap.GL2;
import com.breakingbyte.wrap.Platform;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;

public class ImmediateBuffer {
    
    public int MAX_CAPACITY;
    
    //Store all the vertices
    protected FloatBuffer   mVertexBuffer;
    protected int           vertexStride;
    
    //Store all the texture coordinates
    protected FloatBuffer   mTextureBuffer;
    protected int           textureStride;
    
    //Store all the index
    protected ShortBuffer   mIndexBuffer;
    protected int           indexStride;
    
    public ImmediateBuffer(int capacity) {
        MAX_CAPACITY = capacity;
        
        vertexStride = 3*4;
        textureStride = 2*4;
        indexStride = 6;
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(MAX_CAPACITY*vertexStride*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer tbb = ByteBuffer.allocateDirect(MAX_CAPACITY*textureStride*4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(MAX_CAPACITY*indexStride*4);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();
        
    }
    
    public void updateTextureBuffer(float[] textureData) {
        //Transfer through JNI
        mTextureBuffer.put(textureData);
        mTextureBuffer.position(0);
    }
    
    public void updateBuffers(float[] vertexData, float[] textureData) {
        //Transfer through JNI
        mVertexBuffer.put(vertexData);
        mVertexBuffer.position(0);
        mTextureBuffer.put(textureData);
        mTextureBuffer.position(0);
    }
    
    public void updateBuffers(float[] vertexData, float[] textureData, short[] indexData) {
        //Transfer through JNI
        mVertexBuffer.put(vertexData);
        mVertexBuffer.position(0);
        mTextureBuffer.put(textureData);
        mTextureBuffer.position(0);
        mIndexBuffer.put(indexData);
        mIndexBuffer.position(0);
    }
    
    public void render(int nbToDraw) {
        //if (true) return;
        Renderer.preDraw();
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glVertexPointer(3, GL.GL_FLOAT, 0, mVertexBuffer);
            GL.glTexCoordPointer(2, GL.GL_FLOAT, 0, mTextureBuffer);
            GL.glDrawElements(GL.GL_TRIANGLES, indexStride*nbToDraw, GL.GL_UNSIGNED_SHORT, mIndexBuffer); 
        } else {
            GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.position), 3, GL2.GL_FLOAT, false,
                    0, mVertexBuffer);
            GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), 2, GL2.GL_FLOAT, false,
                    0, mTextureBuffer);
            GL2.glDrawElements(GL2.GL_TRIANGLES, indexStride*nbToDraw, GL2.GL_UNSIGNED_SHORT, mIndexBuffer);
        }
    }
    
}
