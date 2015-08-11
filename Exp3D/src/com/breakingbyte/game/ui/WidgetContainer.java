package com.breakingbyte.game.ui;

import java.util.ArrayList;

import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.Renderer;

public class WidgetContainer extends Widget {
    
    public static final String TAG = "WidgetContainer";

    protected ArrayList<Widget> children = new ArrayList<Widget>();
    
    public boolean addChild(Widget widget) {
        if (!widget.setParent(this)) {
            Log.e(TAG, "Could not add child!");
            return false;
        }
        children.add(widget);
        return true;
    }
    
    public boolean removeChild(Widget widget) {
        if (!children.contains(widget)) {
            Log.e(TAG, "Child could not be found!");
            return false;
        }
        
        if (widget.getParent() != parent) {
            Log.e(TAG, "Child's father does not match!");
            return false;
        }
        
        widget.setParent(null);
        children.remove(widget);
        
        return true;
    }
    
    public Widget getChild(int index) {
        return children.get(index);
    }
    
    public void removeAllChildren() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setParent(null);
        }
        children.clear();
    }
    
    @Override
    public void update() {
        super.update();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).update();
        }
    }
    
    @Override
    public void updateRenderBox(float parentBoxLeft, float parentBoxTop, float parentBoxRight, float parentBoxBottom) {
        super.updateRenderBox(parentBoxLeft, parentBoxTop, parentBoxRight, parentBoxBottom);
        for (int i = 0; i < children.size(); i++) {
            children.get(i).updateRenderBox(renderLeft, renderTop, renderRight, renderBottom);
        }
    }
    
    public void render() {
        if (!isVisibleOnScreen()) return;
        
        Renderer.pushMatrix();
        
        Renderer.translate(posX, posY, 0);
        
        Renderer.scale(scale, scale, scale);
        
        renderChildren();
        
        Renderer.popMatrix();
    }
    
    public final void renderChildren() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).render();
        }
    }
    
    @Override
    public void resetAnimations() {
        super.resetAnimations();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).resetAnimations();
        }
    }
    
    @Override
    public void handleTouch(float parentX, float parentY, Controller.TouchAction action) {
        
        if (!acceptInput) return;
        
        super.handleTouch(parentX, parentY, action);
        
        forwardTouchToChildren(parentX, parentY, action);
        
    }
    
    public void forwardTouchToChildren(float parentX, float parentY, Controller.TouchAction action) {
        //Convert to local coordinate
        float x = convertToLocalX(parentX);
        float y = convertToLocalY(parentY);
        
        for (int i = 0; i < children.size(); i++) {
            children.get(i).handleTouch(x, y, action);
        } 
    }

}
