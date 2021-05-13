package com.mrchocolatesalmon.pastpeter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

        gameData.renderer.renderStart(0, delta, BackgroundType.sky, displayLevel.getCurrentTimeID());

        gameData.renderer.renderLevel(displayLevel);

        gameData.renderer.renderLevelInfo(displayLevel); //TODO: Debug. Replace with rendering win screen

        gameData.renderer.renderEnd();

        gameData.inputs.resetKeysPressed();
    }

    @Override
    public void resize(int width, int height) {

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
