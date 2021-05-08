package com.mrchocolatesalmon.pastpeter.gameworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.mrchocolatesalmon.pastpeter.datastructures.ObjectDef;
import com.mrchocolatesalmon.pastpeter.helpers.*;
import com.mrchocolatesalmon.pastpeter.screens.*;

import javax.management.ReflectionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class GameData {

    public final static int GAMESIZE = 64;
    public final int GAMEHEIGHT = 9;
    public final int GAMEWIDTH = 11;
    public final int CAMWIDTH = GAMEWIDTH * GAMESIZE, CAMHEIGHT = GAMEHEIGHT * GAMESIZE;
    public final float CAMRATIO = (float) CAMWIDTH / CAMHEIGHT;

    public final static int MAXMOVES = 99, FILLERSIZE = 10;

    public Screen levelSelectScreen, levelEditorScreen;
    public InGameScreen ingameScreen;

    public GameRenderer renderer;
    public InputHandler inputs = new InputHandler(this);
    SaveAndLoad saveData = new SaveAndLoad();

    private LinkedList<String> levelNames;
    public Level[] levels;

    public static HashMap<String, ObjectDef> objectDefinitions = new HashMap<String, ObjectDef>();

    public GameData(Game screenControl){
        //Change Log
        //ChangeLogAdd("+ New levels");
        //Gdx.app.log("GameWorld", "ChangeLog = " + changeLog);

        SetObjectDefinitions();

        levelNames = new LinkedList<String>();

        FileHandle levelOrder = Gdx.files.internal("levels/levelConfig.txt");
        BufferedReader orderReader = new BufferedReader(levelOrder.reader());

        String next = "";

        do {
            try {
                next = orderReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gdx.app.log("GameData", next);

            if (next != null) {
                levelNames.add(next.trim());
            }

        } while (next != null);

        //Helper References
        renderer = new GameRenderer(this);
        saveData.loadCurrentGame();
        Gdx.input.setInputProcessor(inputs);

        //Levels
        levels = LevelConfig.ConfigureLevels(this, levelNames);


        //Initialize Screens
        levelSelectScreen = new LevelSelectScreen(this, screenControl);
        ingameScreen = new InGameScreen(this, screenControl);
        levelEditorScreen = new LevelEditorScreen(this, screenControl);
    }

    void SetObjectDefinitions(){
        objectDefinitions.put("axe", new ObjectDef().Parameter("gravity", 1).Parameter("pickup", 1)
                            .Parameter("cut", 1).Animation(1, "axe"));

        objectDefinitions.put("builder", new ObjectDef().Parameter("gravity", 1).Parameter("npc", 1));

        objectDefinitions.put("bird", new ObjectDef().Parameter("pickup", 1).Parameter("npc",1)
                                .Animation(1, "bird_stand").Animation(2, "bird_fly"));

        objectDefinitions.put("carrot", new ObjectDef().Parameter("pickup", 1));

        objectDefinitions.put("churchcross", new ObjectDef());

        objectDefinitions.put("dog", new ObjectDef().Parameter("gravity", 1).Parameter("npc", 1));

        objectDefinitions.put("door", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("fragilestone", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("grass", new ObjectDef().Parameter("wall",1)

                                    .Animation(1, "grass"));

        objectDefinitions.put("key", new ObjectDef().Parameter("pickup",1));

        objectDefinitions.put("ladder", new ObjectDef().Parameter("ladder",1));

        objectDefinitions.put("lever", new ObjectDef().Parameter("interact",1));

        objectDefinitions.put("leaf", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("openstone", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("platform", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("paradoxlever", new ObjectDef().Parameter("interact",1));

        objectDefinitions.put("paradoxplatform", new ObjectDef().Parameter("wall",1));
        objectDefinitions.put("pressureplate", new ObjectDef());

        objectDefinitions.put("rabbit", new ObjectDef().Parameter("gravity",1).Parameter("npc",1));

        objectDefinitions.put("seed", new ObjectDef().Parameter("pickup",1));

        objectDefinitions.put("stone", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("tallgrass", new ObjectDef());

        objectDefinitions.put("tree", new ObjectDef().Parameter("wall",3).Parameter("grow_state", 1).Parameter("cut", 2)
                                .Animation(-1, "tree_cut").Animation(2, "shrub").Animation(3, 4, "tree"));

        objectDefinitions.put("vines", new ObjectDef().Parameter("ladder",1));

        objectDefinitions.put("water", new ObjectDef().Parameter("water",1));

        objectDefinitions.put("woodenplank", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("woodencrate", new ObjectDef().Parameter("wall",1).Parameter("push",1));

        objectDefinitions.put("yellowbrick", new ObjectDef().Parameter("wall",1));
    }

    public void dispose(){
        renderer.dispose();
    }

    public int NumberOfLevels() { return levelNames.size(); }
}
