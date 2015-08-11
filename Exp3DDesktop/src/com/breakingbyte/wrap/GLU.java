package com.breakingbyte.wrap;

public class GLU {
	
	//static javax.media.opengl.glu.GLU glu = new javax.media.opengl.glu.GLU();
    
    public static void gluLookAt (float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ,
            float upX, float upY, float upZ) {
    	new javax.media.opengl.glu.GLU().gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public static void gluOrtho2D (float left, float right, float bottom, float top) {
    	new javax.media.opengl.glu.GLU().gluOrtho2D(left, right, bottom, top);
    }

    public static void gluPerspective (float fovy, float aspect, float zNear, float zFar) {
    	new javax.media.opengl.glu.GLU().gluPerspective(fovy, aspect, zNear, zFar);
    }

    /*
    public static boolean gluProject (float objX, float objY, float objZ, float[] model, int modelOffset, float[] project,
            int projectOffset, int[] view, int viewOffset, float[] win, int winOffset) {
            int result = android.opengl.GLU.gluProject(objX, objY, objZ, model, modelOffset, project, projectOffset, view, viewOffset, win, winOffset);
            return result == GL.GL_TRUE;
    }

    public static boolean gluUnProject (float winX, float winY, float winZ, float[] model, int modelOffset, float[] project,
            int projectOffset, int[] view, int viewOffset, float[] obj, int objOffset) {
            int result = android.opengl.GLU.gluUnProject(winX, winY, winZ, model, modelOffset, project, projectOffset, view, viewOffset, obj, objOffset);
            return result == GL.GL_TRUE;
    }
    */
    


}
