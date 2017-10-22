package com.loop.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private final int WIDTH_VIRTUAL_VIEWPORT = 768;
    private final int HEIGHT_VIRTUAL_VIEWPORT = 1024;

    SpriteBatch batch;
	Texture imgBird[];
    Texture background;
    Texture imgHighTube;
    Texture imgLowTube;
    Texture imgGameOver;

    int screenWidth;
    int screenHeight;

    int initialBirdPositionY;
    int initialBirdPositionX;

    int currentBirdPositionY;
    int currentBirdPositionX;

    int currentHighTubePositionY;
    int currentLowTubePositionY;

    int initialTubesPositionX;
    int currentTubesPositionX;

	int cont = 0;

    int variationHeightTubes = 0;
    int fallVelocity = 0;
    int stateGame = 0;
    int score = 0;
    int level = 180;

    float variantSpriteBird = 0;
    float movementSpeedGame = 0;

    boolean scored = false;

    Random randomValue;

    BitmapFont fontScore;
    BitmapFont textGameOver;

    GlyphLayout layoutGameOver;

    Rectangle rectangleHighTube;
    Rectangle rectangleLowTube;

    Circle circleBird;

    ShapeRenderer shapeRenderer;

    OrthographicCamera camera;
    Viewport viewport;

    @Override
	public void create () {

		Gdx.app.log("create","initialized");

        randomValue = new Random();

        fontScore = new BitmapFont();
        layoutGameOver = new GlyphLayout();

        textGameOver = new BitmapFont();

        batch = new SpriteBatch();

        background = new Texture("fundo.png");

        imgBird = new Texture[3];

        imgHighTube = new Texture("cano_topo_maior.png");
        imgLowTube = new Texture("cano_baixo_maior.png");

        imgBird[0] = new Texture("passaro1.png");
        imgBird[1] = new Texture("passaro2.png");
        imgBird[2] = new Texture("passaro3.png");

        imgGameOver = new Texture("game_over.png");

        shapeRenderer = new ShapeRenderer();

        rectangleHighTube = new Rectangle();
        rectangleLowTube = new Rectangle();

        circleBird = new Circle();

        camera = new OrthographicCamera();
        camera.position.set(WIDTH_VIRTUAL_VIEWPORT/2, HEIGHT_VIRTUAL_VIEWPORT/2, 0);
        viewport = new StretchViewport(WIDTH_VIRTUAL_VIEWPORT, HEIGHT_VIRTUAL_VIEWPORT, camera);

        textGameOver.setColor(Color.WHITE);
        textGameOver.getData().setScale(3);

        layoutGameOver.setText(textGameOver,"Touch to restart!");

        fontScore.setColor(Color.WHITE);
        fontScore.getData().scale(6);


        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        initialBirdPositionY = screenHeight/2;
        initialBirdPositionX = 100;

        currentBirdPositionY = initialBirdPositionY;
        currentBirdPositionX = initialBirdPositionX;

        initialTubesPositionX = screenWidth;
	}

	@Override
	public void render () {
		Gdx.app.log("render","Rendering: " + cont);

        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cont++;

        if((Intersector.overlaps(circleBird,rectangleHighTube) ||
                Intersector.overlaps(circleBird,rectangleLowTube) || currentBirdPositionY < 0) && stateGame == 1) {
            scored = true;
            stateGame = 2;
        }

        if(stateGame == 0){
            variantSpriteBird = 0;
            movementSpeedGame = 0;
            fallVelocity = 0;
            variationHeightTubes = 0;
            currentBirdPositionY = initialBirdPositionY;
            currentTubesPositionX = initialTubesPositionX;
            score = 0;
            level = 180;
            scored = false;

            if(Gdx.input.justTouched()) {
                fallVelocity = -20;
                stateGame = 1;
            }
        }

        if(stateGame == 1) {
            movementSpeedGame += Gdx.graphics.getDeltaTime() * level;
            fallVelocity++;

            variantSpriteBird += Gdx.graphics.getDeltaTime() * 10;

            currentTubesPositionX = screenWidth - (int)movementSpeedGame;

            currentHighTubePositionY = screenHeight/2 + 200 + variationHeightTubes;
            currentLowTubePositionY = screenHeight/2 - imgLowTube.getHeight() - 200 + variationHeightTubes;

            if(currentTubesPositionX < initialBirdPositionX - imgHighTube.getWidth() && scored == false){
                score++;
                scored = true;
                level = 180 + (10 * score);
            }

            if(movementSpeedGame > screenWidth + imgHighTube.getWidth()) {
                scored = false;
                movementSpeedGame = 0;
                variationHeightTubes = randomValue.nextInt(400) - 200;
            }

            if(variantSpriteBird > 2) variantSpriteBird = 0;

            if(Gdx.input.justTouched() && currentBirdPositionY < screenHeight) {
                fallVelocity = -20;
                stateGame = 1;
            }
            if(currentBirdPositionY > 0 || fallVelocity < 0)
                currentBirdPositionY -= fallVelocity;
        }

        if(Gdx.input.justTouched() && stateGame == 2)
            stateGame = 0;

        batch.begin();

        batch.draw(background,0,0,screenWidth, screenHeight);

        batch.draw(imgHighTube, currentTubesPositionX, currentHighTubePositionY);
        batch.draw(imgLowTube, currentTubesPositionX, currentLowTubePositionY);

        batch.draw(imgBird[(int) variantSpriteBird], initialBirdPositionX, currentBirdPositionY);

        fontScore.draw(batch,String.valueOf(score),screenWidth/2, screenHeight - 50);

        if(stateGame == 2) {
            batch.draw(imgGameOver, screenWidth / 2 - imgGameOver.getWidth()/2, screenHeight / 2 - imgGameOver.getHeight()/2);
            textGameOver.draw(batch,"Touch to restart!",screenWidth / 2 - layoutGameOver.width/2 , screenHeight / 2 - imgGameOver.getHeight());
        }

        batch.end();

        circleBird.set(initialBirdPositionX + 33, currentBirdPositionY + 18,32);
        rectangleHighTube.set(currentTubesPositionX, currentHighTubePositionY, imgHighTube.getWidth(), imgHighTube.getHeight());
        rectangleLowTube.set(currentTubesPositionX, currentLowTubePositionY, imgLowTube.getWidth(), imgLowTube.getHeight());


        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.circle(circleBird.x, circleBird.y, circleBird.radius);

        shapeRenderer.rect(rectangleHighTube.x, rectangleHighTube.y, rectangleHighTube.getWidth(), rectangleHighTube.getHeight());
        shapeRenderer.rect(rectangleLowTube.x, rectangleLowTube.y, rectangleLowTube.getWidth(), rectangleLowTube.getHeight());

        shapeRenderer.end();*/
	}

	@Override
	public void dispose (){
		batch.dispose();
		imgBird[(int)variantSpriteBird].dispose();
	}
}