package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.level.Level.LevelID;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;

public class LevelTitle extends RibbonMessage {
    
    public static final String TAG = "LevelTitle";
    
    public LevelID levelId = LevelID.Level1;
    
    public LevelTitle () {
        bgColor[0] = 0.2f;
        bgColor[1] = 0.2f;
        bgColor[2] = 1f;
        bgColor[3] = 0.9f;
        height = 45f;
        appearSpeed = 4f;
        disappearSpeed = 6f;
        bgSpeedAnimation = 2.5f;
        bgRepeat = 2;
    }
    
    @Override
    public void update() {
        super.update();
        
        float offset = -14*t;
        
        texCoords[0] = 0.5f;                  texCoords[1] = 0.75f; //bottom left
        texCoords[2] = 0.5f;   texCoords[3] = 0.75f; //bottom right
        texCoords[4] = 0.5f;                  texCoords[5] = 0.05f; //top left
        texCoords[6] = 0.5f;   texCoords[7] = 0.05f; //top right
        
        offset = 0.2f*t;
        float textRepeat = 0.5f;
        bgTexttexCoords[0] = offset;                    bgTexttexCoords[1] = 1f; //bottom left
        bgTexttexCoords[2] = offset + textRepeat;       bgTexttexCoords[3] = 1f; //bottom right
        bgTexttexCoords[4] = offset;                    bgTexttexCoords[5] = 0f; //top left
        bgTexttexCoords[6] = offset + textRepeat;       bgTexttexCoords[7] = 0f; //top right
        
        
    }
    
    float texCoordsBottom[] = {
            0.0f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            0.0f, 0.0f,  //top left
            1.0f, 0.0f   //top right
    };
    
    float texCoords[] = {
            0.0f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            0.0f, 0.0f,  //top left
            1.0f, 0.0f   //top right
    };
    
    float bgTexttexCoords[] = {
            0.0f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            0.0f, 0.0f,  //top left
            1.0f, 0.0f   //top right
    };
    
    @Override
    public void render() {
        if (!isVisible()) return;
        super.render();
        
        if (!finishedAppear) return;
        if (!appearing) return;
        
        Renderer.pushMatrix();
        
        //Renderer.resetColor();

        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        
        Renderer.setColor(1f, 1f, 1f, 0.3f);
        
        Renderer.unbindVBOs();
        
        //Arrow lines
        QuadVBO.drawQuadImmediate(FontTexture.ALIGN_CENTER, posY + height * zoomer.get() * 0.45f, Screen.ARENA_WIDTH, 1.3f, texCoords);
        QuadVBO.drawQuadImmediate(FontTexture.ALIGN_CENTER, posY - height * zoomer.get() * 0.45f, Screen.ARENA_WIDTH, 1.3f, texCoords);
        
        Renderer.Blending.resetMode();
        
        //BG text
        DynamicText text = TextureManager.txt_ui_levels.get(levelId);
        
        //Renderer.pushMatrix();
        //Renderer.translate(50, posY, 0);
        
        text.textSize = 55f;
        text.setAlpha(0.2f);
        float textWidth = text.getWidth() + 5f;
        
        float offsetY = posY + 2.5f;
        float offsetX = 70 * t;
        int nbExtra = (int)(offsetX / textWidth);
        offsetX -= textWidth * nbExtra;
        
        
        Renderer.pushMatrix();
        Renderer.translate(0, offsetY, 0);
        Renderer.scale(1f, zoomer.get(), 1f);
        Renderer.translate(0, text.textSize * 0.5f, 0);
        text.setPosition(- offsetX, 0);
        text.render();
        text.setPosition(- offsetX + textWidth, 0);
        text.render();
        Renderer.popMatrix();
        
        //Renderer.popMatrix();
        
        //Blinking text
        float alpha = t / 0.8f;
        if (alpha >= 0.8f) alpha = 0.8f;
        //Renderer.setColor(1f, 1f, 1f, alpha);//(float)(1 + Math.cos(9*t)) * 0.5f);
        
        text.textSize = 20f;
        text.setAlpha(alpha);
        text.setPosition(50, posY + 10.5f);
        text.render();
        //Renderer.bindTexture(TextureManager.ui_boss_warning);
        //QuadVBO.drawQuad(FontTexture.ALIGN_CENTER, posY, width, width/4f);

        
        Renderer.resetColor();
        
        Renderer.popMatrix();
        
    }
    

}
