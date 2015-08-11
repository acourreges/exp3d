package com.breakingbyte.wrap;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;
import com.jogamp.opengl.util.FPSAnimator;

public class DesktopApplication implements GLEventListener {
    
    public static final String ANDROID_ROOT = "../Exp3D/";
    public static final String GWT_ROOT = "../Exp3DGWT/";
	
    
    static final int WIDTH = 480 + 16;//480;
    static final int HEIGHT = 800 + 38;//800

    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("Exp 3D");
        frame.setSize(WIDTH, HEIGHT);
        frame.add(canvas);
        frame.setLocation(0, 50);
        //frame.setLocationRelativeTo(null);

        frame.setAlwaysOnTop(true);
        //frame.setUndecorated(true);
        try {
            Image icon = ImageIO.read(new File(ANDROID_ROOT + "/res/drawable-hdpi/ic_launcher.png"));
            frame.setIconImage(icon);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        canvas.addGLEventListener(new DesktopApplication());
        
        Input input = new Input();
        canvas.addKeyListener(input);
        canvas.addMouseListener(input);
        canvas.addMouseMotionListener(input);
        
        FPSAnimator animator = new FPSAnimator(canvas, 120);
        animator.add(canvas);
        animator.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        render(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    	
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.gl = drawable.getGL().getGL2ES1();
            
            /*
             * By default, OpenGL enables features that improve quality
             * but reduce performance. One might want to tweak that
             * especially on software renderer.
             */
            GL.glDisable(GL.GL_DITHER);
    
            /*
             * Some one-time OpenGL initialization can be made here
             * probably based on features of this particular context
             */
             GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT,
                     GL.GL_FASTEST);
    
             float backCol = 0.3f;
             GL.glClearColor(backCol,backCol,backCol,1f);
             //gl.glEnable(GL10.GL_CULL_FACE);
             
             GL.glEnable(GL.GL_DEPTH_TEST);
        } else {
            GL2.gl = drawable.getGL().getGL2ES2();
            GL2.glDisable(GL2.GL_DITHER);
            float backCol = 0.3f;
            GL2.glClearColor(backCol,backCol,backCol,1f);
            GL2.glEnable(GL2.GL_DEPTH_TEST);
        }
        
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.gl = drawable.getGL().getGL2ES1();
            Screen.surfaceChanged(w, h);
        } else {
            GL2.gl = drawable.getGL().getGL2ES2();
            Screen.surfaceChanged(w, h);
        }
        
    }

    static float angle = 0f;
    private void render(GLAutoDrawable drawable) {
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
        	//System.out.println("Drawing");
            GL2ES1 gl = drawable.getGL().getGL2ES1();
            GL.gl = gl;
            Engine.drawFrame();
        } else {
            GL2ES2 gl = drawable.getGL().getGL2ES2();
            GL2.gl = gl;
            
            Engine.drawFrame();
        }

    }
    
    
}
