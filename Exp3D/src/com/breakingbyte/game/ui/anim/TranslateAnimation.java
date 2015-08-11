package com.breakingbyte.game.ui.anim;

import com.breakingbyte.game.util.SmoothJoin;

public class TranslateAnimation extends Animation {

    private float posBegin,
                  posEnd,
                  delay,
                  speed;
    
    public boolean doX, doY;
    
    public TranslateAnimation(float posBegin, float posEnd, float speed) {
        this(posBegin, posEnd, speed, 0);
    }
    
    public TranslateAnimation onX(boolean value) {
        doX = value;
        return this;
    }
    
    public TranslateAnimation onY(boolean value) {
        doY = value;
        return this;
    }
    
    public TranslateAnimation(float posBegin, float posEnd, float speed, float delay) {
        this.posBegin = posBegin;
        this.posEnd = posEnd;
        this.speed = speed;
        this.delay = delay;
        join = new SmoothJoin();
        doX = doY = false;
        reset();
    }

    @Override
    public void update() {
        if (widget == null) return;
        join.update();
        if (doX) widget.setPosX(join.get());
        if (doY) widget.setPosY(join.get());
    }

    @Override
    public TranslateAnimation reset() {
        join.init(posBegin);
        join.setTarget(posEnd, speed, delay);
        return this;
    }
    
}
