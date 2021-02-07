package com.mrchocolatesalmon.pastpeter.datastructures;

import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;

public class Interrupt {

    public InterruptID interruptID;
    public PlayerObject caller;

    public Interrupt(InterruptID interruptID, PlayerObject player){
        this.interruptID = interruptID;
        caller = player;
    }

    public enum InterruptID { move, pickup, drop, interact, update, destroy; }
}
