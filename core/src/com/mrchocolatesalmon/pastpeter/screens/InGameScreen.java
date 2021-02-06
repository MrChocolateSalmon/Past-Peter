package com.mrchocolatesalmon.pastpeter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

public class InGameScreen implements Screen, ScreenMethods {
    Game screenControl;
    GameData gameData;

    Level currentLevel;

    public InGameScreen(GameData gameData, Game screenControl) {
        this.gameData = gameData;
        this.screenControl = screenControl;
    }

    public void setCurrentLevel(Level level) {
        this.currentLevel = level;


    }

    @Override
    public void show() {
        gameData.inputs.SetCurrentScreen(this);
        Gdx.app.log("InGameScreen", "SetCurrentScreen");
    }

    @Override
    public void render(float delta) {
        currentLevel.levelAge += delta;

        gameUpdate();

        gameData.renderer.renderStart(1, delta, AssetLoader.bgPast);

        gameData.renderer.renderLevel(currentLevel);

        gameData.renderer.renderEnd();
    }

    private void gameUpdate(){

        if (gameData.inputs.keysPressed[Input.Keys.NUM_1]){
            currentLevel.setCurrentTimeID(TimeID.past);
        } else if (gameData.inputs.keysPressed[Input.Keys.NUM_2]){
            currentLevel.setCurrentTimeID(TimeID.present);
        } else if (gameData.inputs.keysPressed[Input.Keys.NUM_3]){
            currentLevel.setCurrentTimeID(TimeID.future);
        }

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
