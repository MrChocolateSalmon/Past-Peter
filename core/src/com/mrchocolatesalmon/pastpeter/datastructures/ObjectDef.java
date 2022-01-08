package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.enums.NPCGoal;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class ObjectDef {

    public HashMap<String, Integer> parameters = new HashMap<String, Integer>();

    public LinkedList<HashMap<Integer, String>> textureMaps = new LinkedList<HashMap<Integer, String>>();

    public LinkedList<String> interactLinks = new LinkedList<String>();
    public LinkedList<NPCDef> npcPriorities = new LinkedList<NPCDef>();

    public ObjectDef(){
        parameters.put("start_state", 1);
        parameters.put("connectionID", -1);
    }

    public ObjectDef Parameter(String name, int value){
        parameters.put(name, value);
        return this;
    }

    public ObjectDef Animation(int state, String texName, int alt){

        while (textureMaps.size() <= alt)
        {
            textureMaps.add(new HashMap<Integer, String>());
        }

        HashMap<Integer, String> textureMap = textureMaps.get(alt);
        textureMap.put(state, texName);
        return this;
    }

    public ObjectDef Animation(int state, String texName){
        Animation(state, texName, 0);
        return this;
    }

    //Inserts animation for a range of states (between start and end inclusive)
    public ObjectDef AnimationRange(int stateStart, int stateEnd, String texName){
        for (int i = stateStart; i <= stateEnd; i++){
            Animation(i, texName);
        }
        return this;
    }

    //Inserts animation for a range of states (between start and end inclusive)
    public ObjectDef AnimationRange(int stateStart, int stateEnd, String texName, int alt){
        for (int i = stateStart; i <= stateEnd; i++){
            Animation(i, texName, alt);
        }
        return this;
    }

    public ObjectDef InteractLink(String link){
        interactLinks.add(link);

        return this;
    }

    public ObjectDef NPC(NPCDef npcDef){
        npcPriorities.add(npcDef);

        return this;
    }

    public ObjectDef Connection(String connectionName, int offsetX, int offsetY, boolean connectionAliveLinked){

        if (GameData.objectNameStored.contains(connectionName)){
            parameters.put("connectionID", GameData.objectNameStored.indexOf(connectionName));

            parameters.put("connectionOffsetX", offsetX);
            parameters.put("connectionOffsetY", offsetY);

            parameters.put("connectionAliveLinked", connectionAliveLinked ? 1 : 0);
        }

        return this;
    }

    public ObjectDef CloneObjectDef(){
        ObjectDef c = new ObjectDef();

        c.parameters = (HashMap<String, Integer>)parameters.clone();

        c.textureMaps = new LinkedList<HashMap<Integer, String>>();
        for (int i = 0; i < textureMaps.size(); i++){
            c.textureMaps.add((HashMap<Integer, String>)textureMaps.get(i).clone());
        }

        //Gdx.app.log("ObjectDef", textureMaps.size() + ", " + c.textureMaps.size());

        c.interactLinks = (LinkedList<String>)interactLinks.clone();
        c.npcPriorities = (LinkedList<NPCDef>)npcPriorities.clone();

        return c;
    }

    public static class NPCDef {
        public NPCGoal goal;
        public Vector2 targetVector;
        public float targetValue;
        public LinkedList<String> targetNames =  new LinkedList<String>();

        public int minAliveStatus = 1, maxDist = -1;
        public boolean sameYAxis = true;

        public NPCDef set_moveToTargets(String[] targets){
            goal = NPCGoal.moveTo;
            Collections.addAll(targetNames, targets);

            return this;
        }

        public NPCDef set_moveToTargets(String[] targets, int maxDist, boolean sameYAxis){
            goal = NPCGoal.moveTo;
            Collections.addAll(targetNames, targets);

            this.sameYAxis = sameYAxis;
            this.maxDist = maxDist;

            return this;
        }

        public NPCDef set_huntIfState(int value){
            goal = NPCGoal.huntIfState;
            targetValue = value;

            return this;
        }

        public NPCDef set_flyDownTo(String[] targets, int minAliveStatus){
            goal = NPCGoal.flyDownTo;
            Collections.addAll(targetNames, targets);
            this.minAliveStatus = minAliveStatus;

            return this;
        }

        public NPCDef set_moveDirection(Vector2 dir){
            goal = NPCGoal.moveDirection;
            targetVector = dir;

            return this;
        }

        public NPCDef set_killPlayer(int value){
            goal = NPCGoal.killPlayer;
            targetValue = value;

            return this;
        }
    }
}

//==Parameters==
// [cut]        1 = can cut,
//              2 = can be cut,
//              3 = can both cut and be cut
//
// [fragile]    1 = reduces alive status by 1 when a player is standing above (by 2 if player is holding something)
//
// [grow_state] 1 = increments alive_status by 1 each age
//              2 = ^ same, but only if alive_status >= 2
//
// [interact]   !! Negative values are the same, but can't be interacted with by the player !!
//              1 = cycle between alive state 1 & 2
//              2 = ^ same, but alternates moving right & left
//              3 = ^ same, but alternates moving left & right
//              4 = Sets alive state to 2 when something (pickup=0) is on the same square & alive state is 1. Player interactions sets alive state +2.
//              5 = ^ same, but only resets "interact link" if all identical objects have alive state == 1
//
// [pickup]     1 = can be picked up if alive
//              2 = can be picked up with alivestatus == 1
//              3 = ^ same, but sets alivestatus to 2 when dropped
//              4 = can be picked up if alive, but sets alivestatus to 2 when dropped
//
// [wall] becomes a wall if alive status is larger than or equal to this value
