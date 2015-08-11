package com.breakingbyte.game.ui.anim;

import com.breakingbyte.game.util.SmoothJoin;

public class AlphaAnimation extends Animation {

    private float alphaBegin,
                  alphaEnd,
                  delay,
                  speed;
    
    public AlphaAnimation(float alphaBegin, float alphaEnd, float speed) {
        this(alphaBegin, alphaEnd, speed, 0);
    }
    
    public AlphaAnimation(float alphaBegin, float alphaEnd, float speed, float delay) {
        this.alphaBegin = alphaBegin;
        this.alphaEnd = alphaEnd;
        this.speed = speed;
        this.delay = delay;
        join = new SmoothJoin();
        reset();
    }

    @Override
    public void update() {
        if (widget == null) return;
        join.update();
        widget.setGlobalAlpha(join.get());
    }

    @Override
    public AlphaAnimation reset() {
        join.init(alphaBegin);
        join.setTarget(alphaEnd, speed, delay);
        return this;
    }
    
}
