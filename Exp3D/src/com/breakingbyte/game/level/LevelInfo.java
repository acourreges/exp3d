package com.breakingbyte.game.level;

import com.breakingbyte.game.level.Level.LevelID;

public class LevelInfo {
    
    public LevelID levelId;
    
    public String name;
    
    public float progressOrbs; // between 0 and 1
    
    public int orbsPlayerRecord = 0;
    
    public int costToUnlock = 0;
    
    public float time = 0;
    
    public int damageTaken = 0;
    
    public boolean perfectBonus = false;
    public boolean noSpecialBonus = false;
    
    public int getProgressPercentage() {
        return (int) (progressOrbs * 100f);
    }
    
    public boolean unlocked; //Player can play the level
    
    public boolean bought; //Level was bought and is so fully playable
    
    public boolean completedOnce;
}
