package com.breakingbyte.wrap.shared;




public class Light {
    
    private static float[] lightAmbient = {1f, 1f, 1f, 1.0f};
    private static float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private static float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
    public  static float[] lightPosition = {-0.3f, -0.1f, -1f, 0.0f};
    

    
    static {

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
        if (true) return; //Taken care of in Renderer
    }
    
}
