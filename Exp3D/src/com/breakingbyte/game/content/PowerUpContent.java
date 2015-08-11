package com.breakingbyte.game.content;

import java.util.ArrayList;

import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.entity.bonus.Bonus.BonusType;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;

public class PowerUpContent {
    
    public static abstract class PowerUpItem {
        
        public BonusType bonusType;
        
        public float red, green, blue;
        
        public SoundId soundId;
        
        public String name;
        
        public PowerUpItem() {
            init();
        }
        
        public abstract void init();
        public abstract Texture getTexture();
        public abstract DynamicText getDynamicText();
    }
    
    public static  PowerUpItem getPowerUpItemFromEnum(BonusType bonus) {
        for (int i = 0; i < allPowerUps.size(); i++) {
            if (allPowerUps.get(i).bonusType == bonus) return allPowerUps.get(i);
        }
        return null;
    }
    
    public static ArrayList<PowerUpItem> allPowerUps;
    public static PowerUpItem timeWarp;
    public static PowerUpItem hellfire;
    public static PowerUpItem superShield;
    
    public static void init() {
        
        allPowerUps = new ArrayList<PowerUpContent.PowerUpItem>();
        
        timeWarp = new PowerUpItem() {

            public void init() {
                name = "Time Warp";
                bonusType = BonusType.TIME_WARP;
                red = 104f / 255f; green = 172f / 255f; blue = 1f;
                soundId = SoundId.BONUS_TIME;
            }
            public Texture getTexture() { return TextureManager.powerUpTimewarp; }
            public DynamicText getDynamicText() { return TextureManager.txt_TimeWarp; }
            
        };
        allPowerUps.add(timeWarp);
        
        hellfire = new PowerUpItem() {

            public void init() {
                name = "Hellfire";
                bonusType = BonusType.HELLFIRE;
                red = 1f; green = 0.3f; blue = 0.3f;
                soundId = SoundId.BONUS_HELLFIRE;
            }
            public Texture getTexture() { return TextureManager.powerUpHellfire; }
            public DynamicText getDynamicText() { return TextureManager.txt_Hellfire; }
            
        };
        allPowerUps.add(hellfire);
        
        superShield = new PowerUpItem() {

            public void init() {
                name = "Super Shield";
                bonusType = BonusType.SUPER_SHIELD;
                red = 0.0f; green = 0.8f; blue = 0.3f;
                soundId = SoundId.BONUS_SUPER_SHIELD;
            }
            public Texture getTexture() { return TextureManager.powerUpSuperShield; }
            public DynamicText getDynamicText() { return TextureManager.txt_SuperShield; }
            
        };
        allPowerUps.add(superShield);
        
    }
    
    

}
