package classicMod.library;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.video.*;

import java.io.*;

public class VideoDemo extends ApplicationAdapter {
    private SpriteBatch batch;
    private VideoPlayer videoPlayer;

    @Override
    public void create() {
        batch = new SpriteBatch();

        videoPlayer = VideoPlayerCreator.createVideoPlayer();

        try {
            videoPlayer.play(Gdx.files.local("cutscene/cutscenEnd.mp4"));
        } catch (FileNotFoundException e){
            Gdx.app.error("gdx-video", "OH NOES!");
        }
    }

    @Override
    public void render() {
        videoPlayer.update();

        batch.begin();
        Texture frame = videoPlayer.getTexture();
        if(frame!=null){batch.draw(frame,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());}
        batch.end();
    }

    @Override
    public void dispose(){
        batch.dispose();
    }
}
