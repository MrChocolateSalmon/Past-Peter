package com.mrchocolatesalmon.pastpeter.players;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.enums.PlayerID;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.PlayerObject;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;

public class Peter extends PlayerObject {

    public Peter(Vector2 startPast, Vector2 startPresent, Vector2 startFuture, Vector2 endFuture, PlayerID id, Level level) {
        super(startPast, startPresent, startFuture, endFuture, id, level);

        textureMap.get(TimeID.past).put("idle", "peter_past_idle");
        textureMap.get(TimeID.present).put("idle", "peter_present_idle");
        textureMap.get(TimeID.future).put("idle", "peter_future_idle");

        textureMap.get(TimeID.past).put("endpoint", "peter_past_endpoint");
        textureMap.get(TimeID.present).put("endpoint", "peter_present_endpoint");
        textureMap.get(TimeID.future).put("endpoint", "peter_future_endpoint");
    }
}
