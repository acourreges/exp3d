package com.breakingbyte.wrap;


import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.Sound.LoadState;
import com.breakingbyte.game.audio.SoundEffect;

public class SoundEffectImpl extends SoundEffect {

    public static final String TAG = "SoundEffectImpl";

    public Sound sound;
    
    public SoundEffectImpl(Sound sound) {
        this.sound = sound;
    }
    
    @Override
    public void play() {
        sound.play();
    }

    @Override
    public void stop() {
        sound.stop();
    }

    @Override
    public void pause() {
        Log.e(TAG, "Not implemented!");
        
    }

    @Override
    public void resume() {
        Log.e(TAG, "Not implemented!");
        
    }

    @Override
    public void setVolume(float value) {
        sound.setVolume((int)(value * 100));
    }
    
    public void setLooping(boolean value) {
        sound.setLooping(value);
    }

    @Override
    public boolean isLoaded() {
        // This API is quite broken
        LoadState state = sound.getLoadState();
        switch (state) {
            case LOAD_STATE_NOT_SUPPORTED:
                isLoaded = true;
                Log.e(TAG, "Sound not supported! " + sound.getUrl());
                break;
            case LOAD_STATE_SUPPORTED_AND_READY:
            case LOAD_STATE_SUPPORTED_MAYBE_READY:
                Log.d(TAG, "Sound ready "  + sound.getUrl());
                isLoaded = true;
                break;
            default: break;
        }
        return isLoaded;
    }
}
