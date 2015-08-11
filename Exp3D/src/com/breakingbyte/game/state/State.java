package com.breakingbyte.game.state;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public abstract class State {
    
    public enum StateId {
        LOADING_STATE,
        TITLE_STATE,
        ARENA_STATE,
    }

    public abstract State getInstance();
    
    public abstract void updateImpl();
    
    public abstract void renderImpl();
    
    public abstract void onEnterImpl(boolean isResume);
    
    public abstract void onLeaveCompleted();
    
    public void onLeaveBegan() {}
    
    public void onPostResume() {}
    
    public void renderAlways() {};
    
    public void renderUI() {};
    public void updateUI() {};
    
    protected SmoothJoin fullScreenColor;
    protected boolean fadingCompleted;
    
    float delayerTarget, delayer;
    
    boolean instantSwitch;
    
    public State() {
        fullScreenColor = new SmoothJoin(4);
        fullScreenColor.setInterpolator(Interpolator.SINUSOIDAL);
        fullScreenColor.init(0f, 0f, 0f, 0f);
        fullScreenColor.setTarget(0f, 0f, 0f, 0f, 0f);
        fadingCompleted = true;
    }
    
    
    public void fadeInWithColor(float red, float green, float blue, float speed) {
        fullScreenColor.init(red, green, blue, 1f);
        fullScreenColor.setTarget(red, green, blue, 0f, speed);
        fadingCompleted = false;
    }
    
    public void fadeOutWithColor(float red, float green, float blue, float speed, float delay) {
        fullScreenColor.init(red, green, blue, 0f);
        fullScreenColor.setTarget(red, green, blue, 1f, speed, delay);
        fadingCompleted = false;
    }
    
    
    protected float framesToSkip;
    
    public static boolean followsResume = false;
    public void onEnter() {
        if (!instantSwitch) framesToSkip = 2;
        onEnterImpl(followsResume);
        followsResume = false;
    }
    
    public static boolean transitionInProgress = false;
    private State nextState; 
    protected State previousState = null;
    
    public State getNextState() { return nextState; } 
    
    public void update () {    
        
        if (instantSwitch) {
            delayer += Timer.delta;
            
            if (transitionInProgress && delayer >= delayerTarget) {
                fadingCompleted = true;
                transitionInProgress = false;
                nextState.enterState();
                nextState.framesToSkip = 0;
                nextState = null;
            }
            updateImpl();
        } else {
        
            if (framesToSkip > 0) return;
            fadingCompleted = !fullScreenColor.update();
            updateImpl();
            if (fadingCompleted && transitionInProgress) {
                transitionInProgress = false;
                nextState.enterState();
                nextState = null;
            }
        }
    }
    
    public void render() {
        
        renderAlways();
        
        if (framesToSkip > 0) {
            framesToSkip--;
            //return;
        }
        
        if (framesToSkip <= 0) 
            renderImpl();
        
        if (fullScreenColor.get(3) > 0) {
            
            Renderer.resetToDefaultShading();
            Renderer.Blending.enable();
            Renderer.Blending.resetMode();
            
            TextureManager.blank.bind();
            
            Renderer.setColor(fullScreenColor.get(0), fullScreenColor.get(1), fullScreenColor.get(2), fullScreenColor.get(3));
            
            QuadVBO.drawQuad(Screen.ARENA_WIDTH/2f, Screen.ARENA_HEIGHT/2f, Screen.ARENA_WIDTH * 1.1f, Screen.ARENA_HEIGHT * 1.1f);
        }

        
        Renderer.resetColor();
    }
    
    public void switchToStateWithColor(State newState, float red, float green, float blue, float duration, float delay) {
        switchToStateWithColor(newState, red, green, blue, duration, delay, duration);
    }
    
    public void switchToStateWithColor(State newState, float red, float green, float blue, float duration, float delay, float newStateDuration) {
        instantSwitch = false;
        newState.instantSwitch = false;
        transitionInProgress = true;
        if (Engine.state != null) Engine.state.onLeaveBegan();
        nextState = newState;
        fadeOutWithColor(red, green, blue, duration, delay);
        nextState.fadeInWithColor(red, green, blue, newStateDuration);
    }
    
    public void switchToStateInstant(State newState, float delay) {
        instantSwitch = true;
        newState.instantSwitch = true;
        transitionInProgress = true;
        delayer = 0f; delayerTarget = delay;
        if (Engine.state != null) Engine.state.onLeaveBegan();
        nextState = newState;
    }
    
    public void enterState() { enterState(false); }
    
    public void enterState(boolean isResume) {
        if (Engine.state != null) {
            previousState = Engine.state;
            Engine.state.onLeaveCompleted();
        }
        State state = getInstance();
        Engine.state = state;
        state.onEnter();
    }
}
