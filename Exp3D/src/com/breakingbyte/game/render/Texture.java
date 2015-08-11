package com.breakingbyte.game.render;

import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.resource.Resource.ResourceLevel;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.NativeTexture;
import com.breakingbyte.wrap.shared.Renderer;

public class Texture {
    
    private static final String TAG = "Texture";
    
    public boolean isLoaded = false;
    
    public boolean useMipMap = false;
    
    private String sourceFilePath = "";
    
    public NativeTexture nativeTexture = null;
    
    public int level;
    
    public Texture(String filePath) {
        this.sourceFilePath = filePath;
        this.level = ResourceLevel.ENGINE;
    }
    
    public Texture useMipMap(boolean use) {
        if (Debug.noMipMap) {
            this.useMipMap = false;
            return this;
        }
        this.useMipMap = use;
        return this;
    }
    
    public void load() {
        
        if (isLoaded) {
            Log.e(TAG, "Image already loaded, ignoring. " + sourceFilePath);
            return;
        }
        
        Log.d(TAG, "Image loading. " + sourceFilePath);
        
        if (nativeTexture == null) {
            nativeTexture = new NativeTexture(sourceFilePath);
            nativeTexture.setMipMap(useMipMap);
            nativeTexture.fetch();
        }
        
        if (!nativeTexture.fetched) return; //load() will have to be called again later to complete the upload in the case fetch is not immediate
        
        nativeTexture.uploadToGPU();
        isLoaded = true;
    }
    
    public void unload() {
        if (!isLoaded) {
            Log.e(TAG, "Texture not loaded! Can't unload it. " + sourceFilePath);
        }
        
        Log.d(TAG, "Image unloading. " + sourceFilePath);
        
        nativeTexture.unloadFromGPU();
        isLoaded = false;
        
    }
    
    public void bind() {
        Renderer.bindTexture(this);
    }
    
    public boolean isLoaded() {
        return isLoaded;
    }
    
    public void nativeBind() {
        nativeTexture.nativeBind();
    }

}
