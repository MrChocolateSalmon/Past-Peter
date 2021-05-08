package com.mrchocolatesalmon.pastpeter.datastructures;

import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;

import java.util.HashMap;

public class ObjectDef {

    public HashMap<String, Integer> parameters = new HashMap<String, Integer>();

    public HashMap<Integer, String> textureMap = new HashMap<Integer, String>();


    public ObjectDef(){
        parameters.put("start_state", 1);
        parameters.put("connectionID", -1);
    }

    public ObjectDef Parameter(String name, int value){
        parameters.put(name, value);
        return this;
    }

    public ObjectDef Animation(int state, String texName){
        textureMap.put(state, texName);
        return this;
    }

    public ObjectDef AnimationRange(int stateStart, int stateEnd, String texName){
        for (int i = stateStart; i <= stateEnd; i++){
            textureMap.put(i, texName);
        }
        return this;
    }

    //Inserts animation for a range of states (between start and end inclusive)
    public ObjectDef Animation(int stateStart, int startEnd, String texName){
        for (int i = stateStart; i <= startEnd; i++){
            textureMap.put(i, texName);
        }
        return this;
    }

    public ObjectDef Connection(String connectionName, int offsetX, int offsetY, boolean connectionAliveLinked){

        if (GameData.objectNameStored.contains(connectionName)){
            parameters.put("connectionID", GameData.objectNameStored.indexOf(connectionName));

            parameters.put("connectionOffsetX", offsetX);
            parameters.put("connectionOffsetY", offsetY);

            parameters.put("connectionAliveLinked", connectionAliveLinked ? 1 : 0);

            //parameters.put("connectionType", connectionType);
            //connectionType???
            //0 = Same alive status
            //1 = Alive status minus 1
        }

        return this;
    }

    public ObjectDef CloneObjectDef(){
        ObjectDef c = new ObjectDef();

        c.parameters = (HashMap<String, Integer>)parameters.clone();
        c.textureMap = (HashMap<Integer, String>)textureMap.clone();

        return c;
    }
}

//==Parameters==
// [cut] 1 = can cut, 2 = can be cut, 3 = can both cut and be cut
// [wall] becomes a wall if alive status is larger than or equal to this value
