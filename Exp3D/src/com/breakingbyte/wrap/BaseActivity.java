package com.breakingbyte.wrap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.breakingbyte.exp3d.R;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.EngineState;

public class BaseActivity extends Activity {
	
	private GameSurfaceView mGLSurfaceView;
	
	public static BaseActivity instance;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        BaseActivity.instance = this;
        super.onCreate(savedInstanceState);
        
        Persistence.init();
        
        Engine.wasOnPause = false;
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        setFullScreen();
        
        //getWindow().setFormat(PixelFormat.RGBA_8888);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        
        setContentView(R.layout.activity_android);
        //mGLSurfaceView = new GameSurfaceView(this);
        //setContentView(mGLSurfaceView);
        mGLSurfaceView = (GameSurfaceView) findViewById(R.id.game_view);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        
        //Stops the game rendering loop.
        //This is blocking, we are sure the current iteration will finish.
        mGLSurfaceView.onPause(); 
        
        Engine.wasOnPause = true;
        com.breakingbyte.game.audio.AudioManager.pauseMusic();
        Vibration.stop();
        EngineState.saveToStorage();
    }
    
    @Override
    protected void onDestroy() {
        
        super.onDestroy();
        
        //We depend 100% on the current activity, we have no reason to stay alive if the activity is destroyed.
        //Staying alive would mean we keep the same static context when the game is started again;
        //this is too dangerous to be allowed since some parts of the engine have a cache of the pointer 
        //to the current activity instance that would not be updated to the new instance. 
        System.exit(0);
    }
    
    @Override
    protected void onStop() {
        //Stop analytics
        super.onStop();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
    
    @SuppressLint("NewApi")
	private void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //Dim the soft-buttons of Android OS
        if (Build.VERSION.SDK_INT >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
    
}