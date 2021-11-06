package com.mrchocolatesalmon.pastpeter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.CommandInfo;
import com.mrchocolatesalmon.pastpeter.datastructures.PlayerInterrupt;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

import java.util.HashMap;

public class InGameScreen implements Screen, ScreenMethods {
    Game screenControl;
    GameData gameData;

    Level currentLevel;

    IngameObject usingItem = null; //Whether using an item (or moving)
    boolean playerCanMoveLeft, playerCanMoveRight, playerCanMoveDown, playerCanMoveUp, playerCanInteract, playerCanPickup, playerCanDrop;
    boolean playerUseLeft, playerUseMiddle, playerUseRight;

    public InGameScreen(GameData gameData, Game screenControl) {
        this.gameData = gameData;
        this.screenControl = screenControl;
    }

    public void setCurrentLevel(Level level, boolean restart) {
        this.currentLevel = level;

        if (restart){ level.resetLevel(); }

        timeUpdateAll(TimeID.past); //Perform the first time update for all objects

        usingItem = null;
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

        gameData.renderer.renderStart(delta, currentLevel.GetBackground(), currentLevel.getCurrentTimeID());

        gameData.renderer.renderLevel(currentLevel, delta);

        gameData.renderer.renderEnd();
    }

    private void gameUpdate(){

        PlayerObject activePlayer = currentLevel.getActivePlayer();

        TimeID timeID = currentLevel.getCurrentTimeID();
        int time = currentLevel.getCurrentTime();

        TimePosition playerPosition = activePlayer.getPosition(timeID, time);

        //======================
        //Process player inputs
        //======================
        if (gameData.inputs.keysPressed[Input.Keys.ESCAPE]){
            screenControl.setScreen(gameData.levelSelectScreen);
        }

        if (gameData.inputs.keysPressed[Input.Keys.NUM_1]){
            setCurrentTimeID(TimeID.past);
        } else if (gameData.inputs.keysPressed[Input.Keys.NUM_2]){
            setCurrentTimeID(TimeID.present);
        } else if (gameData.inputs.keysPressed[Input.Keys.NUM_3]){
            setCurrentTimeID(TimeID.future);
        } else if (gameData.inputs.keysPressed[Input.Keys.BACKSPACE]){
            usingItem = null;
            decrementTime();
            renderFlush(currentLevel.getCurrentTimeID(), true); //Snap everything back
        }

        if (playerPosition.aliveStatus > 0){
            if (gameData.inputs.keysPressed[Input.Keys.LEFT]){
                if (usingItem == null){
                    if (playerCanMoveLeft){
                        CommandInfo moveCommand = new CommandInfo(CommandInfo.CommandID.move, new Vector2(-1, 0));
                        playerTakeAction(moveCommand, timeID, time);
                    }
                } else {
                    if (playerUseLeft){
                        CommandInfo useCommand = new CommandInfo(CommandInfo.CommandID.use, new Vector2(-1,0));
                        playerTakeAction(useCommand, timeID, time);
                    }
                }
            } else if (gameData.inputs.keysPressed[Input.Keys.RIGHT]){
                if (usingItem == null) {
                    if (playerCanMoveRight){
                        CommandInfo moveCommand = new CommandInfo(CommandInfo.CommandID.move, new Vector2(1,0));
                        playerTakeAction(moveCommand, timeID, time);
                    }
                } else {
                    if (playerUseRight){
                        CommandInfo useCommand = new CommandInfo(CommandInfo.CommandID.use, new Vector2(1,0));
                        playerTakeAction(useCommand, timeID, time);
                    }
                }
            } else if (gameData.inputs.keysPressed[Input.Keys.UP]){
                if (usingItem == null) {
                    if (playerCanMoveUp){
                        CommandInfo moveCommand = new CommandInfo(CommandInfo.CommandID.move, new Vector2(0,-1));
                        playerTakeAction(moveCommand, timeID, time);
                    }
                }
            } else if (gameData.inputs.keysPressed[Input.Keys.DOWN]){
                if (usingItem == null) {
                    if (playerCanMoveDown){
                        CommandInfo moveCommand = new CommandInfo(CommandInfo.CommandID.move, new Vector2(0,1));
                        playerTakeAction(moveCommand, timeID, time);
                    } else if (playerCanDrop){
                        CommandInfo dropCommand = new CommandInfo(CommandInfo.CommandID.drop, new Vector2(playerPosition.x,playerPosition.y ));
                        playerTakeAction(dropCommand, timeID, time);
                    } else if (playerCanPickup){
                        CommandInfo pickupCommand = new CommandInfo(CommandInfo.CommandID.pickup, new Vector2(playerPosition.x,playerPosition.y ));
                        playerTakeAction(pickupCommand, timeID, time);
                    } else if (playerCanInteract){
                        CommandInfo pickupCommand = new CommandInfo(CommandInfo.CommandID.interact, new Vector2(playerPosition.x,playerPosition.y ));
                        playerTakeAction(pickupCommand, timeID, time);
                    }
                } else {
                    if (playerUseMiddle) {
                        CommandInfo pickupCommand = new CommandInfo(CommandInfo.CommandID.use, new Vector2(playerPosition.x, playerPosition.y));
                        playerTakeAction(pickupCommand, timeID, time);
                    }
                }
            } else if (gameData.inputs.keysPressed[Input.Keys.Q]) {
                if (usingItem == null){
                    usingItem = activePlayer.getHolding(timeID, time);
                } else {
                    usingItem = null;
                }

                checkPlayerOptions();
            }
        }

        gameData.inputs.resetKeysPressed();
    }

    private void playerTakeAction(CommandInfo receivedCommand, TimeID timeID, int time){
        for (int i = 0; i < currentLevel.players.size(); i++){
            PlayerObject player = currentLevel.players.get(i);

            if (i == currentLevel.activePlayerNumber){
                player.setCommand(timeID, time + 1, receivedCommand);
                Gdx.app.log("InGameScreen", "Active Player Taking Action: " + receivedCommand.commandID.toString());
            } else {
                CommandInfo waitCommand = new CommandInfo(CommandInfo.CommandID.wait, new Vector2(0,0));
                player.setCommand(timeID, time + 1, waitCommand);
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

        //sceneUpdate();
        timeUpdateAll(currentLevel.getCurrentTimeID());
    }

    //Update all objects and players for the new time element and all future events
    private void timeUpdateAll(TimeID startID){

        TimeID[] updateTimes;

        int currentTime = currentLevel.getCurrentTime();
        TimeID currentTimeID = currentLevel.getCurrentTimeID();

        Gdx.app.log("InGameScreen", "======================");
        Gdx.app.log("InGameScreen", "timeUpdateAll() start");

        int iterations = 4; //Number of chain actions in a single turn (that can be guarenteed to all be called)

        //Select current time id and all time ids in the future
        if (startID == TimeID.past){
            updateTimes = new TimeID[]{TimeID.past, TimeID.present, TimeID.future};
        } else if (startID == TimeID.present){
            updateTimes = new TimeID[]{TimeID.present, TimeID.future};
        } else {
            updateTimes = new TimeID[]{TimeID.future};
        }

        for (int u = 0; u < updateTimes.length; u++){

            TimeID thisTimeID = updateTimes[u];
            TimeID previousTimeID = (thisTimeID == TimeID.future) ? TimeID.present : TimeID.past;

            //Start from the current point and update forward
            int tempCurrentTime = currentLevel.getCurrentTime(thisTimeID);
            for (int t = (u==0)?tempCurrentTime:0; t < tempCurrentTime + GameData.FILLERSIZE; t++) {

                Gdx.app.log("InGameScreen", "--- " + thisTimeID + ": " + t + " ---");

                boolean isCurrentMoment = (currentTime == t && currentTimeID == thisTimeID);

                //Gdx.app.log("InGameScreen", "currentTimeID = " + currentTimeID);
                //Gdx.app.log("InGameScreen", "currentTime = " + currentTime);

                //Clear interrupts for objects
                for (int i = 0; i < currentLevel.objects.size(); i++) { currentLevel.objects.get(i).clearInterrupts();  }

                //Update players
                for (int i = 0; i < currentLevel.players.size(); i++) {
                    currentLevel.players.get(i).timeUpdate(thisTimeID, t, previousTimeID, isCurrentMoment);
                }

                for (int i = 0; i < currentLevel.players.size(); i++) {
                    PlayerObject player = currentLevel.players.get(i);
                    player.pushAnimationList();
                    player.clearInterrupts();
                }

                //Repeats for a certain number of iterations so all interactions/interrupts can be processed
                for (int it = 1; it <= iterations; it++) {

                    for (int i = 0; i < currentLevel.players.size(); i++) {
                        currentLevel.players.get(i).prepareForInterrupts(thisTimeID, t);
                    }

                    for (int i = 0; i < currentLevel.objects.size(); i++) {
                        currentLevel.objects.get(i).timeUpdate(thisTimeID, t, previousTimeID);
                    }

                    for (int i = 0; i < currentLevel.players.size(); i++) {
                        currentLevel.players.get(i).processInterrupts(thisTimeID, t, it == iterations, isCurrentMoment);
                    }
                }
            }
        }

        sceneUpdate(); //Update the scene when finished updating all objects and players

        checkWin();

        //Gdx.app.log("InGameScreen", "current time for " + startID.toString() + " = " + currentLevel.getCurrentTime(startID));
        Gdx.app.log("InGameScreen", "timeUpdateAll() end");
        Gdx.app.log("InGameScreen", "======================");
    }

    private void checkWin(){

        HashMap<TimeID, Boolean> timeComplete = new HashMap<TimeID, Boolean>();

        timeComplete.put(TimeID.past, true);
        timeComplete.put(TimeID.present, true);
        timeComplete.put(TimeID.future, true);

        boolean allComplete = true;

        for (TimeID timeID : timeComplete.keySet()){

            for (int i = 0; i < currentLevel.players.size(); i++){
                if (currentLevel.players.get(i).isAtTarget(timeID) == false){
                    timeComplete.put(timeID, false);
                }
            }

            Gdx.app.log("InGameScreen",  timeID.toString() + " complete = " + timeComplete.get(timeID));

            if (timeComplete.get(timeID) == false){ allComplete = false; }

        }

        if (allComplete){
            gameData.winScreen.loadLevelStats(currentLevel);
            screenControl.setScreen(gameData.winScreen);
        }
    }

    // Update the scene based on the new positions and states
    private void sceneUpdate(){
        checkPlayerOptions();
        checkPlayerWin();
    }

    private void setCurrentTimeID(TimeID timeID){
        usingItem = null;
        currentLevel.setCurrentTimeID(timeID);
        renderFlush(timeID, false);
        sceneUpdate();
    }

    private void renderFlush(TimeID timeID, boolean fullFlush){
        for (int i = 0; i < currentLevel.players.size(); i++) {
            currentLevel.players.get(i).flushAnimationList(timeID, currentLevel.getCurrentTime(timeID), fullFlush);
        }
    }

    private void checkPlayerOptions() {

        TimeID currentTimeID = currentLevel.getCurrentTimeID();
        int currentTime = currentLevel.getCurrentTime();

        PlayerObject activePlayer = currentLevel.getActivePlayer();
        TimePosition playerPosition = activePlayer.getPosition(currentTimeID, currentTime);

        if (usingItem == null){
            playerUseLeft = playerUseMiddle = playerUseRight = false;

            Vector2 playerPos = new Vector2(playerPosition.x, playerPosition.y);

            //===Moving===
            IngameObject currentlyHolding = activePlayer.getHolding(currentTimeID, currentTime);
            IngameObject potentialHolding = currentLevel.getPickupObject(playerPos, currentTimeID, currentTime);

            IngameObject interactable = currentLevel.getObjectWithValidParameter("interact", playerPos, currentTimeID, currentTime);

            IngameObject ladderHere = currentLevel.getObjectWithParameter("ladder", playerPos, currentTimeID, currentTime);
            IngameObject ladderBelow = currentLevel.getObjectWithParameter("ladder", new Vector2(playerPosition.x, playerPosition.y + 1), currentTimeID, currentTime);

            boolean inAir = !activePlayer.checkCollision(new Vector2(playerPosition.x, playerPosition.y + 1), currentTimeID, currentTime) && ladderBelow == null;

            playerCanMoveLeft = !inAir && !activePlayer.checkCollision(new Vector2(playerPosition.x - 1, playerPosition.y), currentTimeID, currentTime);
            playerCanMoveRight = !inAir && !activePlayer.checkCollision(new Vector2(playerPosition.x + 1, playerPosition.y), currentTimeID, currentTime);
            playerCanMoveDown = inAir || ladderBelow != null;
            playerCanMoveUp = ladderHere != null;
            playerCanInteract = interactable != null;
            playerCanPickup = currentlyHolding == null && potentialHolding != null;
            playerCanDrop = currentlyHolding != null && potentialHolding == null;

            Gdx.app.log("InGameScreen", "playerCanMoveUp = " + playerCanMoveUp);
        } else {
            playerCanMoveLeft = playerCanMoveRight = playerCanMoveDown = playerCanMoveUp = playerCanInteract = playerCanPickup = playerCanDrop = false;

            //===Using an item===
            playerUseLeft = usingItem.canUseHere(new Vector2(playerPosition.x - 1, playerPosition.y), currentTimeID, currentTime);
            playerUseMiddle = usingItem.canUseHere(new Vector2(playerPosition.x, playerPosition.y), currentTimeID, currentTime);
            playerUseRight = usingItem.canUseHere(new Vector2(playerPosition.x + 1, playerPosition.y), currentTimeID, currentTime);
        }
    }

    private void checkPlayerWin(){

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
