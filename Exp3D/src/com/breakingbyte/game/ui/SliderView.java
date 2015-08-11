package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Controller.TouchAction;
import com.breakingbyte.game.ui.dialog.ShopPanel;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;

public class SliderView extends WidgetContainer {
    
    public int columnToDisplay;
    
    public float offsetBetweenChildren = 30;
    
    public float fingerSwipeThreshold = 3f;
    
    private float firstTouchX, firstTouchSliderX;
    private boolean slidingInProgress;
    Controller.TouchAction cancelAction = TouchAction.CANCEL;
    
    private float currentSliderX;
    
    private SmoothJoin sliderJoin;
    private SmoothJoin fingerJoin;
    
    public SliderView() {
        columnToDisplay = 0;
        currentSliderX = 0;
        slidingInProgress = false;
        
        sliderJoin = new SmoothJoin();
        sliderJoin.setInterpolator(Interpolator.BACK_END);
        sliderJoin.setBack(2.8f);
        sliderJoin.init(0);
        sliderJoin.setTarget(0f, 0f);
        
        fingerJoin = new SmoothJoin();
        fingerJoin.init(0);
        fingerJoin.setTarget(0f, 0.1f);
    }
    
    public void handleClassicEvent(float parentX, float parentY, Controller.TouchAction action) {
        //Convert to local coordinate
        float x = convertToLocalX(parentX);
        float y = convertToLocalY(parentY);
        
        if (!slidingInProgress && !isInside(x, y)) {
            //Event happens outside
            if (touchBeganIn) onTouchLost(x, y);
            touchBeganIn = false;
            return;
        }
        
        //Event inside        
        if (action == TouchAction.UP || action == TouchAction.CANCEL) {
            if (touchBeganIn) {
                onTouchLost(x, y);
                if (action == TouchAction.UP) onClick(x, y); //action completed
                touchBeganIn = false;
                return;
            }
        }
        
        if (isInside(x, y) && !touchBeganIn) {
            touchBeganIn = true;
            onTouchBegan(x, y);
        }
        
        return;
    }
    
    private boolean slidingJustFinished = false;
    @Override
    public void handleTouch(float parentX, float parentY, Controller.TouchAction action) {
        
        if (!acceptInput) return;

        handleClassicEvent(parentX, parentY, action);
        
        float x = convertToLocalX(parentX);
        float y = convertToLocalY(parentY);
        
        //Should we enter the sliding mode?
        if (isInside(x, y) && !slidingInProgress && action == TouchAction.MOVE) {
            float distance = (currentSliderX + x - firstTouchX) * (currentSliderX + x - firstTouchX) /* + (y - firstTouchY) * (y - firstTouchY)*/;
            if (distance >= fingerSwipeThreshold * fingerSwipeThreshold) {
                slidingInProgress = true;
                firstTouchX = x;
                firstTouchSliderX = currentSliderX;
                //fingerJoin.init(firstTouchSliderX);
                forwardTouchToChildren(parentX, parentY, TouchAction.CANCEL);
            }
        }
        
        if (slidingInProgress) {
            //fingerJoin.setTarget(firstTouchSliderX + (x - firstTouchX), 20f);
            //currentSliderX = fingerJoin.get();
            currentSliderX = firstTouchSliderX + (x - firstTouchX);
            if (currentSliderX > 0 || currentSliderX < - offsetBetweenChildren * (children.size()-1)) currentSliderX = firstTouchSliderX + (x - firstTouchX) * 0.3f;
        } else {
            if (!slidingJustFinished) forwardTouchToChildren(parentX, parentY, action);
        }
        
        slidingJustFinished = false;
    }

    
    @Override
    public void onTouchBegan(float x, float y) {
        firstTouchX = currentSliderX + x;
        //firstTouchSliderX = currentSliderX;
    }
    
    @Override
    public void onClick(float x, float y) {
        if (slidingInProgress) {
            //Decide to change the column index
            
            if (x - firstTouchX > offsetBetweenChildren * 0.1f) columnToDisplay--;
            if (firstTouchX - x > offsetBetweenChildren * 0.1f) columnToDisplay++;
            
            if (columnToDisplay < 0) columnToDisplay = 0;
            if (columnToDisplay >= children.size()) columnToDisplay = children.size() - 1;
            
            relaxSliderToIdealPosition();
            slidingJustFinished = true;
        }
        slidingInProgress = false;
    }
    
    public void relaxSliderToIdealPosition() {
        relaxSliderToIdealPosition(0.5f);
    }
    
    public void relaxSliderToIdealPosition(float time) {
        sliderJoin.init(currentSliderX);
        sliderJoin.setTarget(- columnToDisplay * offsetBetweenChildren, time);
    }
    
    public void moveToRightPanel() {
        columnToDisplay++;
        if (columnToDisplay >= children.size()) columnToDisplay = children.size() - 1;
        relaxSliderToIdealPosition(0.6f);
    }
    
    public void moveToLeftPanel() {
        columnToDisplay--;
        if (columnToDisplay < 0) columnToDisplay = 0;
        relaxSliderToIdealPosition(0.6f);
    }
    
    public void moveToFirstPanel() {
        columnToDisplay = 0;
        sliderJoin.init(currentSliderX);
        sliderJoin.setTarget(- columnToDisplay * offsetBetweenChildren, 1f, 0.5f);
    }
    
    @Override
    public void update() {
        
        sliderJoin.update();
        fingerJoin.update();
        
        if (!slidingInProgress) currentSliderX = sliderJoin.get();
        
        for (int i = 0; i < children.size(); i++) {
            Widget child = children.get(i);
            child.setPosition(currentSliderX + i * offsetBetweenChildren, child.getPosY());
        }
              
        super.update();
    }
    
    public boolean addShopPanel(ShopPanel shopPanel) {
        
        if (children.size() == 0) {
            shopPanel.arrowRight.setGlobalAlpha(1f);
            shopPanel.arrowRight.setOnClickListener(
                new TouchListener() {public void riseEvent(float x, float y) { moveToRightPanel(); } }
            );
        } else {
            shopPanel.arrowLeft.setGlobalAlpha(1f);
            shopPanel.arrowLeft.setOnClickListener(
                    new TouchListener() {public void riseEvent(float x, float y) { moveToLeftPanel(); } }
                );
            ShopPanel leftPanel = ((ShopPanel)children.get(children.size() - 1));
            leftPanel.arrowRight.setGlobalAlpha(1f);
            leftPanel.arrowRight.setOnClickListener(
                    new TouchListener() {public void riseEvent(float x, float y) { moveToRightPanel(); } }
            );
        }
        
        return super.addChild(shopPanel);
    }

}
