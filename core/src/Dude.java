import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.Stack;

public class Dude {
    private Location loc;
    private Location startLoc;
    private WumpusWorld myWorld;
    private Texture texture;
    private boolean HasGold = false;
    private int totalSteps = 0;
    private boolean killedWumpus = false;
    private Stack<Location> stack = new Stack<>();
    private boolean visited[][];
    private boolean noway;
    private boolean get_gold;


    public Dude(Location loc, WumpusWorld myWorld){
        //this.loc = loc;
        this.startLoc = loc;
        this.myWorld = myWorld;
        texture = new Texture("guy.png");
        reset(loc);

        /*
        this.loc = new Location(loc.getRow(),loc.getCol());
        myWorld.makeVisible(loc);
        visited = new boolean[10][10];
        visited[9][0]=true;
        stack.push(new Location(loc.getRow(),loc.getCol()));
        System.out.println("Initial Stack: " + stack);
        */
    }

    public boolean KilledWumpus(){
        return killedWumpus;
    }
    public boolean moveRight(){
        if(loc.getCol()+1 < myWorld.getNumCols()) {
            loc.setCol(loc.getCol() + 1);
            myWorld.makeVisible(loc);
            totalSteps ++;
            return true;
        }
        return false;
    }
    public boolean moveLeft() {
        if(loc.getCol()-1 >= 0) {
            loc.setCol(loc.getCol() - 1);
            myWorld.makeVisible(loc);
            totalSteps ++;
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        if(loc.getRow()+1 < myWorld.getNumRows()) {
            loc.setRow(loc.getRow() + 1);
            myWorld.makeVisible(loc);
            totalSteps ++;
            return true;
        }
        return false;
    }

    public boolean moveUp() {
        if(loc.getRow()-1 >= 0) {
            loc.setRow(loc.getRow() - 1);
            myWorld.makeVisible(loc);
            totalSteps ++;
            return true;
        }
        return false;
    }
    public void draw(SpriteBatch spriteBatch){
        Point myPoint = myWorld.convertRowColToCoords(loc);
        spriteBatch.draw(texture,(int)myPoint.getX(),(int)myPoint.getY());
    }
    public Location getLoc(){
        return loc;
    }
    public void reset(Location loc){
        //this.loc = loc;
        this.loc = new Location(loc.getRow(),loc.getCol());
        myWorld.makeVisible(loc);
        visited = new boolean[10][10];
        visited[9][0]=true;
        stack.clear();
        stack.push(new Location(loc.getRow(), loc.getCol()));
        get_gold = false;
    }public void setTotalSteps(){
        totalSteps = 0;
    }
    public int getTotalSteps(){
        return totalSteps;
    }
    public boolean hasGold() {
        return HasGold;
    }

    public void setHasGold(boolean hasGold) {
        HasGold = hasGold;
    }
    public void randomAISolution(){

//        System.out.println("------------");

        boolean isMoved = false;
        int choice = (int)(1+Math.random() * 4);
        if(choice==1){
            isMoved = moveDown();
            //System.out.println("Down " + loc);
        }else if(choice==2){
            isMoved = moveLeft();
            //System.out.println("Left " + loc);
        }else if(choice==3){
            isMoved = moveRight();
            //System.out.println("Right " + loc);
        }else {
            isMoved = moveUp();
            //System.out.println("Up " + loc);
        }
        // push only when it actually moved
        if (isMoved) {
            stack.push(new Location(loc.getRow(),loc.getCol()));
            //System.out.println("pushed : " + loc.toString());
        }

        //System.out.println("Stack: " + stack);
//        System.out.println("Stack size: " + stack.size());
//        System.out.println("Current loc: " + loc.toString()); // + "stack: " + stack.lastElement().toString());
        /*
        if(myWorld.getTileId(loc) == WumpusWorld.WUMPUS||myWorld.getTileId(loc) == WumpusWorld.PIT||myWorld.getTileId(loc) == WumpusWorld.SPIDER){//fix it
            stack.pop();
            loc = stack.lastElement();
        }
        */
        //else if(visited[loc.getRow()][loc.getCol()]&& stack.get(0) != loc){
        if(isMoved && visited[loc.getRow()][loc.getCol()]){
            if (!stack.empty()) {
                Location poped = stack.pop();
                //loc = stack.lastElement();
                loc = new Location(stack.lastElement().getRow(), stack.lastElement().getCol());
                //System.out.println("popped : " + poped.toString());
                //System.out.println("loc changed: " + loc.toString());
            }
            /*
            if(choice==1){
                moveUp();
            }else if(choice==2){
                moveRight();
            }else if(choice==3){
                moveLeft();
            }else {
                moveDown();
            }
            */
            totalSteps--;
        }
//        System.out.println("------------");


    }
    public void foundGlitter(){

    }
    public boolean is_noway(){
        noway = true;//
        for(Location x : myWorld.getNeighbors(loc)){
            if(!visited[x.getRow()][x.getCol()]) {
                noway = false;
                return false;
            }
        }
        //if(noway && !loc.equals(stack.get(0))){
        if(noway){
            if (!stack.empty()) stack.pop();

            // update location
            if (!stack.empty()) {
                loc = new Location(stack.lastElement().getRow(), stack.lastElement().getCol());
            }
            else {
                loc = new Location(startLoc.getRow(),startLoc.getCol());
                stack.push(new Location(loc.getRow(),loc.getCol()));
            }
//            System.out.println("Noway, loc = " + loc.toString());

        }
        return true;
    }
    //this method makes ONE step
    public void step(){
        if(myWorld.getTileId(loc) == WumpusWorld.EMPTYCHEST || get_gold){
            get_gold = true;
            if(stack.size() == 1 && get_gold ){

            }
            else if (!stack.empty()) {
                stack.pop();
                //loc = stack.lastElement();x
                loc = new Location(stack.lastElement().getRow(), stack.lastElement().getCol());
                //randomAISolution();
            }
        }
        else if(is_noway());// if there is no way to go out, get back one.
        else{
            if(myWorld.getTileId(loc) == WumpusWorld.WEB||myWorld.getTileId(loc) == WumpusWorld.WIND||myWorld.getTileId(loc) == WumpusWorld.STINK){

                if (!stack.empty()) {
                    stack.pop();
                    //loc = stack.lastElement();x
                    loc = new Location(stack.lastElement().getRow(), stack.lastElement().getCol());
                    //randomAISolution();
                }
                else {
                    loc = new Location(startLoc.getRow(),startLoc.getCol());;
                    stack.push(new Location(loc.getRow(), loc.getCol()));
                }
//                System.out.println("Stink, loc = " + loc.toString());

            }else {
                randomAISolution();
                //System.out.println(stack.lastElement().toString());
                //if (myWorld.getTileId(loc) == WumpusWorld.GROUND && !visited[loc.getRow()][loc.getCol()]) {
                //    stack.push(new Location(loc.getRow(), loc.getCol()));
                //}
            }
            visited[loc.getRow()][loc.getCol()] = true;
        }
    }
}
