package com.breakingbyte.game.util;

import java.util.ArrayList;

import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.wrap.Platform;

public class IAPNotifier {
    
    //private static final String TAG = "IAPManager";
    
    public static enum Kind {
        FULL_VERSION,
        ORBS,
        FAKE
    }
    
    public static class IAPNotification {
        public Kind kind;

        public int orbQuantity = 0;
        
        public boolean purchaseCompleted = false;
        
        public boolean shouldNotify = true;
    }
    
    public static ArrayList<IAPNotification> notifQueue = new ArrayList<IAPNotifier.IAPNotification>();
    
    public static void notifyFakeWasBought(boolean bought) {
        IAPNotification result = new IAPNotification();
        result.kind = Kind.FAKE;
        result.purchaseCompleted = bought;
        //TODO this should be synchronized
        notifQueue.add(result);
    }
    
    public static void notifyFullVersionWasBought(boolean bought, boolean isStealth) {
        IAPNotification result = new IAPNotification();
        result.kind = Kind.FULL_VERSION;
        result.purchaseCompleted = bought;
        result.shouldNotify = !isStealth;
        //TODO this should be synchronized
        notifQueue.add(result);
    }
    
    public static void notifyOrbPackWasBought(boolean bought, int count) {
        IAPNotification result = new IAPNotification();
        result.kind = Kind.ORBS;
        result.purchaseCompleted = bought;
        result.orbQuantity = count;
        //TODO this should be synchronized
        notifQueue.add(result);
    }
    
    //Executed by the GL thread
    public static void update() {
        //TODO this should be synchronized
        while (notifQueue.size() > 0) {
            IAPNotification notif = notifQueue.get(0);
            notifQueue.remove(0);
            
            boolean success = notif.purchaseCompleted;
            
            switch (notif.kind) {
                case FAKE: 
                    //Log.d(TAG, "Fake completed with status " + success);
                    break;
                    
                case FULL_VERSION:
                    //Log.d(TAG, "Full version completed with status " + success);
                    EngineState.isFullVersion = success;
                    EngineState.onFullVersionUpdated();
                    break;
                    
                case ORBS:
                    //Log.d(TAG, "Orbs completed with status " + success + " & orbs are " + notif.orbQuantity);
                    if (success) {
                        EngineState.Player.totalOrbs += notif.orbQuantity;
                        EngineState.onTotalOrbsUpdated();
                    }
                    break;
                    
                default: 
                    break;
            }

            Platform.displayLoadingDialog(false);
            
            if (notif.shouldNotify) {
                Platform.toastNotify(success? "Thank you!" : "Purchase canceled");
            }
            
        }
    }

}
