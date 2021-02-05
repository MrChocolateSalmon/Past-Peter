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
        objectDefinitions.put("axe", new ObjectDef().Tags(new String[]{"gravity", "pickup"})
                                .Animation(1, "axe"));

        objectDefinitions.put("builder", new ObjectDef().Tags(new String[]{"gravity", "npc"}));
        objectDefinitions.put("bird", new ObjectDef().Tags(new String[]{"pickup", "npc"})
                                .Animation(1, "bird_stand").Animation(2, "bird_fly"));

        objectDefinitions.put("carrot", new ObjectDef().Tags(new String[]{"pickup"}));
        objectDefinitions.put("churchcross", new ObjectDef().Tags(new String[]{"pickup"}));
        objectDefinitions.put("dog", new ObjectDef().Tags(new String[]{"gravity",  "npc"}));;
        objectDefinitions.put("door", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("fragilestone", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("grass", new ObjectDef().Tags(new String[]{"wall"})
                                    .Animation(1, "grass"));

        objectDefinitions.put("key", new ObjectDef().Tags(new String[]{"pickup"}));
        objectDefinitions.put("ladder", new ObjectDef().Tags(new String[]{"ladder"}));
        objectDefinitions.put("lever", new ObjectDef().Tags(new String[]{"interact"}));
        objectDefinitions.put("leaf", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("openstone", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("platform", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("paradoxlever", new ObjectDef().Tags(new String[]{"interact"}));
        objectDefinitions.put("paradoxplatform", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("pressureplate", new ObjectDef().Tags(new String[]{}));
        objectDefinitions.put("rabbit", new ObjectDef().Tags(new String[]{"gravity", "npc"}));
        objectDefinitions.put("seed", new ObjectDef().Tags(new String[]{"pickup"}));
        objectDefinitions.put("stone", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("tallgrass", new ObjectDef().Tags(new String[]{}));
        objectDefinitions.put("tree", new ObjectDef().Tags(new String[]{"wall"})
                                .Animation(-1, "tree_cut").Animation(2, "shrub").Animation(3, "tree"));

        objectDefinitions.put("vines", new ObjectDef().Tags(new String[]{"ladder"}));
        objectDefinitions.put("water", new ObjectDef().Tags(new String[]{"water"}));
        objectDefinitions.put("woodenplank", new ObjectDef().Tags(new String[]{"wall"}));
        objectDefinitions.put("woodencrate", new ObjectDef().Tags(new String[]{"wall", "push"}));
        objectDefinitions.put("yellowbrick", new ObjectDef().Tags(new String[]{"wall"}));
    }

    public void dispose(){
        renderer.dispose();
    }

    public int NumberOfLevels() { return levelNames.size(); }
}
