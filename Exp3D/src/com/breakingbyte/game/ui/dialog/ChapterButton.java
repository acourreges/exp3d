package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.level.LevelInfo;
import com.breakingbyte.game.ui.DynamicText;

public class ChapterButton extends PanelButton {
    
    public LevelInfo levelInfo;
    
    public DynamicText chapterNumber;
    public DynamicText chapterName;
    public DynamicText previewText;
    
    public DynamicText progress;
    
    public ChapterButton(LevelInfo levelInfo) {
        
        this.levelInfo = levelInfo;
        
        borderSize = 10f;
        
        //setIdleColor(130f / 255f, 133f / 255f, 255.0f / 255f);
        setIdleColor(110f / 255f, 171f / 255f, 255.0f / 255f);        
        setFocusedColor(110f / 255f, 219f / 255f, 255.0f / 255f);
        setDisabledColor(0.9f, 0.9f, 0.9f);
        
        initColor();
        
        panel.setAlpha(0.8f);
        
        chapterNumber = new DynamicText("Chapter XX".length());
        
        chapterName = new DynamicText(20);
        
        progress = new DynamicText(8);
        
        previewText = new DynamicText("Preview".length());

        addChild(chapterNumber);
        addChild(chapterName);
        addChild(progress);
        //addChild(widget)
        
        setSize (90, 25f);
    }

    public void loadFromLevelInfo() {
        
        chapterNumber.textSize = 6.0f;
        chapterNumber.setAlpha(0.6f);
        
        chapterNumber.reset()
                     .printString("Chapter ")
                     .printChar((char)(levelInfo.levelId.value + '0'))
                     .updateBuffers();
        
        chapterName.textSize = 10f;

        progress.setColor(0f, 0.8f, 1f);
        
        boolean unlocked = levelInfo.unlocked;
        setEnabled(unlocked);
        if (unlocked) {
            chapterName.reset()
                       .printString(levelInfo.name)
                       .updateBuffers();
            
            chapterName.setAlpha(0.8f);

        } else {
            chapterName.reset()
                       .printString("Locked")
                       .updateBuffers();
            
            chapterName.setAlpha(0.3f);
        }
        initColor();
        
        if (levelInfo.unlocked && levelInfo.completedOnce) {
            progress.reset().printInteger(levelInfo.getProgressPercentage()).printChar('%').updateBuffers();
            progress.textSize = 12f;
            progress.setAlpha(0.4f);
            progress.setPosY(5.5f);
            
        } else if (levelInfo.unlocked && !levelInfo.bought && !EngineState.isFullVersion) {
            progress.reset().printString("Preview").updateBuffers();
            progress.textSize = 8f;
            progress.setAlpha(0.4f);
            progress.setPosY(3.5f);
        }
        else {
            progress.setAlpha(0f);
        }
        
        layoutChildren(getWidth(), getHeight());
    }
    

    
//    @Override
//    public void update() {
//        super.update();
//        setFocusColor(110f / 255f, 219f / 255f, 255.0f / 255f);
//    }
    
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        chapterNumber.setPosition(-width * 0.5f + 7f, height * 0.5f -4.6f);
        chapterName.setPosition(-width * 0.5f + 6f, 2.7f);
        
        progress.setPosX(width * 0.5f  - progress.getWidth() - 4f);
    }

}
