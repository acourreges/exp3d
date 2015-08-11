package com.breakingbyte.game.resource;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.wrap.Platform;

public class Resource {

    public static boolean gfxContextTrashed = false;
    
    public static final class ResourceLevel {
        public static final int BOOTLOADER  = 0;
        public static final int ENGINE      = 1;
        public static final int ENGINE_POST = 2;
        public static final int PER_LEVEL   = 3;
        
        public static final int ALL   = 99;
    }
    
    public static void markAllGFXResourceAsTrashed() {
        gfxContextTrashed = true;
        
        ModelManager.trashAll();
        TextureManager.trashAll();
        
        if (Engine.state == ArenaState.instance) {
            if (Engine.currentLevel != null) Engine.currentLevel.markAssetsAsTrashed();
        }
    }
    
    public static boolean areAllNecessaryGFXResourcesLoaded() {
        if (!ModelManager.isAllLoaded(Platform.engineStartLevel)) return false;
        if (!TextureManager.isAllLoaded(Platform.engineStartLevel)) return false;
        if (Engine.state == ArenaState.instance) {
            if (Engine.currentLevel != null && !Engine.currentLevel.areAllAssetsLoaded()) return false;
        }
        return true;
    }

}
