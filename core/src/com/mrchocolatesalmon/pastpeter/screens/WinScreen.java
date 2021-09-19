package com.mrchocolatesalmon.pastpeter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.mrchocolatesalmon.pastpeter.enums.BackgroundType;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

public class WinScreen implements Screen, ScreenMethods {
    Game screenControl;
    GameData gameData;

    Level displayLevel;

    public WinScreen(GameData gameData, Game screenControl) {
        this.gameData = gameData;
        this.screenControl = screenControl;
    }

    @Override
    public void show() {
        gameData.inputs.SetCurrentScreen(this);
        Gdx.app.log("LevelEditor", "SetCurrentScreen");
    }

    public void loadLevelStats(Level level){
        displayLevel = level;
    }

    @Override
    public void render(float delta) {

        gameData.renderer.renderStart(delta, BackgroundType.sky, displayLevel.getCurrentTimeID());

        gameData.renderer.renderWinScreen(displayLevel);

        gameData.renderer.renderEnd();

        if (gameData.inputs.keysPressed[Input.Keys.BACKSPACE]) {
            gameData.ingameScreen.currentLevel.resetLevel();
            screenControl.setScreen(gameData.ingameScreen);
        } else if (gameData.inputs.keysPressed[Input.Keys.ENTER]) {
            gameData.ingameScreen.currentLevel.resetLevel();
            screenControl.setScreen(gameData.levelSelectScreen);
        }

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

    @Override
    public void onClick() {

    }
}
