package com.breakingbyte.wrap;

import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;

public class NativeTexture {
    
    //Public members
    public boolean fetched = false;
    
    public boolean mipmap = false;
    
    public String filePath = null;
    
    //Private - proper to native
    public int glTextureID;
    
    public NativeTexture(String filePath) {
        this.filePath = filePath;
    }
    
    public void setMipMap(boolean value) {
        this.mipmap = value;
    }
    
    public void fetch() {
        //immediate operation
        fetched = true; 
    }
    
    public void uploadToGPU() {
        if (mipmap) {
            glTextureID = TextureUtil.loadMipMappedTexture(filePath);
        } else {
            glTextureID = TextureUtil.loadTexture(filePath);
        }
    }
    
    public void unloadFromGPU() {
        TextureUtil.unloadTexture(glTextureID);
        glTextureID = 0;
    }
    
    public void nativeBind() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glBindTexture(GL.GL_TEXTURE_2D, glTextureID);
        } else {
            GL2.glBindTexture(GL2.GL_TEXTURE_2D, glTextureID);
        }
    }

}
