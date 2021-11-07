package com.mrchocolatesalmon.pastpeter.gameworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.ObjectDef;
import com.mrchocolatesalmon.pastpeter.helpers.*;
import com.mrchocolatesalmon.pastpeter.screens.*;

import javax.management.ReflectionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class GameData {

    public final static int GAMESIZE = 64;
    public final static int GAMEHEIGHT = 9;
    public final static int GAMEWIDTH = 11;
    public final int CAMWIDTH = GAMEWIDTH * GAMESIZE, CAMHEIGHT = GAMEHEIGHT * GAMESIZE;
    public final float CAMRATIO = (float) CAMWIDTH / CAMHEIGHT;

    public final static int MAXMOVES = 99, FILLERSIZE = 10;

    public float screenWidth, screenHeight;

    public Screen levelSelectScreen, levelEditorScreen;
    public InGameScreen ingameScreen;
    public WinScreen winScreen;

    public GameRenderer renderer;
    public InputHandler inputs = new InputHandler(this);
    SaveAndLoad saveData = new SaveAndLoad();

    private LinkedList<String> levelNames;
    public Level[] levels;

    public static HashMap<String, ObjectDef> objectDefinitions = new HashMap<String, ObjectDef>();
    public static LinkedList<String> objectNameStored;

    public static ObjectDef getObjectDefinition(String objectName){
        if (!GameData.objectDefinitions.containsKey(objectName)){ Gdx.app.error("GameData", "No object def named: " + objectName); return null;}

        return GameData.objectDefinitions.get(objectName).CloneObjectDef();
    }

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
        winScreen = new WinScreen(this, screenControl);
        levelEditorScreen = new LevelEditorScreen(this, screenControl);
    }

    public float MouseX(){
        return (float) (Gdx.input.getX()) * ((GAMESIZE * GAMEWIDTH) / screenWidth);
    }

    public float MouseY(){
        return (float) (Gdx.input.getY()) * ((GAMESIZE * GAMEHEIGHT) / screenHeight);
    }

    void SetObjectDefinitions(){

        //Lookup table for quick reference to an object name for spawning by another object
        objectNameStored = new LinkedList<String>( Arrays.asList("leaf") );

        //Object Definitions for creating new objects in a level
        objectDefinitions.put("axe", new ObjectDef().Parameter("gravity", 1).Parameter("pickup", 1)
                            .Parameter("cut", 1).Animation(1, "axe"));

        objectDefinitions.put("builder", new ObjectDef().Parameter("gravity", 1).Parameter("npc", 1));

        objectDefinitions.put("bird", new ObjectDef().Parameter("pickup", 1).Parameter("npc",1)
                                .Animation(1, "bird_stand").Animation(2, "bird_fly")
                                .NPC(new ObjectDef.NPCDef().set_flyDownTo(new String[] {"leaf"}, 3))
                                .NPC(new ObjectDef.NPCDef().set_moveDirection(new Vector2(-1, 0))));

        objectDefinitions.put("carrot", new ObjectDef().Parameter("pickup", 4)
                                .Animation(1, "carrot_down").Animation(2, "carrot_up"));

        objectDefinitions.put("churchcross", new ObjectDef());

        objectDefinitions.put("dog", new ObjectDef().Parameter("gravity", 1).Parameter("npc", 1).Parameter("grow_state", 1)
                                    .AnimationRange(1, 2, "dog_idle").Animation(3, "dog_savage_idle")
                                    .NPC(new ObjectDef.NPCDef().set_huntIfState(3)));

        objectDefinitions.put("door", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("fragilestone", new ObjectDef().Parameter("wall",1).Animation(1, "fragilestone1")
                                .Animation(2, "fragilestone2"));

        objectDefinitions.put("grass", new ObjectDef().Parameter("wall",1)
                                    .Animation(1, "grass"));

        objectDefinitions.put("key", new ObjectDef().Parameter("pickup",1));

        objectDefinitions.put("ladder", new ObjectDef().Parameter("ladder",1).Parameter("grow_state", 1)
                            .AnimationRange(1, 2, "ladder"));

        objectDefinitions.put("lever", new ObjectDef().Parameter("interact",1).InteractLink("platform")
                            .Animation(1, "lever_brown_off", 0).Animation(1, "lever_yellow_off", 1)
                            .Animation(2, "lever_brown_on",  0).Animation(2, "lever_yellow_on",  1));
        objectDefinitions.put("leaf", new ObjectDef().Parameter("wall",3).AnimationRange(3, 6, "leaf"));

        objectDefinitions.put("openstone", new ObjectDef().Parameter("wall",1));

        objectDefinitions.put("platform", new ObjectDef().Parameter("wall",1).Parameter("interact", -2)
                                .AnimationRange(1,2, "platform_brown", 0).AnimationRange(1, 2, "platform_yellow", 1));

        objectDefinitions.put("paradoxlever", new ObjectDef().Parameter("interact",1));

        objectDefinitions.put("paradoxplatform", new ObjectDef().Parameter("wall",1));
        objectDefinitions.put("pressureplate", new ObjectDef().Animation(1, "pressureplate"));

        objectDefinitions.put("rabbit", new ObjectDef().Parameter("gravity",1).Parameter("npc",1).Animation(1, "rabbit")
                                .NPC(new ObjectDef.NPCDef().set_moveToTargets(new String[] {"carrot"}, 5, true)));

        objectDefinitions.put("seed", new ObjectDef().Parameter("pickup",3).Parameter("wall",3).Parameter("grow_state", 2).
                                Animation(1, "seed").Animation(-2, "tree_cut").Animation(2, "shrub").
                                AnimationRange(3, 4, "tree").Connection("leaf", 0, -1, true));

        objectDefinitions.put("stone", new ObjectDef().Parameter("wall",1).Animation(1, "stone"));

        objectDefinitions.put("tallgrass", new ObjectDef());

        objectDefinitions.put("tree", new ObjectDef().Parameter("wall",3).Parameter("grow_state", 1).Parameter("cut", 2)
                                .Connection("leaf", 0, -1, true).Animation(-2, "tree_cut").Animation(2, "shrub")
                                .AnimationRange(3, 4, "tree"));

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
