package com.breakingbyte.game.render;

import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.level.LevelContent;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.Log;
import com.breakingbyte.wrap.Platform;

public class TextTexture {
    
    private static final String TAG = "TextTexture";
    
    private static final String FONT = "Skir.ttf";
    
    private static StringBuilder stringBuilder = new StringBuilder();
    
    public enum TextId {
        DIGITS,
        PRESS_TO_PLAY,
        BOSS_WARNING,
        LEVEL_TITLE_1,
        LEVEL_TITLE_2,
        SHIP_FOCUSER,
    }
    
    public static Texture generate(TextId id) {
        
        FontTexture image = null;

        
        if (id == TextId.DIGITS) {
            
            
            if (!Platform.canGenerateTextTexture) {
                Texture result = new Texture("img/digits.png");
                result.load();
                return result;
            }
            
            int size = 256;
            float grid = size / 4f;
            float fontSize = (int)(size / 3.f);
            
            float charSize = fontSize / 128f * 78f; //font 128pt -> char width 78px
            float charSize1 = charSize * 0.15f; // For '1'
            
            float padding = (grid - charSize) / 2;
            float padding1 = (grid - charSize1) / 2;
            
            image = new FontTexture(size, size, true);
            image.useFont(FONT, fontSize, 0xffffffff);
            
            for (int i = 0; i < 10; i++) {
                stringBuilder.setLength(0);
                stringBuilder.append(i);
                float offsetX = (i % 4) * grid + ((i == 1)? padding1 : padding);
                float offsetY = (i / 4 + 1) * grid - padding;
                image.drawString(stringBuilder.toString(), offsetX, offsetY);
            }

        }
        
        else if (id == TextId.PRESS_TO_PLAY) {

            int sizeX = 256;
            int sizeY = sizeX / 4;
            float fontSize = (int)(sizeY / 2.f);
            
            image = new FontTexture(sizeX, sizeY, false);
            image.useFont(FONT, fontSize, 0xffffffff);
            
            String text = "Press to play";
            // float width = image.getStringLength(text);
            float shadowOffset = sizeX * 0.008f;
            
            image.enableShadow(0xaa000000, shadowOffset, shadowOffset);
            image.drawString(text, FontTexture.ALIGN_CENTER,  sizeY / 2f + fontSize * 0.22f);
            
            /*
            image.useFont(FONT, fontSize, 0xaa000000);
            image.drawString(text, (sizeX - width) / 2f + shadowOffset,  sizeY / 2f + fontSize * 0.22f + shadowOffset);
            image.useFont(FONT, fontSize, 0xffffffff);
            image.drawString(text, (sizeX - width) / 2f,  sizeY / 2f + fontSize * 0.22f);
            */
        }
        
        else if (id == TextId.BOSS_WARNING) {

            int sizeX = 256;
            int sizeY = sizeX / 4;
            float fontSize = (int)(sizeY * 0.8f);
            
            image = new FontTexture(sizeX, sizeY, false);
            image.useFont(FONT, fontSize, 0xffffffff);
            
            String text = "warning";
            //float width = image.getStringLength(text);
            float shadowOffset = sizeX * 0.008f;
            
            image.enableShadow(0xcc000000, shadowOffset, shadowOffset);
            image.drawString(text, FontTexture.ALIGN_CENTER,  sizeY / 2f + fontSize * 0.22f);

        }
        
        else if (id == TextId.LEVEL_TITLE_1 || id == TextId.LEVEL_TITLE_2) {

            int sizeX = 512;
            int sizeY = sizeX / 4;
            float fontSize = (int)(sizeY * 0.7f);
            
            image = new FontTexture(sizeX, sizeY, false);
            image.useFont(FONT, fontSize, 0xffffffff);
            
            String text = "Untitled";
            if (id == TextId.LEVEL_TITLE_1) text = LevelContent.level1.name;
            else if (id == TextId.LEVEL_TITLE_2) text = LevelContent.level2.name;
            //float width = image.getStringLength(text);
            float shadowOffset = sizeX * 0.008f;
            
            image.enableShadow(0xcc000000, shadowOffset, shadowOffset);
            image.drawString(text, FontTexture.ALIGN_CENTER,  sizeY / 2f + fontSize * 0.22f);

        }
        
        else if (id == TextId.SHIP_FOCUSER) {

            int sizeX = 256;
            int sizeY = sizeX / 4;
            float fontSize = (int)(sizeY * 0.8f);
            
            image = new FontTexture(sizeX, sizeY, false);
            image.useFont(FONT, fontSize, 0xffffffff);
            
            String text = "ship";
            //float width = image.getStringLength(text);
            float shadowOffset = sizeX * 0.008f;
            
            image.enableShadow(0xcc000000, shadowOffset, shadowOffset);
            image.drawString(text, FontTexture.ALIGN_CENTER,  sizeY / 2f + fontSize * 0.22f);

        }
        
        else {
            Log.e(TAG, "Image " + id + " not implemented!");
            return null;
        }

        Texture result = image.generateTexture();

        if (image != null) {
            if (Debug.saveTextTextureToFile) image.saveToFile(""+id);
            image.recycle();
        }
        return result;
    }

}
