package com.breakingbyte.game.ui;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.shared.Renderer;

public class RoundGlass extends Widget {
    
    static float texCoords[] = {
            0.5f, 0.5f,  //bottom left
            1.0f, 0.5f,  //bottom right
            0.5f, 0.0f,  //top left
            1.0f, 0.0f   //top right
    };
    
    static float texCoordsBG[] = {
            0.5f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            0.5f, 0.5f,  //top left
            1.0f, 0.5f   //top right
    };
    
    public static RoundGlass generateLifeCounter() {
        RoundGlass result = new RoundGlass();        
        return result;
    }
    
    public void render() {
        TextureManager.uiLife.bind();
         
        Renderer.setColor(UI.defaultColor[0], UI.defaultColor[1], UI.defaultColor[2], 1f);
        
        QuadVBO.drawQuadImmediate(posX, posY, width, height, texCoordsBG);
        Renderer.resetColor();
        QuadVBO.drawQuadImmediate(posX, posY, width, height, texCoords);        
    }
    
}
