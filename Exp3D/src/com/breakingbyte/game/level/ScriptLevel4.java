package com.breakingbyte.game.level;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.bonus.Bonus.BonusType;
import com.breakingbyte.game.entity.enemy.Astrol;
import com.breakingbyte.game.entity.enemy.Byrol;
import com.breakingbyte.game.entity.enemy.Crystol;
import com.breakingbyte.game.entity.enemy.Drakol;
import com.breakingbyte.game.entity.enemy.boss.BossD;
import com.breakingbyte.game.entity.fire.Fire.Type;
import com.breakingbyte.game.entity.fire.FireCyclic;
import com.breakingbyte.game.entity.fire.FireSpiral;
import com.breakingbyte.game.entity.move.LocalMoveDefault;
import com.breakingbyte.game.entity.move.LocalMoveFacePlayer;
import com.breakingbyte.game.entity.move.LocalMoveSinusoidalRotation;
import com.breakingbyte.game.entity.move.LocalMoveZoomRotation;
import com.breakingbyte.game.entity.move.WorldMoveBezierQuadratic;
import com.breakingbyte.game.entity.move.WorldMoveDefault;
import com.breakingbyte.game.entity.move.WorldMoveFollowPlayer;
import com.breakingbyte.game.entity.move.WorldMoveSequence;
import com.breakingbyte.game.entity.move.WorldMoveSinusoidal;
import com.breakingbyte.game.entity.move.WorldMoveSmooth;
import com.breakingbyte.game.entity.move.WorldMoveWait;
import com.breakingbyte.game.entity.particle.Explosion;
import com.breakingbyte.game.entity.particle.ExplosionFireRing;
import com.breakingbyte.game.entity.particle.ShotA;
import com.breakingbyte.game.entity.particle.ShotB;
import com.breakingbyte.game.entity.particle.ShotC;
import com.breakingbyte.game.entity.particle.ShotLong;
import com.breakingbyte.game.script.ScriptInterpreter;
import com.breakingbyte.game.script.ScriptInterpreter.Script;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.state.ForwarderState;
import com.breakingbyte.game.state.ForwarderState.DialogEnum;
import com.breakingbyte.game.state.TitleState;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;


public class ScriptLevel4 implements Script {

    public static ScriptLevel4 instance = new ScriptLevel4();
    
    public void setDefaultValues() {
        ShotA.settings.width = ShotA.settings.height = 5f;
        ShotA.settings.zoomTarget = 1.2f;
        ShotA.settings.attackPower = 100;
        
        ShotB.settings.width = ShotB.settings.height = 5f;
        ShotB.settings.zoomTarget = 3f;
        ShotB.settings.attackPower = 100;
        
        ShotC.settings.width = ShotC.settings.height = 4f;
        ShotC.settings.zoomTarget = 3f;
        ShotC.settings.attackPower = 100;
        
        ShotLong.settings.width = 5f;
        ShotLong.settings.height = 10f;
        ShotLong.settings.zoomTarget = 2.5f;
        ShotLong.settings.attackPower = 100;
    }
    
    @SuppressWarnings("unused")
    public void runScript(ScriptInterpreter script) {
        
        //HACK 
        if (false && script.step == 0) {
            script.step = 45;
            ArenaState.setPlayerControlShip(true);
            UI.displayLifeBar();
            setDefaultValues();
            Engine.player.autoFire = false;
            Engine.player.setPositionTarget(50, 50);
            Explosion.setSlowExplosions(false);
        }
        
        final float H = Screen.ARENA_HEIGHT;
        final float W = Screen.ARENA_WIDTH;
        
        final int s = script.step;
        int i = -1;
        Entity e;
        
        SmoothJoin bgSpeed = ArenaState.instance.backgroundScrollSpeed;
        
        //INTRO
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
            WorldMoveSmooth.applyTo(e).speed(1.2f).pt0(W * 0.f, -20).pt1( W*0.25f, H*0.4f);
            LocalMoveSinusoidalRotation.applyTo(e).speed(2f).amplitude(15f, 30f, 10f).phase(MathUtil.PI);
            UI.displayLevelTitle(true);
            //script.wait(15f/*3f*/);
            script.wait(3.25f);
        }
        
        i++;if(s==i){
            float duration = 1.1f;
            e = Engine.player;
            WorldMoveSmooth.applyTo(e).speed(duration).pt0(e.posX, e.posY).pt1( W*0.5f, H*0.7f).interp(Interpolator.SINUSOIDAL_SLOW_START);
            LocalMoveZoomRotation.applyTo(e).setZoomRot(duration, e.scale, 1f, -(e.rotY - 1 * 360f), 0);
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
        }
        
        i++;if(s==i){
            UI.displayShipFocuser(false);
            e = Engine.player;
            WorldMoveDefault.applyTo(e);
            ArenaState.setPlayerControlShip(true);
        }
        //END INTRO
        
        i++;if(s==i) {
            script.repeatThisStep(5);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.8f).pt0(10, H+10).pt1(W*0.5f, H - 150).pt2(W-10f, H+10f);
            script.wait(0.15f);
        }
        
        i++;if(s==i) {
            //script.repeatThisStep(3);
            e = Drakol.spawn(); e.surviveMoveEnd = false;
            float x = W*0.5f; float y = 0;

            WorldMoveSequence.applyTo(e)
                .append( WorldMoveSmooth.newInstance().speed(1.6f).pt0( W*0.5f, H * 1.1f).pt1(W * 0.5f, H-20f).interp(Interpolator.BACK_END) )
                .append( WorldMoveWait.newInstance().duration(2.8f) )
                .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0( W * 0.5f, H-20f).pt1(W * 0.3f, H+20f).interp(Interpolator.QUADRATIC_START) )
            ;
            LocalMoveFacePlayer.applyTo(e).speed(2f).initRotZ(160f);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(90, true, 50f).offsets(-3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(-90, true, 50f).offsets(3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(90, true, 30f).offsets(-3, -2f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(-90, true, 30f).offsets(3, -2f).bullet(Type.SHOTB);
        }
        
        i++;if(s==i) {
            script.repeatThisStep(30);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.8f).pt0(10, H+10).pt1(W*0.5f, H - 150).pt2(W-10f, H+10f);
            script.wait(0.15f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        
        i++;if(s==i) {
            script.repeatThisStep(2);
            e = Drakol.spawn(); e.surviveMoveEnd = false;
            if (script.getRemainingRepeatSteps() == 1) {
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.0f).pt0( W + 10f, H * 1.1f).pt1(W - 10f, H - 10f).interp(Interpolator.QUADRATIC_END) )
                    .append( WorldMoveWait.newInstance().duration(1f) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0( W - 10f, H - 10f).pt1(W + 10f, H * 1.1f).interp(Interpolator.QUADRATIC_START) )
                ;
                e.carryOrb = true;
            } else {
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.0f).pt0( -10f, - 10f).pt1(10f, 10f).interp(Interpolator.QUADRATIC_END) )
                    .append( WorldMoveWait.newInstance().duration(1f) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0(10f, 10f).pt1(-10f, - 10f).interp(Interpolator.QUADRATIC_START) )
            ;
            }
            LocalMoveFacePlayer.applyTo(e).speed(2f);//.initRotZ(160f);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(90, true, 50f).offsets(-3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(-90, true, 50f).offsets(3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(90, true, 30f).offsets(-3, -2f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(-90, true, 30f).offsets(3, -2f).bullet(Type.SHOTB);
        }
        
        i++;if(s==i) {
            script.wait(2.2f);
        }

        i++;if(s==i) {
            script.repeatThisStep(2);
            e = Drakol.spawn(); e.surviveMoveEnd = false;
            if (script.getRemainingRepeatSteps() == 1) {
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.0f).pt0( -10f, H * 1.1f).pt1(10f, H - 10f).interp(Interpolator.QUADRATIC_END) )
                    .append( WorldMoveWait.newInstance().duration(1f) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0( 10f, H - 10f).pt1(-10f, H * 1.1f).interp(Interpolator.QUADRATIC_START) )
                ;
            } else {
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.0f).pt0( W+10f, - 10f).pt1(W - 10f, 10f).interp(Interpolator.QUADRATIC_END) )
                    .append( WorldMoveWait.newInstance().duration(1f) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0(W - 10f, 10f).pt1(W + 10f, - 10f).interp(Interpolator.QUADRATIC_START) )
            ;
            }
            LocalMoveFacePlayer.applyTo(e).speed(2f);//.initRotZ(160f);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(90, true, 50f).offsets(-3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(-90, true, 50f).offsets(3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(90, true, 30f).offsets(-3, -2f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(1.3f).shoot(8, 0.15f, 1.0f).speed(80f).pulse(-90, true, 30f).offsets(3, -2f).bullet(Type.SHOTB);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) {
            script.repeatThisStep(10); 
            float offset = 13;
            e = Astrol.spawn(); e.posX = W; e.posY = H * 1.1f; e.rotDirY = 0.25f; e.rotY = 90f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.3f).pt0(W, H*1.1f).pt1(-80, -60f).pt2(W+W/2, 50);
            e.carryOrb = script.getRemainingRepeatSteps() < 7;
            e = Astrol.spawn(); e.posX = W; e.posY = H * 1.1f; e.rotDirY = 0.25f; e.rotY = 90f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.3f).pt0(W - offset, H*1.1f).pt1(-80 - offset, -60f - offset).pt2(W+W/2, 50 - offset);
            //e = Astrol.spawn(); e.posX = W; e.posY = H * 1.1f; e.rotDirY = 0.25f; e.rotY = 90f;
            //WorldMoveBezierQuadratic.applyTo(e).speed(0.6f).pt0(W - 12, H*1.1f).pt1(-20 -12, H*0.5f).pt2(W + W/2 - 12, H-H*1.1f);
            
            e = Astrol.spawn(); e.posX = 0; e.posY = H * 1.1f; e.rotDirY = -0.25f; e.rotY = -90f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.3f).pt0(0, H*1.1f).pt1(W+80, -60f).pt2(-W/2, 50);
            e = Astrol.spawn(); e.posX = 0; e.posY = H * 1.1f; e.rotDirY = -0.25f; e.rotY = -90f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.3f).pt0(0 + offset, H*1.1f).pt1(W+80 + offset, -60f - offset).pt2(-W/2, 50 - offset);
            //e = Astrol.spawn(); e.posX = 0; e.posY = H * 1.1f; e.rotDirY = -0.25f; e.rotY = -90f;
            //WorldMoveBezierQuadratic.applyTo(e).speed(0.4f).pt0(0 - 12f, H*1.1f).pt1(W+40 -12, H*0.5f).pt2(W/2 -12f, H-H*1.1f);
            
            script.wait(0.3f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) {
            script.repeatThisStep(5);
            e = Crystol.spawn(); e.clearWhenLeaveScreen = true; 
            //e.carryPowerUp(BonusType.HELLFIRE);
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            e.posX = 10; e.posY = H + 10; e.movY = -1; e.moveSpeed = 50;
            FireSpiral.applyTo(e).branches(8).angleSpeed(40f).delay(0.0f).shoot(6, 0.07f, 2.5f).speed(60f).offsets(0, 4f).bullet(Type.SHOTA);
            
            e = Crystol.spawn(); e.clearWhenLeaveScreen = true; 
            //e.carryPowerUp(BonusType.HELLFIRE);
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            e.posX = 90; e.posY = H + 10; e.movY = -1; e.moveSpeed = 50;
            FireSpiral.applyTo(e).branches(8).angleSpeed(-40f).delay(0.0f).shoot(6, 0.07f, 2.5f).speed(60f).offsets(0, 4f).bullet(Type.SHOTA);
            script.wait(1f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) {
            for (int j = 0; j < 4; j++)
            {
                boolean class1 = (j == 0 || j == 3);
                e = class1? Byrol.spawn() : Drakol.spawn(); LocalMoveDefault.applyTo(e);
                if (class1) e.carryPowerUp(BonusType.TIME_WARP); else e.carryOrb = true;
                boolean first = j==0; float x = first? 15 : 85;
                x = 10 + j * (W - 20) / (4f - 1);
                //LocalMoveSinusoidalRotation.applyTo(e).speed(2f).amplitude(0, 0, 10f).idle(0, 0, first? 15 : -15).phase(!first? MathUtil.HALF_PI : MathUtil.HALF_PI + MathUtil.PI);
                LocalMoveFacePlayer.applyTo(e).speed(6f);
                //WorldMoveSmooth.applyTo(e).speed(1f).pt0( x, H * 1.1f).pt1(x, H - 20).interp(Interpolator.BACK_END);
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1f).pt0( x, H * 1.1f).pt1(x, H - 20).interp(Interpolator.BACK_END) )
                    .append( WorldMoveWait.newInstance().duration(10f) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0(x, H - 20).pt1(x, H * 1.1f).interp(Interpolator.QUADRATIC_START) )
                ;
                
                FireCyclic.applyTo(e).delay(0.9f).shoot(25, 0.08f, 1.9f).speed(110f).angle(-8, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(class1? Type.SHOTB : Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.9f).shoot(25, 0.08f, 1.9f).speed(110f).angle(8, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(class1? Type.SHOTB : Type.SHOTC);
             }
            
            script.wait(2f);
        }
        
        i++;if(s==i) {
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; 
            e.posX = 20; e.posY = H + 10; e.movY = -1; e.moveSpeed = 100;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; 
            e.posX = 80; e.posY = H + 20; e.movY = -1; e.moveSpeed = 60;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; 
            e.posX = 50; e.posY = H + 10; e.movY = -1; e.moveSpeed = 70;
            script.wait(3f);
        }
        
        i++;if(s==i) {
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; 
            e.posX = 20; e.posY = H + 20; e.movY = -1; e.moveSpeed = 60;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; 
            e.posX = 80; e.posY = H + 10; e.movY = -1; e.moveSpeed = 100;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; 
            e.posX = 50; e.posY = H + 10; e.movY = -1; e.moveSpeed = 70;
            script.waitAllEnemiesGone();
        }

        
        i++;if(s==i) { //Byrols protected by 3 Astrols in front of each
            script.repeatThisStep(7);
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.09f).vertical(true).nbOsc(3).pt0(W * 0.1f, H+10).pt1(W * 0.9f, -5);
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.09f).vertical(true).nbOsc(3).pt0(W * 0.1f - 12, H+10).pt1(W * 0.9f - 12, -5);
            e = Astrol.spawn();
            WorldMoveSinusoidal.applyTo(e).speed(0.09f).vertical(true).nbOsc(3).pt0(W * 0.1f + 12, H+10).pt1(W * 0.9f + 12, -5);
            e = Byrol.spawn(); if (script.getRemainingRepeatSteps() > 1) e.carryOrb = true; else e.carryPowerUp(BonusType.TIME_WARP);
            WorldMoveSinusoidal.applyTo(e).speed(0.09f).vertical(true).nbOsc(3).pt0(W * 0.1f, H+10 + 12).pt1(W * 0.9f, -5 + 12);
            FireCyclic.applyTo(e).delay(1.5f).shoot(9, 0.07f, 1f).speed(90f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(1.5f).shoot(9, 0.07f, 1f).speed(90f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            
            boolean anomaly = (script.getRemainingRepeatSteps() % 2 == 0);
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true;
            e.posX = 30 + (anomaly? 5 : 0); e.posY = H + 10; e.movY = -1; e.moveSpeed = anomaly ? 150f : 80f;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true;
            e.posX = 10 - (anomaly? 5 : 0); e.posY = H + 10; e.movY = -1; e.moveSpeed = 100f;
            
            script.wait(2f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i){
            script.wait(1f);
        }
        
        i++;if(s==i){ 
            script.repeatThisStep(12);
            e = Byrol.spawn();
            LocalMoveDefault.applyTo(e); e.rotationSpeed = 1f; e.rotZ = -20f; e.rotX = -50f; e.rotDirX = 40f; e.rotY = -10f; e.rotDirY = 10f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.4f).pt0(W, H+10).pt1(2*W/3, H/5).pt2(W/3, H+10);
            FireCyclic.applyTo(e).delay(1.0f).shoot(8, 0.1f, 9f).speed(90f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);//.reshapeBullets(7f, 7f, 100f);
            FireCyclic.applyTo(e).delay(1.1f).shoot(8, 0.1f, 9f).speed(90f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);

            script.wait(0.3f);
        }
        
        i++;if(s==i){
            script.waitAllEnemiesGone();
        }

        
        i++;if(s==i){
            script.repeatThisStep(4);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.7f).pt0(-10, H - 20).pt1( W * 0.5f, H - 90f).pt2(W + 10, H-20);
            script.wait(0.2f);
        }
        
        i++;if(s==i){ //Must be on top, or die. Bottom is a death trap with too many bullets. 
            script.repeatThisStep(10);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.7f).pt0(-10, H - 20).pt1( W * 0.5f, H - 90f).pt2(W + 10, H-20);
            boolean left = (script.getRemainingRepeatSteps() % 2 == 0);
            e = left? Drakol.spawn() : Byrol.spawn();
            LocalMoveDefault.applyTo(e); e.rotZ = left ? 90f : -90f;//e.rotationSpeed = 1f; e.rotZ = -20f; e.rotX = -50f; e.rotDirX = 40f; e.rotY = -10f; e.rotDirY = 10f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.7f).pt0(left? -10 : W + 10, H - 10).pt1(left? W / 3f : 2*W/3f, H * 0.5f).pt2(left? -10 : W + 10, -10);
            FireCyclic.applyTo(e).delay(0.4f).shoot(20, 0.06f, 9f).speed(120f).angle(0, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(left? Type.SHOTC : Type.SHOTB);//.reshapeBullets(7f, 7f, 100f);
            FireCyclic.applyTo(e).delay(0.4f).shoot(20, 0.06f, 9f).speed(120f).angle(0, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(left? Type.SHOTC : Type.SHOTB);

            script.wait(0.2f);
        }
        
        i++;if(s==i){
            script.wait(0.6f);
        }
        
        i++;if(s==i){ //2 byrols to finish the work if player keeps hiding on the top of the screen
            for (int j = 0; j < 2; j++) {
                boolean left = (j == 0);
                e = Byrol.spawn();
                LocalMoveDefault.applyTo(e); e.rotZ = 0f; e.rotationSpeed = 50f * (left? 1f : -1f); e.rotDirZ = 1f; //e.rotX = -50f; e.rotDirX = 40f; e.rotY = -10f; e.rotDirY = 10f;
                FireCyclic.applyTo(e).delay(0.5f).shoot(25, 0.034f, 9f).speed(120f).angle(0, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);//.reshapeBullets(7f, 7f, 100f);
                FireCyclic.applyTo(e).delay(0.5f).shoot(25, 0.034f, 9f).speed(120f).angle(0, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.6f).pt0( left ? -10f : W + 10f, H).pt1(left ? 10 : W - 10, H-30f).interp(Interpolator.BACK_END) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0(left ? 10 : W - 10, H-30f).pt1( left ? -10f : W + 10f, H + 30f).interp(Interpolator.QUADRATIC_START) )
                ;
                
            }
        }
        
        i++;if(s==i){
            script.wait(1f);
        }
        
        i++;if(s==i){ //2 byrols to finish the work if player keeps hiding on the top of the screen
            for (int j = 0; j < 2; j++) {
                boolean left = (j == 0);
                e = Drakol.spawn();
                LocalMoveDefault.applyTo(e); e.rotZ = 180f; e.rotationSpeed = 50f * (left? -1f : 1f); e.rotDirZ = 1f; //e.rotX = -50f; e.rotDirX = 40f; e.rotY = -10f; e.rotDirY = 10f;
                FireCyclic.applyTo(e).delay(0.42f).shoot(35, 0.034f, 9f).speed(120f).angle(0, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTC);//.reshapeBullets(7f, 7f, 100f);
                FireCyclic.applyTo(e).delay(0.42f).shoot(35, 0.034f, 9f).speed(120f).angle(0, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTC);
                
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.6f).pt0( left ? -10f : W + 10f, H - 100).pt1(left ? 10 : W - 10, H-90).interp(Interpolator.BACK_END) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0(left ? 10 : W - 10, H-90).pt1( left ? -10f : W + 10f, H - 100).interp(Interpolator.QUADRATIC_START) )
                ;
                
            }
        }
        
        i++;if(s==i){
            script.wait(1f);
        }
        
        i++;if(s==i){ //2 byrols to finish the work if player keeps hiding on the top of the screen
            for (int j = 0; j < 2; j++) {
                boolean left = (j == 0);
                e = Byrol.spawn();
                LocalMoveDefault.applyTo(e); e.rotZ = 0f; e.rotationSpeed = 50f * (left? 1f : -1f); e.rotDirZ = 1f; //e.rotX = -50f; e.rotDirX = 40f; e.rotY = -10f; e.rotDirY = 10f;
                FireCyclic.applyTo(e).delay(0.5f).shoot(25, 0.034f, 9f).speed(120f).angle(0, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);//.reshapeBullets(7f, 7f, 100f);
                FireCyclic.applyTo(e).delay(0.5f).shoot(25, 0.034f, 9f).speed(120f).angle(0, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.6f).pt0( left ? -10f : W + 10f, H).pt1(left ? 10 : W - 10, H-30f).interp(Interpolator.BACK_END) )
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0(left ? 10 : W - 10, H-30f).pt1( left ? -10f : W + 10f, H + 30f).interp(Interpolator.QUADRATIC_START) )
                ;
                
            }
        }
        
        i++;if(s==i){
            script.wait(1.5f);
        }
        
        
        i++;if(s==i) {
            ArenaState.instance.displayUnlockLevelDialog();
        }
        
        i++;if(s==i){
            script.wait(0.5f);
        }
        
        
        i++;if(s==i){
            script.repeatThisStep(5);
            e = Crystol.spawn(); e.carryOrb = true;
            WorldMoveSinusoidal.applyTo(e).speed(0.09f).vertical(true).nbOsc(3).pt0(W * 0.9f, H+10).pt1(W * 0.1f, -5);
            FireSpiral.applyTo(e).branches(7).angleSpeed(0f).delay(1.0f).shoot(10, 0.04f, 0.5f).speed(160f).offsets(0, 4f).bullet(Type.SHOTA);
            script.wait(2f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) {
            script.repeatThisStep(6);
            boolean right = (script.getRemainingRepeatSteps() % 2 == 0);
            float centerX = right? 60f : 40f, centerY = H*0.6f, radius1 = 100f, radius2 = 45f;
            float angleOpening = 160f;
            float angleBegin = -55f + (right? 20 : 90), angleEnd = angleBegin + angleOpening;
            int nb = 8;
            for (int j = 0; j < nb; j++) {
                e = Astrol.spawn(); 
                float pt0X = MathUtil.getCirclePosition(centerX, centerY, radius1, angleBegin, angleEnd, j, nb, true), 
                      pt0Y = MathUtil.getCirclePosition(centerX, centerY, radius1, angleBegin, angleEnd, j, nb, false), 
                      pt1X = MathUtil.getCirclePosition(centerX, centerY, radius2, angleBegin, angleEnd, j, nb, true), 
                      pt1Y = MathUtil.getCirclePosition(centerX, centerY, radius2, angleBegin, angleEnd, j, nb, false);
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(0.8f).pt0(pt0X, pt0Y).pt1(pt1X, pt1Y).interp(Interpolator.QUADRATIC_START_END)  )
                    .append( WorldMoveFollowPlayer.newInstance().speed(10f, 120f, Interpolator.QUADRATIC_START, 0.3f) )
                    ;
            }
            script.wait(0.6f);
        }
        
        i++;if(s==i) {
            script.wait(0.5f);
            //script.step -= 2;
            //script.step = -1;
        }
        
        i++;if(s==i) {
            //script.step -= 3;
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
            bgSpeed.setTarget(10, 2f);
        }
        
        i++;if(s==i) {
            BossD boss = BossD.spawn();
            boss.posX = -500f; boss.posY = -500f;
            boss.scriptInterpreter.setScript(BossScript.instance);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { //Boss dead - Explosions
            
            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=BossD.lastAliveX; ex.posY = BossD.lastAliveY;
            ex.setup(1.5f, 20f, 500f, 1f, 0f, 0.5f);
            ex.setColor(1f, 1f, 198f/255f,  1f, 149f/255f, 38f/255f);
            
            Explosion.setSlowExplosions(false);
            int total = 6;
            e = Explosion.spawn(); e.setDimension(90, 90); e.posX = BossD.lastAliveX; e.posY = BossD.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossD.lastAliveX + 14 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossD.lastAliveY + 14 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossD.lastAliveX + 5 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossD.lastAliveY + 5 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(200, 200); 
            e.posX = BossD.lastAliveX;
            e.posY = BossD.lastAliveY;
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }        
        
        i++;if(s==i) { 
            int total = 6;
            Explosion.setSlowExplosions(true);
            e = Explosion.spawn(); e.setDimension(120, 120); e.posX = BossD.lastAliveX; e.posY = BossD.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(120, 120); 
                e.posX = BossD.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossD.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(220, 220); 
            e.posX = BossD.lastAliveX;
            e.posY = BossD.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        
        
        i++;if(s==i) { 
            int total = 6;
            e = Explosion.spawn(); e.setDimension(150, 150); e.posX = BossD.lastAliveX; e.posY = BossD.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(150, 150); 
                e.posX = BossD.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossD.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(250, 250); 
            e.posX = BossD.lastAliveX;
            e.posY = BossD.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) {
            UI.hideLifeBar();
            bgSpeed.init(bgSpeed.get());
            bgSpeed.setTarget(40, 2f);
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
            LocalMoveZoomRotation.applyTo(e).setZoomRot(duration, e.scale, e.scale * 1.5f, e.rotY, e.rotY - 2.0f * 360f);
        }
        
        i++;if(s==i) {
            script.wait(1.4f);
        }
        
        i++;if(s==i) {
            ForwarderState.instance.setNextState(TitleState.instance, 0, 0, 0, 1f, DialogEnum.GAME_CLEARED);
            ArenaState.instance.switchToStateWithColor(ForwarderState.instance, 0f, 0f, 0f, 1f, 0f);
        }

        
        
        //----- End of the script -----
        i++;if(s==i) {
            script.stayInThisStep();
            //script.waitAllEnemiesGone();
            //script.step = -1; //loop
        }
    }
    
    private static class BossScript implements Script {

        private static BossScript instance = new BossScript();
        
        @SuppressWarnings("unused")
        @Override
        public void runScript(ScriptInterpreter script) {
            
            //HACK 
            if (false && script.step == 0) script.step = 4;
            
            final float H = Screen.ARENA_HEIGHT;
            final float W = Screen.ARENA_WIDTH;
            
            final int s = script.step;
            int i = -1;
            BossD e = (BossD) script.entity;
            
            //Intro
            i++;if(s==i) {
                
                ShotC.settings.width = ShotC.settings.height = 6f;
                ShotC.settings.zoomTarget = 3f;
                ShotC.settings.attackPower = 100;
                
                e.surviveMoveEnd = true;
                //e.lifeRemaining = 200;
                //WorldMoveSmooth.applyTo(e).speed(1.2f).pt0(30, H+30).pt1(W/2f, H*0.70f);
                e.rotationY.setInterpolator(Interpolator.QUADRATIC_START_END);
                e.setInitialConfig();
                LocalMoveFacePlayer.applyTo(e).speed(2f);
                e.rotationY.init(0f);
                e.rotationY.setTarget(0, 3.5f); 
                WorldMoveSmooth.applyTo(e).speed(1.5f).pt0(-10, H + 20).pt1(40, H - 30).smoother.setInterpolator(Interpolator.BACK_END).backAmplitude = 1.5f;
                script.wait(2.5f);
                e.immuneToCollision = true;
                
                //Engine.player.immuneToCollision = true;
            }
            
            //Positioning completed, fire!
            i++;if(s==i) {
                //Fight begins
                e.immuneToCollision = false;
                UI.displayBossLifeBar(true);
            }
            
            i++;if(s==i) {
                e.rotationY.setInterpolator(Interpolator.ASYMPTOTIC);
                
                //e.rotationY.setTarget(0, 2f);
                applyFirstWeapon(e, 100, false, 0);
                script.wait(1.2f);
            }
            
            //Move to the opposite side of the screen 
            i++;if(s==i) {
                e.clearFireBehaviors();
                if (e.lifeRemaining / (float)e.lifeStart > 0.75f ) { //LOOP
                    float x = (e.posX > Screen.ARENA_WIDTH * 0.5f)? W * 0.4f : W * 0.6f;
                    WorldMoveSmooth.applyTo(e).speed(1.5f).pt0(e.posX, e.posY).pt1(x, e.posY).interp(Interpolator.QUADRATIC_START_END);
                    script.wait(0.3f);
                    script.step -= 2;
                }
            }
            
            
            i++;if(s==i) {
                script.wait(1.0f);
            }
            
            //Secondary
            
            i++;if(s==i) {
                e.rotationY.setInterpolator(Interpolator.ASYMPTOTIC);
                
                //e.rotationY.setTarget(0, 2f);
                applySecondWeapon(e, 100);
                script.wait(1.2f);
            }
            
            //Move to the opposite side of the screen 
            i++;if(s==i) {
                e.clearFireBehaviors();
                if (e.lifeRemaining / (float)e.lifeStart > 0.5f ) { //LOOP
                    float x = (e.posX > Screen.ARENA_WIDTH * 0.5f)? W * 0.4f : W * 0.6f;
                    WorldMoveSmooth.applyTo(e).speed(1.5f).pt0(e.posX, e.posY).pt1(x, e.posY).interp(Interpolator.QUADRATIC_START_END);
                    script.wait(0.4f);
                    script.step -= 2;
                }
            }
            
            i++;if(s==i) {
                script.wait(0.5f);
            }
           
            
            
            
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                script.repeatThisStep(15);
                e.setReflectAttack(true);
                ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                ex.setup(0.7f, 38f, 80f, 0.8f, 0.0f, 0.0f);
                ex.setColor(0.0f, 0.6f, 0.6f,  0.0f, 0.8f, 1f);
                //ex.setColor(1f, 1, 0.2f,  1f, 1f, 0.6f);
                script.wait(0.25f);
            }
            
            i++;if(s==i) {
                e.setReflectAttack(false);
                script.wait(0.4f);
            }
            
            i++;if(s==i) {
                e.rotationY.setInterpolator(Interpolator.ASYMPTOTIC);
                
                //e.rotationY.setTarget(0, 2f);
                applyPostReflectWeapon(e, 20, false, 0, 0.4f);
                script.wait(4f);
            }
            
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                if (e.lifeRemaining / (float)e.lifeStart > 0.3f) { //LOOP
                    script.step -= 4;
                    script.wait(0.3f);
                }
            }
            
            i++;if(s==i) {
                script.wait(0.5f);
            }
            
            i++;if(s==i) {
                e.rotationY.setInterpolator(Interpolator.ASYMPTOTIC);
                LocalMoveDefault.applyTo(e);
                e.rotDirZ = 1f;
                e.rotationSpeedZ.setTarget(-250, 1f);
                e.rotationY.setTarget(90, 1f);
                e.rotYShakeAmplitude.setTarget(40, 1f);
                e.setReflectAttack(false);
                script.wait(1.2f);
            }
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                WorldMoveSmooth.applyTo(e).speed(1.3f).pt0(e.posX, e.posY).pt1(W - 10, 20).interp(Interpolator.QUADRATIC_START_END);
//                FireCyclic.applyTo(e).shoot(5, 0.033f, 99f).speed(180f).offsets(0, 0).angle(-90, false).bullet(Type.SHOTB);
//                FireCyclic.applyTo(e).shoot(5, 0.033f, 99f).speed(180f).offsets(0, 0).angle(-70, false).bullet(Type.SHOTB);
                FireSpiral.applyTo(e).branches(12).angleSpeed(120f).delay(0.0f).shoot(4, 0.033f, 99f).speed(120).offsets(0, 4f).bullet(Type.SHOTB);
                script.wait(1.3f);
            }
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                WorldMoveSmooth.applyTo(e).speed(1f).pt0(e.posX, e.posY).pt1(10, 20).interp(Interpolator.QUADRATIC_START_END);
                script.wait(1f);
            }
            
            i++;if(s==i) {
                WorldMoveSmooth.applyTo(e).speed(1.3f).pt0(e.posX, e.posY).pt1(W - 10, H - 15).interp(Interpolator.QUADRATIC_START_END);
                script.wait(1.3f);
            }
            
            i++;if(s==i) {
                
                WorldMoveSmooth.applyTo(e).speed(1f).pt0(e.posX, e.posY).pt1(10, H - 15).interp(Interpolator.QUADRATIC_START_END);
//                FireCyclic.applyTo(e).shoot(5, 0.033f, 99f).speed(180f).offsets(0, 0).angle(-90, false).bullet(Type.SHOTB);
//                FireCyclic.applyTo(e).shoot(5, 0.033f, 99f).speed(180f).offsets(0, 0).angle(-120, false).bullet(Type.SHOTB);
                FireSpiral.applyTo(e).branches(12).angleSpeed(-120f).delay(0.0f).shoot(5, 0.033f, 99f).speed(120).offsets(0, 4f).bullet(Type.SHOTB);
                script.wait(1f);
            }
            
            i++;if(s==i) {
                //script.wait(2f);
                script.step -= 5;
            }
            
            
            

           
          //----- End of the script -----
          //  i++;if(s==i) {
          //      script.step -= 1; //loop
          //  }
            
        }
        
        public static void applyFirstWeapon(Entity e, int nbShot, boolean useAngle, float angle) {
            float wait = 99f;
            FireCyclic.applyTo(e).shoot(nbShot, 0.033f, wait).speed(120f).offsets(0, 0).angle(0, true).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).shoot(nbShot, 0.033f, wait).speed(120f).offsets(-14, 14).angle(0, true).delay(0.19f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).shoot(nbShot, 0.033f, wait).speed(120f).offsets(14, 14).angle(0, true).delay(0.19f).bullet(Type.SHOTC);
        }
        
        public static void applyPostReflectWeapon(Entity e, int nbShot, boolean useAngle, float angle, float wait) {
            FireCyclic.applyTo(e).shoot(nbShot, 0.033f, wait).speed(120f).offsets(0, 0).angle(0, true).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).shoot(nbShot, 0.033f, wait).speed(120f).offsets(-14, 14).angle(0, true).delay(0.19f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).shoot(nbShot, 0.033f, wait).speed(120f).offsets(14, 14).angle(0, true).delay(0.19f).bullet(Type.SHOTC);
        }
        
        public static void applySecondWeapon(Entity e, int nbShot) {
            float wait = 99f;
            FireCyclic.applyTo(e).shoot(nbShot, 0.033f, wait).speed(120f).offsets(0, 0).angle(0, true).delay(0.1f).bullet(Type.SHOTB);
            for (int j = 0; j < 6; j++) {
                float x = 0, y = 0, angle = 0;
                if (j == 0) { x = -15; y = 15; angle = 0; }
                if (j == 1) { x = -19; y = 0; angle = 0; }
                if (j == 2) { x = -15; y = -15; angle = 0; }
                if (j == 3) { x = 15; y = 15; angle = 180; }
                if (j == 4) { x = 19; y = 0; angle = 180; }
                if (j == 5) { x = 15; y = -15; angle = 180; }
                FireCyclic.applyTo(e).delay(0.033f).shoot(12, 0.06f, 3.5f).speed(250f).pulse(angle, false, 100f).offsets(x, y).bullet(Type.SHOTC);
            }
            FireCyclic.applyTo(e).delay(0.033f).shoot(12, 0.06f, 3.5f).speed(250f).pulse(90+10, false, 100f).offsets(0, -5).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(0.033f).shoot(12, 0.06f, 3.5f).speed(250f).pulse(90-10, false, 100f).offsets(0, -5).bullet(Type.SHOTB);
        }


        
    }
    
}
