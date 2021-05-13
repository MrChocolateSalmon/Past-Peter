package com.mrchocolatesalmon.pastpeter.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.enums.BackgroundType;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

public class GameRenderer {

    GameData gameData;
    SpriteBatch batcher;
    private OrthographicCamera cam;

    public GameRenderer(GameData gameData){
        this.gameData = gameData;

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batcher = new SpriteBatch();

        // Attaching the batcher to camera
        setCam(new OrthographicCamera());
        getCam().setToOrtho(true, gameData.CAMWIDTH, gameData.CAMHEIGHT);
        batcher.setProjectionMatrix(getCam().combined);
    }

    public void renderStart(int screen, float delta, BackgroundType bgType, TimeID timeID){
        batcher.begin();

        TextureRegion bg = AssetLoader.bgPast;

        if (bgType == BackgroundType.sky){
            switch (timeID){
                case present:
                    bg = AssetLoader.bgPresent;
                    break;
                case future:
                    bg = AssetLoader.bgFuture;
                    break;
            }
        } else if (bgType == BackgroundType.wooden){
            switch (timeID){
                case past:
                    bg = AssetLoader.bgPresent;
                    break;
                case present:
                    bg = AssetLoader.bgPresent;
                    break;
                case future:
                    bg = AssetLoader.bgFuture;
                    break;
            }
        }

        //Draw current background
        batcher.draw(bg, 0, 0, gameData.GAMEWIDTH * gameData.GAMESIZE, gameData.GAMEHEIGHT * gameData.GAMESIZE);

        if (screen == 0){
            batcher.draw(AssetLoader.logo, 24, 180, 10 * gameData.GAMESIZE, 2 * gameData.GAMESIZE);
        }
    }

    public void renderLevelButtons(Vector2[] levelButtons){
        for (int i = 0; i < levelButtons.length; i++){
            Vector2 tempButton = levelButtons[i];
            batcher.draw(AssetLoader.levelBtn, tempButton.x, tempButton.y, gameData.GAMESIZE, gameData.GAMESIZE);
        }
    }

    public void renderLevelInfo(Level level){
        //batcher.draw(AssetLoader.peter1, 0, 0);
    }

    public void renderLevel(Level level){
        for (int i = 0; i < level.objects.size(); i++)
        {
            IngameObject obj = level.objects.get(i);

            obj.render(batcher);
        }

        for (int i = 0; i < level.players.size(); i++)
        {
            PlayerObject player = level.players.get(i);

            player.render(batcher);
        }
    }

    public void renderEnd(){ batcher.end(); }

    public void dispose(){
        batcher.dispose();
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public void setCam(OrthographicCamera cam) {
        this.cam = cam;
    }

}
