package com.mrchocolatesalmon.pastpeter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.CommandInfo;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

public class InGameScreen implements Screen, ScreenMethods {
    Game screenControl;
    GameData gameData;

    Level currentLevel;

    boolean playerCanMoveLeft, playerCanMoveRight, playerCanMoveDown, playerCanMoveUp, playerCanInteract, playerCanPickup, playerCanDrop;

    public InGameScreen(GameData gameData, Game screenControl) {
        this.gameData = gameData;
        this.screenControl = screenControl;
    }

    public void setCurrentLevel(Level level, boolean restart) {
        this.currentLevel = level;

        if (restart){ level.resetLevel(); }

        timeUpdateAll(TimeID.past); //Perform the first time update for all objects

        checkPlayerOptions();
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

        PlayerObject activePlayer = currentLevel.getActivePlayer();
        TimePosition playerPosition = activePlayer.getPosition(currentLevel.getCurrentTimeID(), currentLevel.getCurrentTime());

        if (gameData.inputs.keysPressed[Input.Keys.NUM_1]){
            currentLevel.setCurrentTimeID(TimeID.past);
            sceneUpdate();
        } else if (gameData.inputs.keysPressed[Input.Keys.NUM_2]){
            currentLevel.setCurrentTimeID(TimeID.present);
            sceneUpdate();
        } else if (gameData.inputs.keysPressed[Input.Keys.NUM_3]){
            currentLevel.setCurrentTimeID(TimeID.future);
            sceneUpdate();
        } else if (gameData.inputs.keysPressed[Input.Keys.BACKSPACE]){
            decrementTime();
        }

        if (gameData.inputs.keysPressed[Input.Keys.LEFT]){
            if (playerCanMoveLeft){
                CommandInfo moveCommand = new CommandInfo(CommandInfo.CommandID.move, new Vector2(playerPosition.x - 1,playerPosition.y));
                playerTakeAction(moveCommand);
            }
        } else if (gameData.inputs.keysPressed[Input.Keys.RIGHT]){
            if (playerCanMoveRight){
                CommandInfo moveCommand = new CommandInfo(CommandInfo.CommandID.move, new Vector2(playerPosition.x + 1,playerPosition.y));
                playerTakeAction(moveCommand);
            }
        } else if (gameData.inputs.keysPressed[Input.Keys.DOWN]){
            if (playerCanMoveDown){
                CommandInfo moveCommand = new CommandInfo(CommandInfo.CommandID.move, new Vector2(playerPosition.x,playerPosition.y + 1));
                playerTakeAction(moveCommand);
            } else if (playerCanDrop){
                CommandInfo dropCommand = new CommandInfo(CommandInfo.CommandID.drop, new Vector2(playerPosition.x,playerPosition.y ));
                playerTakeAction(dropCommand);
            } else if (playerCanPickup){
                CommandInfo pickupCommand = new CommandInfo(CommandInfo.CommandID.pickup, new Vector2(playerPosition.x,playerPosition.y ));
                playerTakeAction(pickupCommand);
            }
        }

        gameData.inputs.resetKeysPressed();
    }

    private void playerTakeAction(CommandInfo receivedCommand){
        for (int i = 0; i < currentLevel.players.size(); i++){
            PlayerObject player = currentLevel.players.get(i);

            if (i == currentLevel.activePlayerNumber){
                player.setCommand(currentLevel.getCurrentTimeID(), currentLevel.getCurrentTime() + 1, receivedCommand);
                Gdx.app.log("InGameScreen", "Active Player Taking Action: " + receivedCommand.commandID.toString());
            } else {
                CommandInfo waitCommand = new CommandInfo(CommandInfo.CommandID.wait, new Vector2(0,0));
                player.setCommand(currentLevel.getCurrentTimeID(), currentLevel.getCurrentTime() + 1, waitCommand);
            }
        }

        incrementTime();
    }

    private void incrementTime(){
        currentLevel.incrementTime();

        timeUpdateAll(currentLevel.getCurrentTimeID());
    }

    private void decrementTime(){
        currentLevel.decrementTime();

        sceneUpdate();
    }

    //Update all objects and players for the new time element and all future events
    private void timeUpdateAll(TimeID startID){

        TimeID[] updateTimes;

        Gdx.app.log("InGameScreen", "timeUpdateAll() start");

        //Select current time id and all time ids in the future
        if (startID == TimeID.past){
            updateTimes = new TimeID[]{TimeID.past, TimeID.present, TimeID.future};
        } else if (startID == TimeID.present){
            updateTimes = new TimeID[]{TimeID.present, TimeID.future};
        } else {
            updateTimes = new TimeID[]{TimeID.future};
        }

        for (int u = 0; u < updateTimes.length; u++){

            TimeID currentTimeID = updateTimes[u];
            TimeID previousTimeID = (currentTimeID == TimeID.future) ? TimeID.present : TimeID.past;

            //Start from the current point and update forward
            int tempCurrentTime = currentLevel.getCurrentTime(currentTimeID);
            for (int t = (u==0)?tempCurrentTime:0; t < tempCurrentTime + GameData.FILLERSIZE; t++) {

                for (int i = 0; i < currentLevel.players.size(); i++) {
                    currentLevel.players.get(i).timeUpdate(currentTimeID, t, previousTimeID);
                }

                for (int i = 0; i < currentLevel.objects.size(); i++) {
                    currentLevel.objects.get(i).timeUpdate(currentTimeID, t, previousTimeID);

                }
            }
        }

        sceneUpdate(); //Update the scene when finished updating all objects and players

        Gdx.app.log("InGameScreen", "timeUpdateAll() end");
    }

    // Update the scene based on the new positions and states
    private void sceneUpdate(){
        checkPlayerOptions();
        checkPlayerWin();
    }

    private void checkPlayerOptions() {

        TimeID currentTimeID = currentLevel.getCurrentTimeID();
        int currentTime = currentLevel.getCurrentTime();

        PlayerObject activePlayer = currentLevel.getActivePlayer();
        TimePosition playerPosition = activePlayer.getPosition(currentTimeID, currentTime);

        IngameObject currentlyHolding = activePlayer.getHolding(currentTimeID, currentTime);
        IngameObject potentialHolding = currentLevel.findGameobjectWithParameter("pickup", new Vector2(playerPosition.x, playerPosition.y), currentTimeID, currentTime);

        boolean inAir = !activePlayer.checkCollision(new Vector2(playerPosition.x, playerPosition.y + 1), currentTimeID, currentTime);

        playerCanMoveLeft = !inAir && !activePlayer.checkCollision(new Vector2(playerPosition.x - 1, playerPosition.y), currentTimeID, currentTime);
        playerCanMoveRight = !inAir && !activePlayer.checkCollision(new Vector2(playerPosition.x + 1, playerPosition.y), currentTimeID, currentTime);
        playerCanMoveDown = inAir;
        playerCanMoveUp = false;
        playerCanInteract = false;
        playerCanPickup = currentlyHolding == null && potentialHolding != null;
        playerCanDrop = currentlyHolding != null && potentialHolding == null;
    }

    private void checkPlayerWin(){

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
