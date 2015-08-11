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
import com.breakingbyte.game.entity.enemy.boss.BossC;
import com.breakingbyte.game.entity.fire.Fire.Type;
import com.breakingbyte.game.entity.fire.FireCyclic;
import com.breakingbyte.game.entity.fire.FireSinusoidal;
import com.breakingbyte.game.entity.fire.FireSpiral;
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
import com.breakingbyte.game.entity.particle.ShotC;
import com.breakingbyte.game.entity.particle.ShotLong;
import com.breakingbyte.game.script.ScriptInterpreter;
import com.breakingbyte.game.script.ScriptInterpreter.Script;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;


public class ScriptLevel3 implements Script {

    public static ScriptLevel3 instance = new ScriptLevel3();
    
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
            script.step = 5;
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
            WorldMoveSmooth.applyTo(e).speed(1.2f).pt0(W * 1.f, -20).pt1( W*0.75f, H*0.4f);
            LocalMoveSinusoidalRotation.applyTo(e).speed(2f).amplitude(15f, 30f, 10f);
            UI.displayLevelTitle(true);
            //script.wait(15f/*3f*/);
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
        }
        
        i++;if(s==i){
            UI.displayShipFocuser(false);
            e = Engine.player;
            WorldMoveDefault.applyTo(e);
            ArenaState.setPlayerControlShip(true);
        }
        //END INTRO
        
        i++;if(s==i) {
            script.repeatThisStep(6); 
            float posX = -10f, posY = H * 1.1f, speed = 120f, angle = -67f; 
            e = Astrol.spawn(); e.posX = posX; e.posY = posY; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = posX + 1f; e.posY = posY - 13f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = posX - 15f; e.posY = posY - 6f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            if (script.getRemainingRepeatSteps() == 5) e.carryPowerUp(BonusType.SUPER_SHIELD);
            
            posX = W; posY = H * 1.4f; speed = 120f; angle = -100f; 
            e = Astrol.spawn(); e.posX = posX; e.posY = posY; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = posX + 1f; e.posY = posY - 13f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = posX - 15f; e.posY = posY - 6f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            
            if (script.getRemainingRepeatSteps() == 5) {
                e = Byrol.spawn(); e.surviveMoveEnd = true; e.carryOrb = true;
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                WorldMoveSmooth.applyTo(e).speed(1f).pt0( -10f, H * 1.1f).pt1(W * 0.5f, H*0.8f).interp(Interpolator.QUADRATIC_END);
            }
            
            if (script.getRemainingRepeatSteps() == 4) {
                e = Byrol.spawn(); e.surviveMoveEnd = true; e.carryOrb = true;
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                WorldMoveSmooth.applyTo(e).speed(1f).pt0( -10f, H * 1.1f).pt1(W * 0.2f, H*0.9f).interp(Interpolator.QUADRATIC_END);
            }
            
            if (script.getRemainingRepeatSteps() == 3) {
                e = Byrol.spawn(); e.surviveMoveEnd = true; e.carryOrb = true;
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                WorldMoveSmooth.applyTo(e).speed(1f).pt0( -10f, H * 1.1f).pt1(W * 0.8f, H*0.9f).interp(Interpolator.QUADRATIC_END);
            }
            
            script.wait(1f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }

        i++;if(s==i) { //First 2 drakols
            for (int j = 0; j < 2; j++) {
                boolean first = j == 0;
                e = Drakol.spawn(); e.posX = first? -10 : W * 0.0f; e.posY = first ? H * 1.0f :  H * 1.3f; e.clearWhenLeaveScreen = true; e.rotZ = 30; e.setDirAngle(-50f); e.moveSpeed = 60f * 1f;
                FireCyclic.applyTo(e).delay(0.2f).shoot(8, 0.15f, 3.5f).speed(80f).pulse(180, true, 30f).offsets(-3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.2f).shoot(8, 0.15f, 3.5f).speed(80f).pulse(-180, true, 30f).offsets(3, 4f).bullet(Type.SHOTC);
            }
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) {
            int total = 20;
            script.repeatThisStep(total + 10);
            float progress = (total + 10 - script.getRemainingRepeatSteps()) / (float)total; if (progress < 0) progress = 0; if (progress > 1) progress = 1;
            float posX = 0, posY = 0, speed = 0, angle = 0; 
            for (int j = 0; j < 4; j++) {
                if (j == 0) { posX = 0 + progress * 20f; posY = H * 1.1f; speed = 160f; angle = -85f; }
                if (j == 1) { posX = W - progress * 20f; posY = - H * 0.1f; speed = 160f; angle = 95f; }
                if (j == 2) { posX = -10; posY = 190f - progress * 75f; speed = 160f; angle = -13f; }
                if (j == 3) { posX = W + 10; posY = progress * 50f; speed = 160f; angle = 170f; }
                e = Astrol.spawn(); e.posX = posX; e.posY = posY; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            }
            script.wait(0.1f);
        }

        i++;if(s==i){
            //bgSpeed.init(bgSpeed.get());
            //bgSpeed.setTarget(20, 1.5f);
            e = Crystol.spawn(); e.surviveMoveEnd = true; 
            //e.carryPowerUp(BonusType.HELLFIRE);
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(-30, H/2).pt1(20, H).pt2(W*0.5f, 140);
            FireSpiral.applyTo(e).branches(12).angleSpeed(40f).delay(1.0f).shoot(10, 0.07f, 1.0f).speed(60f).offsets(0, 4f).bullet(Type.SHOTA);
            
            e = Byrol.spawn(); e.surviveMoveEnd = true; LocalMoveDefault.applyTo(e);
            e.carryOrb = true;
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(W+30, H/2).pt1(80, H).pt2(80, 140);
            FireSpiral.applyTo(e).branches(4).angleSpeed(70f).delay(1.0f).shoot(3, 0.07f, 1.3f).speed(60f).offsets(0, 4f).bullet(Type.SHOTB);
            
            e = Byrol.spawn(); e.surviveMoveEnd = true; LocalMoveDefault.applyTo(e);
            e.carryOrb = true;
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(-30, H/2).pt1(20, H).pt2(20, 140);
            FireSpiral.applyTo(e).branches(4).angleSpeed(-70f).delay(1.0f).shoot(3, 0.07f, 1.3f).speed(60f).offsets(0, 4f).bullet(Type.SHOTB);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }

        //Series of Drakols
        i++;if(s==i) { //1 drakol
            e = Drakol.spawn(); e.posX = W*0.5f; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.rotZ = 0; e.setDirAngle(-90f); e.moveSpeed = 40f;
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 60f).offsets(-3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 60f).offsets(3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 90f).offsets(-3, -10f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 90f).offsets(3, -10f).bullet(Type.SHOTC);
            script.wait(1.5f);
        }
        
        i++;if(s==i) { //2 drakols
            for (int j = 0; j < 2; j++) {
                boolean first = j == 0;
                e = Drakol.spawn(); e.posX = first? W*0.2f : W * 0.8f; e.posY = first ? H * 1.1f :  H * 1.1f; e.clearWhenLeaveScreen = true; e.rotZ = 0; e.setDirAngle(-90f); e.moveSpeed = 40f;
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 60f).offsets(-3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 60f).offsets(3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 90f).offsets(-3, -10f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 90f).offsets(3, -10f).bullet(Type.SHOTC);
            }
            script.wait(1.5f);
        }
        
        i++;if(s==i) { //1 drakol
            e = Drakol.spawn(); e.posX = W*0.5f; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.rotZ = 0; e.setDirAngle(-90f); e.moveSpeed = 40f;
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 60f).offsets(-3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 60f).offsets(3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 90f).offsets(-3, -10f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 90f).offsets(3, -10f).bullet(Type.SHOTC);
            script.wait(1.5f);
        }
        
        i++;if(s==i) { //2 drakols
            for (int j = 0; j < 2; j++) {
                boolean first = j == 0;
                e = Drakol.spawn(); e.posX = first? W*0.2f : W * 0.8f; e.posY = first ? H * 1.1f :  H * 1.1f; e.clearWhenLeaveScreen = true; e.rotZ = 0; e.setDirAngle(-90f); e.moveSpeed = 40f;
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 60f).offsets(-3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 60f).offsets(3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 90f).offsets(-3, -10f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 90f).offsets(3, -10f).bullet(Type.SHOTC);
            }
            script.wait(1.5f);
        }
        
        i++;if(s==i) { //1 drakol
            e = Drakol.spawn(); e.posX = W*0.5f; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.rotZ = 0; e.setDirAngle(-90f); e.moveSpeed = 40f;
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 60f).offsets(-3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 60f).offsets(3, 4f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 90f).offsets(-3, -10f).bullet(Type.SHOTC);
            FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 90f).offsets(3, -10f).bullet(Type.SHOTC);
            script.wait(1.5f);
        }
        
        i++;if(s==i) { //2 drakols
            for (int j = 0; j < 2; j++) {
                boolean first = j == 0;
                e = Drakol.spawn(); e.posX = first? W*0.2f : W * 0.8f; e.posY = first ? H * 1.1f :  H * 1.1f; e.clearWhenLeaveScreen = true; e.rotZ = 0; e.setDirAngle(-90f); e.moveSpeed = 40f;
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 60f).offsets(-3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 60f).offsets(3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(120, true, 90f).offsets(-3, -10f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(0.1f).shoot(5, 0.15f, 9.5f).speed(190f).pulse(-120, true, 90f).offsets(3, -10f).bullet(Type.SHOTC);
            }
            script.wait(0.2f);
        }
        

        i++;if(s==i) { // V
            int nbRepeat = 4;
            script.repeatThisStep(nbRepeat);
            float iter = nbRepeat - script.getRemainingRepeatSteps();
            e = Astrol.spawn(); e.posX = W*0.5f - iter * 10; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
            e.carryOrb = iter <= 1;
            if (iter > 0) {
                e = Astrol.spawn(); e.posX = W*0.5f + iter * 10; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
                e.carryOrb = iter <= 1;
            }
            script.wait(0.3f - iter * 0.06f );
        }
        
        i++;if(s==i) { // |
            int nbRepeat = 4;
            script.repeatThisStep(nbRepeat);
            e = Astrol.spawn(); e.posX = W*0.5f; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
            e.carryOrb = script.getRemainingRepeatSteps() % 2 == 0;
            script.wait(0.3f);
        }
        
        i++;if(s==i) { // V
            int nbRepeat = 4;
            script.repeatThisStep(nbRepeat);
            float iter = nbRepeat - script.getRemainingRepeatSteps();
            e = Astrol.spawn(); e.posX = W*0.5f - iter * 10; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
            e.carryOrb = iter <= 1;
            if (iter > 0) {
                e = Astrol.spawn(); e.posX = W*0.5f + iter * 10; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
                e.carryOrb = iter <= 1;
            }
            script.wait(0.3f);
        }
        
        i++;if(s==i) { // V
            int nbRepeat = 4;
            script.repeatThisStep(nbRepeat);
            float iter = nbRepeat - script.getRemainingRepeatSteps();
            e = Astrol.spawn(); e.posX = W*0.5f - iter * 10; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
            e.carryOrb = iter <= 1;
            if (iter > 0) {
                e = Astrol.spawn(); e.posX = W*0.5f + iter * 10; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
                e.carryOrb = iter <= 1;
            }
            script.wait(0.3f);
        }
        
        i++;if(s==i) {
            script.wait(0.5f);
        }
        

        
        
        i++;if(s==i) { // 2 byrols to end
            for (int j = 0; j < 2; j++) {
                e = Byrol.spawn(); e.clearWhenLeaveScreen = true; e.rotZ = 0f; e.lifeRemaining = (int) (e.lifeRemaining * 1.5f);
                LocalMoveDefault.applyTo(e);
                boolean first = j==0;
                float angle = -90;
                e.posX = first? W * 0.2f : W * 0.8f; e.posY = H*1.1f; 
                e.setDirAngle(angle);
                e.rotZ = 0;
                e.moveSpeed = 40f;
                float sp = 1.6f;
                FireSinusoidal.applyTo(e).sinus(10f, 5f).                   delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(angle, false).offsets(0f, 4f).bullet(Type.SHOTB);
                FireSinusoidal.applyTo(e).sinus(10f, 5f).phase(MathUtil.PI).delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(angle, false).offsets(0f, 4f).bullet(Type.SHOTB);
            }
            
            script.wait(2.0f);
        }
        

        
        i++;if(s==i) { //and a surprise last one, player should move from the center ASAP
            e = Byrol.spawn(); e.clearWhenLeaveScreen = true; e.rotZ = 0f; e.lifeRemaining = (int) (e.lifeRemaining * 1.5f);
            e.carryPowerUp(BonusType.HELLFIRE);
            LocalMoveDefault.applyTo(e);
            float angle = -90;
            e.posX = W * 0.5f; e.posY = H*1.1f; 
            e.setDirAngle(angle);
            e.rotZ = 0;
            e.moveSpeed = 40f;
            float sp = 1.6f;
            FireSinusoidal.applyTo(e).sinus(10f, 5f).                   delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(angle, false).offsets(0f, 4f).bullet(Type.SHOTB);
            FireSinusoidal.applyTo(e).sinus(10f, 5f).phase(MathUtil.PI).delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(angle, false).offsets(0f, 4f).bullet(Type.SHOTB);
            script.wait(2.5f);
        }
        
        i++;if(s==i) {
            script.repeatThisStep(6);
            boolean left = script.getRemainingRepeatSteps() % 2 == 0;
            e = Byrol.spawn(); e.clearWhenLeaveScreen = true; e.rotZ = 0f; e.lifeRemaining = (int) (e.lifeRemaining * 1.5f);
            e.carryOrb = true;
            float angle = -90;
            e.posX = left? W * 0.1f : W * 0.9f; e.posY = H*1.1f; 
            e.setDirAngle(angle);
            e.rotDirZ = 1;
            e.rotationSpeed = 17f; if (!left)  e.rotationSpeed = -e.rotationSpeed;
            e.moveSpeed = 40f;
            float sp = 1.6f;
            FireSinusoidal.applyTo(e).sinus(10f, 5f).                   delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(0, true).offsets(0f, 4f).bullet(Type.SHOTB);
            FireSinusoidal.applyTo(e).sinus(10f, 5f).phase(MathUtil.PI).delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(0, true).offsets(0f, 4f).bullet(Type.SHOTB);
            LocalMoveDefault.applyTo(e);
            
            e = Astrol.spawn(); e.posX = W*0.5f; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 70f;
            
            script.wait(1f);
        }

        i++;if(s==i){
            script.wait(1.5f);
        }
        
        i++;if(s==i){ //Fast shooting crystols
            script.repeatThisStep(2); boolean first = !script.isLastStepOfRepeat();
            e = Crystol.spawn(); e.surviveMoveEnd = true; e.lifeRemaining = (int) (e.lifeRemaining * 1.5f);
            e.carryOrb = true;
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            float posX = first? W * 0.2f : W * 0.8f;
            WorldMoveSmooth.applyTo(e).speed(1f).pt0(posX, H*1.1f).pt1(posX, H*0.8f).smoother.setInterpolator(Interpolator.BACK_END).setBack(2.7f);;
            FireSpiral.applyTo(e).branches(7).angleSpeed(0f).delay(1.0f).shoot(10, 0.04f, 0.5f).speed(160f).offsets(0, 4f).bullet(Type.SHOTA);
            script.wait(0.5f);
        }
        
        i++;if(s==i){
            e = Crystol.spawn(); e.surviveMoveEnd = true; e.lifeRemaining = (int) (e.lifeRemaining * 1.5f);
            e.carryOrb = true;
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            float posX = W * 0.5f;
            WorldMoveSmooth.applyTo(e).speed(1f).pt0(posX, H*1.1f).pt1(posX, H*0.93f).smoother.setInterpolator(Interpolator.BACK_END).setBack(2.7f);;
            FireSpiral.applyTo(e).branches(7).angleSpeed(0f).delay(1.0f).shoot(10, 0.04f, 0.5f).speed(160f).offsets(0, 4f).bullet(Type.SHOTA);
        }
        
        i++;if(s==i){ //Protect crystols a little with a line of Astrols
            script.repeatThisStep(20);
            //e.carryPowerUp(BonusType.HELLFIRE);
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = -10f; e.posY = H * 0.7f; e.movX = 1f; e.moveSpeed = 160f;
            script.wait(0.1f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }

        
        i++;if(s==i){ 
            script.repeatThisStep(3);
            int nb = script.getRemainingRepeatSteps();
            float pos = 0;
            if (nb == 3) pos = 20; if (nb == 2) pos = 80; if (nb == 1) pos = 50;
            
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = pos; e.posY = H + 10f; e.movY = -1f; e.moveSpeed = 90f;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = pos; e.posY = H + 25f; e.movY = -1f; e.moveSpeed = 90f;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = pos - 10f; e.posY = H + 100f; e.movY = -1f; e.moveSpeed = 200f;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = pos + 10f; e.posY = H + 100f; e.movY = -1f; e.moveSpeed = 200f;
            script.wait(1f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }


        i++;if(s==i) { // V
            int nbRepeat = 3;
            script.repeatThisStep(nbRepeat);
            float iter = nbRepeat - script.getRemainingRepeatSteps();
            float nbE = (iter > 0)? 2 : 1;
            for (int j = 0; j< nbE; j++) {
                boolean first = j == 0;
                float x = W*0.5f + (first? 1f : -1f) * iter * 19;
                float y = H*0.8f + iter * H * 0.05f;
                e = Drakol.spawn(); e.surviveMoveEnd = true;
                if (iter > 0) e.carryOrb = true;
                else e.carryPowerUp(BonusType.TIME_WARP);
                LocalMoveFacePlayer.applyTo(e).speed(6f);
                WorldMoveSmooth.applyTo(e).speed(2f).pt0(x, H*1.5f).pt1(x, y).smoother.setInterpolator(Interpolator.BACK_END).setBack(3.0f);
                FireCyclic.applyTo(e).delay(1f).shoot(5, 0.15f, 1.28f).speed(190f).pulse(120, true, 60f).offsets(-3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(1f).shoot(5, 0.15f, 1.28f).speed(190f).pulse(-120, true, 60f).offsets(3, 4f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(1f).shoot(5, 0.15f, 1.28f).speed(190f).pulse(120, true, 90f).offsets(-3, -10f).bullet(Type.SHOTC);
                FireCyclic.applyTo(e).delay(1f).shoot(5, 0.15f, 1.28f).speed(190f).pulse(-120, true, 90f).offsets(3, -10f).bullet(Type.SHOTC);
            }
//            if (iter > 0) 
//                e = Drakol.spawn(); e.posX = W*0.5f + iter * 10; e.posY = H * 1.1f; e.clearWhenLeaveScreen = true; e.setDirAngle(-90f); e.moveSpeed = 40f;
            script.wait(0.2f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        

        i++;if(s==i) { // Nice bullet flood - harmless
            script.repeatThisStep(3);
            for (int j = 0; j < 2; j++)
            {
                e = Byrol.spawn(); e.surviveMoveEnd = false; LocalMoveDefault.applyTo(e);
                boolean first = j==0; float x = first? 10 : 70;
                LocalMoveSinusoidalRotation.applyTo(e).speed(2f).amplitude(0, 0, 10f).idle(0, 0, first? 15 : -15).phase(!first? MathUtil.HALF_PI : MathUtil.HALF_PI + MathUtil.PI);
                WorldMoveSinusoidal.applyTo(e).speed(0.2f).vertical(true).nbOsc(1.5f + (first?0:0.8f)).pt0(x, H+10 + (first?0:70f)).pt1(x+ 30, -5);
                FireCyclic.applyTo(e).delay(0.5f).shoot(20, 0.1f, 1f).speed(70f).angle(-20, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(0.5f).shoot(20, 0.1f, 1f).speed(70f).angle(20, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
             }
            script.wait(2f);
        }
        
        i++;if(s==i) {
            ArenaState.instance.displayUnlockLevelDialog();
        }
        
        i++;if(s==i) {
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = 50; e.posY = H + 10f; e.movY = -1f; e.moveSpeed = 90f; e.carryPowerUp(BonusType.TIME_WARP);
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = 20; e.posY = H + 10f; e.movY = -1f; e.moveSpeed = 120f;
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = 80; e.posY = H + 10f; e.movY = -1f; e.moveSpeed = 100f;
            script.wait(1f);
        }

        
        
        i++;if(s==i) { // Nice bullet flood - harmless
            script.repeatThisStep(3);
            if (script.getRemainingRepeatSteps() == 2) {
                e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = 50; e.posY = H + 10f; e.movY = -1f; e.moveSpeed = 90f;
                e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = 20; e.posY = H + 10f; e.movY = -1f; e.moveSpeed = 120f;
                e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = 80; e.posY = H + 10f; e.movY = -1f; e.moveSpeed = 100f;
            }
            for (int j = 0; j < 2; j++)
            {
                e = Byrol.spawn(); e.clearWhenLeaveScreen = true; LocalMoveDefault.applyTo(e);
                boolean first = j==0; float x = first? 20 : 80;
                e.rotDirZ = 1f; e.rotationSpeed = 150f;
                e.movY = -1; e.moveSpeed = 50f; e.posX = x; e.posY = H+10 + (first?0:70f);
                FireCyclic.applyTo(e).delay(0.5f).shoot(20, 0.1f, 5f).speed(70f).angle(-20, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(0.5f + (first?0:0.5f)).shoot(20, 0.1f, 5f).speed(70f).angle(20, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
             }
            script.wait(2f);
        }
        
        i++;if(s==i) {
            script.wait(3f);
        }
        
        i++;if(s==i) {
            e = Crystol.spawn(); e.surviveMoveEnd = true; e.lifeRemaining *= 2.5f;
            e.carryOrb = true;
            //e.carryPowerUp(BonusType.HELLFIRE);
            LocalMoveFacePlayer.applyTo(e).speed(6f);
            WorldMoveBezierQuadratic.applyTo(e).speed(1f).pt0(W+30, H/2).pt1(80, H).pt2(W*0.5f, H-20);
            FireSpiral.applyTo(e).branches(6).angleSpeed(150f).delay(1.0f).shoot(6, 0.05f, 0.8f).speed(35f).offsets(0, 4f).bullet(Type.SHOTA);
        }
        
        i++;if(s==i){
            script.repeatThisStep(20);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(W+30, H+30).pt1(W/2, 30).pt2(-30, H+30);
            script.wait(0.2f);
        }
        
        i++;if(s==i) {
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
            bgSpeed.setTarget(10, 2f);
        }
        
        //Boss
        i++;if(s==i) {
            BossC boss = BossC.spawn();
            boss.posX = -500f; boss.posY = -500f;
            boss.scriptInterpreter.setScript(BossScript.instance);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { //Boss dead - Explosions
            
            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=BossC.lastAliveX; ex.posY = BossC.lastAliveY;
            ex.setup(1.5f, 20f, 500f, 1f, 0f, 0.5f);
            ex.setColor(1f, 1f, 198f/255f,  1f, 149f/255f, 38f/255f);
            
            Explosion.setSlowExplosions(false);
            int total = 6;
            e = Explosion.spawn(); e.setDimension(90, 90); e.posX = BossC.lastAliveX; e.posY = BossC.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossC.lastAliveX + 14 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossC.lastAliveY + 14 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossC.lastAliveX + 5 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossC.lastAliveY + 5 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(200, 200); 
            e.posX = BossC.lastAliveX;
            e.posY = BossC.lastAliveY;
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }        
        
        i++;if(s==i) { 
            int total = 6;
            Explosion.setSlowExplosions(true);
            e = Explosion.spawn(); e.setDimension(120, 120); e.posX = BossC.lastAliveX; e.posY = BossC.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(120, 120); 
                e.posX = BossC.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossC.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(220, 220); 
            e.posX = BossC.lastAliveX;
            e.posY = BossC.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        
        
        i++;if(s==i) { 
            int total = 6;
            e = Explosion.spawn(); e.setDimension(150, 150); e.posX = BossC.lastAliveX; e.posY = BossC.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(150, 150); 
                e.posX = BossC.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossC.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(250, 250); 
            e.posX = BossC.lastAliveX;
            e.posY = BossC.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) {
            UI.hideLifeBar();
            bgSpeed.init(bgSpeed.get());
            bgSpeed.setTarget(15, 2f);
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
            ArenaState.instance.goBackToTitle();
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
            BossC e = (BossC) script.entity;
            
            //Intro
            i++;if(s==i) {
                
                ShotC.settings.width = ShotC.settings.height = 6f;
                ShotC.settings.zoomTarget = 3f;
                ShotC.settings.attackPower = 100;
                
                e.surviveMoveEnd = true;
                //e.lifeRemaining = 200;
                //WorldMoveSmooth.applyTo(e).speed(1.2f).pt0(30, H+30).pt1(W/2f, H*0.70f);
                e.rotationY.setInterpolator(Interpolator.QUADRATIC_START_END);
                e.rotationY.init(0f);
                e.rotationY.setTarget(-90, 3.5f); 
                WorldMoveBezierQuadratic.applyTo(e).speed(0.4f).pt0(W+30, H + 30).pt1(-10, 60).pt2(W* 0.2f, H * 0.8f);
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
                applyUltraBeam(e, 100, false, 0);
                script.wait(1f);
            }
            
            //Move to the opposite side of the screen 
            i++;if(s==i) {
                e.clearFireBehaviors();
                if (e.lifeRemaining / (float)e.lifeStart > 0.75f ) { //LOOP
                    float x = (e.posX > Screen.ARENA_WIDTH * 0.5f)? W * 0.2f : W * 0.8f;
                    WorldMoveSmooth.applyTo(e).speed(1.5f).pt0(e.posX, e.posY).pt1(x, e.posY).interp(Interpolator.QUADRATIC_START_END);
                    script.wait(1.5f);
                    script.step -= 2;
                }
            }
            
            i++;if(s==i) {
                WorldMoveSmooth.applyTo(e).speed(2f).pt0(e.posX, e.posY).pt1(W * 0.5f, e.posY).interp(Interpolator.QUADRATIC_START_END);
                e.rotationY.setTarget(0, 1f);
                script.wait(2f);
            }

            i++;if(s==i) {
                //Flat rotating weapons
                
                
                ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                ex.setup(1.3f, 50f, 250f, 0.8f, 0.0f, 0.0f);
                ex.setColor(0.5f, 0, 1,  0.8f, 0.8f, 1f);
                
                applyFlatWeapons(e);
                script.wait(0.2f);
            }
            
            i++;if(s==i) {
                ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                ex.setup(1.3f, 50f, 250f, 0.8f, 0.0f, 0.0f);
                ex.setColor(0.5f, 0, 1,  0.8f, 0.8f, 1f);
                script.wait(0.2f);
            }
            
            i++;if(s==i) {
                ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                ex.setup(1.3f, 50f, 250f, 0.8f, 0.0f, 0.0f);
                ex.setColor(0.5f, 0, 1,  0.8f, 0.8f, 1f);
                script.wait(4.6f);
            }
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                if (e.lifeRemaining / (float)e.lifeStart > 0.5f) { //LOOP
                    script.step -= 4;
                    script.wait(2f);
                }
            }
            
            i++;if(s==i) {
                //Stop to position vertically
                e.clearFireBehaviors();
                script.wait(1f);
            }
            
            i++;if(s==i) {
                //Position vertical
                e.rotationY.setTarget(-90, 2f);
                script.wait(1f);
            }
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                applyCloudBullet(e);
                script.wait(2f);
            }


            i++;if(s==i) {
                if (e.lifeRemaining / (float)e.lifeStart > 0.15f) { //LOOP
                    script.step -= 2;
                }
            }
            
            i++;if(s==i) {
                //Reposition horizontally
                e.clearFireBehaviors();
                e.rotationY.setTarget(0, 2f);
                script.wait(1f);
            }
            
            
            i++;if(s==i) {
                e.rotationSpeedZ.setTarget(290f, 100);
                boolean playerAtRight = Engine.player.posX > Screen.ARENA_WIDTH * 0.5f;
                boolean onTop = e.posY > Screen.ARENA_HEIGHT * 0.5f;
                e.clearFireBehaviors();
                
                float angle = 0;
                if (playerAtRight && onTop) angle = -50;
                else if (!playerAtRight && onTop) angle = 180 + 50;
                else if (playerAtRight && !onTop) angle = 50;
                else if (!playerAtRight && !onTop) angle = 180 - 50;
               
                applyUltraBeam(e, 20, true, angle);
                applyRadialBullet(e, angle);

                //Add a lateral firing with timer
                int armNb = 5; float angOpen = 45;
                for (int j = 0; j < armNb; j++) {
                    float ang = -angOpen + j * angOpen*2 / (armNb - 1);
                    FireCyclic.applyTo(e).shoot(20, 0.034f, 99).delay(1.5f).speed(90f).offsets(0, 0).angle((playerAtRight ? 180 : 0) + ang, false).bullet(j%2 == 1? Type.SHOTC : Type.SHOTB);
                }
                
                ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=e.posX; ex.posY = e.posY;
                ex.setup(0.6f, 60f, 110f, 0.2f, 0.0f, 0.0f);
                ex.setColor(0f, 1f, 1,  0.8f, 0.8f, 1f);
                
                script.wait(1.3f);
            }
            
            i++;if(s==i) {
                boolean onTop = e.posY > Screen.ARENA_HEIGHT * 0.5f;
                WorldMoveSmooth.applyTo(e).speed(1.5f).pt0(e.posX, e.posY).pt1(e.posX, onTop? H * 0.2f : H * 0.8f).interp(Interpolator.QUADRATIC_START_END);
                script.wait(1.5f);
            }
            
            i++;if(s==i) {
                script.step -= 3;
            }
            
            i++;if(s==i) {
                script.wait(200f);
            }
            
          //----- End of the script -----
          //  i++;if(s==i) {
          //      script.step -= 1; //loop
          //  }
            
        }
        
        public static void applyFlatWeapons(Entity e) {
            e.clearFireBehaviors();
            float off = 17f;
            for (int i = 0; i < 4; i++) {
                float x = 0, y = 0, ang = 0;
                if (i == 0) { x =  off; y = off;  ang = 0;  }
                if (i == 1) { x = -off; y = off;  ang = 90; }
                if (i == 2) { x =  off; y = -off; ang = -90;}
                if (i == 3) { x = -off; y = -off; ang = 180;}
                FireCyclic.applyTo(e).shoot(9999, 0.08f, 0.03f).speed(80f).offsets(x, y).angle(ang, true).bullet( i == 0 || i == 3? Type.SHOTB : Type.SHOTC);
            }
            int nbShot = 5; float wait = 1f;
            FireCyclic.applyTo(e).shoot(nbShot, 0.04f, wait).delay(2f).speed(80f).offsets(0, 0).angle(-90, false).bullet(Type.SHOTLONG);
            FireCyclic.applyTo(e).shoot(nbShot, 0.05f, wait).delay(2f).speed(80f).offsets(0, 0).angle(-90 + 40, false).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).shoot(nbShot, 0.05f, wait).delay(2f).speed(80f).offsets(0, 0).angle(-90 - 40, false).bullet(Type.SHOTB);
            /*
            for (int i = 0; i < 2; i++) {
                FireSinusoidal.applyTo(e).sinus(10f, 7f).phase(i == 0? 0 : MathUtil.PI).delay(0.0f).shoot(nbShot, 0.05f, wait).speed(80f).angle(-90, false).offsets(0f, 0f).bullet(Type.SHOTB);
            }
            */
            
        }
        
        public static void applyUltraBeam(Entity e, int nbShot, boolean useAngle, float angle) {
            float wait = 99f; float ang = FireCyclic.TARGET_PLAYER;
            ang = MathUtil.radiansToDegrees * MathUtil.getAngleBetween(e.posX, e.posY, 0f, Engine.player.posX, Engine.player.posY);
            if (useAngle) ang = angle;
            FireCyclic.applyTo(e).shoot(nbShot, 0.034f, wait).speed(90f).offsets(0, 0).angle(ang, false).bullet(Type.SHOTC);
            for (int i = 0; i < 2; i++) {
                FireSinusoidal.applyTo(e).sinus(10f, 7f).phase(i == 0? MathUtil.HALF_PI : -MathUtil.HALF_PI).delay(0.0f).shoot(nbShot, 0.034f, wait).speed(90f).angle(ang, false).offsets(0f, 0f).bullet(Type.SHOTB);
            }
        }
        
        public static void applyCloudBullet(Entity e) {
            int nb = 17; float halfAngle = 60;
            for (int i = 0; i < nb; i++) {
                float angle = 90f - halfAngle + i * (2*halfAngle)/(nb -1);
                FireCyclic.applyTo(e).delay(0.2f).shoot(8, 0.15f, 3.5f).speed(150f).pulse(angle, false, 100f).offsets(0f, 0f).bullet( (i%2 == 0)? Type.SHOTB : Type.SHOTC);
            }
        }
        
        public static void applyRadialBullet(Entity e, float angle) {
            int nb = 8; float halfAngle = 40;
            for (int i = 0; i < nb; i++) {
                if (i >= 3 && i <= 3) continue;
                float ang = angle - halfAngle + i * (2*halfAngle)/(nb -1);
                FireCyclic.applyTo(e).shoot(13, 0.034f, 99f).delay(0.3f).speed(110f).offsets(0, 0).angle(ang, false).bullet(Type.SHOTC);
            }
        }
        
    }
    
}
