package com.mrchocolatesalmon.pastpeter.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.screens.ScreenMethods;

public class InputHandler implements InputProcessor {

    private GameData game;

    public boolean anykeyDown = false;
    public boolean[] keysDown = new boolean[256];

    public boolean mouseDown = false;

    ScreenMethods currentScreen;

    public InputHandler(GameData game){
        this.game = game;
    }

    public void SetCurrentScreen(ScreenMethods newScreen){
        currentScreen = newScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        keysDown[keycode] = true;
        anykeyDown = true;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keysDown[keycode] = false;

        anykeyDown = false;
        for (int i = 0; i < keysDown.length; i++){if (keysDown[i]){anykeyDown = true; break;}}
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (button == 0) {
            mouseDown = true;
            currentScreen.onClick();
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            mouseDown = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
