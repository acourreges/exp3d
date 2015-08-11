package com.breakingbyte.wrap.shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.breakingbyte.wrap.GL2;
import com.breakingbyte.wrap.Log;

public class ShaderPipeline {
    
    public static final String TAG = "ShaderPipeline";
    
    public static ShaderPipeline currentPipelineBound = null;
    
    public Shader vertexShader, fragmentShader;
    
    public ArrayList<String> attributes;
    
    public HashMap<String, Integer> defaulAttributetBinding;
    public HashMap<String, Integer> attributeLocation;
    
    public ArrayList<String> uniforms;
    public HashMap<String, Integer> uniformLocation;
    
    public ArrayList<String> uniformTexturesNames;
    public HashMap<String, Integer> uniformTextures;
    
    public int programId;
    
    public ShaderPipeline(Shader vertexShader, Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        attributes = new ArrayList<String>();
        defaulAttributetBinding = new HashMap<String, Integer>();
        attributeLocation = new HashMap<String, Integer>();
        uniforms = new ArrayList<String>();
        uniformLocation = new HashMap<String, Integer>();
        uniformTextures = new HashMap<String, Integer>();
        uniformTexturesNames = new ArrayList<String>();
        load();
    }
    
    public void addAttribute(String name) {
        attributes.add(name);
    }
    
    public void addAttributeWithBinding(String name, int defaultBinding) {
        addAttribute(name);
        defaulAttributetBinding.put(name, defaultBinding);
    }

    public void addUniform(String name) {
        uniforms.add(name);
    }
    
    public void addUniformTexture(String name, int texureUnitNumber) {
        addUniform(name);
        uniformTextures.put(name, texureUnitNumber);
        uniformTexturesNames.add(name);
    }
    
    public boolean load() {
        if (vertexShader.shaderID == 0) {
            Log.e(TAG, "Vertex shader is not compiled!");
            return false;
        }
        if (fragmentShader.shaderID == 0) {
            Log.e(TAG, "Fragment shader is not compiled!");
            return false;
        }
        
        programId = GL2.glCreateProgram();
        
        if (programId == 0) {
            Log.e(TAG, "Could not create shader pipeline!");
            return false;
        }
        
        // Bind the vertex shader to the program.
        GL2.glAttachShader(programId, vertexShader.shaderID);
     
        // Bind the fragment shader to the program.
        GL2.glAttachShader(programId, fragmentShader.shaderID);
        
        for (String str : defaulAttributetBinding.keySet()) {
            GL2.glBindAttribLocation(programId, defaulAttributetBinding.get(str), str);
        }
        
        // Link the two shaders together into a program.
        GL2.glLinkProgram(programId);
        
        // Get the link status.
        IntBuffer linkStatus = ByteBuffer.allocateDirect(4)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        GL2.glGetProgramiv(programId, GL2.GL_LINK_STATUS, linkStatus);
     
        // If the link failed, delete the program.
        if (linkStatus.get(0) == 0)
        {
            Log.e(TAG, "Could not link shader pipeline!");
            Log.e(TAG, "Error is " + GL2.glGetProgramInfoLog(programId));
            GL2.glDeleteProgram(programId);
            programId = 0;
            return false;
        }
        
        for (String str : attributes) {
            int id = GL2.glGetAttribLocation(programId, str);
            if (id == -1) {
                Log.e(TAG, "Could not find attribute location: " + str);
                return false;
            }
            attributeLocation.put(str, id);
        }
        
        for (String str : uniforms) {
            int id = GL2.glGetUniformLocation(programId, str);
            if (id == -1) {
                Log.e(TAG, "Could not find uniform location: " + str);
                return false;
            }
            uniformLocation.put(str, id);
        }
        
        return true;
        
    }
    
    public boolean hasUniform(String str) {
        return uniforms.contains(str);
    }
    
    public boolean hasAttribute(String str) {
        return attributes.contains(str);
    }
    
    @SuppressWarnings("unused")
    private int[] previousAttributes = new int[32];
    @SuppressWarnings("unused")
    private int previousAttributesSize = 0;
    public void bind() {
        
        if (currentPipelineBound == this) return;
        
        //Check old used vertex attributes, and disable/enable only the necessary ones
        /*
        if (currentPipelineBound != null) {
            ShaderPipeline oldPipeline = currentPipelineBound;
            ShaderPipeline newPipeline = this;
            
            for (int i = 0; i < oldPipeline.attributes.size(); i++) {
                String attr = oldPipeline.attributes.get(i);
                Integer idNumber = oldPipeline.attributeLocation.get(attr);
                for (int j = 0; j < newPipeline.attributes.size(); j++) {
                    String attrNew = newPipeline.attributes.get(j);
                    Integer idNumberNew = newPipeline.attributeLocation.get(attrNew);
                    //if ()
                }
            }
        }
        */
        //TODO optimize
        if (currentPipelineBound != null) {
            ShaderPipeline oldPipeline = currentPipelineBound;
            
            for (int i = 0; i < oldPipeline.attributes.size(); i++) {
                GL2.glDisableVertexAttribArray(oldPipeline.attributeLocation.get(oldPipeline.attributes.get(i)));
            }
        }
        for (int i = 0; i < attributes.size(); i++) {
            GL2.glEnableVertexAttribArray(attributeLocation.get(attributes.get(i)));
        }
        
        
        // Tell OpenGL to use this program when rendering.
        GL2.glUseProgram(programId);
        
        //Bind needed texture units
        for (int i = 0; i < uniformTexturesNames.size(); i++) {
            String name = uniformTexturesNames.get(i);
            GL2.glUniform1i(uniformLocation.get(name), uniformTextures.get(name));
        }
        
        
        currentPipelineBound = this;
    }
}
