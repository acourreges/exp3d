package com.breakingbyte.game.state;


import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.Widget.TouchListener;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.ui.dialog.Dialog;
import com.breakingbyte.game.ui.dialog.PanelButton;
import com.breakingbyte.game.ui.dialog.SideButtonMenu;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;


public class SettingsState extends State {
    
    public static SettingsState instance = new SettingsState();

    public State getInstance() { return instance; }
    
    public TitleState getParent() { return TitleState.instance; }

    public static WidgetContainer mainCanvas;
    public static SideButtonMenu sideMenu;
    public static Dialog mainDialog;
    
    public static SmoothJoin mainDialogJoin;
    public static float mainDialogJoinTarget;
    
    private static DynamicText musicLabel,
                                soundLabel,
                                vibrateLabel,
                                displayFPSLabel;
    
    private static PanelButton musicValue,
                                soundValue,
                                vibrateValue,
                                displayFPSValue;
    
    private static float dialogWidth = 85f;
    private static float dialogHeight = 70f;
    
    public static void init() {
        
        mainCanvas = new WidgetContainer();
        
        sideMenu = new SideButtonMenu();
        sideMenu.generateBackButton()
            .setOnClickListener( new TouchListener() { public void riseEvent(float x, float y){ instance.buttonBackPressed(); } } );
        
        mainDialog = generateDialog();
        
        mainCanvas.addChild(mainDialog);
        mainCanvas.addChild(sideMenu);
        
        mainDialogJoinTarget = Screen.ARENA_HEIGHT * 0.8f;
        mainDialogJoin = new SmoothJoin();
        mainDialogJoin.setInterpolator(Interpolator.BACK_START_END);
        mainDialogJoin.setBack(1.5f);
        mainDialogJoin.init(mainDialogJoinTarget);
        mainDialogJoin.setTarget(mainDialogJoinTarget, 0f);
    }
    
    public static Dialog generateDialog() {
        Dialog dialog = new Dialog();
        dialog.showInstant();
        dialog.panel.setColor(110f / 255f, 171f / 255f, 255.0f / 255f);    
        
        dialog.title.printString("Settings");
        dialog.title.updateBuffers();
        
        dialog.setSize(dialogWidth, dialogHeight);
        
        float currentX = - dialogWidth * 0.5f + 10f;
        float currentValueX = dialogWidth * 0.5f - 14f;
        float currentY = dialogHeight * 0.5f - 15.0f;
        float valueOffsetY = -5f;
        float incrementer = -12f;
        
        musicLabel = createLabelText("Music", dialog);
        musicLabel.setPosition(currentX, currentY);
        musicValue = createBinaryPanel(dialog);
        musicValue.setPosition(currentValueX, currentY + valueOffsetY);
        musicValue.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y){ instance.buttonMusicPressed(); } } );
        
        currentY += incrementer;
        soundLabel = createLabelText("Sound", dialog);
        soundLabel.setPosition(currentX, currentY);
        soundValue = createBinaryPanel(dialog);
        soundValue.setPosition(currentValueX, currentY + valueOffsetY);
        soundValue.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y){ instance.buttonSoundPressed(); } } );
        
        currentY += incrementer;
        vibrateLabel = createLabelText("Vibrate", dialog);
        vibrateLabel.setPosition(currentX, currentY);
        vibrateValue = createBinaryPanel(dialog);
        vibrateValue.setPosition(currentValueX, currentY + valueOffsetY);
        vibrateValue.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y){ instance.buttonVibratePressed(); } } );
        
        currentY += incrementer;
        displayFPSLabel = createLabelText("Display FPS", dialog);
        displayFPSLabel.setPosition(currentX, currentY);
        displayFPSValue = createBinaryPanel(dialog);
        displayFPSValue.setPosition(currentValueX, currentY + valueOffsetY);
        displayFPSValue.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y){ instance.buttonDisplayFPSPressed(); } } );
        
        synchronizeButtonStates();
        
        return dialog;
    }
    
    public static DynamicText createLabelText(String text, Dialog dialog) {
        DynamicText result = new DynamicText(text.length());
        dialog.addChild(result);
        setUpLabelText(result);
        result.setAlignment(Align.LEFT);
        result.printString(text);
        result.updateBuffers();
        return result;
    }
    
    public static PanelButton createBinaryPanel(Dialog dialog) {
        PanelButton result = new PanelButton();
        dialog.addChild(result);
        //result.setTexture(TextureManager.astrol);
        result.innerBorder.setColor(1f, 1f, 1f);
        result.innerBorder.setAlpha(1f);
        result.borderSize = 5f;
        result.focusScale = 1.2f;
        result.setSize(10, 10);
        return result;
    }
    
    public static void setUpLabelText(DynamicText text) {
        text.textSize = 10f;
        text.setAlpha(0.9f);
        text.setColor(218, 227, 255);
    }
    
    public static void setBinaryStatus(PanelButton button, boolean on) {
        if (on) {
            float red = 0, green = 0.9f, blue = 0.9f;
            button.setIdleColor(red, green, blue);
            button.setFocusedColor(red, green, blue);
        } else {
            float red = 0.2f, green = 0.2f, blue = 0.2f;
            button.setIdleColor(red, green, blue);
            button.setFocusedColor(red, green, blue);
        }
        button.initColor();
    }
    
    public static void synchronizeButtonStates() {
        setBinaryStatus(musicValue, EngineState.Settings.musicOn);
        setBinaryStatus(soundValue, EngineState.Settings.soundOn);
        setBinaryStatus(vibrateValue, EngineState.Settings.vibrateOn);
        setBinaryStatus(displayFPSValue, EngineState.Settings.displayFPSOn);
    }
    
    public void buttonBackPressed() {
        AudioManager.playClickSound();
        switchToStateInstant(TitleState.instance, 0.5f);
    }
    
    public void buttonMusicPressed() {
        AudioManager.playClickSound();
        EngineState.Settings.musicOn = !EngineState.Settings.musicOn;
        if (!EngineState.Settings.musicOn) {
            AudioManager.stopMusic();
        } else {
            AudioManager.playTitleMusic();
        }
        synchronizeButtonStates();
    }
    
    public void buttonSoundPressed() {
        EngineState.Settings.soundOn = !EngineState.Settings.soundOn;
        //TODO cut sound
        synchronizeButtonStates();
        AudioManager.playClickSound();
    }
    
    public void buttonVibratePressed() {
        AudioManager.playClickSound();
        EngineState.Settings.vibrateOn = !EngineState.Settings.vibrateOn;
        synchronizeButtonStates();
    }
    
    public void buttonDisplayFPSPressed() {
        AudioManager.playClickSound();
        EngineState.Settings.displayFPSOn = !EngineState.Settings.displayFPSOn;
        synchronizeButtonStates();
    }
    
    public void updateImpl() {
        updateControl();
        getParent().updateUI();
    }
    
    public void updateUI() {
        //Update main canvas here
        mainDialogJoin.update();
        mainDialog.setPosition(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f + mainDialogJoin.get());
        mainCanvas.update();
    }
    
    public void updateControl() {
        
        if (transitionInProgress) return;
        
        if (Controller.backFlag) {
            buttonBackPressed();
            return;
        }

        if (Controller.hasEvent) 
            mainCanvas.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
        
    } 

    public void renderImpl() {
        getParent().renderImpl();
    }
    
    public void renderUI() {
        //UI draw here. This is always called in continuity of TitleState.renderImpl
        if (sideMenu.isHidden()) return;
        mainCanvas.render();
    }

    @Override
    public void onPostResume() {
        TitleState.instance.onPostResume();
    }    

    @Override
    public void onEnterImpl(boolean isResume) {
        //mainDialog.show();
        
        TitleState.lastSubState = this;
        
        if (!isResume) {
            mainDialogJoin.init(mainDialogJoin.get());
            mainDialogJoin.setTarget(0, 1f, 0);
            
            sideMenu.display(0);
        } else {
            onPostResume();
        }
    }

    @Override
    public void onLeaveBegan() {
        //mainDialog.hide();
        mainDialogJoin.init(mainDialogJoin.get());
        mainDialogJoin.setTarget(mainDialogJoinTarget, 1f);
        sideMenu.hide();
    }
    
    @Override
    public void onLeaveCompleted() {
        
    }
    
}
