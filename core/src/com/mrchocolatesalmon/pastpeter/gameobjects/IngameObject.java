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
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class IngameObject {

    protected HashMap<TimeID,TimePosition[]> positionArray = new HashMap<TimeID, TimePosition[]>();
    protected HashMap<TimeID, Interrupt[]> interrupts = new HashMap<TimeID, Interrupt[]>();

    protected HashMap<String, Boolean> tags = new HashMap<String, Boolean>();

    protected String nameID = "";

    protected ObjectDef definition;
    protected Level level;

    protected Vector2 startPast;

    protected IngameObject connectedObject;

    public IngameObject(Vector2 startPast, String nameID, ObjectDef definition, Level level){

        this.nameID = nameID;
        this.definition = definition;
        this.level = level;

        this.startPast = startPast;
        Reset();

        int connectionID = parameterValue("connectionID");
        if (connectionID >= 0){
            String connectionName = GameData.objectNameStored.get(connectionID);
            Vector2 connectionPos = new Vector2(startPast.x + parameterValue("connectionOffsetX"), startPast.y + parameterValue("connectionOffsetY"));
            boolean linkedAliveStatus = (parameterValue("connectionAliveLinked") == 1);

            ObjectDef connectionDef = GameData.getObjectDefinition(connectionName);

            if (linkedAliveStatus){ connectionDef.Parameter("start_state", parameterValue("start_state")); }

            connectedObject = level.AddObject(connectionPos, connectionName, connectionDef);

            Gdx.app.log("IngameObject", nameID + ": connection of " + connectionName);
        }
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

            //Handle any interrupts for this time point
            if (interrupt != null){

                switch (interrupt.interruptID){
                    case pickup:
                        interrupt.caller.hold(this, timeID, time); //Confirm pickup with player
                        currentPosition.aliveStatus = 0; //Set alive status to 0 to hide
                        //Gdx.app.log("IngameObject", nameID + ": processing pickup interrupt");
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

                    case use:

                        //Gdx.app.log("IngameObject", nameID + ": use interrupt");
                        Vector2 pos = new Vector2(interrupt.targetPos.x, interrupt.targetPos.y);
                        LinkedList<IngameObject> objs = level.getObjectsAt(timeID, time, pos);

                        AtomicReference<String> parameterName = new AtomicReference<String>();

                        IngameObject toUseOn = objectToUseOn(timeID, time, pos, objs, parameterName);
                        if (toUseOn != null){

                            switch (parameterName.get()){
                                case "cut":
                                    toUseOn.sendInterrupt(new Interrupt(Interrupt.InterruptID.itemUsedOn, interrupt.caller), timeID, time);
                                    break;
                            }
                        } else {

                        }

                        break;

                    case itemUsedOn:

                        //Gdx.app.log("IngameObject", nameID + ": itemUsedOn interrupt");
                        IngameObject item = interrupt.caller.getHolding(timeID, time);
                        if (item == null){ break; }

                        String useParameter = parameterForInteracting(timeID, time, item);

                        switch(useParameter){
                            case "cut":
                                if (currentPosition.aliveStatus > 0) {
                                    currentPosition.aliveStatus = (currentPosition.aliveStatus * -1) + 1;
                                }
                                break;
                        }
                        break;

                    case destroy:
                        currentPosition.aliveStatus = 0;
                        break;
                }

                interrupts.get(timeID)[time] = null;
            }

            //Continue processing this time point
            if (currentPosition.aliveStatus > 0){
                //TODO: Check other parameters and execute accordingly
            } else {
                if (connectedObject != null) {
                    connectedObject.sendInterrupt(new Interrupt(Interrupt.InterruptID.destroy, null), timeID, time);
                }
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

        if (nameID.equals("leaf")){
            Gdx.app.log("IngameObject", nameID + ": " + timeID.toString() + " " + String.valueOf(time) + ", a=" + currentPosition.aliveStatus);
            Gdx.app.log("IngameObject", nameID + ": " + timeID.toString() + " " + String.valueOf(time) + ", pos=" + currentPosition.x + "," + currentPosition.y);
        }
    }

    public boolean canUseHere(Vector2 pos, TimeID timeID, int time){
        if (definition.parameters.get("pickup") == 0){ return false; }

        TimePosition[] currentPositions = positionArray.get(timeID);
        TimePosition currentPosition = currentPositions[time];

        LinkedList<IngameObject> objs = level.getObjectsAt(timeID, time, pos);

        if (objectToUseOn(timeID, time, pos, objs, null) != null) { return true; }

        return emptySpaceToUseOn(timeID, time, pos, objs);
    }

    public IngameObject objectToUseOn(TimeID timeID, int time, Vector2 pos, LinkedList<IngameObject> objs, AtomicReference<String> stringRef){

        int myCut = parameterValue("cut");

        //TODO: Return null if all of the above parameters are equal to 0?
        if (myCut == 0){
            return null;
        }

        for (int i = 0; i < objs.size(); i++){
            IngameObject obj = objs.get(i);

            if ((myCut == 1 || myCut == 3) && obj.parameterValue("cut") >= 2) {
                if (stringRef != null){ stringRef.set("cut"); }
                return obj;
            }
        }

        return null;
    }

    public boolean emptySpaceToUseOn(TimeID timeID, int time, Vector2 pos, LinkedList<IngameObject> objs) {

        //TODO: return false unless it has a certain parameter

        return false;
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

    public String parameterForInteracting(TimeID timeID, int time, IngameObject item) {
        TimePosition myTimePos = getTimePosition(timeID, time), itemTimePos = item.getTimePosition(timeID, time);
        
        if (parameterValue("cut") >= 2 && (item.parameterValue("cut") == 1 || item.parameterValue("cut") == 3)){
            return "cut";
        }

        return "";
    }
}
