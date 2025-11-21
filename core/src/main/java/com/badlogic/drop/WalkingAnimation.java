package com.badlogic.drop;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WalkingAnimation {
    public TextureRegion[] frames;
    public float frameDuration;
    public float timer = 0f;
    public int index = 0;

    public WalkingAnimation(TextureRegion[] frames, float frameDuration) {
        this.frames = frames;
        this.frameDuration = frameDuration;
    }

    public TextureRegion getFrame(float delta, boolean moving) {
        if (moving) {
            timer += delta;
            if (timer >= frameDuration) {
                timer -= frameDuration;
                index = (index + 1) % frames.length;
            }
        } else {
            // back to stand still
            index = 0;
            timer = 0;
        }
        return frames[index];
    }
}
