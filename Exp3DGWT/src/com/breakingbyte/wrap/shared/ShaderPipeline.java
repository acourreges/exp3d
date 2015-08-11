package com.breakingbyte.wrap.shared;

import java.util.ArrayList;
import java.util.HashMap;

import com.breakingbyte.wrap.Log;
import com.client.Exp3DGWT;
import com.googlecode.gwtgl.binding.WebGLProgram;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLUniformLocation;

public class ShaderPipeline {
    
    public static final String TAG = "ShaderPipeline";
    
    public static ShaderPipeline currentPipelineBound = null;
    
    public Shader vertexShader, fragmentShader;
    
    public ArrayList<String> attributes;
    
    public HashMap<String, Integer> defaulAttributetBinding;
    public HashMap<String, Integer> attributeLocation;
    
    public ArrayList<String> uniforms;
    public HashMap<String, WebGLUniformLocation> uniformLocation;
    
    public ArrayList<String> uniformTexturesNames;
    public HashMap<String, Integer> uniformTextures;
    
    public int programId;
    
    WebGLProgram webglProgram;
    
    public ShaderPipeline(Shader vertexShader, Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        attributes = new ArrayList<String>();
        defaulAttributetBinding = new HashMap<String, Integer>();
        attributeLocation = new HashMap<String, Integer>();
        uniforms = new ArrayList<String>();
        uniformLocation = new HashMap<String, WebGLUniformLocation>();
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
    	/*
        if (vertexShader.shaderID == 0) {
            Log.e(TAG, "Vertex shader is not compiled!");
            return false;
        }
        if (fragmentShader.shaderID == 0) {
            Log.e(TAG, "Fragment shader is not compiled!");
            return false;
        }
        */
        
    	webglProgram = Exp3DGWT.glContext.createProgram();
        
        Exp3DGWT.glContext.attachShader(webglProgram, vertexShader.webglShader);
        Exp3DGWT.glContext.attachShader(webglProgram, fragmentShader.webglShader);
        
        /*
        for (String str : defaulAttributetBinding.keySet()) {
            GL2.glBindAttribLocation(programId, defaulAttributetBinding.get(str), str);
        }
        */
        
        // Link the two shaders together into a program.
        Exp3DGWT.glContext.linkProgram(webglProgram);
        
        // Get the link status.
        if (!Exp3DGWT.glContext.getProgramParameterb(webglProgram, WebGLRenderingContext.LINK_STATUS)) {
            throw new RuntimeException("Could not initialise shaders");
        }
        
        for (String str : attributes) {
            int id = Exp3DGWT.glContext.getAttribLocation(webglProgram, str);
            if (id == -1) {
                Log.e(TAG, "Could not find attribute location: " + str);
                return false;
            }
            attributeLocation.put(str, id);
        }
        
        for (String str : uniforms) {
        	WebGLUniformLocation id = Exp3DGWT.glContext.getUniformLocation(webglProgram, str);
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
                Exp3DGWT.glContext.disableVertexAttribArray(oldPipeline.attributeLocation.get(oldPipeline.attributes.get(i)));
            }
        }
        
        for (int i = 0; i < attributes.size(); i++) {
            Exp3DGWT.glContext.enableVertexAttribArray(attributeLocation.get(attributes.get(i)));
        }
        
        
        // Tell OpenGL to use this program when rendering.
        Exp3DGWT.glContext.useProgram(webglProgram);
        
        //Bind needed texture units
        for (int i = 0; i < uniformTexturesNames.size(); i++) {
            String name = uniformTexturesNames.get(i);
            Exp3DGWT.glContext.uniform1i(uniformLocation.get(name), uniformTextures.get(name));
        }
        
        currentPipelineBound = this;
    }
}
