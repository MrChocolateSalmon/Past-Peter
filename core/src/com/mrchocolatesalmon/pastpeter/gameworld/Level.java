package com.mrchocolatesalmon.pastpeter.gameworld;

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

    public void AddObject(IngameObject obj){
        objects.add(obj);
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

    public void SetBackground(BackgroundType backgroundType) {
        backType = backgroundType;
    }
}
