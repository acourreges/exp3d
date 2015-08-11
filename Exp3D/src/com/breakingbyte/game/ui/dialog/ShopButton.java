package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.OrbWidget;
import com.breakingbyte.game.ui.DynamicText.Align;


public class ShopButton extends PanelButton {
    
    public DynamicText label;
    
    private OrbWidget orbAnimation;
    
    public ShopButton() {
        
        borderSize = 10f;
        panel.symmetryFromRight = true;
        innerBorder.symmetryFromRight = true;
        glow.symmetryFromRight = true;
        panel.setAlpha(0.8f);
        
        label = new DynamicText("Upgrade Shop".length());
        label.setAlignment(Align.LEFT);
        label.textSize = 12f;
        label.reset();
        label
            //.printString("Upgrade")
            //.newLine()
            .printString("Shop")
            .updateBuffers();
        label.setAlpha(0.8f);
        //label.setColor(1f, 0.8f, 0.8f);
        addChild(label);
        
        orbAnimation = new OrbWidget();
        orbAnimation.setSize(12, 12);
        orbAnimation.setAlpha(1f);
        addChild(orbAnimation);
        
        setIdleColor(130f / 255f, 133f / 255f, 255.0f / 255f);
        //setIdleColor(255f / 255f, 110f / 255f, 110.0f / 255f);
        //setIdleColor(255f / 255f, 163f / 255f, 163.0f / 255f);
        
        setFocusedColor(110f / 255f, 219f / 255f, 255.0f / 255f);
        setDisabledColor(0.9f, 0.9f, 0.9f);
        
        initColor();
        
        setSize (90, 25f);
    }

    
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        label.setPosition(-7f, /*8.7f*/ 6.0f);
        orbAnimation.setPosition(-16f, 0f);
    }

}
