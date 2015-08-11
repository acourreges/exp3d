package com.breakingbyte.game.engine;


public class Debug {
    
    public static final boolean isRelease = true;
    
    //Test mode
    public static final boolean devMode = false;
    public static final boolean jumpToGame = false;
    public static final boolean fixedTimestep = false;
    public static final boolean godMode = false;
    
    //In-App purchases
    public static final boolean flushInAppPurchases = false;
    
    //Collisions
    public static boolean performCollisionDetection = true;
    public static boolean drawBoundingBoxes = false;
    
    //Rendering
    public static boolean useLightmap = true;
    public static final boolean noVBO = false;
    public static final boolean noMipMap = false;

    //Text textures
    public static boolean saveTextTextureToFile = false;
    public static boolean fontTextureDebugBG = false;
    
}
