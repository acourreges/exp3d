package com.breakingbyte.game.render;


import java.util.ArrayList;
import java.util.HashMap;

import com.breakingbyte.game.content.PowerUpContent;
import com.breakingbyte.game.level.Level.LevelID;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.level.LevelInfo;
import com.breakingbyte.game.resource.Resource.ResourceLevel;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.wrap.BitmapFont;
import com.breakingbyte.wrap.shared.Renderer;



public class TextureManager {

	private static ArrayList<Texture> allTextures;
	
	public static Texture blank;
	
	public static Texture ship;
	
	public static Texture astrol;
	public static Texture byrol;
	public static Texture crystol; //also bossa
	public static Texture drakol;
	
	public static Texture bossb;
	public static Texture bossd;
	
	public static Texture tunnel;
	public static Texture tunnel_lm;
	
	//Particles
	public static Texture simpleBlast;
	public static Texture explosion;
	public static Texture enemyBullet;
	
	public static Texture fireRing;
	
	public static Texture orbIn;
	public static Texture orbInWhiter;
	public static Texture orbGloss;
	
	public static Texture powerUpBg; 
	public static Texture powerUpTimewarp;
	public static Texture powerUpHellfire;
	public static Texture powerUpSuperShield;
	
	public static Texture star;
	public static Texture shield;
	
	//UI
	public static Texture uiLife;
	public static Texture title;
    public static Texture ribbon;
    public static Texture focuser;
    public static Texture specialIcon;
    public static Texture panel;
    public static Texture panelButton;
    public static Texture panelButtonFrame;
    public static Texture panelButtonGlow;
    public static Texture settingsIcon;
    public static Texture creditsIcon;
    public static Texture arrowIcon;
    public static Texture damage;
    
    //Shop
    public static Texture shopPrimaryDamage;
    public static Texture shopPrimaryRate;
    public static Texture shopPrimaryCannon;
    public static Texture shopSpecialReload;
    public static Texture shopSpecialEMWave;
    public static Texture shopMiscShield;
    public static Texture shopMiscLife;
    
    //Bitmap font texts
    public static BitmapFont defaultFont;
    
    //public static DynamicText txt_PressToPlay;
    public static DynamicText txt_oneMorOrb;
    public static DynamicText txt_TimeWarp;
    public static DynamicText txt_Hellfire;
    public static DynamicText txt_SuperShield;
	
	//--------------------
	//Text
	public static Texture ui_digit;
	public static DynamicText txt_ui_boss_warning;
	public static HashMap<LevelID, DynamicText> txt_ui_levels;
	public static DynamicText txt_ui_ship_focuser;
	
	static {
	    init();
	}
	
	public static void init() {
	    
	    allTextures = new ArrayList<Texture>();
	    
	    blank = createTexture("img/blank.png"); blank.level = ResourceLevel.BOOTLOADER;
	    
	    defaultFont = createBitmapFont("Skir.ttf");
        defaultFont.level = ResourceLevel.BOOTLOADER;
        
        uiLife =  createTexture("img/ui_life.png").useMipMap(true); uiLife.level = ResourceLevel.BOOTLOADER;
        
        //Ship
        ship = createTexture("img/ship.png").useMipMap(true);
        
        //Astrol
        astrol = createTexture("img/astrol.png");
        
        byrol = createTexture("img/byrol.png");
        
        crystol = createTexture("img/crystol.png");
        
        drakol = createTexture("img/drakol.png").useMipMap(true);
        
        bossb = createTexture("img/bossb.png");
        
        bossd = createTexture("img/bossd.png");
        
        tunnel =  createTexture("img/tunnel.png").useMipMap(true);
        
        tunnel_lm =  createTexture("img/tunnel_lm.png");
        
        simpleBlast =  createTexture("img/simple_blast.png");
        
        enemyBullet = createTexture("img/enemy_bullet.png");
        
        fireRing =  createTexture("img/fire_ring.png");
        
        explosion =  createTexture("img/expl.png");
        
        title =  createTexture("img/title.png");
        
        ribbon =  createTexture("img/ribbon.png").useMipMap(true);
        
        focuser =  createTexture("img/focuser.png");
        
        specialIcon =  createTexture("img/special_icon.png");
        
        panel =  createTexture("img/panel.png").useMipMap(true);
        
        panelButton = createTexture("img/button.png").useMipMap(true);
        panelButtonFrame = createTexture("img/button_frame.png").useMipMap(true);
        panelButtonGlow = createTexture("img/button_glow.png").useMipMap(true);
        
        orbIn = createTexture("img/orb_in.png").useMipMap(true);
        orbInWhiter = createTexture("img/orb_in_whiter.png").useMipMap(true);
        orbGloss = createTexture("img/orb_gloss.png").useMipMap(true);
        
        powerUpBg = createTexture("img/powerup.png");
        powerUpTimewarp = createTexture("img/pu_timewarp.png");
        powerUpHellfire = createTexture("img/pu_hellfire.png");
        powerUpSuperShield = createTexture("img/pu_shield.png");
        
        star = createTexture("img/star.png");
        shield = createTexture("img/shield.png");
        
        settingsIcon = createTexture("img/settings.png");
        creditsIcon = createTexture("img/credits.png");
        arrowIcon = createTexture("img/arrow.png");
        damage = createTexture("img/damage.png");
        
        shopPrimaryDamage = createTexture("img/shop_primary_dmg.png");
        shopPrimaryRate = createTexture("img/shop_primary_rate.png");
        shopPrimaryCannon = createTexture("img/shop_primary_cannon.png");
        shopSpecialReload = createTexture("img/shop_special_reload.png");
        shopSpecialEMWave = createTexture("img/shop_special_emwave.png");
        shopMiscShield = createTexture("img/shop_misc_shield.png");
        shopMiscLife = createTexture("img/shop_misc_life.png");
        
	}
	
	public static final Texture createTexture(String filePath) {
	    Texture result = new Texture(filePath);
	    allTextures.add(result);
	    return result;
	}
	
	public static final BitmapFont createBitmapFont(String fontFilePath) {
	    BitmapFont result = new BitmapFont(fontFilePath);
	    allTextures.add(result);
	    return result;
	}
	
	/**
	 * Loads all the textures.
	 */
	public static boolean loadTextures(int level){
		
	    if (ui_digit == null && level > ResourceLevel.BOOTLOADER) {
	        //Special case: is runtime-generated on Android only
	        ui_digit = TextTexture.generate(TextTexture.TextId.DIGITS);
	        allTextures.add(ui_digit);
	    }
	    
	    Renderer.enableTexture();
        //gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		
	    boolean allLoaded = true;
	    
	    for (int i = 0; i < allTextures.size(); i++) {
	        Texture tex = allTextures.get(i);
	        if (tex.level <= level && !tex.isLoaded) {
	            tex.load();
	            allLoaded = false;
	        }
	    }
        
        return allLoaded;
        //ui_boss_warning = TextTexture.generate(TextTexture.TextId.BOSS_WARNING);
        //ui_level_title_1 = TextTexture.generate(TextTexture.TextId.LEVEL_TITLE_1);
        //ui_level_title_2 = TextTexture.generate(TextTexture.TextId.LEVEL_TITLE_2);
        //ui_ship_focuser = TextTexture.generate(TextTexture.TextId.SHIP_FOCUSER);
        
	}
	
	public static float getProgress(int level) {
	    int total = 0;
	    int done = 0;
	    
	    for (int i = 0; i < allTextures.size(); i++) {
            Texture tex = allTextures.get(i);
            if (tex.level <= level) {
                total++;
                if (tex.isLoaded) done++;
            }
        }
	    
	    if (total == 0) return 1f;
	    return (float)done / (float)total;
	}
	
	public static void trashAll() {
	    if (ui_digit != null) allTextures.remove(ui_digit);
	    ui_digit = null;
	    
        for (int i = 0; i < allTextures.size(); i++) {
            Texture tex = allTextures.get(i);
            tex.isLoaded = false;
        }	    
	}
	
	public static boolean isAllLoaded(int level) {
        for (int i = 0; i < allTextures.size(); i++) {
            Texture tex = allTextures.get(i);
            if (tex.level <= level) {
                if (!tex.isLoaded) return false;
            }
        }
        return true;
    }
	 
	public static void loadDynamicTextTextures() {
	    
	    /*
	    txt_PressToPlay = new DynamicText(13);
	    txt_PressToPlay.setAlignment(Align.CENTER);
	    txt_PressToPlay.printString("Press to play");
	    txt_PressToPlay.updateBuffers();
	    */
	    
	    txt_oneMorOrb      = generateDynamicText("+1 orb");
	    txt_TimeWarp       = generateDynamicText(PowerUpContent.timeWarp.name);
	    txt_Hellfire       = generateDynamicText(PowerUpContent.hellfire.name);
	    txt_SuperShield    = generateDynamicText(PowerUpContent.superShield.name);
	    
	    txt_ui_levels = new HashMap<LevelID, DynamicText>();
	    for (int i = 0; i < LevelContent.allLevels.size(); i++) {
	        LevelInfo lvl = LevelContent.allLevels.get(i);
	        txt_ui_levels.put(lvl.levelId, generateDynamicText(lvl.name));
	    }
	    txt_ui_boss_warning = generateDynamicText("warning");
	    txt_ui_ship_focuser = generateDynamicText("ship");
	    
	}
	
	private static DynamicText generateDynamicText(String str) {
	    DynamicText result = new DynamicText(str.length());
	    result.setAlignment(Align.CENTER);
	    result.printString(str);
	    result.updateBuffers();
	    return result;
	}
	
	
}
