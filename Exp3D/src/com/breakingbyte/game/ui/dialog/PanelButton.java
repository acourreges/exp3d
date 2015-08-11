package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.NinePatch;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class PanelButton extends WidgetContainer {
    
    protected NinePatch panel;
    public NinePatch innerBorder, glow;
    
    protected float[] idleColor, focusedColor, disabledColor;
    public float idleColorSpeed, focusedColorSpeed;
    protected SmoothJoin dynamicColor;
    
    protected SmoothJoin dynamicScale;
    
    protected boolean enabled;
    
    public float glowElapsed;
    public boolean glowing;
    
    public float focusScale = 1.13f;
    
    public boolean isInFocus = false;
    
    public PanelButton() {
        dynamicColor = new SmoothJoin(4);
        
        enabled = true;
        
        glowing = false;
        
        idleColor     = new float[] { 1f, 1f, 1f};
        focusedColor  = new float[] { 1f, 1f, 1f};
        disabledColor = new float[] { 1f, 1f, 1f};
        
        setIdleColor(0.5f, 0.6f, 0.9f);
        setFocusedColor(0.8f, 0.9f, 1f);
        idleColorSpeed = 2f;
        focusedColorSpeed = 100f;
        
        initColor();
        
        dynamicScale = new SmoothJoin();
        dynamicScale.init(1f);
        dynamicScale.setTarget(1f, 0f);
        
        panel = new NinePatch(0.5f, 0.5f, 0.5f, 0.5f);
        panel.setTexture(TextureManager.panelButton);
        
        innerBorder = new NinePatch(0.5f, 0.5f, 0.5f, 0.5f);
        innerBorder.setTexture(TextureManager.panelButtonFrame);
        innerBorder.setAlpha(0.6f);
        
        glow = new NinePatch(0.5f, 0.5f, 0.5f, 0.5f);
        glow.setTexture(TextureManager.panelButtonGlow);
        
        //
        addChild(panel);
        addChild(innerBorder);
        addChild(glow);
        
        
        setOnTouchBeganListener(
            new TouchListener() {
                public void riseEvent(float x, float y) { if (enabled) onGotFocus(); }
            });
        
        setOnTouchLostListener(
            new TouchListener() {
                public void riseEvent(float x, float y) { if (enabled) onLostFocus(); }
            });
        
    }
    
    public void setEnabled(boolean value) { enabled = value; }
    
    public boolean isEnabled() { return enabled; }
    
    public void setIdleColor(float red, float green, float blue) {
        idleColor[0] = red;
        idleColor[1] = green;
        idleColor[2] = blue;
    }
    
    public void setFocusedColor(float red, float green, float blue) {
        focusedColor[0] = red;
        focusedColor[1] = green;
        focusedColor[2] = blue;
    }
    
    public void setDisabledColor(float red, float green, float blue) {
        disabledColor[0] = red;
        disabledColor[1] = green;
        disabledColor[2] = blue;
    }
    
    public void initColor() {
        if (enabled) {
            dynamicColor.init(idleColor[0], idleColor[1], idleColor[2], 1f);
            dynamicColor.setTarget(idleColor[0], idleColor[1], idleColor[2], 1f, 0.0f);
        } else {
            dynamicColor.init(disabledColor[0], disabledColor[1], disabledColor[2], 1f);
            dynamicColor.setTarget(disabledColor[0], disabledColor[1], disabledColor[2], 1f, 0.0f);
        }
    }
    
    public void onGotFocus() {
        if (enabled) {
            dynamicColor.init(dynamicColor.get(0), dynamicColor.get(1), dynamicColor.get(2), 1f);
            dynamicColor.setTarget(focusedColor[0], focusedColor[1], focusedColor[2], 1f, focusedColorSpeed);
            dynamicScale.init(dynamicScale.get());
            dynamicScale.setTarget(focusScale, focusedColorSpeed * 0.3f);
            isInFocus = true;
        } else {
            
        }
    }
    
    public void onLostFocus() {
        if (enabled) {
            dynamicColor.init(dynamicColor.get(0), dynamicColor.get(1), dynamicColor.get(2), 1f);
            dynamicColor.setTarget(idleColor[0], idleColor[1], idleColor[2], 1f, idleColorSpeed, 0.2f);
            dynamicScale.init(dynamicScale.get());
            dynamicScale.setTarget(1f, idleColorSpeed);
            isInFocus = false;
        } else {
            
        }
    }
    
    @Override
    public void onClick(float x, float y) {
        if (!enabled) return;
        super.onClick(x, y);
    }
    
    @Override
    public void update() {
        super.update();
        dynamicColor.update();
        panel.setColor(dynamicColor.get(0), dynamicColor.get(1), dynamicColor.get(2));
        dynamicScale.update();
        
        if (glowing) {
            glowElapsed += Timer.delta;
            glow.setAlpha(MathUtil.getCyclicValue(0.1f, 1f, glowElapsed * 8f));
        } else {
            glow.setAlpha(0f);
        }
    }
    
    @Override
    public void setColor(float red, float green, float blue) {
        Log.w(TAG, "You should use setIdleColor() or setFocusColor() instead.");
        setIdleColor(red, green, blue);
    }
    
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        layoutChildren(width, height);
    }
    
    public float borderSize = 10f;
    public void layoutChildren(float width, float height) {
        panel.setUp(width, height, borderSize, borderSize, borderSize, borderSize);
        innerBorder.setUp(width, height, borderSize, borderSize, borderSize, borderSize);
        float glowBorder = borderSize * 1.25f;
        float sizeOffset = borderSize * 0.46f;
        glow.setUp(width + sizeOffset, height + sizeOffset, glowBorder, glowBorder, glowBorder, glowBorder);
    }
    
    public void render() {
        
        if (!isVisibleOnScreen()) return;
        
        Renderer.pushMatrix();
        
        Renderer.translate(posX, posY, 0);
        
        Renderer.scale(scale * dynamicScale.get(), scale * dynamicScale.get(), scale * dynamicScale.get());
        
        renderChildren();
        
        //Renderer.pushMatrix();
        //glow.scale = 1.1f;
        //glow.render();
        //Renderer.popMatrix();
        
        Renderer.popMatrix();
        
    }
    

}
