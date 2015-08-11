package com.breakingbyte.wrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class Resource {
    
    private static final String TAG = "Resource";

    public static InputStream getResInputStream(String filePath) {
        
        String hdFilePath = getFullFilePath(filePath);
        
        try {
            FileInputStream is = new FileInputStream(hdFilePath);
            return is;
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file " + hdFilePath);
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getFullFilePath(String localPath) {
        String ROOT_FOLDER = DesktopApplication.ANDROID_ROOT;
        if (!ROOT_FOLDER.endsWith("/")) ROOT_FOLDER += "/";
        ROOT_FOLDER += "assets/";
        
        return ROOT_FOLDER + localPath;
    }
    
    /**
     * Get the raw path of a file within the android project
     * @param id some integer within R.java
     * @return the raw HDD path of the file
     */
    public static String getFilePathFromID(int id) {
        
        String ROOT_FOLDER = DesktopApplication.ANDROID_ROOT;
        if (!ROOT_FOLDER.endsWith("/")) ROOT_FOLDER += "/";
        ROOT_FOLDER += "res/";
        
        try {
            
            String fullPartialPath = ROOT_FOLDER + getPartialFilePath(id);
            
            String rootFolder = discardAfterLast(fullPartialPath, '/');
            String partialFileName = discardBeforeLast(fullPartialPath, '/');
            
            String fullFileName = findFullNameFromPartialName(rootFolder, partialFileName);
            
            return rootFolder + "/" + fullFileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Log.e(TAG, "No resource found for id: " + id + "!");
        return "";

    }

    public static InputStream getResInputStream(int file) {
        // return GameSurfaceView.context.getResources().openRawResource(file);
        String hdFilePath = getFilePathFromID(file);
        try {
            FileInputStream is = new FileInputStream(hdFilePath);
            return is;
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file " + hdFilePath);
            e.printStackTrace();
        }
        return null;
    }

    
    /**
     * Finds a filepath in the res folder given an ID. (File extension missing)
     * Example: for (int)R.drawable.someId we get "drawable/someId"
     * @param toFindId an integer value corresponding to an attribute in R.java
     * @return the relative path in the res folder, without file extension ("drawable/someId")
     * @throws IllegalAccessException 
     * @throws Exception 
     */
    @SuppressWarnings("rawtypes")
    private static String getPartialFilePath(int toFindId) throws Exception {

        //Some Java reflection goodness
        
        //Get package name
        String fullyQualifiedName = Resource.class.getName();
        String packageName = discardAfterLast(fullyQualifiedName, '.');

        //Get full name of R.java
        String androidRClass = packageName + ".R";

        Class c = Class.forName(androidRClass);
        
        //Get all sub-classes ("drawable", "raw", ...)
        Class[] innerClasses = c.getDeclaredClasses();
        for (Class innerClass : innerClasses){
            
            //Get attributes
            Field[] fields = innerClass.getDeclaredFields();
            for (Field field : fields) {

                int value = field.getInt(null);
                
                if (value == toFindId) {
                    //We found it!
                    String filePath = innerClass.getSimpleName()+"/"+field.getName();
                    return filePath;
                }                    
            }
        }            

        throw new Exception("Could not find " + toFindId + " in R.java!");
    }
    
    
    private static String findFullNameFromPartialName(String rootFolder, String partialName) throws Exception {
        
        File folder = new File(rootFolder);
        if (!folder.isDirectory()) {
            Log.e(TAG, rootFolder + " is not a folder!");
            return "";
        }
        
        File[] candidates = folder.listFiles();
        for (File candidate : candidates) {
            String noExtensionName = discardAfterLast(candidate.getName(), '.');
            if (noExtensionName.equals(partialName)) {
                //Match!
                return candidate.getName();
            }
        }
        Log.e(TAG, "No file " + partialName + " found within " + rootFolder);
        return "";
        
    }
    
    // "a.b.c.d" -> "a.b.c"
    public static String discardAfterLast(String s, char delimiter) throws Exception{
        int lastDot = s.lastIndexOf(delimiter);
        if (lastDot==-1){ 
           throw new Exception("No delimiter '" + delimiter +"' in the string \"" + s +"\"!");
        }
        return s.substring (0, lastDot);
    }
    
    // "a.b.c.d" -> "d"
    public static String discardBeforeLast(String s, char delimiter) throws Exception{
        int lastDot = s.lastIndexOf(delimiter);
        if (lastDot==-1){ 
            throw new Exception("No delimiter '" + delimiter +"' in the string \"" + s +"\"!");
        }
        return s.substring (lastDot+1, s.length());
    }

}
