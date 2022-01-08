package com.mrchocolatesalmon.pastpeter.players;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.enums.PlayerID;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;

public class Paige extends PlayerObject {

    public Paige(Vector2 startPast, Vector2 startPresent, Vector2 startFuture, Level level) {
        super(startPast, startPresent, startFuture, startFuture, PlayerID.paige, level);

        textureMap.get(TimeID.past).put("idle", "paige_past_idle");
        textureMap.get(TimeID.present).put("idle", "paige_present_idle");

        textureMap.get(TimeID.past).put("endpoint", "paige_past_endpoint");
        textureMap.get(TimeID.present).put("endpoint", "paige_present_endpoint");
    }
}
