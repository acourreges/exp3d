package com.breakingbyte.game.ui;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.shared.Renderer;

public class PauseButton {
    
    protected NinePatch panel;
    
    public float posX, posY;
    
    public float alphaBack = 1f, alphaImg = 1f;
    
    public PauseButton() {
        panel = new NinePatch(0.5f, 0.5f, 0.5f, 0.5f);
        panel.setTexture(TextureManager.panelButtonFrame);
        
        float border = 7f;
        panel.setUp(border * 2f, border * 2f, border, border, border, border);
        panel.setAlpha(0.6f);
    }
    
    public void render() {
        Renderer.pushMatrix();
        
        float border = 7f;
        panel.setUp(border * 2f, border * 2.1f, border, border, border, border);
        panel.render();
        
        TextureManager.powerUpTimewarp.bind();
        Renderer.setColor(1f, 1f, 1f, 0.5f);
        QuadVBO.drawQuad(0, 0, 10, 10);
        
        Renderer.popMatrix();
    }
    
}
