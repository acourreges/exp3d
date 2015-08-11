package com.breakingbyte.wrap.shared;

import com.client.Exp3DGWT;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLShader;

public class Shader {

    public static final String TAG = "Shader";
    
    public enum ShaderType {
        Vertex,
        Fragment
    }
    public ShaderType shaderType;
    
    public int shaderID;
    
    public String code;
    
    WebGLShader webglShader;
    
    public static Shader createVertexShader(String code) {
        Shader result = new Shader();
        result.shaderType = ShaderType.Vertex;
        result.code = code;
        return result;
    }
    
    public static Shader createFragmentShader(String code) {
        Shader result = new Shader();
        result.shaderType = ShaderType.Fragment;
        result.code = code;
        return result;
    }
    
    public boolean load() {
        if (shaderType == ShaderType.Vertex) 
        	webglShader = Exp3DGWT.glContext.createShader(WebGLRenderingContext.VERTEX_SHADER);
        else
        	webglShader = Exp3DGWT.glContext.createShader(WebGLRenderingContext.FRAGMENT_SHADER);
        
        /*
        if (shaderID == 0) {
            Log.e(TAG, "Could not create shader!");
            return false;
        }
        */
        
        Exp3DGWT.glContext.shaderSource(webglShader, code);
        Exp3DGWT.glContext.compileShader(webglShader);
        
        if (!Exp3DGWT.glContext.getShaderParameterb(webglShader, WebGLRenderingContext.COMPILE_STATUS)) {
            throw new RuntimeException(Exp3DGWT.glContext.getShaderInfoLog(webglShader));
        }
        
        return true;
    }
    
}
