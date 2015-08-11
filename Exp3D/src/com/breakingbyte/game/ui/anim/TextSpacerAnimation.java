package com.breakingbyte.game.ui.anim;

import com.breakingbyte.game.ui.DynamicText;
import com.breakingbyte.game.util.SmoothJoin;

public class TextSpacerAnimation extends Animation {

    private DynamicText dynamicText;
    
    private String textToPrint;
    
    private float   spaceBegin,
                    spaceEnd,
                    delay,
                    speed;
    
    public TextSpacerAnimation(DynamicText dynamicText, String textToPrint) {
        join = new SmoothJoin();
        this.widget = dynamicText;
        this.dynamicText = dynamicText;
        this.textToPrint = textToPrint;
    }
    
    public void setUp(float spaceBegin, float spaceEnd, float speed, float delay){
        this.spaceBegin = spaceBegin;
        this.spaceEnd = spaceEnd;
        this.delay = delay;
        this.speed = speed;
        reset();
    }
    
    @Override
    public void update() {
        join.update();
    }

    @Override
    public TextSpacerAnimation reset() {
        join.init(spaceBegin);
        join.setTarget(spaceEnd, speed, delay);
        return this;
    }
    
    @Override
    public void onRenderPass() {
        dynamicText.reset();
        dynamicText.printString(textToPrint, join.get());
        dynamicText.updateBuffers();
    }

}
