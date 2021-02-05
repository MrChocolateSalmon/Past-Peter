package com.mrchocolatesalmon.pastpeter.datastructures;

import java.util.HashMap;

public class ObjectDef {

    public String[] tags;

    public HashMap<Integer, String> textureMap = new HashMap<Integer, String>();

    public ObjectDef(){
    }

    public ObjectDef Tags(String[] tags){
        this.tags = tags;
        return this;
    }

    public ObjectDef Animation(int state, String texName){
        textureMap.put(state, texName);
        return this;
    }
}
