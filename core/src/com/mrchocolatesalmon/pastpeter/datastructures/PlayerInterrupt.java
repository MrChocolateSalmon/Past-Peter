package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;

public class PlayerInterrupt extends Interrupt {

    public PlayerInterrupt(InterruptID interruptID) {
        super(interruptID);
    }

    public PlayerInterrupt(int value){
        super(InterruptID.setAliveStatus);
        this.value = value;
        this.targetPos = new Vector2(-1,-1);
    }
}
