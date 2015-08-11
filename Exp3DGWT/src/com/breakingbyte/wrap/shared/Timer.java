package com.breakingbyte.wrap.shared;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.wrap.Platform;
import com.google.gwt.core.client.Duration;

public class Timer {
      

    //When the last frame started (nano seconds)
    private static double previousDrawTime;
    
    //How many seconds between the two frames
    public static float delta = 0;
    
    //Pause management
    private static boolean paused;
    
        //How many frames to average
        private static final int NB_FRAMES_FOR_FPS_AVG = 30;
        
        //Number of frames elapsed
        private static int frameElapsed = 0;
        
        //Time of the first frame (ms)
        private static double startFrameTime = 0;

        
    public static void init() {
        previousDrawTime = Platform.getNanoTime();
        paused = false;
        delta = 0;
    }
    
    public static void pause() {
        paused = true;
    }
    
    public static void resume() {
        paused = false;
    }
    
    public static boolean isPaused() {
        return paused;
    }
        
    public static void update() {
        double nowNano = Duration.currentTimeMillis();
        // Log.d("Timer", "Current time " + nowNano);
        delta = (float)(nowNano - previousDrawTime);        
        delta = delta / 1e3f; //in seconds
        previousDrawTime = nowNano;
        
        if (paused) delta = 0;
        
        //if (Platform.forceFixedTimestep || Debug.fixedTimestep) delta = 0.015f;
        
        if (EngineState.Settings.displayFPSOn) calculateFPS();
    }
    
    @SuppressWarnings("unused")
    private static StringBuilder stringBuilder = new StringBuilder();
    
    private static void calculateFPS(){
        
        if (frameElapsed == 0) startFrameTime = Duration.currentTimeMillis();
        
        frameElapsed++;
        
        if (frameElapsed >= NB_FRAMES_FOR_FPS_AVG) {
            //Display FPS and reset counter
            double totalDuration = (Duration.currentTimeMillis()  - startFrameTime);
            float msPerFrame = (float)totalDuration / frameElapsed;
            Engine.latestFPSValue = (int)(1000f / msPerFrame);
            /*
            stringBuilder.setLength(0);
            stringBuilder.append(1000f / msPerFrame);
            stringBuilder.append(" FPS (");
            stringBuilder.append(msPerFrame);
            stringBuilder.append(" ms)");
            Log.d("FPS", stringBuilder.toString());
            */
            frameElapsed = 0;
        }
        
        
    }

}
