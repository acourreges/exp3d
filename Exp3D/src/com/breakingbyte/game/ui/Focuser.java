package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class Focuser extends Widget {
    
    public float scale = 32;
    
    public SmoothJoin alphaJoin;
    
    private float t;
    
    public Focuser() {
        t = 0f;
        alphaJoin = new SmoothJoin();
        alphaJoin.init(0f);
        startDisappearAnimation();
    }
    
    public void reset() {
        alphaJoin.init(0f);
        alphaJoin.setTarget(0f, 0f);
    }
    
    public void update() {
        t += Timer.delta;
        alphaJoin.update();
        posX = Engine.player.posX + 16.27f;
        posY = Engine.player.posY + 2.5f;
    }
    
    public void startAppearAnimation() {
        t = 0f;
        alphaJoin.init(alphaJoin.get());
        alphaJoin.setTarget(1.0f, 5f);
    }
    
    public void startDisappearAnimation() {
        alphaJoin.init(alphaJoin.get());
        alphaJoin.setTarget(0f, 5f);
    }
    
    public void render() {
        
        if (alphaJoin.get() < 0.005f) return;
        
        Renderer.Blending.resetMode();
        
        Renderer.pushMatrix();
        Renderer.unbindVBOs();
        
        Renderer.setColor(1f, 1f, 1f, alphaJoin.get());
        
        TextureManager.focuser.bind();
        QuadVBO.drawQuad(posX, posY, scale * 2f, scale);
        
        float labelAlpha = alphaJoin.get() * (float)(1 + Math.sin(18*t));

        DynamicText text = TextureManager.txt_ui_ship_focuser;
        text.setAlpha(labelAlpha);
        text.textSize = 17f;
        text.setPosition(posX + 16.5f, posY + 20.7f);
        text.render();
        
        Renderer.resetColor();
        Renderer.popMatrix();
    }

}
