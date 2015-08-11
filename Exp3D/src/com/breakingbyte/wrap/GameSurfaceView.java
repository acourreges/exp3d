package com.breakingbyte.wrap;


import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Controller.TouchAction;

public class GameSurfaceView extends GLSurfaceView {
	
	public static Context context;

	GestureDetector gestureDetector;
	
    public GameSurfaceView(Context context) {
        super(context);
        setup(context);
    }
	
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }
	
   @TargetApi(Build.VERSION_CODES.HONEYCOMB)
   private void setup(Context context) {
        //Handle the keys
        setFocusableInTouchMode(true);
        gestureDetector = new GestureDetector(context, new GestureListener());
        GameSurfaceView.context = context;
        setRenderer(new GLRenderer());
        
        //Try to avoid the EGL context being trashed when activity receives onPause()
        //Works only for Honeycomb (Android 3.0) and later
        if (Build.VERSION.SDK_INT >= 11) {
            this.setPreserveEGLContextOnPause(true);
        }
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {

        //Log.d("surfaceview Action:",event.getAction()+";"); 
	    
	    TouchAction act;

	    switch (event.getAction()) {
	        case (MotionEvent.ACTION_UP): act = TouchAction.UP; break;
	        case (MotionEvent.ACTION_DOWN): act = TouchAction.DOWN; break;
	        case (MotionEvent.ACTION_MOVE): act = TouchAction.MOVE; break;
	        case (MotionEvent.ACTION_CANCEL): act = TouchAction.CANCEL; break;
	        default: {
	            //Log.e("EXP3D", "Special Event! code " + event.getAction());
	            act = TouchAction.MOVE; break;
	        }
	    }
 
	    Controller.onTouch(act, event.getX(), event.getY());
	    
	    //return true;
	    
	    return gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int k, KeyEvent e){
	    
	    if(e.getAction() == KeyEvent.ACTION_DOWN)
	    {
	        switch(k)
	        {
	        case KeyEvent.KEYCODE_BACK:
	            Controller.onBack();
	            return true;
	        default: break;//System.exit(0);
	        }
	    }

		return false;
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

	    @Override
	    public boolean onDown(MotionEvent e) {
	        return true;
	    }

	    @Override
	    public boolean onDoubleTap(MotionEvent e) {
	        Controller.onSpecialTouch();
	        return true;
	    }
	}

	
	

}
