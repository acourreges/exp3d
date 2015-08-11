package com.breakingbyte.game.content;

import java.util.ArrayList;

import com.breakingbyte.game.entity.Player;
import com.breakingbyte.game.entity.particle.SimpleBlast;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;

public class ShopContent {
    
    public static final String TAG = "ShopContent";
    
    public static ArrayList<ArticleGroup> allGroups;
    
    public static ArticleGroup 
                    primaryWeaponGroup,
                    specialWeaponGroup,
                    miscGroup;
    
    public static Article
                    primaryDamageArticle,
                    primaryRateArticle,
                    primaryCannonArticle,
                    
                    specialReloadArticle,
                    specialEMWaveArticle,
                    
                    miscShieldArticle,
                    miscLifeArticle;
    
    public static void init() {
        
        //---------- Primary -------------
        primaryWeaponGroup = new ArticleGroup();
        primaryWeaponGroup.name = "Primary Weapon";
        
        
        primaryDamageArticle = 
            new Article() {
            public String  getName()        { return "Damage"; }
            public Texture getIcon()        { return TextureManager.shopPrimaryDamage; }
            public int     getPrice()       { return 30; }
            public int     getMaxLevel()    { return 15; }
            public int     getValueForLevel(int lvl) { return lvl * 5; }
            public void    printLabelForLevel(int lvl) {
                printStr("+").printInt(getValueForLevel(lvl)).printStr("%");
            }
            public void    printShortLabelForLevel(int lvl) {
                printLabelForLevel(lvl);
            }
            public void printLongDescription(DynamicText txt) {
                txt.printString("Each shot fired by the").newLine();
                txt.printString("primary weapon will").newLine();
                txt.printString("cause greater damage.");
            }
            public void applyToEngineState() {
                SimpleBlast.CURRENT_DAMAGE = (int) (SimpleBlast.BASE_DAMAGE * (1f + getValueForCurrentLevel() / 100f));
                //Log.d(TAG, "Bonus " + getName() + " has now for value: " + SimpleBlast.CURRENT_DAMAGE);
            }
            };
        primaryWeaponGroup.addArticle(primaryDamageArticle);
        
        primaryRateArticle =
            new Article() {
            public String  getName()        { return "Shot Rate"; }
            public Texture getIcon()        { return TextureManager.shopPrimaryRate; }
            public int     getPrice()       { return 30; }
            public int     getMaxLevel()    { return 10; }
            public int     getValueForLevel(int lvl) { return lvl * 10; }
            public void    printLabelForLevel(int lvl) {
                printStr("+").printInt(getValueForLevel(lvl)).printStr("% faster");
            }
            public void    printShortLabelForLevel(int lvl) {
                printStr("+").printInt(getValueForLevel(lvl)).printStr("%");
            }
            public void printLongDescription(DynamicText txt) {
                txt.printString("Primary weapon shots").newLine();
                txt.printString("will be fired at a").newLine();
                txt.printString("faster rate.");
            }
            public void applyToEngineState() {
                float reduce = getValueForCurrentLevel();
                if (reduce > 100) reduce = 99;
                Player.DELAY_FIRE = (Player.DELAY_FIRE_BASE * (1f - reduce / 100f));
                //Log.d(TAG, "Bonus " + getName() + " has now for value: " + Player.DELAY_FIRE);
            }
            };
        primaryWeaponGroup.addArticle(primaryRateArticle);
        
        primaryCannonArticle =
            new Article() {
            public String  getName()        { return "Extra Cannon"; }
            public Texture getIcon()        { return TextureManager.shopPrimaryCannon; }
            public int     getPrice()       { return 155; }
            public int     getMaxLevel()    { return 3; }
            public int     getValueForLevel(int lvl) { return lvl; }
            public void    printLabelForLevel(int lvl) {
                printInt(getValueForLevel(lvl)).printStr(" cannon");
                if (lvl > 1) printStr("s");
            }
            public void    printShortLabelForLevel(int lvl) {
                printInt(getValueForLevel(lvl));
            }
            public void printLongDescription(DynamicText txt) {
                txt.printString("Install one additional").newLine();
                txt.printString("cannon to the ship.");
            }
            public void applyToEngineState() {
                int lvl = getValueForCurrentLevel();
                Player.leftFire = lvl >= 2;
                Player.rightFire = lvl >= 2;
                Player.centerFire = lvl != 2;
            }
            };
        primaryWeaponGroup.addArticle(primaryCannonArticle);
        
        //---------- Special -------------
        specialWeaponGroup = new ArticleGroup();
        specialWeaponGroup.name = "Special Weapon";
        
        
        specialReloadArticle =
            new Article() {
            public String  getName()        { return "Reload Speed"; }
            public Texture getIcon()        { return TextureManager.shopSpecialReload; }
            public int     getPrice()       { return 50; }
            public int     getMaxLevel()    { return 50; }
            
            public int     getValueForLevel(int lvl) { return lvl * 10; }
            public void    printLabelForLevel(int lvl) {
                printStr("+").printInt(getValueForLevel(lvl)).printStr("% faster");
            }
            public void    printShortLabelForLevel(int lvl) {
                printStr("+").printInt(getValueForLevel(lvl)).printStr("%");
            }
            public void printLongDescription(DynamicText txt) {
                txt.printString("Special weapon will take").newLine();
                txt.printString("less time to reload").newLine();
                txt.printString("between 2 shots.");
            }
            public void  applyToEngineState() {
                float reduce = getValueForCurrentLevel();
                if (reduce > 100) reduce = 99;
                Player.SPECIAL_WEAPON_DELAY =  Player.SPECIAL_WEAPON_DELAY_BASE * (1f - reduce / 100f);
            }
            };
        specialWeaponGroup.addArticle(specialReloadArticle);
        
        specialEMWaveArticle =
            new Article() {
            public String  getName()        { return "EM Wave"; }
            public Texture getIcon()        { return TextureManager.shopSpecialEMWave; }
            public int     getPrice()       { return 130; }
            public int     getMaxLevel()    { return 1; }
            public int     getValueForLevel(int lvl) { return lvl; }
            public void    printLabelForLevel(int lvl) {
                printStr(lvl == 0? "None" : "Equiped");
            }
            public void    printShortLabelForLevel(int lvl) {
                printLabelForLevel(lvl);
            }
            public void printLongDescription(DynamicText txt) {
                txt.printString("When firing the special").newLine();
                txt.printString("weapon, an EM Wave will").newLine();
                txt.printString("disable enemy bullets.");
            }
            public void applyToEngineState() {
                Player.EM_WAVE_ON = getValueForCurrentLevel() == 1;
            }
            };
        specialWeaponGroup.addArticle(specialEMWaveArticle);
        
        //---------- Misc -------------
        miscGroup = new ArticleGroup();
        miscGroup.name = "Other";
        
        miscShieldArticle =
            new Article() {
            public String  getName()        { return "Shield"; }
            public Texture getIcon()        { return TextureManager.shopMiscShield; }
            public int     getPrice()       { return 15; }
            public int     getMaxLevel()    { return 10; }
            public int     getValueForLevel(int lvl) { return lvl * 10; }
            public void    printLabelForLevel(int lvl) {
                printStr("-").printInt(getValueForLevel(lvl)).printStr("% damage");
            }
            public void    printShortLabelForLevel(int lvl) {
                printStr("-").printInt(getValueForLevel(lvl)).printStr("%");
            }
            public void printLongDescription(DynamicText txt) {
                txt.printString("The shield will lower").newLine();
                txt.printString("damage taken from").newLine();
                txt.printString("enemy bullets.");
            }
            public void applyToEngineState() {
                Player.DAMAGE_KEEPER_PERCENT = 100 - getValueForCurrentLevel();
            }
            };
        miscGroup.addArticle(miscShieldArticle);
        
        miscLifeArticle =
            new Article() {
            public String  getName()        { return "Extra Life"; }
            public Texture getIcon()        { return TextureManager.shopMiscLife; }
            public int     getPrice()       { return 120; }
            public int     getMaxLevel()    { return 5; }
            public int     getValueForLevel(int lvl) { return lvl; }
            public void    printLabelForLevel(int lvl) {
                printInt(getValueForLevel(lvl)).printStr(lvl == 1? " life" : " lives");
            }
            public void    printShortLabelForLevel(int lvl) {
                printInt(getValueForCurrentLevel());
            }
            public void printLongDescription(DynamicText txt) {
                txt.printString("Grants one additional").newLine();
                txt.printString("life at the start of").newLine();
                txt.printString("each chapter.");
            }
            public void applyToEngineState() {
                Player.LIFE_NUMBER_START = getValueForCurrentLevel();
            }
            };
        miscGroup.addArticle(miscLifeArticle);
        
        //---------------------------------------
        allGroups = new ArrayList<ArticleGroup>();
        
        allGroups.add(primaryWeaponGroup);
        allGroups.add(specialWeaponGroup);
        allGroups.add(miscGroup);
        
    }
    
    public static void updateAllPanelsFromArticles() {
        for (int i = 0; i < allGroups.size(); i++) {
            allGroups.get(i).updateAllPanelsFromArticles();
        }
    }
    
    public static void applyAllToEngineState() {
        for (int i = 0; i < allGroups.size(); i++) {
            allGroups.get(i).applyAllToEngineState();
        }
    }
    
}
