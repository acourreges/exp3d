package com.breakingbyte.wrap;

import com.client.Exp3DGWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLTexture;

public class NativeTexture {
    
    //Public members
    public boolean fetched = false;
    
    public boolean mipmap = false;
    
    public String filePath = null;
    
    //Private - proper to native
    private Image img = null;
    
    private WebGLTexture webglTexture;
    
    public NativeTexture(String filePath) {
        this.filePath = filePath;
    }
    
    public void setMipMap(boolean value) {
        this.mipmap = value;
    }
    
    public void fetch() {
        if (img == null) {
            img = new Image("assets/" + filePath);
            img.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                            RootPanel.get().remove(img);
                            fetched = true;
                    }
                    
            });
            img.setVisible(false);
            RootPanel.get().add(img);
        }
    }
    
    public void uploadToGPU() {
        webglTexture = Exp3DGWT.glContext.createTexture();
        Exp3DGWT.glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, webglTexture);
        Exp3DGWT.glContext.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, img.getElement());
        if (mipmap) {
            Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
            Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_LINEAR);
            Exp3DGWT.glContext.generateMipmap(WebGLRenderingContext.TEXTURE_2D);
        } else {
            Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
            Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR);
        }
        Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_S, WebGLRenderingContext.REPEAT);
        Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_T, WebGLRenderingContext.REPEAT);
        
    }
    
    public void unloadFromGPU() {
        Exp3DGWT.glContext.deleteTexture(webglTexture);
        webglTexture = null;
    }
    
    public void nativeBind() {
        Exp3DGWT.glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, webglTexture);
    }

}
