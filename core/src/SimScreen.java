import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.*;

public class SimScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;

    //Object that allows us to draw all our graphics
    private SpriteBatch spriteBatch;

    //Object that allwos us to draw shapes
    private ShapeRenderer shapeRenderer;

    //Camera to view our virtual world
    private Camera camera;

    //control how the camera views the world
    //zoom in/out? Keep everything scaled?
    private Viewport viewport;

    boolean simOver = false;
    boolean runAI = false;
    WumpusWorld myWorld = new WumpusWorld();
    BitmapFont defaultFont = new BitmapFont();
    int currentlySelectedTile = -1; //no current selection

    Dude dude = new Dude(new Location(9,0), myWorld);
    Texture questionButton = new Texture("question.png");
    Texture trophyButton = new Texture("trophy.png");

    boolean showWorld = true;

    //runs one time, at the very beginning
    //all setup should happen here
    @Override
    public void show() {
        camera = new OrthographicCamera(); //2D camera
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2,0);//move the camera
        camera.update();

        //freeze my view to 800x600, no matter the window size
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        spriteBatch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true); //???, I just know that this was the solution to an annoying problem
    }

    public void clearScreen() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    //this method runs as fast as it can, repeatedly, constantly looped
    @Override
    public void render(float delta) {
        clearScreen();


        //user input
        handleMouseClick();
        handleKeyPresses();
        if(runAI && !simOver){
            dude.step();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //ai or game updates


        //all drawing of shapes MUST be in between begin/end
        shapeRenderer.begin();

        shapeRenderer.end();

        //all drawing of graphics MUST be in between begin/end
        spriteBatch.begin();
        myWorld.draw(spriteBatch,showWorld);
        drawToolBar();
        drawDebug();
        dude.draw(spriteBatch);
        spriteBatch.end();
    }

    public Point convertFromMouseToWorldCoords(int x , int y){
        Point p = new Point();
        p.x = x;

        p.y = 600 - y;

        return p;
    }

    public void handleKeyPresses(){
        if(!simOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)||Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                dude.moveRight();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)||Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                dude.moveLeft();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)||Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                dude.moveUp();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)||Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                dude.moveDown();
            }
            int tileUnderDude = myWorld.getTileId(dude.getLoc());
            if (tileUnderDude == WumpusWorld.WUMPUS || tileUnderDude == WumpusWorld.SPIDER || tileUnderDude == WumpusWorld.PIT) {
                simOver = true;
            }else if(tileUnderDude == WumpusWorld.GOLD){
                myWorld.removeGold(dude.getLoc());
            }
            if(dude.hasGold() && dude.getLoc().equals(new Location(9,0))){
                simOver = true;
            }
        }
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                myWorld.reset();
                dude.reset(new Location(9,0));
                simOver = false;
                runAI = false;
                dude.setTotalSteps();
            }
    }

    public void handleMouseClick(){
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();
            //spider is (650,140) to (700,190)
            if(mouseX>= 650 && mouseX <= 700 && mouseY >= 90 && mouseY <= 140){
                currentlySelectedTile = WumpusWorld.GROUND;
            }
            else if(mouseX>= 650 && mouseX <= 700 && mouseY >= 140 && mouseY <= 190){
                currentlySelectedTile = WumpusWorld.SPIDER;
            }else if(mouseX>= 650 && mouseX <= 700 && mouseY >= 190 && mouseY <= 240){
                currentlySelectedTile = WumpusWorld.PIT;
            }
            else if(mouseX>= 650 && mouseX <= 700 && mouseY >= 240 && mouseY <= 290){
                currentlySelectedTile = WumpusWorld.WUMPUS;
            }
            else if(mouseX>= 650 && mouseX <= 700 && mouseY >= 290 && mouseY <= 340){
                currentlySelectedTile = WumpusWorld.GOLD;
            }
            // question button is (650,175) to (700,225)
            else if(mouseX>= 650 && mouseX <= 700 && mouseY >= 375 && mouseY <= 425){
                showWorld = !showWorld; //flip it

            }// trophy button is (650,175) to (700,225)
            else if(mouseX>= 650 && mouseX <= 700 && mouseY >= 375 && mouseY <= 585){
                runAI = !runAI; //flip it

            }
            //clear out the selection if they didn't click anywhere on the toolbar
            else if(currentlySelectedTile != - 1){
                Location wordLoc = myWorld.covertCoordsToRowCol(mouseX,mouseY);
                myWorld.placeTile(currentlySelectedTile,wordLoc);
                currentlySelectedTile = -1;

            }
        }
    }
    public void drawToolBar(){
        if(simOver){
            defaultFont.draw(spriteBatch,"Sim Over",300,600);
        }
        defaultFont.draw(spriteBatch,"Toolbar",650,550);
        defaultFont.draw(spriteBatch,"Total Steps: " + dude.getTotalSteps(),630,80);
        defaultFont.draw(spriteBatch,"Killed Wumpus: "+ dude.KilledWumpus(),625,60);
        spriteBatch.draw(myWorld.getGroundTile(),650,460);
        spriteBatch.draw(myWorld.getSpiderTile(),650,410);
        spriteBatch.draw(myWorld.getPitTile(),650,360);
        spriteBatch.draw(myWorld.getWumpusTile(),650,310);
        spriteBatch.draw(myWorld.getGoldTile(),650,260);
        spriteBatch.draw(questionButton,650,175);
        spriteBatch.draw(trophyButton,650,115);

        //if there is  a selected tile
        if(currentlySelectedTile != -1){
            Point p = convertFromMouseToWorldCoords(Gdx.input.getX(),Gdx.input.getY());
            p.x -= myWorld.getSpiderTile().getWidth()/2;
            p.y -= myWorld.getSpiderTile().getHeight()/2;
            if(currentlySelectedTile == WumpusWorld.SPIDER){
                spriteBatch.draw(myWorld.getSpiderTile(),p.x, p.y);
            }else if(currentlySelectedTile == WumpusWorld.WUMPUS){
                spriteBatch.draw(myWorld.getWumpusTile(),p.x, p.y);
            }else if(currentlySelectedTile == WumpusWorld.GOLD){
                spriteBatch.draw(myWorld.getGoldTile(),p.x, p.y);
            }else if(currentlySelectedTile == WumpusWorld.PIT){
                spriteBatch.draw(myWorld.getPitTile(),p.x, p.y);
            }else if(currentlySelectedTile == WumpusWorld.GROUND){
                spriteBatch.draw(myWorld.getGroundTile(),p.x, p.y);
            }
        }

    }

    public void drawDebug(){
        //show the (x,y) coordinates of the mouse
//        defaultFont.draw(spriteBatch,"x: "+ Gdx.input.getX(),650,200);
//        defaultFont.draw(spriteBatch,"y: "+ Gdx.input.getY(),650,150);


    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
