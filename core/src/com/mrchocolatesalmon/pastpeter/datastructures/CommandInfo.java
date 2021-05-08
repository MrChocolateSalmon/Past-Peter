package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;

public class CommandInfo {
    public CommandID commandID = CommandID.wait;
    public Vector2 pos = new Vector2(0,0);

    public CommandInfo(CommandID commandID, Vector2 pos){
        this.commandID = commandID;
        this.pos = pos;
    }

    public enum CommandID { wait, move, pickup, drop, interact, use; }
}