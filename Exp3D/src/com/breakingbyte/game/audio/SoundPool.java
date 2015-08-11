package com.breakingbyte.game.audio;

import java.util.ArrayList;
import java.util.HashMap;


import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.wrap.Audio;
import com.breakingbyte.wrap.Log;

public class SoundPool {

    private static final String TAG = "SoundPool";
    
    private HashMap<SoundId, ArrayList<SoundEffect> > pool;
    
    private HashMap<SoundId, Integer> poolIndex;
    
    private Audio audio;
    
    public SoundPool(Audio audio, int maxStreams) {
        pool = new HashMap<SoundId, ArrayList<SoundEffect>>();
        poolIndex = new HashMap<SoundId, Integer>();
        this.audio = audio;
        this.audio.initAudioSystem(maxStreams);
    }
    
    public float getProgress() {
        int total = 0;
        int done = 0;
        for (int i = 0; i < AudioManager.allSounds.size(); i++) {
            ArrayList<SoundEffect> effects = pool.get(AudioManager.allSounds.get(i).id);
            for (int j = 0; j < effects.size(); j++) {
                total++;
                if (effects.get(j).isLoaded()) done++;
            }
        }
        
        if (total == 0) return 0;
        
        return (float)done / (float)total;
    }
    
    public boolean registerSound(SoundDescriptor soundDesc) {
        
        if (!audio.prepareToLoad(soundDesc.id, soundDesc.path)) {
            Log.e(TAG, "Error initializing new sound " + soundDesc.id);
            return false;
        }
        
        ArrayList<SoundEffect> sounds = new ArrayList<SoundEffect>(soundDesc.poolCapacity);
        
        for (int i = 0; i < soundDesc.poolCapacity; i++) {
            SoundEffect sound = audio.generateSoundEffect(soundDesc.id, soundDesc.path);
            if (sound == null) return false;
            sounds.add(sound);
            sound.touchLastPlayed();
            sound.duration = soundDesc.duration;
        }
        
        pool.put(soundDesc.id, sounds);
        poolIndex.put(soundDesc.id, 0);
        return true;
        
    }
    
    public boolean playSound(SoundId soundId) {
        return playSound(soundId, Audio.getDefaultVolume(soundId));
    }
    
    public boolean playSound(SoundId soundId, float volume) {
        int soundIndex = poolIndex.get(soundId);
        SoundEffect sound = pool.get(soundId).get(soundIndex);
        if (!sound.isIddle()) {
            Log.w(TAG, "Can't play " + soundId + " for now, all streams busy...");
            return false;
        }
        sound.setVolume(volume);
        sound.playSound();
        soundIndex++;
        if (soundIndex >= pool.get(soundId).size()) soundIndex = 0;
        poolIndex.put(soundId, soundIndex);
        return true;
    }
    
}
