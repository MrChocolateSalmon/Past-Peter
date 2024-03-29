package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;

//Stores the details of the position in time
public class TimePosition {

    public int x, y, aliveStatus, breathing;
    public IngameObject holding;

    public Vector2 vector2(){
        return new Vector2(x,y);
    }

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

    public void copyValues(TimePosition other){
        x = other.x;
        y = other.y;
        aliveStatus = other.aliveStatus;
        breathing = other.breathing;
        holding = other.holding;
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
