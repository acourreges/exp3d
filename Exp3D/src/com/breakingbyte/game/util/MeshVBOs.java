package com.breakingbyte.game.util;

import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.render.VBOEnum.Format;
import com.breakingbyte.wrap.shared.VBO;


public class MeshVBOs {
    
    //Buffers in native memory (not used in VBO mode)
    /*
    public FloatBuffer  mVertexBuffer   = null;
    public FloatBuffer  mTextureBuffer  = null;
    public FloatBuffer  mNormalBuffer   = null;
    public ShortBuffer  mIndexBuffer    = null;
    */
    
    //Number of indexes and their type
    public int nbIndex;
    
    
    public VBO vertex_VBO = null;
    public VBO texture_VBO = null;
    public VBO normal_VBO = null;
    public VBO index_VBO = null;
    
    
    // These 3 methods can be called only in VBO mode (Renderer.switchToVBOMode();)

    public void bindVBOs() {
        
        if (!Debug.noVBO) {
            //Bind vertex
            vertex_VBO.bindWithFormat(3, Format.FLOAT, 0, 0);
            
            //Bind texture coordinates
            texture_VBO.bindWithFormat(2, Format.FLOAT, 0, 0);
            
            if (normal_VBO != null){
                normal_VBO.bindWithFormat(3, Format.FLOAT, 0, 0);
            }
            
            //Bind indexes
            index_VBO.bindAsIndex();
        } else {
            //Bind vertex
            vertex_VBO.bindImmediate(3, Format.FLOAT, 0);
            
            //Bind texture coordinates
            texture_VBO.bindImmediate(2, Format.FLOAT, 0);
            
            if (normal_VBO != null){
                normal_VBO.bindImmediate(3, Format.FLOAT, 0);
            }
            
            //Bind indexes
            //index_VBO.bindAsIndex();
        }
               
    }

    public void render() {        
        bindVBOs();
        if (!Debug.noVBO) VBO.render(nbIndex, Format.UNSIGNED_SHORT);
        else {
            index_VBO.renderImmediate(nbIndex, Format.UNSIGNED_SHORT);
        }
    }
    
    public void renderDrawOnly() {
        if (!Debug.noVBO) VBO.render(nbIndex, Format.UNSIGNED_SHORT);
        else index_VBO.renderImmediate(nbIndex, Format.UNSIGNED_SHORT);
    }

    
    // -------------- Non-VBO rendering --------------
    /*
    public void render() {
        
        bindVBOs();
        GL.glDrawElements(GL.GL_TRIANGLES, nbIndex, indexType, mIndexBuffer);

    }
    
    public void bindVBOs() {
        GL.glVertexPointer(3, GL.GL_FLOAT, 0,mVertexBuffer);
        GL.glTexCoordPointer(2, GL.GL_FLOAT, 0, mTextureBuffer);
    }

    
    public void renderDrawOnly() {
        GL.glDrawElements(GL.GL_TRIANGLES, nbIndex, indexType, mIndexBuffer);
    }
    */
    // -----------------------------------------------
    
    
    
    public void uploadVBOs() {
        
        //Vertex
        if (vertex_VBO != null) {
            vertex_VBO.createGL();
            vertex_VBO.upload();
        }
        if (texture_VBO != null) {
            texture_VBO.createGL();
            texture_VBO.upload();
        }
        if (normal_VBO != null) {
            normal_VBO.createGL();
            normal_VBO.upload();
        }
        if (index_VBO != null) {
            index_VBO.createGL();
            index_VBO.upload();
        }
        
        //Could free buffers
        //mVertexBuffer  = null;
        //mTextureBuffer = null;
        //mNormalBuffer = null;
        //mIndexBuffer   = null;

    }
    
    public void releaseGLResource() {
        
        if (vertex_VBO != null)  vertex_VBO.delete();
        if (texture_VBO != null) texture_VBO.delete();
        if (normal_VBO != null)  normal_VBO.delete();
        if (index_VBO != null)   index_VBO.delete();
    }

}
