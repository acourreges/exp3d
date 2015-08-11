package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.ui.dialog.LevelClearedDialog;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.Shaker;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;


public class UI {

    private static LifeBar lifeBar;
    private static SmoothJoin lifeBarJoin;
    private static RoundGlass roundGlass;
    private static RoundGlassProgress specialReload;
    private static Digit lifeCount;
    
    private static Shaker shaker;
    private static SmoothJoin damageScreenColor;
    
    private static boolean displayBossLifeBar;
    private static LifeBar bossLifeBar;
    private static SmoothJoin bossLifeBarJoin;
    private static final float bossLifeBarJoinOffset = 20f;
    
    public static float defaultColor[] = {0.0f,0.36f,0.566f, /*0.4f*/0.85f}; 
    
    public static RibbonMessage bossWarning;
    public static LevelTitle levelTitle;
    
    public static Focuser shipFocuser;
    
    public static PauseButton pauseButton;
    public static OrbCounter orbCounter;
    
    public static LevelClearedDialog levelClearedDialog;
    
    public static TutorialMessage tutorialMessage;

    
    public static void initialize() {
        lifeBar = new LifeBar();
        lifeBar.setup(60f, 12f, 4f);
        lifeBar.setPosition(26f, 5f);
        lifeBarJoin = new SmoothJoin();
        
        roundGlass = RoundGlass.generateLifeCounter();
        roundGlass.setSize(15f, 15f);
        roundGlass.setPosition(90f - 1f, 10f + 1f);
        
        specialReload = new RoundGlassProgress();
        specialReload.setSize(20f, 20f);
        specialReload.setPosition(13f, 10f + 1f);
        
        lifeCount = new Digit();
        lifeCount.setSize(7f, 7f);
        lifeCount.setPosition(90f - 1f, 10f + 1f);
        
        shaker = new Shaker();
        shaker.shakingSpeed = 4f;
        shaker.xAmplitude = 4f; shaker.xOscill = 50f;
        shaker.yAmplitude = 3f; shaker.yOscill = 30f;
        
        damageScreenColor = new SmoothJoin(4);
        //damageScreenColor.setInterpolator(Interpolator.SINUSOIDAL);
        damageScreenColor.init(0f, 0f, 0f, 0f);
        damageScreenColor.setTarget(0f, 0f, 0f, 0f, 0f);
        
        bossLifeBarJoin = new SmoothJoin();
        bossLifeBar = new LifeBar();
        bossLifeBar.setup(Screen.ARENA_WIDTH - 50f, 12f, 5f);
        
        bossWarning = new BossWarning();
        levelTitle = new LevelTitle();
        
        shipFocuser = new Focuser();
        
        pauseButton = new PauseButton();
        
        orbCounter = new OrbCounter();
        
        levelClearedDialog = new LevelClearedDialog();
        
        tutorialMessage = new TutorialMessage();
        tutorialMessage.init();
    }
    
    public static void reset() { //reset any open dialog / in-progress animation
        lifeBar.reset();
        lifeBarJoin.init(-50);
        lifeBarJoin.setTarget(-30, 1f);
        
        setLifeNumber(Engine.player.lifeNumber);
        setLifeAmount(1f);
        
        displayBossLifeBar = false;
        bossLifeBarJoin.init(bossLifeBarJoinOffset);
        bossLifeBarJoin.setTarget(bossLifeBarJoinOffset, 1f);
        
        bossWarning.reset();
        levelTitle.reset();
        levelTitle.levelId = ArenaState.currentLevel;
        
        shipFocuser.reset();
        
        levelClearedDialog.reset();
        
        damageScreenColor.init(0f, 0f, 0f, 0f);
        damageScreenColor.setTarget(0f, 0f, 0f, 0f, 0f);
        tutorialMessage.reset();
        synchronizeOrbCounter();
    }
    
    public static void displayBossWarning(boolean display) {
        if (display) bossWarning.startAppearAnimation();
        else bossWarning.startDisappearAnimation();
    }

    public static void displayLevelTitle(boolean display) {
        if (display) levelTitle.startAppearAnimation();
        else levelTitle.startDisappearAnimation();
    }
    
    public static void displayShipFocuser(boolean display) {
        if (display) shipFocuser.startAppearAnimation();
        else shipFocuser.startDisappearAnimation();
    }
    
    public static void displayLifeBar() {
        lifeBarJoin.init(lifeBarJoin.get());
        lifeBarJoin.setTarget(0, 3f);
    }
    
    public static void hideLifeBar() {
        lifeBarJoin.init(lifeBarJoin.get());
        lifeBarJoin.setTarget(-30, 3f);
    }
    
    public static boolean isPauseButtonVisible() {
        return lifeBarJoin.get() > -15;
    }
    
    public static void displayBossLifeBar(boolean display) {
        displayBossLifeBar = display;
        if (displayBossLifeBar) {
            setBossLifeAmount(1.0f);
            bossLifeBarJoin.init(bossLifeBarJoinOffset);
            bossLifeBarJoin.setTarget(0f, 3f);
        } else {
            bossLifeBarJoin.init(bossLifeBarJoin.get());
            bossLifeBarJoin.setTarget(bossLifeBarJoinOffset, 3f);
        }
    }
    
    private static boolean isBossLifeVisible() {
        return bossLifeBarJoin.get() < 0.9f * bossLifeBarJoinOffset;
    }
    
    public static void setLifeAmount(float amount) {
        lifeBar.setLife(amount);
    }
    
    public static void setBossLifeAmount(float amount) {
        bossLifeBar.setLife(amount);
    }
    
    public static void setLifeNumber(int number) {
        lifeCount.setLifeValue(number, true);
    }
    
    public static void setSpecialWeaponProgress(float value) {
        specialReload.setProgress(value);
    }
    
    public static void shake() {
        shaker.shake();
        damageScreenColor.init(1f, 0f, 0f, 0.5f);
        damageScreenColor.setTarget(1f, 0f, 0f, 0f, 2f);
    }
    
    public static void displayLevelClearedDialog() {
        levelClearedDialog.loadValues();
        levelClearedDialog.show();
        ArenaState.instance.stats.saveData();
    }

    
    public static void update() {
        lifeBar.update();
        lifeBarJoin.update();
        lifeCount.update();
        specialReload.update();
        bossWarning.update();
        levelTitle.update();
        bossLifeBarJoin.update();
        if (isBossLifeVisible()) {
            //bossLifeBar.setup(Screen.ARENA_WIDTH - 50f, 10f, 5f);
            //bossLifeBar.setPosition((Screen.ARENA_WIDTH - 50f)*0.52f, Screen.ARENA_HEIGHT - 12f + bossLifeBarJoin.get());
            bossLifeBar.setPosition((Screen.ARENA_WIDTH - 50f)*0.57f, Screen.ARENA_HEIGHT - 13f + bossLifeBarJoin.get());
            bossLifeBar.update();
        }
        shipFocuser.update();
        orbCounter.update();
        levelClearedDialog.update();
        shaker.update();
        damageScreenColor.update();
        tutorialMessage.update();
    }
    
    public static void handleInput(float x, float y, Controller.TouchAction action) {
        levelClearedDialog.handleTouch(x, y, action);
    }
    
    public static void synchronizeOrbCounter() {
        orbCounter.updateNumberValue(ArenaState.instance.stats.orbsCollected);
    }
    
    public static void render(){
        
        Renderer.Dithering.enable();
        
        shipFocuser.render();
        tutorialMessage.render();

        Renderer.Blending.resetMode();
        
        
        if (damageScreenColor.get() >= 0.05f) {
            //Draw fullscreen quad
            Renderer.setColor(damageScreenColor.get(0), damageScreenColor.get(1), damageScreenColor.get(2), damageScreenColor.get(3));
            TextureManager.damage.bind();
            {
                Renderer.pushMatrix();
                Renderer.translate(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f, 0f);
                Renderer.scale(Screen.ARENA_WIDTH * 1.1f, Screen.ARENA_HEIGHT * 1.1f, 1f);
                QuadVBO.drawQuad();
                Renderer.popMatrix();
            }
            Renderer.resetColor();
        }
        
        {
            
            Renderer.pushMatrix();
            
            {   //Pause button
                Renderer.pushMatrix();
                float pauseX = Screen.ARENA_WIDTH - 8f - lifeBarJoin.get();
                float pauseY = Screen.ARENA_HEIGHT - 8f * 0.84f;
                Renderer.translate(pauseX, pauseY, 0);
                pauseButton.render();
                Renderer.popMatrix();
            }
            
            //Shaking
            shaker.applyTransformation();
            
            {   
                Renderer.pushMatrix();
                Renderer.translate(10 + lifeBarJoin.get(), Screen.ARENA_HEIGHT - 7f, 0);
                orbCounter.render();
                Renderer.popMatrix();
                
                Renderer.pushMatrix();
                Renderer.translate(0f, lifeBarJoin.get(), 0f);
                lifeBar.render();
                roundGlass.render();
                lifeCount.render();
                specialReload.render();
                Renderer.popMatrix();
            }
            
            if (isBossLifeVisible()) bossLifeBar.render();
        
            Renderer.popMatrix();
        }
        
        Renderer.Blending.resetMode();
        bossWarning.render();
        levelTitle.render();
        
        levelClearedDialog.render();
        
        Renderer.Dithering.disable();
    }
    
}
