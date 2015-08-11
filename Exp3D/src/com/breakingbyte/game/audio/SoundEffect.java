package com.breakingbyte.game.audio;

import com.breakingbyte.wrap.Platform;

/**
 * Platform-independent representation of a sound effect.
 */

public abstract class SoundEffect {

    public int id;
    
    public long duration; //duration of the sound in ms
    
    public double lastPlayed;
    
    public float volume;
    
    protected boolean isLoaded = false;
    
    public void touchLastPlayed() {
        lastPlayed = Platform.getMillisecondTime();
    }
    
    public boolean isIddle() {
        return (Platform.getMillisecondTime() - lastPlayed) > duration;
    }
    
    public void playSound() {
        touchLastPlayed();
        play();
    }
    
    public boolean isLoaded() {
        return isLoaded;
    }
    
    public void setLoaded(boolean value) {
        isLoaded = value;
    }
    
    public abstract void setVolume(float value);
    
    public abstract void play();
    
    public abstract void stop();
    
    public abstract void pause();
    
    public abstract void resume();
}
