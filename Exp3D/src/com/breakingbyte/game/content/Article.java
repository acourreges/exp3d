package com.breakingbyte.game.content;

import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.dialog.ShopItem;

public abstract class Article {

    public int currentLevel = 0;
    
    public ShopItem itemPanel;
    
    public void buy() {
        EngineState.Player.totalOrbs -= getPrice();
        EngineState.GeneralStats.orbsSpent += getPrice();
        if (EngineState.Player.totalOrbs < 0) EngineState.Player.totalOrbs = 0;
        levelUp();
    }
    
    public void levelUp() {
        currentLevel++;
        applyToEngineState();
    }
    
    public void setLevel(int level) {
        currentLevel = level;
    }
    
    // Icon
    abstract public Texture getIcon();
    
    // Name
    abstract public String getName();
    
    // Value for the engine
    public int getValueForLevel(int level) {
        return level;
    }
    
    public int getValueForCurrentLevel() {
        return getValueForLevel(currentLevel);
    }
    
    // Label description
    abstract public void printLabelForLevel(int level);
    
    abstract public void printShortLabelForLevel(int level);
    
    public int getCurrentLevelLabel() { 
       resetText().printLabelForLevel(currentLevel);
       return charBufferLength;
    }
    public int getCurrentLevelShortLabel() { 
        resetText().printShortLabelForLevel(currentLevel);
        return charBufferLength;
     }
    public int getNextLevelLabel() {
        resetText().printLabelForLevel(currentLevel + 1);
        return charBufferLength;
    }
    
    // description, + string under the name
    
    abstract public int getPrice();
    
    abstract public int getMaxLevel(); 
    
    abstract public void printLongDescription(DynamicText txt);
    
    abstract public void applyToEngineState();
    
    public boolean isAtMaxLevel() { return currentLevel >= getMaxLevel(); }
    
    public boolean canBeBoughtByPlayer() {
        return EngineState.Player.totalOrbs >= getPrice();
    }
    
    //Some hacks to avoid instancing new String objects
    //Not thread-safe
    public char[] charBuffer = new char[128];
    public int charBufferLength = 0;
    
    public Article resetText() {
        charBufferLength = 0;
        return this;
    }
    
    public void printChar(char c) {
        charBuffer[charBufferLength] = c;
        charBufferLength++;
    }
    
    public Article printStr(String str) {
        for (int i = 0; i < str.length(); i++) printChar(str.charAt(i));
        return this;
    }
    
    private char[] digitTmp = new char[32];
    public Article printInt(int value) {
        int numberCharPrinted = 0;
        int minimumNumberOfDigits = 1;
        int i = 0;
        while (value > 0 || numberCharPrinted < minimumNumberOfDigits) {
            int digit = value % 10;
            digitTmp[i++] = (char)('0' + digit);
            numberCharPrinted++;
            value = value / 10;
        }
        for (int j = i-1; j >= 0; j--) printChar(digitTmp[j]);
        return this;
    }
    
    public void updatePanelContent() {
        if (itemPanel == null) return;
        itemPanel.loadFromArticle();
    }
    
}
