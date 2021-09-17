package com.badlogic.gdx;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class GameScreen implements Screen {
    final MiniGolf game;

    // Defining game objects
    private Array<Rectangle> walls;
    private Array<Rectangle> grassArray;
    private OrthographicCamera camera;
    private Rectangle ball;
    private Rectangle hole;
    private Texture ballTexture;
    private Texture grassTexture;
    private Texture wallTexture;
    private Texture holeTexture;
    private double velocity, xVelocity, yVelocity, xRatio, yRatio;
    private double friction;
    private int power;
    private int shots;
    private int par;
    private int holeNum;
    private boolean holeStatus;

    public GameScreen(final MiniGolf game) {
        this.game = game;

        // Creating textures
        grassTexture = new Texture(Gdx.files.internal("grass.jpg"));
        ballTexture = new Texture(Gdx.files.internal("ball.png"));
        wallTexture = new Texture(Gdx.files.internal("wall.jpg"));
        holeTexture = new Texture(Gdx.files.internal("hole.png"));

        // Setting up camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,800);

        // Creating ball
        ball = new Rectangle();
        ball.width = 16;
        ball.height = 16;
        velocity = 0;
        xVelocity = 0;
        yVelocity = 0;

        hole = new Rectangle();
        hole.width = 24;
        hole.height = 24;

        friction = 25;
        power = 50;
        game.score = 0;
        holeNum = 1;

        hole1();

        holeStatus = false;
    }

    @Override
    public void render(float delta) {
        // Clearing the screen
        Gdx.gl.glClearColor(255,255,255,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Drawing game objects
        game.batch.begin();
        for (Rectangle grass : grassArray) {
            game.batch.draw(grassTexture, grass.x, grass.y, grass.width, grass.height);
        }
        for (Rectangle wall : walls) {
            game.batch.draw(wallTexture, wall.x, wall.y, wall.width, wall.height);
        }
        game.batch.draw(holeTexture, hole.x, hole.y, hole.width, hole.height);
        if (!holeStatus) {
            game.batch.draw(ballTexture, ball.x, ball.y, ball.width, ball.height);
        }
        game.font.draw(game.batch,"Hole " + holeNum + "   Par " + par,15,785);
        game.font.draw(game.batch,"Shot Power: " + power,15,765);
        game.font.draw(game.batch,"Strokes: " + shots,15,745);
        if (game.score > 0) {
            game.font.draw(game.batch,"Score: +" + game.score,15,725);
        } else if (game.score < 0){
            game.font.draw(game.batch,"Score: " + game.score,15,725);
        } else if (game.score == 0) {
            game.font.draw(game.batch,"Score: E",15,725);
        }
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && power < 100) power++;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && (power > 0)) power--;

        if (Gdx.input.isTouched() && velocity == 0 && !holeStatus) {
            shots++;
            double xDif = abs(Gdx.input.getX() - ball.x - 8);
            double yDif = abs(800 - Gdx.input.getY() - ball.y - 8);
            double hypLength = sqrt((xDif * xDif) + (yDif * yDif));
            velocity = power * 2;
            xRatio = xDif / hypLength;
            yRatio = yDif / hypLength;
            xVelocity = velocity * xRatio;
            yVelocity = velocity * yRatio;
            if (Gdx.input.getX() < ball.x) {
                xVelocity = -xVelocity;
            }
            if (800 - Gdx.input.getY() < ball.y) {
                yVelocity = -yVelocity;
            }
        }

        ball.x += xVelocity * Gdx.graphics.getDeltaTime();
        ball.y += yVelocity * Gdx.graphics.getDeltaTime();

        if (velocity > 0) {
            velocity -= friction * Gdx.graphics.getDeltaTime();
        } else {
            velocity = 0;
        }
        if (xVelocity < 0) {
            xVelocity = -velocity * xRatio;
        } else {
            xVelocity = velocity * xRatio;
        }

        if (yVelocity < 0) {
            yVelocity = -velocity * yRatio;
        } else {
            yVelocity = velocity * yRatio;
        }

        for (Rectangle wall : walls) {
            if (ball.overlaps(wall)) {

                if ((abs(wall.x - ball.x) <= 16) && (wall.height > 10)) {
                    ball.x -= xVelocity * Gdx.graphics.getDeltaTime();
                    xVelocity = -xVelocity;
                } else if ((abs(wall.y - ball.y) <= 16) && (wall.width > 10)) {
                    ball.x -= yVelocity * Gdx.graphics.getDeltaTime();
                    yVelocity = -yVelocity;
                } else if (wall.height == 10 && wall.width == 10) {
                    if (abs(wall.x - ball.x) < abs(wall.y - ball.y)) {
                        ball.y -= yVelocity * Gdx.graphics.getDeltaTime();
                        yVelocity = - yVelocity;
                    } else if (abs(wall.x - ball.x) > abs(wall.y - ball.y)) {
                        ball.x -= xVelocity * Gdx.graphics.getDeltaTime();
                        xVelocity = -xVelocity;
                    } else {
                        ball.y -= yVelocity * Gdx.graphics.getDeltaTime();
                        yVelocity = - yVelocity;
                        ball.x -= xVelocity * Gdx.graphics.getDeltaTime();
                        xVelocity = -xVelocity;
                    }
                }
                break;
            }
        }

        if ((abs(ball.x - 8 - hole.x) < 12) && (abs(ball.y - 8 - hole.y) < 12)){
            velocity = 0;
            holeStatus = true;
            nextHole();
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        ballTexture.dispose();
        grassTexture.dispose();
        wallTexture.dispose();
        ballTexture.dispose();
    }

    // Method to create grass areas
    private void createGrass(int x1, int y1, int x2, int y2) {
        Rectangle grass = new Rectangle();
        grass.x = x1;
        grass.y = y1;
        grass.width = x2 - x1;
        grass.height = y2 - y1;
        grassArray.add(grass);
    }

    // Method to create walls
    private void createWall(int x1, int y1, int x2, int y2) {
        Rectangle wall = new Rectangle();
        wall.x = x1;
        wall.y = y1;
        wall.width = x2 - x1;
        wall.height = y2 - y1;
        walls.add(wall);
    }

    private void nextHole() {
        game.score += shots - par;
        if (holeNum == 1) {
            hole2();
        } else if (holeNum == 2) {
            hole3();
        } else if (holeNum == 3) {
            endGame();
        }
        holeNum++;
        holeStatus = false;
    }

    private void hole1() {
        shots = 0;

        par = 2;

        // Creating grass
        grassArray = new Array<Rectangle>();
        createGrass(300,150,400,450);
        createGrass(400,350,500,650);

        walls = new Array<Rectangle>();
        createWall(391,449,401,459);
        createWall(399,341,409,351);
        createWall(291,140,301,460);
        createWall(399,140,409,350);
        createWall(391,450,401,660);
        createWall(499,340,509,660);
        createWall(290,141,410,151);
        createWall(290,449,400,459);
        createWall(400,341,510,351);
        createWall(390,649,510,659);

        hole.x = 438;
        hole.y = 588;

        ball.x = 342;
        ball.y = 192;
    }

    private void hole2() {
        shots = 0;

        par = 3;

        grassArray = new Array<Rectangle>();
        createGrass(200,150,300,650);
        createGrass(300,550,500,650);
        createGrass(500,150,600,650);

        walls = new Array<Rectangle>();
        createWall(299,541,309,551);
        createWall(491,541,501,551);
        createWall(191,140,201,660);
        createWall(299,140,309,550);
        createWall(491,140,501,550);
        createWall(599,140,609,660);
        createWall(190,141,310,151);
        createWall(190,649,610,659);
        createWall(300,541,500,551);
        createWall(490,141,610,151);

        hole.x = 538;
        hole.y = 188;

        ball.x = 242;
        ball.y = 192;
    }

    private void hole3() {
        shots = 0;

        par = 4;

        grassArray = new Array<Rectangle>();
        createGrass(150,150,250,650);
        createGrass(250,550,550,650);
        createGrass(550,150,650,650);
        createGrass(450,150,550,250);
        createGrass(350,150,450,450);

        walls = new Array<Rectangle>();
        createWall(249,541,259,551);
        createWall(541,541,551,551);
        createWall(449,249,459,259);
        createWall(541,249,551,259);
        createWall(141,140,151,660);
        createWall(249,140,259,550);
        createWall(649,140,659,660);
        createWall(541,250,551,550);
        createWall(449,250,459,460);
        createWall(341,140,351,460);
        createWall(140,141,260,151);
        createWall(140,649,660,659);
        createWall(250,541,550,551);
        createWall(340,141,660,151);
        createWall(450,249,550,259);
        createWall(340,449,460,459);

        hole.x = 388;
        hole.y = 388;

        ball.x = 192;
        ball.y = 192;
    }

    private void endGame() {
        game.setScreen(new EndScreen(game));
    }
}
