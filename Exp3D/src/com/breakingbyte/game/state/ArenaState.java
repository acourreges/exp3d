package com.breakingbyte.game.state;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.engine.Testing;
import com.breakingbyte.game.entity.group.EnemyBulletGroup;
import com.breakingbyte.game.entity.group.ExplosionGroup;
import com.breakingbyte.game.level.Level;
import com.breakingbyte.game.level.Level.LevelID;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.level.LevelStats;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.ui.dialog.DoubleChoiceDialog;
import com.breakingbyte.game.ui.dialog.UnlockLevelDialog;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.Vibration;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;
import com.breakingbyte.wrap.shared.Timer;

public class ArenaState extends State {

    public static ArenaState instance = new ArenaState();
    
    public State getInstance() { return instance; }
    
    public static LevelID currentLevel = LevelID.Level1;
    
    private boolean playerControlShip; 
    
    public SmoothJoin backgroundScrollSpeed;
    
    public LevelStats stats;
    
    //Time warp (bullet-time)
    public SmoothJoin timeWarpJoin;
    public static final float TIME_WARP_SLOWDOWN_FACTOR = 0.4f;
    public float timeWarpRemaining;
    
    public boolean onPause = false;
    public DoubleChoiceDialog pauseDialog;
    public DoubleChoiceDialog gameOverDialog;
    
    public boolean playerDied;
    public float playerDiedRemaining = 0;
    
    public UnlockLevelDialog unlockLevelDialog;
    
    public ArenaState() {
        backgroundScrollSpeed = new SmoothJoin();
        backgroundScrollSpeed.setInterpolator(Interpolator.SINUSOIDAL_SLOW_START);
        stats = new LevelStats();
        timeWarpJoin = new SmoothJoin();
        timeWarpJoin.setInterpolator(Interpolator.LINEAR_TIMED);
    }
    
    public static void init() {
        UI.initialize();
        instance.pauseDialog = DoubleChoiceDialog.generatePauseDialog();
        instance.gameOverDialog = DoubleChoiceDialog.generateGameOver();
        instance.unlockLevelDialog = new UnlockLevelDialog(); 
    }
    
    @Override
    public void onPostResume() {
        if (playerControlShip) startPause();
        playMusic();
    }
    
    @Override
    public void onEnterImpl(boolean isResume) {
        if (isResume) {
            Engine.currentLevel.loadAssets();
            Engine.currentLevel.setUpLighting();
            onPostResume();
        } else {
            playerControlShip = true;
            prepareLevel();
            clearSceneGraph();
            //try {Thread.sleep(3000);} catch (Exception e) {}
            //framesToSkip = 2;  
    
            timeWarpRemaining = 0f;
            timeWarpJoin.init(1f);
            timeWarpJoin.setTarget(1f, 0f);
            
            playMusic();
        }
        
    }
    
    public final void playMusic() {
        AudioManager.playMusic(Engine.currentLevel.getMusic()); 
    }
    
    public void clearSceneGraph() {
        Engine.layer_player.clear();
        Engine.layer_playerBullets.clear();
        Engine.layer_enemies.clear();
        Engine.layer_enemyBullets.clear();
        Engine.layer_bonus.clear();
        Engine.layer_explosions.clear();
        
        Engine.explosions = ExplosionGroup.newInstance();
        Engine.layer_explosions.addEntity(Engine.explosions);
        Engine.enemyBullets = EnemyBulletGroup.newInstance();
        Engine.layer_enemyBullets.addEntity(Engine.enemyBullets);
        
        Engine.player.registerInLayer();
        
    }
    
    public void prepareLevel() {
        Renderer.loadMatrixIdentity(); 
        //Renderer.switchToVBOMode();
        Engine.currentLevel = Level.getInstance(currentLevel);
        Engine.currentLevel.initSetup();
        
        Engine.player.reset();
        stats.reset(LevelContent.getLevelFromID(currentLevel));
        UI.reset();
        onPause = false;
        pauseDialog.resetPositions();
        gameOverDialog.resetPositions();
        
        unlockLevelDialog.reset();
        
        Vibration.stop();
        
        playerDied = false;
        playerDiedRemaining = 0f;
        
        Engine.player.posX = Engine.player.posTargetX = Screen.ARENA_WIDTH / 2f;
        Engine.player.posY = Engine.player.posTargetY = Screen.ARENA_HEIGHT / 2f;
    }

    @Override
    public void onLeaveCompleted() {
        Engine.currentLevel.cleanUp();
    }
    
    public static void setPlayerControlShip(boolean control) {
        instance.playerControlShip = control;
        Engine.player.setAutoPilot(!control);
        if (control) {
            Engine.player.setPositionTarget(Engine.player.posX, Engine.player.posY);
        }
        Engine.player.immuneToCollision = !control;
    }

    @Override
    public void renderImpl() {
        
        // ~~~~~~~~~~~~~~~~~~
        //      Render      ~
        // ~~~~~~~~~~~~~~~~~~
        
        // - Front to Back: opaque entities (saves fillrate)
        // - Back to Front: transparent bullets alpha-blended
        // - UI
        
        
        //Clear screen
        Renderer.clear(true, false);
        
        Renderer.DepthTest.enable();
        Renderer.Lighting.disable();
        
        // ~~~~~~~~~ Level background ~~~~~~~~~
        Engine.currentLevel.render();
        
        
        // ~~~~~~~~~ Enemies (3D) ~~~~~~~~~
        Renderer.loadOrtho();
        Renderer.Blending.disable();
        //Renderer.switchToVBOMode();
        
        Renderer.Lighting.enable();
        {
            Engine.layer_enemies.render();
        }  
        
        Renderer.Lighting.enable();
        {
            Engine.layer_player.render();
        }
        
        Renderer.loadOrtho();

        // ~~~~~~~~~ Bullets (2D) ~~~~~~~~~
        Renderer.DepthTest.disable();
        Renderer.Lighting.disable();

        Renderer.Blending.enable();
        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        {
            Renderer.unbindVBOs();
            Engine.layer_playerBullets.render();
        }
        
        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        Renderer.setColor(1f,1f,1f,1);
        {
            Renderer.unbindVBOs();
            Engine.layer_enemyBullets.render();
        }
        Renderer.resetColor();

        
        //Player shield
        Renderer.Blending.resetMode();
        
        Engine.player.renderShield();
        
        
        //Bonus
        {
            Renderer.unbindVBOs();
            Engine.layer_bonus.render();
        }
        
        
        // ~~~~~~~~~ Explosions (2D) ~~~~~~~~~
        
        Renderer.Blending.resetMode();
        Renderer.Blending.setMode(BlendingMode.EXPLOSION);
        {
            Renderer.unbindVBOs();
            Engine.layer_explosions.render();
        }
        
        if (Debug.drawBoundingBoxes) {
            Renderer.Blending.resetMode();
        
            Engine.layer_player.renderBoundingBox();
            Engine.layer_enemies.renderBoundingBox();
            Engine.layer_playerBullets.renderBoundingBox();
            Engine.layer_enemyBullets.renderBoundingBox();
            Engine.layer_bonus.renderBoundingBox();
        }

        // ~~~~~~~~~ UI ~~~~~~~~~
        {
            UI.render();
        }
        
        pauseDialog.render();
        gameOverDialog.render();
        unlockLevelDialog.render();
        
        Renderer.Blending.resetMode();
        
        if (Debug.devMode) {
            //TitleState.render();
            Testing.test();
            return;
        }
        
        //Renderer.checkGLErrors();
        
    }

    @Override
    public void updateImpl() {
        
        EngineState.GeneralStats.duration += Timer.delta;
        
        if (Debug.devMode) {
            //TitleState.render();
            Testing.update();
            return;
        }
        
        readControls();
        pauseDialog.update();
        gameOverDialog.update();
        unlockLevelDialog.update();
        
        if (playerDied) {
            playerDiedRemaining -= Timer.delta;
            if (playerDiedRemaining < 0) {
                playerDied = false;
                startGameOver();
            }
        }
        
        if (onPause) return;
        
        backgroundScrollSpeed.update();
        
        stats.update();
        
        if (timeWarpRemaining > 0) {
            timeWarpRemaining -= Timer.delta;
            if (timeWarpRemaining <= 0) endTimeWarp();
        }
        timeWarpJoin.update();
        
        
        float timeBackup = Timer.delta;
        Timer.delta *= timeWarpJoin.get();
        
        {   //Affected by bullet-time
            Engine.currentLevel.update();
            Engine.layer_player.update();
            Engine.layer_playerBullets.update();
            Engine.layer_enemies.update();
            Engine.layer_enemyBullets.update();
        }
        Timer.delta = timeBackup;
        
        if (Debug.performCollisionDetection) {
            Engine.layer_playerBullets.collisionWith(Engine.layer_enemies);
            Engine.layer_enemyBullets.collisionWith(Engine.layer_player);
            Engine.layer_player.collisionWith(Engine.layer_enemies);
            
            //Special Hack for the bonuses: always do collision detection
            boolean backup = Engine.player.immuneToCollision;
            Engine.player.immuneToCollision = false;
            Engine.layer_bonus.collisionWith(Engine.layer_player);
            Engine.player.immuneToCollision = backup;
        }
        
        Engine.layer_bonus.update();
        Engine.layer_explosions.update(); // Explosions can be spawn in the collision-detection step
        
        UI.update();
    }
    
    public void onPlayerDied() {
        setPlayerControlShip(false);
        playerDied = true;
        playerDiedRemaining = 1.5f;
        AudioManager.playMusic(SoundId.BGM_GAME_OVER);
    }
    
    public void startTimeWarp(float duration) {
        timeWarpRemaining = duration;
        timeWarpJoin.init(timeWarpJoin.get());
        timeWarpJoin.setTarget(TIME_WARP_SLOWDOWN_FACTOR, 0.7f);
    }
    
    public void endTimeWarp() {
        timeWarpJoin.init(timeWarpJoin.get());
        timeWarpJoin.setTarget(1f, 1f);
        timeWarpRemaining = 0f;
    }
    
    public void startPause() {
        onPause = true;
        pauseDialog.startAnimation();
        playerControlShip = false;
    }
    
    public void endPause() {
        onPause = false;
        pauseDialog.endAnimation();
        playerControlShip = true;
    }
    
    public void startGameOver() {
        onPause = true;
        gameOverDialog.startAnimation();
    }
    
    
    public void displayUnlockLevelDialog() {
        if (EngineState.isFullVersion || LevelContent.getLevelFromID(currentLevel).bought) return;
        onPause = true;
        unlockLevelDialog.setUpFromLevel(LevelContent.getLevelFromID(ArenaState.currentLevel));
        unlockLevelDialog.show();
        playerControlShip = false;
    }
    
    public void hideUnlockLevelDialogIfNecessary() {
        if (!unlockLevelDialog.isOpen()) return;
        unlockLevelDialog.hide();
        onPause = false;
        playerControlShip = true;
    }
    
    public void goBackToTitle() {
        switchToStateWithColor(TitleState.instance, 0f, 0f, 0f, 1f, 0f);
    }
    
    public boolean specialInProgress = false;
    public boolean pauseInProgress = false;
    public void readControls() {

        if (transitionInProgress) return;
        
        if (Controller.backFlag) {
            if (UI.levelClearedDialog.isOpen()) {}
            else if (!onPause) ArenaState.instance.startPause();
            else if (onPause && pauseDialog.isOpen) ArenaState.instance.endPause();
            else {
                //goBackToTitle();
            }
            return;
        }
        
        if (Controller.hasEvent) {
            //Controller.isTouched = false;
            
            if (UI.isPauseButtonVisible()) {
                //Handles Pause
                boolean isInsidePause = Controller.touchX > Screen.ARENA_WIDTH - 20f && Controller.touchY > Screen.ARENA_HEIGHT - 20f;
                
                if (!isInsidePause) pauseInProgress = false;
                
                if (!onPause && Controller.downFlag && isInsidePause) {
                    pauseInProgress = true;
                    AudioManager.playClickSound(); 
                    startPause();
                }
            }
            
            if (playerControlShip) {
            
                Engine.player.autoFire = true;

                //Handles Special weapon
                
                final float arenaWidth = 25f;
                boolean isInSpecial = Controller.touchX <= arenaWidth && Controller.touchY <= arenaWidth;
                
                if (!isInSpecial) specialInProgress = false;
                
                if (Controller.downFlag && isInSpecial) {
                    //Touch began on the special icon
                    Engine.player.fireHolyBlast();
                    specialInProgress = true;
                }
                
                if (!specialInProgress) Engine.player.setPositionTarget(Controller.touchX, Controller.touchY + 20);
                
                if (Controller.upFlag) specialInProgress = false;
            }
            UI.handleInput(Controller.touchX, Controller.touchY, Controller.touchAction);
            if (onPause) {
                pauseDialog.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
                gameOverDialog.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
                unlockLevelDialog.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
            }
        }
        
        if (Controller.hasSpecialEvent) {
            //Controller.isSpecialTouched = false;
            Engine.player.fireHolyBlast();
            //startTimeWarp(4f);
        }
    }

}
