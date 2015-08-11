package com.breakingbyte.game.ui.anim;

import com.breakingbyte.game.util.SmoothJoin;

public class ScaleAnimation extends Animation {

    private float scaleBegin,
                  scaleEnd,
                  delay,
                  speed;
    
    public ScaleAnimation(float scaleBegin, float scaleEnd, float speed) {
        this(scaleBegin, scaleEnd, speed, 0);
    }
    
    public ScaleAnimation(float scaleBegin, float scaleEnd, float speed, float delay) {
        this.scaleBegin = scaleBegin;
        this.scaleEnd = scaleEnd;
        this.speed = speed;
        this.delay = delay;
        join = new SmoothJoin();
        reset();
    }

    @Override
    public void update() {
        if (widget == null) return;
        join.update();
        widget.setScale(join.get());
    }

    @Override
    public ScaleAnimation reset() {
        join.init(scaleBegin);
        join.setTarget(scaleEnd, speed, delay);
        return this;
    }
    
}
