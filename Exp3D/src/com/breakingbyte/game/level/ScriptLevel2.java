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
import com.breakingbyte.game.entity.enemy.boss.BossB;
import com.breakingbyte.game.entity.fire.Fire.Type;
import com.breakingbyte.game.entity.fire.FireCyclic;
import com.breakingbyte.game.entity.fire.FireSinusoidal;
import com.breakingbyte.game.entity.fire.FireSpiral;
import com.breakingbyte.game.entity.group.AstrolRing;
import com.breakingbyte.game.entity.move.LocalMoveDefault;
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
import com.breakingbyte.game.ui.UI;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;


public class ScriptLevel2 implements Script {

    public static ScriptLevel2 instance = new ScriptLevel2();
    
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
            script.step = 35;
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
        
        
        
        i++;if(s==i){ // 2 easy targets
            float posX = -10f, posY = H * 1.1f, speed = 70f, angle = -48f; 
            e = Astrol.spawn(); e.posX = posX; e.posY = posY; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e.carryOrb = true;
            e = Astrol.spawn(); e.posX = posX + 1f; e.posY = posY - 13f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = posX - 15f; e.posY = posY - 6f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            
            posX = -10f; posY = H * 1.4f; speed = 70f; angle = -45f; 
            e = Astrol.spawn(); e.posX = posX; e.posY = posY; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = posX + 1f; e.posY = posY - 13f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            e = Astrol.spawn(); e.posX = posX - 15f; e.posY = posY - 6f; e.setDirAngle(angle); e.moveSpeed = speed; e.clearWhenLeaveScreen = true;
            
            script.waitAllEnemiesGone();
        }
        
        
        i++;if(s==i) {
            script.repeatThisStep(15); 
            e = Astrol.spawn(); e.posX = W; e.posY = H * 1.1f; e.rotDirY = 0.25f; e.rotY = 90f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(W, H*1.1f).pt1(-40, H*0.5f).pt2(W/2, H-H*1.1f);
            e.carryOrb = script.getRemainingRepeatSteps() < 8 && script.getRemainingRepeatSteps() % 2 == 1; 
            e = Astrol.spawn(); e.posX = 0; e.posY = H * 1.1f; e.rotDirY = -0.25f; e.rotY = -90f;
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(0, H*1.1f).pt1(W+40, H*0.5f).pt2(W/2, H-H*1.1f);
            
            script.wait(0.23f);
        }
        
        i++;if(s==i) {
            for (int j = 0; j < 3; j++) {
                e = Byrol.spawn(); e.surviveMoveEnd = true;
                if (j == 1) e.carryPowerUp(BonusType.TIME_WARP);
                if (j == 0) e.carryOrb = true;
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.3f, 1.5f).speed(50f).offsets(0f, 4f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                if (j <= 1) WorldMoveSmooth.applyTo(e).speed(2f).pt0(j == 0? -10f : W+10f, 20f).pt1(j==0? 20f:W-20, H*0.8f).interp(Interpolator.QUADRATIC_END);
                else WorldMoveSmooth.applyTo(e).speed(2f).pt0(W*0.5f, H*1.5f).pt1(W* 0.5f, H*0.9f).smoother.setInterpolator(Interpolator.BACK_END).setBack(2.7f);
            }
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) {
            script.repeatThisStep(2);
            float centerX = 50f, centerY = H*0.6f, radius1 = 90f, radius2 = 35f;
            float angleBegin = 10, angleEnd = 170;
            int nb = 9;
            for (int j = 0; j < nb; j++) {
                e = Astrol.spawn(); 
                float pt0X = MathUtil.getCirclePosition(centerX, centerY, radius1, angleBegin, angleEnd, j, nb, true), 
                      pt0Y = MathUtil.getCirclePosition(centerX, centerY, radius1, angleBegin, angleEnd, j, nb, false), 
                      pt1X = MathUtil.getCirclePosition(centerX, centerY, radius2, angleBegin, angleEnd, j, nb, true), 
                      pt1Y = MathUtil.getCirclePosition(centerX, centerY, radius2, angleBegin, angleEnd, j, nb, false);
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1f).pt0(pt0X, pt0Y).pt1(pt1X, pt1Y).interp(Interpolator.QUADRATIC_START_END)  )
                    .append( WorldMoveFollowPlayer.newInstance().speed(10f, 120f, Interpolator.QUADRATIC_START, 0.3f) )
                    ;
            }
            script.wait(1.3f);
        }
        
        i++;if(s==i) {
            script.repeatThisStep(4);
            boolean right = (script.getRemainingRepeatSteps() % 2 == 0);
            float centerX = right? 60f : 40f, centerY = H*0.6f, radius1 = 90f, radius2 = 35f;
            float angleOpening = 80f;
            float angleBegin = right? 20 : 90, angleEnd = angleBegin + angleOpening;
            int nb = 4;
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
            script.wait(0.7f);
        }
        
        
        i++;if(s==i) { //Bounce-Back of 5 Byrols
            int nb = 5;
            script.repeatThisStep(nb);
            float x = (float) (W - script.getRemainingRepeatSteps() * W / nb + 0.5f * W / nb);
            e = Byrol.spawn(); e.surviveMoveEnd = true;
            e.carryOrb = script.getRemainingRepeatSteps() == 3 || script.getRemainingRepeatSteps() == 5;
            if (script.getRemainingRepeatSteps() == 1) e.carryPowerUp(BonusType.HELLFIRE);
            //if (script.getRemainingRepeatSteps() == nb -1) e.carryOrb = true;
            WorldMoveSmooth.applyTo(e).speed(2f).pt0(x, H*1.5f).pt1(x, H*0.8f).smoother.setInterpolator(Interpolator.BACK_END).setBack(3.0f);
            FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.08f, 1.9f).speed(50f).offsets(0f, 4f).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(1.8f).shoot(4, 0.08f, 1.9f).speed(50f).offsets(0f, 4f).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            
            script.wait(0.3f);
        }
        
        i++;if(s==i) {
            script.wait(4f);
        }

        i++;if(s==i) {
            script.repeatThisStep(2);
            int nb = 5;
            for (int j = 0; j < nb; j++) {
                float x = (float) (W - j * W / nb + 0.5f * W / nb);
                e = Astrol.spawn(); e.surviveMoveEnd = true;
                WorldMoveSequence.applyTo(e)
                .append( WorldMoveSmooth.newInstance().speed(1f).pt0(x, H*1.1f).pt1(x, H*0.9f).interp(Interpolator.QUADRATIC_START_END)  )
                .append( WorldMoveFollowPlayer.newInstance().speed(10f, 120f, Interpolator.QUADRATIC_START, 0.3f) )
                ;
            }
            script.wait(1.5f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { //Many Astrol rings
            script.repeatThisStep(4);
            AstrolRing astrolC = AstrolRing.newInstance();  
            astrolC.BRANCH_ROTATION_SPEED = -3f;
            astrolC.posX = 50; astrolC.posY = H*0.5f;
            astrolC.populateChildren(10);
            astrolC.registerInLayer();
            script.wait(1.5f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }

        i++;if(s==i){ // Sinusoidal "come-back" moves
            script.repeatThisStep(3);
            e = Astrol.spawn(); e.carryOrb = true;
            WorldMoveSinusoidal.applyTo(e).speed(0.2f).vertical(false).nbOsc(1.5f).pt0(0, H+10).pt1(W * 0.6f, -5);
            e = Astrol.spawn(); e.carryOrb = true;
            WorldMoveSinusoidal.applyTo(e).speed(0.2f).vertical(false).nbOsc(1.5f).pt0(W, H+10).pt1(W * 0.6f, -5);
            script.wait(0.6f);
        }
        
        i++;if(s==i) {
            script.wait(2f);
        }
        

        
        i++;if(s==i){ // 2 Crystols S-firing

            for (int j = 0; j < 2; j++) {
                e = Crystol.spawn(); e.posX = 30; e.posY = 140f; e.carryOrb = true;
                FireSinusoidal.applyTo(e).sinus(8, 8f).delay(0f).shoot(20, 0.09f, 1f).speed(70f).angle(0, true).bullet(Type.SHOTA);
                //FireSinusoidal.applyTo(e).sinus(8, 8f).phase(MathUtil.PI).delay(1.8f).shoot(1, 0f, 0.09f).speed(70f).angle(0, true).bullet(Type.SHOTA);
                WorldMoveSequence.applyTo(e)
                .append( WorldMoveSmooth.newInstance().speed(1.5f).pt0((j==0?-10:W+10), H*0.3f).pt1(W * (j==0?0.25f:0.75f), H*0.8f).interp(Interpolator.QUADRATIC_START_END) )
                .append( WorldMoveSmooth.newInstance().speed(12f).pt0(W * (j==0?0.25f:0.75f), H*0.8f).pt1(W * (j==0?0.25f:0.75f), H*1.1f).interp(Interpolator.QUADRATIC_START) );
            }
            script.wait(2.0f);
        }
        
        i++;if(s==i){ // Attack a little in the middle
            script.repeatThisStep(4);
            e = Astrol.spawn(); e.clearWhenLeaveScreen = true; e.posX = W * 0.5f; e.posY =  H*1.1f; e.movY = -1f; e.moveSpeed = 50f;
            script.wait(1.5f);
        }
        

        
        i++;if(s==i) {
            //script.wait(0.5f);
        }
        
        
        for (int _j = 0; _j < 4; _j++) { 
            // v v
            //  v     attack of Byrols
            
            float mainX = (_j % 2 == 0)? W * 0.3f : W * 0.7f;
            float rot = (_j % 2 == 0)? -10 : 10;
        
            i++;if(s==i) {
                script.repeatThisStep(2); 
                if (!script.isLastStepOfRepeat()) {
                    Entity ent = ShowCatalog.byrolVerticalWaitFireGoDown(mainX, H * 0.7f);
                    ent.rotY = rot;
                    //ent.carryOrb = true;
                    script.wait(1f);
                } else {
                    for (int j = 0; j < 2; j++) {
                        Entity ent = ShowCatalog.byrolVerticalWaitFireGoDown(mainX + 12 * (j==0? -1 : 1), H * 0.8f);
                        ent.rotY = rot;
                        ent.carryOrb = ((_j % 2 == 0) && (j == 1) || ((_j % 2 == 1) && (j == 0)));
                    }
                    script.wait(1.5f);
                }
            }
        
        }
        
        i++;if(s==i) { //Shower of astrols
            script.repeatThisStep(8);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(-5, H*0.5f).pt1(W*1.2f, H*1.3f).pt2(W * 0.6f, -0.1f);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(W+5, H*0.5f).pt1(W - W*1.2f, H*1.3f).pt2(W * 0.4f, -0.1f);
            script.wait(0.15f);
        }
        
        i++;if(s==i) { // Nice bullet flood - harmless
            for (int j = 0; j < 2; j++)
            {
                e = Byrol.spawn(); e.surviveMoveEnd = true; LocalMoveDefault.applyTo(e);  e.carryOrb = true;
                boolean first = j==0;
                LocalMoveSinusoidalRotation.applyTo(e).speed(2f).amplitude(0, 0, 10f).idle(0, 0, first? 15 : -15).phase(!first? MathUtil.HALF_PI : MathUtil.HALF_PI + MathUtil.PI);
                WorldMoveSmooth.applyTo(e).speed(1f).pt0(first? W*0.1f : W*0.9f, H*1.1f).pt1(first? W * 0.3f : W*0.7f, H * 0.9f).interp(Interpolator.QUADRATIC_END);
                FireCyclic.applyTo(e).delay(0.5f).shoot(20, 0.1f, 1f).speed(70f).angle(-20, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
                FireCyclic.applyTo(e).delay(0.5f).shoot(20, 0.1f, 1f).speed(70f).angle(20, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
             }
            script.wait(3f);
        }

        
        i++;if(s==i) { //A little asymmetry
            script.repeatThisStep(3);
            e = Astrol.spawn();
            WorldMoveBezierQuadratic.applyTo(e).speed(0.5f).pt0(-W* 0.1f, H*0.6f).pt1(W*0.5f, H*0.7f).pt2(W * 0.5f, H);
            script.wait(0.5f);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { 
            
            float margin = 10;
            float totalWidth = W - 2*margin;            
            int xNb = 5;
            int yNB = 3; float Y_INC = 16f;
            
            float DELAY_INC = 0.08f;
            float delay = 0;
            
            boolean goRight = false;
            float lineY = H * 0.7f;
            
            for (int line = 0; line < yNB; line++)
            {
                for (int ind = 0; ind <= xNb; ind++) {
                    int ind2 = goRight? ind : xNb - ind;
                    float posX = margin + (totalWidth) / xNb * ind2;
                    e = Astrol.spawn(); //e.surviveMoveEnd = true;
                    e.carryOrb = line == yNB - 1;
                    WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(1.0f).pt0(posX, H * 1.1f).pt1(posX, lineY).interp(Interpolator.QUADRATIC_END).delay(delay) )
                    .append( WorldMoveWait.newInstance().duration(1.0f) )
                    .append( WorldMoveFollowPlayer.newInstance().speed(10f, 120f, Interpolator.QUADRATIC_START, 0.3f));
                    
                    delay += DELAY_INC;
                }
                
                lineY += Y_INC;
                goRight = !goRight;
            }
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { // 2 Byrols with continuous fire
            script.repeatThisStep(2);
            boolean first = !script.isLastStepOfRepeat();
            e = Byrol.spawn(); e.clearWhenLeaveScreen = true; e.rotZ = 90f; e.lifeRemaining = (int) (e.lifeRemaining * 1.5f);
            e.posX = first? W * 0.2f : W * 0.8f; e.posY = H*1.1f; e.movY = -1f; e.moveSpeed = 30f;
            float angle = (first? -1 : 1) * 20;
            FireCyclic.applyTo(e).delay(0.05f).shoot(1, 0f, 0.05f).speed(80f).angle(angle, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            FireCyclic.applyTo(e).delay(0.05f).shoot(1, 0f, 0.05f).speed(80f).angle(angle, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            script.wait(1.5f);
            if (first) e.carryPowerUp(BonusType.SUPER_SHIELD);
            else e.carryOrb = true;
        }
        
        i++;if(s==i) { 
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { 
            int nbToRepeat = 5; 
            script.repeatThisStep(nbToRepeat);
            boolean left = script.getRemainingRepeatSteps() % 2 == 0;
            float finalX = left? 4f : W - 4f;
            float startX = left? W + 10f : -10f;
            e = Crystol.spawn(); e.rotZ = left? 90f : -90f; //e.clearWhenLeaveScreen = true;
            e.carryOrb = script.getRemainingRepeatSteps() > 1;
            e.movY = -1; e.moveSpeed = 1f * 13.7f; e.clearWhenLeaveScreen = true;
            WorldMoveSmooth introMove = WorldMoveSmooth.newInstance().speed(1.5f).pt0(startX, H * 0.9f).pt1(finalX, H * 0.9f).interp(Interpolator.BACK_END).delay(0);
            introMove.smoother.setBack(1.1f);
            WorldMoveSequence.applyTo(e)
            .append( introMove )
            //.append( WorldMoveSmooth.newInstance().speed(6f).pt0(finalX, H * 0.9f).pt1(finalX, H * 0.9f - 150).interp(Interpolator.LINEAR_TIMED).delay(0));
            .append( WorldMoveDefault.newInstance() );
            
            FireCyclic.applyTo(e).delay(1.5f).shoot(30, 0.06f, 0.7f).speed(60f).angle(left ? -27f : 180 + 27f, false).offsets(-6f, 4f).bullet(Type.SHOTA);
            FireCyclic.applyTo(e).delay(1.5f).shoot(30, 0.06f, 0.7f).speed(60f).angle(left ? -27f : 180 + 27f, false).offsets(6f, 4f).bullet(Type.SHOTA);
            //FireSinusoidal.applyTo(e).sinus(10f, 1f).           delay(1.5f).shoot(30, 0.06f, 0.7f).speed(60f).angle(left ? -27f : 180 + 27f, false).offsets(0f, 4f).bullet(Type.SHOTA);
            //FireSinusoidal.applyTo(e).sinus(10f, 1f).phase(10f).delay(1.5f).shoot(30, 0.06f, 0.7f).speed(60f).angle(left ? -27f : 180 + 27f, false).offsets(0f, 4f).bullet(Type.SHOTA);
            
            if (!left) {
                e = Astrol.spawn(); e.posX = 20f; e.posY = H + 10f; e.clearWhenLeaveScreen = true; e.movY = -1; e.moveSpeed = 50f;
                e = Astrol.spawn(); e.posX = 60f; e.posY = H + 10f; e.clearWhenLeaveScreen = true; e.movY = -1; e.moveSpeed = 20f;
                e = Astrol.spawn(); e.posX = 40f; e.posY = H + 10f; e.clearWhenLeaveScreen = true; e.movY = -1; e.moveSpeed = 70f;    
            } else {
                e = Astrol.spawn(); e.posX = 80f; e.posY = H + 10f; e.clearWhenLeaveScreen = true; e.movY = -1; e.moveSpeed = 50f;
            }
            
            script.wait(2f);
        }
        
        i++;if(s==i) {
            ArenaState.instance.displayUnlockLevelDialog();
        }
        
        i++;if(s==i) {
            script.wait(2f);
        }
        
        i++;if(s==i) { // 2 Byrols with crossed fire
            script.repeatThisStep(2);
            boolean first = !script.isLastStepOfRepeat();
            e = Byrol.spawn(); e.clearWhenLeaveScreen = true; e.rotZ = 0f; e.lifeRemaining = (int) (e.lifeRemaining * 1.5f);
            e.carryOrb = true;
            LocalMoveDefault.applyTo(e);
            float angle = first? -60 : 180+60;
            e.posX = first? W * 0.2f : W * 0.8f; e.posY = H*1.1f; 
            e.movX = (float)Math.cos(angle*MathUtil.degreesToRadians); 
            e.movY = (float)Math.sin(angle*MathUtil.degreesToRadians);
            e.rotZ = (first? -50 : 180+50)  + 90;
            e.moveSpeed = 40f;
            //FireCyclic.applyTo(e).delay(0.05f).shoot(1, 0f, 0.05f).speed(80f).angle(angle, true).offsets(-FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            //FireCyclic.applyTo(e).delay(0.05f).shoot(1, 0f, 0.05f).speed(80f).angle(angle, true).offsets(FireCyclic.BYROL_LATERAL, 4f).bullet(Type.SHOTB);
            float sp = 1.6f;
            FireSinusoidal.applyTo(e).sinus(10f, 5f).                   delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(angle, false).offsets(0f, 4f).bullet(Type.SHOTB);
            FireSinusoidal.applyTo(e).sinus(10f, 5f).phase(MathUtil.PI).delay(0.5f).shoot(30, 0.06f, 0.7f).speed(sp * 60f).angle(angle, false).offsets(0f, 4f).bullet(Type.SHOTB);
            
            script.wait(1.5f);
        }
        
        i++;if(s==i) {
            e = Astrol.spawn(); e.posX = 20f; e.posY = H + 10f; e.clearWhenLeaveScreen = true; e.movY = -1; e.moveSpeed = 50f;
            e = Astrol.spawn(); e.posX = 60f; e.posY = H + 10f; e.clearWhenLeaveScreen = true; e.movY = -1; e.moveSpeed = 20f;
            e = Astrol.spawn(); e.posX = 40f; e.posY = H + 10f; e.clearWhenLeaveScreen = true; e.movY = -1; e.moveSpeed = 70f;    
        }
        
        i++;if(s==i) {
            //script.step = -1;
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
        
        i++;if(s==i) {
            BossB boss = BossB.spawn();
            boss.posX = -100f; boss.posY = -100f;
            boss.scriptInterpreter.setScript(BossScript.instance);
        }
        
        i++;if(s==i) {
            script.waitAllEnemiesGone();
        }
        
        i++;if(s==i) { //Boss dead - Explosions
            
            ExplosionFireRing ex = ExplosionFireRing.spawn(); ex.posX=BossB.lastAliveX; ex.posY = BossB.lastAliveY;
            ex.setup(1.5f, 20f, 500f, 1f, 0f, 0.5f);
            ex.setColor(1f, 1f, 198f/255f,  1f, 149f/255f, 38f/255f);
            
            Explosion.setSlowExplosions(false);
            int total = 6;
            e = Explosion.spawn(); e.setDimension(90, 90); e.posX = BossB.lastAliveX; e.posY = BossB.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossB.lastAliveX + 14 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossB.lastAliveY + 14 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(90, 90); 
                e.posX = BossB.lastAliveX + 5 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossB.lastAliveY + 5 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(200, 200); 
            e.posX = BossB.lastAliveX;
            e.posY = BossB.lastAliveY;
            script.wait(0.25f);
            
            AudioManager.playExplosion();
        }        
        
        i++;if(s==i) { 
            int total = 6;
            Explosion.setSlowExplosions(true);
            e = Explosion.spawn(); e.setDimension(120, 120); e.posX = BossB.lastAliveX; e.posY = BossB.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(120, 120); 
                e.posX = BossB.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossB.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(220, 220); 
            e.posX = BossB.lastAliveX;
            e.posY = BossB.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        
        
        i++;if(s==i) { 
            int total = 6;
            e = Explosion.spawn(); e.setDimension(150, 150); e.posX = BossB.lastAliveX; e.posY = BossB.lastAliveY;
            for (int j = 0; j < total; j++) {
                e = Explosion.spawn(); e.setDimension(150, 150); 
                e.posX = BossB.lastAliveX + 15 * (float)Math.cos(MathUtil.TWO_PI / total * j);
                e.posY = BossB.lastAliveY + 15 * (float)Math.sin(MathUtil.TWO_PI / total * j);
            }
            script.wait(0.1f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) { //More explosions
            e = Explosion.spawn(); e.setDimension(250, 250); 
            e.posX = BossB.lastAliveX;
            e.posY = BossB.lastAliveY;
            script.wait(0.25f);
            AudioManager.playExplosion();
        }
        
        i++;if(s==i) {
            UI.hideLifeBar();
            bgSpeed.init(bgSpeed.get());
            bgSpeed.setTarget(20, 2f);
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
        
        //*********************************************************************************************************
        
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
            BossB e = (BossB) script.entity;
            
            i++;if(s==i) {
                e.surviveMoveEnd = true;
                e.setArmsImmuneToCollision(true);
                WorldMoveSmooth.applyTo(e).speed(2.3f).pt0(W*0.5f, H * 1.2f).pt1(W/2f, H*0.80f).interp(Interpolator.BACK_END).smoother.backAmplitude = 2f;
                
                e.makeEntrance();//shake(true);
                script.wait(2.5f);
            }
            
            i++;if(s==i) {
                WorldMoveDefault.applyTo(e);
                LocalMoveDefault.applyTo(e);
                e.setArmsImmuneToCollision(false);
                UI.displayBossLifeBar(true);
                
                //spawn arms
                //FireCyclic.applyTo(e.armLeft).delay(0.5f).shoot(21, 0.14635f, 1f).speed(40f).angle(0, true).offsets( 0f, 0f).bullet(Type.SHOTB);
                applyArmWeapons(e.armLeft, 55f);
                applyArmWeapons(e.armRight, -70f);
            }
            
            i++;if(s==i) {
                if (e.armLeft != null && e.armRight != null) script.stayInThisStep();
            }
            
            i++;if(s==i) {
                //One arm was killed
                boolean leftArmKilled = e.armLeft == null;
                Entity en = leftArmKilled? e.armRight : e.armLeft;
                en.clearFireBehaviors();
                FireSpiral.applyTo(en).branches(2).angleSpeed(90).delay(0.5f).shoot(10, 0.3f, 0.3f).speed(40f).offsets(0, 4f).bullet(Type.SHOTB);
                FireSpiral.applyTo(en).branches(2).angleSpeed(60).delay(0.5f).shoot(10, 0.3f, 0.3f).speed(40f).offsets(0, 4f).bullet(Type.SHOTB);
                if (e.armLeft != null && e.armRight != null) script.stayInThisStep();
            }
            
            i++;if(s==i) {
                if (e.armLeft != null || e.armRight != null) script.stayInThisStep();
            }
            
            i++;if(s==i) {
                script.wait(2f);
            }
            
            i++;if(s==i) {
                //Both arms killed
                for (int j = 0; j < 2; j++) {
                    FireCyclic.applyTo(e).delay(0.5f).shoot(10, 0.035f, 1.0f).speed(120f).offsets( 22f * (j==0? -1 : 1), -9f).bullet(Type.SHOTC);
                }
                FireCyclic.applyTo(e).delay(0.5f).shoot(10, 0.035f, 0.5f).speed(120f).offsets( 0, 15f).bullet(Type.SHOTC);
                script.wait(5f);
            }
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                float fov = 30;
                for (int j = 0; j < 5; j++) {
                    FireCyclic.applyTo(e).delay(0.5f).shoot(15, 0.015f, 5f).speed(180f).angle(-90f - fov*0.5f + fov / 4 * j, false).offsets( 0, 15f).bullet(Type.SHOTB);
                }
                script.wait(1.5f);
            }
            
            i++;if(s==i) {
                e.clearFireBehaviors();
                e.surviveMoveEnd = true;
                boolean slowCharge = (((float)e.lifeRemaining) / e.lifeStart > 0.3f);
                WorldMoveSequence.applyTo(e)
                    .append( WorldMoveSmooth.newInstance().speed(slowCharge? 0.8f : 0.5f).pt0(e.posX, e.posY).pt1(Engine.player.posX, 10).interp(Interpolator.QUADRATIC_START_END)  )
                    .append( WorldMoveSmooth.newInstance().speed(2f).pt0(Engine.player.posX, 10).pt1(e.posX, e.posY).interp(Interpolator.QUADRATIC_START_END)  );
                ;
                script.wait(1.5f);
            }
            
            
            i++;if(s==i) {
                //Both arms killed
                script.step -= 4; 
            }

          //----- End of the script -----
          //  i++;if(s==i) {
          //      script.step -= 1; //loop
          //  }
            
        }
        
        public static void applyArmWeapons(Entity e, float angleSpeed) {
            FireSpiral.applyTo(e).branches(2).angleSpeed(angleSpeed).delay(0.5f).shoot(10, 0.3f, 0.3f).speed(20f).offsets(0, 4f).bullet(Type.SHOTB);
            
        }
        
        @SuppressWarnings("unused")
        public static void applyRotateWeapons(BossB e, int number) {
            e.clearFireBehaviors();
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < number; k++) {
                    FireCyclic.applyTo(e).delay(0.5f).shoot(21, 0.14635f, 1f).speed(40f).angle(j==0?0:180, true).offsets( 0f, j==0?14f + 4*k - 1.5f : -14f - 4*k + 1.5f).bullet(Type.SHOTB);
                    FireCyclic.applyTo(e).delay(0.5f).shoot(21, 0.14635f, 1f).speed(40f).angle(j==0?0:180, true).offsets( 0f, j==0?14f + 4*k : -14f - 4*k).bullet(Type.SHOTA);
                }
                
            }
        }
        
        @SuppressWarnings("unused")
        public static void applyTranslateWeapons(BossB e, int number) {
            e.clearFireBehaviors();

            for (int k = 0; k < number; k++) {
                FireCyclic.applyTo(e).delay(0.5f).shoot(2, 0.5f, 1.5f).speed(60f).angle(0, true).offsets( 0f, 12f + 4*k).bullet(Type.SHOTLONG);
            }
                
        }
        
    }
    
}
