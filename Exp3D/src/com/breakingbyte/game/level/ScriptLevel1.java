package com.breakingbyte.game.level;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.content.ShopContent;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.bonus.Bonus.BonusType;
import com.breakingbyte.game.entity.enemy.Astrol;
import com.breakingbyte.game.entity.enemy.Byrol;
import com.breakingbyte.game.entity.enemy.Crystol;
import com.breakingbyte.game.entity.enemy.boss.BossA;
import com.breakingbyte.game.entity.fire.Fire.Type;
import com.breakingbyte.game.entity.fire.FireCyclic;
import com.breakingbyte.game.entity.fire.FireSpiral;
import com.breakingbyte.game.entity.group.AstrolRing;
import com.breakingbyte.game.entity.move.LocalMoveDefault;
import com.breakingbyte.game.entity.move.LocalMoveFacePlayer;
import com.breakingbyte.game.entity.move.LocalMoveSinusoidalRotation;
import com.breakingbyte.game.entity.move.LocalMoveZoomRotation;
import com.breakingbyte.game.entity.move.WorldMoveBezierQuadratic;
import com.breakingbyte.game.entity.move.WorldMoveDefault;
import com.breakingbyte.game.entity.move.WorldMoveSinusoidal;
import com.breakingbyte.game.entity.move.WorldMoveSmooth;
import com.breakingbyte.game.entity.particle.Explosion;
import com.breakingbyte.game.entity.particle.ExplosionFireRing;
import com.breakingbyte.game.entity.particle.ShotA;
import com.breakingbyte.game.entity.particle.ShotB;
import com.breakingbyte.game.entity.particle.ShotLong;
import com.breakingbyte.game.script.ScriptInterpreter;
import com.breakingbyte.game.script.ScriptInterpreter.Script;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.state.ForwarderState;
import com.breakingbyte.game.state.ForwarderState.DialogEnum;
import com.breakingbyte.game.state.TitleState;
import com.breakingbyte.game.ui.TutorialMessage.TUTO_STRING;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Timer;


public class ScriptLevel1 implements Script {

    public static ScriptLevel1 instance = new ScriptLevel1();
    
    
    public void setDefaultValues() {
        
        ShotA.settings.width = ShotA.settings.height = 4f;
        ShotA.settings.zoomTarget = 1.2f;
        ShotA.settings.attackPower = 100;
        
        ShotB.settings.width = ShotB.settings.height = 3f;
        ShotB.settings.zoomTarget = 3f;
        ShotB.settings.attackPower = 100;
        
        ShotLong.settings.width = 5f;
        ShotLong.settings.height = 10f;
        ShotLong.settings.zoomTarget = 2.5f;
        ShotLong.settings.attackPower = 100;
    }
    
    @SuppressWarnings("unused")
    public void runScript(ScriptInterpreter script) {
        
        //HACK 
        if (false && script.step == 0) {
            script.step = 59;
            ArenaState.setPlayerControlShip(true);
            UI.displayLifeBar();
            setDefaultValues();
        }
        
        final float H = Screen.ARENA_HEIGHT;
        final float W = Screen.ARENA_WIDTH;
        
        final int s = script.step;
        int i = -1;
        Entity e;
        
        SmoothJoin bgSpeed = ArenaState.instance.backgroundScrollSpeed;
        
        //INTRO
        
        // We don't use a switch case here (which would be more efficient than checking each step). 
        // It's because we want to be able to take some steps of the script and re-order them or move them around. 
        // This way, when designing the level, we don't have to maintain the value of the switch case by hand.
        // The performance hit was too low to push the move to a switch case. 
        
        i++;if(s==i){
            setDefaultValues();
            Explosion.setSlowExplosions(false);
            ArenaState.setPlayerControlShip(false);
            script.wait(1f);
            e = Engine.player;
            e.posX = W; e.posY = -20f;
        }

        i++;if(s==i){

            e = Engine.player; e.surviveMoveEnd = true; e.scale = 2.3f;
            WorldMoveSmooth.applyTo(e).speed(1.2f).pt0(W * 1.f, -20).pt1( W*0.75f, H*0.4f);
            LocalMoveSinusoidalRotation.applyTo(e).speed(2f).amplitude(15f, 30f, 10f);
            UI.displayLevelTitle(true);
            script.wait(3.25f);
        }
        
        i++;if(s==i){
            float duration = 1.1f;
            e = Engine.player;
            WorldMoveSmooth.applyTo(e).speed(duration).pt0(e.posX, e.posY).pt1( W*0.5f, H*0.7f).interp(Interpolator.SINUSOIDAL_SLOW_START);
            LocalMoveZoomRotation.applyTo(e).setZoomRot(duration, e.scale, 1f, e.rotY - 1 * 360f, 0);
            UI.displayLevelTitle(false);
            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
            ex.setup(0.4f, 40f, 100f, 0.75f, 0f, 0.0f);
            ex.setColor(0.65f,0.65f,1f);
            script.wait(duration);
        }
        
        i++;if(s==i){
            float duration = 1.2f;
            UI.displayShipFocuser(true);
            e = Engine.player;
            WorldMoveSmooth.applyTo(e).speed(duration).pt0(e.posX, e.posY).pt1( W*0.5f, H*0.45f).interp(Interpolator.SINUSOIDAL_SLOW_START);
            LocalMoveDefault.applyTo(e);
            UI.displayLifeBar();
            Engine.player.depleteSpecialWeapon();
            Engine.player.autoFire = true;
            script.wait(duration);
            if (EngineState.doTutorial) Engine.player.blockSpecialWeaponReload = true;
        }
        
        i++;if(s==i){
            UI.displayShipFocuser(false);
            e = Engine.player;
            WorldMoveDefault.applyTo(e);
            ArenaState.setPlayerControlShip(true);
        }
        //END INTRO
        
        
        
        i++;if(s==i){ // 2 easy targets
            e = Astrol.spawn(); e.posX = -10; e.posY = H * 1.1f; e.setDirAngle(-40); e.moveSpeed = 17f; e.clearWhenLeaveScreen = true; e.carryOrb = true;
            e = Astrol.spawn(); e.posX = -15; e.posY = H * 0.8f; e.setDirAngle(-40); e.moveSpeed = 15f; e.clearWhenLeaveScreen = true; e.carryOrb = true;
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i){
            if (EngineState.doTutorial) {
                UI.tutorialMessage.prepare(TUTO_STRING.GRAB_ORB);
                UI.tutorialMessage.startAppearAnimation();
            }
        }
        
        i++;if(s==i){ //Some easy Astrols, slowly going down the screen
            e = Astrol.spawn(); e.posX = 10; e.posY = H + 10; e.movY = -1; e.moveSpeed = 15f; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = 80; e.posY = H + 15; e.movY = -1; e.moveSpeed = 10f; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = 60; e.posY = H + 20; e.movY = -1; e.moveSpeed =  5f; e.clearWhenLeaveScreen = true; e.carryOrb = true;
            e = Astrol.spawn(); e.posX = 15; e.posY = H + 60; e.movY = -1; e.moveSpeed = 15f; e.clearWhenLeaveScreen = true;
            //e = Astrol.spawn(); e.posX = 30; e.posY = H + 80; e.movY = -1; e.moveSpeed = 15f; e.clearWhenLeaveScreen = true;
            script.wait(4f);
        }
        
        i++;if(s==i){
            UI.tutorialMessage.startDisappearAnimation();
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }

        // Sinusoidal astrols
        i++;if(s==i){
            script.repeatThisStep(6);
            e = Astrol.spawn(); e.carryOrb = script.getRemainingRepeatSteps() == 3;
            WorldMoveSinusoidal.applyTo(e).speed(0.15f).vertical(true).nbOsc(2).pt0(5, H+10).pt1(W-5, -5);
            script.wait(0.5f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i){
            script.repeatThisStep(6);
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.15f).vertical(true).nbOsc(2).pt0(W-5, H+10).pt1(5, -5);
            script.wait(0.5f);
        }
        
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i){ //A-B-A-B
            e = Astrol.spawn();
            e.carryPowerUp(BonusType.TIME_WARP); 
            WorldMoveBezierQuadratic.applyTo(e).speed(0.25f).pt0(-10, H/2).pt1(W/2, H*1.3f).pt2(W+10, H/2);
            script.wait(0.6f);
        }

        
        i++;if(s==i){
            e = Byrol.spawn();
            e.carryOrb = true;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.25f).pt0(-10, H/2).pt1(W/2, H*1.3f).pt2(W+10, H/2);
            FireCyclic.applyTo(e).delay(1f).shoot(4, 0.3f, 1f).speed(30f).offsets(0f, 4f).bullet(Type.SHOTB);
            script.wait(0.6f);
        }
        
        i++;if(s==i){
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.25f).pt0(-10, H/2).pt1(W/2, H*1.3f).pt2(W+10, H/2);
            script.wait(0.6f);
        }
        
        i++;if(s==i){
            e = Byrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.25f).pt0(-10, H/2).pt1(W/2, H*1.3f).pt2(W+10, H/2);
            FireCyclic.applyTo(e).delay(1f).shoot(4, 0.3f, 1f).speed(30f).offsets(0f, 4f).bullet(Type.SHOTB);
            //script.wait(0.6f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i){
            if (EngineState.doTutorial) {
                UI.tutorialMessage.prepare(TUTO_STRING.FIRE_SPECIAL);
                UI.tutorialMessage.startAppearAnimation();
                Engine.player.blockSpecialWeaponReload = false;
                Engine.player.fillSpecialWeapon();
            }
        }

        // ][ attack of astrols
        i++;if(s==i){
            script.repeatThisStep(15);
            if (EngineState.doTutorial && (Engine.player.specialWeaponLastShot < 0.8f || script.getRemainingRepeatSteps() == 5) ) {
                UI.tutorialMessage.startDisappearAnimation();
            }
            if (EngineState.doTutorial && script.getRemainingRepeatSteps() == 3) {
                UI.tutorialMessage.prepare(TUTO_STRING.RELOAD_SPECIAL);
                UI.tutorialMessage.startAppearAnimation();
            }
            boolean carryOrb = script.getRemainingRepeatSteps() < 7 && (script.getRemainingRepeatSteps() % 2 == 0);
            e = Astrol.spawn(); e.carryOrb = carryOrb;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(W/2 - 10, H+10).pt1(W/2, 0).pt2(-10, H-10);
            e = Astrol.spawn(); e.carryOrb = carryOrb;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(W/2 + 10, H+10).pt1(W/2, 0).pt2(W+10, H-10);
            script.wait(0.45f);
        }

        i++;if(s==i){
            if (EngineState.doTutorial) script.wait(3f); 
        }
        
        i++;if(s==i){
            if (EngineState.doTutorial) UI.tutorialMessage.startDisappearAnimation();
            script.waitAllEnemiesGone();
        }
        
        // U attack of astrols + protected Byrol
        i++;if(s==i){
            e = Byrol.spawn(); e.carryOrb = true;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.12f).pt0(W/2-20, H+10).pt1(W/2, H/2).pt2(W/2+20, H+10);
            FireCyclic.applyTo(e).delay(0.7f).shoot(2, 0.2f, 1f).speed(60f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(0.8f).shoot(2, 0.2f, 1f).speed(60f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
        }
        
        i++;if(s==i){
            script.repeatThisStep(12);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(-30, H+10).pt1(W/2, 0).pt2(W+30, H+10);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.3f).pt0(W+30, H+30).pt1(W/2, 30).pt2(-30, H+30);
            script.wait(0.5f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { //Astrol ring
            script.repeatThisStep(1);
            AstrolRing astrolC = AstrolRing.newInstance();         
            astrolC.posX = 50; astrolC.posY = Screen.ARENA_HEIGHT/2;
            astrolC.populateChildren(10/*15*/);
            astrolC.registerInLayer();
            script.wait(3f);
        }
        
        // Astrol scissors
        i++;if(s==i){
            script.waitAllEnemiesGone();
            script.c = 0;
            //bgSpeed.init(bgSpeed.get());
            //bgSpeed.setTarget(30, 1.5f);
            script.wait(0.6f);
        }
        
        i++;if(s==i){
            script.repeatThisStep(28);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.8f).pt0(-30, H+10).pt1(2*script.c, H).pt2(2*script.c, -10);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.8f).pt0(W+30, H+10).pt1(W-2*script.c, H).pt2(W-2*script.c, -10);
            script.c++;
            script.wait(0.1f);
        }
        
        i++;if(s==i){
            e = Byrol.spawn(); e.posX = 20; e.posY = -10; e.movY = 1; e.moveSpeed = 30f; e.clearWhenLeaveScreen = true;
            e.carryOrb = true;
            FireCyclic.applyTo(e).delay(1.3f).shoot(2, 0.2f, 1.5f).speed(60f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(1.4f).shoot(2, 0.2f, 1.5f).speed(60f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            e = Byrol.spawn(); e.posX = 80; e.posY = -10; e.movY = 1; e.moveSpeed = 30f; e.clearWhenLeaveScreen = true;
            e.carryOrb = true;
            FireCyclic.applyTo(e).delay(1.3f).shoot(2, 0.2f, 1.5f).speed(60f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(1.4f).shoot(2, 0.2f, 1.5f).speed(60f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        // Some crystol
        i++;if(s==i){
            //bgSpeed.init(bgSpeed.get());
            //bgSpeed.setTarget(20, 1.5f);
            e = Crystol.spawn(); e.surviveMoveEnd = true; 
            e.carryPowerUp(BonusType.HELLFIRE);
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(-30, H/2).pt1(20, H).pt2(20, 140);
            FireSpiral.applyTo(e).branches(4).angleSpeed(60f).delay(1.0f).shoot(10, 0.07f, 1f).speed(60f).offsets(0, 4f).bullet(Type.SHOTA);
            e = Crystol.spawn(); e.surviveMoveEnd = true;
            e.carryOrb = true;
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(W+30, H/2).pt1(80, H).pt2(80, 140);
            FireSpiral.applyTo(e).branches(4).angleSpeed(60f).delay(1.0f).shoot(8, 0.07f, 1f).speed(60f).offsets(0, 4f).bullet(Type.SHOTA);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i){ //First snake of Byrols
            script.repeatThisStep(10);
            e = Byrol.spawn(); e.carryOrb = (script.getRemainingRepeatSteps() == 8 || script.getRemainingRepeatSteps() == 6);
            LocalMoveDefault.applyTo(e); e.rotationSpeed = 1f; e.rotZ = -20f; e.rotX = -50f; e.rotDirX = 40f; e.rotY = -10f; e.rotDirY = 10f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.4f).pt0(W, H+10).pt1(2*W/3, H/5).pt2(W/3, H+10);
            FireCyclic.applyTo(e).delay(1.0f).shoot(2, 0.2f, 9f).speed(60f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);//.reshapeBullets(7f, 7f, 100f);
            FireCyclic.applyTo(e).delay(1.1f).shoot(2, 0.2f, 9f).speed(60f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);

            script.wait(0.3f);
        }
        

        i++;if(s==i){
            script.waitAllEnemiesGone();
            script.b = false;
        }
        
        i++;if(s==i){
            script.repeatThisStep(10);
            script.b = !script.b;
            if (script.b) {
                e = Byrol.spawn(); e.lifeRemaining = 1000;
                if (script.getRemainingRepeatSteps() == 10) e.carryOrb = true;
                LocalMoveDefault.applyTo(e); e.rotationSpeed = 1f; e.rotZ = 20f; e.rotX = -50f; e.rotDirX = 40f; e.rotY = 10f; e.rotDirY = -10f;
                WorldMoveBezierQuadratic.applyTo(e).speed(0.4f).pt0(0, H+10).pt1(W/3, H/5).pt2(2*W/3, H+10);
                FireCyclic.applyTo(e).delay(0.4f).shoot(2, 0.2f, 1f).speed(60f).offsets(FireCyclic.BYROL_LATERAL, 6f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(0.5f).shoot(2, 0.2f, 1f).speed(60f).offsets(-FireCyclic.BYROL_LATERAL, 6f).bullet(Type.SHOTB);
            } else {
                e = Byrol.spawn(); e.lifeRemaining = 1000;
                LocalMoveDefault.applyTo(e); e.rotationSpeed = 1f; e.rotZ = -20f; e.rotX = -50f; e.rotDirX = 40f; e.rotY = -10f; e.rotDirY = 10f;
                WorldMoveBezierQuadratic.applyTo(e).speed(0.4f).pt0(W, H+10).pt1(2*W/3, H/5).pt2(W/3, H+10);
                FireCyclic.applyTo(e).delay(0.8f).shoot(2, 0.2f, 9f).speed(60f).offsets(FireCyclic.BYROL_LATERAL, 6f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(0.9f).shoot(2, 0.2f, 9f).speed(60f).offsets(-FireCyclic.BYROL_LATERAL, 6f).bullet(Type.SHOTB);
            }
            script.wait(0.4f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        
        //Astrols again

        
        i++;if(s==i){
            script.repeatThisStep(5);
            e = Astrol.spawn();
            if (script.getRemainingRepeatSteps() % 2 == 0) e.carryOrb = true;
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.2f, H+10).pt1(W * 0.4f, -5);
            script.wait(0.15f);
        }
        
        i++;if(s==i){
            script.wait(0.5f);
        }
        
        i++;if(s==i){
            script.repeatThisStep(5);
            e = Astrol.spawn();
            if (script.getRemainingRepeatSteps() % 2 == 0) e.carryOrb = true;
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.6f, H+10).pt1(W * 0.8f, -5);
            script.wait(0.15f);
        }
        
        i++;if(s==i){
            script.wait(1.8f);
        }
        
        i++;if(s==i){
            script.repeatThisStep(5);
            e = Astrol.spawn();
            if (script.getRemainingRepeatSteps() == 5) e.carryOrb = true;
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.2f, H+10).pt1(W * 0.4f, -5);
            script.wait(0.15f);
        }
        
        i++;if(s==i){
            script.wait(0.5f);
        }
        
        i++;if(s==i){
            script.repeatThisStep(5);
            e = Astrol.spawn();
            if (script.getRemainingRepeatSteps() == 5) e.carryOrb = true;
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.6f, H+10).pt1(W * 0.8f, -5);
            script.wait(0.15f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        //   o o
        // o o o o
        i++;if(s==i){
            script.repeatThisStep(3);
            e = Astrol.spawn(); e.carryOrb = script.getRemainingRepeatSteps() == 2;
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.2f, H+10).pt1(W * 0.4f, -5);
            
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.0f, H+10).pt1(W * 0.2f, -5);
            
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.6f, H+10).pt1(W * 0.8f, -5);
            
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.8f, H+10).pt1(W * 1f, -5);
            
            //e = Astrol.spawn();
            //.applyTo(e, 0.4f, true, 3, W * 0.2f, H+10, W * 0.4f, -5);
            
            script.wait(0.15f);
        }
        
        i++;if(s==i){
            script.repeatThisStep(10);
            e = Astrol.spawn(); e.carryOrb = script.getRemainingRepeatSteps() == 9 || script.getRemainingRepeatSteps() == 3; 
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.2f, H+10).pt1(W * 0.4f, -5);
            
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.4f).vertical(true).nbOsc(3).pt0(W * 0.6f, H+10).pt1(W * 0.8f, -5);
            
            script.wait(0.15f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        //Fix shoot - Byrol
        i++;if(s==i){
            for (int j = 0; j < 3; j++) {
                e = Byrol.spawn(); e.surviveMoveEnd = true;
                e.carryOrb = true;
                LocalMoveSinusoidalRotation.applyTo(e).speed(2f).amplitude(j==0?5f:-5f, j==0?20f:-20f, 0);
                if (j==0) WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(W*0.3f, H+10).pt1(W/2, H+10).pt2(W/2, H * 0.8f);
                if (j==1) WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(W*0.3f, H+10).pt1(W/2, H+10).pt2(W*0.2f, H * 0.92f);
                if (j==2) WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(W*0.3f, H+10).pt1(W/2, H+10).pt2(W*0.8f, H * 0.92f);
                FireCyclic.applyTo(e).delay(j==0?1.2f:2f).shoot(4, 0.07f, 2f).speed(70f).angle(-88f, false).offsets( 6f, 3f).bullet(Type.SHOTB); ////
                FireCyclic.applyTo(e).delay(j==0?1.2f:2f).shoot(4, 0.07f, 2f).speed(70f).angle(-92f, false).offsets(-6f, 3f).bullet(Type.SHOTB);
                //FireCyclic.applyTo(e, Type.SHOTB, j==0?1.2f:2f/*delay*/, 4/*nb*/, 0.07f/*freq*/, 2f/*salveRep*/, 70f/*bulletSpeed*/, -88f, false).setOffsetLateral(6f).setOffsetFront(3f);
                //FireCyclic.applyTo(e, Type.SHOTB, j==0?1.2f:2f/*delay*/, 4/*nb*/, 0.07f/*freq*/, 2f/*salveRep*/, 70f/*bulletSpeed*/, -92f, false).setOffsetLateral(-6f).setOffsetFront(3f);
            }
            script.c = 0;
        }
        
        //Protection with Astrol
        i++;if(s==i){
            script.c++;
            script.repeatThisStep(30);
            //e = Astrol.spawn(); e.posX = W+10; e.posY = H * 0.73f; e.movX = -1; e.moveSpeed = 90f; e.clearWhenLeaveScreen = true;
            if (script.c > 0) { e = Astrol.spawn(); e.posX = -10; e.posY = H * 0.65f; e.movX = 1; e.moveSpeed = 90f; e.clearWhenLeaveScreen = true; }
            if (script.c > 8) { e = Astrol.spawn(); e.posX = W+10; e.posY = H * 0.57f; e.movX = -1; e.moveSpeed = 90f; e.clearWhenLeaveScreen = true;}
            script.wait(0.18f);
        }

        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        //Time for the boss!
        
        //Boss warning
        i++;if(s==i){
            UI.displayBossWarning(true);
            AudioManager.playSound(SoundId.ALARM);
        }
        
        i++;if(s==i){
            script.wait(4.5f);
        }
        
        i++;if(s==i){
            UI.displayBossWarning(false);
            bgSpeed.init(bgSpeed.get());
            bgSpeed.setTarget(15, 2f);
        }
        
        // Boss appearance
        i++;if(s==i){
            BossA boss = BossA.spawn(); 
            boss.surviveMoveEnd = true;
            boss.immuneToCollision = true;
            boss.scriptInterpreter.setScript(BossScript.instance);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { //Boss dead - Explosions
            
            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=BossA.lastAliveX; ex.posY = BossA.lastAliveY;
            ex.setup(1.5f, 20f, 500f, 1f, 0f, 0.5f);
            ex.setColor(1f, 1f, 198f/255f,  1f, 149f/255f, 38f/255f);
            
            Explosion.setSlowExplosions(false);
            int total = 6;
            e = Explosion.spawn(); e.setDimension(90, 90); e.posX = BossA.lastAliveX; e.posY = BossA.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossA.lastAliveX + 14 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossA.lastAliveY + 14 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossA.lastAliveX + 5 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossA.lastAliveY + 5 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(200, 200); 
            e.posX = BossA.lastAliveX;
            e.posY = BossA.lastAliveY;
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }        
        
        i++;if(s==i) { 
            int total = 6;
            Explosion.setSlowExplosions(true);
            e = Explosion.spawn(); e.setDimension(120, 120); e.posX = BossA.lastAliveX; e.posY = BossA.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(120, 120); 
                e.posX = BossA.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossA.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(220, 220); 
            e.posX = BossA.lastAliveX;
            e.posY = BossA.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        
        
        i++;if(s==i) { 
            int total = 6;
            e = Explosion.spawn(); e.setDimension(150, 150); e.posX = BossA.lastAliveX; e.posY = BossA.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(150, 150); 
                e.posX = BossA.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossA.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(250, 250); 
            e.posX = BossA.lastAliveX;
            e.posY = BossA.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) {
            UI.hideLifeBar();
            bgSpeed.init(bgSpeed.get());
            bgSpeed.setTarget(30, 2f);
            script.wait(1f);
        }
        
        i++;if(s==i) {
            AudioManager.playMusic(SoundId.BGM_S_B_S_W);
            UI.displayLevelClearedDialog();
            script.wait(0.5f);
        }
        
        i++;if(s==i) {
            ArenaState.setPlayerControlShip(false);
            Engine.player.autoFire = false;
            e = Engine.player; e.surviveMoveEnd = true;
            WorldMoveSmooth.applyTo(e).speed(3f).pt0(e.posX, e.posY).pt1(W*0.5f, H*0.3f).interp(Interpolator.SINUSOIDAL_SLOW_START);
        }
        
        i++;if(s==i) { //End-level dialog
            if (UI.levelClearedDialog.isOpen()) script.stayInThisStep();
        }
        
        i++;if(s==i) {
            script.wait(0.3f);
        }
        
        i++;if(s==i) {
            e = Engine.player;
            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
            ex.setup(0.45f, 20f, 70f, 0.85f, 0f, 0.0f);
            ex.setColor(0.65f,0.65f,1f);
            script.wait(0.2f);
        }
        
        i++;if(s==i) {
            e = Engine.player; e.surviveMoveEnd = true;
            float duration = 2.6f;
            WorldMoveSmooth.applyTo(e).speed(duration).pt0(e.posX, e.posY).pt1( W*0.5f, H*1.5f).interp(Interpolator.SINUSOIDAL_SLOW_START);
            LocalMoveZoomRotation.applyTo(e).setZoomRot(duration, e.scale, 4f, e.rotY, e.rotY - 2.0f * 360f);
        }
        
        i++;if(s==i) {
            script.wait(1.4f);
        }
        
        i++;if(s==i) {
            if (ShopContent.primaryCannonArticle.currentLevel <= 1) {
                ShopContent.primaryCannonArticle.levelUp();
                ForwarderState.instance.setNextState(TitleState.instance, 0, 0, 0, 1f, DialogEnum.EXTRA_CANNON);
                ArenaState.instance.switchToStateWithColor(ForwarderState.instance, 0f, 0f, 0f, 1f, 0f);
            } else ArenaState.instance.goBackToTitle();
        }

        
        //----- End of the script -----
        i++;if(s==i) {
            script.stayInThisStep();
            /*
            script.waitAllEnemiesGone();
            script.step = -1; //loop
            */
        }
        
    }
    
    private static class BossScript implements Script {

        private static BossScript instance = new BossScript();
        
        @Override
        public void runScript(ScriptInterpreter script) {
            
            //HACK 
            //if (false && script.step == 0) script.step = 41;
            
            final float H = Screen.ARENA_HEIGHT;
            final float W = Screen.ARENA_WIDTH;
            
            final int s = script.step;
            int i = -1;
            BossA e = (BossA) script.entity;
            
            //Intro
            i++;if(s==i) {
                e.rotDirY = 1; e.rotationSpeed = -190.0f;
                WorldMoveSmooth.applyTo(e).speed(1.2f).pt0(-30, H+30).pt1(W/2f, H*0.70f);
                script.wait(2.5f);
            }
            
            i++;if(s==i) {
                e.rotationSpeed += Timer.delta * 70f;
                final float minSpeed = -40f;
                if (e.rotationSpeed > minSpeed) e.rotationSpeed = minSpeed;
                if (e.rotationSpeed < minSpeed) script.step--;
                else if (((int)Math.abs(e.rotY) % 360) > 5f) script.step--;
                else {
                    //move to next step
                    e.rotationSpeed = 0f;
                    e.rotDirY = 0;
                    e.rotDirZ = 1;
                    e.timer = 0f;
                }
            }
            
            i++;if(s==i) {
                //Fight begins
                e.immuneToCollision = false;
                UI.displayBossLifeBar(true);
                WorldMoveSmooth.applyTo(e).speed(1.5f).pt0(e.posX, e.posY).pt1(W/2 + W*0.4f, H*0.8f);
                ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                ex.setup(1.3f, 50f, 450f, 0.8f, 0.0f, 0.0f);
                ex.setColor(1f, 0, 0,  0.8f, 0.8f, 1f);
                script.wait(2f);
            }
            
            i++;if(s==i) {
                WorldMoveDefault.applyTo(e);
                e.waveAmplitude = e.posX - W/2;
                applyRotateWeapons(e, 1);
                e.timer = 0;
                e.subState = 0;
                e.rotateAttack = true;
            }
            
            final float period = MathUtil.TWO_PI / 0.8f;
            final int nbPeriodsRotate = 1;
            final int nbPeriodsTranslate = 1;
            
            i++;if(s==i) {
                
                e.timer += Timer.delta;
                if (e.rotateAttack) {
                    if (e.timer >= nbPeriodsRotate * period) {
                        e.timer -= nbPeriodsRotate * period;
                        if (e.getLifePercent() < 0.8f && e.subState == 0) {
                            e.subState = 1;
                            e.rotateAttack = false;
                            e.rotDirY = 1;
                            e.rotDirZ = 0;
                            applyTranslateWeapons(e, 3);
                            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                            ex.setup(1.3f, 50f, 450f, 0.8f, 0.0f, 0.0f);
                            ex.setColor(1f, 1f, 0f,  0.8f, 0.8f, 1f);
                        }
                        
                    }

                } else {
                    if (e.timer >= nbPeriodsTranslate * period) {
                        e.timer -= nbPeriodsTranslate * period; 
                        if (e.getLifePercent() < 0.29f) {
                            e.subState = 2;
                            e.rotateAttack = true;
                            e.rotDirY = 0;
                            e.rotDirZ = 1;
                            applyRotateWeapons(e, 5);
                            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                            ex.setup(1.3f, 50f, 450f, 0.8f, 0.0f, 0.0f);
                            ex.setColor(0f, 1f, 1f,  0.8f, 0.8f, 1f);
                        }

                        
                    }
                }
                e.rotationSpeed = (float)(180f*Math.sin(0.8f * e.timer) /* * Math.sin(0.8f * e.rotationer) */);
                e.posX = W/2 + e.waveAmplitude * (float)Math.cos(0.8f * e.timer);
                script.step--;
            }
            

            
          //----- End of the script -----
          //  i++;if(s==i) {
          //      script.step -= 1; //loop
          //  }
            
        }
        
        public static void applyRotateWeapons(BossA e, int number) {
            e.clearFireBehaviors();
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < number; k++) {
                    FireCyclic.applyTo(e).delay(0.5f).shoot(21, 0.14635f, 1f).speed(40f).angle(j==0?0:180, true).offsets( 0f, j==0?14f + 4*k - 1.5f : -14f - 4*k + 1.5f).bullet(Type.SHOTB);
                    FireCyclic.applyTo(e).delay(0.5f).shoot(21, 0.14635f, 1f).speed(40f).angle(j==0?0:180, true).offsets( 0f, j==0?14f + 4*k : -14f - 4*k).bullet(Type.SHOTA);
                }
                
            }
        }
        
        public static void applyTranslateWeapons(BossA e, int number) {
            e.clearFireBehaviors();

            for (int k = 0; k < number; k++) {
                FireCyclic.applyTo(e).delay(0.5f).shoot(2, 0.5f, 1.5f).speed(60f).angle(0, true).offsets( 0f, 12f + 4*k).bullet(Type.SHOTLONG);
            }
                
        }
        
    }
    
}
