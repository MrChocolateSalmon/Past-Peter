package com.mrchocolatesalmon.pastpeter.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.enums.BackgroundType;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

import static com.badlogic.gdx.math.MathUtils.floor;

public class GameRenderer {

    GameData gameData;
    SpriteBatch batcher;
    private OrthographicCamera cam;

    BitmapFont font1;

    public GameRenderer(GameData gameData){
        this.gameData = gameData;

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batcher = new SpriteBatch();

        // Attaching the batcher to camera
        setCam(new OrthographicCamera());
        getCam().setToOrtho(true, gameData.CAMWIDTH, gameData.CAMHEIGHT);
        batcher.setProjectionMatrix(getCam().combined);

        font1 = new BitmapFont(true);
    }

    public void renderStart(float delta, BackgroundType bgType, TimeID timeID){
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
    }

    public void drawText(String text, float x, float y){
        font1.draw(batcher, text, x, y);
    }

    public void renderTitle(){
        batcher.draw(AssetLoader.logo, (gameData.GAMEWIDTH * gameData.GAMESIZE - 412)/2, (gameData.GAMEHEIGHT * gameData.GAMESIZE - 72)/2);
    }

    public void renderLevelButtons(Vector2[] levelButtons){
        for (int i = 0; i < levelButtons.length; i++){
            Vector2 tempButton = levelButtons[i];
            batcher.draw(AssetLoader.levelBtn, tempButton.x, tempButton.y, gameData.GAMESIZE, gameData.GAMESIZE);
        }
    }

    public void renderLevelInfo(Level level){

        float midX = (gameData.GAMEWIDTH * gameData.GAMESIZE)/2;
        float midY = (gameData.GAMEHEIGHT * gameData.GAMESIZE)/2;

        float winBorderX = midX - 160;
        float winBorderY = midY - 256;

        batcher.draw(AssetLoader.winBorder, winBorderX, winBorderY, 160 * 2, 256 * 2);


        boolean even = level.players.size()%2==0;
        float startXOffset = -64;
        for (int i = 0; i < level.players.size(); i++){
            TextureRegion playerTex = (TextureRegion)AssetLoader.getPlayerTexture(level.players.get(i).playerIDToString()+"_present_idle").getKeyFrame(0);

            int multiplier = floor((i+1) / 2);

            batcher.draw(playerTex, midX + (startXOffset * (multiplier) * ((i%2 == 0)?-1:1)) - (even?0:32), midY);
        }

        drawText(level.name, winBorderX + 10, winBorderY + 10);
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

    public void renderWinScreen(Level currentLevel){
        renderLevel(currentLevel);
        batcher.draw(AssetLoader.winBorder, 128, 128);
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
