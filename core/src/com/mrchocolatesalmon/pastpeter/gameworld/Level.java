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

    public int pastTime, presentTime, futureTime;
    public HashMap<TimeID, Boolean> timeAvailable = new HashMap<TimeID, Boolean>();

    public boolean platformActive = false;

    public LinkedList<IngameObject> objects = new LinkedList<IngameObject>();
    public LinkedList<PlayerObject> players = new LinkedList<PlayerObject>();

    TimeID currentTimeID;

    public Level(GameData game, int index) {
        this.game = game;
        this.index = index;

        timeAvailable.put(TimeID.past, true);
        timeAvailable.put(TimeID.present, true);
        timeAvailable.put(TimeID.future, true);

        resetLevel();
    }

    public void AddObject(IngameObject obj){
        objects.add(obj);
    }

    public void resetLevel(){
        pastTime = 0;
        presentTime = 0;
        futureTime = 0;

        currentTimeID = TimeID.present;
    }

    public TimeID getCurrentTimeID(){ return currentTimeID; }
    public void setCurrentTimeID(TimeID timeID){
        if (timeAvailable.get(timeID)){
            currentTimeID = timeID;
        }
    }

    public int getCurrentTime(TimeID timeID){
        switch(timeID){
            case past: return pastTime;
            case present: return presentTime;
            case future: return futureTime;
            default: return -1;
        }
    }

    public int getCurrentTime(){
        return getCurrentTime(currentTimeID);
    }

    public void SetBackground(BackgroundType backgroundType) {
        backType = backgroundType;
    }
}
