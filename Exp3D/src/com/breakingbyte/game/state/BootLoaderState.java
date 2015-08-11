package com.breakingbyte.game.state;

import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.resource.Resource.ResourceLevel;
import com.breakingbyte.wrap.shared.Renderer;

public class BootLoaderState extends State {
    
    public static BootLoaderState instance = new BootLoaderState();

    public State getInstance() { return instance; }

    public static int numberOfFrames = 0;
    
    public static void reset() {
        numberOfFrames = 0;
    }

    @Override
    public void updateImpl() {
        // Skip a few frames not to overload the OS
        // => smooth boot on Android
        while (numberOfFrames < 5) return;
        
        boolean loadingCompleted = TextureManager.loadTextures(ResourceLevel.BOOTLOADER);
        
        if (loadingCompleted && !transitionInProgress) {
            ShaderManager.init();
            //switchToStateInstant(LoadingState.instance, 0f);
            //LoadingState.instance.fadeInWithColor(0f, 0f, 0f, 0.3f);
            switchToStateWithColor(LoadingState.instance, 0, 0, 0, 0f, 0f, 0.3f);
        }

    }

    @Override
    public void renderImpl() {
    }
    
    @Override
    public void renderAlways() {
        numberOfFrames++;
        Renderer.setClearColor(0f, 0f, 0f, 1f);
        Renderer.clear(true, true);
    }

    @Override
    public void onEnterImpl(boolean isResume) {
    }

    @Override
    public void onLeaveCompleted() {

    }

}
