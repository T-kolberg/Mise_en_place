package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    TextureRegion characterLeft;
    TextureRegion characterRight;
    TextureRegion characterFront;
    TextureRegion characterBack;
    TextureRegion characterOrientation;
    FitViewport viewport;

    float previousPositionX;
    float previousPositionY;


    Sprite characterSprite;

    Texture backgroundTexture;
    Texture kitchenTileLeft;
    Texture kitchenTileRight;
    Texture kitchenTileMiddle;
    Texture characterTexture;

    Rectangle characterBox;
    Array<Rectangle> kitchenBox;
    ShapeRenderer borderBox;

    Vector2 mousePos;
    Vector2 targetPos;
    Vector2 characterPos;

    @Override
    public void create() {
        // Prepare your application here.
        backgroundTexture = new Texture("kitchen.png");
        kitchenTileLeft = new Texture("kitchenTileLeft.png");
        kitchenTileRight = new Texture("kitchenTileRight.png");
        kitchenTileMiddle = new Texture("kitchenTileMiddle.png");
        characterTexture = new Texture("worker.png");

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        // character images
        characterRight = new TextureRegion(characterTexture, 0, 480, 300, 480);
        characterLeft = new TextureRegion(characterTexture, 300, 480, 300, 480);
        characterFront = new TextureRegion(characterTexture, 0, 0, 300, 480);
        characterBack = new TextureRegion(characterTexture, 300, 0, 300, 480);

        // character width and height based on the images
        float regionWidth = characterLeft.getRegionWidth();
        float regionHeight = characterLeft.getRegionHeight();
        float aspectRatio = regionWidth / regionHeight;

        characterSprite = new Sprite(characterTexture);
        characterSprite.setSize(aspectRatio, 1f);
        characterSprite.setPosition(2f, 2f);

        // standard position image of the character
        characterOrientation = characterLeft;

        characterBox = new Rectangle(characterSprite.getX(), characterSprite.getY(), characterSprite.getWidth(), characterSprite.getHeight());
        borderBox = new ShapeRenderer();
        kitchenBox = new Array<>();

        /// kitchen furniture boxes
        // box for kitchenTileLeft
        kitchenBox.add(new Rectangle(0.08f, 1.1f, 1.4f, 1.2f));
        // box for upper kitchen part
        kitchenBox.add(new Rectangle(0f, 3.6f, 8f, 1.0f));
        // box for kitchenTileMiddle
        kitchenBox.add(new Rectangle(3f, 1.8f, 2f, 0.2f));
        // box for kitchenTileRight
        kitchenBox.add(new Rectangle(6.5f, 1.0f, 1.0f, 1f));

        /// wall boxes
        kitchenBox.add(new Rectangle(0.2f, 0f, 0.3f, 1f));
        kitchenBox.add(new Rectangle(0.3f, 3f, 0.3f, 0.5f));
        kitchenBox.add(new Rectangle(7.4f, 3f, 0.3f, 0.5f));
        kitchenBox.add(new Rectangle(7.4f, 0f, 0.3f, 0.5f));

        mousePos = new Vector2();
        targetPos = new Vector2(characterSprite.getX(), characterSprite.getY());
        characterPos = new Vector2();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // Draw your application here.
        input();
        logic();
        draw();
    }

    private void input() {
        // movement speed
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();
        previousPositionX = characterSprite.getX();
        previousPositionY = characterSprite.getY();

        // movement keyboard controls
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
         characterSprite.translateX(speed * delta);
         characterOrientation = characterRight;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterSprite.translateX(-speed * delta);
            characterOrientation = characterLeft;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            characterSprite.translateY(speed * delta);
            characterOrientation = characterBack;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            characterSprite.translateY(-speed * delta);
            characterOrientation = characterFront;
        }

        // mouse controls
        if (Gdx.input.isTouched()) {
            //get mouse position and translate it to the world
            mousePos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(mousePos);

            //save target coordinate
            targetPos.set(mousePos.x, mousePos.y);

            //Character position
            characterPos.set(characterSprite.getX() + (characterSprite.getWidth()/2), characterSprite.getY() + (characterSprite.getHeight()/2));

            Vector2 direction = new Vector2(targetPos).sub(characterPos);

            if (direction.len() > 0) {
                direction.nor(); // normalize direction
                characterSprite.translate(direction.x * speed * delta, direction.y * speed * delta);
            }

            // characterOrientation gets the correct character image according to the movement
            if (direction.x > 0) {
                characterOrientation = characterRight;
            } else if (direction.x < 0) {
                characterOrientation = characterLeft;
            }

            if (direction.y > 0) {
                characterOrientation = characterBack;
            } else if (direction.y < 0) {
                characterOrientation = characterFront;
            }
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float characterWidth = characterSprite.getWidth();
        float characterHeight = characterSprite.getHeight();

        // keep character in frame (frame border)
        characterSprite.setX(MathUtils.clamp(characterSprite.getX(), 0, worldWidth - characterWidth));
        characterSprite.setY(MathUtils.clamp(characterSprite.getY(), 0, worldHeight - characterHeight));
        characterBox.setPosition(characterSprite.getX(), characterSprite.getY());

        // collision boxes for environment
        for (Rectangle boxes : kitchenBox) {
            if (characterBox.overlaps(boxes)) {
                characterSprite.setPosition(previousPositionX, previousPositionY);
                characterBox.setPosition(previousPositionX, previousPositionY);
                break;
            }
        }
    }

    private void draw() {
        // clears the screen with black every frame ?to avoid ghosting?
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // start of the draw loop
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // draw background
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        // draw the kitchenTiles beforehand for a smooth transition
        spriteBatch.draw(kitchenTileLeft, 0.54f, 0.14f, 1.2f, 2.6f);
        spriteBatch.draw(kitchenTileRight, 6.4f, 0.09f, 1.1f, 2.54f);
        spriteBatch.draw(kitchenTileMiddle, 2.85f, 0.95f, 2.33f, 1.45f);

        /// draws furniture tiles on top or underneath the character
        /*
         * draw the kitchen furniture either before or after the character is drawn
         * take half of the furniture height + y-coordinate to determine if the character is above or beyond the furniture
         * and take the x-coordinate so it draws the correct kitchenTile at the correct character position
         */

        // if character is closer to the left kitchenTile
        if (characterSprite.getX() < 2.5f) {
            // z-order for kitchenTileLeft and character (take half of the tiles height and add the y-position)
            if ((characterSprite.getY() >= (2.6f / 2) + 0.14f)) {
                System.out.println("yes");
                spriteBatch.draw(characterOrientation,
                    characterSprite.getX(),
                    characterSprite.getY(),
                    characterSprite.getWidth(),
                    characterSprite.getHeight()
                );
                spriteBatch.draw(kitchenTileLeft, 0.54f, 0.14f, 1.2f, 2.6f);
            } else {
                spriteBatch.draw(kitchenTileLeft, 0.54f, 0.14f, 1.2f, 2.6f);
                spriteBatch.draw(characterOrientation,
                    characterSprite.getX(),
                    characterSprite.getY(),
                    characterSprite.getWidth(),
                    characterSprite.getHeight()
                );
            }
        // character is close to the middle kitchenTile
        } else if (characterSprite.getX() >= 2.5f && characterSprite.getX() <= 5.5f) {
            // z-order for kitchenTileMiddle and character (take half of the tiles height and add the y-position)
            if (characterSprite.getY() >=  ((1.45f / 2) + 0.95f)) {
                spriteBatch.draw(characterOrientation,
                    characterSprite.getX(),
                    characterSprite.getY(),
                    characterSprite.getWidth(),
                    characterSprite.getHeight()
                );
                spriteBatch.draw(kitchenTileMiddle, 2.85f, 0.95f, 2.33f, 1.45f);
            } else {
                spriteBatch.draw(kitchenTileMiddle, 2.85f, 0.95f, 2.33f, 1.45f);
                spriteBatch.draw(characterOrientation,
                    characterSprite.getX(),
                    characterSprite.getY(),
                    characterSprite.getWidth(),
                    characterSprite.getHeight()
                );
            }
        // character is closer to the leftover which is the right kitchenTile
        } else {
            // z-order for kitchenTileRight and character
            if (characterSprite.getY() >=  ((1.45f / 2) + 0.95f)) {
                spriteBatch.draw(characterOrientation,
                    characterSprite.getX(),
                    characterSprite.getY(),
                    characterSprite.getWidth(),
                    characterSprite.getHeight()
                );
                spriteBatch.draw(kitchenTileRight, 6.4f, 0.09f, 1.1f, 2.54f);
            } else {
                spriteBatch.draw(kitchenTileRight, 6.4f, 0.09f, 1.1f, 2.54f);
                spriteBatch.draw(characterOrientation,
                    characterSprite.getX(),
                    characterSprite.getY(),
                    characterSprite.getWidth(),
                    characterSprite.getHeight()
                );
            }
        }

        spriteBatch.end();

        /* Commented out! Is only for debugging or construction purposes!
         * Does produce a red line around the collision boxes
         */
        /*
        borderBox.setProjectionMatrix(viewport.getCamera().combined);
        borderBox.begin(ShapeRenderer.ShapeType.Line);
        // color of the border boxes for debugging construction purposes
        borderBox.setColor(Color.RED);

        // Visible collision box for the character
        borderBox.rect(characterSprite.getX(),
            characterSprite.getY(),
            characterSprite.getWidth(),
            characterSprite.getHeight()
        );

        //Visible collision box for the kitchen furniture
        for (Rectangle box : kitchenBox) {
            borderBox.rect(box.x, box.y, box.width, box.height);
        }
        */

        borderBox.end();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
