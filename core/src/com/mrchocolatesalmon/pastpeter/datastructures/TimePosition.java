package com.mrchocolatesalmon.pastpeter.datastructures;

import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;

//Stores the details of the position in time
public class TimePosition {

    public int x, y, aliveStatus, breathing;
    public IngameObject holding;

    public TimePosition(int x, int y, int status){
        this.x = x;
        this.y = y;
        this.aliveStatus = status;
        breathing = 1;
        holding = null;
    }

    public TimePosition(int x, int y, int status, int breathing){
        this.x = x;
        this.y = y;
        this.aliveStatus = status;
        this.breathing = breathing;
    }

    public TimePosition Clone(){
        return new TimePosition(x, y, aliveStatus, breathing);
    }

    public boolean XYEquals(TimePosition testVector){
        return (x == testVector.x && y == testVector.y);
    }

    public boolean XYEquals(int x, int y){
        return (this.x == x && this.y == y);
    }
}
