package com.breakingbyte.wrap;

import com.breakingbyte.game.engine.EngineState;

import android.content.Context;
import android.os.Vibrator;

public class Vibration {

    private static Vibrator vibrator = null;
    
    public static void vibrate(int milliseconds) {
        if (!EngineState.Settings.vibrateOn) return;
        if (vibrator == null) 
            vibrator = (Vibrator) BaseActivity.instance.getSystemService(Context.VIBRATOR_SERVICE);
        
        vibrator.vibrate(milliseconds);
    }
    
    public static void stop() {
        if (vibrator == null) return;
        vibrator.cancel();
    }
    
}
