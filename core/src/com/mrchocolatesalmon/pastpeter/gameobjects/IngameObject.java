package com.mrchocolatesalmon.pastpeter.gameobjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.mrchocolatesalmon.pastpeter.datastructures.ObjectDef;
import com.mrchocolatesalmon.pastpeter.datastructures.TimePosition;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

import java.util.HashMap;

public class IngameObject {

    protected HashMap<TimeID,TimePosition[]> positionArray = new HashMap<TimeID, TimePosition[]>();
    protected HashMap<String, Boolean> tags = new HashMap<String, Boolean>();

    protected String nameID = "";

    protected ObjectDef definition;

    public IngameObject(Vector3 startPast, String nameID, ObjectDef definition){

        this.nameID = nameID;
        this.definition = definition;

        //Past
        TimePosition[] positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        TimePosition startPosition = new TimePosition((int)startPast.x, (int)startPast.y, (int)startPast.z);

        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
        }
        positionArray.put(TimeID.past, positions);

        //Present
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
        }
        positionArray.put(TimeID.present, positions);

        //Future
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
        }
        positionArray.put(TimeID.future, positions);
    }

    public String getNameID(){ return nameID; }

    public void render(Level level, SpriteBatch batcher){

        TimePosition position = positionArray.get(level.currentTimeID)[level.getCurrentTime()];

        if (position.aliveStatus == 0 || !definition.textureMap.containsKey(position.aliveStatus)){ return; }

        String animName = definition.textureMap.get(position.aliveStatus);

        if (animName != null) {
            Animation anim = AssetLoader.getIngameTexture(animName);

            //Gdx.app.log("IngameObject", nameID);
            //Gdx.app.log("IngameObject", definition.textureMap.get(1));
            //Gdx.app.log("IngameObject", String.valueOf(levelAge));

            batcher.draw((TextureRegion) anim.getKeyFrame(level.levelAge), position.x * GameData.GAMESIZE, position.y * GameData.GAMESIZE);
        }
    }
}
