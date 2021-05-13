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

    public TimeID getCurrentTimeID(){ return currentTimeID; }
    public void setCurrentTimeID(TimeID timeID){
        if (timeAvailable.get(timeID)){
            currentTimeID = timeID;
        }
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

    public IngameObject findGameobjectWithParameter(String parameter, Vector2 pos, TimeID timeID, int time){

        for (int i = 0; i < objects.size(); i++){
            IngameObject temp = objects.get(i);
            TimePosition objPos = temp.getTimePosition(timeID,time);

            if (objPos.x == pos.x && objPos.y == pos.y && temp.parameterValue(parameter) != 0){
                return temp;
            }
        }

        return null;
    }

    public void SetBackground(BackgroundType backgroundType) {
        backType = backgroundType;
    }

    public BackgroundType GetBackground(){ return backType; }

    public LinkedList<IngameObject> getObjectsAt(TimeID timeID, int time, Vector2 pos) {
        LinkedList<IngameObject> objs = new LinkedList<IngameObject>();

        for (int i = 0; i < objects.size(); i++){
            IngameObject obj = objects.get(i);

            TimePosition timePos = obj.getTimePosition(timeID, time);

            if (timePos.x == pos.x && timePos.y == pos.y){ objs.add(obj); }
        }

        return objs;
    }
}
