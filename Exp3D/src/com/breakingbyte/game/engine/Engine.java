package com.breakingbyte.game.engine;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.entity.Player;
import com.breakingbyte.game.entity.group.EnemyBulletGroup;
import com.breakingbyte.game.entity.group.ExplosionGroup;
import com.breakingbyte.game.level.Level;
import com.breakingbyte.game.resource.Resource;
import com.breakingbyte.game.state.BootLoaderState;
import com.breakingbyte.game.state.LoadingState;
import com.breakingbyte.game.state.State;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.util.IAPNotifier;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class Engine {

    public static Engine instance = null; 

    public static State state;
    public static State backupState = null;
    public static boolean isTransitionToBackupState = false;
    
    public static boolean wasOnPause = false; // Only used for Android

    // Entity groups organized in layers
    public static Layer layer_player = new Layer();
    public static Layer layer_playerBullets = new Layer();
    public static Layer layer_enemies = new Layer();
    public static Layer layer_enemyBullets = new Layer();
    public static Layer layer_bonus = new Layer();
    public static Layer layer_explosions = new Layer();
    
    public static ExplosionGroup explosions;
    public static EnemyBulletGroup enemyBullets;
    
    // Level
    public static Level currentLevel;
    
    public static DynamicText fpsDisplay;
    public static int currentFPSValue = 0;
    public static int latestFPSValue = 0;

    public static void initEngine() {
        instance = new Engine();
        explosions = ExplosionGroup.newInstance();
        layer_explosions.addEntity(explosions);
        enemyBullets = EnemyBulletGroup.newInstance();
        layer_enemyBullets.addEntity(enemyBullets);
        Engine.player = Player.newInstance();
        Player.init();
        player.registerInLayer();
        QuadVBO.initialize();
        BootLoaderState.instance.enterState();
    }
    
    private static boolean  doneOnce = false;
    public static Player  player = null;
    
    public static void drawFrame() {
        
        Timer.update();
        
        if (!doneOnce) {
            doneOnce = true;
            Timer.update();
            initEngine();
            return;
        }
        
        if (wasOnPause) {
            // We'll go here only after each onResume() happens to the activity on Android
            handleResumeAfterPause();
            wasOnPause = false;
        }
        
        if (Engine.state != BootLoaderState.instance && Engine.state != LoadingState.instance) {
            // Checks IAP notifications
            IAPNotifier.update();
        }
        
        updateGame();
        
        if (!Screen.isViewportFullScreen) {
            // If we don't do this, on some devices there will be garbage outside of the viewport
            Screen.putScissorFullScreen();
            Renderer.clearColorBuffer();
            Screen.putScissorToViewport();
        }
        
        Engine.state.render();
        
        if (EngineState.Settings.displayFPSOn && Engine.state != BootLoaderState.instance && Engine.state != LoadingState.instance) {
            updateFPSIfNecessary();
            Renderer.loadOrtho();
            
            Renderer.resetToDefaultShading();
            
            fpsDisplay.textSize = 5f;
            fpsDisplay.setPosition(0f, Screen.ARENA_HEIGHT + 1f);
            fpsDisplay.setColor(0f, 1f, 1f);
            
            fpsDisplay.render();
        }
    }
    
    public static void handleResumeAfterPause() {
        
        if (Resource.gfxContextTrashed) 
        {
            // The OGL context was trashed so we need to re-load all the models, textures and VBOs
            Resource.gfxContextTrashed = false;
            QuadVBO.initialize();
            
            BootLoaderState.reset(); LoadingState.reset();
            
            if (state != BootLoaderState.instance && state != LoadingState.instance) {
                backupState = state;
                // Check if a transition was in progress
                if (backupState != null)
                {
                    if (backupState.getNextState() != null) { // Instant close of old state to enter the new state later
                        backupState.onLeaveCompleted();
                        backupState = backupState.getNextState();
                        LoadingState.preventResume = true;
                    }
                }
            }
            state = null;
            State.transitionInProgress = false;
            BootLoaderState.instance.enterState();
        } else {
            // Easy case, nothing was lost
            if (state != null) state.onPostResume();
            AudioManager.wasPaused = false; // In case some states don't handle the music correctly
        }        
    }
    
    public static void initFPSDisplay() {
        fpsDisplay = new DynamicText(40);
        fpsDisplay.textSize = 10f;
        fpsDisplay.setPosition(Screen.ARENA_WIDTH * 0.5f - 10f, Screen.ARENA_HEIGHT * 0.5f);
    }
    
    public static void updateFPSIfNecessary() {
        if (fpsDisplay == null) initFPSDisplay();
        if (latestFPSValue == currentFPSValue) return;
        fpsDisplay.reset().printString("FPS: ").printInteger(latestFPSValue).updateBuffers();
        //newLine().printString("Model: ").printString(Platform.getDeviceModel()).updateBuffers();
        currentFPSValue = latestFPSValue;
    }
    
    public static void updateGame(){
        
        //The first version simply called update()
        //But it was messing with HolyBlast, when delta is too big
        //(low frame-rate => not enough control points)
       
        /*
        final boolean SINGLE_UPDATE_PER_FRAME = true;
        if (SINGLE_UPDATE_PER_FRAME) {
            update();
            return;
        }
        */
        
        //Instead, when the frame delta is too large we do a succession of
        //update() with a constant small delta
        
        final float GAME_LOGIC_DELTA = 0.02f; //period in seconds
        
        float timeToSimulate = Timer.delta;
        
        if (Engine.state == BootLoaderState.instance || Engine.state == LoadingState.instance) {
            //Special case hack
                if (timeToSimulate > GAME_LOGIC_DELTA) {
                Timer.delta = 0.016f;
                update();
                return;
            }
        }
        
        while (timeToSimulate > GAME_LOGIC_DELTA) {
            Timer.delta = GAME_LOGIC_DELTA;
            update();
            timeToSimulate -= GAME_LOGIC_DELTA;
        }
        
        //Note: If we want a fixed timestep all the way long,
        // we should finish with an interpolation for the rendering
        // + accumulate the remaining time value to give it to the next frame
        Timer.delta = timeToSimulate;
        update();
    }
    
    private static void update() {
        Controller.readKeys();
        Engine.state.update();
        Controller.resetFlags();
    }

    
    public static int lifeTest = 9;
    public static void testPhase() {
        Engine.player.putSuperShield(5);
    }
    
    public static void switchBoundingBoxDrawing() {
        Debug.drawBoundingBoxes = !Debug.drawBoundingBoxes;
    }

    public static void switchLightmap() {
        Debug.useLightmap = !Debug.useLightmap;
    }


}
