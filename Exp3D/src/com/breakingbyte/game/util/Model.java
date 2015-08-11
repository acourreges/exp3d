package com.breakingbyte.game.util;


import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.StringFetcher;

public class Model {

    private static final String TAG = "Model";
    
    public boolean isLoaded = false;
    private boolean isParsed = false;
    
    public String filePath = "";
    
    public MeshVBOs mesh;
    
    private boolean forLightMapUse = false;
    
    StringFetcher stringFetcher = null;
    
    public int level;
    
    public Model(String filePath){
        this.filePath = filePath;
    }
    
    public Model setForLightMapUse(boolean useLM) {
        this.forLightMapUse = useLM;
        return this;
    }
    
    public void load() {
        
        if (isLoaded) {
            Log.e(TAG, "Model already loaded, ignoring. " + filePath);
            return;
        }
        
        if (stringFetcher == null) {
            Log.d(TAG, "Model reading from file:  " + filePath);
            stringFetcher = new StringFetcher(filePath);
            stringFetcher.fetch();
        }
        
        if (!stringFetcher.fetched) return;
        
        if (isParsed) {
            Log.d(TAG, "Model loading from RAM. " + filePath);
            mesh.uploadVBOs();
        } else {
            mesh = forLightMapUse? OBJLoader.loadLightMapModelFromString(stringFetcher.content) : OBJLoader.loadModelFromString(stringFetcher.content);
            isParsed = true;
        }
        isLoaded = true;
    }
    
    public Model unload() {
        
        if (!isLoaded) {
            Log.e(TAG, "Model not loaded! Can't unload it. " + filePath);
            return this;
        }
        
        Log.d(TAG, "Model unloading. " + filePath);
        mesh.releaseGLResource();
        isLoaded = false;
        return this;

    }
    
}
