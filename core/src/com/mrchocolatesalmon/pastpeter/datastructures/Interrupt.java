package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;

public abstract class Interrupt {

    public InterruptID interruptID;

    public Vector2 targetPos;
    public String useParameter = "";
    public int value;

    public Interrupt(InterruptID interruptID){
        this.interruptID = interruptID;
    }

    public enum InterruptID { move, pickup, drop, use, itemUsedOn, interact, setAliveStatus; }
}
