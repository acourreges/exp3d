package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.shared.Renderer;

public class ImageWidget extends Widget {
    
    public float rotation = 0f;
    
    public void render() {
        super.render();
        
        if (!isVisibleOnScreen()) return;
        
        Renderer.pushMatrix();
        
        Renderer.unbindVBOs();
        
        texture.bind();
        
        if (posX == FontTexture.ALIGN_CENTER) {
            posX = Screen.ARENA_WIDTH / 2f;
        }
        
        Renderer.translate(posX, posY, 0);
        
        Renderer.scale(scale, scale, 1f);
        
        applyColor();
        Renderer.rotate(rotation, 0, 0, 1);
        QuadVBO.drawQuad(0, 0, width, height);
        Renderer.resetColor();
        
        Renderer.popMatrix();
        
    }

}
