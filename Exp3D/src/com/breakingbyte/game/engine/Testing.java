package com.breakingbyte.game.engine;

import com.breakingbyte.game.content.ArticleGroup;
import com.breakingbyte.game.content.ShopContent;
import com.breakingbyte.game.entity.bonus.Orb;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.render.QuadBatch;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicTextNumber;
import com.breakingbyte.game.ui.NinePatch;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.SliderView;
import com.breakingbyte.game.ui.dialog.ChapterButton;
import com.breakingbyte.game.ui.dialog.Dialog;
import com.breakingbyte.game.ui.dialog.DialogButton;
import com.breakingbyte.game.ui.dialog.InfoDialog;
import com.breakingbyte.game.ui.dialog.LevelClearedDialog;
import com.breakingbyte.game.ui.dialog.ShopBuyDialog;
import com.breakingbyte.game.ui.dialog.ShopItem;
import com.breakingbyte.game.ui.dialog.ShopPanel;
import com.breakingbyte.game.ui.dialog.UnlockLevelDialog;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

/**
 * Scratch pad to experiment with new effects
 */

public class Testing {
    
    private static boolean doneOnce = false;
    
    public static void test() {

        testFunction();
        
    }
    
    private static void doOnce() {
        
        Renderer.setClearColor(0f, 0f, 0f, 0f);
        
        
        
        text = new DynamicText(20);
        text.setAlignment(Align.CENTER);
        
        text.setPosition(50, 140);
        text.textSize = 30;
        
        //DynamicTextTexture.test();
        
        ninePatch = new NinePatch(0.5f, 0.5f, 0.5f, 0.5f);
        ninePatch.setUp(60, 60, 20, 20, 20, 20);
        //ninePatch = new NinePatch(0.5f, 0.5f, 0.5f, 0.5f);
        //ninePatch.setUp(40, 40, 20, 20, 20, 20);
        ninePatch.setPosition(50, 50);
        
        dialog  = new LevelClearedDialog();
        dialog.show();
        
        setUpShop();
        
        
        
        dialogButton = new DialogButton(10);
        dialogButton.setSize(50, 14);
        dialogButton.setPosition(Screen.ARENA_WIDTH * 0.5f, 60);
        dialogButton.text.setAlignment(Align.CENTER);
        dialogButton.text.printString("Next");
        dialogButton.text.updateBuffers();
        
        sizer = 30;
        
        qb= new QuadBatch(10);
        //qb.addQuad(50, 50, 10, 10, 10, 10, scale, angle) QuadWithUV(20, 80, 70, 10, 0, 1, 0, 1);
        
        dynNumber = new DynamicTextNumber(15);
        dynNumber.textSize = 10;
        dynNumber.setPosition(Screen.ARENA_WIDTH * 0.5f, 50);
        dynNumber.value.setTarget(0/*12101188*/, 2f);
        //dynNumber.updateBufferToMatchValue(14151511);
        
        orb = Orb.newInstance();
        orb.posX = 50f;
        orb.posY = 55f;
        orb.scale = 20f;
        
        chapterButton = new ChapterButton(LevelContent.level1);
        chapterButton.loadFromLevelInfo();
        chapterButton.setPosition(Screen.ARENA_WIDTH * 0.5f, 50);
        
        smoothJoin = new SmoothJoin();
        smoothJoin.init(0);
        
        infoDialog = new InfoDialog();
        infoDialog.setTitle("Thank you");
        infoDialog.message.reset()
                            .printString("Upgrade completed!").newLine()
                            .printString("Message line 2").newLine()
                            .printString("Message line 3")//.newLine()
                            .updateBuffers();
        infoDialog.autoSize();
        infoDialog.show();
        
        unlockDialog = new UnlockLevelDialog();
        unlockDialog.setUpFromLevel(LevelContent.level2);
        unlockDialog.show();
    }
    
    public static DynamicText text;
    
    public static NinePatch ninePatch;
    public static float sizer;
    
    public static Dialog dialog;
    public static DialogButton dialogButton;
    
    public static SliderView slider;
    
    public static DynamicTextNumber dynNumber;
    
    public static QuadBatch qb;
    
    public static Orb orb;
    
    public static float spacer = 0.0f;
    
    public static ChapterButton chapterButton;
    
    public static SmoothJoin smoothJoin;
    
    public static InfoDialog infoDialog;
    
    public static UnlockLevelDialog unlockDialog;
    
    public static void update() {
        
        if (!doneOnce) { doOnce(); doneOnce = true; }
        
        if (Controller.hasEvent) {
            Controller.hasEvent = false;
            //slider.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
            //chapterButton.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
            unlockDialog.handleTouch(Controller.touchX, Controller.touchY, Controller.touchAction);
            
            smoothJoin.init(0);
            smoothJoin.setInterpolator(Interpolator.BACK_START_END);
            //smoothJoin.setElasticValues(1.0f, 0.9f);
            //smoothJoin.setElasticValues(1.0f, 0.9f);
            smoothJoin.setTarget(80, 0.5f);
            
        }
        
        slider.update();
        buyDialog.update();
        buyDialog.setSize(92, 105);
        infoDialog.update();
        dialog.update();
        unlockDialog.update();
        
        dynNumber.update();
        spacer += Timer.delta * 20;
        orb.update();
        chapterButton.update();
        
        smoothJoin.update();
        chapterButton.setPosY(20 + smoothJoin.get());
        
        for (int i = 0; i < 3; i++) {
            primaryWeaponPanel = (ShopPanel)slider.getChild(i);
            primaryWeaponPanel.setSize(primaryWeaponPanel.getWidth() * 1f, primaryWeaponPanel.getHeight()); //for live debug
        }
        

    }
    
    static float texCoords[] = {
            1.0f, 1.0f,  //bottom left
            0.0f, 1.0f,  //bottom right
            1.0f, 0.0f,  //top left
            0.0f, 0.0f   //top right
    };
    
    
    public static float panelWidth = 85f;
    public static float panelHeight = 100f;
    public static float offsetBetweenPanels = 88f;
    
    public static ShopPanel primaryWeaponPanel;
    
    public static ShopBuyDialog buyDialog;
    
    public static void setUpShop() {
        slider = new SliderView();
        slider.offsetBetweenChildren = offsetBetweenPanels;
        slider.setSize(Screen.ARENA_WIDTH, panelHeight);
        slider.setPosition(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f);
        
        for (int i = 0; i < ShopContent.allGroups.size(); i++) {
            ArticleGroup group = ShopContent.allGroups.get(i);
            ShopPanel panel = createShopPanel(group);
            slider.addShopPanel(panel);
            for (int j = 0; j < group.articles.size(); j++) {
                ShopItem item = new ShopItem(group.articles.get(j));
                item.setSize(1f, 17f);
                panel.addListItemChild(item);
            }
        }
        
        buyDialog = new ShopBuyDialog();
        buyDialog.setValuesFromArticle(ShopContent.specialReloadArticle);
        buyDialog.setSize(30, 30);
        buyDialog.showInstant();
    }
    
    public static ShopPanel createShopPanel(ArticleGroup group) {
        ShopPanel result = new ShopPanel();
        result.setSize(panelWidth, panelHeight);
        result.setPosition(0, 0);
        result.setValues(group.name);
        return result;
    }
    

    
    public static void testFunction() {
        
        //System.out.println("========== Frame starts ==========");
        Renderer.unbindVBOs();
        Renderer.loadOrtho();
        
        Renderer.clearColorBuffer();
        
        if (unlockDialog != null) unlockDialog.render();

        
    }
    


}
