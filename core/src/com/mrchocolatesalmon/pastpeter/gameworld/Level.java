package com.mrchocolatesalmon.pastpeter.gameworld;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.ObjectDef;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.BackgroundType;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;

import java.util.HashMap;
import java.util.LinkedList;

public class Level {

    private GameData game;
    private int index;
    public float levelAge = 0;

    private BackgroundType backType = BackgroundType.sky;

    public String name = "???", hint = "No Hint/Story for this level.";

    protected HashMap<TimeID, Integer> timeMap = new HashMap<TimeID, Integer>();
    public HashMap<TimeID, Boolean> timeAvailable = new HashMap<TimeID, Boolean>();

    public boolean platformActive = false;

    public LinkedList<IngameObject> objects = new LinkedList<IngameObject>();
    public LinkedList<PlayerObject> players = new LinkedList<PlayerObject>();

    public int activePlayerNumber = 0;

    TimeID currentTimeID;

    public Level(GameData game, int index) {
        this.game = game;
        this.index = index;

        timeAvailable.put(TimeID.past, true);
        timeAvailable.put(TimeID.present, true);
        timeAvailable.put(TimeID.future, true);

        timeMap.put(TimeID.past, 0);
        timeMap.put(TimeID.present, 0);
        timeMap.put(TimeID.future, 0);

        resetLevel();
    }

    public IngameObject AddObject(Vector2 pos, String objectName, ObjectDef definition){
        IngameObject obj = new IngameObject(pos, objectName, definition, this);
        objects.add(obj);

        return obj;
    }

    public void resetLevel(){
        timeMap.put(TimeID.past, 0);
        timeMap.put(TimeID.present, 0);
        timeMap.put(TimeID.future, 0);

        currentTimeID = TimeID.present;

        activePlayerNumber = 0;

        levelAge = 0;
    }

    public PlayerObject getActivePlayer(){
        return players.get(activePlayerNumber);
    }

    public PlayerObject setActivePlayer(int newPlayerNumber) {
        activePlayerNumber = newPlayerNumber % players.size();
        return getActivePlayer();
    }

    public TimeID getCurrentTimeID(){ return currentTimeID; }
    public boolean setCurrentTimeID(TimeID timeID){
        if (timeAvailable.get(timeID)){
            currentTimeID = timeID;
            return true;
        }

        return false;
    }

    public void incrementTime(TimeID timeID){
        timeMap.put(timeID, timeMap.get(timeID) + 1);
    }

    public void incrementTime(){
        incrementTime(getCurrentTimeID());
    }

    public void decrementTime(TimeID timeID){
        int newTime = timeMap.get(timeID) - 1;
        if (newTime < 0){ newTime = 0;}
        timeMap.put(timeID, newTime);
    }

    public void decrementTime(){
        decrementTime(getCurrentTimeID());
    }

    public int getCurrentTime(TimeID timeID){
        return timeMap.get(timeID);
    }

    public int getCurrentTime(){
        return getCurrentTime(currentTimeID);
    }

    public IngameObject getObjectWithParameter(String parameter, Vector2 pos, TimeID timeID, int time){

        for (int i = 0; i < objects.size(); i++){
            IngameObject temp = objects.get(i);
            TimePosition objPos = temp.getTimePosition(timeID,time);

            if (objPos.x == pos.x && objPos.y == pos.y && temp.parameterValue(parameter) != 0){
                return temp;
            }
        }

        return null;
    }

    public IngameObject getObjectWithValidParameter(String parameter, Vector2 pos, TimeID timeID, int time){

        for (int i = 0; i < objects.size(); i++){
            IngameObject temp = objects.get(i);
            TimePosition objPos = temp.getTimePosition(timeID,time);

            if (objPos.x == pos.x && objPos.y == pos.y && temp.parameterValue(parameter) > 0){
                return temp;
            }
        }

        return null;
    }

    public IngameObject getObjectWithParameterEqualTo(String parameter, int requiredValue, Vector2 pos, TimeID timeID, int time, IngameObject exclude){

        for (int i = 0; i < objects.size(); i++){
            IngameObject temp = objects.get(i);
            TimePosition objPos = temp.getTimePosition(timeID,time);

            if (temp == exclude){ continue; }

            if (objPos.x == pos.x && objPos.y == pos.y && temp.parameterValue(parameter) == requiredValue){
                return temp;
            }
        }

        return null;
    }

    public IngameObject getObjectWithParameterEqualTo(String parameter, int requiredValue, Vector2 pos, TimeID timeID, int time){

        return getObjectWithParameterEqualTo(parameter, requiredValue, pos, timeID, time, null);
    }

    public IngameObject getPickupObject(Vector2 pos, TimeID timeID, int time){

        for (int i = 0; i < objects.size(); i++){
            IngameObject temp = objects.get(i);
            TimePosition objPos = temp.getTimePosition(timeID,time);

            if (objPos.x == pos.x && objPos.y == pos.y){
                int pickupParameter = temp.parameterValue("pickup");

                if (pickupParameter == 1 || pickupParameter == 4 ||(pickupParameter >= 2 && pickupParameter <= 3 && objPos.aliveStatus == 1)) {
                    return temp;
                }
            }
        }

        return null;
    }

    public void SetBackground(BackgroundType backgroundType) {
        backType = backgroundType;
    }

    public BackgroundType GetBackground(){ return backType; }

    public LinkedList<IngameObject> getObjects(TimeID timeID, int time, Vector2 pos, LinkedList<String> names, IngameObject exclude) {
        LinkedList<IngameObject> objs = new LinkedList<IngameObject>();

        for (int i = 0; i < objects.size(); i++){
            IngameObject obj = objects.get(i);

            if (obj == exclude){ continue; }

            TimePosition timePos = obj.getTimePosition(timeID, time);

            if (pos == null || (timePos.x == pos.x && timePos.y == pos.y)){

                boolean found = false;

                if (names == null || names.size() == 0){
                    found = true;

                } else {

                    for (String tempName : names){
                        if (tempName.equals(obj.getNameID())){ found = true; break; }
                    }
                }

                if (found){
                    objs.add(obj);
                }
            }
        }

        return objs;
    }

    public LinkedList<IngameObject> getObjects(TimeID timeID, int time, Vector2 pos, LinkedList<String> names) {
        return getObjects(timeID, time, pos, names, null);
    }

    public LinkedList<IngameObject> getObjectsAt(TimeID timeID, int time, Vector2 pos, IngameObject exclude) {
        return getObjects(timeID, time, pos, null, exclude);
    }

    public LinkedList<IngameObject> getObjectsWithNameID(TimeID timeID, int time, LinkedList<String> names) {
        return getObjects(timeID, time, null, names);
    }

    public PlayerObject getPlayerAt(TimeID timeID, int time, Vector2 pos) {

        for (int i = 0; i < players.size(); i++){
            PlayerObject player = players.get(i);

            TimePosition timePos = player.getPosition(timeID, time);

            if (pos == null || timePos.x == pos.x && timePos.y == pos.y){
                return player;
            }
        }

        return null;
    }
}
