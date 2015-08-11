package com.breakingbyte.game.engine;


public class Controller {
    
    public enum TouchAction {
        DOWN,
        MOVE,
        UP,
        CANCEL,
    }
    
    public static boolean hasSpecialEvent = false;
    
    //Current Event for an 'update' iteration in the engine
    public static boolean hasEvent = false;
    public static TouchAction touchAction = TouchAction.UP;
    public static float touchX, touchY;
    public static long identifier;
    public static boolean downFlag = false, upFlag = false;
    
    //Last received event from the native controller
    public static TouchAction nativeTouchAction = TouchAction.UP;
    public static float nativeTouchX, nativeTouchY;
    public static long nativeIdentifier;
    
    public static TouchAction previousTouchAction;
    public static float previousTouchX, previousTouchY;
    
    public static boolean backFlag = false;
    
    private static long identifierCounter = 0;
    
    public static boolean nativeDown = false;
    public static void onTouch(TouchAction action, float windowX, float windowY) {
        
        // This should be using an event queue but some Android touch-screen drivers will completely 
        // flood the application with events. 
        // So we simply overwrite the last event received each time: definitely not thread-safe
        // but optimal performance for all the devices.
        // readKeys() has workarounds to handle the inconsistencies between events.
        
        windowX -= Screen.VIEWPORT_X;
        if (windowX < 0) windowX = 0;
        if (windowX > Screen.VIEWPORT_WIDTH) windowX = Screen.VIEWPORT_WIDTH;
        
        windowY -= Screen.VIEWPORT_Y - Screen.AD_HEIGHT;
        if (windowY < 0) windowY = 0;
        if (windowY > Screen.VIEWPORT_HEIGHT) windowY = Screen.VIEWPORT_HEIGHT;
        
        float x = (windowX * Screen.PIXEL_TO_ARENA_UNIT);
        float y = ((Screen.VIEWPORT_HEIGHT - windowY) * Screen.PIXEL_TO_ARENA_UNIT);
        
        //Hack to avoid skipping a Down event
        if (action == TouchAction.DOWN) 
            nativeDown = true;
        if (!(nativeDown && action == TouchAction.MOVE))
            nativeTouchAction = action;
        
        nativeTouchX = x;
        nativeTouchY = y;
        nativeIdentifier = identifierCounter++;
        
    }
    
    public static void readKeys() {
        
        // This is not thread-safe, but we can get away by avoiding synchronization if we tolerate 
        // slight incoherences of x/y or DOWN/MOVE actions.
        // UP actions, however, must absolutely be treated. This code will handle them correctly,
        // by postponing them to the next update cycle in the worst case scenario. 
        
        if (touchX == nativeTouchX
            && touchY == nativeTouchY
            && touchAction == nativeTouchAction
            && identifier == nativeIdentifier)
        {
            //No new event since the last update cycle
            hasEvent = false;
            return;
        }
        
        hasEvent = true;
        
        //Make a copy of the current event so it doesn't change during an update cycle
        touchX = nativeTouchX;
        touchY = nativeTouchY;
        touchAction = nativeTouchAction;
        identifier = nativeIdentifier;
        
        if (touchAction == TouchAction.DOWN) { downFlag = true; nativeDown = false; }
        else if (touchAction == TouchAction.UP || touchAction == TouchAction.CANCEL) upFlag = true;        
    }
    
    public static void onBack() {
        backFlag = true;
    }
    
    public static void onSpecialTouch() {
        hasSpecialEvent = true;
    }
    
    public static void resetFlags() {
        hasEvent = hasSpecialEvent = false;
        downFlag = upFlag = false; 
        backFlag = false;
        previousTouchAction = touchAction;
        previousTouchX = touchX;
        previousTouchY = touchY;
    }

}
