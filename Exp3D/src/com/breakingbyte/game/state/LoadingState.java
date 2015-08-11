package com.breakingbyte.game.state;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.content.PowerUpContent;
import com.breakingbyte.game.content.ShopContent;
import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.LoadingBar;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.wrap.Platform;
import com.breakingbyte.wrap.shared.Renderer;

public class LoadingState extends State {
    
    public static LoadingState instance = new LoadingState();

    public State getInstance() { return instance; }

    private static boolean contentLoaded;
    
    private static int loadingSubState = 0;
    private static int alreadyLoadedStates = 0; //Keeps track of the highest loading state reached before an onPause() occurs
    
    private DynamicText loadingTxt;
    private LoadingBar loadingBar;
    
    private static int framesToWait = 0;
    
    public static boolean preventResume = false;
    
    public static void reset() {
        if (loadingSubState > alreadyLoadedStates) alreadyLoadedStates = loadingSubState;
        loadingSubState = 0;
        framesToWait = 0;
        contentLoaded = false;
        preventResume = false;
    }
    
    @Override
    public void updateImpl() {
        
        if (transitionInProgress) return;
        
        if (framesToWait > 0) return;
        
        if (!contentLoaded) {
            
            int levelEnteringMainMenu = Platform.engineStartLevel;
            
            //Not so pretty, because on Android loading textures is a long blocking call whereas on WebGL it's an async call
            //The following sequence works fine whether resource loading is sync or async
            
            if (loadingSubState == 0) {
                if (alreadyLoadedStates <= 0){
                    ShopContent.init();
                    LevelContent.init();
                    PowerUpContent.init();
                }
                loadingSubState++;
                return;
            }
            
            else if (loadingSubState == 1) {
                if (alreadyLoadedStates <= 1) AudioManager.initialize();
                loadingSubState++;
                return;
            }
            
            else if (loadingSubState == 2) {
                TextureManager.loadTextures(levelEnteringMainMenu);
                loadingSubState++;
                return;
            }
            
            else if (loadingSubState == 3) {
                TextureManager.loadTextures(levelEnteringMainMenu);
                ModelManager.loadModels(levelEnteringMainMenu);
                loadingSubState++;
                return;
            }
            
            else if (loadingSubState == 4) {
                boolean loadingCompleted = true;
                loadingCompleted &= ModelManager.loadModels(levelEnteringMainMenu);
                loadingCompleted &= TextureManager.loadTextures(levelEnteringMainMenu);
                loadingCompleted &= AudioManager.load();
                if (loadingCompleted) {
                    loadingSubState++;
                    Platform.garbageCollect();
                }
                return;
            }
            
            else if (loadingSubState == 5) {
                if (alreadyLoadedStates <= 5) {
                    //Some strings as texture
                    TextureManager.loadDynamicTextTextures();          
                    
                    //Init states
                    EngineState.loadFromStorage();
                    TitleState.init();
                    SettingsState.init();
                    ShopState.init();
                    CreditsState.init();
                    ArenaState.init();
                    ForwarderState.init();
                }
                loadingSubState++;
                contentLoaded = true; 
            }
            
        } else {
            //Loading done
            
            State nextState = Engine.backupState;
            if (nextState != null && !preventResume) State.followsResume = true;
            
            if (nextState == null) nextState = TitleState.instance;
            
            if (Debug.jumpToGame) {
                switchToStateWithColor(ArenaState.instance, 0, 0, 0, 0, 0);
            } else {
                switchToStateWithColor(nextState, 0, 0, 0, 0.7f, 0f, 2f);
            }
            
        }
    }
    
    @Override
    public void renderAlways() {
        
        framesToWait--; if (framesToWait < 0) framesToWait = 0;
        
        Renderer.DepthTest.disable();
        Renderer.Blending.enable();
        Renderer.Blending.resetMode();
        Renderer.clear(false, true);
        
        Renderer.loadOrtho();
        
        
        loadingTxt.update();
        float progress = TextureManager.getProgress(Platform.engineStartLevel);
        progress += ModelManager.getProgress(Platform.engineStartLevel);
        progress += AudioManager.getProgress();
        progress /= 3f;
        
        float posY = Screen.ARENA_HEIGHT * 0.5f + 17f;
        loadingTxt.setPosition(50, posY);
        loadingTxt.setAlpha(0.85f);
        loadingTxt.setColor(1f, 1f, 1f);
        loadingTxt.textSize = 12;
        loadingTxt.render();
        
        posY -= 21f;
        loadingBar.setLoading(progress);
        loadingBar.setup(70, 10, 4);
        loadingBar.setPosition(15, posY);
        loadingBar.render();
    }
    
    @Override
    public void renderImpl() {
        
    }

    @Override
    public void onEnterImpl(boolean isResume) {
        framesToSkip = 0;
        framesToWait = (int) (0.3 / 0.016f + 1);
        contentLoaded = false;
        
        Renderer.initialize();
        
        if (loadingTxt == null) {
            String text = "Loading";
            loadingTxt = new DynamicText(text.length());
            loadingTxt.spacer *= 0.5f;
            loadingTxt.setAlignment(Align.CENTER);
            loadingTxt.printString(text);
            loadingTxt.updateBuffers();
            loadingBar = new LoadingBar();
        }
    }

    @Override
    public void onLeaveCompleted() {

    }



}
