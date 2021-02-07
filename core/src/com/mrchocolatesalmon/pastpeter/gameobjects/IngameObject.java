package com.mrchocolatesalmon.pastpeter.gameobjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.Interrupt;
import com.mrchocolatesalmon.pastpeter.datastructures.ObjectDef;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

import java.util.HashMap;

public class IngameObject {

    protected HashMap<TimeID,TimePosition[]> positionArray = new HashMap<TimeID, TimePosition[]>();
    protected HashMap<TimeID, Interrupt[]> interrupts = new HashMap<TimeID, Interrupt[]>();

    protected HashMap<String, Boolean> tags = new HashMap<String, Boolean>();

    protected String nameID = "";

    protected ObjectDef definition;
    protected Level level;

    protected Vector2 startPast;

    public IngameObject(Vector2 startPast, String nameID, ObjectDef definition, Level level){

        this.nameID = nameID;
        this.definition = definition;
        this.level = level;

        this.startPast = startPast;
        Reset();
    }

    public void Reset(){
        //Past
        TimePosition[] positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        Interrupt[] interruptArray = new Interrupt[GameData.MAXMOVES + GameData.FILLERSIZE];
        TimePosition startPosition = new TimePosition((int)startPast.x, (int)startPast.y,definition.parameters.get("start_state"));

        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            interruptArray[i] = null;
        }
        positionArray.put(TimeID.past, positions);
        interrupts.put(TimeID.past, interruptArray);

        //Present
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        interruptArray = new Interrupt[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            interruptArray[i] = null;
        }
        positionArray.put(TimeID.present, positions);
        interrupts.put(TimeID.present, interruptArray);

        //Future
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        interruptArray = new Interrupt[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            interruptArray[i] = null;
        }
        positionArray.put(TimeID.future, positions);
        interrupts.put(TimeID.future, interruptArray);
    }

    public String getNameID(){ return nameID; }

    public void render(SpriteBatch batcher){

        TimePosition position = positionArray.get(level.getCurrentTimeID())[level.getCurrentTime()];

        if (position.aliveStatus == 0 || !definition.textureMap.containsKey(position.aliveStatus)){ return; }

        String animName = definition.textureMap.get(position.aliveStatus);

        if (animName != null) {
            Animation anim = AssetLoader.getIngameTexture(animName);

            //Gdx.app.log("IngameObject", nameID);
            //Gdx.app.log("IngameObject", definition.textureMap.get(1));
            //Gdx.app.log("IngameObject", String.valueOf(levelAge));

            batcher.draw((TextureRegion) anim.getKeyFrame(level.levelAge), position.x * GameData.GAMESIZE, position.y * GameData.GAMESIZE);
        }
    }

    public void timeUpdate(TimeID timeID, int time, TimeID previousTimeID){

        TimePosition[] currentPositions = positionArray.get(timeID);
        TimePosition[] earlierTimePositions = positionArray.get(previousTimeID);

        TimePosition currentPosition = currentPositions[time];

        if (time > 0){

            //======= During TimeID =======
            TimePosition previousPosition = currentPositions[time - 1];
            currentPosition.copyValues(previousPosition);

            Interrupt interrupt = interrupts.get(timeID)[time];

            if (interrupt != null){

                switch (interrupt.interruptID){
                    case pickup:
                        interrupt.caller.hold(this, timeID, time); //Confirm pickup with player
                        currentPosition.aliveStatus = 0; //Set alive status to 0 to hide
                        Gdx.app.log("IngameObject", nameID + ": processing pickup interrupt");
                        break;

                    case drop:
                        if (currentPosition.aliveStatus == 0){
                            interrupt.caller.drop(timeID, time, this);

                            TimePosition callerPosition = interrupt.caller.positionArray.get(timeID)[time];

                            currentPosition.x = callerPosition.x;
                            currentPosition.y = callerPosition.y;
                            currentPosition.aliveStatus = 1;
                        }
                        break;
                }

                interrupts.get(timeID)[time] = null;
            }

            if (currentPosition.aliveStatus > 0){

            }

        } else {

            //======= Start of timeID =======
            TimePosition previousPosition = earlierTimePositions[level.getCurrentTime(previousTimeID) + GameData.FILLERSIZE - 1];
            currentPosition.copyValues(previousPosition);

            if (timeID != TimeID.past && currentPosition.aliveStatus > 0){
                int grow_state = parameterValue("grow_state");

                if (grow_state != 0){
                    currentPosition.aliveStatus += grow_state;
                }
            }
        }

        if (nameID.equals("axe")){Gdx.app.log("IngameObject", "axe: " + timeID.toString() + " " + String.valueOf(time) + ", a=" + currentPosition.aliveStatus); }
    }

    public void sendInterrupt(Interrupt interrupt, TimeID timeID, int time){
        interrupts.get(timeID)[time] = interrupt;

        Gdx.app.log("IngameObject", nameID + ": received " + interrupt.interruptID.toString() + " interrupt at " + timeID.toString() + " " + String.valueOf(time));
    }

    public TimePosition getTimePosition(TimeID timeID, int time){
        return positionArray.get(timeID)[time];
    }

    public int parameterValue(String param){
        if (!definition.parameters.containsKey(param)){ return 0; }

        return definition.parameters.get(param);
    }
}
