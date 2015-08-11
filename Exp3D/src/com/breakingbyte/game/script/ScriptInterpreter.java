package com.breakingbyte.game.script;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.wrap.shared.Timer;

public class ScriptInterpreter {
    
    /**
     * Script should be a simple stateless functor. 
     * Any state variable must be stored within the interpreter instance. 
     */
    public interface Script {
        public void runScript(ScriptInterpreter scriptInterpreter);
    }

    
    public int step; //current step
    private float waitTime; //how much to wait in seconds
    private boolean waitAllEnemiesGone;
    private boolean waitEntityMoveEnd; 
    
    private int repeatCurrentStep;
    
    public Entity entity;
    public Script script; //script to execute
    
    //Some registers the actual script might want to use
    public boolean b, b1, b2, b3;
    public int c, c1, c2, c3;
    public float f, f1, f2, f3;
    
    public ScriptInterpreter() {
        resetState();
    }
    
    public void resetState() {
        this.step = 0;
        this.waitTime = 0;
        this.waitAllEnemiesGone = false;
        this.waitEntityMoveEnd = false;
        repeatCurrentStep = 0;
    }
    
    public void setScript(Script script){
        this.script = script;
    }
    
    public void wait(float seconds) {
        waitTime += seconds;
    }
    
    public void waitAllEnemiesGone() {
        waitAllEnemiesGone = true;
    }
    
    public void waitEntityMoveEnd() {
        waitEntityMoveEnd = true;
    }
    
    public void repeatThisStep(int times) {
        if (repeatCurrentStep == 0) repeatCurrentStep = times;
    }
    
    public int getRemainingRepeatSteps() {
        return repeatCurrentStep;
    }
    
    public boolean isLastStepOfRepeat() {
        return repeatCurrentStep == 1;
    }
    
    public void stayInThisStep() {
        this.step -= 1;
    }
    
    public void updateRunScript() {
        
        if (waitTime > 0) waitTime -= Timer.delta;        
        if (waitTime > 0) return;
        
        if (waitAllEnemiesGone) {
            if (Engine.layer_enemies.isEmpty()) waitAllEnemiesGone = false;
            else return;
        }
        
        if (waitEntityMoveEnd) {
            if (!entity.couldMove) waitEntityMoveEnd = false;
            else return;
        }
        
        script.runScript(this);
        
        if (repeatCurrentStep > 0) repeatCurrentStep--;
        if (repeatCurrentStep <= 0) {
            step++;
            //Log.d(TAG, "Step " + step);
        }
    }
    

}


