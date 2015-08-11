package com.breakingbyte.wrap.shared;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.game.render.VBOEnum.Format;
import com.breakingbyte.game.render.VBOEnum.Type;
import com.breakingbyte.wrap.GL;
import com.breakingbyte.wrap.GL2;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.Platform;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;

public class VBO {
    
    public int id = -1;
    
    public Buffer nativeBuffer;
    public int nativeSize;
    
    public Type vboType;
    
    public VBO(Type vboType) {
        this.vboType = vboType;
    }
    
    public static int [] singleId = new int[1];
    public static IntBuffer singleInteger = null;
    public void createGL() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            
            GL.glGenBuffers(1, singleId, 0);
            id = singleId[0];
        } else {
            if (singleInteger == null) {
                singleInteger = ByteBuffer.allocateDirect(4)
                        .order(ByteOrder.nativeOrder()).asIntBuffer(); 
            }
            GL2.glGenBuffers(1, singleInteger);
            id = singleInteger.get(0);
        }
    }
    
    public void delete() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            singleId[0] = id;
            GL.glDeleteBuffers(1, singleId, 0);
        } else {
            singleInteger.position(0);
            singleInteger.put(id).position(0);
            GL2.glDeleteBuffers(1, singleInteger);
        }
    }
    
    public void fillBuffer(float[] sourceArray) {
        //TODO maybe support multiple calls
        nativeSize = sourceArray.length*4;
        ByteBuffer bb = ByteBuffer.allocateDirect(nativeSize);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(sourceArray);
        fb.position(0);
        nativeBuffer = fb;
    }
    
    public void fillBuffer(short[] sourceArray) {
        //TODO maybe support multiple calls
        nativeSize = sourceArray.length*2;
        ByteBuffer bb = ByteBuffer.allocateDirect(nativeSize);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(sourceArray);
        sb.position(0);
        nativeBuffer = sb;
    }
    
    public void upload() {
        if (Debug.noVBO) return;
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            int ARRAY_TYPE = (vboType == Type.INDEX)? GL.GL_ELEMENT_ARRAY_BUFFER : GL.GL_ARRAY_BUFFER;
            GL.glBindBuffer(ARRAY_TYPE, id);
            GL.glBufferData(ARRAY_TYPE, 
                            nativeSize, 
                            nativeBuffer, 
                            GL.GL_STATIC_DRAW);
        } else {
            int ARRAY_TYPE = (vboType == Type.INDEX)? GL2.GL_ELEMENT_ARRAY_BUFFER : GL2.GL_ARRAY_BUFFER;
            GL2.glBindBuffer(ARRAY_TYPE, id);
            GL2.glBufferData(ARRAY_TYPE, 
                            nativeSize, 
                            nativeBuffer, 
                            GL2.GL_STATIC_DRAW); 
        }
    }

    public void bindAsIndex() {
        if (Debug.noVBO) return;
        bindWithFormat(0, Format.FLOAT, 0, 0);
    }
    
    public void bindWithFormat(int size, Format format, int stride, int pointer) {
        if (Debug.noVBO) return;
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            int nativeFormat = 0;
            switch (format) {
                case FLOAT:
                    nativeFormat = GL.GL_FLOAT;
                    break;
                case UNSIGNED_SHORT:
                    nativeFormat = GL.GL_UNSIGNED_SHORT;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            
            if (vboType == Type.POSITION) {
                GL.glBindBuffer(GL.GL_ARRAY_BUFFER, id);
                GL.glVertexPointer(size, nativeFormat, stride, pointer);
            }
            else if (vboType == Type.TEXTURE) {
                GL.glBindBuffer(GL.GL_ARRAY_BUFFER, id);
                GL.glTexCoordPointer(size, nativeFormat, stride, pointer);
            }
            else if (vboType == Type.NORMAL) {
                GL.glBindBuffer(GL.GL_ARRAY_BUFFER, id);
                GL.glNormalPointer(nativeFormat, stride, pointer);
            }
            else if (vboType == Type.INDEX) {
                GL.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, id);
            }
            
        } else {
            int nativeFormat = 0;
            switch (format) {
                case FLOAT:
                    nativeFormat = GL2.GL_FLOAT;
                    break;
                case UNSIGNED_SHORT:
                    nativeFormat = GL2.GL_UNSIGNED_SHORT;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            
            if (vboType == Type.POSITION) {
                GL2.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.position), size, nativeFormat, false, stride, pointer);
            }
            else if (vboType == Type.TEXTURE) {
                GL2.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), size, nativeFormat, false, stride, pointer);
            }
            else if (vboType == Type.NORMAL) {
                if (ShaderPipeline.currentPipelineBound.hasAttribute(ShaderManager.Attribute.normal)) {
                    GL2.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
                    GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.normal), size, nativeFormat, false, stride, pointer);    
                }
            }
            else if (vboType == Type.INDEX) {
                GL2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, id);
            }
        }
        
    }
    
    //Workaround for platforms where VBOs are buggy. We fallback to immediate mode. (vertex arrays)
    public void bindImmediate(int size, Format format, int stride) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            int nativeFormat = 0;
            switch (format) {
                case FLOAT:
                    nativeFormat = GL.GL_FLOAT;
                    break;
                case UNSIGNED_SHORT:
                    nativeFormat = GL.GL_UNSIGNED_SHORT;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            if (vboType == Type.POSITION) {
                GL.glVertexPointer(size, nativeFormat, stride, nativeBuffer);
            }
            else if (vboType == Type.TEXTURE) {
                GL.glTexCoordPointer(size, nativeFormat, stride, nativeBuffer);
            }
            else if (vboType == Type.NORMAL) {
                GL.glNormalPointer(nativeFormat, stride, nativeBuffer);
            }
            
        } else {
            int nativeFormat = 0;
            switch (format) {
                case FLOAT:
                    nativeFormat = GL2.GL_FLOAT;
                    break;
                case UNSIGNED_SHORT:
                    nativeFormat = GL2.GL_UNSIGNED_SHORT;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            
            if (vboType == Type.POSITION) {
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.position), size, nativeFormat, false, stride, nativeBuffer);
            }
            else if (vboType == Type.TEXTURE) {
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), size, nativeFormat, false, stride, nativeBuffer);
            }
            else if (vboType == Type.NORMAL) {
                if (ShaderPipeline.currentPipelineBound.hasAttribute(ShaderManager.Attribute.normal)) {
                    GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.normal), size, nativeFormat, false, stride, nativeBuffer);    
                }
            }
        }
        
    }
    
    public void renderImmediate(int number, Format format) {
        Renderer.preDraw();
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            int nativeFormat = 0;
            switch (format) {
                case UNSIGNED_SHORT:
                    nativeFormat = GL.GL_UNSIGNED_SHORT;
                    break;
                case UNSIGNED_BYTE:
                    nativeFormat = GL.GL_UNSIGNED_BYTE;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            GL.glDrawElements(GL.GL_TRIANGLES, number, nativeFormat, nativeBuffer);
        } else {
            int nativeFormat = 0;
            switch (format) {
                case UNSIGNED_SHORT:
                    nativeFormat = GL2.GL_UNSIGNED_SHORT;
                    break;
                case UNSIGNED_BYTE:
                    nativeFormat = GL2.GL_UNSIGNED_BYTE;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            GL2.glDrawElements(GL2.GL_TRIANGLES, number, nativeFormat, nativeBuffer);
        }
    }
    
    public static void render(int number, Format format) {
        if (Debug.noVBO) return;
        Renderer.preDraw();
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            int nativeFormat = 0;
            switch (format) {
                case UNSIGNED_SHORT:
                    nativeFormat = GL.GL_UNSIGNED_SHORT;
                    break;
                case UNSIGNED_BYTE:
                    nativeFormat = GL.GL_UNSIGNED_BYTE;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            GL.glDrawElements(GL.GL_TRIANGLES, number, nativeFormat, 0);
        } else {
            int nativeFormat = 0;
            switch (format) {
                case UNSIGNED_SHORT:
                    nativeFormat = GL2.GL_UNSIGNED_SHORT;
                    break;
                case UNSIGNED_BYTE:
                    nativeFormat = GL2.GL_UNSIGNED_BYTE;
                    break;
                default:
                    Log.e("VBO", "Format not supported");
                    break;
            }
            GL2.glDrawElements(GL2.GL_TRIANGLES, number, nativeFormat, 0);
        }
    }

}
