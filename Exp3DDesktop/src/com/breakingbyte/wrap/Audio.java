package com.breakingbyte.wrap;

import java.io.FileInputStream;
import java.util.HashMap;

import org.newdawn.easyogg.OggClip;

import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.audio.SoundDescriptor;
import com.breakingbyte.game.audio.SoundEffect;

public class Audio {
    
    private static final String TAG = "Audio";
    
    private HashMap<SoundId, SoundDescriptor> musics;

    public static float getDefaultVolume(SoundId id) {
        switch (id) {
            case EXPLOSION_1:   return 1f;
            case EXPLOSION_2:   return 1f;
            case CHIME_1:   return 1f;
            default: 
                // Log.w(TAG, "Default volume not defined for " + id);
                return 1f;
        }
    }
    
    public void initAudioSystem(int maxStreams) {
        musics = new HashMap<SoundId, SoundDescriptor>();
    }
    
    public boolean prepareToLoad(SoundId soundId, String filePath) {
        //nothing
        return true;
    }
    
    public SoundEffect generateSoundEffect(SoundId soundId, String filePath) {
        try {
            FileInputStream input = new FileInputStream(Resource.getFullFilePath(filePath));
            return new SoundEffectOGG(input);
        } catch (Exception e) {
            Log.e(TAG, "Cannot load sound effect!", e);
            return null;
        }
    }
    
    //Music
    public void registerMusic(SoundDescriptor music) {
        musics.put(music.id, music);
    }
    
    public boolean isMusicLoaded(SoundId id) {
        return true;
    }
    
    OggClip musicPlayer;
    
    public void loadMusicToPlay(SoundId soundId) {
        
        String musicFile = musics.get(soundId).path;
        
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        
        try {
        musicPlayer = new OggClip(new FileInputStream(Resource.getFullFilePath(musicFile)));
        } catch (Exception e) {
            Log.e(TAG, "Cannot load music! " + musicFile, e);
            musicPlayer = null;
        }
    }
    
    public void playMusic(float volume) {
        if (musicPlayer == null) return;
        musicPlayer.setGain(volume);
        new Thread() {
            @Override
            public void run() {
                musicPlayer.loop();
            }
        }.start();
    }
    
    public void pauseMusic() {
        if (musicPlayer == null) return;
        new Thread() {
            @Override
            public void run() {
                musicPlayer.pause();
            }
        }.start();
    }
    
    public void resumeMusic() {
        if (musicPlayer == null) return;
        new Thread() {
            @Override
            public void run() {
                musicPlayer.resume();
            }
        }.start();
    }
    
    public void stopMusic() {
        if (musicPlayer == null) return;
        new Thread() {
            @Override
            public void run() {
                musicPlayer.stop();
            }
        }.start();

    }
}
