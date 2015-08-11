package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.ui.DynamicText;

public class DialogButton extends PanelButton {
    
    public DynamicText text;
    
    public DialogButton(int maxTextLength) {
        
        //glowing = true;
        
        borderSize = 6f;
        
        text = new DynamicText(maxTextLength);
        text.setPosition(0, 5);
        text.setAlpha(0.8f);
        text.textSize = 10f;
        
        addChild(text);
        
        setSize(50, 14);
        
        setIdleColor(0.5f, 0.6f, 0.9f);
        setFocusedColor(0.5f, 0.8f, 1f);
        idleColorSpeed = 2f;
        focusedColorSpeed = 100f;
        
        initColor();
    }


}
