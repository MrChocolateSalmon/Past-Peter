package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.enums.CommandID;

public class CommandInfo {
    public CommandID commandID = CommandID.wait;
    public Vector2 pos = new Vector2(0,0);

    public CommandInfo(CommandID commandID, Vector2 pos){
        this.commandID = commandID;
        this.pos = pos;
    }
}