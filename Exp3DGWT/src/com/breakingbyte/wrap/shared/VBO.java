package com.breakingbyte.wrap.shared;


import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.game.render.VBOEnum.Format;
import com.breakingbyte.game.render.VBOEnum.Type;
import com.breakingbyte.wrap.Log;
import com.client.Exp3DGWT;
import com.googlecode.gwtgl.array.Float32Array;
import com.googlecode.gwtgl.array.Uint16Array;
import com.googlecode.gwtgl.binding.WebGLBuffer;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

public class VBO {
    
    public static final String TAG = "VBO";
    
    public WebGLBuffer webglBuffer;
    public int nativeSize;
    
    public Float32Array innerBufferFloat;
    public Uint16Array innerBufferShort;
    
    public Type vboType;
    
    public VBO(Type vboType) {
        this.vboType = vboType;
    }
    
    public static int [] singleId = new int[1];
    public void createGL() {
        webglBuffer = Exp3DGWT.glContext.createBuffer();
        //Log.d(TAG, "Created new VBO " + webglBuffer.toString());
    }
    
    public void delete() {
        Exp3DGWT.glContext.deleteBuffer(webglBuffer);
    }
    
    public void fillBuffer(float[] sourceArray) {
        //TODO maybe support multiple calls
        innerBufferFloat = Float32Array.create(sourceArray);
        nativeSize = sourceArray.length*4;
    }
    
    public void fillBuffer(short[] sourceArray) {
        //TODO maybe support multiple calls
        int[] tmp = new int[sourceArray.length];
        for (int i = 0; i < sourceArray.length; i++) tmp[i] = sourceArray[i]; //TODO optimize this
        innerBufferShort = Uint16Array.create(tmp);
        nativeSize = sourceArray.length*2;
    }
    
    public void upload() {
        int ARRAY_TYPE = (vboType == Type.INDEX)? WebGLRenderingContext.ELEMENT_ARRAY_BUFFER : WebGLRenderingContext.ARRAY_BUFFER;
        if (innerBufferFloat != null) {
            //Log.d("TAG", "Upload buffer float of bytes " + innerBufferFloat.getByteLength());
            Exp3DGWT.glContext.bindBuffer(ARRAY_TYPE, webglBuffer);
            Exp3DGWT.glContext.bufferData(ARRAY_TYPE, innerBufferFloat, WebGLRenderingContext.STATIC_DRAW);
        }
        else if (innerBufferShort != null) {
            //Log.d("TAG", "Upload buffer short of bytes " + innerBufferShort.getByteLength());
            Exp3DGWT.glContext.bindBuffer(ARRAY_TYPE, webglBuffer);
            Exp3DGWT.glContext.bufferData(ARRAY_TYPE, innerBufferShort, WebGLRenderingContext.STATIC_DRAW);
        }
        else {
            Log.e("VBO", "Both short buffer and int buffers are null!");
        }
    }

    public void bindAsIndex() {
        bindWithFormat(0, Format.FLOAT, 0, 0);
    }
    
    public void bindWithFormat(int size, Format format, int stride, int pointer) {
        

        int nativeFormat = 0;
        switch (format) {
            case FLOAT:
                nativeFormat = WebGLRenderingContext.FLOAT;
                break;
            case UNSIGNED_SHORT:
                nativeFormat = WebGLRenderingContext.UNSIGNED_SHORT;
                break;
            default:
                Log.e("VBO", "Format not supported");
                break;
        }
        
        if (vboType == Type.POSITION) {
            Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, webglBuffer);
            Exp3DGWT.glContext.vertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.position), size, nativeFormat, false, stride, pointer);
        }
        else if (vboType == Type.TEXTURE) {
            Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, webglBuffer);
            Exp3DGWT.glContext.vertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), size, nativeFormat, false, stride, pointer);
        }
        else if (vboType == Type.NORMAL) {
            if (ShaderPipeline.currentPipelineBound.hasAttribute(ShaderManager.Attribute.normal)) {
                Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, webglBuffer);
                Exp3DGWT.glContext.vertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.normal), size, nativeFormat, false, stride, pointer);
            }
        }
        else if (vboType == Type.INDEX) {
            Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, webglBuffer);
        }

        
    }
    
    //Workaround for platforms where VBOs are buggy. We fallback to immediate mode. (vertex arrays)
    public void bindImmediate(int size, Format format, int stride) {
        Log.e("VBO", "Immediate mode is not supported in WebGL. All is handled by vertex buffers");
    }
    
    public void renderImmediate(int number, Format format) {
        Log.e("VBO", "Immediate mode is not supported in WebGL. All is handled by vertex buffers");
    }
    
    public static void render(int number, Format format) {
        Renderer.preDraw();
        int nativeFormat = 0;
        switch (format) {
            case UNSIGNED_SHORT:
                nativeFormat = WebGLRenderingContext.UNSIGNED_SHORT;
                break;
            case UNSIGNED_BYTE:
                nativeFormat = WebGLRenderingContext.UNSIGNED_BYTE;
                break;
            default:
                Log.e("VBO", "Format not supported");
                break;
        }
        Exp3DGWT.glContext.drawElements(WebGLRenderingContext.TRIANGLES, number, nativeFormat, 0);
    }

}
