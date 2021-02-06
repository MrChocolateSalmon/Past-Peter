package com.mrchocolatesalmon.pastpeter.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.CommandInfo;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.CommandID;
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
            commandsArray[i] = new CommandInfo(CommandID.wait, null);
        }
        positionArray.put(TimeID.past, positions);
        commands.put(TimeID.past, commandsArray);

        //Present
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startPresent.x, (int)startPresent.y, 1);
        commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandID.wait, null);
        }
        positionArray.put(TimeID.present, positions);
        commands.put(TimeID.present, commandsArray);

        //Future
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startFuture.x, (int)startFuture.y, 1);
        commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandID.wait, null);
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
            }
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

}
