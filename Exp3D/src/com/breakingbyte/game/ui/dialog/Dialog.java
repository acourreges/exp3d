package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.NinePatch;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;

public class Dialog extends WidgetContainer {
    
    private SmoothJoin zoomer;
    
    public SmoothJoin alphaizer;
    
    public NinePatch panel;
    
    public DynamicText title;
    
    protected float appearSpeed;
    
    private boolean isOpen;
    
    public Dialog() {
        
        posX = Screen.ARENA_WIDTH * 0.5f;
        posY = Screen.ARENA_HEIGHT * 0.5f;
        
        isOpen = false;
        
        setColor(0.3f, 0.4f, 1f);
        zoomer = new SmoothJoin();
        //zoomer.setInterpolator(Interpolator.SINUSOIDAL);
        alphaizer = new SmoothJoin();
        //alphaizer.setInterpolator(Interpolator.SINUSOIDAL);
        
        appearSpeed = 4f;
        
        panel = new NinePatch(0.5f, 0.5f, 0.5f, 0.5f);
        panel.setTexture(TextureManager.panel);
        panel.setColor(0.3f, 0.4f, 1f);

        title = new DynamicText(20);
        title.textSize = 9f;
        title.setAlpha(0.8f);
        
        addChild(panel);
        addChild(title);
        
        acceptInput = false;
    }
    
    public void reset() {
        isOpen = false;
        acceptInput = false;
        zoomer.init(0.001f);
        zoomer.setTarget(0.001f, 0f);
        alphaizer.init(0);
        alphaizer.setTarget(0f, 0f);
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public void setSize(float width, float height) {
        super.setSize(width, height);
        layoutChildren(width, height);
    }
    
    public void layoutChildren(float width, float height) {
        panel.setUp(width, height, 20, 20, 20, 20);
        title.setPosition(-width * 0.5f + 5f, height * 0.5f -2.5f);
    }
    
    public void show() {
        isOpen = true;
        acceptInput = false;
        zoomer.init(0.001f);
        zoomer.setTarget(1f, appearSpeed);
        alphaizer.init(0);
        alphaizer.setTarget(1f, appearSpeed);
    }
    
    public void showInstant() {
        isOpen = true;
        acceptInput = true;
        zoomer.init(1f);
        zoomer.setTarget(1f, 0f);
        alphaizer.init(1);
        alphaizer.setTarget(1f, 0f);
    }
    
    public void hide() {        
        isOpen = false;
        acceptInput = false;
        zoomer.init(zoomer.get());
        zoomer.setTarget(1.1f, appearSpeed * 1.8f);
        alphaizer.init(alphaizer.get());
        alphaizer.setTarget(0f, appearSpeed * 1.8f);
    }
    
    public void update() {
        zoomer.update();
        alphaizer.update();
        setGlobalAlpha(alphaizer.get());
        
        if (alphaizer.get() > 0.9f) acceptInput = true;
        if (!isOpen) acceptInput = false;
        if (alphaizer.get() > 0.01f) super.update();
    }
    
    @Override
    public void render() {
        
        if (!isVisibleOnScreen()) return;
        
        //We need good dithering
        Renderer.Dithering.enable();
        
        Renderer.pushMatrix();
        
        Renderer.translate(posX, posY, 0);
        
        Renderer.scale(scale * zoomer.get(), scale * zoomer.get(), 1f);
        
        renderChildren();
        
        Renderer.popMatrix();
        
        Renderer.Dithering.disable();
    }

}
