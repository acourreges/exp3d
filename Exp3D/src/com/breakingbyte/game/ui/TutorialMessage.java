package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class TutorialMessage extends Widget {
    
    public float scale = 32;
    
    public SmoothJoin alphaJoin;
    
    private float t;
    
    public DynamicText text;
    private float textOffsetX, textOffsetY;
    
    private boolean displayReticule;
    
    private float textSize;
    
    private float textScalerAmplitude;
    
    public TutorialMessage() {
        t = 0f;
        alphaJoin = new SmoothJoin();
        alphaJoin.init(0f);
        startDisappearAnimation();
    }
    
    public void init() {
        text = new DynamicText(64);
    }
    
    public void reset() {
        alphaJoin.init(0f);
        alphaJoin.setTarget(0f, 0f);
    }
    
    public void update() {
        t += Timer.delta;
        alphaJoin.update();
    }
    
    public void startAppearAnimation() {
        t = 0f;
        alphaJoin.init(alphaJoin.get());
        alphaJoin.setTarget(1.0f, 5f);
    }
    
    public void startDisappearAnimation() {
        alphaJoin.init(alphaJoin.get());
        alphaJoin.setTarget(0f, 5f);
    }
    
    public void render() {
        
        if (alphaJoin.get() < 0.005f) return;
        
        Renderer.Blending.resetMode();
        
        Renderer.pushMatrix();
        Renderer.unbindVBOs();
        
        Renderer.setColor(1f, 1f, 1f, alphaJoin.get());
        
        if (displayReticule) {
            TextureManager.focuser.bind();
            QuadVBO.drawQuad(posX, posY, scale * 2f, scale);
        }
        
        float labelAlpha = alphaJoin.get() * (float)(0.7f + 0.3f*Math.sin(50*t));

        text.setAlpha(labelAlpha);
        text.textSize = textSize + textScalerAmplitude * (float)Math.sin(6*t);
        text.setPosition(posX + 16.5f, posY + 20.7f);
        text.setPosition(posX + textOffsetX, posY + textOffsetY);
        text.render();
        
        Renderer.resetColor();
        Renderer.popMatrix();
    }
    
    public static enum TUTO_STRING {
        GRAB_ORB,
        FIRE_SPECIAL,
        RELOAD_SPECIAL
    }
    
    public void prepare(TUTO_STRING tutoString) {
        text.reset();
        
        switch (tutoString) { 
            case GRAB_ORB:
                text.setAlignment(Align.CENTER);
                text.printString("Pick up").newLine();
                text.printString("the orbs");
                posX = Screen.ARENA_WIDTH * 0.5f;
                posY =  Screen.ARENA_HEIGHT - 20f;
                displayReticule = false;
                textOffsetX = textOffsetY = 0;
                textSize = 17f;
                textScalerAmplitude = 1f;
                break;
                
            case FIRE_SPECIAL:
                text.setAlignment(Align.CENTER);
                text.printString("Double-tap for").newLine();
                text.printString("special weapon");
                textSize = 13f;
                posX = Screen.ARENA_WIDTH * 0.5f;
                posY = Screen.ARENA_HEIGHT - 30f;
                displayReticule = false;
                textOffsetX = textOffsetY = 0;
                textScalerAmplitude = 1f;
                break;   
                
             case RELOAD_SPECIAL:
                    text.setAlignment(Align.LEFT);
                    text.printString("Special weapon").newLine();
                    text.printString("reloads over time");
                    textSize = 10f;
                    posX = 29.4f;
                    posY = 13.76f;
                    textOffsetX = -25;
                    textOffsetY = 26;
                    textScalerAmplitude = 0;
                    displayReticule = true;
                    break;    
                    
            default: break;
        }
        
        text.updateBuffers();
    }

}
