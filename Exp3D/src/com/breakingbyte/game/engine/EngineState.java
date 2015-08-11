package com.breakingbyte.game.engine;

import com.breakingbyte.game.content.ShopContent;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.state.ShopState;
import com.breakingbyte.game.util.PreferencesSerializer;
import com.breakingbyte.wrap.Persistence;
import com.breakingbyte.wrap.Platform;

public class EngineState {
    
    public static final class Settings {
        
        public static String versionName = "Version 1.6"; // TODO read from Manifest on Android
        
        public static boolean musicOn = false;
        
        public static boolean soundOn = false;
        
        public static boolean vibrateOn = true;
        
        public static boolean displayFPSOn = false;
    }
    
    public static final class Player {
        
        public static int totalOrbs;
        
    }
    
    public static boolean doTutorial = true;
    
    public static boolean isFullVersion = false;
    
    private static boolean completedBoot = false;
    
    public static final class GeneralStats {
        
        public static float duration;
        public static int kills;
        public static int deaths;
        public static int orbsSpent;
        public static int specialFired;
        public static int hellfire;
        public static int timeWarp;
        public static int superShield;
        
        public static int reserved1;
        public static int reserved2;
        public static int reserved3;
        
    }
    
    public static void loadFromStorage() {
        
        //Settings
        Settings.musicOn = true;
        Settings.soundOn = true;
        Settings.vibrateOn = true;
        Settings.displayFPSOn = false;
        
        //Player
        Player.totalOrbs = 0;
        
        doTutorial = true;
        
        isFullVersion = true;
        
        //Shop
        ShopContent.primaryDamageArticle.setLevel(0);
        ShopContent.primaryRateArticle.setLevel(0);
        ShopContent.primaryCannonArticle.setLevel(1);
        
        ShopContent.specialReloadArticle.setLevel(0);
        ShopContent.specialEMWaveArticle.setLevel(0);
        
        ShopContent.miscShieldArticle.setLevel(0);
        ShopContent.miscLifeArticle.setLevel(3);
        
        ShopContent.applyAllToEngineState();
        
        //General stats
        GeneralStats.duration = 0f;
        GeneralStats.kills = 0;
        GeneralStats.deaths = 0;
        GeneralStats.orbsSpent = 0;
        GeneralStats.specialFired = 0;
        GeneralStats.hellfire = 0;
        GeneralStats.timeWarp = 0;
        GeneralStats.superShield = 0;
        
        GeneralStats.reserved1 = 0;
        GeneralStats.reserved2 = 0;
        GeneralStats.reserved3 = 0;
        
        boolean allowLevelBrowsing = Platform.name.equals("WebGL"); //Only for WebGL demo
        
        //Levels
        LevelContent.level1.bought = true;
        LevelContent.level1.unlocked = true;
        LevelContent.level1.completedOnce = false;
        
        LevelContent.level2.bought = false;
        LevelContent.level2.unlocked = allowLevelBrowsing; //can be set to true for web
        LevelContent.level2.completedOnce = false;
        
        LevelContent.level3.bought = false;
        LevelContent.level3.unlocked = allowLevelBrowsing;
        LevelContent.level3.completedOnce = false;
        
        LevelContent.level4.bought = false;
        LevelContent.level4.unlocked = allowLevelBrowsing;
        LevelContent.level4.completedOnce = false;
        
        //Load actual values from persistent storage
        PreferencesSerializer.tryToLoadSaveAndApply();
        
        ShopContent.applyAllToEngineState();
        
        completedBoot = true;
    }
    
    public static final void onFullVersionUpdated() {
        ShopState.updateFullVersionButton(isFullVersion);
        if (isFullVersion && ArenaState.instance.unlockLevelDialog.isOpen()) {
            ArenaState.instance.hideUnlockLevelDialogIfNecessary();
        }
    }
    
    public static final void onTotalOrbsUpdated() {
        ShopState.updateOrbCountDisplay();
        ArenaState.instance.unlockLevelDialog.updateCurrentOrbsLabel();
        
    }
   
    
    public static void saveToStorage() {
        if (!completedBoot) return;
        String result = PreferencesSerializer.serializeState();
        Persistence.writePreferences(result);
    }

}
