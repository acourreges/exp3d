package com.breakingbyte.wrap;

import java.util.HashMap;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.allen_sauer.gwt.voices.client.SoundType;
import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.audio.SoundDescriptor;
import com.breakingbyte.game.audio.SoundEffect;



public class Audio {
    
    private static final String TAG = "Audio";
    
    private static SoundController soundController;
    
    private HashMap<SoundId, SoundEffectImpl> musics;
    
    public static float getDefaultVolume(SoundId id) {
        switch (id) {
            case EXPLOSION_1:       return 0.6f;
            case EXPLOSION_2:       return 0.6f;
            case CHIME_1:           return 0.6f;
            case RESPAWN:           return 0.6f;
            case ALARM:             return 0.8f;
            case SPECIAL_WEAPON:    return 0.6f; 
            case BONUS_TIME:        return 1f;
            case BONUS_SUPER_SHIELD:return 0.6f;
            case BONUS_HELLFIRE:    return 0.6f;
            default: 
                Log.w(TAG, "Default volume not defined for " + id);
                return 1f;
        }
    }
    
    @SuppressWarnings("deprecation")
    public void initAudioSystem(int maxStreams) {
        soundController = new SoundController();
        soundController.setPreferredSoundTypes(SoundType.HTML5);
        musics = new HashMap<SoundId, SoundEffectImpl>();
    }
    
    public boolean prepareToLoad(SoundId soundId, String filePath) {
        //nothing
        return true;
    }
    
    public SoundEffect generateSoundEffect(SoundId soundId, String filePath) {
        Sound sound = soundController.createSound(Sound.MIME_TYPE_AUDIO_OGG_VORBIS, "assets/"+filePath);
        return new SoundEffectImpl(sound);
    }
    
    //Music
    public void registerMusic(SoundDescriptor musicDesc) {
        SoundEffectImpl result = (SoundEffectImpl)generateSoundEffect(musicDesc.id, musicDesc.path);
        musics.put(musicDesc.id, result);
    }
    
    public boolean isMusicLoaded(SoundId id) {
        return musics.get(id).isLoaded();
    }
    
    SoundEffectImpl musicPlayer;
    
    
    public void loadMusicToPlay(SoundId soundId) {
        
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        
        musicPlayer = musics.get(soundId);
    }
    
    public void playMusic(float volume) {
        if (musicPlayer == null) return;
        musicPlayer.setVolume(volume);
        musicPlayer.setLooping(true);
        musicPlayer.play();
    }
    
    public void pauseMusic() {
        if (musicPlayer == null) return;
        Log.e(TAG, "Not implemented!");
    }
    
    public void resumeMusic() {
        if (musicPlayer == null) return;
        Log.e(TAG, "Not implemented!");
    }
    
    public void stopMusic() {
        if (musicPlayer == null) return;
        musicPlayer.stop();
    }
    
    //TODO cleanupAll

}
