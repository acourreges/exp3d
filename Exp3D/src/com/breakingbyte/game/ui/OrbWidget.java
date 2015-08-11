package com.breakingbyte.game.ui;


import com.breakingbyte.game.entity.bonus.Orb;

public class OrbWidget extends Widget {
    
    public Orb orb;

    public OrbWidget() {
        orb = new Orb();
    }
    
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        orb.setDimension(width, height);
    }
    
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        orb.posX = x;
        orb.posY = y;
    }
    
    @Override 
    public void setScale(float scale) {
        super.setScale(scale);
        orb.scale = scale;
    }
    
    @Override
    public void update() {
        super.update();
        orb.update();
    }
    
    @Override
    public void render() {
        orb.globalAlpha = getGlobalAlpha() * alpha;
        orb.render();
    }
}
