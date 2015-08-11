package com.breakingbyte.game.ui.anim;

import com.breakingbyte.game.ui.Widget;
import com.breakingbyte.game.util.SmoothJoin;

public abstract class Animation {

    protected Widget widget;
    
    public SmoothJoin join;
    
    public void setWidget(Widget widget) {
        this.widget = widget;
    }
    
    public abstract void update();
    
    public abstract Animation reset();
    
    public void onRenderPass() {}
}
