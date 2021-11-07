package com.mrchocolatesalmon.pastpeter.gameobjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mrchocolatesalmon.pastpeter.datastructures.*;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class IngameObject {

    protected HashMap<TimeID,TimePosition[]> positionArray = new HashMap<TimeID, TimePosition[]>();
    protected LinkedList<IngameInterrupt> interrupts = new LinkedList<IngameInterrupt>();

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

            ObjectDef connectionDef = GameData.getObjectDefinition(connectionName);

            if (parameterValue("connectionAliveLinked") == 1)
            {
                connectionDef.Parameter("start_state", parameterValue("start_state"));
            }

            connectedObject = level.AddObject(connectionPos, connectionName, connectionDef);

            Gdx.app.log("IngameObject", nameID + ": connection of " + connectionName);
        }
    }

    public void Reset(){
        //Past
        TimePosition[] positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        IngameInterrupt[] interruptArray = new IngameInterrupt[GameData.MAXMOVES + GameData.FILLERSIZE];
        TimePosition startPosition = new TimePosition((int)startPast.x, (int)startPast.y,definition.parameters.get("start_state"));

        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            interruptArray[i] = null;
        }
        positionArray.put(TimeID.past, positions);

        //Present
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        interruptArray = new IngameInterrupt[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            interruptArray[i] = null;
        }
        positionArray.put(TimeID.present, positions);

        //Future
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        interruptArray = new IngameInterrupt[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            interruptArray[i] = null;
        }
        positionArray.put(TimeID.future, positions);
    }

    public String getNameID(){ return nameID; }

    public void render(SpriteBatch batcher){

        TimePosition position = positionArray.get(level.getCurrentTimeID())[level.getCurrentTime()];

        //if (nameID.equals("fragilestone")){ Gdx.app.log("IngameObject", "Fragile Stone: " + level.getCurrentTime() + ", " + position.aliveStatus); }

        int alt = parameterValue("alt"); //Alternate textures type
        if (definition.textureMaps.size() <= alt) { return; }

        HashMap<Integer, String> textureMap = definition.textureMaps.get(alt);

        if (position.aliveStatus == 0 || !textureMap.containsKey(position.aliveStatus)){ return; }

        String animName = textureMap.get(position.aliveStatus);

        if (animName != null) {
            Animation anim = AssetLoader.getIngameTexture(animName);

            //Gdx.app.log("IngameObject", nameID);
            //Gdx.app.log("IngameObject", textureMap.get(1));
            //Gdx.app.log("IngameObject", String.valueOf(level.levelAge));

            batcher.draw((TextureRegion) anim.getKeyFrame(level.levelAge), position.x * GameData.GAMESIZE, position.y * GameData.GAMESIZE);
        }
    }

    TimePosition ProcessInterrupts(TimePosition currentPosition, TimeID timeID, int time)
    {
        for (int i = interrupts.size() - 1; i >= 0; i--){

            IngameInterrupt interrupt = interrupts.get(0);
            Gdx.app.log("IngameObject", nameID + ": processing " + interrupt.interruptID.toString() + " interrupt");

            switch (interrupt.interruptID) {
                case pickup:
                    int pickupValue = parameterValue("pickup");

                    if (pickupValue == 1 || (pickupValue == 4 && currentPosition.aliveStatus > 0) || (pickupValue < 4 && pickupValue > 1 && currentPosition.aliveStatus == 1)) {
                        interrupt.caller.hold(this, timeID, time); //Confirm pickup with player
                        currentPosition.aliveStatus = 0; //Set alive status to 0 to hide
                    }
                    break;

                case drop:
                    if (currentPosition.aliveStatus == 0) {
                        interrupt.caller.drop(timeID, time, this);

                        TimePosition callerPosition = interrupt.caller.positionArray.get(timeID)[time];

                        currentPosition.x = callerPosition.x;
                        currentPosition.y = callerPosition.y;

                        if (parameterValue("pickup") == 3 || parameterValue("pickup") == 4) {
                            currentPosition.aliveStatus = 2;
                        } else {
                            currentPosition.aliveStatus = 1;
                        }
                    }
                    break;

                case use:

                    //Gdx.app.log("IngameObject", nameID + ": use interrupt");
                    Vector2 pos = new Vector2(interrupt.targetPos.x, interrupt.targetPos.y);
                    LinkedList<IngameObject> objs = level.getObjectsAt(timeID, time, pos);

                    AtomicReference<String> parameterName = new AtomicReference<String>();

                    IngameObject toUseOn = objectToUseOn(timeID, time, pos, objs, parameterName);
                    if (toUseOn != null) {

                        switch (parameterName.get()) {
                            case "cut":
                                toUseOn.sendInterrupt(new IngameInterrupt(Interrupt.InterruptID.itemUsedOn, interrupt.caller), timeID, time);
                                break;
                        }
                    } else {

                    }

                    break;

                case itemUsedOn:

                    //Gdx.app.log("IngameObject", nameID + ": itemUsedOn interrupt");
                    IngameObject item = interrupt.caller.getHolding(timeID, time);
                    if (item == null) {
                        break;
                    }

                    String useParameter = parameterForInteracting(timeID, time, item);

                    switch (useParameter) {
                        case "cut":
                            if (currentPosition.aliveStatus > 0) {
                                currentPosition.aliveStatus = (currentPosition.aliveStatus * -1) + 1;
                            }
                            break;
                    }
                    break;

                case interact:
                    int interactType = parameterValue("interact");
                    int alt = parameterValue("alt");

                    if (interactType != 0) {
                        switch (interactType) {
                            case -1:
                            case 1:
                                if (currentPosition.aliveStatus == 1) {
                                    currentPosition.aliveStatus = 2;
                                } else if (currentPosition.aliveStatus == 2) {
                                    currentPosition.aliveStatus = 1;
                                }
                                break;

                            case -2:
                            case 2:
                                if (currentPosition.aliveStatus == 1) {
                                    currentPosition.aliveStatus = 2;
                                    currentPosition.x += 1;
                                } else if (currentPosition.aliveStatus == 2) {
                                    currentPosition.aliveStatus = 1;
                                    currentPosition.x -= 1;
                                }
                                break;

                            case -3:
                            case 3:
                                if (currentPosition.aliveStatus == 1) {
                                    currentPosition.aliveStatus = 2;
                                    currentPosition.x -= 1;
                                } else if (currentPosition.aliveStatus == 2) {
                                    currentPosition.aliveStatus = 1;
                                    currentPosition.x += 1;
                                }
                                break;
                        }

                        //Gdx.app.log("IngameObject", nameID + ": definition.interactLinks.size() = " + definition.interactLinks.size());
                        if (definition.interactLinks.size() > 0) {
                            for (IngameObject link : level.getObjectsWithNameID(timeID, time, definition.interactLinks)) {
                                if (link.parameterValue("alt") == alt) {
                                    //Gdx.app.log("IngameObject", nameID + ": Sending linked interact to " + link.nameID);
                                    link.sendInterrupt(new IngameInterrupt(Interrupt.InterruptID.interact, null), timeID, time);
                                }
                            }
                        }
                    }

                    break;

                case setAliveStatus:
                    currentPosition.aliveStatus = interrupt.value;
                    if (interrupt.targetPos.x >= 0) {
                        currentPosition.x = (int) interrupt.targetPos.x;
                        currentPosition.y = (int) interrupt.targetPos.y;
                    }
                    break;
            }

            if (interrupt.caller == null) {
                interrupts.remove(i);
            }
        }

        return currentPosition;
    }

    public void timeUpdate(TimeID timeID, int time, TimeID previousTimeID){

        TimePosition[] currentPositions = positionArray.get(timeID);
        TimePosition[] earlierTimePositions = positionArray.get(previousTimeID);

        TimePosition currentPosition = currentPositions[time];

        if (time > 0){

            //======= During TimeID =======
            TimePosition previousPosition = currentPositions[time - 1];
            currentPosition.copyValues(previousPosition);

            currentPosition = ProcessInterrupts(currentPosition, timeID, time);

            //Continue processing this time point
            if (currentPosition.aliveStatus > 0){

                boolean automaticAction = false;

                //IngameObject wallBelow = level.findGameobjectWithParameter("wall", new Vector2(currentPosition.x, currentPosition.y+1), timeID, time);

                //TODO: Check other parameters and execute accordingly

                Vector2 currentPositionVector = currentPosition.vector2();

                //And now perform any npcAction, if nothing was already done (such as falling)?
                int numberOfNPCPriorities = definition.npcPriorities.size();
                if (!automaticAction && definition.npcPriorities.size() > 0){
                    for (int i = 0 ;i < numberOfNPCPriorities; i++){
                        ObjectDef.NPCDef npcDef = definition.npcPriorities.get(i);

                        boolean used = false;

                        switch(npcDef.goal){

                            case flyDownTo:
                                //TODO: Search through list, and if that object is below, move down to it
                                int flyx = currentPosition.x;
                                for (int y = currentPosition.y+1; y < GameData.GAMEHEIGHT; y++){

                                    Vector2 checkPosition = new Vector2(flyx,y);

                                    for (String n : npcDef.targetNames){
                                        LinkedList<IngameObject> targetsFound = level.getObjects(timeID, time, checkPosition, npcDef.targetNames);

                                        for (int t = targetsFound.size() - 1; t >= 0; t--){
                                            if (targetsFound.get(t).getTimePosition(timeID, time).aliveStatus < npcDef.minAliveStatus){
                                                targetsFound.remove(t);
                                            }
                                        }

                                        if (targetsFound.size() > 0){

                                            if (y > currentPosition.y + 1){
                                                currentPosition.y += 1; //Move down if not already on top of the object
                                            }

                                            used = true;
                                            break;
                                        }
                                    }

                                    if (used) { break; }
                                }
                                break;

                            case huntIfState:

                                //Don't kill if not high enough aliveStatus
                                if (currentPosition.aliveStatus < npcDef.targetValue){ break; }

                                PlayerObject closest = null;
                                float closestDist = 0;

                                for (int p = 0; p < level.players.size(); p++) {

                                    PlayerObject tempPlayer = level.players.get(p);
                                    TimePosition playerTimePos = tempPlayer.getPosition(timeID, time);

                                    if (playerTimePos.y == currentPosition.y && playerTimePos.aliveStatus > 0) {

                                        if (playerTimePos.x == currentPosition.x) {
                                            level.players.get(p).sendInterrupt(timeID, time, new PlayerInterrupt(0));
                                            used = true;
                                            Gdx.app.log("InGameObject", "Kill player!!");
                                            break;
                                        }
                                        else {
                                            float tempDist = playerTimePos.vector2().dst(currentPositionVector);

                                            if (closest == null || tempDist < closestDist) {
                                                closestDist = tempDist;
                                                closest = tempPlayer;
                                            }
                                        }
                                    }
                                }

                                if (!used && closest != null) {

                                    TimePosition closestPos = closest.getPosition(timeID, time);
                                    if (closestPos.x < currentPosition.x) {
                                        currentPosition.x -= 1; //Move down if not already on top of the object
                                    } else if (closestPos.x > currentPosition.x) {
                                        currentPosition.x += 1; //Move down if not already on top of the object
                                    }

                                    if (currentPosition.x == closestPos.x){
                                        closest.sendInterrupt(timeID, time, new PlayerInterrupt(0));
                                        Gdx.app.log("InGameObject", "Kill player!!");
                                    }

                                    used = true;
                                }

                                break;

                            case killPlayer:

                                //Don't kill if not high enough aliveStatus
                                if (currentPosition.aliveStatus < npcDef.targetValue){ break; }

                                for (int p = 0; p < level.players.size(); p++) {

                                    TimePosition playerTimePos = level.players.get(p).getPosition(timeID, time);

                                    if (playerTimePos.x == currentPosition.x && playerTimePos.y == currentPosition.y && playerTimePos.aliveStatus > 0) {
                                        level.players.get(p).sendInterrupt(timeID, time, new PlayerInterrupt(0));
                                        used = true;
                                    }
                                }

                                break;

                            case moveDirection:
                                currentPosition.x += npcDef.targetVector.x;
                                currentPosition.y += npcDef.targetVector.y;
                                used = true;
                                break;

                            case moveTo:
                                //TODO: Change this to path finding throughout whole leel
                                int y = currentPosition.y;

                                TimePosition closestObjectPos = null;
                                float closestObjectDist = 0;

                                for (int movex = 0; movex < GameData.GAMEWIDTH; movex++){

                                    Vector2 checkPosition = new Vector2(movex,y);

                                    for (String n : npcDef.targetNames){
                                        LinkedList<IngameObject> targetsFound = level.getObjects(timeID, time, checkPosition, npcDef.targetNames);

                                        for (int t = targetsFound.size() - 1; t >= 0; t--){

                                            TimePosition tempObjectPos = targetsFound.get(t).getTimePosition(timeID, time);

                                            if (tempObjectPos.aliveStatus >= npcDef.minAliveStatus){
                                                float tempDist = tempObjectPos.vector2().dst(currentPositionVector);

                                                if (closestObjectPos == null || tempDist < closestObjectDist) {
                                                    closestObjectDist = tempDist;
                                                    closestObjectPos = tempObjectPos;
                                                }
                                            }
                                        }
                                    }
                                }

                                if (!used && closestObjectPos != null) {

                                    if (closestObjectPos.x < currentPosition.x) {
                                        currentPosition.x -= 1; //Move down if not already on top of the object
                                    } else if (closestObjectPos.x > currentPosition.x) {
                                        currentPosition.x += 1; //Move down if not already on top of the object
                                    }

                                    used = true;
                                }

                                break;
                        }

                        if (used){ break; }
                    }
                }

            } else {
                if (connectedObject != null) {
                    Gdx.app.log("IngameObject", nameID + ": sending destroy interrupt");
                    connectedObject.sendInterrupt(new IngameInterrupt(null, 0), timeID, time);
                }
            }

        } else if (timeID != TimeID.past){

            //======= Start of timeID =======
            TimePosition previousPosition = earlierTimePositions[level.getCurrentTime(previousTimeID) + GameData.FILLERSIZE - 1];
            currentPosition.copyValues(previousPosition);

            currentPosition = ProcessInterrupts(currentPosition, timeID, time);

            if (timeID != TimeID.past && currentPosition.aliveStatus > 0){
                int grow_state = parameterValue("grow_state");

                if (grow_state == 1 || grow_state == 2)  {
                    if (currentPosition.aliveStatus >= grow_state)  {
                        currentPosition.aliveStatus += grow_state;
                    }
                }
            }
        }

        int connectionID = parameterValue("connectionID");
        if (connectionID >= 0)
        {
            if (parameterValue("connectionAliveLinked") == 1)
            {
                IngameInterrupt connectionUpdate = new IngameInterrupt(null,
                                                    new Vector2(currentPosition.x + parameterValue("connectionOffsetX"), currentPosition.y + parameterValue("connectionOffsetY")),
                                                            currentPosition.aliveStatus);

                connectedObject.sendInterrupt(connectionUpdate, timeID, time);
            }
        }

        //if (nameID.equals("leaf")){
        //    Gdx.app.log("IngameObject", nameID + ": " + timeID.toString() + " " + String.valueOf(time) + ", a=" + currentPosition.aliveStatus);
        //    Gdx.app.log("IngameObject", nameID + ": " + timeID.toString() + " " + String.valueOf(time) + ", pos=" + currentPosition.x + "," + currentPosition.y);
        //}
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

    public void sendInterrupt(IngameInterrupt interrupt, TimeID timeID, int time){
        interrupts.add(0,interrupt);

        Gdx.app.log("IngameObject", nameID + ": received " + interrupt.interruptID.toString() + " interrupt at " + timeID.toString() + " " + String.valueOf(time));
    }

    public void clearInterrupts(){
        interrupts.clear();
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
