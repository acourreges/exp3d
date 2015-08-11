package com.breakingbyte.game.audio;

import com.breakingbyte.game.audio.AudioManager.SoundId;

/**
 * Describes a sound effect.
 */

public class SoundDescriptor {
    
    public SoundId id; 
    
    public String path = "";
    
    public int duration; //in ms
    
    public int poolCapacity;
    
    public float defaultVolume;

}
