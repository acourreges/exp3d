package com.breakingbyte.wrap.shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.breakingbyte.wrap.GL2;
import com.breakingbyte.wrap.Log;

public class Shader {

    public static final String TAG = "Shader";
    
    public enum ShaderType {
        Vertex,
        Fragment
    }
    public ShaderType shaderType;
    
    public int shaderID;
    
    public String code;
    
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
            shaderID = GL2.glCreateShader(GL2.GL_VERTEX_SHADER);
        else
            shaderID = GL2.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        
        if (shaderID == 0) {
            Log.e(TAG, "Could not create shader!");
            return false;
        }
        
        // Pass in the shader source
        GL2.glShaderSource(shaderID, code);
        // Compile the shader
        GL2.glCompileShader(shaderID);
        
        IntBuffer compileStatus = ByteBuffer.allocateDirect(4)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        GL2.glGetShaderiv(shaderID, GL2.GL_COMPILE_STATUS, compileStatus);
     
        // If the compilation failed, delete the shader
        if (compileStatus.get(0) == 0)
        {
            Log.e(TAG, "Compilation failed with code " + compileStatus.get(0));
            Log.e(TAG, "Error is " + GL2.glGetShaderInfoLog(shaderID));
            GL2.glDeleteShader(shaderID);
            shaderID = 0;
            return false;
        }
        
        return true;
    }
    
}
