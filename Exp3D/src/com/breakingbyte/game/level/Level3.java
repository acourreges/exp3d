package com.breakingbyte.game.level;

import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.entity.Entity;
import com.breakingbyte.game.entity.EntityGroupParticle;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.render.TextureManager;
import com.breakingbyte.game.script.ScriptInterpreter.Script;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.util.Model;
import com.breakingbyte.game.util.QuadVBO;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Light;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class Level3 extends Level {
    
    private static final Model MESH_OBJ_ID = new Model("mdl/ring.obj");
    private static final Texture MESH_TEX_ID = new Texture("img/ring.png").useMipMap(true);

    private static final Texture BG_TEX_ID = new Texture("img/galaxy.png");
    
    private static final float repeatYOffset = 10f * 2f; 
    private float scrollSpeed = 2;
        
    private SmoothJoin translateJoin = new SmoothJoin();
    private SmoothJoin rotateJoin = new SmoothJoin();
    
    private float bgOffset = 0;

    public static final Level3 instance = new Level3();
    
    public Level3() {
        modelsUsed.add(MESH_OBJ_ID);
        texturesUsed.add(MESH_TEX_ID);
        texturesUsed.add(BG_TEX_ID);
    }
    
    @Override
    public Script getScript() {
        return ScriptLevel3.instance;
    }
    
    public SoundId getMusic() {
        return SoundId.BGM_P_P;
    }
    
    private float elapsed = 0;
    
    private static boolean doneOnce = false;
    
    EntityGroupParticle stars1;
    
    @Override
    public void initSetup() {
        
        super.initSetup(); //Load models and set-up lighting
        
        translateJoin.init(0);
        rotateJoin.init(0);
        
        shaker.shakingSpeed = 4f;
        shaker.shakingSpeed = 4f;
        shaker.xAmplitude = 0.15f * 1f; shaker.xOscill = 40f * 0.7f;
        shaker.yAmplitude = 0.15f * 1f; shaker.yOscill = 30f * 0.7f;
        
        ArenaState.instance.backgroundScrollSpeed.init(scrollSpeed);
        ArenaState.instance.backgroundScrollSpeed.setTarget(scrollSpeed, 0f);
        
        if (!doneOnce) {
            doneOnce = true;
            stars1 = new EntityGroupParticle();
            stars1.MAX_CAPACITY = 21;
            stars1.postConstructor();
            
            for (int i = 0; i < stars1.MAX_CAPACITY; i++) {
                Star s = new Star();
                s.setSize(3);
                switch (i) {
                    case 0: s.posX = 20; s.posY = 0; break;
                    case 1: s.posX = 60; s.posY = 10; break;
                    case 2: s.posX = 40; s.posY = 30; break;
                    case 3: s.posX = 90; s.posY = 40; break;
                    case 4: s.posX = 30; s.posY = 55; break;
                    case 5: s.posX = 60; s.posY = 60; break;
                    case 6: s.posX = 10; s.posY = 65; break;
                    case 7: s.posX = 80; s.posY = 75; break;
                    case 8: s.posX = 20; s.posY = 92; break;
                    case 9: s.posX = 40; s.posY = 100; break;
                    case 10: s.posX = 60; s.posY = 110; break;
                    case 11: s.posX = 50; s.posY = 120; break;
                    case 12: s.posX = 10; s.posY = 130; break;
                    case 13: s.posX = 30; s.posY = 140; break;
                    case 14: s.posX = 60; s.posY = 155; break;
                    case 15: s.posX =  0; s.posY = 160; break;
                    case 16: s.posX = 70; s.posY = 170; break;
                    case 17: s.posX = 100; s.posY = 185; break;
                    case 18: s.posX = 110; s.posY = 65; break;
                    case 19: s.posX = -10; s.posY = 40; break;
                    case 20: s.posX = 40; s.posY = 200; break;
                }
                stars1.addMember(s);
            }

        }
    }
    
    @Override
    public void setUpLighting() {
        //Lighting settings
        Light.setAmbient(1f, 1f, 1f, 1f);
        Light.setDiffuse(1f, 1f, 1f, 1f);
        Light.setSpecular(1f, 1f, 1f, 1f);
        Light.setPosition(0.3f, -0.1f, -1f, 0.0f);
        
        Light.apply();      
    }
    
    private float stars1Offset = 0;
    private float stars2Offset = 0;
    private float stars1OffsetRepeat = 210;
    
    @Override
    public void update() {
        
        super.update();
        
        elapsed += Timer.delta;
        
        bgOffset += ArenaState.instance.backgroundScrollSpeed.get() * Timer.delta;
        if (bgOffset > repeatYOffset) bgOffset -= repeatYOffset;
        
        stars1Offset += ArenaState.instance.backgroundScrollSpeed.get() * Timer.delta * 2;
        if (stars1Offset > stars1OffsetRepeat) stars1Offset -= stars1OffsetRepeat;
        
        stars2Offset += ArenaState.instance.backgroundScrollSpeed.get() * Timer.delta * 4;
        if (stars2Offset > stars1OffsetRepeat) stars2Offset -= stars1OffsetRepeat;
        
        if (!Engine.player.getAutoPilot()) {
            translateJoin.setTarget(getLateralRotate()*0.2f, 5);
            rotateJoin.setTarget(getLateralRotate()*0.6f, 5);
        }
        
        translateJoin.update();
        rotateJoin.update();
    }
    
    private static float[] bgVertexData = {
         0.0f,  0.0f,  0f,  //bottom left
         0.5f,  0.0f,  0f,  //bottom right
         0.0f,  0.5f,  0f,  //top left
         0.5f,  0.5f,  0f   //top right
    };
    
    private static float bgTexCoords[] = {
            0.0f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            0.0f, 0.0f,  //top left
            1.0f, 0.0f   //top right
    };
    
    private float bgOffsetHorizontal = 0.5f;
    private float bgOffsetVertical = 0.1f;
    
    public void renderBG() {
        Renderer.loadOrtho();
        Renderer.DepthTest.disable();
        bgOffsetVertical = -0.04f;
        
        bgOffsetHorizontal = -0.05f - rotateJoin.get() * 0.008f ;
        
        bgVertexData[3]  = Screen.ARENA_WIDTH;
        bgVertexData[7]  = Screen.ARENA_HEIGHT;
        bgVertexData[9]  = Screen.ARENA_WIDTH;
        bgVertexData[10] = Screen.ARENA_HEIGHT;
        
        final float imgRatio = 982f/616f; 
        float maxV = Screen.ARENA_HEIGHT / Screen.ARENA_WIDTH / imgRatio;
        bgTexCoords[0] =     bgOffsetHorizontal; bgTexCoords[1] = maxV + bgOffsetVertical;
        bgTexCoords[2] = 1 + bgOffsetHorizontal; bgTexCoords[3] = maxV + bgOffsetVertical;
        bgTexCoords[4] =     bgOffsetHorizontal; bgTexCoords[5] =     bgOffsetVertical;
        bgTexCoords[6] = 1 + bgOffsetHorizontal; bgTexCoords[7] =     bgOffsetVertical;
        
        BG_TEX_ID.bind();
        Renderer.unbindVBOs();
        Renderer.Dithering.enable();
        QuadVBO.drawQuadImmediate(bgVertexData, bgTexCoords);
        Renderer.Dithering.disable();
        //QuadVBO.drawQuad(Screen.ARENA_WIDTH * 0.5f, Screen.ARENA_HEIGHT * 0.5f, Screen.ARENA_WIDTH, Screen.ARENA_HEIGHT); 
    }
    
    public void renderStars() {
        stars1.render();
        Renderer.translate(0, stars1OffsetRepeat, 0);
        stars1.render();
    }
    
    public void render() {
        
        renderBG();
        
        Renderer.Blending.enable();
        Renderer.setColor(1f, 0.7f, 1f, 0.5f);
        //Renderer.setColor(0.3f, 0.7f, 1f, 0.5f);
        //Renderer.setColor(0.7f, 1f, 0.7f, 0.5f);
        TextureManager.star.bind();
        
        //Far stars
        Renderer.pushMatrix();
            Renderer.translate(rotateJoin.get() * 0.008f * Screen.ARENA_WIDTH * 1.1f, -stars1Offset, 0);
            renderStars();
        Renderer.popMatrix();
        
        //Near stars
        Renderer.pushMatrix();
        float scaler = 1.6f;
            Renderer.scale(scaler, scaler, scaler);
            Renderer.translate(-10f + rotateJoin.get() * 0.008f * Screen.ARENA_WIDTH * 1.3f, -stars2Offset, 0);
            renderStars();
        Renderer.popMatrix();
        
        Renderer.resetColor();
        Renderer.Blending.disable();
        
        Renderer.loadPerspective();
        Renderer.DepthTest.enable();
        Renderer.Blending.disable();
        
        shaker.applyTransformation();
        
        Renderer.bindTexture(MESH_TEX_ID);
        float col = 0.8f;
        Renderer.setColor(col -0.1f, col , col + 0.4f , 1f);
        
        Renderer.rotate(18f, 1f, 0, 0);

        Renderer.translate(0, 0, -6);
        
        Renderer.translate(0f, -7 -bgOffset, 0);
        
        Renderer.translate(translateJoin.get() * 0.6f, 0, 0);
        //Renderer.rotate(rotateJoin.get() * 2f, 0, 1, 0);
        
        renderMesh();
        
        Renderer.translate(0f, repeatYOffset, 0);
        
        renderMesh();
    
        //Renderer.clear(true, false);
        
        /*
        shaker.applyTransformation();
        
        Renderer.translate(0f, 0f, -20f);
        Renderer.rotate(-10, 1, 0, 0);
        
        Renderer.translate(0f, -bgOffset, 0);
        
        Renderer.translate(translateJoin.get(), 0, 0);
        Renderer.rotate(rotateJoin.get() * 2f, 0, 1, 0);
        

        Renderer.bindTexture(MESH_TEX_ID);  
        renderMesh();
        */

    }
    
    public void renderMesh() {
        Renderer.pushMatrix();
        
        boolean doColor = true;
        float scale = 1.5f;
        float speed = 0.2f;
        
        Renderer.pushMatrix();
            if (doColor) Renderer.setColor(0.6f, 0.6f, 0.6f, 1);
            Renderer.scale(1f, 0.7f, scale);
            Renderer.rotate(speed * 50*elapsed, 0, 1, 0);
            
            MESH_OBJ_ID.mesh.render();
        Renderer.popMatrix();
        
        Renderer.translate(0f, 5f, 0f);
        Renderer.pushMatrix();
            if (doColor) Renderer.setColor(0.4f, 0.5f, 0.6f, 1);
            Renderer.scale(1f, 0.6f, scale);
            Renderer.rotate(speed * -10*elapsed, 0, 1, 0);
            MESH_OBJ_ID.mesh.render();
            Renderer.translate(0f, 1.1f, 0f);
            //Renderer.rotate(45, 0, 1, 0);
            MESH_OBJ_ID.mesh.render();
        Renderer.popMatrix();
        
        Renderer.translate(0f, 5f, 0f);
        Renderer.pushMatrix();
            if (doColor) Renderer.setColor(0.5f, 0.5f, 0.45f, 1);
            Renderer.scale(1f, 0.7f, scale);
            Renderer.rotate(45 + speed * 40*elapsed, 0, 1, 0);
            MESH_OBJ_ID.mesh.render();
        Renderer.popMatrix();
        
        Renderer.translate(0f, 5f, 0f);
        Renderer.pushMatrix();
            if (doColor) Renderer.setColor(0.6f, 0.7f, 0.8f, 1);
            Renderer.scale(1f, 1f, scale);
            Renderer.rotate(-90f, 0, 1, 0);
            MESH_OBJ_ID.mesh.render();
        Renderer.popMatrix();
        
        Renderer.popMatrix();
    }

    private static class Star extends Entity{
        
        public static float texCoords[] = {
            0.0f, 1.0f,  //bottom left
            1.0f, 1.0f,  //bottom right
            0.0f, 0.0f,  //top left
            1.0f, 0.0f   //top right
        };
        
        public Star() {
            toInitValues();
            clearWhenLeaveScreen = false;
            surviveMoveEnd = true;
        }
        
        public void setSize(float size) {
            width = height = size;
            postInit();
        }
        
        @Override
        public float[] getTextureArray() {
            return texCoords;
        }
        
    }

}
