package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.content.Article;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.ImageWidget;
import com.breakingbyte.game.ui.LoadingBar;
import com.breakingbyte.game.ui.OrbWidget;
import com.breakingbyte.game.ui.WidgetContainer;
import com.breakingbyte.game.util.SmoothJoin;

public class ShopItem extends WidgetContainer {
    
    public ImageWidget icon;
    
    public DynamicText itemName;
    public DynamicText currentStatus;
    public DynamicText nextStatus;
    
    public PriceButton priceButton;
    
    public Article article;
    
    public ImageWidget bg;
    public SmoothJoin bgJoin;
    
    public ImageWidget separator;
    
    public ImageWidget arrow;
    
    public LoadingBar progressBar;
    
    public ShopItem(Article article) {
        
        this.article = article;
        article.itemPanel = this;
        
        icon = new ImageWidget();
        bg = new ImageWidget();
        bg.setTexture(TextureManager.blank);
        
        separator = new ImageWidget();
        separator.setTexture(TextureManager.blank);
        
        bgJoin = new SmoothJoin();
        bgJoin.init(0f);
        bgJoin.setTarget(0f, 0f);
        
        itemName = new DynamicText(32);
        
        currentStatus = new DynamicText(32);
        nextStatus = new DynamicText(32);
        
        priceButton = new PriceButton();
        
        progressBar = new LoadingBar();
        progressBar.setLoading(0f);
        
        arrow = new ImageWidget();
        arrow.rotation = 180f;
        arrow.setTexture(TextureManager.arrowIcon);

        addChild(bg);
        addChild(icon);
        addChild(priceButton);
        addChild(itemName);
        //addChild(currentLabel);
        addChild(currentStatus);
        //addChild(nextLabel);
        addChild(nextStatus);
        addChild(progressBar);
        addChild(arrow);
        addChild(separator);
        
        setOnTouchBeganListener(
            new TouchListener() {
                public void riseEvent(float x, float y) { onGotFocus(); }
            });
        
        setOnTouchLostListener(
            new TouchListener() {
                public void riseEvent(float x, float y) { onLostFocus(); }
            });
        
        loadFromArticle();

    }
    
    public void loadFromArticle() {
        
        boolean maxReached = article.isAtMaxLevel();
        
        this.icon.setTexture(article.getIcon());
        
        itemName.reset().printString(article.getName()).updateBuffers();
        
        this.priceButton.setPrice(article.getPrice());
        this.priceButton.setGlobalAlpha(maxReached? 0f : 1f);
        this.priceButton.setOverPricedColor(!article.canBeBoughtByPlayer());
        

        //'Now' and 'Next' values
        if (!maxReached) {
            int nbChar = article.getCurrentLevelShortLabel();
            currentStatus.reset().printCharArray(article.charBuffer, nbChar).updateBuffers();
            
            nbChar = article.getNextLevelLabel();
            nextStatus.reset().printCharArray(article.charBuffer, nbChar).updateBuffers();
            
            arrow.setGlobalAlpha(1f);
        } else {
            int nbChar = article.getCurrentLevelLabel();
            currentStatus.reset().printCharArray(article.charBuffer, nbChar).updateBuffers();
            
            nextStatus.reset().updateBuffers();
            
            arrow.setGlobalAlpha(0f);
        }
        
        //Progress bar value
        progressBar.setLoading((float)article.currentLevel / article.getMaxLevel());
        
        layoutChildren(this.width, this.height);
    }

    @Override
    public void update() {
        bgJoin.update();
        bg.setAlpha(bgJoin.get());
        super.update();
    }
    
    public void setSize(float width, float height) {
        super.setSize(width, height);
        layoutChildren(width, height);
    }
    
    public void layoutChildren(float width, float height) {
        
        bg.setSize(width - 5f, height);
        
        separator.setAlpha(0.3f);
        separator.setSize(width - 5f, 0.3f);
        separator.setPosY(height * -0.5f);
        
        float marginLeft = - width * 0.5f + 3.6f;
        
        float currentX = marginLeft;
        
        float imgSize = height * 0.90f;
        icon.setSize(imgSize, imgSize);
        icon.setPosition(currentX + imgSize * 0.5f, 0);
        icon.setAlpha(0.85f);
        currentX += imgSize;
        
        currentX += 1.0;
        
        itemName.textSize = 7f;
        itemName.setAlpha(0.8f);
        itemName.setPosition(currentX, 7.8f);
        
        //Sub texts
        float subTextSize = 5.8f; 
        float xOffsetValue = 0;//12f;
        
        float subY2 = -1.9f;
        
        
        currentStatus.textSize = subTextSize;
        currentStatus.setAlpha(0.8f);
        currentStatus.setPosition(currentX + xOffsetValue, subY2);
        currentStatus.setColor(190, 190, 255);
        
        nextStatus.textSize = subTextSize;
        nextStatus.setAlpha(0.8f);
        nextStatus.setPosition(currentX + xOffsetValue + currentStatus.getWidth() + 5f, subY2);
        nextStatus.setColor(0, 200, 255);
        
        progressBar.setPosition(currentX, subY2 - 1.3f);
        //progressBar.setPosition(currentX, subY2 - 1.8f); ////
        progressBar.setBgColor(0f, 0.3f, 0.7f);
        progressBar.setBgColor(0f, 0.67f, 1f);////
        progressBar.setup(25, 5f, 2f);
        //progressBar.setup(25, 6f, 2f); ////
        
        imgSize = 4f;
        arrow.setSize(imgSize, imgSize);
        float arrowColor = 0.9f;
        arrow.setColor(arrowColor, arrowColor, arrowColor);
        arrow.setAlpha(0.7f);
        arrow.setPosition(currentX + xOffsetValue + currentStatus.getWidth() + 2.5f, subY2 - 3.0f);
        
        //Price button
        float buyWidth = 18, buyHeight = 16;
        priceButton.setSize(buyWidth, buyHeight);
        priceButton.setPosition(width * 0.5f - buyWidth * 0.5f - 4f, 0);
        
        priceButton.price.textSize = 8f;
        priceButton.price.setPosY(3.8f);
        //priceButton.price.setPosX(priceButton.price.getPosX() + 0f);
        
    }
    
    public void onGotFocus() {
        bgJoin.setTarget(0.15f, 12f);
    }
    
    public void onLostFocus() {
        bgJoin.setTarget(0f, 12f);
    }
    
    public class PriceButton extends WidgetContainer {
        
        public DynamicText price;
        
        public OrbWidget orb;
        
        public PriceButton() {
            
            orb = new OrbWidget();
            
            addChild(orb);
            
            price = new DynamicText(5);
            price.setAlpha(0.9f);
            addChild(price);
        }
        
        public void setOverPricedColor(boolean overpriced) {
            if (overpriced) orb.orb.setColor(1f, 0.5f, 0f);
            else orb.orb.setColor(1f, 0.5f, 1f);
        }
        
        public void setPrice(int value) {
            price.reset().printInteger(value).updateBuffers();
            price.setPosX(price.getWidth() * -0.5f + 0.3f);
        }
    }

}
