package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;

public class SideButtonMenu extends WidgetContainer {
    
    public SmoothJoin sideButtonJoin;
    public static float sideButtonJoinTarget = 20;
    
    public static float sideButtonSizeX = 70, sideButtonSizeY = 70;
    
    private SideButton leftButton = null, rightButton = null;
    
    public SideButtonMenu() {
        sideButtonJoin = new SmoothJoin();
        sideButtonJoin.setInterpolator(Interpolator.BACK_START_END);
        sideButtonJoin.setBack(2.5f);
        sideButtonJoin.init(sideButtonJoinTarget);
        sideButtonJoin.setTarget(sideButtonJoinTarget, 0f);
        setSizeFullScreen();
    }
    
    public void display(float delay) {
        sideButtonJoin.init(sideButtonJoin.get());
        sideButtonJoin.setTarget(0, 1f, delay); 
    }
    
    public void hide() {
        sideButtonJoin.init(sideButtonJoin.get());
        sideButtonJoin.setTarget(sideButtonJoinTarget, 1f);
    }
    
    public SideButton generateLeftButton() {
        leftButton = generateSideButton(false);
        addChild(leftButton);
        return leftButton;
    }
    
    public SideButton generateRightButton() {
        rightButton = generateSideButton(true);
        addChild(rightButton);
        return rightButton;
    }
    
    public SideButton generateBackButton() {
        SideButton backButton = generateLeftButton();
        backButton.setImage(TextureManager.arrowIcon, 14f, 14f, 0.9f);
        return backButton;
    }
    
    public static SideButton generateSideButton(boolean onRight) {
        SideButton result = new SideButton();
        if (onRight) result.onRightSide(); else result.onLeftSide(); 
        result.setSize(sideButtonSizeX, sideButtonSizeY);
        result.setPosition(onRight? Screen.ARENA_WIDTH : 0, 3);
        return result;
    }
    
    public boolean isHidden() {
        return sideButtonJoin.get() >= sideButtonJoinTarget;
    }
    
    @Override
    public void update() {
        super.update();
        sideButtonJoin.update();
        if (leftButton != null) leftButton.setPosition(-15 - sideButtonJoin.get(), -14 - sideButtonJoin.get());
        if (rightButton != null) rightButton.setPosition(15 + Screen.ARENA_WIDTH + sideButtonJoin.get(), -14 - sideButtonJoin.get());
    }

}
