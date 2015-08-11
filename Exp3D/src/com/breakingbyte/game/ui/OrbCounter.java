package com.breakingbyte.game.ui;

import com.breakingbyte.game.ui.DynamicText.Align;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Renderer;

public class OrbCounter extends WidgetContainer {

    DynamicText number;
    
    int orbNumber;
    
    OrbWidget orbWidget;
    
    SmoothJoin zoomer;
    
    public OrbCounter() {
        number = new DynamicText(10);
        number.setAlignment(Align.LEFT);
        orbWidget = new OrbWidget();
        orbNumber = 0;
        zoomer = new SmoothJoin();
        reset();
    }
    
    public void reset() {
        updateNumberValue(0);
        zoomer.init(1f);
        zoomer.setTarget(1f, 0f);
        float orbSize = 7f;
        orbWidget.setSize(orbSize, orbSize);
        number.setAlpha(0.8f);
        number.textSize = 12f;
    }
    
    public void updateNumberValue(int value) {
        orbNumber = value;
        number
            .reset()
            //.printString("x")
            .printInteger(orbNumber)
            .updateBuffers();
        zoomer.init(2.4f);
        zoomer.setTarget(1f, 4f);
    }
    
    public void update() {
        orbWidget.update();
        zoomer.update();
    }
    
    public void render() {
        float scaler = zoomer.get();
        
        Renderer.pushMatrix();
        
        Renderer.scale(scaler, scaler, scaler);
        orbWidget.render();
        
        Renderer.translate(5f, 0f, 0f);        
        
        //Renderer.pushMatrix();
        
        Renderer.translate(0, number.textSize * 0.5f, 0);
        number.render();
        //Renderer.popMatrix();
        
        Renderer.popMatrix();
    }
    
}
