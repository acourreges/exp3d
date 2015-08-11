package com.breakingbyte.wrap;

import java.io.FileInputStream;
import java.io.IOException;

import org.newdawn.easyogg.OggClip;

import com.breakingbyte.game.audio.SoundEffect;

public class SoundEffectOGG  extends SoundEffect {

    @SuppressWarnings("unused")
    private static final String TAG = "SoundEffectOGG";
    
    private OggClip clip;
    
    public SoundEffectOGG(FileInputStream inputStream) throws IOException {
        clip = new OggClip(inputStream);
        setLoaded(true);
    }
    
    @SuppressWarnings("unused")
    @Override
    public void play() {
        if (false && clip.stopped()) {
            clip.play();
            return;
        }
        
        new Thread() {
            @Override
            public void run() {
                clip.play();
            }
        }.start();
    }
    
    @Override
    public void stop() {
        new Thread() {
            @Override
            public void run() {
                clip.stop();
            }
        }.start();
    }

    @Override
    public void pause() {
        clip.pause();
    }

    @Override
    public void resume() {
       clip.resume();
    }
    

    @Override
    public void setVolume(float value) {
        volume = value;
        clip.setGain(volume);
    }
    
}
