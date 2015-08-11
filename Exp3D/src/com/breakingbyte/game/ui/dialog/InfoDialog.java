package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;

public class InfoDialog extends Dialog {
    
    public DynamicText message;
    
    public DialogButton okButton;

    public InfoDialog() {
        okButton = new DialogButton(8);
        okButton.text.setAlignment(Align.CENTER);
        okButton.text.reset().printString("Ok").updateBuffers();
        
        message = new DynamicText(64);
        message.setAlignment(Align.CENTER);
        message.setAlpha(0.8f);
        message.textSize = 9f;
        
        addChild(message);
        addChild(okButton);
        
        setPosition(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f);
    }
    
    public void setTitle(String title) {
        this.title.reset().printString(title).updateBuffers();
    }
    
    public InfoDialog autoSize() {
        setSize(message.getWidth() + 7f * 2, message.getHeight() + 28 * 2);
        return this;
    }
    
    public void setSize(float width, float height) {
        super.setSize(width, height);
        layoutChildren(width, height);
    }
 
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        
        message.setPosition(0, height * 0.5f - 18f);
        
        float buttonYOffset = height * -0.5f + 12f;
        float buttonWidth = 38f, buttonHeight = 15f;
        okButton.setSize(buttonWidth, buttonHeight);
        okButton.setPosition(0, buttonYOffset);
    }
    
}
