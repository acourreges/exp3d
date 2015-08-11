package com.breakingbyte.wrap.shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.breakingbyte.wrap.GL;
import com.breakingbyte.wrap.Platform;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;

public class Light {
    
    private static float[] lightAmbient = {1f, 1f, 1f, 1.0f};
    private static float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private static float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
    public  static float[] lightPosition = {-0.3f, -0.1f, -1f, 0.0f};
    
    /* The buffers for our light values */
    protected static FloatBuffer lightAmbientBuffer;
    protected static FloatBuffer lightDiffuseBuffer;
    protected static FloatBuffer lightSpecularBuffer;
    protected static FloatBuffer lightPositionBuffer;
    
    static {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightAmbientBuffer = byteBuf.asFloatBuffer();
        
        byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightDiffuseBuffer = byteBuf.asFloatBuffer();
        
        byteBuf = ByteBuffer.allocateDirect(lightSpecular.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightSpecularBuffer = byteBuf.asFloatBuffer();
        
        byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightPositionBuffer = byteBuf.asFloatBuffer();
    }

    public static void setAmbient(float red, float green, float blue, float alpha) {
        lightAmbient[0] = red;
        lightAmbient[1] = green;
        lightAmbient[2] = blue;
        lightAmbient[3] = alpha;
    }
    
    public static void setDiffuse(float red, float green, float blue, float alpha) {
        lightDiffuse[0] = red;
        lightDiffuse[1] = green;
        lightDiffuse[2] = blue;
        lightDiffuse[3] = alpha;

    }
    
    public static void setSpecular(float red, float green, float blue, float alpha) {
        lightSpecular[0] = red;
        lightSpecular[1] = green;
        lightSpecular[2] = blue;
        lightSpecular[3] = alpha;
    }
    
    public static void setPosition(float x, float y, float z, float w) {
        lightPosition[0] = x;
        lightPosition[1] = y;
        lightPosition[2] = z;
        lightPosition[3] = w;
    }
    
    public static void apply() {
        if (Platform.renderingAPI != GraphicsAPI.GLES1) return; // Handled in Renderer for GL ES 2
        Renderer.Lighting.enable();
        
        lightAmbientBuffer.put(lightAmbient);
        lightAmbientBuffer.position(0);
        
        lightDiffuseBuffer.put(lightDiffuse);
        lightDiffuseBuffer.position(0);
        
        lightSpecularBuffer.put(lightSpecular);
        lightSpecularBuffer.position(0);
        
        lightPositionBuffer.put(lightPosition);
        lightPositionBuffer.position(0);
        
        GL.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, lightAmbientBuffer);
        GL.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuseBuffer);
        GL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPositionBuffer);
        GL.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, lightSpecularBuffer);
        
        Renderer.Lighting.disable();
    }
    
}
