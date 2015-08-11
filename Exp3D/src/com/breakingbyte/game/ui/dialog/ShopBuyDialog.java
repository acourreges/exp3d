package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.content.Article;
import com.breakingbyte.game.state.ShopState;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.ImageWidget;
import com.breakingbyte.game.ui.OrbWidget;
import com.breakingbyte.game.ui.DynamicText.Align;

public class ShopBuyDialog extends Dialog {
    
    public ImageWidget icon;
    
    public OrbWidget orb;
    
    public DynamicText 
        description,
        price,
        upgradeText,
        upgradeValue,
        tooExpensiveText;
    
    public DialogButton 
            buyButton,
            cancelButton;
    
    public Article article;
    
    public ShopBuyDialog() {
        icon = new ImageWidget();
        orb = new OrbWidget();
        price = new DynamicText(16);
        description = new DynamicText(128);
        
        buyButton = new DialogButton(8);
        buyButton.text.setAlignment(Align.CENTER);
        buyButton.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); buyButtonClicked(); } } );
        
        cancelButton = new DialogButton(8);
        cancelButton.text.setAlignment(Align.CENTER);
        cancelButton.text.reset().printString("Cancel").updateBuffers();
        cancelButton.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); hide(); } } );
        
        upgradeText = new DynamicText(16);
        
        upgradeValue = new DynamicText(32);
        upgradeValue.setAlignment(Align.LEFT);
        
        tooExpensiveText = new DynamicText(64);
        tooExpensiveText.setAlignment(Align.CENTER);
        tooExpensiveText.reset().printString("You don't have enough orbs!").updateBuffers();
        
        addChild(icon);
        addChild(description);
        addChild(upgradeText);
        addChild(upgradeValue);
        addChild(orb);
        addChild(tooExpensiveText);
        
        addChild(buyButton);
        addChild(cancelButton);
    }
    
    public void buyButtonClicked() {
        ShopState.articleBuyButtonClicked(article);
    }
    
    public void setValuesFromArticle(Article article) {    
        
        this.article = article;
        
        //boolean maxReached = article.isAtMaxLevel();
        
        title.reset().printString(article.getName()).updateBuffers();
        
        this.icon.setTexture(article.getIcon());
        
        description.setAlignment(Align.CENTER);
        description.reset();
        article.printLongDescription(description);
        description.updateBuffers();
        
        int nbChar = article.getNextLevelLabel();
        upgradeValue.reset().printCharArray(article.charBuffer, nbChar).updateBuffers();
        upgradeValue.setPosX(upgradeValue.getWidth() * -0.5f + 2f);
        
        upgradeText.reset().printString("Upgrade for ").printInteger(article.getPrice()).updateBuffers();
        
        boolean canBeBought = article.canBeBoughtByPlayer();
        
        tooExpensiveText.setAlpha(canBeBought? 0f :0.8f);
        
        buyButton.glowing = true;
        buyButton.text.reset().printString(canBeBought?"Buy":"Get Orbs").updateBuffers();
    }

    public void setSize(float width, float height) {
        super.setSize(width, height);
        layoutChildren(width, height);
    }
    
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        
        panel.setAlpha(1f);
        //panel.setColor(0.3f, 0.6f, 1f);
        
        float offsetY = height * 0.5f - 24.5f;
        
        icon.setAlpha(0.8f);
        icon.setSize(22, 22);
        icon.setPosition(0, offsetY);
        
        float textSize = 7.8f;
        
        offsetY -= 11f;
        description.setAlpha(0.7f);
        description.textSize = textSize;
        description.setPosY(offsetY);
        
        textSize = 10f;
        offsetY -= 25f;
        upgradeText.textSize = textSize;
        upgradeText.setAlpha(0.7f);
        upgradeText.setPosition(-35f, offsetY);
        
        float orbSize = 7f;
        orb.setSize(orbSize, orbSize);
        orb.setPosition(upgradeText.getPosX() + upgradeText.getWidth() + 5f, offsetY - 5f);
        
        offsetY -= 7f;
        upgradeValue.setColor(0, 200, 255);
        upgradeValue.textSize = textSize;
        upgradeValue.setAlpha(0.7f);
        upgradeValue.setPosY(offsetY);
        
        offsetY -= 15f;

        tooExpensiveText.setColor(1f, 0, 0);
        tooExpensiveText.textSize = 6.5f;
        tooExpensiveText.setPosition(0, offsetY + 6f);
        
        
        float buttonYOffset = height * -0.5f + 12f;
        float buttonXOffset = 21;
        float buttonWidth = 38f, buttonHeight = 15f;
        float buttonTextSize = 9f, buttonTextY = 4.5f;
        
        buyButton.text.textSize = buttonTextSize;
        buyButton.text.setPosY(buttonTextY);
        buyButton.setSize(buttonWidth, buttonHeight);
        buyButton.setPosition(-buttonXOffset, buttonYOffset);
        
        cancelButton.text.textSize = buttonTextSize;
        cancelButton.text.setPosY(buttonTextY);
        cancelButton.setSize(buttonWidth, buttonHeight);
        cancelButton.setPosition(buttonXOffset, buttonYOffset);
        
    }

}
