package com.breakingbyte.game.ui;


import com.breakingbyte.game.engine.Screen;
import com.breakingbyte.game.render.QuadBatch;
import com.breakingbyte.wrap.FontTexture;
import com.breakingbyte.wrap.shared.Renderer;

public class NinePatch extends Widget {
    
    public float scale = 1f;
    
    private class Quad {
        public float left, top, right, bottom;
    }
    
    //Texture coordinates
    private Quad inTexture = new Quad();
    
    //Vertex positions
    public Quad outlineVertices = new Quad();
    public Quad padding = new Quad(); 
    
    
    //Raw data    
    private boolean buffersDirty;
    
    private QuadBatch quadBatch;

    public boolean flipU = false;
    public boolean flipV = false;
    
    public boolean symmetryFromLeft = false;
    public boolean symmetryFromRight = false;
    
    public NinePatch(float left, float top, float right, float bottom) {
        quadBatch = new QuadBatch(9);
        setInTexture(left, top, right, bottom);
    }
    
    public void setInTexture(float left, float top, float right, float bottom) {
        inTexture.left = left;
        inTexture.top = top;
        inTexture.right = right;
        inTexture.bottom = bottom;
        buffersDirty = true;   
    }
    
    public void updateBuffers() {
        quadBatch.updateBuffers();
    }
    
    public void setUp(  float totalWidth, 
                        float totalHeight,
                        float paddingLeft,
                        float paddingTop,
                        float paddingRight,
                        float paddingBottom)
    {
        resize(totalWidth, totalHeight);
        
        padding.left = paddingLeft;
        padding.right = paddingRight;
        padding.top = paddingLeft;
        padding.bottom = paddingBottom;
    }
    
    public void resize(float totalWidth, float totalHeight) {
        outlineVertices.left = -totalWidth * 0.5f;
        outlineVertices.right = totalWidth * 0.5f;
        outlineVertices.top = totalHeight * 0.5f;
        outlineVertices.bottom = -totalHeight * 0.5f;
        
        buffersDirty = true;
    }
    
    public void generateBuffers() {
        
        quadBatch.clearBatch();
        
        // --- First line ---

        for (int line = 0; line < 3; line ++) {
            

            float lineVertTop = 0;
            float lineVertBottom = 0;
            float lineUVTop = 0;
            float lineUVBottom = 0;
            
            if (line == 0) {
                //Top
                lineVertTop = outlineVertices.top;
                lineVertBottom = outlineVertices.top - padding.top;
                lineUVTop = flipV ? 1 : 0;
                lineUVBottom = flipV? 1 - inTexture.top : inTexture.top;
            } 
            else if ( line == 1) {
                //Middle
                lineVertTop = outlineVertices.top - padding.top;
                lineVertBottom = outlineVertices.bottom + padding.bottom;
                lineUVTop = flipV? 1 - inTexture.top : inTexture.top;
                lineUVBottom = flipV? inTexture.bottom : 1 - inTexture.bottom;
            }
            else if ( line == 2) {
                //Bottom
                lineVertTop = outlineVertices.bottom + padding.bottom;
                lineVertBottom =  outlineVertices.bottom;
                lineUVTop = flipV? inTexture.bottom : 1 - inTexture.bottom;
                lineUVBottom = flipV? 0 : 1;
            }
            

            //Left
            float uvLeftBlockLeft = flipU ? 1 : 0;
            float uvRightBlockLeft = flipU ? 1 - inTexture.left : inTexture.left;
            
            //Middle
            float uvLeftBlockMiddle = flipU ? 1 - inTexture.left : inTexture.left;
            float uvRightBlockMiddle = flipU ? inTexture.right : 1 - inTexture.right;
            
            //Right
            float uvLeftBlockRight = flipU ? inTexture.right : 1 - inTexture.right;
            float uvRightBlockRight = flipU ? 0 : 1;
            
            if (symmetryFromLeft) {
                uvLeftBlockRight = uvRightBlockLeft;
                uvRightBlockRight = uvLeftBlockLeft;
            }
            else if (symmetryFromRight) {
                uvLeftBlockLeft = uvRightBlockRight;
                uvRightBlockLeft = uvLeftBlockRight;
            }
            
            //Left
            quadBatch.addQuadWithUV(
                    outlineVertices.left, 
                    outlineVertices.left + padding.left, 
                    lineVertTop, 
                    lineVertBottom, 
                    uvLeftBlockLeft, 
                    uvRightBlockLeft, 
                    lineUVTop, 
                    lineUVBottom);
            
            
            //Middle
            quadBatch.addQuadWithUV(
                    outlineVertices.left + padding.left, 
                    outlineVertices.right - padding.right, 
                    lineVertTop, 
                    lineVertBottom, 
                    uvLeftBlockMiddle, 
                    uvRightBlockMiddle, 
                    lineUVTop, 
                    lineUVBottom);
            
            //Right
            quadBatch.addQuadWithUV(
                    outlineVertices.right - padding.right, 
                    outlineVertices.right, 
                    lineVertTop, 
                    lineVertBottom, 
                    uvLeftBlockRight, 
                    uvRightBlockRight, 
                    lineUVTop, 
                    lineUVBottom);
        }
       
        updateBuffers();
        
        buffersDirty = false;
    }

    public void render() {
        
        super.render();
        
        if (buffersDirty) generateBuffers();
        
        Renderer.pushMatrix();
        
        Renderer.unbindVBOs();
        
        texture.bind();
        
        if (posX == FontTexture.ALIGN_CENTER) {
            posX = Screen.ARENA_WIDTH / 2f;
        }
        
        Renderer.translate(posX, posY, 0);
        
        Renderer.scale(scale, scale, 1f);
        
        applyColor();
        quadBatch.render();
        Renderer.resetColor();
        
        Renderer.popMatrix();
    }

}
