package com.breakingbyte.game.level;

import java.util.ArrayList;

import com.breakingbyte.game.level.Level.LevelID;

public class LevelContent {

    public static LevelInfo 
        level1,
        level2,
        level3,
        level4
        ;
    
    public static ArrayList<LevelInfo> allLevels;
    
    public static void init() {
        
        allLevels = new ArrayList<LevelInfo>();
        
        level1 = createLevel(LevelID.Level1, "Athryl City", 0);
        level2 = createLevel(LevelID.Level2, "Skirl Path", 40);
        level3 = createLevel(LevelID.Level3, "Hub Border", 45 );
        level4 = createLevel(LevelID.Level4, "Myor Mines", 40 );
        //level 4 brings also 30 orbs
        
    }
    
    public static LevelInfo getLevelFromID(LevelID id) {
        for (int i = 0; i < allLevels.size(); i++) {
            LevelInfo lvl = allLevels.get(i);
            if (lvl.levelId == id) return lvl;
        }
        return null;
    }
    
    public static LevelInfo createLevel(LevelID id, String name, int costToUnlock) {
        LevelInfo result = new LevelInfo();
        allLevels.add(result);
        result.levelId = id;
        result.name = name;
        result.bought = false;
        result.unlocked = false;
        result.completedOnce = false;
        result.costToUnlock = costToUnlock;
        return result;
    }
    
}
