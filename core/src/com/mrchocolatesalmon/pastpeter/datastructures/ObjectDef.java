package com.mrchocolatesalmon.pastpeter.datastructures;

import java.util.HashMap;

public class ObjectDef {

    public HashMap<String, Integer> parameters = new HashMap<String, Integer>();

    public HashMap<Integer, String> textureMap = new HashMap<Integer, String>();

    public ObjectDef(){
        parameters.put("start_state", 1);
    }

    public ObjectDef Parameter(String name, int value){
        parameters.put(name, value);
        return this;
    }

    public ObjectDef Animation(int state, String texName){
        textureMap.put(state, texName);
        return this;
    }

    //Inserts animation for a range of states (between start and end inclusive)
    public ObjectDef Animation(int stateStart, int startEnd, String texName){
        for (int i = stateStart; i <= startEnd; i++){
            textureMap.put(i, texName);
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
