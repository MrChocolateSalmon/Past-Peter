package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;

public class AnimationCommand {

    public Vector2 position;
    public TransitionType transition;
    public String animName;

    public AnimationCommand(Vector2 p, TransitionType t, String s) {
        position = p;
        transition = t;
        animName = s;
    }

    public void copyVariables(AnimationCommand c){
        position = new Vector2(c.position.x, c.position.y);
        transition = c.transition;
        animName = c.animName;
    }


    public enum TransitionType { none, lerp; }
}
