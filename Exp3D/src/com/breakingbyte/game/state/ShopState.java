package com.breakingbyte.game.state;


import java.util.ArrayList;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.content.Article;
import com.breakingbyte.game.content.ArticleGroup;
import com.breakingbyte.game.content.ShopContent;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.DynamicTextNumber;
import com.breakingbyte.game.ui.OrbWidget;
import com.breakingbyte.game.ui.SliderView;
import com.breakingbyte.game.ui.Widget;
import com.breakingbyte.game.ui.Widget.TouchListener;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.ui.dialog.Dialog;
import com.breakingbyte.game.ui.dialog.InfoDialog;
import com.breakingbyte.game.ui.dialog.PanelButton;
import com.breakingbyte.game.ui.dialog.ShopBuyDialog;
import com.breakingbyte.game.ui.dialog.ShopItem;
import com.breakingbyte.game.ui.dialog.ShopPanel;
import com.breakingbyte.game.ui.dialog.SideButtonMenu;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.iap.IAPManager;


public class ShopState extends State {
    
    public static ShopState instance = new ShopState();

    public State getInstance() { return instance; }
    
    public TitleState getParent() { return TitleState.instance; }

    // All widgets are put into mainCanvas
    public static WidgetContainer mainCanvas;
    
    public static SideButtonMenu sideMenu;
    
    public static SmoothJoin mainDialogJoin;
    public static float mainDialogJoinTarget;
    
    public static SmoothJoin sliderJoin;
    
    public static SmoothJoin sliderAlphaJoin;
    
    public static float panelWidth = 85f; // shop panel width
    public static float panelHeight = 75f; // shop panel height
    public static float offsetBetweenPanels = 85.5f; // offset between 2 panels in the slider
    
    public static SliderView slider;
    
    public static InfoDialog infoDialog;
    public static ShopBuyDialog buyDialog;
    
    public static WidgetContainer upCanvas;
    public static PanelButton headerBg;
    public static DynamicText shopTitle;
    public static DynamicTextNumber orbCount;
    public static OrbWidget orbIcon;
    
    public static WidgetContainer Canvas;
    
    public static WidgetContainer lowerCanvas;
    public static PremiumButton 
                    getMoreOrbsButton,
                    unlockFullVersionButton;
    
    private static ArrayList<Widget> bgItems;
    
    public static void init() {
        
        bgItems = new ArrayList<Widget>();
        
        mainCanvas = new WidgetContainer();
        mainCanvas.setSizeFullScreen();
        
        sideMenu = new SideButtonMenu();
        sideMenu.generateBackButton()
            .setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { instance.buttonBackPressed(); } } );
        
        upCanvas = new WidgetContainer();
        upCanvas.setSizeFullScreen();
        mainCanvas.addChild(upCanvas);
        
        lowerCanvas = new WidgetContainer();
        lowerCanvas.setSizeFullScreen();
        mainCanvas.addChild(lowerCanvas);
        
        
        
        headerBg = new PanelButton();
        headerBg.acceptInput = false;
        upCanvas.addChild(headerBg);
        
        shopTitle = new DynamicText(5);
        shopTitle.reset().printString("Shop").updateBuffers();
        upCanvas.addChild(shopTitle);
        
        orbCount = new DynamicTextNumber(10);
        upCanvas.addChild(orbCount);
        
        orbIcon = new OrbWidget();
        upCanvas.addChild(orbIcon);
        
        
        slider = new SliderView();
        slider.offsetBetweenChildren = offsetBetweenPanels;
        slider.setSize(Screen.ARENA_WIDTH, panelHeight);
        slider.setPosition(Screen.ARENA_WIDTH * 0.5f, 0f);
        
        for (int i = 0; i < ShopContent.allGroups.size(); i++) {
            ArticleGroup group = ShopContent.allGroups.get(i);
            ShopPanel panel = createShopPanel(group);
            slider.addShopPanel(panel);
            for (int j = 0; j < group.articles.size(); j++) {
                ShopItem item = new ShopItem(group.articles.get(j));
                item.setSize(1f, 17f);
                panel.addListItemChild(item);
                item.setOnClickListener(new ShopItemClickListener(item));
            }
        }
        
        infoDialog = new InfoDialog();
        infoDialog.okButton
            .setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); instance.infoDialogOkPressed(); } } );
        
        buyDialog = new ShopBuyDialog();
        buyDialog.setValuesFromArticle(ShopContent.specialReloadArticle);
        buyDialog.setSize(92, 105);
        
        mainCanvas.addChild(slider);
        
        getMoreOrbsButton = new PremiumButton();
        getMoreOrbsButton.text.reset()
            .printString("Get").newLine()
            .printString("More").newLine()
            .printString("Orbs").updateBuffers();
        getMoreOrbsButton
            .setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) {AudioManager.playClickSound(); instance.buttonGetMoreOrbsPressed(); } } );
        lowerCanvas.addChild(getMoreOrbsButton);
        
        unlockFullVersionButton = new PremiumButton();
        updateFullVersionButton(EngineState.isFullVersion);
        unlockFullVersionButton
            .setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); instance.buttonUnlockFullVerisonPressed(); } } );
        lowerCanvas.addChild(unlockFullVersionButton);
        
        mainCanvas.addChild(infoDialog);
        mainCanvas.addChild(buyDialog);
        
        mainCanvas.addChild(sideMenu);
        
        mainDialogJoinTarget = 1f;
        mainDialogJoin = new SmoothJoin();
        //mainDialogJoin.setInterpolator(Interpolator.BACK_START_END);
        //mainDialogJoin.setBack(1.2f);
        mainDialogJoin.setInterpolator(Interpolator.QUADRATIC_END);
        mainDialogJoin.init(mainDialogJoinTarget);
        mainDialogJoin.setTarget(mainDialogJoinTarget, 0f);
        
        sliderJoin = new SmoothJoin();
        sliderJoin.setInterpolator(Interpolator.BACK_START_END);
        sliderJoin.setBack(3.8f);
        sliderJoin.init(1f);
        sliderJoin.setTarget(1f, 0f);
        
        
        sliderAlphaJoin = new SmoothJoin();
        sliderAlphaJoin.init(1f);
        sliderAlphaJoin.setTarget(1f, 0f);
        
        bgItems.add(slider);
        bgItems.add(getMoreOrbsButton);
        bgItems.add(unlockFullVersionButton);
    }
    
    public static void updateFullVersionButton(boolean isFullVersion) {
        //Log.d("IAPManager", "Updating full version button to " + isFullVersion);
        if (isFullVersion) {
            unlockFullVersionButton.text.reset()
                .printString("Full").newLine()
                .printString("Version").newLine()
                .printString("Unlocked").updateBuffers();
            unlockFullVersionButton.setEnabled(false);
        } else {
            unlockFullVersionButton.text.reset()
                .printString("Unlock").newLine()
                .printString("Full").newLine()
                .printString("Version").updateBuffers();
            unlockFullVersionButton.setEnabled(true);
        }
        unlockFullVersionButton.glowing = !isFullVersion;
        unlockFullVersionButton.text.setAlpha(isFullVersion ? 0.35f : 0.8f);
        unlockFullVersionButton.initColor();
    }
    
    public static ShopPanel createShopPanel(ArticleGroup group) {
        ShopPanel result = new ShopPanel();
        result.setSize(panelWidth, panelHeight);
        result.setPosition(0, 0);
        result.setValues(group.name);
        return result;
    }
    
    //For each article in the shop
    public static class ShopItemClickListener implements TouchListener {

        ShopItem shopItem;
        
        public ShopItemClickListener(ShopItem item) {
            shopItem = item;
        }
        
        public void riseEvent(float x, float y) {
            AudioManager.playClickSound();
            if (shopItem.article.isAtMaxLevel()) {
                infoDialog.setTitle(shopItem.article.getName());
                infoDialog.message.reset()
                            .printString("This item has been").newLine()
                            .printString("upgraded to the").newLine()
                            .printString("maximum level.")
                            .updateBuffers();
                infoDialog.autoSize().show();
            } else {
                buyDialog.setValuesFromArticle(shopItem.article);
                buyDialog.show();
            }
        }
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

    public static void articleBuyButtonClicked(Article article) {
        if (!article.canBeBoughtByPlayer()) {
            IAPManager.buyOrbs();
        } else {
            article.buy();
            updateOrbCountDisplay();
            ShopContent.updateAllPanelsFromArticles();
            
            //Show confirmation dialog
            infoDialog.setTitle("Thank you");
            infoDialog.message.reset().printString("Upgrade completed!").updateBuffers();
            infoDialog.autoSize().show();
        }
        buyDialog.hide();
    }
    
    public void infoDialogOkPressed() {
        infoDialog.hide();
    }
    
    public static void updateOrbCountDisplay() {
        orbCount.value.init(orbCount.value.get());
        orbCount.value.setTarget(EngineState.Player.totalOrbs, 2f);
    }
    
    public void buttonBackPressed() {
        AudioManager.playClickSound();
        switchToStateInstant(TitleState.instance, 0.5f);
    }
    
    public void buttonGetMoreOrbsPressed() {
        IAPManager.buyOrbs();
    }
    
    public void buttonUnlockFullVerisonPressed() {
        IAPManager.buyFullVersion();
    }
    
    public void updateImpl() {
        updateControl();
        getParent().updateUI();
    }
    
    public static boolean doneIt = false;
    public void updateUI() {
        
        float H = Screen.ARENA_HEIGHT;
        
        //TODO move
        if (!doneIt) {
            doneIt = true;
        
            float currentY = H * 0.975f;
            float titleSize = 13f;
            
            headerBg.borderSize = 10f;
            headerBg.setSize(Screen.ARENA_WIDTH + 30f, 22f);
            headerBg.setPosition(Screen.ARENA_WIDTH * 0.5f, currentY - 6f);
            
            shopTitle.setPosition(10, currentY);
            shopTitle.setAlpha(0.8f);
            shopTitle.textSize = titleSize;
            
            orbCount.setPosition(78, currentY);
            orbCount.setAlpha(0.8f);
            orbCount.textSize = titleSize;
            
            orbIcon.setSize(10, 10);
            orbIcon.setPosition(84, currentY - 6.5f);
            
            currentY = H * 0.85f;
            slider.setPosY(currentY + panelHeight * -0.5f);
            
            currentY = H * 0.22f;
            float premiumTextSize = 9.5f;
            float premiumTextY = 11.5f;
            float premiumWidth = 40f, premiumHeight = 34f;
            float premiumXOffset = 29f;
            
            getMoreOrbsButton.setSize(premiumWidth, premiumHeight);
            getMoreOrbsButton.setPosition(Screen.ARENA_WIDTH - premiumXOffset, currentY);
            getMoreOrbsButton.text.setAlpha(0.8f);
            getMoreOrbsButton.text.textSize = premiumTextSize;
            getMoreOrbsButton.text.setPosY(premiumTextY);
            getMoreOrbsButton.setIdleColor(1f, 0.4f, 0.8f);
            getMoreOrbsButton.setFocusedColor(1f, 0.6f, 1f);
            
            getMoreOrbsButton.initColor();
            
            unlockFullVersionButton.setSize(premiumWidth, premiumHeight);
            unlockFullVersionButton.setPosition(premiumXOffset, currentY);
            unlockFullVersionButton.text.textSize = premiumTextSize;
            unlockFullVersionButton.text.setPosY(premiumTextY);
            //unlockFullVersionButton.setIdleColor(1f, 0.3f, 0.2f);
            unlockFullVersionButton.setIdleColor(0.0f, 1f, 0.9f);
            unlockFullVersionButton.setFocusedColor(0.6f, 1f, 0.7f);
            unlockFullVersionButton.setDisabledColor(0.0f, 0.3f, 0.3f);
            unlockFullVersionButton.initColor();
            unlockFullVersionButton.mirrorBG();
        }
        
        //Update main canvas here
        mainDialogJoin.update();
        sliderJoin.update();
        sliderAlphaJoin.update();
        
        upCanvas.setPosY(mainDialogJoin.get() * 40f);
        lowerCanvas.setPosY(mainDialogJoin.get() * -100f);
        slider.setPosX(Screen.ARENA_WIDTH * 0.5f + sliderJoin.get() * 100f);
        
        mainCanvas.update();
        
        boolean someDialogOpen = buyDialog.isOpen() || infoDialog.isOpen();
        
        for (int i = 0; i < bgItems.size(); i++) {
            bgItems.get(i).setGlobalAlpha(sliderAlphaJoin.get());
            bgItems.get(i).acceptInput = !someDialogOpen;
        }
        
        if (!transitionInProgress) {
            if (someDialogOpen) {
                sliderAlphaJoin.setTarget(0.3f, 2f);
            } else {
                sliderAlphaJoin.setTarget(1f, 2f);
            }
        }
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
        
        sliderJoin.init(sliderJoin.get());
        sliderJoin.setTarget(0, 1.5f, 0f);
        
        //sliderAlphaJoin.setTarget(1f, 1f);
        
        sideMenu.display(0);
        
        ShopContent.updateAllPanelsFromArticles();
        
        orbCount.value.init(EngineState.Player.totalOrbs);
        orbCount.value.setTarget(EngineState.Player.totalOrbs, 1f);
        
        if (isResume) onPostResume();

    }

    @Override
    public void onLeaveBegan() {
        slider.moveToFirstPanel();
        mainDialogJoin.init(mainDialogJoin.get());
        mainDialogJoin.setTarget(mainDialogJoinTarget, 1f);
        
        sliderJoin.init(sliderJoin.get());
        sliderJoin.setTarget(1f, 1f);
        
        sliderAlphaJoin.setTarget(0f, 6f, 0.3f);
        
        sideMenu.hide();
        buyDialog.hide();
        infoDialog.hide();
    }
    
    @Override
    public void onLeaveCompleted() {
        
    }
    
    public static class PremiumButton extends PanelButton {
        
        private DynamicText text;
        
        public PremiumButton() {
            innerBorder.setAlpha(0.8f);
            text = new DynamicText(32);
            text.setAlignment(Align.CENTER);
            addChild(text);  
        }
        
        public void mirrorBG() {
            panel.flipU = true;
            innerBorder.flipU = true;
            glow.flipU = true;
        }
    }
    
}
