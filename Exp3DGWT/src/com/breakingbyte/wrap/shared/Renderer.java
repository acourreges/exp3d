package com.breakingbyte.wrap.shared;

import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.Matrix4;
import com.breakingbyte.game.util.MatrixStack;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.wrap.Platform;
import com.client.Exp3DGWT;
import com.google.gwt.core.client.GWT;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

public class Renderer {
    
    public static final String TAG = "Renderer";
    
    public static enum GraphicsAPI {
        GLES1,
        GLES2
    }
    
    public static enum BlendingMode {
        NORMAL,     // GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA
        ADDITIVE,   // GL.GL_SRC_ALPHA, GL.GL_ONE   
        EXPLOSION,  // GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_COLOR   
    }
    
    public static final boolean usePremultipliedAlphaTextures = true;
    
    //Current texture binded
    private static Texture currentTexture = null;

    
    public static final void initialize() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            //switchToVBOMode();
            //GL.glEnable(GL.GL_RESCALE_NORMAL);
        }
    }
    
    public static final void enableTexture() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {

        } else {
            /*
        	Exp3DGWT.glContext.enable(WebGLRenderingContext.TEXTURE_2D);
            
        	Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
        	Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR);
        	Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_S, WebGLRenderingContext.REPEAT);
        	Exp3DGWT.glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_T, WebGLRenderingContext.REPEAT);
        	*/
        }
    }
    
    
    public static final void bindTexture(Texture texture) {
        if (texture == null) return;
        if (currentTexture == texture) return;
        currentTexture = texture;
        texture.nativeBind();
    	/*
        if (currentTexture == id) return;
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glBindTexture(GL.GL_TEXTURE_2D, id);
        } else {
            GL2.glBindTexture(GL2.GL_TEXTURE_2D, id);
        }
        */
    }
    
    //Buffer, arrays, VBOs...
    
    
    public static class Lighting {
        
        public static final void enable() {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) {
                /*
                if (lightingON) return;
                GL.glEnableClientState(GL.GL_NORMAL_ARRAY);
                GL.glEnable(GL.GL_LIGHTING);
                GL.glEnable(GL.GL_LIGHT0);   
                GL.glShadeModel(GL.GL_SMOOTH);
                lightingON = true;
                */
            } else {
                ShaderManager.spLighting.bind();
                
                float lightPos[] = Light.lightPosition;
                
                if (ShaderPipeline.currentPipelineBound.hasUniform(ShaderManager.Uniform.lightPosition))
                    Exp3DGWT.glContext.uniform4f(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.lightPosition), lightPos[0], lightPos[1], lightPos[2], lightPos[3]);
            }
        }
        
        public static final void disable() {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) {
                /*
                if (!lightingON) return;
                GL.glDisableClientState(GL.GL_NORMAL_ARRAY);
                GL.glDisable(GL.GL_LIGHTING);
                GL.glDisable(GL.GL_LIGHT0);  
                GL.glShadeModel(GL.GL_FLAT);
                lightingON = false;
                */
            } else {
                ShaderManager.spTexture.bind();
            }
        }
        
    }

    
    public static final void unbindVBOs() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            //Bind back to 0
            /*
            GL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            GL.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
            */
        } else {
            //All is done with VBO anyway
            /*
            GL2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
            GL2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
            */
        }
    }
    
    public static final void switchToVBOMode() {
        /*
        GL.glEnableClientState( GL.GL_VERTEX_ARRAY );
        GL.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);         
        */
    }
    
    /*
    public static final void switchToNonVBOMode() {

        //Bind back to 0
        GL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        GL.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
        
        //GL.glDisableClientState( GL.GL_VERTEX_ARRAY );
        //GL.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY); 
        //GL.glDisableClientState(GL.GL_NORMAL_ARRAY);     
                
    }
    */
    
    public static final class Blending {
        
        public static final void enable() { 
            Exp3DGWT.glContext.enable(WebGLRenderingContext.BLEND);
            
            //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glEnable(GL.GL_BLEND); 
            //else GL2.glEnable(GL2.GL_BLEND); 
        }
        
        public static final void disable() { 
            Exp3DGWT.glContext.disable(WebGLRenderingContext.BLEND);
            //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDisable(GL.GL_BLEND);
            //else GL2.glDisable(GL2.GL_BLEND);
            }
        
        public static final void resetMode() {
            setMode(BlendingMode.NORMAL);
        }
        
        public static final void setMode(BlendingMode mode) {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) {
                /*
                switch (mode) {
                    case NORMAL: 
                        GL.glBlendFunc(usePremultipliedAlphaTextures? GL.GL_ONE : GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                        break;
                    case ADDITIVE: 
                        GL.glBlendFunc(usePremultipliedAlphaTextures? GL.GL_ONE : GL.GL_SRC_ALPHA, GL.GL_ONE);
                        break; 
                    case EXPLOSION: 
                        GL.glBlendFunc(usePremultipliedAlphaTextures? GL.GL_ONE : GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_COLOR);
                        break; 
                }
                */
            } else {
                switch (mode) {
                    case NORMAL: 
                        Exp3DGWT.glContext.blendFunc(usePremultipliedAlphaTextures? WebGLRenderingContext.ONE : WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);
                        break;
                    case ADDITIVE: 
                        Exp3DGWT.glContext.blendFunc(usePremultipliedAlphaTextures? WebGLRenderingContext.ONE : WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE);
                        break; 
                    case EXPLOSION: 
                        Exp3DGWT.glContext.blendFunc(usePremultipliedAlphaTextures? WebGLRenderingContext.ONE : WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE_MINUS_SRC_COLOR);
                        break; 
                }
            }
        }
        
    }

    public static final void setClearColor(float red, float green, float blue, float alpha) {
        Exp3DGWT.glContext.clearColor(red, green, blue, alpha);
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glClearColor(red, green, blue, alpha); 
        //else GL2.glClearColor(red, green, blue, alpha); 
    }
    
    public static final void clearDepthBuffer() { clear(true, false); }
    
    public static final void clearColorBuffer() { clear(false, true); }
    
    public static final void clear(boolean depthBuffer, boolean colorBuffer) {
        int mask = 0;
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            /*
            if (depthBuffer) mask |= GL.GL_DEPTH_BUFFER_BIT;
            if (colorBuffer) mask |= GL.GL_COLOR_BUFFER_BIT;
            GL.glClear(mask);
            */
        } else {
            if (depthBuffer) mask |= WebGLRenderingContext.DEPTH_BUFFER_BIT;
            if (colorBuffer) mask |= WebGLRenderingContext.COLOR_BUFFER_BIT;
            Exp3DGWT.glContext.clear(mask);
        }
    }
    
    public static final class Scissor {
        public static final void enable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) { /* NOP */ }
            else Exp3DGWT.glContext.enable(WebGLRenderingContext.SCISSOR_TEST);
        }
        public static final void disable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) { /* NOP */ }
            else Exp3DGWT.glContext.disable(WebGLRenderingContext.SCISSOR_TEST);
        }
        
        public static final void setTo(int x, int y, int width, int height) {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) { /* NOP */ }
            else Exp3DGWT.glContext.scissor(x, y, width, height);
        }
    }
    
    public static final class DepthTest {
        
        public static final void enable() {
            Exp3DGWT.glContext.enable(WebGLRenderingContext.DEPTH_TEST);
            //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glEnable(GL.GL_DEPTH_TEST);
            //else GL2.glEnable(GL2.GL_DEPTH_TEST);
            }
        
        public static final void disable() { 
            Exp3DGWT.glContext.disable(WebGLRenderingContext.DEPTH_TEST);
            //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDisable(GL.GL_DEPTH_TEST); 
            //else GL2.glDisable(GL2.GL_DEPTH_TEST); 
            }
        
        public static final void setReadOnly(boolean readOnly) {
            Exp3DGWT.glContext.depthMask(!readOnly);
        }
    }
    
    public static final class Dithering {
        public static final void enable() {
            Exp3DGWT.glContext.enable(WebGLRenderingContext.DITHER);
            //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glEnable(GL.GL_DITHER);
            //else GL2.glEnable(GL2.GL_DITHER);
        }
        
        public static final void disable() { 
            Exp3DGWT.glContext.disable(WebGLRenderingContext.DITHER);
            //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDisable(GL.GL_DITHER); 
            //else GL2.glDisable(GL2.GL_DITHER);
        }
    }

    public static final void setViewport(int x, int y, int width, int height) {
        Exp3DGWT.glContext.viewport(x, y, width, height);
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glViewport(x, y, width, height);
        //else GL2.glViewport(x, y, width, height);
    }
    
    // Matrix management for OGL ES 2.0
    public static MatrixStack matrixStack = new MatrixStack(32); //Used for the modelView matrix
    
    public static Matrix4 projectionMtx = new Matrix4();
    
    public static Matrix4 MVPMatrix = new Matrix4();
    
    //Load perspective projection
    public static final void loadPerspective(float nearPlane, float farPlane) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
      
        } else {
            projectionMtx.setToProjection(nearPlane, farPlane, 45.0f, (float)Screen.VIEWPORT_WIDTH / (float)Screen.VIEWPORT_HEIGHT);
            matrixStack.getCurrent().idt();
        }
    }
    
    public static final void loadPerspective() {
        loadPerspective(1f, 1000f);
    }
    
    public static final void positionOnZAxisToMatchScreen() {
        final float fov = 45f;
        float zValue = Screen.ARENA_HEIGHT * 0.5f / (float)Math.tan(fov * 0.5f * MathUtil.degreesToRadians);
        Renderer.translate(-Screen.ARENA_WIDTH * 0.5f, -Screen.ARENA_HEIGHT * 0.5f, -zValue);
    }
    
    public static void preDraw() {
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) return;
        
        MVPMatrix.set(projectionMtx);
        MVPMatrix.mul(matrixStack.getCurrent());
        
        Exp3DGWT.glContext.uniformMatrix4fv(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.MVPMatrix), false, MVPMatrix.val);
        
        if (ShaderPipeline.currentPipelineBound.hasUniform(ShaderManager.Uniform.MVMatrix))
            Exp3DGWT.glContext.uniformMatrix4fv(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.MVMatrix), false, matrixStack.getCurrent().val);
        
        if (ShaderPipeline.currentPipelineBound.hasUniform(ShaderManager.Uniform.customColor))
            Exp3DGWT.glContext.uniform4f(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.customColor), colorRed, colorGreen, colorBlue, colorAlpha);
    }
    
    //Load orthonormal projection
    public static final void loadOrtho() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            /*
            GL.glMatrixMode(GL.GL_PROJECTION);
            GL.glLoadIdentity();
            //Orthogonal
            GL.glOrthof(0.0f, Screen.ARENA_WIDTH, 0.0f, Screen.ARENA_HEIGHT, 100.0f, -100.0f);
            GL.glMatrixMode(GL.GL_MODELVIEW);
            GL.glLoadIdentity();
            */    
        } else {
            projectionMtx.setToOrtho2D(0, 0, Screen.ARENA_WIDTH, Screen.ARENA_HEIGHT, 100.0f, -100.0f);
            matrixStack.getCurrent().idt();
        }
    }
    
    public static final void enableLightMapMode(MeshVBOs mainMesh, 
            Texture colorMapTexture,
            MeshVBOs lightMapMesh,
            Texture lightMapTexture) 
    {
        currentTexture = null;
        
        ShaderManager.spLightmap.bind();
        Exp3DGWT.glContext.activeTexture(WebGLRenderingContext.TEXTURE0); 
        //Exp3DGWT.glContext.enable(WebGLRenderingContext.TEXTURE_2D);
        colorMapTexture.nativeBind(); 
        
        Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, mainMesh.texture_VBO.webglBuffer);
        Exp3DGWT.glContext.vertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), 2, WebGLRenderingContext.FLOAT, false, 0, 0);
        
        Exp3DGWT.glContext.activeTexture(WebGLRenderingContext.TEXTURE1); 
        //Exp3DGWT.glContext.enable(WebGLRenderingContext.TEXTURE_2D);
        lightMapTexture.nativeBind(); 
        
        Exp3DGWT.glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, lightMapMesh.texture_VBO.webglBuffer);
        Exp3DGWT.glContext.vertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.textureLM), 2, WebGLRenderingContext.FLOAT, false, 0, 0);
        
    }
    
    public static final void disableLightMapMode() {
        
        currentTexture = null;
        
        Exp3DGWT.glContext.activeTexture(WebGLRenderingContext.TEXTURE0); 
        
        ShaderManager.spTexture.bind();
        
    }
    
    public static final void resetToDefaultShading() {
        currentTexture = null;
        
        Exp3DGWT.glContext.activeTexture(WebGLRenderingContext.TEXTURE0); 
        
        ShaderManager.spTexture.bind();
    }
    
    // Color
    public static float colorRed = 1f, colorGreen = 1f, colorBlue = 1f, colorAlpha = 1f;
    
    public static final void setColor(float red, float green, float blue, float alpha) {
        if (usePremultipliedAlphaTextures) {
            red *= alpha;
            green *= alpha;
            blue *= alpha;
        }
        if (Platform.renderingAPI == GraphicsAPI.GLES1)  {
            //GL.glColor4f(red, green, blue, alpha);
        } else {
            colorRed = red; colorGreen = green; colorBlue = blue; colorAlpha = alpha;
        }
    }
    
    public static final void resetColor() {
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glColor4f(1f, 1f, 1f, 1f);
        //else 
        {colorRed = 1f; colorGreen = 1f; colorBlue = 1f; colorAlpha = 1f;}
    }
    
    public static final void loadMatrixIdentity() {
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glLoadIdentity();
        //else 
            matrixStack.getCurrent().idt();
    }
    
    public static final void pushMatrix() {
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glPushMatrix();
        //else 
        matrixStack.push();
    }
    
    public static final void popMatrix() {
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glPopMatrix();
        //else 
        matrixStack.pop();
    }
    
    public static final void scale(float x, float y, float z) {
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glScalef(x, y, z);
        //else 
        matrixStack.getCurrent().scaleGL(x, y, z);
    }
    
    public static final void translate(float x, float y, float z) {
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glTranslatef(x, y, z);
        //else 
        matrixStack.getCurrent().translateGL(x, y, z);
    }
    
    public static final void rotate(float angle, float x, float y, float z) {
        //if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glRotatef(angle, x, y, z);
        //else 
        matrixStack.getCurrent().rotateGL(angle, x, y, z);
    }
    
    public static final void checkGLErrors() {
        
        /*
        int error = Platform.renderingAPI == GraphicsAPI.GLES1 ? GL.glGetError() : GL2.glGetError();
        if (error != 0) {
            Log.e(TAG, "GL error code " + error);
        } else {
            Log.i(TAG, "GL error code " + error);
        }
        */
        int error = Exp3DGWT.glContext.getError();
        if (error != WebGLRenderingContext.NO_ERROR) {
                String message = "WebGL Error: " + error;
                GWT.log(message, null);
                throw new RuntimeException(message);
        }
    }
    
}
