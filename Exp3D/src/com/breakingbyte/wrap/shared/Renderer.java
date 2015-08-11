package com.breakingbyte.wrap.shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.ShaderManager;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.util.MathUtil;
import com.breakingbyte.game.util.Matrix4;
import com.breakingbyte.game.util.MatrixStack;
import com.breakingbyte.game.util.MeshVBOs;
import com.breakingbyte.wrap.GL;
import com.breakingbyte.wrap.GL2;
import com.breakingbyte.wrap.GLU;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.Platform;

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

    //If the lighting is currently on or off
    private static boolean lightingON = false;
    
    public static final void initialize() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            switchToVBOMode();
            GL.glEnable(GL.GL_RESCALE_NORMAL);
        }
    }
    
    public static final void enableTexture() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glEnable(GL.GL_TEXTURE_2D);
        } else {
        }
    }
    
    public static final void bindTexture(Texture texture) {
        if (currentTexture == texture) return;
        currentTexture = texture;
        currentTexture.nativeBind();
    }
    
    //Buffer, arrays, VBOs...
    
    
    public static class Lighting {

        public static final void enable() {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) {
                if (lightingON) return;
                GL.glEnableClientState(GL.GL_NORMAL_ARRAY);
                GL.glEnable(GL.GL_LIGHTING);
                GL.glEnable(GL.GL_LIGHT0);   
                GL.glShadeModel(GL.GL_SMOOTH);
                lightingON = true;
            } else {
                ShaderManager.spLighting.bind();
                //float lightPos[] = new float[] {0f, 0f, 1f, 0f};
                float lightPos[] = Light.lightPosition;
                
                if (ShaderPipeline.currentPipelineBound.hasUniform(ShaderManager.Uniform.lightPosition))
                    GL2.glUniform4f(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.lightPosition), lightPos[0], lightPos[1], lightPos[2], lightPos[3]);
            }
        }
        
        public static final void disable() {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) {
                if (!lightingON) return;
                GL.glDisableClientState(GL.GL_NORMAL_ARRAY);
                GL.glDisable(GL.GL_LIGHTING);
                GL.glDisable(GL.GL_LIGHT0);  
                GL.glShadeModel(GL.GL_FLAT);
                lightingON = false;
            } else {
                ShaderManager.spTexture.bind();
            }
        }
        
    }

    
    public static final void unbindVBOs() {
        if (Debug.noVBO) return;
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            //Bind back to 0
            GL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            GL.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            GL2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
            GL2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }
    
    public static final void switchToVBOMode() {
        GL.glEnableClientState( GL.GL_VERTEX_ARRAY );
        GL.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);         
        //GL.glEnableClientState(GL.GL_NORMAL_ARRAY);
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
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glEnable(GL.GL_BLEND); 
            else GL2.glEnable(GL2.GL_BLEND); 
        }
        
        public static final void disable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDisable(GL.GL_BLEND);
            else GL2.glDisable(GL2.GL_BLEND);
            }
        
        public static final void resetMode() {
            setMode(BlendingMode.NORMAL);
        }
        
        public static final void setMode(BlendingMode mode) {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) {
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
            } else {
                switch (mode) {
                    case NORMAL: 
                        GL2.glBlendFunc(usePremultipliedAlphaTextures? GL2.GL_ONE : GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
                        break;
                    case ADDITIVE: 
                        GL2.glBlendFunc(usePremultipliedAlphaTextures? GL2.GL_ONE : GL2.GL_SRC_ALPHA, GL2.GL_ONE);
                        break; 
                    case EXPLOSION: 
                        GL2.glBlendFunc(usePremultipliedAlphaTextures? GL2.GL_ONE : GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
                        break; 
                }
            }
        }
        
    }

    public static final void setClearColor(float red, float green, float blue, float alpha) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glClearColor(red, green, blue, alpha); 
        else GL2.glClearColor(red, green, blue, alpha); 
    }
    
    public static final void clearDepthBuffer() { clear(true, false); }
    
    public static final void clearColorBuffer() { clear(false, true); }
    
    public static final void clear(boolean depthBuffer, boolean colorBuffer) {
        int mask = 0;
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            if (depthBuffer) mask |= GL.GL_DEPTH_BUFFER_BIT;
            if (colorBuffer) mask |= GL.GL_COLOR_BUFFER_BIT;
            GL.glClear(mask);
        } else {
            if (depthBuffer) mask |= GL2.GL_DEPTH_BUFFER_BIT;
            if (colorBuffer) mask |= GL2.GL_COLOR_BUFFER_BIT;
            GL2.glClear(mask);
        }
    }
    
    public static final class Scissor {
        public static final void enable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glEnable(GL.GL_SCISSOR_TEST);
            else GL2.glEnable(GL2.GL_SCISSOR_TEST);
        }
        public static final void disable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDisable(GL.GL_SCISSOR_TEST);
            else GL2.glDisable(GL2.GL_SCISSOR_TEST);
        }
        
        public static final void setTo(int x, int y, int width, int height) {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glScissor(x, y, width, height);
            else GL2.glScissor(x, y, width, height);
        }
    }
    
    public static final class DepthTest {
        
        public static final void enable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glEnable(GL.GL_DEPTH_TEST);
            else GL2.glEnable(GL2.GL_DEPTH_TEST);
            }
        
        public static final void disable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDisable(GL.GL_DEPTH_TEST); 
            else GL2.glDisable(GL2.GL_DEPTH_TEST); 
            }
        
        public static final void setReadOnly(boolean readOnly) {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDepthMask(!readOnly); 
            else GL2.glDepthMask(!readOnly);
        }
    }
    
    public static final class Dithering {
        public static final void enable() {
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glEnable(GL.GL_DITHER);
            else GL2.glEnable(GL2.GL_DITHER);
        }
        
        public static final void disable() { 
            if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glDisable(GL.GL_DITHER); 
            else GL2.glDisable(GL2.GL_DITHER);
        }
    }

    public static final void setViewport(int x, int y, int width, int height) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glViewport(x, y, width, height);
        else GL2.glViewport(x, y, width, height);
    }
    
    // Matrix management for OGL ES 2.0
    private static MatrixStack matrixStack = new MatrixStack(32); //Used for the modelView matrix
    
    public static Matrix4 projectionMtx = new Matrix4();
    
    public static Matrix4 MVPMatrix = new Matrix4();
    
    //Load perspective projection
    public static final void loadPerspective(float nearPlane, float farPlane) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glMatrixMode(GL.GL_PROJECTION);
            GL.glLoadIdentity();
            //Perspective
            GLU.gluPerspective(45.0f, (float)Screen.VIEWPORT_WIDTH / (float)Screen.VIEWPORT_HEIGHT, nearPlane, farPlane);
            GL.glMatrixMode(GL.GL_MODELVIEW);
            GL.glLoadIdentity();       
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
        Renderer.translate(-Screen.ARENA_WIDTH * 0.5f, -Screen.ARENA_HEIGHT * 0.5f, -zValue * 1f);
        Renderer.scale(1f, 1f, -1f); //Small hack because in the default orthogonal projection below near and far values have been inverted by mistake...
    }
    
    private static FloatBuffer tmpUniform = null;
    public static void preDraw() {
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) return;
        
        MVPMatrix.set(projectionMtx);
        MVPMatrix.mul(matrixStack.getCurrent());
        
        if (tmpUniform == null) {
            tmpUniform = ByteBuffer.allocateDirect(16 * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        tmpUniform.put(MVPMatrix.val).position(0);
        
        //if (ShaderPipeline.currentPipelineBound.hasUniform(ShaderManager.Uniform.MVPMatrix))
            GL2.glUniformMatrix4fv(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.MVPMatrix), 1, false, tmpUniform);
        
        if (ShaderPipeline.currentPipelineBound.hasUniform(ShaderManager.Uniform.MVMatrix))
        {
            tmpUniform.put(matrixStack.getCurrent().val).position(0);
            GL2.glUniformMatrix4fv(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.MVMatrix), 1, false, tmpUniform);
        }
            
        if (ShaderPipeline.currentPipelineBound.hasUniform(ShaderManager.Uniform.customColor))
            GL2.glUniform4f(ShaderPipeline.currentPipelineBound.uniformLocation.get(ShaderManager.Uniform.customColor), colorRed, colorGreen, colorBlue, colorAlpha);
    }
    
    //Load orthogonal projection
    public static final void loadOrtho() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glMatrixMode(GL.GL_PROJECTION);
            GL.glLoadIdentity();
            //Orthogonal
            GL.glOrthof(0.0f, Screen.ARENA_WIDTH, 0.0f, Screen.ARENA_HEIGHT, 100.0f, -100.0f);
            GL.glMatrixMode(GL.GL_MODELVIEW);
            GL.glLoadIdentity();    
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
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glClientActiveTexture(GL.GL_TEXTURE0); // first texture LightMap
            GL.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            
            if (!Debug.noVBO) {
                GL.glBindBuffer(GL.GL_ARRAY_BUFFER, lightMapMesh.texture_VBO.id);
                GL.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
            } else {
                GL.glTexCoordPointer(2, GL.GL_FLOAT, 0, lightMapMesh.texture_VBO.nativeBuffer);
            }
            
            GL.glClientActiveTexture(GL.GL_TEXTURE1); // second texture
            GL.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            if (!Debug.noVBO) {
                GL.glBindBuffer(GL.GL_ARRAY_BUFFER, mainMesh.texture_VBO.id);
                GL.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
            } else {
                GL.glTexCoordPointer(2, GL.GL_FLOAT, 0, mainMesh.texture_VBO.nativeBuffer);
            }
            
            // Enable 2D texturing
            GL.glActiveTexture(GL.GL_TEXTURE0); // lightmap
            GL.glEnable(GL.GL_TEXTURE_2D);
            lightMapTexture.nativeBind(); //lightmap
                        
            GL.glActiveTexture(GL.GL_TEXTURE1);
            GL.glEnable(GL.GL_TEXTURE_2D);
            colorMapTexture.nativeBind();
            
            /* Set the texture environment mode for this texture to combine */
            GL.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE);
        } else {
            ShaderManager.spLightmap.bind();
            GL2.glActiveTexture(GL.GL_TEXTURE0); 
            //GL2.glEnable(GL2.GL_TEXTURE_2D);
            colorMapTexture.nativeBind(); 
            
            if (!Debug.noVBO) {
                GL2.glBindBuffer(GL2.GL_ARRAY_BUFFER, mainMesh.texture_VBO.id);
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), 2, GL2.GL_FLOAT, false, 0, 0);
            } else {
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.texture), 2, GL2.GL_FLOAT, false, 0, mainMesh.texture_VBO.nativeBuffer);
            }
            
            GL2.glActiveTexture(GL2.GL_TEXTURE1); 
            //GL2.glEnable(GL2.GL_TEXTURE_2D);
            lightMapTexture.nativeBind(); 
            
            if (!Debug.noVBO) {
                GL2.glBindBuffer(GL2.GL_ARRAY_BUFFER, lightMapMesh.texture_VBO.id);
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.textureLM), 2, GL2.GL_FLOAT, false, 0, 0);
            } else {
                GL2.glVertexAttribPointer(ShaderPipeline.currentPipelineBound.attributeLocation.get(ShaderManager.Attribute.textureLM), 2, GL2.GL_FLOAT, false, 0, lightMapMesh.texture_VBO.nativeBuffer);
            }
            
            
        }
       
    }
    
    public static final void disableLightMapMode() {
        currentTexture = null;
        //Disable multi-texture
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
            GL.glActiveTexture(GL.GL_TEXTURE1);
            GL.glBindTexture(GL.GL_TEXTURE_2D, 0);
            GL.glDisable(GL.GL_TEXTURE_2D);
            
            GL.glClientActiveTexture(GL.GL_TEXTURE1);
            if (!Debug.noVBO) GL.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            GL.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            
            
            GL.glClientActiveTexture(GL.GL_TEXTURE0);
            GL.glActiveTexture(GL.GL_TEXTURE0);
            GL.glEnable(GL.GL_TEXTURE_2D);
        } else {
            //GL2.glActiveTexture(GL.GL_TEXTURE1); 
            //GL2.glDisable(GL2.GL_TEXTURE_2D);
            
            currentTexture = null;
            
            GL2.glActiveTexture(GL2.GL_TEXTURE0); 
            
            ShaderManager.spTexture.bind();
        }
    }
    
    public static final void resetToDefaultShading() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            currentTexture = null;
        } else {
            currentTexture = null;
            
            GL2.glActiveTexture(GL2.GL_TEXTURE0); 
            
            ShaderManager.spTexture.bind();
        }
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
            GL.glColor4f(red, green, blue, alpha);
        } else {
            colorRed = red; colorGreen = green; colorBlue = blue; colorAlpha = alpha;
        }
    }
    
    public static final void resetColor() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glColor4f(1f, 1f, 1f, 1f);
        else {colorRed = 1f; colorGreen = 1f; colorBlue = 1f; colorAlpha = 1f;}
    }
    
    public static final void loadMatrixIdentity() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glLoadIdentity();
        else matrixStack.getCurrent().idt();
    }
    
    public static final void pushMatrix() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glPushMatrix();
        else matrixStack.push();
    }
    
    public static final void popMatrix() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glPopMatrix();
        else matrixStack.pop();
    }
    
    public static final void scale(float x, float y, float z) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glScalef(x, y, z);
        else matrixStack.getCurrent().scaleGL(x, y, z);
    }
    
    public static final void translate(float x, float y, float z) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glTranslatef(x, y, z);
        else matrixStack.getCurrent().translateGL(x, y, z);
    }
    
    public static final void rotate(float angle, float x, float y, float z) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) GL.glRotatef(angle, x, y, z);
        else matrixStack.getCurrent().rotateGL(angle, x, y, z);
    }
    
    public static final void checkGLErrors() {
        
        int error = Platform.renderingAPI == GraphicsAPI.GLES1 ? GL.glGetError() : GL2.glGetError();
        if (error != 0) {
            Log.e(TAG, "GL error code " + error);
        } else {
            Log.i(TAG, "GL error code " + error);
        }
    }
    
}
