package com.badlogic.gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen implements Screen{

    // Defining game objects
    final MiniGolf game;
    private OrthographicCamera camera;
    private Texture titleTexture;
    private Texture putterTexture, putter2Texture;
    private Rectangle title;
    private Rectangle putter, putter2;

    // Setting up the game screen
    public MenuScreen(final MiniGolf game) {
        this.game = game;

        titleTexture = new Texture(Gdx.files.internal("menuTitle.png"));
        putterTexture = new Texture(Gdx.files.internal("putter.png"));
        putter2Texture = new Texture(Gdx.files.internal("putter2.png"));

        title = new Rectangle(250,350,300,100);
        putter = new Rectangle(550,250,200,300);
        putter2 = new Rectangle(50,250,200,300);

        // Setting up camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,800);
    }

    // Rendering the game
    @Override
    public void render(float delta) {
        // Clearing the screen
        Gdx.gl.glClearColor(255,255,255,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Setting up camera
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Drawing objects to the screen
        game.batch.begin();
        game.batch.draw(titleTexture, title.x, title.y, title.width, title.height);
        game.batch.draw(putterTexture, putter.x, putter.y, putter.width, putter.height);
        game.batch.draw(putter2Texture, putter2.x, putter2.y, putter2.width, putter2.height);
        game.batch.end();

        if(Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    // Window resizing
    @Override
    public void resize(int width, int height) {}

    // Showing window
    @Override
    public void show() {}

    // Hiding window
    @Override
    public void hide() {}

    // Pausing game
    @Override
    public void pause() {}

    // Resuming game
    @Override
    public void resume() {}

    // Disposing game assets
    @Override
    public void dispose() {
        titleTexture.dispose();
        putterTexture.dispose();
        putter2Texture.dispose();
    }
}
