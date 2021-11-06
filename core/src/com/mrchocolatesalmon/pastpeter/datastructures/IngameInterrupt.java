package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;

public class IngameInterrupt extends Interrupt {

    public PlayerObject caller;

    public Vector2 targetPos;
    public String useParameter = "";
    public int value;

    public IngameInterrupt(InterruptID interruptID, PlayerObject player){
        super(interruptID);
        caller = player;
    }

    public IngameInterrupt(PlayerObject player, int value){
        super(InterruptID.setAliveStatus);
        caller = player;
        this.value = value;
        this.targetPos = new Vector2(-1,-1);
    }

    public IngameInterrupt(PlayerObject player, Vector2 targetPos, int value){
        super(InterruptID.setAliveStatus);
        caller = player;
        this.value = value;
        this.targetPos = targetPos;
    }

    public IngameInterrupt(InterruptID interruptID, PlayerObject player, Vector2 targetPos){
        super(interruptID);
        caller = player;
        this.targetPos = targetPos;
    }

    public IngameInterrupt(InterruptID interruptID, PlayerObject player, String useParameter){
        super(interruptID);
        caller = player;
        this.useParameter = useParameter;
    }
}
