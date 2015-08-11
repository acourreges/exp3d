package com.breakingbyte.game.render;

import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.Platform;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;
import com.breakingbyte.wrap.shared.Shader;
import com.breakingbyte.wrap.shared.ShaderPipeline;


public class ShaderManager {
    
    
    public static class Attribute {
        
        public static final String position     = "a_Position";
        
        public static final String normal       = "a_Normal";
        
        public static final String texture      = "a_TexCoordinate";
        
        public static final String textureLM    = "a_TexCoordinateLM";
    }
    
    public static class Uniform {
        
        public static final String MVPMatrix    = "u_MVPMatrix"; // Model View Projection
        
        public static final String MVMatrix     = "u_MVMatrix"; // Model View
        
        public static final String customColor  = "u_CustomColor";
        
        public static final String texture      = "u_Texture";
        
        public static final String textureLM    = "u_TextureLM";
        
        public static final String lightPosition  = "u_LightPosition";
        
    }
    
    public static Shader debugVS, debugFS;
    public static ShaderPipeline debugSP;
    
    //Texture shader
    public static Shader vsTexture;
    public static Shader fsTexture;
    public static ShaderPipeline spTexture; // position + UV + custom color
    
    //Lightmap shader
    public static Shader vsLightmap;
    public static Shader fsLightmap;
    public static ShaderPipeline spLightmap; // position + double set of UV and texture
    
    //Lighting shader
    public static Shader vsLighting;
    public static Shader fsLighting;
    public static ShaderPipeline spLighting; // position + double set of UV and texture
    
    
    public static void init() {
        
        if (Platform.renderingAPI != GraphicsAPI.GLES2) return;
        
        initDebug();

        loadAllShaders();
        
        spTexture.bind();
    }
    
    public static void loadAllShaders() {
        
        /* ------------------------------------------------------------------
         * Simple texture shader with custom color. No lighting at all.
         * ------------------------------------------------------------------
         */
        
        vsTexture = Shader.createVertexShader(
                "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
                
                + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                + "attribute vec2 a_TexCoordinate;\n" // Per-vertex texture coordinate information we will pass in.
               
                + "varying vec2 v_TexCoordinate;   \n"     // This will be passed into the fragment shader.
               
                + "void main()                    \n"     // The entry point for our vertex shader.
                + "{                              \n"
                + "   v_TexCoordinate = a_TexCoordinate;\n"     // Pass the color through to the fragment shader.
                                                          // It will be interpolated across the triangle.
                + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                + "}                              \n"    // normalized screen coordinates.
                );
        if (!vsTexture.load()) onError("Error loading VS");
        
        fsTexture = Shader.createFragmentShader(
                "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
                                                        // precision in the fragment shader.
                + "uniform sampler2D u_Texture; \n"
                + "uniform vec4 u_CustomColor;        \n"
                + "varying vec2 v_TexCoordinate; \n"     
                
                + "void main()                    \n"     // The entry point for our fragment shader.
                + "{                              \n"
                + "   gl_FragColor = u_CustomColor * texture2D(u_Texture, v_TexCoordinate);\n"     // Pass the color directly through the pipeline.
                + "}                              \n"
                );
        if (!fsTexture.load()) onError("Error loading FS");
        
        spTexture = new ShaderPipeline(vsTexture, fsTexture);
        spTexture.addAttributeWithBinding(Attribute.position, 0);
        spTexture.addAttributeWithBinding(Attribute.texture, 1);
        spTexture.addUniform(Uniform.MVPMatrix);
        spTexture.addUniform(Uniform.customColor);
        spTexture.addUniformTexture(Uniform.texture, 0);
        if (!spTexture.load()) onError("Error loading SP");
        
        
        /* ------------------------------------------------------------------
         * Light-map shader. (2 textures with 2 sets of UVs)
         * ------------------------------------------------------------------
         */
        
        vsLightmap = Shader.createVertexShader(
                "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
                
                + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                + "attribute vec2 a_TexCoordinate;\n"
                + "attribute vec2 a_TexCoordinateLM;\n" 
               
                + "varying vec2 v_TexCoordinate;   \n"
                + "varying vec2 v_TexCoordinateLM;   \n"
               
                + "void main()                    \n"     // The entry point for our vertex shader.
                + "{                              \n"
                + "   v_TexCoordinate = a_TexCoordinate;\n"   
                + "   v_TexCoordinateLM = a_TexCoordinateLM;\n"
                
                + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                + "}                              \n"    // normalized screen coordinates.
                );
        if (!vsLightmap.load()) onError("Error loading VS");
        
        
        fsLightmap = Shader.createFragmentShader(
                "precision mediump float;       \n"    
                
                + "uniform sampler2D u_Texture; \n"
                + "uniform sampler2D u_TextureLM; \n"
                
                + "varying vec2 v_TexCoordinate; \n"
                + "varying vec2 v_TexCoordinateLM; \n" 
                
                + "void main()                    \n"     
                + "{                              \n"
                + "   gl_FragColor = texture2D(u_Texture, v_TexCoordinate) * texture2D(u_TextureLM, v_TexCoordinateLM);\n" 
                + "}                              \n"
                );
        if (!fsLightmap.load()) onError("Error loading FS");
        
        spLightmap = new ShaderPipeline(vsLightmap, fsLightmap);
        spLightmap.addAttributeWithBinding(Attribute.position, 0);
        spLightmap.addAttributeWithBinding(Attribute.texture, 1);
        spLightmap.addAttributeWithBinding(Attribute.textureLM, 2);
        spLightmap.addUniform(Uniform.MVPMatrix);
        spLightmap.addUniformTexture(Uniform.texture, 0);
        spLightmap.addUniformTexture(Uniform.textureLM, 1);
        if (!spLightmap.load()) onError("Error loading SP");
        
        /* ------------------------------------------------------------------
         * Lighting shader. (Directional light with Blinn-Phong)
         * ------------------------------------------------------------------
         */
        
        vsLighting = Shader.createVertexShader(
                  "uniform mat4 u_MVPMatrix;      \n"   
                + "uniform mat4 u_MVMatrix;      \n"
                
                + "attribute vec4 a_Position;     \n"  
                + "attribute vec2 a_TexCoordinate;\n"
                + "attribute vec3 a_Normal;\n" 
               
                + "varying vec4 v_Position;   \n"
                + "varying vec2 v_TexCoordinate;   \n"
                + "varying vec3 v_Normal;   \n"
               
                + "void main()                    \n"    
                + "{                              \n"
                + "   v_Position = u_MVMatrix * a_Position;\n"
                + "   v_TexCoordinate = a_TexCoordinate;\n"   
                
                // Should ideally use the inverse transpose of the model-view, 
                // but since scaling operations are uniform in the game (the same for
                // each axis) we can take this shortcut. 
                
                + "   v_Normal = normalize( vec3(u_MVMatrix * vec4(a_Normal, 0.0)) );" 
                
                + "   gl_Position = u_MVPMatrix   \n"
                + "               * a_Position;   \n"
                + "}                              \n"
                );
        if (!vsLighting.load()) onError("Error loading VS");
        
        
        fsLighting = Shader.createFragmentShader(
                "precision mediump float;       \n"    
                
                + "uniform sampler2D u_Texture; \n"
                + "uniform vec4      u_LightPosition; \n"
                
                + "varying vec4 v_Position;   \n"
                + "varying vec2 v_TexCoordinate; \n"
                + "varying vec3 v_Normal; \n" 
                
                + "void main()                    \n"     
                + "{                              \n"
                + "   vec3 lightDirection = normalize(u_LightPosition).xyz; \n"
                + "   float directionalLightWeight = max( dot(normalize(v_Normal), lightDirection), 0.0 ); \n"
                
                + "   vec3 ambientColor = vec3(0.3, 0.3, 0.3); \n"
                + "   vec3 pointLightColor = vec3(1.0, 1.0, 1.0); \n"
                
                + "   vec3 lightWeight = ambientColor + 0.7 * directionalLightWeight * pointLightColor; \n"
                
                + "   vec4 texture = texture2D(u_Texture, v_TexCoordinate); \n"
                
                + "   gl_FragColor = vec4(texture.rgb * lightWeight, texture.a); \n" 
                + "}                              \n"
                );
        if (!fsLighting.load()) onError("Error loading FS");
        
        spLighting = new ShaderPipeline(vsLighting, fsLighting);
        spLighting.addAttributeWithBinding(Attribute.position, 0);
        spLighting.addAttributeWithBinding(Attribute.texture, 1);
        spLighting.addAttributeWithBinding(Attribute.normal, 2);
        spLighting.addUniform(Uniform.MVPMatrix);
        spLighting.addUniform(Uniform.MVMatrix);
        spLighting.addUniform(Uniform.lightPosition);
        spLighting.addUniformTexture(Uniform.texture, 0);
        if (!spLighting.load()) onError("Error loading SP Lighting");
        
    }
    
    
    public static void initDebug() {
        
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
             
              + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
              + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
             
              + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
             
              + "void main()                    \n"     // The entry point for our vertex shader.
              + "{                              \n"
              + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
                                                        // It will be interpolated across the triangle.
              + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
              + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
              + "}                              \n";    // normalized screen coordinates.

        final String fragmentShader =
                "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
                                                        // precision in the fragment shader.
              + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
                                                        // triangle per fragment.
              + "void main()                    \n"     // The entry point for our fragment shader.
              + "{                              \n"
              + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
              + "}                              \n";
        
        debugVS = Shader.createVertexShader(vertexShader);
        if (!debugVS.load()) onError("Error loading VS");

        debugFS = Shader.createFragmentShader(fragmentShader);
        if (!debugFS.load()) onError("Error loading FS");
        
        debugSP = new ShaderPipeline(debugVS, debugFS);
        debugSP.addAttributeWithBinding("a_Position", 0);
        debugSP.addAttributeWithBinding("a_Color", 1);
        debugSP.addUniform("u_MVPMatrix");
        
        if (!debugSP.load()) onError("Error loading SP");
        
    }
    
    public static void onError(String str) {
        Log.e("ShaderManager", str);
        Platform.exitApplication();
    }

}
