package com.breakingbyte.wrap;

import android.media.SoundPool;

import com.breakingbyte.game.audio.SoundEffect;

public class SoundEffectImpl extends SoundEffect {

    private SoundPool pool;
    private int poolId;
    
    private int streamId;
    
    public SoundEffectImpl(SoundPool pool, int poolId) {
        this.pool = pool;
        this.poolId = poolId;
        setLoaded(true);
    }
    
    @Override
    public void play() {
        streamId = pool.play(poolId, volume, volume, 1, 0, 1);
        
    }

    @Override
    public void stop() {
        pool.stop(streamId);
        
    }

    @Override
    public void pause() {
        pool.pause(streamId);
        
    }

    @Override
    public void resume() {
        pool.resume(streamId);
        
    }

    @Override
    public void setVolume(float value) {
        volume = value;
    }

}
