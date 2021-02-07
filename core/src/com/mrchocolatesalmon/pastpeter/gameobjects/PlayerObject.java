package com.mrchocolatesalmon.pastpeter.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.CommandInfo;
import com.mrchocolatesalmon.pastpeter.datastructures.Interrupt;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.PlayerID;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

import java.util.HashMap;

public class PlayerObject {

    protected PlayerID playerID;

    protected HashMap<TimeID,TimePosition[]> positionArray = new HashMap<TimeID, TimePosition[]>();
    protected HashMap<TimeID, CommandInfo[]> commands = new HashMap<TimeID, CommandInfo[]>();

    public HashMap<TimeID, HashMap<String, String>> textureMap = new HashMap<TimeID, HashMap<String, String>>();

    protected Level level;

    public PlayerObject(Vector2 startPast, Vector2 startPresent, Vector2 startFuture, Vector2 endFuture,
                            PlayerID id, Level level){

        playerID = id;
        this.level = level;

        textureMap.put(TimeID.past, new HashMap<String, String>());
        textureMap.put(TimeID.present, new HashMap<String, String>());
        textureMap.put(TimeID.future, new HashMap<String, String>());

        //Past
        TimePosition[] positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        TimePosition startPosition = new TimePosition((int)startPast.x, (int)startPast.y, 1);
        CommandInfo[] commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandInfo.CommandID.wait, null);
        }
        positionArray.put(TimeID.past, positions);
        commands.put(TimeID.past, commandsArray);

        //Present
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startPresent.x, (int)startPresent.y, 1);
        commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandInfo.CommandID.wait, null);
        }
        positionArray.put(TimeID.present, positions);
        commands.put(TimeID.present, commandsArray);

        //Future
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startFuture.x, (int)startFuture.y, 1);
        commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandInfo.CommandID.wait, null);
        }
        positionArray.put(TimeID.future, positions);
        commands.put(TimeID.future, commandsArray);
    }

    public void setCommand(TimeID timeID, int time, CommandInfo command){
        commands.get(timeID)[time] = command;
    }

    public TimePosition getPosition(TimeID timeID, int time){
        return positionArray.get(timeID)[time];
    }

    public IngameObject getHolding(TimeID timeID, int time){ return getPosition(timeID,time).holding; }

    private void drop(TimeID timeID, int time){
        IngameObject obj = getHolding(timeID, time);

        if (obj != null){
            obj.sendInterrupt(new Interrupt(Interrupt.InterruptID.drop, this), timeID, time);
            positionArray.get(timeID)[time].holding = null;
        }
    }

    public void drop(TimeID timeID, int time, IngameObject dropObj){
        IngameObject obj = getHolding(timeID, time);

        if (obj == dropObj && obj != null){
            obj.sendInterrupt(new Interrupt(Interrupt.InterruptID.drop, this), timeID, time);
            positionArray.get(timeID)[time].holding = null;
        }
    }

    public void hold(IngameObject obj, TimeID timeID, int time){
        drop(timeID, time); //Drop anything the player is already holding
        positionArray.get(timeID)[time].holding = obj; //Carry this new object
    }

    public void timeUpdate(TimeID timeID, int time, TimeID previousTimeID){

        TimePosition[] currentPositions = positionArray.get(timeID);
        TimePosition[] earlierTimePositions = positionArray.get(previousTimeID);

        TimePosition currentPosition = currentPositions[time];

        if (time > 0){
            TimePosition previousPosition = currentPositions[time - 1];
            currentPosition.copyValues(previousPosition);

            CommandInfo command = commands.get(timeID)[time];

            switch(command.commandID){
                case wait:
                    break;
                case move:
                    currentPosition.x = (int)command.pos.x;
                    currentPosition.y = (int)command.pos.y;
                    break;
                case pickup:
                    IngameObject potentialHolding = level.findGameobjectWithParameter("pickup", new Vector2(currentPosition.x, currentPosition.y), timeID, time);
                    if (potentialHolding != null){
                        potentialHolding.sendInterrupt(new Interrupt(Interrupt.InterruptID.pickup, this), timeID, time);
                    }
                    break;
                case drop:
                    drop(timeID, time);
                    break;
            }

        } else if (timeID != TimeID.past){
            TimePosition previousPosition = earlierTimePositions[level.getCurrentTime(previousTimeID) + GameData.FILLERSIZE - 1];
            currentPosition.aliveStatus = previousPosition.aliveStatus;
            currentPosition.holding = previousPosition.holding;

        }
    }

    public void render(SpriteBatch batcher) {

        TimeID timeID = level.getCurrentTimeID();
        TimePosition position = getPosition(timeID, level.getCurrentTime());

        if (!textureMap.get(timeID).containsKey("idle")){ return; }

        String animName = textureMap.get(timeID).get("idle");

        if (animName != null) {
            Animation anim = AssetLoader.getPlayerTexture(animName);

            //Gdx.app.log("PlayerObject", "animName = " + animName);
            //Gdx.app.log("PlayerObject", "anim = " + anim.toString());

            batcher.draw((TextureRegion) anim.getKeyFrame(level.levelAge), position.x * GameData.GAMESIZE, position.y * GameData.GAMESIZE);
        }
    }

    public boolean checkCollision(Vector2 checkPosition, TimeID timeID, int time){

        TimePosition myPos = positionArray.get(timeID)[time];

        //Check for collisions with other characters
        for (int p = 0; p < level.players.size(); p++){
            PlayerObject tempPlayer = level.players.get(p);

            if (tempPlayer != this){
                TimePosition tempPos = tempPlayer.positionArray.get(timeID)[time];

                if (tempPos.x == checkPosition.x && tempPos.y == checkPosition.y) {
                    return true;
                }
            }
        }

        for (int i = 0; i < level.objects.size(); i++){
            IngameObject obj = level.objects.get(i);

            TimePosition objPos = obj.positionArray.get(timeID)[time];

            //If object is at position
            if (objPos.x == checkPosition.x && objPos.y == checkPosition.y){

                int wallType = obj.parameterValue("wall");

                //If object is currently a wall (blocking movement)
                if (wallType != 0 && objPos.aliveStatus >= wallType){
                    return true;
                }
            }
        }

        return false;
    }

}
