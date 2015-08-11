package com.breakingbyte.wrap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.Player;

public class GLRenderer implements GLSurfaceView.Renderer {
	
	Player ship = null;
	
	public GLRenderer() {
	    
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		
	    GL.setGLContext(gl);
	    
	    Engine.drawFrame();
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	    GL.setGLContext(gl);
	    	    
        Screen.surfaceChanged(width, height);
	    
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	    
	    GL.setGLContext(gl);
	    
	    //This means we are starting from a brand new EGL context
	    if (Engine.wasOnPause) {
	        //We had a context and we lost it, mark all the OGL resources as trashed
	        com.breakingbyte.game.resource.Resource.markAllGFXResourceAsTrashed();
	    }
	    
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

         //GL.glDepthFunc(GL.GL_LEQUAL);
         
         float backCol = 0.0f;
         GL.glClearColor(backCol,backCol,backCol,1f);
         //gl.glEnable(GL10.GL_CULL_FACE);
         
         GL.glEnable(GL.GL_DEPTH_TEST);
		
	}
	
	

}
