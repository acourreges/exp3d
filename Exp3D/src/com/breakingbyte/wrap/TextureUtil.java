package com.breakingbyte.wrap;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


public class TextureUtil {
    
    private static String TAG = "TextureUtil";
    
    public static int loadTexture(int file) {
        GL.glGenTextures(1, singleID, 0);
        loadTexture( singleID[0], file);
        return singleID[0]; 
    }
    
    public static int loadTexture(String filePath) {
        GL.glGenTextures(1, singleID, 0);
        loadTexture( singleID[0], filePath);
        return singleID[0]; 
    }
    
    public static void unloadTexture(int id) {
        singleID[0] = id;
        GL.glDeleteTextures(1, singleID, 0);
    }
    
    /**
     * Loads one texture file
     * @param glId OGL id
     * @param file id of the file in the res folder
     * @return
     */
    public static boolean loadTexture(int glId, int file){
        
        InputStream is =  Resource.getResInputStream(file);
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            //opts.inPreferredConfig = Bitmap.Config.RGB_565;
            //opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (Exception e){
            Log.e(TAG, "Could not read texture file " + file, e);
            return false;
        }
        finally {
            try { is.close();} 
            catch(Exception e) { Log.e(TAG, "Error closing stream", e); }
        }
        
        return loadTexture(glId, bitmap);
    }
    
    public static boolean loadTexture(int glId, String filePath){
        
        InputStream is =  Resource.getResInputStream(filePath);
        if (is == null) return false;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            //opts.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (Exception e){
            Log.e(TAG, "Could not read texture file " + filePath, e);
            return false;
        }
        finally {
            try { is.close();} 
            catch(Exception e) { Log.e(TAG, "Error closing stream", e); }
        }
        
        return loadTexture(glId, bitmap);
    }
    
    public static int loadTexture(Bitmap bitmap) {
        GL.glGenTextures(1, singleID, 0);
        loadTexture( singleID[0], bitmap);
        return singleID[0];
    }
    
    public static boolean loadTexture(int glId, Bitmap bitmap){
        GL.glBindTexture(GL.GL_TEXTURE_2D, glId);
        
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);       
        
        if (bitmap != null) {
            GLUtils.texImage2D(GL.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle(); 
        } else {
            Log.e(TAG, "Bitmap null!");
            return false;
        }
        
        return true;
    }
    
    private static int[] singleID = new int[1];
    public static int loadTiledTexture( int file) {
        GL.glGenTextures(1, singleID, 0);
        
        GL.glBindTexture(GL.GL_TEXTURE_2D, singleID[0]);
        
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        
        InputStream is = Resource.getResInputStream(file);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e){
            Log.e(TAG, "Could not read texture file " + file, e);
            return 0;
        }
        finally {
            try { is.close();} 
            catch(Exception e) { Log.e(TAG, "Error closing stream", e); }
        }
        
        if (bitmap != null) {
            GLUtils.texImage2D(GL.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle(); 
        } else {
            Log.e(TAG, "Could not read bitmap " + file);
            return 0;
        }       
        
        return singleID[0];
    }
    
    public static int loadMipMappedTexture( int file) {
        InputStream is =  Resource.getResInputStream(file);
        Bitmap bitmap = null;
        try {
            //Log.d(TAG, "ETC1 Support: " + ETC1Util.isETC1Supported());
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            //opts.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(is, null, opts);// decodeStream(is, opts);
            return loadMipMappedTexture(bitmap);
        } catch (Exception e){
            Log.e(TAG, "Could not read texture file " + file, e);
            return 0;
        }
        finally {
            try { is.close();} 
            catch(Exception e) { Log.e(TAG, "Error closing stream", e); }
        }
    }
    
    public static int loadMipMappedTexture( String filePath) {
        InputStream is =  Resource.getResInputStream(filePath);
        if (is == null) return 0;
        Bitmap bitmap = null;
        try {
            //Log.d(TAG, "ETC1 Support: " + ETC1Util.isETC1Supported());
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            //opts.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(is, null, opts);// decodeStream(is, opts);
            return loadMipMappedTexture(bitmap);
        } catch (Exception e){
            Log.e(TAG, "Could not read texture file " + filePath, e);
            return 0;
        }
        finally {
            try { is.close();} 
            catch(Exception e) { Log.e(TAG, "Error closing stream", e); }
        }
    }
    
    public static final boolean autoMipMap = true;
    public static int loadMipMappedTexture( Bitmap bitmap) {
        
        GL.glGenTextures(1, singleID, 0);
        
        int textureID = singleID[0];
        GL.glBindTexture(GL.GL_TEXTURE_2D, textureID);
        
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        
        if (autoMipMap) {
            //To use automatic mipmap generation
            GL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
            GLUtils.texImage2D(GL.GL_TEXTURE_2D, 0, bitmap, 0);
            return textureID;
        }
        
        //Original code is buggy on certain devices ("HTC Desire S", "Motorola Milestone (Droid) 3 XT860 Android 2.3.5 Gingerbread")
        //Need conversion as a workaround
        Bitmap oldBitmap = bitmap;
        bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        if (bitmap == null) return 0;
        oldBitmap.recycle();

        int level = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
       
        while(height >= 1 || width >= 1) {
          //First of all, generate the texture from our bitmap and set it to the according level
          GLUtils.texImage2D(GL.GL_TEXTURE_2D, level, bitmap, 0);
       
          if(height == 1 && width == 1) break;
       
          //Increase the mipmap level
          level++;
       
          height /= 2; if (height == 0) height = 1;
          width /= 2; if (width == 0) width = 1;
          Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
       
          //Clean up
          bitmap.recycle();
          bitmap = bitmap2;
        }
       
        return textureID;
      }

}
