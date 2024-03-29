package com.mrchocolatesalmon.pastpeter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.enums.BackgroundType;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

public class LevelSelectScreen implements Screen, ScreenMethods {
    Game screenControl;
    GameData gameData;

    Vector2[] levelButtons;

    Level selectedLevel;

    boolean displayLevelInfo = false;

    int levelButtonsPerRow = 5;

    public LevelSelectScreen(GameData gameData, Game screenControl){
        this.gameData = gameData;
        this.screenControl = screenControl;

        levelButtons = new Vector2[gameData.NumberOfLevels()];

        for (int i = 0; i < gameData.NumberOfLevels(); i++){

            int x = i;
            int y = 0;
            while (x >= levelButtonsPerRow) {
                x -= levelButtonsPerRow;
                y++;
            }

            //TODO: Create level buttons
            levelButtons[i] = new Vector2(gameData.GAMESIZE * (1 + x*2), gameData.GAMESIZE * (1 + y*1.5f));
        }
    }

    @Override
    public void onClick() {

        if (displayLevelInfo) {

        }
        else {

            float mousex = gameData.MouseX();
            float mousey = gameData.MouseY();

            Gdx.app.log("LevelSelect", "pos : " + mousex + "," + mousey);

            for (int i = 0; i < gameData.NumberOfLevels(); i++) {
                Vector2 buttonPos = levelButtons[i];
                if (mousex >= buttonPos.x && mousey >= buttonPos.y && mousex <= buttonPos.x + gameData.GAMESIZE && mousey <= buttonPos.y + gameData.GAMESIZE) {
                    displayLevelInfo = true;
                    selectedLevel = gameData.levels[i];
                }
            }
        }
    }

    void startLevel(Level level) {
        gameData.ingameScreen.setCurrentLevel(level, false);
        screenControl.setScreen(gameData.ingameScreen);
    }

    @Override
    public void show() {
        gameData.inputs.SetCurrentScreen(this);
        Gdx.app.log("LevelSelect", "SetCurrentScreen");

        gameData.screenWidth = Gdx.graphics.getWidth();
        gameData.screenHeight = Gdx.graphics.getHeight();

        Gdx.app.log("LevelSelect", "camRatio : " + gameData.CAMRATIO);
        Gdx.app.log("LevelSelect", "screenRatio : " + (gameData.screenWidth/gameData.screenHeight));
    }

    @Override
    public void render(float delta) {
        gameData.renderer.renderStart(delta, BackgroundType.sky, TimeID.past);

        gameData.renderer.renderLevelButtons(levelButtons);

        if (displayLevelInfo) {
            gameData.renderer.renderLevelInfo(selectedLevel);

            if (gameData.inputs.keysPressed[Input.Keys.ENTER]){
                displayLevelInfo = false;
                startLevel(selectedLevel);
            } else if (gameData.inputs.keysPressed[Input.Keys.ESCAPE] || gameData.inputs.keysPressed[Input.Keys.BACKSPACE]){
                displayLevelInfo = false;
            }
        }

        gameData.renderer.renderEnd();

        gameData.inputs.resetKeysPressed();
    }

    @Override
    public void resize(int width, int height) {
        gameData.screenWidth = width;
        gameData.screenHeight = height;
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

    }
}
