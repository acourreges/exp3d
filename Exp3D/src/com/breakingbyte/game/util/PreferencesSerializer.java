package com.breakingbyte.game.util;

import com.breakingbyte.game.content.ShopContent;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.EngineState.GeneralStats;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.level.LevelInfo;
import com.breakingbyte.wrap.Persistence;
import com.breakingbyte.wrap.Platform;

public class PreferencesSerializer {

    public static final int saveFormat = 1;
    public static StringBuilder strBuilder;
    public static final char separator = '|';
    static {
        strBuilder = new StringBuilder();
    }
    
    private static final void app(int i) {
        strBuilder.append(i);
        strBuilder.append(separator);
    }
    
    private static final void app(float i) {
        strBuilder.append(i);
        strBuilder.append(separator);
    }
    
    private static final void app(boolean b) {
        strBuilder.append(b? '1' : '0');
        strBuilder.append(separator);
    }
    
    private static final void app(String str) {
        strBuilder.append(str);
        strBuilder.append(separator);
    }

    private static final void app(LevelInfo lvl) {
        app(lvl.progressOrbs);
        app(lvl.orbsPlayerRecord);
        app(lvl.time);
        app(lvl.damageTaken);
        app(lvl.perfectBonus);
        app(lvl.noSpecialBonus);
        app(lvl.unlocked);
        app(lvl.bought);
        app(lvl.completedOnce);
    }
    
    public static final String serializeState() {
        
        strBuilder.setLength(0);
        
        //Version
        app(saveFormat);
        
        //Model
        app(Platform.getDeviceModel());
        
        app(EngineState.Settings.musicOn);
        app(EngineState.Settings.soundOn);
        app(EngineState.Settings.vibrateOn);
        app(EngineState.Settings.displayFPSOn);
        
        app(EngineState.Player.totalOrbs);
        
        //Full version
        app(EngineState.isFullVersion);
        
        //Shop content
        app(ShopContent.primaryDamageArticle.currentLevel);
        app(ShopContent.primaryRateArticle.currentLevel);
        app(ShopContent.primaryCannonArticle.currentLevel);
        
        app(ShopContent.specialReloadArticle.currentLevel);
        app(ShopContent.specialEMWaveArticle.currentLevel);
        
        app(ShopContent.miscShieldArticle.currentLevel);
        app(ShopContent.miscLifeArticle.currentLevel);
        
        //General stats
        app(GeneralStats.duration);
        app(GeneralStats.kills);
        app(GeneralStats.deaths);
        app(GeneralStats.orbsSpent);
        app(GeneralStats.specialFired);
        app(GeneralStats.hellfire);
        app(GeneralStats.timeWarp);
        app(GeneralStats.superShield);
        
        app(GeneralStats.reserved1);
        app(GeneralStats.reserved2);
        app(GeneralStats.reserved3);

        
        //Levels
        app(LevelContent.level1);
        app(LevelContent.level2);
        app(LevelContent.level3);
        app(LevelContent.level4);
        
        //Get the hash for the string
        String hash = Security.hashString(strBuilder.toString());
        strBuilder.append(hash);
        
        return strBuilder.toString();
    }
    
    //==========================
    // Deserialization
    //==========================
    
    @SuppressWarnings("unused")
    private static final void readAndApply(String str) {
        tokenizer.reinit(str, "\\|");
        
        int saveFormat = readInt();
        
        String model = readString();
        if (!Platform.getDeviceModel().equals(model)) return;
        
        EngineState.Settings.musicOn = readBoolean();
        EngineState.Settings.soundOn = readBoolean();
        EngineState.Settings.vibrateOn = readBoolean();
        EngineState.Settings.displayFPSOn = readBoolean();
        
        EngineState.Player.totalOrbs = readInt();
        
        EngineState.isFullVersion = readBoolean();
        
        ShopContent.primaryDamageArticle.currentLevel = readInt();
        ShopContent.primaryRateArticle.currentLevel = readInt();
        ShopContent.primaryCannonArticle.currentLevel = readInt();
        
        ShopContent.specialReloadArticle.currentLevel = readInt();
        ShopContent.specialEMWaveArticle.currentLevel = readInt();
        
        ShopContent.miscShieldArticle.currentLevel = readInt();
        ShopContent.miscLifeArticle.currentLevel = readInt();
        
        //General stats
        GeneralStats.duration = readFloat();
        GeneralStats.kills = readInt();
        GeneralStats.deaths = readInt();
        GeneralStats.orbsSpent = readInt();
        GeneralStats.specialFired = readInt();
        GeneralStats.hellfire = readInt();
        GeneralStats.timeWarp = readInt();
        GeneralStats.superShield = readInt();
        
        GeneralStats.reserved1 = readInt();
        GeneralStats.reserved2 = readInt();
        GeneralStats.reserved3 = readInt();
        
        readAndApply(LevelContent.level1);
        readAndApply(LevelContent.level2);
        readAndApply(LevelContent.level3);
        readAndApply(LevelContent.level4);
    }
    
    public static void readAndApply(LevelInfo lvl) {
        lvl.progressOrbs = readFloat();
        lvl.orbsPlayerRecord = readInt();
        lvl.time = readFloat();
        lvl.damageTaken = readInt();
        lvl.perfectBonus = readBoolean();
        lvl.noSpecialBonus = readBoolean();
        lvl.unlocked = readBoolean();
        lvl.bought = readBoolean();
        lvl.completedOnce = readBoolean();
    }
    
    public static int readInt() {
        try {
            return Integer.parseInt(tokenizer.nextToken());
        } catch(Exception e){}
        return 0;
    }
    
    public static float readFloat() {
        try {
            return Float.parseFloat(tokenizer.nextToken());
        } catch(Exception e){}
        return 0;
    }
    
    public static String readString() {
        return tokenizer.nextToken();
    }
    
    public static boolean readBoolean() {
        return "1".equals(tokenizer.nextToken());
    }
    
    private static StringTokenizer tokenizer = new StringTokenizer("");
    
    public static void tryToLoadSaveAndApply() {
        //Log.d("Preferences", "Loading...");
        String str = Persistence.readPreferences();
        if (str == null || str.length() == 0) return;
        
        //Check signature
        try {
            int lastIndex = str.lastIndexOf("|");
            String dataStr = str.substring(0, lastIndex);
            String suffix = str.substring(lastIndex+1);
            
            String hash = Security.hashString(dataStr+"|");
            
            //Log.d("Pref", "Read tag " + suffix);
            //Log.d("Pref", "Expected " + hash);
            
            if (!hash.equals(suffix)) {
                //Hash is wrong
                //Log.e("Pref", "Wrong hash!");
                return;
            }
            
            readAndApply(str);
            
        } catch (Exception e) {
            //Log.e("Preferences", "Error loading preferences");
            e.printStackTrace();
            return;
        }

    }
    
    
}
