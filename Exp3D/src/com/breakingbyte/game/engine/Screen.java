package com.breakingbyte.game.engine;

import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;

public class Screen {
    
    private static final String TAG = "Screen";
    
    // Pixels available (window, hardware screen)
    public static int SCREEN_HW_PIXEL_HEIGHT; 
    public static int SCREEN_HW_PIXEL_WIDTH;
    
    // Viewport to draw the main scene
    public static int VIEWPORT_X; 
    public static int VIEWPORT_Y;
    
    public static int VIEWPORT_HEIGHT; 
    public static int VIEWPORT_WIDTH;

    // Arena for the game
    public static float ARENA_HEIGHT;
    public static float ARENA_WIDTH = 100f;
    
    public static float PIXEL_TO_ARENA_UNIT;
    
    public static int AD_HEIGHT = 0;
    
    public static boolean isViewportFullScreen = true;
    
    public static void surfaceChanged(int screenWidth, int screenHeight) {
        
        Log.d(TAG, "New resolution: " + screenWidth + "x" + screenHeight);
        
        if (screenWidth < 1) screenWidth = 1;
        if (screenHeight < 2) screenHeight = 2;
        
        SCREEN_HW_PIXEL_WIDTH = screenWidth;
        SCREEN_HW_PIXEL_HEIGHT = screenHeight;
        
        Renderer.setViewport(0, 0, SCREEN_HW_PIXEL_WIDTH, SCREEN_HW_PIXEL_HEIGHT);
        Renderer.setClearColor(0f, 0f, 0f, 1f);
        Renderer.clear(true, true);
        
        //How much place is taken by the ad at the bottom
        SCREEN_HW_PIXEL_HEIGHT -= AD_HEIGHT;
        screenHeight -= AD_HEIGHT;
        
        //If screen too large, limit the resolution 
        final float maxRatio = 900f/ 480f; 
        final float minRatio = 800f/ 550f; //800f/ 480f;
        
        if (screenWidth > screenHeight / minRatio) {
            //Large width
            VIEWPORT_HEIGHT = screenHeight;
            VIEWPORT_WIDTH = (int)(screenHeight / minRatio);
            VIEWPORT_Y = 0;
            VIEWPORT_X = (SCREEN_HW_PIXEL_WIDTH - VIEWPORT_WIDTH) / 2;
            isViewportFullScreen = false;
        }
        
        else if (screenHeight > screenWidth * maxRatio) {
            //Long height
            VIEWPORT_WIDTH = screenWidth;
            VIEWPORT_HEIGHT = (int)(screenWidth * maxRatio);
            VIEWPORT_X = 0;
            VIEWPORT_Y = (SCREEN_HW_PIXEL_HEIGHT - VIEWPORT_HEIGHT)/2;
            isViewportFullScreen = false;
        }
        
        else {
            //Uses all pixels available
            VIEWPORT_X = VIEWPORT_Y = 0;
            VIEWPORT_WIDTH = SCREEN_HW_PIXEL_WIDTH;
            VIEWPORT_HEIGHT = SCREEN_HW_PIXEL_HEIGHT;  
            isViewportFullScreen = true;
        }
        
        float ratio = (float)VIEWPORT_HEIGHT / VIEWPORT_WIDTH;
        ARENA_HEIGHT = ratio * ARENA_WIDTH;  
        
        PIXEL_TO_ARENA_UNIT = Screen.ARENA_WIDTH / Screen.VIEWPORT_WIDTH;
        
        //Log.d(TAG, "Viewport  " + VIEWPORT_WIDTH + "x" + VIEWPORT_HEIGHT);
        
        VIEWPORT_Y += AD_HEIGHT;
        if (AD_HEIGHT > 0) isViewportFullScreen = false;
        
        //Viewport
        Renderer.setViewport(
                VIEWPORT_X, 
                VIEWPORT_Y , 
                VIEWPORT_WIDTH, 
                VIEWPORT_HEIGHT);
        
    }
    
    public static void putScissorToViewport() {
        Renderer.Scissor.enable();
        Renderer.setViewport(
                VIEWPORT_X, 
                VIEWPORT_Y , 
                VIEWPORT_WIDTH, 
                VIEWPORT_HEIGHT);
        Renderer.Scissor.setTo(
                VIEWPORT_X, 
                VIEWPORT_Y, 
                VIEWPORT_WIDTH, 
                VIEWPORT_HEIGHT);
    }
    
    public static void putScissorFullScreen() {
        Renderer.setViewport(
                0, 
                0 , 
                SCREEN_HW_PIXEL_WIDTH, 
                SCREEN_HW_PIXEL_HEIGHT + AD_HEIGHT);
        Renderer.Scissor.setTo(
                0, 
                0, 
                SCREEN_HW_PIXEL_WIDTH, 
                SCREEN_HW_PIXEL_HEIGHT + AD_HEIGHT);
    }


}
