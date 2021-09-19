package com.mrchocolatesalmon.pastpeter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mrchocolatesalmon.pastpeter.enums.BackgroundType;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

public class IntroScreen implements Screen, ScreenMethods {

    Game screenControl;
    GameData gameData;

    public IntroScreen(Game control){
        screenControl = control;
        gameData = new GameData(screenControl);
    }

    @Override
    public void show() {
        gameData.inputs.SetCurrentScreen(this);
        Gdx.app.log("IntroScreen", "SetCurrentScreen");
    }

    @Override
    public void render(float delta) {
        if (gameData.inputs.anykeyDown) {
            screenControl.setScreen(gameData.levelSelectScreen);
        }

        gameData.renderer.renderStart(delta, BackgroundType.sky, TimeID.past);
        gameData.renderer.renderTitle();
        gameData.renderer.renderEnd();

        gameData.inputs.resetKeysPressed();
    }

    @Override
    public void onClick() {
        screenControl.setScreen(gameData.levelSelectScreen);
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
        gameData.dispose();
    }
}
