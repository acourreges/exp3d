package com.breakingbyte.wrap;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Controller.TouchAction;
import com.breakingbyte.game.resource.Resource;
import com.breakingbyte.wrap.shared.Timer;


public class Input implements KeyListener, MouseListener, MouseMotionListener {

    @Override
    public void keyPressed(KeyEvent arg0) {

        // Only for debug purpose, the game is supposed to be played only with the mouse
        Log.d("Input", "Key pressed: " + arg0.getKeyChar());
        
        if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) Controller.onBack();
        
        if (arg0.getKeyChar() == 't') Engine.testPhase();
        
        if (arg0.getKeyChar() == 'b') Engine.switchBoundingBoxDrawing();
        
        if (arg0.getKeyChar() == 'r') Resource.markAllGFXResourceAsTrashed();
        
        if (arg0.getKeyChar() == KeyEvent.VK_SPACE) {
            if (Timer.isPaused()) Timer.resume(); else Timer.pause();
        }
        
        if (arg0.getKeyChar() == 'l') Engine.switchLightmap();
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        Controller.onTouch( TouchAction.DOWN, arg0.getX(), arg0.getY());
        if (arg0.getButton() == MouseEvent.BUTTON3) {
            Controller.onSpecialTouch();
        }
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        Controller.onTouch(TouchAction.UP, arg0.getX(), arg0.getY());
        
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        Controller.onTouch( TouchAction.MOVE, arg0.getX(), arg0.getY());
        
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

   
}
