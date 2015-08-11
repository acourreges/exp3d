package com.breakingbyte.wrap;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GLProfile;

import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;



public class TextureUtil {
    
    private static String TAG = "TextureUtil";
    
    private static int[] singleID = new int[1];
    public static IntBuffer singleInteger = null;
    
    public static int loadTexture(int file) {
        int id = getNewTextureID();
        loadTexture( id, file);
        return id;        
    }
    
    public static int loadTexture(String filePath) {
        int id = getNewTextureID();
        loadTexture( id, filePath);
        return id;        
    }
    
    public static void unloadTexture(int id) {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            singleID[0] = id;
            GL.glDeleteTextures(1, singleID, 0);
        } else {
            singleInteger.position(0);
            singleInteger.put(id);
            singleInteger.position(0);
            GL2.glDeleteTextures(1, singleInteger);
        }
    }
    
    public static int getNewTextureID() {
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glGenTextures(1, singleID, 0);
            return singleID[0];
        } else {
            if (singleInteger == null) {
                singleInteger = ByteBuffer.allocateDirect(4)
                        .order(ByteOrder.nativeOrder()).asIntBuffer();
            }
            singleInteger.position(0);
            GL2.glGenTextures(1, singleInteger);
            return singleInteger.get(0);
        }
    }
    
    /**
     * Loads one texture file
     * @param glId OGL id
     * @param file id of the file in the res folder
     * @return
     */
    public static boolean loadTexture(int glId, int file){

        InputStream is =  Resource.getResInputStream(file);
        BufferedImage bitmap = loadARGBImage(is);
        
        return loadTexture(glId, bitmap);
    }
    
    public static boolean loadTexture(int glId, String filePath){

        InputStream is =  Resource.getResInputStream(filePath);
        BufferedImage bitmap = loadARGBImage(is);
        
        return loadTexture(glId, bitmap);
    }
    
    public static int loadTexture(BufferedImage bitmap) {
        int id = getNewTextureID();
        loadTexture( id, bitmap);
        return id;
    }
    
    public static boolean loadTexture(int glId, BufferedImage bitmap) {
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glBindTexture(GL.GL_TEXTURE_2D, glId);
            
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        } else {
            GL2.glBindTexture(GL2.GL_TEXTURE_2D, glId);
            
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        }
        

        
        TextureData texData = null;
        if (Platform.renderingAPI == GraphicsAPI.GLES1) texData = AWTTextureIO.newTextureData(GLProfile.getGL2ES1(), bitmap, false);
        else texData = AWTTextureIO.newTextureData(GLProfile.getGL2ES2(), bitmap, false);
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1)  {
            GL.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texData.getWidth(), texData.getHeight(), 
                0,  GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, texData.getBuffer());
        } else {
            GL2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, texData.getWidth(), texData.getHeight(), 
                    0,  GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, texData.getBuffer());
        }
        
        return true;
    }
    

    public static int loadMipMappedTexture( int file) {
        
        InputStream is =  Resource.getResInputStream(file);
        BufferedImage bitmap = loadARGBImage(is);
        
        return loadMipMappedTexture(bitmap);
    }
    
    public static int loadMipMappedTexture( String filePath) {
        
        InputStream is =  Resource.getResInputStream(filePath);
        BufferedImage bitmap = loadARGBImage(is);
        
        return loadMipMappedTexture(bitmap);
    }
    
    public static int loadMipMappedTexture( BufferedImage bitmap) {
        
        
        int textureID = getNewTextureID();
        
        if (Platform.renderingAPI == GraphicsAPI.GLES1) {
            GL.glBindTexture(GL.GL_TEXTURE_2D, textureID);
          
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        } else {
            GL2.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
            
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            GL2.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        }

        
        int level = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
       
        //ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        
        try {
        
        while(height >= 1 || width >= 1) {
          //First of all, generate the texture from our bitmap and set it to the according level
          
          //Horror: temporary save to PNG 
          //(is there a simple way to turn a BufferedImage to a TextureData?)
          //buffer.reset();
          //ImageIO.write(bitmap, "png", buffer);
          
          //ByteArrayInputStream imgIs = new ByteArrayInputStream(buffer.toByteArray());
          
          //More horror: make a texture from this PNG
          //TextureData texData = TextureIO.newTextureData(GLProfile.getGL2ES1(), imgIs, false, "png");
            
          TextureData texData = null;
          if (Platform.renderingAPI == GraphicsAPI.GLES1) {
              texData = AWTTextureIO.newTextureData(GLProfile.getGL2ES1(), bitmap, false);
              GL.glTexImage2D(GL.GL_TEXTURE_2D, level, GL.GL_RGBA, width, height, 
                      0,  GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, texData.getBuffer());
          }
          else {
              texData = AWTTextureIO.newTextureData(GLProfile.getGL2ES2(), bitmap, false);
              GL2.glTexImage2D(GL2.GL_TEXTURE_2D, level, GL2.GL_RGBA, width, height, 
                      0,  GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, texData.getBuffer());
          }
    
          
          
       
          if(height == 1 && width == 1) break;
       
          //Increase the mipmap level
          level++;
       
          height /= 2; if (height == 0) height = 1;
          width /= 2; if (width == 0) width = 1;
          
          BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          Graphics2D g2 = tmp.createGraphics();
          g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
          g2.drawImage(bitmap, 0, 0, width, height, null);
          g2.dispose();
       
          //Clean up
          bitmap = tmp;
        }
        
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
       
        return textureID;
      }
    
    private static BufferedImage loadARGBImage(InputStream is){
        
        BufferedImage bitmap = null;
        try {
            bitmap = ImageIO.read(is);
            
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            
            BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(bitmap, 0, 0, width, height, null);
            g2.dispose();
            
            return tmp;
            
        } catch (Exception e){
            Log.e(TAG, "Could not read texture file " , e);
            return null;
        }
        finally {
            try { is.close();} 
            catch(Exception e) { Log.e(TAG, "Error closing stream", e); }
        }
        
    }

}
