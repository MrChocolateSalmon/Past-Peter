package com.mrchocolatesalmon.pastpeter.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.CommandInfo;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.PlayerID;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;

import java.util.HashMap;

public class PlayerObject {

    protected PlayerID playerID;

    protected HashMap<TimeID,TimePosition[]> positionArray = new HashMap<TimeID, TimePosition[]>();
    protected HashMap<TimeID, CommandInfo[]> commands = new HashMap<TimeID, CommandInfo[]>();

    public HashMap<String, String> textureMap = new HashMap<String, String>();

    public PlayerObject(Vector2 startPast, Vector2 startPresent, Vector2 startFuture, Vector2 endFuture,
                            PlayerID id){

        playerID = id;

        //Past
        TimePosition[] positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        TimePosition startPosition = new TimePosition((int)startPast.x, (int)startPast.y, 1);
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
        }
        positionArray.put(TimeID.past, positions);

        //Present
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startPresent.x, (int)startPresent.y, 1);
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
        }
        positionArray.put(TimeID.present, positions);

        //Future
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startFuture.x, (int)startFuture.y, 1);
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
        }
        positionArray.put(TimeID.future, positions);
    }

    public void DrawPlayer(SpriteBatch batcher, Level currentlevel, float runTime) {


    }

}
