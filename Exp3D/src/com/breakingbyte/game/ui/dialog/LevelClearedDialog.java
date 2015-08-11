package com.breakingbyte.game.ui.dialog;

import com.breakingbyte.game.audio.AudioManager;
import com.breakingbyte.game.level.LevelStats;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.ui.DynamicTextNumber;
import com.breakingbyte.game.ui.ImageWidget;
import com.breakingbyte.game.ui.OrbWidget;
import com.breakingbyte.game.ui.Widget;
import com.breakingbyte.game.ui.anim.AlphaAnimation;
import com.breakingbyte.game.ui.anim.ScaleAnimation;
import com.breakingbyte.game.ui.anim.TextSpacerAnimation;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.game.util.SmoothJoin.Interpolator;

public class LevelClearedDialog extends Dialog {

    private SmoothJoin unroller;
    
    private DialogButton nextButton;
    
    public float defaultTextLabelSize = 9f;
    
    public int timeMinutes = 14,
               timeSeconds = 32,
               kills = 14, 
               orbs = 88,
               orbsTotal = 88,
               orbsFinalTotal = 98,
               currentRecord = 80,
               newOrbsEarned = 23;
    public boolean perfectBonus = false;
    public boolean noSpecialBonus = false;
    
    public boolean isNewRecord = false;
    
    private DynamicText timeLabel,
                       timeSeparatorLabel,
                       killsLabel,
                       orbsLabel,
                       orbsSeparatorLabel,
                       damageLabel,
                       damageBonusLabel,
                       damageFailedLabel,
                       noSpecialWeaponLabel,
                       noSpecialWeaponBonusLabel,
                       noSpecialWeaponFailedLabel,
                       orbsFinalLabel,
                       orbsCurrentRecordLabel,
                       orbsNewRecordLabel,
                       newOrbsLabel,
                       newOrbsCountLabel;
    
    private DynamicTextNumber timeMinutesValue,
                       timeSecondsValue,
                       killsValue,
                       orbsValue,
                       orbsTotalValue,
                       orbsFinalValue,
                       orbsCurrentRecordValue;
    
    private OrbWidget orbAnimation;
    
    public ImageWidget separator;
    
    public void loadValues() {
        loadValuesFrom(ArenaState.instance.stats);
    }
    
    public void loadValuesFrom(LevelStats stats) {
        timeMinutes = (int) (stats.timeElapsed / 60f);
        timeSeconds = (int) (stats.timeElapsed - 60 * timeMinutes);
        kills = stats.kills;
        orbs = stats.orbsCollected;
        orbsTotal = stats.orbsSpawned;
        perfectBonus = stats.gotNoDamageBonus();
        noSpecialBonus = stats.gotNoSpecialFiredBonus();
        isNewRecord = stats.isNewRecord();
        orbsFinalTotal = stats.getOrbsCollectedWithBonus();
        currentRecord = stats.currentRecord;
        newOrbsEarned = stats.getNewOrbsEarned();
    }
    
    public LevelClearedDialog() {
        
        setColor(0.3f, 0.4f, 1f);
        
        unroller = new SmoothJoin();
        
        title.printString("Level Cleared");
        title.updateBuffers();
        
        String buttonTitle = "Next";
        nextButton = new DialogButton(buttonTitle.length());
        nextButton.setIdleColor(0.5f, 0.6f, 0.9f);
        nextButton.setFocusedColor(0.5f, 0.8f, 1f);
        nextButton.setSize(45, 14);
        nextButton.glowing = true;
        nextButton.text.setAlignment(Align.CENTER);
        nextButton.text.printString(buttonTitle);
        nextButton.text.updateBuffers();
        
        addChild(nextButton);
        
        nextButton.setOnClickListener(new TouchListener() {
            @Override
            public void riseEvent(float x, float y) {
                AudioManager.playClickSound();
                hide();
            }
        });
        
        int group = 0;
        
        //Time
        timeLabel = createLabelText("Time", null, group);
        timeSeparatorLabel = createLabelText(":", null, group); 
        //Special case of separator
        setUpValueText(timeSeparatorLabel); //used in the value part
        timeSeparatorLabel.setAlignment(Align.RIGHT);
        timeSeparatorLabel.reset().printString(":").updateBuffers();
        
        timeMinutesValue = createValueText(2, group);
        timeSecondsValue = createValueText(2, group); timeSecondsValue.minimumNumberOfDigits = 2;
        
        //Kills
        group++;
        killsLabel = createLabelText("Kills", null, group);
        killsValue = createValueText(8, group);
        
        //Orbs
        group++;
        orbsLabel = createLabelText("Orbs", null, group);
        orbsSeparatorLabel = createLabelText("/", null, group); 
        //Special case of separator
        setUpValueText(orbsSeparatorLabel); //used in the value part
        orbsSeparatorLabel.setAlignment(Align.RIGHT);
        orbsSeparatorLabel.reset().printString("/").updateBuffers();
        
        orbsValue = createValueText(3, group);
        orbsTotalValue = createValueText(3, group);
                
        //Damage
        group++;
        damageLabel = createLabelText("No Damage Bonus", null, group);
        damageFailedLabel = createLabelText("failed", null,  group);
        setUpValueText(damageFailedLabel);
        damageFailedLabel.setAlignment(Align.RIGHT);
        damageFailedLabel.reset().printString("failed").updateBuffers();
        damageFailedLabel.setColor(0.8f, 0f, 0f);
        
        damageBonusLabel = createLabelText("+"+LevelStats.BONUS_ORB_COUNT+" orbs", null, group);
        setUpValueText(damageBonusLabel);
        damageBonusLabel.setAlignment(Align.RIGHT);
        damageBonusLabel.reset().printString("+"+LevelStats.BONUS_ORB_COUNT+" orbs").updateBuffers();
        damageBonusLabel.setColor(224, 200, 0);
        
        //No special weapon
        group++;
        noSpecialWeaponLabel = createLabelText("Special Weapon", "Unused Bonus", group);
        noSpecialWeaponFailedLabel = createLabelText("failed", null, group);
        setUpValueText(noSpecialWeaponFailedLabel);
        noSpecialWeaponFailedLabel.setAlignment(Align.RIGHT);
        noSpecialWeaponFailedLabel.reset().printString("failed").updateBuffers();
        noSpecialWeaponFailedLabel.setColor(0.8f, 0f, 0f);
        
        noSpecialWeaponBonusLabel = createLabelText("+"+LevelStats.BONUS_ORB_COUNT+" orbs", null, group);
        setUpValueText(noSpecialWeaponBonusLabel);
        noSpecialWeaponBonusLabel.setAlignment(Align.RIGHT);
        noSpecialWeaponBonusLabel.reset().printString("+"+LevelStats.BONUS_ORB_COUNT+" orbs").updateBuffers();
        noSpecialWeaponBonusLabel.setColor(224, 200, 0);
        
        //Separator
        group++; group++;
        separator = new ImageWidget();
        separator.setTexture(TextureManager.blank);
        setUpAlphaAnimation(separator, group);
        addChild(separator);
        
        //Final total
        orbsFinalLabel = createLabelText("Total Orbs", null, group);
        orbsFinalValue = createValueText(8, group);
        
        //Current record
        group++;
        orbsCurrentRecordLabel = createLabelText("Current Record", null, group);
        orbsCurrentRecordValue = createValueText(8, group);
        
        orbsNewRecordLabel = createLabelText("New Record! Old one was", null, group);
        orbsNewRecordLabel.setColor(10, 255, 255);
        
        //New orbs
        group++; group++;
        String text = "New Orbs";
        newOrbsLabel = new DynamicText(text.length());
        addChild(newOrbsLabel);
        setUpLabelText(newOrbsLabel);
        newOrbsLabel.setAlignment(Align.CENTER);
        //newOrbsLabel.printString(text);
        //newOrbsLabel.updateBuffers();
        newOrbsLabel.textSize = 12f;
        newOrbsLabel.setColor(0.6f, 0.7f, 0.8f);
        setUpAlphaAnimation(newOrbsLabel, group);
        TextSpacerAnimation anim = new TextSpacerAnimation(newOrbsLabel, text);
        anim.setUp(0.4f, -0.1f, 1.8f , initialDelay + (groupDelay * group));
        newOrbsLabel.addAnimation(anim);
        
        group++; group++;  group++;
        orbAnimation = new OrbWidget();
        orbAnimation.setSize(35, 35);
        setUpAlphaAnimation(orbAnimation, group);
        setUpZoomAnimation(orbAnimation, group);
        addChild(orbAnimation);
        
        newOrbsCountLabel = new DynamicText(5);
        addChild(newOrbsCountLabel);
        newOrbsCountLabel.textSize = 15f;
        setUpAlphaAnimation(newOrbsCountLabel, group + 0.9f);
        //setUpZoomAnimation(newOrbsCountLabel, group);
        
        newOrbsCountLabel.setColor(0.9f, 0.0f, 0.9f);
        newOrbsCountLabel.setAlpha(0.9f);
        
        //Last part: set-up size
        setSize(90, 135);
    }
    
    @Override
    public void layoutChildren(float width, float height) {
        super.layoutChildren(width, height);
        nextButton.setPosition(0, -height * 0.5f + 10f);
        
        float leftOffset = -width * 0.5f + 8f;
        float rightOffset = width * 0.5f - 8;
        float currentY = height * 0.5f - 15f;
        float yStep = 5.5f;
        
        //Time
        timeLabel.setPosition(leftOffset, currentY);
        timeSecondsValue.setPosition(rightOffset, currentY);
        timeSeparatorLabel.setPosition(rightOffset - timeSecondsValue.getWidth() - 1f , currentY);
        timeMinutesValue.setPosition(rightOffset - timeSecondsValue.getWidth() - 4f, currentY);
        
        //Kills
        currentY -= yStep;
        killsLabel.setPosition(leftOffset, currentY);
        killsValue.setPosition(rightOffset, currentY);
        
        //Orbs
        currentY -= yStep;
        orbsLabel.setPosition(leftOffset, currentY);
        orbsTotalValue.setPosition(rightOffset, currentY);
        orbsSeparatorLabel.setPosition(rightOffset - orbsTotalValue.getWidth() - 1f , currentY);
        orbsValue.setPosition(rightOffset - orbsTotalValue.getWidth() - 5f, currentY);
        
        //Ship damage
        currentY -= yStep;
        damageLabel.setPosition(leftOffset, currentY);
        //currentY -= 1.5f;
        damageFailedLabel.setPosition(rightOffset, currentY);
        damageFailedLabel.setAlpha(perfectBonus? 0 : 0.9f);
        damageBonusLabel.setPosition(rightOffset, currentY);
        damageBonusLabel.setAlpha(perfectBonus? 0.9f : 0);
        
        //No special weapon used
        currentY -= yStep;
        noSpecialWeaponLabel.setPosition(leftOffset, currentY);
        currentY -= 1f;
        noSpecialWeaponFailedLabel.setPosition(rightOffset, currentY);
        noSpecialWeaponFailedLabel.setAlpha(noSpecialBonus? 0 : 0.9f);
        noSpecialWeaponBonusLabel.setPosition(rightOffset, currentY);
        noSpecialWeaponBonusLabel.setAlpha(noSpecialBonus? 0.9f : 0);
        
        //BG frame
        float bgOffset = 12f;
        currentY -= 8f;
        currentY -= bgOffset;
        separator.setAlpha(0.16f);
        separator.setSize(width * 0.84f, 16f);
        separator.setPosition(0, currentY);
        currentY += bgOffset;
        
        //Final total
        currentY -= 4f;
        bgOffset = 1f;
        orbsFinalLabel.setPosition(leftOffset + bgOffset, currentY);
        orbsFinalValue.setPosition(rightOffset - bgOffset, currentY + 0.3f);
        orbsFinalLabel.textSize = 10f; orbsFinalValue.textSize = 11f; 
        orbsFinalLabel.setColor(0.95f, 0.95f, 1f);
        
        //Current record
        currentY -= yStep + 3f;
        orbsCurrentRecordLabel.setPosition(leftOffset + bgOffset + 1f, currentY - 0.4f);
        orbsCurrentRecordLabel.setAlpha(!isNewRecord ? 0.9f : 0f);
        
        orbsNewRecordLabel.setPosition(leftOffset + bgOffset + 1f, currentY - 0.4f);
        orbsNewRecordLabel.setAlpha(isNewRecord ? 0.9f : 0f);
        
        orbsCurrentRecordValue.setPosition(rightOffset - bgOffset - 0.7f, currentY + 0.2f);
        
        if (isNewRecord) {
            orbsCurrentRecordValue.setColor(5, 255, 255);
        } else {
            orbsCurrentRecordValue.setColor(0.8f, 0.9f, 1f);
        }
        
        
        //New orbs
        currentY -= 13f;
        newOrbsLabel.setPosition(0, currentY);
        newOrbsLabel.textSize = 12f;
        newOrbsLabel.setColor(1f, 1f, 1f);
        
        currentY -= 27f;
        //Orb sprite
        orbAnimation.setPosition(0, currentY);
        
        //Orb value
        currentY += 10f;
        newOrbsCountLabel.textSize = 20;
        newOrbsCountLabel.setPosition( -newOrbsCountLabel.getWidth() * 0.5f , currentY);
        newOrbsCountLabel.setColor(142f/255f, 111f/255f, 1.0f); newOrbsCountLabel.setAlpha(1f);
    }
    
    public DynamicText createLabelText(String text, String textLine2, int groupNumber) {
        boolean has2ndLine = textLine2 != null;
        DynamicText result = new DynamicText(text.length() + (has2ndLine? textLine2.length() : 0));
        addChild(result);
        setUpLabelText(result);
        result.setAlignment(Align.LEFT);
        result.printString(text);
        if (has2ndLine) result.newLine(-0.6f).printString(textLine2);
        result.updateBuffers();
        setUpAlphaAnimation(result, groupNumber);
        setUpZoomAnimationForLabel(result, groupNumber);
        return result;
    }
    
    public void setUpLabelText(DynamicText text) {
        text.textSize = 6f;
        text.setAlpha(0.9f);
        text.setColor(0.6f, 0.7f, 0.8f);
        text.setColor(188, 197, 255);
    }
    
    public DynamicTextNumber createValueText(int maxChars, int groupNumber) {
        DynamicTextNumber result = new DynamicTextNumber(maxChars);
        addChild(result);
        setUpValueText(result);
        setUpAlphaAnimation(result, groupNumber);
        return result;
    }
    
    public void setUpValueText(DynamicText text) {
        text.textSize = 7f;
        text.setAlpha(0.9f);
        text.setColor(0.8f, 1f, 1f);
        //text.setColor(0.9f, 0.9f, 0.5f);
        
    }
    
    private float initialDelay = 1.f;
    private float groupDelay = 0.25f;
    public void setUpAlphaAnimation(Widget w, float groupNumber) {
        w.addAnimation(new AlphaAnimation(0f, 1f, 3f, initialDelay + (groupDelay * groupNumber)));
    }
    public void setUpZoomAnimation(Widget w, float groupNumber) {
        ScaleAnimation anim = new ScaleAnimation(0.01f, 1f, 1.7f, initialDelay + (groupDelay * groupNumber));
        anim.join.setInterpolator(Interpolator.ELASTIC).setElasticValues(1.1f, 0.6f);
        w.addAnimation(anim);
    }
    public void setUpZoomAnimationForLabel(Widget w, float groupNumber) {
        ScaleAnimation anim = new ScaleAnimation(0.1f, 1f, 2.5f, initialDelay + (groupDelay * groupNumber));
        anim.join.setInterpolator(Interpolator.ELASTIC).setElasticValues(1.5f, 0.9f);
        w.addAnimation(anim);
    }
    
    @Override
    public void show() {
        super.show();
        unroller.init(40f);
        unroller.setTarget(height, appearSpeed * 0.4f, 0.8f);
        initAnimations();
    }
    
    private void initAnimations() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).resetAnimations();
        }
        
        float counterSpeed = 1.5f; //actually duration
        
        int groupNumber = 0;
        
        //Time
        timeSecondsValue.value.init(0);
        timeSecondsValue.value.setTarget(timeSeconds, counterSpeed, initialDelay + (groupDelay * groupNumber));
        timeMinutesValue.value.init(0);
        timeMinutesValue.value.setTarget(timeMinutes, counterSpeed, initialDelay + (groupDelay * groupNumber));
        
        //Kills
        groupNumber++;
        killsValue.value.init(0);
        killsValue.value.setTarget(kills, counterSpeed, initialDelay + (groupDelay * groupNumber));

        //Orbs
        groupNumber++;
        orbsTotalValue.value.init(orbsTotal);
        orbsTotalValue.value.setTarget(orbsTotal, counterSpeed, initialDelay + (groupDelay * groupNumber));
        orbsValue.value.init(0);
        orbsValue.value.setTarget(orbs, counterSpeed, initialDelay + (groupDelay * groupNumber));
        
        //Damage
        groupNumber++;

        //No special weapon
        groupNumber++;
        
        //Final orb total
        groupNumber++;
        orbsFinalValue.value.init(0);
        orbsFinalValue.value.setTarget(orbsFinalTotal, counterSpeed, initialDelay + (groupDelay * groupNumber));
        
        //Current record
        groupNumber++;
        orbsCurrentRecordValue.value.init(0);
        orbsCurrentRecordValue.value.setTarget(currentRecord, counterSpeed, initialDelay + (groupDelay * groupNumber));
        
        
        //New orbs
        groupNumber++;
        newOrbsCountLabel.reset();
        if (newOrbsEarned == 0) {
            newOrbsCountLabel.printChar('0');
        } else {
            float spacer = -0.05f;
            newOrbsCountLabel.printChar('+', spacer);
            newOrbsCountLabel.printInteger(newOrbsEarned, spacer);
        }
        newOrbsCountLabel.updateBuffers();
    }
    
    @Override
    public void update() {

        super.update();
        if (unroller.update()) {
            layoutChildren(width, unroller.get());
        }
        
        //TODO remove
        layoutChildren(width, unroller.get());
    }
    

    
}
