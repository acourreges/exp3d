package com.breakingbyte.wrap.shared;


import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.wrap.Platform;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;
import com.client.Exp3DGWT;
import com.googlecode.gwtgl.array.Float32Array;
import com.googlecode.gwtgl.array.Uint16Array;
import com.googlecode.gwtgl.binding.WebGLBuffer;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

public class ImmediateBuffer {
    
    public int MAX_CAPACITY;
    
    //Store all the vertices
    //protected FloatBuffer   mVertexBuffer;
    protected int           vertexStride;
    
    //Store all the texture coordinates
    //protected FloatBuffer   mTextureBuffer;
    protected int           textureStride;
    
    //Store all the index
    //protected ShortBuffer   mIndexBuffer;
    protected int           indexStride;
    
    public Float32Array mVertexBuffer;
    public Float32Array mTextureBuffer;
    public Uint16Array mIndexBuffer;
    
    public static WebGLBuffer webglVertexBuffer;
    public static WebGLBuffer webglTextureBuffer;
    public static WebGLBuffer webglIndexBuffer;
    
    public ImmediateBuffer(int capacity) {
        MAX_CAPACITY = capacity;
        
        vertexStride = 3*4;
        textureStride = 2*4;
        indexStride = 6;
        
        mVertexBuffer = Float32Array.create(MAX_CAPACITY * vertexStride);
        mTextureBuffer = Float32Array.create(MAX_CAPACITY * textureStride);
        mIndexBuffer = Uint16Array.create(MAX_CAPACITY * indexStride);
        
    }
    
    public static boolean buffersCreated = false;
    public void createBuffersIfNecessary() {
        if (buffersCreated) return;
        buffersCreated = true;
        
        webglVertexBuffer = Exp3DGWT.glContext.createBuffer();
        webglTextureBuffer = Exp3DGWT.glContext.createBuffer();
        webglIndexBuffer = Exp3DGWT.glContext.createBuffer(); 
    }
    
    public void updateTextureBuffer(float[] textureData) {
        createBuffersIfNecessary();
        mTextureBuffer.set(textureData);
        
    }
    
    public void updateBuffers(float[] vertexData, float[] textureData) {
        createBuffersIfNecessary();
        mVertexBuffer.set(vertexData);
        mTextureBuffer.set(textureData);
    }
    
    public void updateBuffers(float[] vertexData, float[] textureData, short[] indexData) {
        createBuffersIfNecessary();
        //Transfer through JNI
        mVertexBuffer.set(vertexData);
        mTextureBuffer.set(textureData);
        int[] tmp = new int[indexData.length];
        for (int i = 0; i < indexData.length; i++) tmp[i] = indexData[i]; //TODO optimize this
        mIndexBuffer.set(tmp);
    }
    
    public void render(int nbToDraw) {
        //if (true) return;
        Renderer.preDraw();
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            /*
            GL.glVertexPointer(3, GL.GL_FLOAT, 0, mVertexBuffer);
            GL.glTexCoordPointer(2, GL.GL_FLOAT, 0, mTextureBuffer);
            GL.glDrawElements(GL.GL_TRIANGLES, indexStride*nbToDraw, GL.GL_UNSIGNED_SHORT, mIndexBuffer); 
            */
        } else {
            Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, webglVertexBuffer);
            Exp3DGWT.glContext.bufferData(WebGLRenderingContext.ARRAY_BUFFER, mVertexBuffer, WebGLRenderingContext.DYNAMIC_DRAW);
            Exp3DGWT.glContext.vertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.position), 3, WebGLRenderingContext.FLOAT, false, 0, 0);
            
            Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, webglTextureBuffer);
            Exp3DGWT.glContext.bufferData(WebGLRenderingContext.ARRAY_BUFFER, mTextureBuffer, WebGLRenderingContext.DYNAMIC_DRAW);
            Exp3DGWT.glContext.vertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), 2, WebGLRenderingContext.FLOAT, false, 0, 0);
            
            Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, webglIndexBuffer);
            Exp3DGWT.glContext.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, mIndexBuffer, WebGLRenderingContext.DYNAMIC_DRAW);
            
            Exp3DGWT.glContext.drawElements(WebGLRenderingContext.TRIANGLES, indexStride*nbToDraw, WebGLRenderingContext.UNSIGNED_SHORT, 0);
            
        }
    }
    
}
