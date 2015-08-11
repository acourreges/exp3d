package com.breakingbyte.game.util;

import java.util.ArrayList;

import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.shared.VBO;
import com.breakingbyte.game.render.VBOEnum.Type;
import com.breakingbyte.game.util.MeshVBOs;


public class OBJLoader {
    
    private static String TAG = "OBJLoader";
    
    public static class StringReader {
        
        public String str;
        public int currentIdx;
        
        public StringReader(String all) {
            str = all;
            currentIdx = 0;
        }
        
        public String readLine() {
            if (currentIdx >= str.length()) return null;
            int initial = currentIdx;
            while (currentIdx < str.length() && str.charAt(currentIdx) != '\n' && str.charAt(currentIdx) != '\r') currentIdx++;
            return str.substring(initial, currentIdx++);
        }
        
    }
    
    public static MeshVBOs loadModelFromString(String is) {
        return readOBJFile(is, false);
    }
    
    public static MeshVBOs loadLightMapModelFromString(String is) {
        return readOBJFile(is, true);
    }
    
    public static StringTokenizer tok = new StringTokenizer("");
    public static StringTokenizer subTok = new StringTokenizer("");
    
    public static MeshVBOs readOBJFile(String source, boolean isLightMap) {
        
        //Vertex
        ArrayList<Float> vertices = new ArrayList<Float>();
        
        //Textures
        ArrayList<Float> textures = new ArrayList<Float>();
        
        //Normals
        ArrayList<Float> normals = new ArrayList<Float>();
        
        //Indices
        ArrayList<Short> indices = new ArrayList<Short>();
        
        //Create pre-VBOs structures
        ArrayList<Vertex> verticesOBJ = new ArrayList<Vertex>();
        ArrayList<Vertex> texturesOBJ = new ArrayList<Vertex>();
        ArrayList<Vertex> normalsOBJ = new ArrayList<Vertex>();
        //ArrayList<Short> indicesOBJ = new ArrayList<Short>();
        
                
        //InputStreamReader streamReader = new InputStreamReader(inputStream);
        //BufferedReader reader = new BufferedReader(streamReader);
        StringReader reader = new StringReader(source);
        
        boolean hasNormals = false;
        
        int indicesIndex = 0;
        
        String line = null;
        while ((line = reader.readLine()) != null) {
            
            if (line.startsWith("v ")) {
                //vertex
                tok.reinit(line);
                tok.nextToken();
                Vertex v = new Vertex(Float.parseFloat(tok.nextToken()),
                        Float.parseFloat(tok.nextToken()),
                        Float.parseFloat(tok.nextToken()));
                verticesOBJ.add(v);
                //Log.d("ObjLoader", "Read vertex: " + v.x + " " + v.y + " " + v.z);

            }
            else if (line.startsWith("vt ")) {
                //texture
                tok.reinit(line);
                tok.nextToken();
                Vertex v = new Vertex(Float.parseFloat(tok.nextToken()),
                        1 - Float.parseFloat(tok.nextToken()), //of course v bad
                        0f);
                texturesOBJ.add(v);
                //Log.d("ObjLoader", "Read uv: " + v.x + " " + v.y + " " + v.z);

            }
            if (line.startsWith("vn ")) {
                //normal
                tok.reinit(line);
                tok.nextToken();
                Vertex v = new Vertex(Float.parseFloat(tok.nextToken()),
                        Float.parseFloat(tok.nextToken()),
                        Float.parseFloat(tok.nextToken()));
                normalsOBJ.add(v);
                hasNormals = true;

            }
            else if (line.startsWith("f ")) {
                //face
                tok.reinit(line);
                tok.nextToken();
                
                for (int i = 0; i < 3; i++) {
                    subTok.reinit(tok.nextToken(), "/");
                    short vert = (short)(Short.parseShort(subTok.nextToken()) - 1);
                    short text = (short)(Short.parseShort(subTok.nextToken()) - 1);
                    //Log.d("ObjLoader", "Face: " + vert + " " + text);
                    short norm = 0;
                    if (hasNormals) norm = (short)(Short.parseShort(subTok.nextToken()) - 1);
                    
                    //short normale = (short)(Short.parseShort(subTok.nextToken()));
                    
                    //Add vertices
                    Vertex v = verticesOBJ.get(vert);
                    vertices.add(v.x);vertices.add(v.y);vertices.add(v.z);
                    
                    //Add texture
                    v = texturesOBJ.get(text);
                    textures.add(v.x);textures.add(v.y);
                    
                    if (hasNormals)
                    {
                        //Add normal
                        v = normalsOBJ.get(norm);
                        normals.add(v.x);normals.add(v.y);normals.add(v.z);
                    }
                    
                    //Add indices
                    indices.add((short)indicesIndex++);
                    
                }
            }

            
        }
        
        //Debug
        Log.d(TAG, "Read " + vertices.size() + "v " + normals.size() + "n " + indices.size() + "i");

        
        //Send to native memory
        MeshVBOs vbos = new MeshVBOs();
        
        //Vertices
        if (!isLightMap) {
            float[] vertexData =  new float[vertices.size()];
            
            //TODO should optimize this
            for (int i = 0; i < vertices.size(); i++) {
                vertexData[i] = vertices.get(i);
            }
            
            vbos.vertex_VBO = new VBO(Type.POSITION);
            vbos.vertex_VBO.fillBuffer(vertexData);
        }
        
        //Normals
        if (!isLightMap) {
            if (hasNormals) {
                float[] normalData =  new float[normals.size()];
                
                //TODO should optimize this
                for (int i = 0; i < normals.size(); i++) {
                    normalData[i] = normals.get(i);
                }
                
                vbos.normal_VBO = new VBO(Type.NORMAL);
                vbos.normal_VBO.fillBuffer(normalData);
            }
        }
        
        //Textures
        float[] textureData =  new float[textures.size()];
        
        //TODO should optimize this
        for (int i = 0; i < textures.size(); i++) {
            textureData[i] = textures.get(i);
        }
        
        vbos.texture_VBO = new VBO(Type.TEXTURE);
        vbos.texture_VBO.fillBuffer(textureData);
        
        //Indices
        if (!isLightMap) {
            short[] indexData =  new short[indices.size()];
            
            //TODO should optimize this
            for (int i = 0; i < indices.size(); i++) {
                indexData[i] = (short)(indices.get(i)); 
            }
            
            vbos.index_VBO = new VBO(Type.INDEX);
            vbos.index_VBO.fillBuffer(indexData);
        }
        
        vbos.nbIndex = indices.size();
        
        vbos.uploadVBOs();
        
        return vbos;

    }

    
    public static class Vertex {
        float x, y, z;

        public Vertex(float x, float y, float z) {
            super();
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
    }

}
