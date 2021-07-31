package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;

public class Interrupt {

    public InterruptID interruptID;
    public PlayerObject caller;

    public Vector2 targetPos;
    public String useParameter = "";
    public int value;

    public Interrupt(InterruptID interruptID, PlayerObject player){
        this.interruptID = interruptID;
        caller = player;
    }

    public Interrupt(PlayerObject player, int value){
        this.interruptID = InterruptID.setAliveStatus;
        caller = player;
        this.value = value;
        this.targetPos = new Vector2(-1,-1);
    }

    public Interrupt(PlayerObject player, Vector2 targetPos, int value){
        this.interruptID = InterruptID.setAliveStatus;
        caller = player;
        this.value = value;
        this.targetPos = targetPos;
    }

    public Interrupt(InterruptID interruptID, PlayerObject player, Vector2 targetPos){
        this.interruptID = interruptID;
        caller = player;
        this.targetPos = targetPos;
    }

    public Interrupt(InterruptID interruptID, PlayerObject player, String useParameter){
        this.interruptID = interruptID;
        caller = player;
        this.useParameter = useParameter;
    }

    public enum InterruptID { move, pickup, drop, use, itemUsedOn, interact, setAliveStatus; }
}
