package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.ui.ImageWidget;


public class SideButton extends PanelButton {
    
    ImageWidget image;
    
    boolean onRight;
    
    public SideButton() {
        
        image = new ImageWidget();
        addChild(image);
        
        onRightSide();
        
        focusScale = 1.08f;
        
        borderSize = 15f;
        
        //setIdleColor(130f / 255f, 133f / 255f, 255.0f / 255f);
        setIdleColor(110f / 255f, 171f / 255f, 255.0f / 255f);        
        setFocusedColor(110f / 255f, 219f / 255f, 255.0f / 255f);
        setDisabledColor(0.9f, 0.9f, 0.9f);
        
        initColor();
        
        panel.setAlpha(0.8f);
        
        
        setSize (90, 25f);
    }
    
    public void setImage(Texture texture, float width, float height, float alpha) {
        image.setTexture(texture);
        image.setSize(width, height);
        image.setAlpha(alpha);
    }
    
    public void onLeftSide() {
        onRight = false;
        panel.flipU = innerBorder.flipU = glow.flipU = false;
        panel.flipV = innerBorder.flipV = glow.flipV = true;
    }
    
    public void onRightSide() {
        onRight = true;
        panel.flipU = innerBorder.flipU = glow.flipU = true;
        panel.flipV = innerBorder.flipV = glow.flipV = true;
    }
    
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        image.setPosition(width * 0.33f * (onRight? -1 : 1), height * 0.295f);
        //image.setSize(14, 14);
        //image.setAlpha(0.9f);
    }


}
