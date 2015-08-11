package com.breakingbyte.game.state;


import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.ImageWidget;
import com.breakingbyte.game.ui.Widget;
import com.breakingbyte.game.ui.Widget.TouchListener;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.ui.dialog.Dialog;
import com.breakingbyte.game.ui.dialog.SideButtonMenu;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.Platform;


public class CreditsState extends State {
    
    public static CreditsState instance = new CreditsState();

    public State getInstance() { return instance; }
    
    public TitleState getParent() { return TitleState.instance; }

    public static WidgetContainer mainCanvas;
    public static SideButtonMenu sideMenu;
    public static Dialog mainDialog;
    
    public static SmoothJoin mainDialogJoin;
    public static float mainDialogJoinTarget;
    
    private static float dialogWidth = 85f;
    private static float dialogHeight = 77f;
    
    public static void init() {
        
        mainCanvas = new WidgetContainer();
        
        sideMenu = new SideButtonMenu();
        sideMenu.generateBackButton()
            .setOnClickListener( new TouchListener() { public void riseEvent(float x, float y){ instance.buttonBackPressed(); } } );
        
        mainDialog = generateDialog();
        mainDialog.setScale(1.1f);
        
        mainCanvas.addChild(mainDialog);
        mainCanvas.addChild(sideMenu);
        
        mainDialogJoinTarget = Screen.ARENA_HEIGHT * 0.8f;
        mainDialogJoin = new SmoothJoin();
        mainDialogJoin.setInterpolator(Interpolator.BACK_START_END);
        mainDialogJoin.setBack(1.2f);
        mainDialogJoin.init(mainDialogJoinTarget);
        mainDialogJoin.setTarget(mainDialogJoinTarget, 0f);
    }
    
    public static Dialog generateDialog() {
        Dialog dialog = new Dialog();
        dialog.showInstant();
        dialog.panel.setColor(110f / 255f, 171f / 255f, 255.0f / 255f);    
        
        dialog.title.printString("Credits");
        dialog.title.updateBuffers();
        
        dialog.setSize(dialogWidth, dialogHeight);
        
        float currentY = dialogHeight * 0.5f - 25.0f;
        
        ImageWidget titleWidget = new ImageWidget();
        float spriteWidth = 60f;
        titleWidget.setSize(spriteWidth, spriteWidth * 0.3f);
        titleWidget.setPosition(0, currentY);
        titleWidget.setTexture(TextureManager.title);
        dialog.addChild(titleWidget);
        
        currentY -= 10f;
        
        String devByStr = "Developed by";
        String versionStr = EngineState.Settings.versionName + " " + Platform.name;
        
        DynamicText devBy = createLabelText(devByStr.length() + versionStr.length(), 5f, dialog, false);
        devBy.printString(versionStr)
            //.newLine().newLine().printString(devByStr)
            .updateBuffers();
        devBy.setPosition(0, currentY);
        
        currentY -= 10; //11f;
        final String webSiteStr = "www.breakingbyte.com";
        DynamicText webSite = createLabelText(webSiteStr.length(), 6.5f, dialog, false);
        webSite.setColor(0.3f, 0.3f, 1f);
        webSite.printString(webSiteStr).updateBuffers();
        webSite.setPosition(0, currentY);
        
        Widget webClick = new Widget();
        webClick.setSize(dialogWidth, 30f);
        webClick.setPosition(0, currentY -10f);
        webClick.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y){ Platform.openURL("http://"+webSiteStr); } } );
        dialog.addChild(webClick);
        
        currentY -= 10f;
        String codeCreditsStr = "Program, GFX, Design";
        DynamicText codeCredits = createLabelText(codeCreditsStr.length(), 6.5f, dialog, false);
        codeCredits.printString(codeCreditsStr).updateBuffers();
        codeCredits.setPosition(0, currentY);
        
        currentY -= 5.5f;
        String nameStr = "Adrian Courreges";
        DynamicText name = createLabelText(nameStr.length(), 7.0f, dialog, true);
        name.printString(nameStr).updateBuffers();
        name.setPosition(0, currentY);
        

        
        return dialog;
    }
    
    public static DynamicText createLabelText(int maxLength, float textSize, Dialog dialog, boolean highlight) {
        DynamicText result = new DynamicText(maxLength);
        dialog.addChild(result);
        result.setAlignment(Align.CENTER);
        result.textSize = textSize;
        result.setAlpha(0.9f);
        if (!highlight) result.setColor(188, 197, 255);
        else result.setColor(255, 255, 255);
        return result;
    }

    
    public void buttonBackPressed() {
        AudioManager.playClickSound();
        switchToStateInstant(TitleState.instance, 0.5f);
    }
    
    public void updateImpl() {
        updateControl();
        getParent().updateUI();
    }
    
    public void updateUI() {
        //Update main canvas here
        mainDialogJoin.update();
        mainDialog.setPosition(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f + 5f + mainDialogJoin.get());
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
        TitleState.lastSubState = this;
        mainDialogJoin.init(mainDialogJoin.get());
        mainDialogJoin.setTarget(0, 1f, 0);
        
        sideMenu.display(0);
        if (isResume) onPostResume();
        
        //IAPManager.displayWakuWakuCoupon();
    }

    @Override
    public void onLeaveBegan() {
        mainDialogJoin.init(mainDialogJoin.get());
        mainDialogJoin.setTarget(mainDialogJoinTarget, 1f);
        sideMenu.hide();
    }
    
    @Override
    public void onLeaveCompleted() {
        
    }
    
}
