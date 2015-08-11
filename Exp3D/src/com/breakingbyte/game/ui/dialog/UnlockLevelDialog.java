package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.content.Article;
import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.game.level.LevelInfo;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.OrbWidget;
import com.breakingbyte.wrap.iap.IAPManager;
import com.breakingbyte.wrap.shared.Timer;

public class UnlockLevelDialog extends Dialog {
    
    public OrbWidget orb;
    
    public DynamicText 
        buyText,
        unlockText,
        currentCredits,
        explanationTitle,
        explanationText,
        moreOrbsText
        ;
    
    public DialogButton 
            buyButton,
            playFreeButton,
            unlockButton,
            cancelButton,
            moreOrbsButton;
    
    public Article article;
    
    private LevelInfo currentLevel;
    
    private boolean isFreeExplanation = false;
    
    public UnlockLevelDialog() {
        this.appearSpeed = 2f;
        
        orb = new OrbWidget();
        
        buyText = new DynamicText(48);
        buyText.setAlignment(Align.CENTER);
        buyText.reset().
            printString("The demo ends here.").newLine(-0.8f).
            printString("But you can get the")
            .updateBuffers();
        
        unlockText = new DynamicText(40);
        unlockText.setAlignment(Align.CENTER);
        unlockText.reset().
            printString("OR")
            //.newLine().printString("with some orbs")
            .updateBuffers();
        
        explanationTitle = new DynamicText(48);
        explanationTitle.setAlignment(Align.CENTER);
        explanationTitle.reset().
            printString("Alright, but are you").newLine().
            printString("up for the challenge?")
            //.newLine().printString("with some orbs")
            .updateBuffers();
        
        explanationText = new DynamicText(128);
        explanationText.setAlignment(Align.CENTER);
        explanationText.reset().
            printString("We reward our top-skilled").newLine().
            printString("players: clear the previous").newLine().
            printString("chapter at 100% and use your").newLine().
            printString("orbs to continue for free!")
            //.newLine().printString("with some orbs")
            .updateBuffers();
        
        moreOrbsText = new DynamicText(64);
        moreOrbsText.setAlignment(Align.CENTER);
        moreOrbsText.reset().printString("A few orbs short?").updateBuffers();
        
        
        currentCredits = new DynamicText(40);
        
        buyButton = new DialogButton(16);
        buyButton.text.setAlignment(Align.CENTER);
        buyButton.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); buyButtonClicked(); } } );
        buyButton.setIdleColor(0f, 1f, 0f);
        buyButton.setFocusedColor(0.2f, 1f, 0.6f);
        buyButton.initColor();
        
        playFreeButton = new DialogButton(32);
        playFreeButton.text.setAlignment(Align.CENTER);
        playFreeButton.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); playForFreeClicked(); } } );
        playFreeButton.setIdleColor(0.8f, 0.6f, 0.9f);
        playFreeButton.setFocusedColor(1f, 0.6f, 1f);
        playFreeButton.initColor();
        
        unlockButton = new DialogButton(32);
        unlockButton.text.setAlignment(Align.CENTER);
        unlockButton.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); unlockButtonClicked(); } } );
        unlockButton.setIdleColor(0.8f, 0.6f, 0.9f);
        unlockButton.setFocusedColor(1f, 0.6f, 1f);
        unlockButton.setDisabledColor(0.7f, 0.7f, 0.7f);
        unlockButton.initColor();
        
        moreOrbsButton = new DialogButton(32);
        moreOrbsButton.text.setAlignment(Align.CENTER);
        moreOrbsButton.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); unlockButtonClicked(); } } );
        moreOrbsButton.setIdleColor(0.8f, 0.6f, 0.9f);
        moreOrbsButton.setFocusedColor(1f, 0.6f, 1f);
        moreOrbsButton.setDisabledColor(0.7f, 0.7f, 0.7f);
        moreOrbsButton.initColor();
        moreOrbsButton.text.reset().printString("Get More Orbs").updateBuffers();
        
        cancelButton = new DialogButton(8);
        cancelButton.text.setAlignment(Align.CENTER);
        cancelButton.text.reset().printString("Exit").updateBuffers();
        cancelButton.setOnClickListener( new TouchListener() { public void riseEvent(float x, float y) { AudioManager.playClickSound(); exitBackClicked(); } } );

        addChild(buyText);
        addChild(unlockText);
        addChild(explanationTitle);
        addChild(explanationText);
        addChild(moreOrbsText);

        addChild(buyButton);
        addChild(playFreeButton);
        addChild(unlockButton);
        unlockButton.addChild(orb);
        addChild(moreOrbsButton);
        addChild(currentCredits);
        addChild(cancelButton);
        
        buyButton.glowing = true;
        buyButton.text.reset().printString("Full Version").updateBuffers();
        
        //playFreeButton.text.reset().printString("Can't I just").newLine()
        //    .printString("play for FREE?").updateBuffers();
        
        playFreeButton.text.reset().printString("Just let me").newLine()
        .printString("play for free!").updateBuffers();
        
    }
    
    private float elapsed = 0;
    
    @Override
    public void update() {
        setSize(90, 130);
        elapsed += Timer.delta;
        super.update();
    }
    
    public void buyButtonClicked() {
        IAPManager.buyFullVersion();
    }
    
    public void unlockButtonClicked() {
        if (currentLevel.costToUnlock > EngineState.Player.totalOrbs) {
            IAPManager.buyOrbs();
        } else {
            EngineState.Player.totalOrbs -= currentLevel.costToUnlock;
            EngineState.GeneralStats.orbsSpent += currentLevel.costToUnlock;
            EngineState.onTotalOrbsUpdated();
            LevelContent.getLevelFromID(ArenaState.currentLevel).bought = true;
            ArenaState.instance.hideUnlockLevelDialogIfNecessary();
        }
    }
    
    public void playForFreeClicked() {
        setFreeExplanation(true);
    }
    
    public void exitBackClicked() {
        if (isFreeExplanation) setFreeExplanation(false);
        else  ArenaState.instance.goBackToTitle();
    }
    
    public void moreOrbsClicked() {
        IAPManager.buyOrbs();
    }
    
    public void setUpFromLevel(LevelInfo levelInfo) {
        
        currentLevel = levelInfo;
        
        title.reset().printString("Thanks For Playing!").updateBuffers();
        
        int cost = levelInfo.costToUnlock;
        //unlockButton.text.reset().printString("Unlock For ").printInteger(cost).updateBuffers();
        unlockButton.text.reset().printString("Unlock Level").newLine()
            .printString("For " + cost + "   ").updateBuffers(); //Creating new objects should ideally be avoided
        
        setFreeExplanation(false);
        updateCurrentOrbsLabel();
    }
    
    public void updateCurrentOrbsLabel() {
        if (currentLevel == null) return;
        int currentCreditsValue = EngineState.Player.totalOrbs;
        boolean tooExpensive = (currentCreditsValue < currentLevel.costToUnlock);
        
        //currentCredits.reset().printString("(You currently have ").printInteger(currentCreditsValue)
        //    .printString(" orb");
        //if (currentCreditsValue != 1) currentCredits.printString("s");
        //currentCredits.printString(")").updateBuffers();
        
        if (tooExpensive) {
            currentCredits.reset().printString("(You still need ").printInteger(currentLevel.costToUnlock - currentCreditsValue)
                .printString(" more orb");
            if (currentCreditsValue != 1) currentCredits.printString("s");
            currentCredits.printString(")").updateBuffers();
            currentCredits.setColor(1f, 0f, 0f);
        } else {
            currentCredits.reset().printString("(You have enough orbs!)").updateBuffers();
            currentCredits.setColor(0.1f, 1f, 0.1f);
        }
        
        
        unlockButton.setEnabled(!tooExpensive);
        unlockButton.initColor();
        unlockButton.text.setAlpha(tooExpensive? 0.4f : 0.9f);
        orb.setAlpha(tooExpensive? 0.4f : 1f);
        
        unlockButton.glowing = !tooExpensive;
        moreOrbsButton.glowing = tooExpensive;
    }

    public void setSize(float width, float height) {
        super.setSize(width, height);
        layoutChildren(width, height);
    }
    
    public void setFreeExplanation(boolean value) {
        isFreeExplanation = value;
        if (!isFreeExplanation) {
            cancelButton.text.reset().printString("Exit").updateBuffers();
        } else {
            cancelButton.text.reset().printString("Back").updateBuffers();
        }
    }
    
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        
        panel.setAlpha(1f);
        //panel.setColor(0.3f, 0.6f, 1f);
        //isFreeExplanation = false;
        
        float offsetY = height * 0.5f - 18f;
        
        float textSize = 7.8f;
        
        float offsetX = isFreeExplanation ? 9999 : 0;
        
        buyText.setAlpha(0.8f);
        buyText.textSize = textSize;
        buyText.setPosition(offsetX, offsetY);
        
        offsetY -= 35f;
        
        float buttonYOffset = height * -0.5f + 12f;
        float buttonWidth = 70f, buttonHeight = 22f;
        float buttonTextSize = 12f, buttonTextY = 6f;
        
        buyButton.text.textSize = buttonTextSize;
        buyButton.text.setPosY(buttonTextY);
        buyButton.setSize(buttonWidth, buttonHeight);
        buyButton.setPosition(offsetX, offsetY);
        
        //Or unlock
        
        offsetY -= 15f;
        unlockText.setAlpha(0.8f);
        unlockText.textSize = textSize;
        unlockText.setPosition(offsetX, offsetY);
        
        offsetY -= 25f;
        

        
        buttonYOffset = height * -0.5f + 12f;
        buttonWidth = 70f; buttonHeight = 22f;
        buttonTextSize = 9.9f; buttonTextY = 8.5f;
        
        playFreeButton.text.textSize = buttonTextSize;
        playFreeButton.text.setPosition(0, buttonTextY);
        playFreeButton.setSize(buttonWidth, buttonHeight);
        playFreeButton.setPosition(offsetX, offsetY);
        
        //Explanation
        
        offsetX = !isFreeExplanation ? 9999 : 0;
        
        offsetY = height * 0.5f - 18f;
        
        textSize = 8.5f;
        
        explanationTitle.setAlpha(0.99f);
        explanationTitle.textSize = textSize;
        explanationTitle.setPosition(offsetX, offsetY);
        explanationTitle.setColor( 1f, 193f / 255f, 74f / 255f);
        
        offsetY -= 16f;
        
        float modifier =  (float)Math.sin(3f * elapsed);
        textSize = 6.2f + 0.2f * modifier;
        explanationText.setAlpha(0.8f);
        explanationText.textSize = textSize;
        explanationText.setPosition(offsetX, offsetY);
        explanationText.setColor( (0.2f - 0.2f * modifier), 0.9f + 0.1f * modifier, 1f);
        
        offsetY -= 33f;
        
        buttonYOffset = height * -0.5f + 12f;
        buttonWidth = 70f; buttonHeight = 20f;
        buttonTextSize = 8.7f; buttonTextY = 6.9f;
        
        unlockButton.text.textSize = buttonTextSize;
        unlockButton.text.setPosition(0, buttonTextY);
        unlockButton.setSize(buttonWidth, buttonHeight);
        unlockButton.setPosition(offsetX, offsetY);

        
        offsetY -= 10f;
        
        currentCredits.setAlpha(0.9f);
        currentCredits.textSize = 5.7f;
        currentCredits.setPosY(offsetY);
        currentCredits.setPosX(offsetX + currentCredits.getWidth() * -0.5f);
        
        float orbSize = 6f;
        orb.setSize(orbSize, orbSize);
        orb.setPosition(13f, -3.5f);
        
        //A few orbs short?
        offsetY -= 10f;
        
        moreOrbsText.setAlpha(0.8f);
        moreOrbsText.textSize = 8f;
        moreOrbsText.setPosition(offsetX, offsetY);
        
        //Get more orbs button
        offsetY -= 16f;
        
        buttonYOffset = height * -0.5f + 12f;
        buttonWidth = 70f; buttonHeight = 15f;
        buttonTextSize = 8.7f; buttonTextY = 4.2f;
        
        moreOrbsButton.text.textSize = buttonTextSize;
        moreOrbsButton.text.setPosition(0, buttonTextY);
        moreOrbsButton.setSize(buttonWidth, buttonHeight);
        moreOrbsButton.setPosition(offsetX, offsetY);
        
        buttonYOffset = height * -0.5f + 9f;
        buttonWidth = 30f; buttonHeight = 12f;
        buttonTextSize = 8f; buttonTextY = 4f;
        
        cancelButton.text.textSize = buttonTextSize;
        cancelButton.text.setPosY(buttonTextY);
        cancelButton.setSize(buttonWidth, buttonHeight);
        cancelButton.setPosition(0, buttonYOffset);
    }
}
