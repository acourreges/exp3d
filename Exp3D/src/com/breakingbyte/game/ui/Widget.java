package com.breakingbyte.game.ui;

import java.util.ArrayList;

import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.engine.Controller.TouchAction;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.anim.Animation;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;

public class Widget {
    
    public static final String TAG = "Widget";
    
    protected float posX, posY;
    protected float width = 10f;
    protected float  height = 10f;
    protected float alpha = 1f; //Intrinsic widget alpha
    protected float globalAlpha = 1f; //Passed along the father-children chain to determine the final alpha value
    protected float scale = 1f;
    private float[] color = new float[]{1f, 1f, 1f};
    
    protected float renderLeft, renderTop, renderRight, renderBottom;
    
    protected Widget parent = null;
    
    protected Texture texture = TextureManager.blank;
    
    public boolean acceptInput = true;
    
    protected ArrayList<Animation> animations = new ArrayList<Animation>(); 
    
    public static enum OriginX {
        LEFT, CENTER, RIGHT
    }
    public static enum OriginY {
        TOP, CENTER, BOTTOM
    }
    
    protected OriginX originX = OriginX.CENTER;
    protected OriginY originY = OriginY.CENTER;
    
    public void setPosition(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }
    
    public void setPosX(float value) { this.posX = value; }
    
    public void setPosY(float value) { this.posY = value; }
    
    public float getPosX() { return posX; }
    
    public float getPosY() { return posY; }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public void setSizeFullScreen() {
        setSize(Screen.ARENA_WIDTH * 2f, Screen.ARENA_HEIGHT * 2f);
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    
    public float getAlpha() {
        return alpha;
    }
    
    public void setGlobalAlpha(float alpha) {
        globalAlpha = alpha;
    }
    
    public float getGlobalAlpha() {
        if (parent == null) return globalAlpha;
        return parent.getGlobalAlpha() * globalAlpha;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    public Widget getParent() {
        return parent;
    }
    
    public boolean setParent(Widget parent) {
        if (this.parent != null) {
            Log.e(TAG, "Widget already has a parent!");
            return false;
        }
        this.parent = parent;
        return true;
    }
    
    public void setTexture(Texture text) {
        texture = text;
    }
    
    public void addAnimation(Animation anim) {
        anim.setWidget(this);
        animations.add(anim);
    }
    
    public void removeAnimation(Animation anim) {
        animations.remove(anim);
        anim.setWidget(null);
    }
    
    public void removeAllAnimations() {
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).setWidget(null);
        }
        animations.clear();
    }
    
    public void resetAnimations() {
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).reset();
        }
    }
    
    public void setColor(float red, float green, float blue) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
    }
    
    public void setColor(int red, int green, int blue) {
        color[0] = red / 255f;
        color[1] = green / 255f;
        color[2] = blue / 255f;
    }
    
    public void setColor(float[] color3f) {
        color[0] = color3f[0];
        color[1] = color3f[1];
        color[2] = color3f[2];
    }
    
    public void applyColor() {
        Renderer.setColor(color[0], color[1], color[2], getGlobalAlpha() * alpha);
    }
    
    public boolean isVisibleOnScreen() {
        return getGlobalAlpha() > 0.001f && !isOutOfScreen();
    }
    
    public void update() {
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).update();
        }
        if (parent == null) {
            updateRenderBox(0, Screen.ARENA_HEIGHT, Screen.ARENA_WIDTH, 0);
        }
    }
    
    public void updateRenderBox(float parentBoxLeft, float parentBoxTop, float parentBoxRight, float parentBoxBottom) {
        renderLeft   = convertToLocalX(parentBoxLeft);
        renderRight  = convertToLocalX(parentBoxRight);
        renderTop    = convertToLocalY(parentBoxTop);
        renderBottom = convertToLocalY(parentBoxBottom);
    }
    
    public boolean isOutOfScreen() {
        boolean invisible =  - width / 2f > renderRight
                            || width / 2f < renderLeft
                            || height / 2f < renderBottom
                            || height / -2f > renderTop;
        //invisible = false;
        return invisible;
                
    }
    
    public void render() {
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).onRenderPass();
        }
    }
    
    public float convertToLocalX(float inX) {
        return (inX - posX) / scale;
    }
    
    public float convertToLocalY(float inY) {
        return (inY - posY) / scale;
    }
    
    public boolean isInside(float x, float y) {
        final float top = height / 2f;
        final float bottom = top - height;
        final float left = - width / 2f;
        final float right = left + width;
        
        if (
                y > top
             || y < bottom
             || x < left
             || x > right
        ) return false;
        
        
        return true;
    }
    
    
    protected boolean touchBeganIn = false;
    
    public void handleTouch(float parentX, float parentY, Controller.TouchAction action) {
        
        if (!acceptInput) return;
        
        //Log.d(TAG, "Handle touch: " + parentX + " , " + parentY);
        
        //Convert to local coordinate
        float x = convertToLocalX(parentX);
        float y = convertToLocalY(parentY);
        
        //Log.d(TAG, "Local coord: " + x + " , " + y + "  " + this.toString());
        
        if (!isInside(x, y)) {
            //Event happens outside
            if (touchBeganIn) onTouchLost(x, y);
            touchBeganIn = false;
            return;
        }
        
        //Event inside
        
        //Might need an event queue for this...
        /*
        if (action == TouchAction.UP || action == TouchAction.CANCEL) {
            if (touchBeganIn) {
                onClick(); //action completed
                return;
            }
        }
        
        if (action == TouchAction.DOWN) {
            onTouchBegan();
        }
        */
        
        if (action == TouchAction.UP || action == TouchAction.CANCEL) {
            if (touchBeganIn) {
                onTouchLost(x, y);
                if (action == TouchAction.UP) onClick(x, y); //action completed
                touchBeganIn = false;
                return;
            }
        }
        
        if (!touchBeganIn) {
            touchBeganIn = true;
            onTouchBegan(x, y);
        }
        
        return;
    }
    

    public interface TouchListener {
        public void riseEvent(float x, float y);
    }
    
    private TouchListener onTouchBeganListener = null;
    
    private TouchListener onTouchLostListener = null;
    
    private TouchListener onClickListener = null;
    
    public void setOnTouchBeganListener(TouchListener listener) {
        onTouchBeganListener = listener;
    }
    
    public void setOnTouchLostListener(TouchListener listener) {
        onTouchLostListener = listener;
    }
    
    public void setOnClickListener(TouchListener listener) {
        onClickListener = listener;
    }
    
    public void onTouchBegan(float x, float y) {
        //Log.d(TAG, "Touch began " + this.toString());
        if (onTouchBeganListener != null) onTouchBeganListener.riseEvent(x, y);
    }
    
    public void onTouchLost(float x, float y) {
        //Log.d(TAG, "Touch lost " + this.toString());
        if (onTouchLostListener != null) onTouchLostListener.riseEvent(x, y);
    }
    
    public void onClick(float x, float y) {
        //Log.d(TAG, "Touch click " + this.toString());
        if (onClickListener != null) onClickListener.riseEvent(x, y);
    }
}


