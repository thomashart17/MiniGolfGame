package com.badlogic.gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class EndScreen implements Screen{

    final MiniGolf game;
    private OrthographicCamera camera;
    private Texture titleTexture;
    private Rectangle title;

    public EndScreen(final MiniGolf game) {
        this.game = game;

        titleTexture = new Texture("endTitle.png");

        title = new Rectangle(225,350,350,100);

        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,800);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255,255,255,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(titleTexture, title.x, title.y, title.width, title.height);
        game.batch.end();

        if(Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
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
        titleTexture.dispose();
    }
}
