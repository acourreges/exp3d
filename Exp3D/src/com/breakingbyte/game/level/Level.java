package com.breakingbyte.game.level;

import java.util.ArrayList;

import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.script.ScriptInterpreter;
import com.breakingbyte.game.script.ScriptInterpreter.Script;
import com.breakingbyte.game.util.Model;
import com.breakingbyte.game.util.Shaker;
import com.breakingbyte.wrap.Log;

public class Level {
    
    private static String TAG = "Level";
    
    public static enum LevelID {
        Level1(1),
        Level2(2),
        Level3(3),
        Level4(4)
        ;
        
        public final int value;
        private LevelID(int value) { this.value = value; }
        public static LevelID fromID(int idInteger) {
            for (int i = 0; i < LevelID.values().length; i++) {
                LevelID lvl = LevelID.values()[i];
                if (lvl.value == idInteger) return lvl;
            }
            return null;
        }
        public LevelID getNextID() {
            return fromID(this.value + 1);
        }
    }
    
    public static Level getInstance(LevelID levelId) {
        switch (levelId) {
            case Level1: return Level1.instance;
            case Level2: return Level2.instance;
            case Level3: return Level3.instance;
            case Level4: return Level4.instance;
        }
        return null;
    }
    
    protected Shaker shaker;
    
    protected ArrayList<Model>   modelsUsed;
    protected ArrayList<Texture> texturesUsed;
    
    //Script
    public ScriptInterpreter scriptInterpreter = new ScriptInterpreter();
    
    public Level() {
        shaker = new Shaker();
        texturesUsed = new ArrayList<Texture>();
        modelsUsed = new ArrayList<Model>();
    }
    
    public void initSetup() {
        scriptInterpreter.resetState();
        scriptInterpreter.setScript(getScript());
        stopShake();
        
        loadAssets();
        setUpLighting();
    };
    
    public void setUpLighting() {}
    
    public void loadAssets() {
        for (int i = 0; i < modelsUsed.size(); i++) modelsUsed.get(i).load();
        for (int i = 0; i < texturesUsed.size(); i++) texturesUsed.get(i).load();
        
        System.gc();
    }
    
    public void render() {
        Log.d(TAG, "render() not overridden for " + this.getClass().getName());
    };
    
    public void update() {
        scriptInterpreter.updateRunScript();
        shaker.update();
    };
    
    protected Script getScript() {
        Log.e(TAG, "getScript() not overridden for " + this.getClass().getName());
        return null;
    }
    
    public float getLateralRotate() {
        return - (Engine.player.posX - (Screen.ARENA_WIDTH / 2f)) * 0.2f;
    }
    
    public void cleanUp() {
        for (int i = 0; i < modelsUsed.size(); i++) modelsUsed.get(i).unload();
        for (int i = 0; i < texturesUsed.size(); i++) texturesUsed.get(i).unload();
    };
    
    public void markAssetsAsTrashed() {
        for (int i = 0; i < modelsUsed.size(); i++) modelsUsed.get(i).isLoaded = false;
        for (int i = 0; i < texturesUsed.size(); i++) texturesUsed.get(i).isLoaded = false;
    }
    
    public boolean areAllAssetsLoaded() {
        for (int i = 0; i < modelsUsed.size(); i++) {
            if (!modelsUsed.get(i).isLoaded) return false;
        }
        for (int i = 0; i < texturesUsed.size(); i++) {
            if (!texturesUsed.get(i).isLoaded) return false;
        }
        return true;
    }

    public void shake() {
        shaker.shake();
    }
    
    public void stopShake() {
        shaker.reset();
    }
    
    public SoundId getMusic() {
        return SoundId.BGM_F_L;
    }
    
}
