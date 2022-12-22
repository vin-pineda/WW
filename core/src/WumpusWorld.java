import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.ArrayList;

public class WumpusWorld {
    private int world[][] = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
    };

    boolean visible[][] = new boolean[10][10];

    private static final int xoffset = 20, yoffset = 500;
    private final int tileWidth;
    private Texture groundTile, spiderTile, pitTile, wumpusTile, goldTile, webTile, windTile, glitterTile,stinkTile,blackTile,emptyGoldTile;
    public static final int GROUND = 0,SPIDER = 1, PIT = 2, WUMPUS = 3, GOLD = 4, WEB = 11, WIND = 12, STINK = 13, GLITTER = 14, EMPTYCHEST = 24;

    public WumpusWorld(){
        groundTile = new Texture("groundTile.png");
        blackTile = new Texture("blackTile.png");
        glitterTile = new Texture("glitterTile.png");
        goldTile = new Texture("goldTile.png");
        spiderTile = new Texture("spiderTile.png");
        wumpusTile = new Texture("wumpusTile.png");
        webTile = new Texture("webTile.png");
        pitTile = new Texture("pitTile.png");
        stinkTile = new Texture("stinkTile.png");
        windTile = new Texture("windTile.png");
        emptyGoldTile = new Texture("emptyChest.png");

        tileWidth = blackTile.getWidth();
    }

    public void makeVisible(Location loc){
        if(isValid(loc)) visible[loc.getRow()][loc.getCol()] = true;
    }

    public Location covertCoordsToRowCol(int x, int y){
        int row; //y
        int col; //x
        //x=30,col =0
        col = (x-xoffset)/50;
        //y=73,row=0
        //y=130,row = 1
        row=(y-50)/50; // not happy with this solution but works

        return new Location(row,col);
    }

    public Point convertRowColToCoords(Location loc){
        int x = (loc.getCol()*50)+xoffset;
        int y =600 - (loc.getRow()*50)-(600-yoffset);
        return new Point(x,y);
    }
    public boolean isValid(Location loc){
        return loc.getRow() >= 0 && loc.getRow()<world.length && loc.getCol() >=0 && loc.getCol() < world[0].length;
    }

    private void addHints(ArrayList<Location> locs, int tileId){
        for(Location loc:locs){
            world[loc.getRow()][loc.getCol()] = tileId;
        }
    }

    public int getTileId(Location loc){
        if (isValid(loc)) {
            return world[loc.getRow()][loc.getCol()];
        }
        return -1; // ifgiven loc is not valid
    }
    public ArrayList<Location> getNeighbors(Location loc){
        Location above = new Location(loc.getRow()-1,loc.getCol());
        Location below = new Location(loc.getRow()+1,loc.getCol());
        Location right = new Location(loc.getRow(),loc.getCol()+1);
        Location left = new Location(loc.getRow(),loc.getCol()-1);

        ArrayList<Location> locs = new ArrayList<>();
        if(isValid(above)) locs.add(above);
        if(isValid(below)) locs.add(below);
        if(isValid(right)) locs.add(right);
        if(isValid(left)) locs.add(left);

        return locs;
    }
    public void placeTile(int tileId, Location loc){

        if(isValid(loc)) {
            world[loc.getRow()][loc.getCol()] = tileId;
            if(tileId == GROUND)
                addHints(getNeighbors(loc),GROUND);
            else
                addHints(getNeighbors(loc),tileId+10);
        }
    }

    public int getNumRows(){
        return world.length;
    }
    public int getNumCols(){
        return world[0].length;
    }

    public void draw(SpriteBatch spriteBatch,boolean showHidden){
        for (int row=0; row<world.length; row++){
            for (int col=0; col < world[row].length; col++){
                if(world[row][col] == GROUND && (visible[row][col] || showHidden))
                    spriteBatch.draw(groundTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == SPIDER && (visible[row][col] || showHidden))
                    spriteBatch.draw(spiderTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == WUMPUS && (visible[row][col] || showHidden))
                    spriteBatch.draw(wumpusTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == PIT && (visible[row][col] || showHidden))
                    spriteBatch.draw(pitTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == WIND && (visible[row][col] || showHidden))
                    spriteBatch.draw(windTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == STINK && (visible[row][col] || showHidden))
                    spriteBatch.draw(stinkTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == GLITTER && (visible[row][col] || showHidden))
                    spriteBatch.draw(glitterTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == GOLD && (visible[row][col] || showHidden))
                    spriteBatch.draw(goldTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == WEB && (visible[row][col] || showHidden))
                    spriteBatch.draw(webTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
                else if(world[row][col] == EMPTYCHEST && (visible[row][col] || showHidden))
                    spriteBatch.draw(emptyGoldTile, xoffset+col*tileWidth, yoffset-row*tileWidth);
            }//end inner for
        }//end outer for
    }//end method draw
    public void reset(){
//        int world[][] = {
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0}
//        };

        for(int i =0; i<world.length; i++){
            for(int j =0; j<world[i].length; j++){
                world[i][j] = 0;
                visible[i][j]=false;
            }
        }
    }

    public void removeGold(Location loc){
        if(isValid(loc) && world[loc.getRow()][loc.getCol()] == GOLD){
            ArrayList<Location> n = getNeighbors(loc);
            world[loc.getRow()][loc.getCol()] =EMPTYCHEST;
            for(Location temp: n){
                world[temp.getRow()][temp.getCol()] = GROUND;
            }
        }
    }
    public Texture getGroundTile() {
        return groundTile;
    }

    public Texture getSpiderTile() {
        return spiderTile;
    }

    public Texture getPitTile() {
        return pitTile;
    }

    public Texture getWumpusTile() {
        return wumpusTile;
    }

    public Texture getGoldTile() {
        return goldTile;
    }
}//end class
