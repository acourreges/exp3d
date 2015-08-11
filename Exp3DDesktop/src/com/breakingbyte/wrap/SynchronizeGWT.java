package com.breakingbyte.wrap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Launch this to synchronize the GWT build process.
 */

public class SynchronizeGWT {

    public static final String packageA = "com";
    public static final String packageB = "breakingbyte";
    public static final String packageC = "game";
    
    public static boolean forceCopy = true;
    
    /**
     * @param args
     */
    public static void main(String[] args) {

        synchronizeSourceFiles();
        
        //com.google.gwt.dev.Compiler.main(new String[] {"Exp3DGWT"});
    }
    
    public static void synchronizeSourceFiles() {
        
        //Game sources
        String sourcePath = DesktopApplication.ANDROID_ROOT + "src/" + packageA + "/" + packageB + "/" + packageC + "/";
        String destinationPath = DesktopApplication.GWT_ROOT + "src/" + packageA + "/" + packageB + "/" + packageC + "/";
        
        File source = new File(sourcePath);
        File destination = new File(destinationPath);
        
        if (forceCopy || getLatestModifiedDate(source) > getLatestModifiedDate(destination)) {
            System.out.println("Copying game sources from " + sourcePath + " to " + destinationPath);
            
            deleteAllRecursively(destination);
            synchronizeFiles(source, destination);
            
            System.out.println("Success. (" + filesCopied + " files copied)");
        }
        
        //Assets
        filesCopied = 0;
        
        sourcePath = DesktopApplication.ANDROID_ROOT + "assets/";
        destinationPath = DesktopApplication.GWT_ROOT + "war/assets";
        
        source = new File(sourcePath);
        destination = new File(destinationPath);
        
        if (forceCopy || getLatestModifiedDate(source) > getLatestModifiedDate(destination)) {
            System.out.println("Copying and converting assets from " + sourcePath + " to " + destinationPath);
            deleteAllRecursively(destination);
            synchronizeFiles(source, destination);
            System.out.println("Success. (" + filesCopied + " copies, " + filesConverted + " conversions)");
        }
        System.out.println("Done.");
    }
    
    public static void deleteAllRecursively(File file) {
        if (!file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File f : children) deleteAllRecursively(f);
        }
        if (!file.delete()) throw new RuntimeException("Could not delete file! " + file.getAbsolutePath());
    }
    
    public static long getLatestModifiedDate(File file) {
        if (!file.exists()) return 0;
        if (file.isDirectory()) {
            
            if (file.getName().equals(".svn")) return 0;
            
            File[] children = file.listFiles();
            long result = 0;
            for (File f : children) {
                long tmpResult = getLatestModifiedDate(f);
                if (tmpResult > result) result = tmpResult;
            }
            return result;
        }
        return file.lastModified();
    }
    
    public static int filesCopied = 0;
    public static int filesConverted = 0;
    
    public static void synchronizeFiles(File source, File destination) {
        
        if (source.isDirectory()) {
            
            if (source.getName().equals(".svn")) return;
            
            if (!destination.exists()) {
                if (!destination.mkdirs()) throw new RuntimeException("Could not create directory! " + destination.getAbsolutePath());
            }
            String[] files = source.list();
            for (String f : files) {
                synchronizeFiles(new File(source, f), new File(destination, f));
            }
        } else {
            //normal file
            
            String name = source.getName();
            if (name.endsWith(".png") || name.endsWith(".PNG")) {
                if (!copyAndPremultiplyAlpha(source, destination))
                    throw new RuntimeException("Could not convert image " + source.getAbsolutePath());
            }
            else 
            {
                if (!copyFile(source, destination)) 
                    throw new RuntimeException("Could not copy file from " + source.getAbsolutePath() + " to " + destination.getAbsolutePath());
            }
        }
    }
    
    public static boolean copyFile(File source, File destination) {
      //normal file
        byte[] buffer = new byte[1024];

        int length;
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination); 
            while ((length = in.read(buffer)) > 0){
               out.write(buffer, 0, length);
            }
            in.close();
            out.close();
            filesCopied++;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } 
        return true;
    }
    

    public static boolean copyAndPremultiplyAlpha(File source, File destination) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(source);
        } catch (IOException e) {
            System.out.println("Error treating file! ("+source.getAbsolutePath()+")");
            return false;
        }
        
        BufferedImage resultImg = new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int color = img.getRGB(x, y);

                int alpha = (color >> 24) & 0xff;
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = color & 0x000000ff;
                
                //POST-PROCESS
                float alphaCoeff = alpha / 255f;
                
                red = (int) (alphaCoeff * red);
                green = (int) (alphaCoeff * green);
                blue = (int) (alphaCoeff * blue);
                
                int result = alpha << 24 | red << 16 | green << 8 | blue;
                
                resultImg.setRGB(x, y, result);
            }
        }
        
        //Save
        try {
            ImageIO.write(resultImg, "png", destination);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        filesConverted++;
        return true;
    }

    
   

}
