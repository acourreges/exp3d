package com.breakingbyte.game.util;

public class PackingTexture {
    
    private int nbTilesX, nbTilesY;
    private int maxId;
    
    private int currentId;
    
    public PackingTexture(int nbTilesX,int  nbTilesY, int maxId) {
        currentId = -1;
        this.nbTilesX = nbTilesX;
        this.nbTilesY = nbTilesY;
        this.maxId = maxId;
    }
    
    public boolean prepareNextTexCoord(float[] coords){
        currentId++;
        if (currentId > maxId) return false;
        
        float tileWidth = 1f /nbTilesX;
        float tileHeight = 1f /nbTilesY;
        
        int row = currentId / nbTilesX;
        int col = currentId - nbTilesX * row;
        
        //bottom left
        coords[0] = (col    ) * tileWidth;
        coords[1] = (row + 1) * tileHeight;
        
        //bottom right
        coords[2] = (col + 1) * tileWidth;
        coords[3] = (row + 1) * tileHeight;
        
        //top left
        coords[4] = (col    ) * tileWidth;
        coords[5] = (row    ) * tileHeight;
        
        //top right
        coords[6] = (col + 1) * tileWidth;
        coords[7] = (row    ) * tileHeight;
        
        return true;
    }
    
    public void setCurrentId(int id) {
        this.currentId = id;
    }
    
    public void resetAnimation() {
        setCurrentId(-1);
    }
    
}
