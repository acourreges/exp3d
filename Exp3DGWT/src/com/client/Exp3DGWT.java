package com.client;


import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.breakingbyte.game.engine.Controller;
import com.breakingbyte.game.engine.Controller.TouchAction;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.level.Level;
import com.breakingbyte.game.level.Level.LevelID;
import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.resource.Resource.ResourceLevel;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.state.ShopState;
import com.breakingbyte.game.ui.LifeBar;
import com.breakingbyte.game.util.Model;
import com.breakingbyte.game.util.ModelManager;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.ImmediateBuffer;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.ShaderPipeline;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtgl.array.Float32Array;
import com.googlecode.gwtgl.array.Uint16Array;
import com.googlecode.gwtgl.binding.WebGLBuffer;
import com.googlecode.gwtgl.binding.WebGLProgram;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLShader;
import com.googlecode.gwtgl.binding.WebGLTexture;
import com.googlecode.gwtgl.binding.WebGLUniformLocation;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Exp3DGWT implements EntryPoint {
    
    public static WebGLRenderingContext glContext;


    public static int RESOLUTION_HEIGHT = //400;
                                                (int) (800  * 1f);
    public static int RESOLUTION_WIDTH = //400;
                                               (int) (480  * 1f);
    
    public static boolean mouseDown = false;
    
    public void onModuleLoad() {
        
       final Canvas webGLCanvas = Canvas.createIfSupported();
       
       calculateGameResolution();
       
       webGLCanvas.setCoordinateSpaceHeight(RESOLUTION_HEIGHT);
       webGLCanvas.setCoordinateSpaceWidth(RESOLUTION_WIDTH);
       glContext = (WebGLRenderingContext) webGLCanvas.getContext("experimental-webgl");
       
       adaptSplashOverlay();
       
       if(glContext == null) {
               RootPanel.get("webgl-nosupport-div").getElement().setAttribute("style", "display: block;");
               Window.alert("Sorry, your browser doesn't support WebGL!");
               return;
       }
       

       glContext.viewport(0, 0, RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
       
       RootPanel rootPanel =  RootPanel.get("gwtGL");
       rootPanel.add(webGLCanvas);
       
       
       Window.addResizeHandler(new ResizeHandler() {


           @Override
           public void onResize(ResizeEvent event) {
               calculateGameResolution();
               
               webGLCanvas.setCoordinateSpaceHeight(RESOLUTION_HEIGHT);
               webGLCanvas.setCoordinateSpaceWidth(RESOLUTION_WIDTH);
               Screen.surfaceChanged(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
               
               adaptSplashOverlay();
           }
         });
       
       //webGLCanvas.getElement().setAttribute("style", "border: none; padding: 0; margin: 0;");
       
       // Add all the controllers: mouse for desktop, touch for mobile
       
       webGLCanvas.addDomHandler(new DoubleClickHandler() {

           @Override
           public void onDoubleClick(DoubleClickEvent pEvent) {
               Controller.onSpecialTouch();
           }

       }, DoubleClickEvent.getType());
       
       webGLCanvas.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                Controller.onTouch(TouchAction.DOWN, event.getX(), event.getY());
                mouseDown = true;
                event.preventDefault();
                event.stopPropagation();
            }}
       );
       
       webGLCanvas.addMouseUpHandler(new MouseUpHandler() {
        
        @Override
        public void onMouseUp(MouseUpEvent event) {
            handleInAppPurchaseAttempt(); //Dirty hack, but it's the only way to get around pop-up blocking
            Controller.onTouch(TouchAction.UP, event.getX(), event.getY());
            mouseDown = false;
            event.preventDefault();
            event.stopPropagation();
        }
       });
       
       webGLCanvas.addMouseMoveHandler(new MouseMoveHandler() {
        
        @Override
        public void onMouseMove(MouseMoveEvent event) {
            if (mouseDown) Controller.onTouch(TouchAction.MOVE, event.getX(), event.getY());
            event.preventDefault();
            event.stopPropagation();
        }
       });
       
       // ----- Touch events for mobile
       
       final Element el = webGLCanvas.getElement();
       
       webGLCanvas.addTouchStartHandler(new TouchStartHandler() {
        
           @Override
           public void onTouchStart(TouchStartEvent event) {
               Controller.onTouch(TouchAction.DOWN, event.getTouches().get(0).getRelativeX(el),  event.getTouches().get(0).getRelativeY(el));
               mouseDown = true;
               event.preventDefault();
               event.stopPropagation();
           }
       });
       
       webGLCanvas.addTouchEndHandler(new TouchEndHandler() {
           
           @Override
           public void onTouchEnd(TouchEndEvent event) {
               handleInAppPurchaseAttempt(); //Dirty hack, but it's the only way to get around pop-up blocking
               Controller.onTouch(TouchAction.UP, event.getChangedTouches().get(0).getRelativeX(el),  event.getChangedTouches().get(0).getRelativeY(el));
               mouseDown = false;
               event.preventDefault();
               event.stopPropagation();
           }

          });
       
       webGLCanvas.addTouchCancelHandler(new TouchCancelHandler() {
           
           @Override
           public void onTouchCancel(TouchCancelEvent event) {
               handleInAppPurchaseAttempt(); //Dirty hack, but it's the only way to get around pop-up blocking
               Controller.onTouch(TouchAction.UP, event.getChangedTouches().get(0).getRelativeX(el),  event.getChangedTouches().get(0).getRelativeY(el));
               mouseDown = false;
               event.preventDefault();
               event.stopPropagation();
           }

          });
       
       webGLCanvas.addTouchMoveHandler(new TouchMoveHandler() {
           
           @Override
           public void onTouchMove(TouchMoveEvent event) {
               if (mouseDown) Controller.onTouch(TouchAction.MOVE, event.getTouches().get(0).getRelativeX(el),  event.getTouches().get(0).getRelativeY(el));
               event.preventDefault();
               event.stopPropagation();
           }

          });
       
       start();
    }
    
    
    private void handleInAppPurchaseAttempt() {
        if (Engine.state == ShopState.instance) {
            if (ShopState.unlockFullVersionButton.isInFocus || ShopState.getMoreOrbsButton.isInFocus) openMarketPage();
        }
        else if (Engine.state == ArenaState.instance) {
            if (ArenaState.instance.unlockLevelDialog.buyButton.isInFocus) openMarketPage();
        }
    }
    
    private void openMarketPage() {
        String link = "https://play.google.com/store/apps/details?id=com.breakingbyte.exp3d";
        Window.open(link, "_blank", "");
    }
    
    private void start() {
       customInit();
       
       glContext.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
       glContext.clear(WebGLRenderingContext.COLOR_BUFFER_BIT);
       glContext.clearDepth(1.0f);
       //glContext.enable(WebGLRenderingContext.DEPTH_TEST);
       //glContext.depthFunc(WebGLRenderingContext.LEQUAL);
       
       
       // Old code with recursive timer every 16ms, replaced by requestAnimationFrame().
//       Timer timer = new Timer() {
//           @Override
//           public void run() {
//               drawScene();
//           }
//       };
//       timer.scheduleRepeating(16);

       AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
        
        @Override
        public void execute(double timestamp) {
            drawScene();
            AnimationScheduler.get().requestAnimationFrame(this);
        }
       });
       
    }

    
    public void customInit() {
        ShaderManager.init();
        ShaderManager.spTexture.bind();
        
        TextureManager.loadTextures(ResourceLevel.ALL);
        ///ModelManager.loadAllModels();
        ModelManager.tunnel.load();
        ModelManager.tunnel_lm.load();
        ModelManager.ship.load();
        //TODO check resource are handled correctly
        for (LevelID lvl : LevelID.values()) {
            Level l = Level.getInstance(lvl);
            if (l != null) l.initSetup();
        }
         
        Screen.surfaceChanged(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
    }
    
    
    private void drawScene() {
        Engine.drawFrame();
    }
    
    
    /**
     * Checks the WebGL Errors and throws an exception if there is an error.
     */
    private void checkErrors() {
            int error = glContext.getError();
            if (error != WebGLRenderingContext.NO_ERROR) {
                    String message = "WebGL Error: " + error;
                    GWT.log(message, null);
                    throw new RuntimeException(message);
            }
    }

    private boolean isPortrait() {
        int height = Window.getClientHeight();
        int width = Window.getClientWidth();
        return height > width;
    }
    
    private void adaptSplashOverlay() {
        
        Element desc = DOM.getElementById("description-div");
        Element descPortrait = DOM.getElementById("description-portrait");
        if (desc == null || descPortrait == null) return;
        
        if (isPortrait()) {
            desc.setAttribute("style", "display: none;");
            descPortrait.setAttribute("style", "display: block; margin-bottom: -10px; margin-top: 2px;");
        } else {
            desc.setAttribute("style", "display: block;");
            descPortrait.setAttribute("style", "display: none;");
        }
    }
    
    public void calculateGameResolution() {
        int clientHeight = Window.getClientHeight();
        int clientWidth = Window.getClientWidth();
        
        float targetRatio = 480f / 800f;
        
        RESOLUTION_HEIGHT = clientHeight - 2 * 50 - (isPortrait()? 50 : 0);
        RESOLUTION_WIDTH = (int) (RESOLUTION_HEIGHT * targetRatio);
        
        int MAX_WIDTH = (int)(clientWidth - 30);
        if (RESOLUTION_WIDTH > MAX_WIDTH) {
            RESOLUTION_WIDTH = MAX_WIDTH;
            RESOLUTION_HEIGHT = (int) (RESOLUTION_WIDTH / targetRatio);
        }
    }

}
