package com.breakingbyte.game.ui.dialog;

import java.util.ArrayList;

import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.ImageWidget;
import com.breakingbyte.game.ui.Widget;
import com.breakingbyte.game.ui.DynamicText.Align;

public class ShopPanel extends Dialog {
    
    public ArrayList<Widget> listItems;
    
    public ImageWidget arrowLeft, arrowRight;
    
    public ShopPanel() {
        listItems = new ArrayList<Widget>();
        panel.symmetryFromRight = true;
        panel.setAlpha(0.9f);
        title.setAlignment(Align.CENTER);
        title.setPosX(0);
        title.reset();
        
        arrowLeft = new ImageWidget();
        arrowLeft.setGlobalAlpha(0f);
        arrowLeft.setTexture(TextureManager.arrowIcon);
        addChild(arrowLeft);
        
        arrowRight = new ImageWidget();
        arrowRight.rotation = 180;
        arrowRight.setGlobalAlpha(0f);
        arrowRight.setTexture(TextureManager.arrowIcon);
        addChild(arrowRight);
        
        showInstant();
    }
    
    public boolean addListItemChild(Widget child) {
        if (!super.addChild(child)) return false;
        listItems.add(child);
        layoutChildren(width, height);
        return true;
    }
    
    public void setValues(String title) {        
        this.title.reset().printString(title).updateBuffers();
    }

    public void setSize(float width, float height) {
        super.setSize(width, height);
        layoutChildren(width, height);
    }
    
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        
        title.setPosition(0, height * 0.5f -2.5f);
        
        float currentY = height * 0.5f - 13f;
        
        for (int i = 0; i < listItems.size(); i++) {
            Widget child = listItems.get(i);
            float childHeight = child.getHeight();
            child.setSize(width, childHeight);
            child.setPosition(0 ,currentY - childHeight * 0.5f);
            currentY -= childHeight;
        }
        
        float arrowSize = 7f;
        arrowLeft.setSize(arrowSize, arrowSize);
        arrowRight.setSize(arrowSize, arrowSize);
        
        float imgOffset = 7f;
        
        arrowRight.setColor(0.7f, 0.8f, 1f);
        arrowRight.setAlpha(0.9f);
        arrowRight.setPosition(width * 0.5f - 6.5f, height * 0.5f - imgOffset);
        
        arrowLeft.setColor(0.7f, 0.8f, 1f);
        arrowLeft.setAlpha(0.9f);
        arrowLeft.setPosition(-width * 0.5f + 6.5f, height * 0.5f - imgOffset);
        
    }

}
