package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.state.ForwarderState;
import com.breakingbyte.game.state.ForwarderState.DialogEnum;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.anim.TextSpacerAnimation;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Renderer;

public class DoubleChoiceDialog extends WidgetContainer {
    
    public DialogButton topButton, bottomButton;
    public DialogButton middleButton;
    
    public SmoothJoin buttonsJoin, alphaJoin;
    
    public SmoothJoin fullscreenQuadPauseAlpha;
    public float fullscreenQuadPauseAlphaTarget, fullscreenQuadPauseAlphaSpeed;
    public float fullscreenRed, fullscreenGreen, fullscreenBlue;
    
    public boolean isOpen = false;
    
    private boolean hasExtraButton = false;
    
    public DoubleChoiceDialog() {
        this(false);
    }
    
    public DoubleChoiceDialog(boolean extraButton) {
        hasExtraButton = extraButton;
        
        setSize(Screen.ARENA_WIDTH, Screen.ARENA_HEIGHT);
        setPosition(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f);
        
        buttonsJoin = new SmoothJoin();
        buttonsJoin.setInterpolator(Interpolator.BACK_END);
        buttonsJoin.setBack(1.4f);

        
        alphaJoin = new SmoothJoin();
        
        fullscreenQuadPauseAlpha = new SmoothJoin();
        fullscreenRed = fullscreenGreen = fullscreenBlue = 0;
        fullscreenQuadPauseAlphaTarget = 0.8f;
        fullscreenQuadPauseAlphaSpeed = 3f;
        
        resetPositions();
    }
    
    public void resetPositions() {
        fullscreenQuadPauseAlpha.init(0f);
        fullscreenQuadPauseAlpha.setTarget(0f, 0f);
        
        alphaJoin.init(0f);
        alphaJoin.setTarget(0f, 0f);
        
        buttonsJoin.init(Screen.ARENA_WIDTH);
        buttonsJoin.setTarget(Screen.ARENA_WIDTH, 0f);
    }
    
    public static DialogButton generateButton(String buttonTitle) {
        DialogButton result = new DialogButton(buttonTitle.length());
        result.setIdleColor(0.5f, 0.6f, 0.9f);
        result.setFocusedColor(0.5f, 0.8f, 1f);
        result.setSize(48, 14);
        result.text.setAlignment(Align.CENTER);
        result.text.printString(buttonTitle);
        result.text.updateBuffers();
        return result;
    }
    
    public void update() {
        super.update();
        
        fullscreenQuadPauseAlpha.update();
        
        buttonsJoin.update();
        alphaJoin.update();
        
        setGlobalAlpha(alphaJoin.get());
        
        if (!hasExtraButton) {
            topButton.setPosition(buttonsJoin.get(), 10f);
            bottomButton.setPosition(-buttonsJoin.get(), -10f);
        } else {
            topButton.setPosition(-buttonsJoin.get(), 17f);
            middleButton.setPosition(buttonsJoin.get(), 0f);
            bottomButton.setPosition(-buttonsJoin.get(), -17f);
        }
    }
    
    @Override
    public void render() {
        if (fullscreenQuadPauseAlpha.get() < 0.05f) return;
        
        //Draw fullscreen quad
        Renderer.setColor(fullscreenRed, fullscreenGreen, fullscreenBlue, fullscreenQuadPauseAlpha.get());
        TextureManager.blank.bind();
        {
            Renderer.pushMatrix();
            Renderer.translate(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f, 0f);
            Renderer.scale(Screen.ARENA_WIDTH * 1.1f, Screen.ARENA_HEIGHT * 1.1f, 1f);
            QuadVBO.drawQuad();
            Renderer.popMatrix();
        }
        Renderer.resetColor();
        
        super.render();
    }
    
    public float buttonsSpeed = 0.7f;
    public float alphaSpeed = 4f;
    public void startAnimation() {
        isOpen = true;
        fullscreenQuadPauseAlpha.setTarget(fullscreenQuadPauseAlphaTarget, fullscreenQuadPauseAlphaSpeed);
        resetAnimations();
        buttonsJoin.init(buttonsJoin.get());
        buttonsJoin.setTarget(0f, buttonsSpeed);
        alphaJoin.setTarget(1f, alphaSpeed);
    }
    
    public void endAnimation() {
        isOpen = false;
        fullscreenQuadPauseAlpha.setTarget(0f, fullscreenQuadPauseAlphaSpeed);
        buttonsJoin.init(buttonsJoin.get());
        buttonsJoin.setTarget(Screen.ARENA_WIDTH, 1f);
        alphaJoin.setTarget(0f, 10f);
    }

    
    public static DoubleChoiceDialog generatePauseDialog() {
        DoubleChoiceDialog result = new DoubleChoiceDialog(true);
        
        result.fullscreenRed = result.fullscreenGreen = result.fullscreenBlue = 0f;
        result.fullscreenQuadPauseAlphaSpeed = 3f;
        
        String str = "PAUSE";
        DynamicText pauseLabel = DynamicText.generateCenteredString(str);
        TextSpacerAnimation anim = new TextSpacerAnimation(pauseLabel, str);
        anim.setUp(1f, -0.0f, 3.5f , 0f);
        pauseLabel.addAnimation(anim);
        result.addChild(pauseLabel);
        
        pauseLabel.setPosY(40f);
        pauseLabel.textSize = 13f;
        pauseLabel.setAlpha(0.8f);
        
        result.topButton = generateButton("Resume");
        result.topButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); ArenaState.instance.endPause(); }});
        result.addChild(result.topButton);
        
        result.middleButton = generateButton("Retry");
        result.middleButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y) 
                { AudioManager.playClickSound(); 
                ForwarderState.instance.setNextState(ArenaState.instance, 0f, 0f, 0f, 0.3f, DialogEnum.NONE);
                ArenaState.instance.switchToStateWithColor(ForwarderState.instance, 0f, 0f, 0f, 0.3f, 0f, 0.3f);
              }});
        result.addChild(result.middleButton);
        
        result.bottomButton = generateButton("Quit");
        result.bottomButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); ArenaState.instance.goBackToTitle(); }});
        result.addChild(result.bottomButton);
        
        return result;
    }
    
    public static DoubleChoiceDialog generateGameOver() {
        DoubleChoiceDialog result = new DoubleChoiceDialog();
        
        result.fullscreenRed = 0.2f; 
        result.fullscreenGreen = result.fullscreenBlue = 0f;
        result.fullscreenQuadPauseAlphaTarget = 0.6f;
        
        result.buttonsSpeed = 2.0f;
        result.alphaSpeed = 1f;
        result.fullscreenQuadPauseAlphaSpeed = 1f;
        
        String str = "GAME";
        DynamicText label = DynamicText.generateCenteredString(str);
        TextSpacerAnimation anim = new TextSpacerAnimation(label, str);
        anim.setUp(1.4f, -0.0f, 1.5f , 0f);
        label.addAnimation(anim);
        result.addChild(label);
        
        label.setPosY(50f);
        label.textSize = 13f;
        label.setAlpha(0.8f);
        
        str = "OVER";
        label = DynamicText.generateCenteredString(str);
        anim = new TextSpacerAnimation(label, str);
        anim.setUp(1.4f, -0.0f, 1.5f , 0f);
        label.addAnimation(anim);
        result.addChild(label);
        
        label.setPosY(40f);
        label.textSize = 13f;
        label.setAlpha(0.8f);
        
        
        result.topButton = generateButton("Retry");
        result.topButton.setIdleColor(1f, 0.1f, 0.1f);
        result.topButton.setFocusedColor(1f, 0.5f, 0.5f);
        result.topButton.initColor();
        result.topButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y) 
                                                                    { AudioManager.playClickSound(); 
                                                                      ForwarderState.instance.setNextState(ArenaState.instance, 0f, 0f, 0f, 0.3f, DialogEnum.NONE);
                                                                      ArenaState.instance.switchToStateWithColor(ForwarderState.instance, 0f, 0f, 0f, 0.3f, 0f, 0.3f);
                                                                    }});
        result.addChild(result.topButton);
        
        result.bottomButton = generateButton("Quit");
        result.bottomButton.setIdleColor(1f, 0f, 0f);
        result.bottomButton.setFocusedColor(1f, 0.5f, 0.5f);
        result.bottomButton.initColor();
        result.bottomButton.setOnClickListener(new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); ArenaState.instance.goBackToTitle(); }});
        result.addChild(result.bottomButton);
        
        return result;        
    }

}
