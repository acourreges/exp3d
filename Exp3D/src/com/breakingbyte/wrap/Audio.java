package com.breakingbyte.wrap;

import java.util.HashMap;

import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.audio.SoundDescriptor;
import com.breakingbyte.game.audio.SoundEffect;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Audio {
    
    private static final String TAG = "Audio";
    
    private SoundPool soundPool;
    private HashMap<SoundId, Integer> idToPoolId; 
    
    private HashMap<SoundId, SoundDescriptor> musics;
    
    public static float getDefaultVolume(SoundId id) {
        switch (id) {
            case BEEP_1:            return 1f;
            case EXPLOSION_1:       return 0.7f;
            case EXPLOSION_2:       return 0.6f;
            case CHIME_1:           return 0.6f;
            case RESPAWN:           return 0.6f;
            case ALARM:             return 0.5f;
            case SPECIAL_WEAPON:    return 0.5f;
            case BULLET_IMPACT:     return 0.5f;
            case BONUS_TIME:        return 1f;
            case BONUS_SUPER_SHIELD:return 0.6f;
            case BONUS_HELLFIRE:    return 0.6f;
            default: 
                Log.w(TAG, "Default volume not defined for " + id);
                return 1f;
        }
    }
    
    public void initAudioSystem(int maxStreams) {
        soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        idToPoolId = new HashMap<SoundId, Integer>();
        musics = new HashMap<SoundId, SoundDescriptor>();
    }
    
    public boolean prepareToLoad(SoundId soundId, String filePath) {
        try {
            AssetManager am = BaseActivity.instance.getAssets();
            idToPoolId.put(soundId, soundPool.load(am.openFd(filePath), 1));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Cannot initialize sound effect!", e);
            return false;
        }
    }
    
    public SoundEffect generateSoundEffect(SoundId soundId, String filePath) {
        try {
            SoundEffect sound = new SoundEffectImpl(soundPool, idToPoolId.get(soundId));
            return sound;
        } catch (Exception e) {
            Log.e(TAG, "Cannot create sound effect!", e);
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
    
    MediaPlayer musicPlayer;
    
    public void loadMusicToPlay(SoundId soundId) {
        
        String musicFile = musics.get(soundId).path;
        
        if (musicPlayer != null) {
            if (musicPlayer.isPlaying()) musicPlayer.stop();
            musicPlayer.release();
        }
        musicPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = BaseActivity.instance.getAssets().openFd(musicFile);
            musicPlayer.reset();
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            musicPlayer.prepare();
            afd.close();
        } catch (Exception e) {
            Log.w(TAG, "Could not create music for id " + musicFile, e);
        }
    }
    
    public void playMusic(float volume) {
        if (musicPlayer == null) return;
        musicPlayer.setLooping(true);
        musicPlayer.setVolume(volume, volume);
        musicPlayer.start();
    }
    
    public void pauseMusic() {
        if (musicPlayer == null) return;
        musicPlayer.pause();
    }
    
    public void resumeMusic() {
        if (musicPlayer == null) return;
        musicPlayer.start();
    }
    
    public void stopMusic() {
        if (musicPlayer == null) return;
        musicPlayer.stop();
    }
    
    //TODO cleanupAll

}
