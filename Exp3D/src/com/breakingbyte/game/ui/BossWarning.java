package com.breakingbyte.game.ui;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Renderer.BlendingMode;

public class BossWarning extends RibbonMessage {
    
    public static final String TAG = "BossWarning";
    
    public BossWarning () {
        bgColor[0] = 1f;
        bgColor[1] = 0f;
        bgColor[2] = 0f;
        bgColor[3] = 0.9f;
        height = 45f;
        appearSpeed = 4f;
        disappearSpeed = 6f;
        bgSpeedAnimation = 5f;
        bgRepeat = 2;
    }
    
    private float borderRepeat = 10; //20;
    
    @Override
    public void update() {
        super.update();
        
        float offset = -10 * t; //-14*t;
        
        texCoords[0] = offset;                  texCoords[1] = 0.75f; //bottom left
        texCoords[2] = offset + borderRepeat;   texCoords[3] = 0.75f; //bottom right
        texCoords[4] = offset;                  texCoords[5] = 0.05f; //top left
        texCoords[6] = offset + borderRepeat;   texCoords[7] = 0.05f; //top right
        
        texCoordsBottom[0] = offset + borderRepeat;     texCoordsBottom[1] = 0.75f; //bottom left
        texCoordsBottom[2] = offset;                    texCoordsBottom[3] = 0.75f; //bottom right
        texCoordsBottom[4] = offset + borderRepeat;     texCoordsBottom[5] = 0.05f; //top left
        texCoordsBottom[6] = offset;                    texCoordsBottom[7] = 0.05f; //top right
        
        offset = 0.7f*t;
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
        
        
        //Renderer.setColor(1f, 1f, 1f, 1f);

        Renderer.Blending.setMode(BlendingMode.ADDITIVE);
        
        Renderer.setColor(1f, 1f, 1f, 0.4f);
        
        Renderer.unbindVBOs();
        
        //Arrow lines
        float stripeHeight = 4f;
        QuadVBO.drawQuadImmediate(FontTexture.ALIGN_CENTER, posY + height * zoomer.get() * 0.43f, Screen.ARENA_WIDTH, stripeHeight, texCoords);
        QuadVBO.drawQuadImmediate(FontTexture.ALIGN_CENTER, posY - height * zoomer.get() * 0.43f, Screen.ARENA_WIDTH, stripeHeight, texCoordsBottom);
        
        Renderer.Blending.resetMode();
        
        //BG text
        DynamicText text = TextureManager.txt_ui_boss_warning;
        
        text.textSize = 55f;
        text.setAlpha(0.4f);
        float textWidth = text.getWidth() + 5f;
        
        float offsetY = posY + 3.7f;
        float offsetX = 150 * t;
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

        
        //Blinking text
        float alphaTxt = (float)(1 + Math.cos(9*t)) * 0.5f;
        //Renderer.setColor(1f, 1f, 1f, alphaTxt);
        
        //Renderer.bindTexture(TextureManager.ui_boss_warning);
        //QuadVBO.drawQuad(FontTexture.ALIGN_CENTER, posY, width, width/4f);
        text.textSize = 20f;
        text.setAlpha(alphaTxt);
        text.setPosition(50, posY + 10.5f);
        text.render();
        
        Renderer.resetColor();
        
        Renderer.popMatrix();
        
    }
    

}
