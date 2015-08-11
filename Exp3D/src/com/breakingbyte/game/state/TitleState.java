package com.breakingbyte.game.state;

import java.util.ArrayList;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.engine.Layer;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.Player;
import com.breakingbyte.game.entity.SparkleGroup;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.level.LevelInfo;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.ImageWidget;
import com.breakingbyte.game.ui.Widget.TouchListener;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.ui.anim.AlphaAnimation;
import com.breakingbyte.game.ui.anim.Animation;
import com.breakingbyte.game.ui.anim.ScaleAnimation;
import com.breakingbyte.game.ui.anim.TranslateAnimation;
import com.breakingbyte.game.ui.dialog.ChapterButton;
import com.breakingbyte.game.ui.dialog.ShopButton;
import com.breakingbyte.game.ui.dialog.SideButton;
import com.breakingbyte.game.ui.dialog.SideButtonMenu;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.Shaker;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.Platform;
import com.breakingbyte.wrap.shared.Light;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;
import com.breakingbyte.wrap.shared.Timer;

public class TitleState extends State {
    
    public static TitleState instance = new TitleState();

    public State getInstance() { return instance; }

    public float elapsed;
    
    private static boolean doIntro;
    
    public static State lastSubState = null;
    
    //UI - Only Title
    private static ImageWidget titleWidget;
    private static DynamicText pressToPlayTxt;
    
    //UI - Main menu (level selection + shop/option buttons)
    public static WidgetContainer mainCanvas;
    
    public static WidgetContainer levelSelector;
    public static Animation levelSelectorTranslate, levelSelectorAlpha;
    
    private static ArrayList<ChapterButton> chapterButtons;
    
    public static SideButtonMenu sideMenu;
    
    public static SmoothJoin shopButtonJoin;
    public static float shopButtonJoinTarget = 40;
    public static ShopButton shopButton;
    
    public static float bgLightNextSwitch = 0;
    public static boolean bgLightOn = false; 
    public static int bgLightPhase = -1;
    public static float[] bgLightDurations = new float[] {10, 0.5f, /*0.1f, 0.1f,*/ 0.5f, /*0.2f, 0.6f,*/ /* 0.1f, 0.2f,*/ 0.1f, 0.2f, 0.1f, 0.1f};
    
    public static Shaker bgShaker;
    
    private static Layer layerSparkle;
    
    public static void init() {
        
        doIntro = true;
        
        //Intro widgets
        
        titleWidget = new ImageWidget();
        float spriteWidth = Screen.ARENA_WIDTH * 0.95f;
        titleWidget.setSize(spriteWidth, spriteWidth * 0.3f);
        titleWidget.setPosition(50, Screen.ARENA_HEIGHT- 20);
        titleWidget.setTexture(TextureManager.title);
        
        titleWidget.addAnimation(new AlphaAnimation(1f, 0f, 3f, 0.6f));
        titleWidget.addAnimation(new ScaleAnimation(1f, 1.8f, 3f, 0.6f));
        
        pressToPlayTxt = new DynamicText(13);
        pressToPlayTxt.setAlignment(Align.CENTER);
        pressToPlayTxt.printString("Press to play");
        pressToPlayTxt.updateBuffers();
        
        pressToPlayTxt.addAnimation(new AlphaAnimation(1f, 0f, 3f, 0.6f));
        pressToPlayTxt.addAnimation(new ScaleAnimation(1f, 1.8f, 3f, 0.6f));
        
        //Main menu widgets
        mainCanvas = new WidgetContainer();
        mainCanvas.setSizeFullScreen();
                
        levelSelectorTranslate = new TranslateAnimation(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_WIDTH * 1.5f, 0.6f).onX(true); //new ScaleAnimation(1f, 1.3f, 2f);
        levelSelectorTranslate.join.setInterpolator(Interpolator.SINUSOIDAL_SLOW_START);
        levelSelectorAlpha = new AlphaAnimation(1f, 0f, 2f);
        
        levelSelector = new WidgetContainer();
        levelSelector.setSizeFullScreen();
        levelSelector.setPosition(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f);
        chapterButtons = new ArrayList<ChapterButton>();
        
        for (int i = 0; i < LevelContent.allLevels.size(); i++) {
            addChapter(i+1, LevelContent.allLevels.get(i));
        }
        
        //Side buttons
        sideMenu = new SideButtonMenu();
        
        SideButton optionButton = sideMenu.generateLeftButton();
        optionButton.setImage(TextureManager.settingsIcon, 14f, 14f, 0.9f);
        optionButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y){ instance.buttonOptionsPressed(); } });
        
        SideButton creditButton = sideMenu.generateRightButton();
        creditButton.setImage(TextureManager.creditsIcon, 12f, 12f, 0.9f);
        creditButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y){ instance.buttonCreditsPressed(); } });
        
        shopButtonJoin = new SmoothJoin();
        shopButtonJoin.setInterpolator(Interpolator.BACK_START_END);
        shopButtonJoin.setBack(2.5f);
        shopButtonJoin.init(shopButtonJoinTarget);
        shopButtonJoin.setTarget(shopButtonJoinTarget, 0f);
        
        float shopWidth = Screen.ARENA_WIDTH - 45 * 0.5f * 2f + 10f;
        shopButton = new ShopButton();
        shopButton.setSize(shopWidth, 25f);
        shopButton.setPosition(Screen.ARENA_WIDTH * 0.5f, 10);
        shopButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y){ instance.buttonShopPressed(); } });
        
        mainCanvas.addChild(sideMenu);
        mainCanvas.addChild(shopButton);
        mainCanvas.addChild(levelSelector);
        
        bgShaker = new Shaker();
        bgShaker.shakingSpeed = 4f;
        bgShaker.shakingSpeed = 4f;
        bgShaker.xAmplitude = 0.6f; bgShaker.xOscill = 40f;
        bgShaker.yAmplitude = 0.5f; bgShaker.yOscill = 30f;
        
        layerSparkle = new Layer();
    }
    
    private static void addChapter(int position, LevelInfo levelInfo) {
        
        int number = position; 
        final ChapterButton chapter = new ChapterButton(levelInfo);
        chapter.loadFromLevelInfo();
        chapter.setPosition(0, Screen.ARENA_HEIGHT * 0.5f - 20 - (number-1) * Screen.ARENA_HEIGHT * 0.16f);
        float delay = 0.8f + 0.2f * number;
        delay = 0.1f + 0.22f * number;
        float duration = 3.5f;
        chapter.addAnimation(new AlphaAnimation(0f, 1f, duration, delay));
        chapter.addAnimation(new ScaleAnimation(2.5f, 1f, duration, delay));
        
        TranslateAnimation translateAnimationX = new TranslateAnimation(Screen.ARENA_WIDTH * 2f, chapter.getPosX(), 1.9f, delay).onX(true);
        translateAnimationX.join.setInterpolator(Interpolator.ELASTIC).setElasticValues(1.1f, 0.8f);
        chapter.addAnimation(translateAnimationX);
        
        TranslateAnimation translateAnimationY = new TranslateAnimation(chapter.getPosY() - 100f,   chapter.getPosY(), 2.2f, delay).onY(true);
        translateAnimationY.join.setInterpolator(Interpolator.ELASTIC).setElasticValues(1f, 2.2f);
        chapter.addAnimation(translateAnimationY);
        
        chapter.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y){ instance.buttonLevelPressed(chapter); } });
        
        levelSelector.addChild(chapter);
        chapterButtons.add(chapter);
    }
    
    public void updateImpl() {
        updateControl();
        updateUI();
    }
    
    private void spawnSparkle(boolean phase) {
        //--- middle
        SparkleGroup sg = SparkleGroup.newInstance();
        sg.populatePattern(phase);
        sg.posX = -3f;
        sg.posY = 6.5f;
        sg.posZ = 5f;
        layerSparkle.addEntity(sg);
        //--- right
        sg = SparkleGroup.newInstance();
        sg.populatePattern(!phase);
        sg.posX = 1f;
        sg.posY = 5f;
        sg.posZ = 8f;
        layerSparkle.addEntity(sg);
        //--- left
        sg = SparkleGroup.newInstance();
        sg.populatePattern(!phase);
        sg.posX = -7f;
        sg.posY = 8.5f;
        sg.posZ = 2f;
        layerSparkle.addEntity(sg);
        
        //Bottom
        sg = SparkleGroup.newInstance();
        sg.populatePattern(!phase);
        sg.posX = -8f;
        sg.posY = -3.0f;
        sg.posZ = -5f;
        layerSparkle.addEntity(sg);
        
        sg = SparkleGroup.newInstance();
        sg.populatePattern(phase);
        sg.posX = -3f;
        sg.posY = -6.0f;
        sg.posZ = -3f;
        layerSparkle.addEntity(sg);
        
        sg = SparkleGroup.newInstance();
        sg.populatePattern(!phase);
        sg.posX = 1f;
        sg.posY = -7.5f;
        sg.posZ = 3f;
        layerSparkle.addEntity(sg);
    }
    
    public void updateUI() {
        elapsed += Timer.delta;
        bgLightNextSwitch -= Timer.delta;
        bgShaker.update();
        
        layerSparkle.update();
        
        tunnelOffset -= Timer.delta * TUNNEL_SCALE * 12f;
        if (tunnelOffset <  0.06f * TUNNEL_PART_LENGTH * TUNNEL_SCALE) 
            tunnelOffset += 1*TUNNEL_PART_LENGTH * TUNNEL_SCALE;
        
        titleWidget.update();
        pressToPlayTxt.update();
        
        if (!doIntro) {
            shopButtonJoin.update();
            shopButton.setPosition(Screen.ARENA_WIDTH * 0.5f, 25f - shopButtonJoin.get());
            
            mainCanvas.update();
            
            if (lastSubState != null) lastSubState.updateUI();
        }
        
    }
    
    public void updateControl() {
        
        if (!transitionInProgress) readControls();

        if (doIntro) {
            titleWidget.resetAnimations();
            pressToPlayTxt.resetAnimations();
        } else {
            if (Controller.hasEvent) {
                if (!transitionInProgress) {
                    mainCanvas.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
                }
            }
        }
    }    

    
    static float TUNNEL_PART_LENGTH = 23f;
    static float TUNNEL_SCALE = 10f;
    static float tunnelOffset = TUNNEL_PART_LENGTH * TUNNEL_SCALE;

    static final float flameUVs[] = {
        0.25f + 0.2f*0.25f, 0.999f,  //bottom left
        0.25f + 0.8f*0.25f, 0.999f,  //bottom right
        0.25f + 0.2f*0.25f, 0.4f,  //top left
        0.25f + 0.8f*0.25f, 0.4f   //top right
    };
    
    public void renderBGMesh() {
        Renderer.pushMatrix();
        
        Renderer.pushMatrix();
        Renderer.scale(TUNNEL_SCALE, TUNNEL_SCALE, TUNNEL_SCALE);
        ModelManager.tunnel.mesh.render();
        Renderer.popMatrix();
        
        Renderer.translate(-TUNNEL_PART_LENGTH * TUNNEL_SCALE, 0, 0);
        
        Renderer.pushMatrix();
        Renderer.scale(TUNNEL_SCALE, TUNNEL_SCALE, TUNNEL_SCALE);
        ModelManager.tunnel.mesh.render();
        Renderer.popMatrix();
        Renderer.popMatrix();
    }
    
    public void renderSparkle() {
        Renderer.pushMatrix();
        renderSparkleUnit();
        Renderer.translate(-TUNNEL_PART_LENGTH * TUNNEL_SCALE, 0, 0);
        renderSparkleUnit();
        Renderer.popMatrix();
    }
    
    private void renderSparkleUnit() {
        Renderer.pushMatrix();
        TextureManager.astrol.bind();
        
        Renderer.scale(TUNNEL_SCALE, TUNNEL_SCALE, TUNNEL_SCALE);
        
        
        Renderer.rotate(-85, 1, 0, 0);
        
        Renderer.rotate(-30, 1, 0, 1);
        Renderer.rotate(40, 0, 0, 1);
        Renderer.rotate(40, 0, 1, 0);
        
        layerSparkle.render();
        /*
        Renderer.translate(3, 4, 9);
        
        //some scaling
        Renderer.pushMatrix();
        Renderer.scale(3f, 3f, 3f);
        QuadVBO.drawQuad();
        Renderer.popMatrix();
        
        Renderer.translate(-3, -4, 0);
        
        //some scaling
        Renderer.pushMatrix();
        Renderer.scale(1f, 1f, 1f);
        QuadVBO.drawQuad();
        Renderer.popMatrix();
        */
        
        
        Renderer.unbindVBOs();
        Renderer.popMatrix();
    }
    
    public void renderImpl() {
        
        Renderer.setClearColor(0f, 0f, 0f, 1f); 
        
        Renderer.clear(true, false);
        
        //3D
        Renderer.loadPerspective(100f, 500f);
        
        Renderer.DepthTest.enable();
        
        
        //BG
        Renderer.pushMatrix();
        float darkener = 1f;
        Renderer.setColor(darkener, darkener, darkener, 1f);
        
        
        bgShaker.shakingSpeed = 1f;
        bgShaker.yAmplitude = 4f; bgShaker.yOscill = 30f;
        bgShaker.applyTransformation();
        
        
        
        Renderer.translate(0f, 0f, -280f);
        
        Renderer.rotate(-40, 0, 1, 0);
        Renderer.rotate(-40, 0, 0, 1);
        Renderer.rotate(30, 1, 0, 1);
        
        Renderer.translate(tunnelOffset, 0f, 0f);
        //Renderer.translate(0f, 0f, 0f);
        
        //Tunnel
        Renderer.rotate(85, 1, 0, 0);
        
        Renderer.translate(0f, 2f * TUNNEL_SCALE, 0f);
        

        
        int factor = 4;
        boolean lightedTunnel = ((int)elapsed) == ((int)elapsed / factor) * factor;
        lightedTunnel = ((int)elapsed) % factor != 0;
        
        
        
        if (bgLightNextSwitch <= 0) {
            bgLightOn = !bgLightOn; if (bgLightPhase == 0 || bgLightPhase == 0) bgLightOn = true;
            bgLightPhase++; if (bgLightPhase >= bgLightDurations.length) bgLightPhase = 0;
            bgLightNextSwitch += bgLightDurations[bgLightPhase];
            if (bgLightPhase == 1 || bgLightPhase == 2 /*|| bgLightPhase == 3*/) bgShaker.shake();
            if (bgLightPhase == 2 ) { spawnSparkle(true); spawnSparkle(false); }
            if (bgLightPhase == 3 ) { spawnSparkle(true); }
        }
        lightedTunnel = bgLightOn;
        

        
        if (lightedTunnel) {
            if (Debug.useLightmap) {
                Renderer.enableLightMapMode(ModelManager.tunnel.mesh, 
                                            TextureManager.tunnel, 
                                            ModelManager.tunnel_lm.mesh, 
                                            TextureManager.tunnel_lm);
                
                renderBGMesh();
                
                //Disable multi-texture
                Renderer.disableLightMapMode();
                
            } else {   
                TextureManager.tunnel.bind();
                renderBGMesh();
            }
        } else {
            float c = 0.65f;
            Renderer.setColor(c, c, c, 1f);
            TextureManager.tunnel.bind();
            renderBGMesh();
            Renderer.resetColor();
        }
        
        Renderer.unbindVBOs();
        
        //Sparkle
        Renderer.Blending.enable();
        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        Renderer.DepthTest.setReadOnly(true);
        renderSparkle();
        Renderer.DepthTest.setReadOnly(false);
        Renderer.popMatrix();
        Renderer.resetColor();
        Renderer.Blending.resetMode();
        
        
        Renderer.clearDepthBuffer();
        Renderer.loadPerspective(1, 1000);
        
        Renderer.Lighting.enable();
        

        Renderer.pushMatrix();
        bgShaker.yAmplitude = 1f; bgShaker.yOscill = 30f;
        bgShaker.applyTransformation();
        //Ship
        
        //First flame
        Renderer.pushMatrix();
        {
            
            Renderer.translate(0f, 0f, -155f);
            
            Renderer.rotate(30, 1, 0, 0);
            Renderer.rotate(40 + 15 * (float)Math.cos(0.4f * elapsed), 0, 1, 0);
            //Renderer.rotate(5 * (float)Math.cos(2 * elapsed), 0, 0, 1);
            Renderer.rotate(90, 1, 0, 0);
            
            Renderer.unbindVBOs();
            Renderer.Lighting.disable();
            Renderer.Blending.enable();
            Renderer.Blending.setMode(BlendingMode.ADDITIVE);
            Renderer.DepthTest.disable();
            Renderer.setColor(1f, 0.6f, 0.5f, 1f);
            TextureManager.simpleBlast.bind();
            
            float scaler = MathUtil.getCyclicValue(0.5f, 1f, 10*elapsed);
            scaler += MathUtil.getCyclicValue(0.5f, 1.0f, 23*elapsed);
            scaler *= MathUtil.getCyclicValue(0.7f, 1f, 40*elapsed);
            scaler *= MathUtil.getCyclicValue(1f, 1.5f, 6*elapsed);
            //scaler += MathUtil.getCyclicValue(0.5f, 1f, 50*flameElapsed);
            
            scaler *= 9f;

            
            Renderer.translate(0, -12.5f, 0f);
            Renderer.scale(6.5f, scaler, 1f);
            Renderer.translate(/*-rotY * 0.007f*/ 0f, -0.5f, 0f);
            QuadVBO.drawQuadImmediate(flameUVs);
            
            Renderer.resetColor();
            Renderer.Blending.disable();
            Renderer.DepthTest.enable();
        }
        Renderer.popMatrix();
        
        /*
        if (lightedTunnel) {
            Light.setAmbient(1f, 1f, 1f, 1f);
            
        } else {
            Light.setAmbient(0.3f, 0.3f, 0.3f, 1f);
        }
        Light.apply();
        */
        
        if (lightedTunnel)
            Renderer.Lighting.enable();
        else Renderer.setColor(0.4f, 0.4f, 0.4f, 1f);
        
        Renderer.pushMatrix();
        float shipZoom = 1f;
        Renderer.translate(0f, 0f, -85f);
        
        
        Renderer.rotate(30, 1, 0, 0);
        Renderer.rotate(40 + 15 * (float)Math.cos(0.4f * elapsed), 0, 1, 0);
        Renderer.rotate(5 * (float)Math.cos(2 * elapsed), 0, 0, 1);
        //Renderer.rotate(90, 1, 0, 0);
        
        //Renderer.rotate(rotY, 0, 1, 0);
        
        
        Renderer.scale(shipZoom, shipZoom, shipZoom);
        TextureManager.ship.bind();  
        Renderer.rotate(180f, 0, 1, 0);
        Player.getMesh().render();
        
        Renderer.popMatrix();
        
        Renderer.popMatrix();
        //2D
        
        Renderer.unbindVBOs();
        Renderer.loadOrtho();
        
        Renderer.resetColor();
        if (lightedTunnel) Renderer.Lighting.disable();
        
        Renderer.DepthTest.disable();
        Renderer.Blending.enable();
        
        Renderer.Blending.resetMode();
        
        Renderer.Dithering.enable();
        titleWidget.render();
        Renderer.Dithering.disable();
        /*
        TextureManager.title.bind();
        
        float spriteWidth = Screen.ARENA_WIDTH * 0.95f;
        QuadVBO.drawQuad(50, Screen.ARENA_HEIGHT- 20, spriteWidth, spriteWidth * 0.3f);
        */
        
        //Press to play
        if (elapsed > MathUtil.PI * 0.25f) {
            
            pressToPlayTxt.setAlpha((float)(1 + Math.cos(4*elapsed)) * 0.5f);
            pressToPlayTxt.setPosition(FontTexture.ALIGN_CENTER, Screen.ARENA_HEIGHT * 0.25f);
            pressToPlayTxt.textSize = 10f;
            
            pressToPlayTxt.render();
            
        }
        
        if (!doIntro) {
            mainCanvas.render();
            if (lastSubState != null) lastSubState.renderUI();
        }
    }

    public void buttonLevelPressed(ChapterButton button) {
        if (!button.isEnabled()) { /*levelSelector.resetAnimations();*/ return;}
        AudioManager.playClickSound();
        ArenaState.currentLevel = button.levelInfo.levelId;
        switchToStateWithColor(ArenaState.instance, 1f, 1f, 1f, 1f, 0.5f);

    }
    
    public void buttonOptionsPressed() {
        AudioManager.playClickSound();
        switchToStateInstant(SettingsState.instance, 0.3f);
    }
    
    public void buttonCreditsPressed() {
        AudioManager.playClickSound();
        switchToStateInstant(CreditsState.instance, 0.3f);
    }
    
    public void buttonShopPressed() {
        AudioManager.playClickSound();
        switchToStateInstant(ShopState.instance, 0.3f);
    }
    
    @Override 
    public void onPostResume() {
        setUpLighting();
        AudioManager.playTitleMusic();
    }
    
    @Override
    public void onEnterImpl(boolean isResume) {
        
        setUpLighting();
        
        //Update levels
        for (int i = 0; i < chapterButtons.size(); i++) {
            chapterButtons.get(i).loadFromLevelInfo();
            chapterButtons.get(i).glowing = false;
        }
        
        if (!isResume)
        {
            levelSelector.removeAnimation(levelSelectorAlpha);
            levelSelector.removeAnimation(levelSelectorTranslate);
            levelSelector.setScale(1f);
            levelSelector.setGlobalAlpha(1f);
            levelSelector.setPosX(Screen.ARENA_WIDTH * 0.5f);
            
            resetMainCanvasAnimation();
            //Decide which button will glow
            int toGlow = 0;
            for (int i = 0; i < chapterButtons.size(); i++) {
                if (!chapterButtons.get(i).levelInfo.completedOnce) break;
                toGlow++;
            }
            if (toGlow >= chapterButtons.size()) toGlow = chapterButtons.size() - 1;
            chapterButtons.get(toGlow).glowing = true;
            
            if (previousState != SettingsState.instance
                && previousState != ShopState.instance
                && previousState != CreditsState.instance)
            {
                AudioManager.playTitleMusic();
            }
        } else {
            //onResume
            onPostResume();
        }
    }
    
    private static void setUpLighting() {
        Light.setPosition(0f, 0f, 1f, 0.0f);        
        Light.apply(); 
    }
    
    public void resetMainCanvasAnimation() {
        levelSelector.resetAnimations();
        
        sideMenu.display(0.5f);
        
        shopButtonJoin.init(shopButtonJoin.get());
        shopButtonJoin.setTarget(0, 1f, 0.5f);
    }
    
    public void readControls() {
        
        if (Controller.backFlag) {
            Platform.exitApplication();
            return;
        }
        
        if (elapsed < MathUtil.PI * 0.25f) return;
        
        if (doIntro && Controller.hasEvent) {
            //switchToStateWithColor(ArenaState.instance, 1f, 1f, 1f, 1f, 0f);
            AudioManager.playClickSound();
            doIntro = false;
            resetMainCanvasAnimation();
        }

    }

    @Override
    public void onLeaveBegan() {
        //levelSelector.addAnimation(levelSelectorAlpha.reset());
        levelSelector.addAnimation(levelSelectorTranslate.reset());
        
        sideMenu.hide();
        
        shopButtonJoin.init(shopButtonJoin.get());
        shopButtonJoin.setTarget(shopButtonJoinTarget, 1f);
    }
    
    @Override
    public void onLeaveCompleted() {

    }

    
}
