package com.breakingbyte.game.level;

import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.level.Level.LevelID;
import com.breakingbyte.wrap.shared.Timer;

public class LevelStats {
    
    public LevelInfo level;
    
    public float timeElapsed;
    
    public int kills;
    
    public int orbsCollected;
    public int orbsSpawned;
    
    public int damageTaken;
    
    public int currentRecord;
    
    public boolean specialNotFired;
    
    public static final int BONUS_ORB_COUNT = 5;
    
    public LevelStats() {
        //reset();
    }
    
    public void reset(LevelInfo levelInfo) {
        this.level = levelInfo;
        timeElapsed
        = kills
        = orbsCollected
        = orbsSpawned
        = damageTaken
        = currentRecord
        = 0;
        specialNotFired = true;
        currentRecord = level.orbsPlayerRecord;
    }
    
    public void update() {
        timeElapsed += Timer.delta;
    }

    public final void addEnemyKilled() {
        kills++;
    }
    
    public final void addOrbCollected() {
        orbsCollected++;
    }
    
    public final void addOrbSpawned() {
        orbsSpawned++;
    }
    
    public final void addDamageTaken(int value) {
        damageTaken += value;
    }
    
    public final void setCurrentRecord(int value) {
        currentRecord = value;
    }
    
    public final void setSpecialFired() {
        specialNotFired = false;
    }
    
    public boolean isNewRecord() {
        return getNewOrbsEarned() > 0;
    }
    
    public int getOrbsCollectedWithBonus() {
        int result = orbsCollected;
        if (gotNoDamageBonus()) result += BONUS_ORB_COUNT;
        if (gotNoSpecialFiredBonus()) result += BONUS_ORB_COUNT;
        return result;
    }
    
    public int getNewOrbsEarned() {
        int result = getOrbsCollectedWithBonus() - currentRecord;
        if (result < 0) result = 0;
        return result;
    }
    
    public boolean gotNoDamageBonus() {
        return damageTaken == 0;
    }
    
    public boolean gotNoSpecialFiredBonus() {
        return specialNotFired;
    }
    
    public int getMaximumOrbsGainable() {
        return orbsSpawned + 2 * BONUS_ORB_COUNT;
    }
    
    public void saveData() {
        
        boolean wasNeverCompleted = !level.completedOnce;
        if (wasNeverCompleted) {
            LevelID nextLvlId = level.levelId.getNextID();
            
            if (nextLvlId != null) {
                LevelInfo nextLvlInfo = LevelContent.getLevelFromID(nextLvlId);
                nextLvlInfo.unlocked = true;
            }
        }
        
        if (wasNeverCompleted || isNewRecord()) {
            level.progressOrbs =  (float)getOrbsCollectedWithBonus() / (float)getMaximumOrbsGainable();
            level.orbsPlayerRecord = getOrbsCollectedWithBonus();
            level.time = timeElapsed;
            level.damageTaken = damageTaken;
            level.perfectBonus = gotNoDamageBonus();
            level.noSpecialBonus = gotNoSpecialFiredBonus();
            level.completedOnce = true;
        }
        
        if (getNewOrbsEarned() > 0) {
            EngineState.Player.totalOrbs += getNewOrbsEarned();
            EngineState.onTotalOrbsUpdated();
        }
    }
    
}
