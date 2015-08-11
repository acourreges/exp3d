package com.breakingbyte.wrap;

import java.util.ArrayList;

import com.breakingbyte.game.render.Texture;

public class BitmapFont extends Texture {
    
    private static final String TAG = "BitmapFont";
    
    private String fontName;
    
    public ArrayList<float[]> glyphRegion; // left top right bottom


    
    private char charMin;
    @SuppressWarnings("unused")
    private char charMax;
    
    public int pixelWidth = 512, pixelHeight = 512;
    
    public static void main(String[] args) {
        BitmapFont f = new BitmapFont("Skir.ttf");
        //f.generate(256, 256);
        f.generate(512, 512);
        
    }
    
    public BitmapFont(String fontName) {
        super(fontName);
        glyphRegion = new ArrayList<float[]>(40);
        this.fontName = fontName;
    }
    
    @Override
    public void load() {
        if (isLoaded) {
            Log.e(TAG, "Image already loaded, ignoring. " + fontName);
            return;
        }
        
        if (nativeTexture == null) {
            generate(512, 512);
            nativeTexture = new NativeTexture("img/bitmap_font.png");
            nativeTexture.setMipMap(useMipMap);
            nativeTexture.fetch();
        }
        
        if (!nativeTexture.fetched) return;
        
        nativeTexture.uploadToGPU();
        isLoaded = true;
    }
    
    public void generate(int resolutionX, int resolutionY) {
        generate(resolutionX, resolutionY, ' ', '~');
    }
    
    public void generate(int resolutionX, int resolutionY, char charMin, char charMax) {
        
        this.charMin = charMin;
        this.charMax = charMax;
        
        //Generated from a dump of the desktop version data
        glyphRegion.add( new float[] { 0.01233125f, 0.02853125f, 0.06384063f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.06897187f, 0.02853125f, 0.10095f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.10608125f, 0.02853125f, 0.15173125f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.1568625f, 0.02853125f, 0.22009063f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.22522187f, 0.02853125f, 0.30016875f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.3053f, 0.02853125f, 0.36071563f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.36584687f, 0.02853125f, 0.45055938f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.45569062f, 0.02853125f, 0.48766875f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.4928f, 0.02853125f, 0.5364969f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.5416281f, 0.02853125f, 0.585325f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.59045625f, 0.02853125f, 0.65173125f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.6568625f, 0.02853125f, 0.7200906f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.7252219f, 0.02853125f, 0.7572f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.76233125f, 0.02853125f, 0.8294656f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.8345969f, 0.02853125f, 0.866575f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.87170625f, 0.02853125f, 0.92516875f, 0.15709375f} );
        glyphRegion.add( new float[] { 0.01233125f, 0.149625f, 0.08727813f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.09240937f, 0.149625f, 0.1243875f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.12951875f, 0.149625f, 0.20446563f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.20959687f, 0.149625f, 0.28454375f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.289675f, 0.149625f, 0.36462188f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.36975312f, 0.149625f, 0.4447f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.44983125f, 0.149625f, 0.5247781f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.5299094f, 0.149625f, 0.6068094f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.6119406f, 0.149625f, 0.6868875f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.69201875f, 0.149625f, 0.7669656f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.7720969f, 0.149625f, 0.804075f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.80920625f, 0.149625f, 0.8411844f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.8463156f, 0.149625f, 0.90954375f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.914675f, 0.149625f, 0.9779031f, 0.2781875f} );
        glyphRegion.add( new float[] { 0.01233125f, 0.27071875f, 0.07555938f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.08069062f, 0.27071875f, 0.1556375f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.16076875f, 0.27071875f, 0.23180938f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.23694062f, 0.27071875f, 0.32165313f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.32678437f, 0.27071875f, 0.41149688f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.41662812f, 0.27071875f, 0.491575f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.49670625f, 0.27071875f, 0.58141875f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.58655f, 0.27071875f, 0.6712625f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.67639375f, 0.27071875f, 0.76110625f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.7662375f, 0.27071875f, 0.8411844f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.8463156f, 0.27071875f, 0.9310281f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.9361594f, 0.27071875f, 0.9681375f, 0.39928126f} );
        glyphRegion.add( new float[] { 0.01233125f, 0.3918125f, 0.08727813f, 0.520375f} );
        glyphRegion.add( new float[] { 0.09240937f, 0.3918125f, 0.17712188f, 0.520375f} );
        glyphRegion.add( new float[] { 0.18225312f, 0.3918125f, 0.26696563f, 0.520375f} );
        glyphRegion.add( new float[] { 0.27209687f, 0.3918125f, 0.37048125f, 0.520375f} );
        glyphRegion.add( new float[] { 0.3756125f, 0.3918125f, 0.460325f, 0.520375f} );
        glyphRegion.add( new float[] { 0.46545625f, 0.3918125f, 0.5404031f, 0.520375f} );
        glyphRegion.add( new float[] { 0.5455344f, 0.3918125f, 0.6302469f, 0.520375f} );
        glyphRegion.add( new float[] { 0.6353781f, 0.3918125f, 0.710325f, 0.520375f} );
        glyphRegion.add( new float[] { 0.71545625f, 0.3918125f, 0.80016875f, 0.520375f} );
        glyphRegion.add( new float[] { 0.8053f, 0.3918125f, 0.8802469f, 0.520375f} );
        glyphRegion.add( new float[] { 0.8853781f, 0.3918125f, 0.9700906f, 0.520375f} );
        glyphRegion.add( new float[] { 0.01233125f, 0.51290625f, 0.08727813f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.09240937f, 0.51290625f, 0.16540313f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.17053437f, 0.51290625f, 0.25915313f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.26428437f, 0.51290625f, 0.33923125f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.3443625f, 0.51290625f, 0.41735625f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.4224875f, 0.51290625f, 0.49743438f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.5025656f, 0.51290625f, 0.5462625f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.55139375f, 0.51290625f, 0.60485625f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.6099875f, 0.51290625f, 0.6536844f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.6588156f, 0.51290625f, 0.72204375f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.727175f, 0.51290625f, 0.7962625f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.80139375f, 0.51290625f, 0.8333719f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.8385031f, 0.51290625f, 0.90954375f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.914675f, 0.51290625f, 0.9857156f, 0.64146876f} );
        glyphRegion.add( new float[] { 0.01233125f, 0.634f, 0.08337188f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.08850312f, 0.634f, 0.15954375f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.164675f, 0.634f, 0.23571563f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.24084687f, 0.634f, 0.2962625f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.30139375f, 0.634f, 0.37243438f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.37756562f, 0.634f, 0.44860625f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.4537375f, 0.634f, 0.48571563f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.49084687f, 0.634f, 0.522825f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.52795625f, 0.634f, 0.5989969f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.6041281f, 0.634f, 0.63610625f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.6412375f, 0.634f, 0.72204375f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.727175f, 0.634f, 0.7982156f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.8033469f, 0.634f, 0.8743875f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.87951875f, 0.634f, 0.9505594f, 0.7625625f} );
        glyphRegion.add( new float[] { 0.01233125f, 0.75509375f, 0.08337188f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.08850312f, 0.75509375f, 0.15954375f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.164675f, 0.75509375f, 0.23571563f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.24084687f, 0.75509375f, 0.29040313f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.29553437f, 0.75509375f, 0.366575f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.37170625f, 0.75509375f, 0.44079375f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.445925f, 0.75509375f, 0.52673125f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.5318625f, 0.75509375f, 0.6029031f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.6080344f, 0.75509375f, 0.679075f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.68420625f, 0.75509375f, 0.7552469f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.7603781f, 0.75509375f, 0.8118875f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.81701875f, 0.75509375f, 0.8489969f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.8541281f, 0.75509375f, 0.9056375f, 0.88365626f} );
        glyphRegion.add( new float[] { 0.91076875f, 0.75509375f, 0.97204375f, 0.88365626f} );
    }
    
    public float[] getGlyphRegion(char c) {
        return glyphRegion.get(c - charMin);
    }
    


    
    public void saveToFile(String fileName) {
       //NOP
    }
    
}
