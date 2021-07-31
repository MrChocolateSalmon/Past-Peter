package com.mrchocolatesalmon.pastpeter.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.ObjectDef;
import com.mrchocolatesalmon.pastpeter.enums.BackgroundType;
import com.mrchocolatesalmon.pastpeter.enums.PlayerID;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameobjects.IngameObject;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.players.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Time;
import java.util.LinkedList;

public class LevelConfig {

    //Convert all level files into level objects
    public static Level[] ConfigureLevels(GameData gameData, LinkedList<String> levelNames){
        Level[] levels = new Level[levelNames.size()];

        for (int i = 0; i < levels.length; i++){
            levels[i] = ConfigNewLevel(gameData, i, levelNames.get(i));
        }

        return levels;
    }

    //Read each line of the level file and add objects accordingly
    static Level ConfigNewLevel(GameData gameData, int index, String levelName){
        Level newLevel = new Level(gameData, index);

        Gdx.app.log("LevelConfig", "*** " + levelName);

        FileHandle file = Gdx.files.internal("levels/" + levelName + ".txt");
        if (file != null){
            BufferedReader lineReader = new BufferedReader(file.reader());

            try {
                String line = null;

                line = lineReader.readLine();

                newLevel.name = (index+1) + " - " + line;

                Gdx.app.log("LevelConfig", "New Level Name = " + newLevel.name);

                do {
                    //Read next line of level file
                    line = lineReader.readLine();
                    if (line == null){ break; }

                    //Process the line
                    if (line.startsWith("settings")){

                        line = lineReader.readLine();
                        newLevel.timeAvailable.put(TimeID.past, line.startsWith("true"));

                        line = lineReader.readLine();
                        newLevel.timeAvailable.put(TimeID.present, line.startsWith("true"));

                        line = lineReader.readLine();
                        newLevel.timeAvailable.put(TimeID.future, line.startsWith("true"));

                        line = lineReader.readLine();
                        newLevel.platformActive = line.startsWith("true");

                        line = lineReader.readLine();
                        if (line.startsWith("sky")){ newLevel.SetBackground(BackgroundType.sky); }
                        else if (line.startsWith("wooden")){ newLevel.SetBackground(BackgroundType.wooden); }

                        Gdx.app.log("LevelConfig", "Applied Settings");

                    } else if (line.startsWith("hint")){
                        line = lineReader.readLine();

                        String hint = "";
                        while (!line.startsWith("endhint")){
                            hint += line;
                            line = lineReader.readLine();
                        }

                        newLevel.hint = hint;

                    } else if (line.startsWith("objects")){

                        //Begins loading all the objects
                        for (int y = 0; y < gameData.GAMEHEIGHT; y++){
                            for (int x = 0; x < gameData.GAMEWIDTH; x++){

                                line = lineReader.readLine();

                                if(!line.startsWith("null")) {

                                    String[] params = line.trim().split(":");
                                    String objectName = params[0];

                                    ObjectDef definition = GameData.getObjectDefinition(objectName);

                                    if (definition != null) {
                                        Gdx.app.log("LevelConfig", "Creating: " + objectName + " at " + x + "," + y);

                                        //Apply other parameters
                                        int i = 1;
                                        while (i < params.length) {

                                            String[] parameter_desc = params[i].trim().split(",");
                                            i++;

                                            definition.Parameter(parameter_desc[0], Integer.parseInt(parameter_desc[1]));
                                        }

                                        newLevel.AddObject(new Vector2(x, y), objectName, definition);
                                    }
                                }
                            }
                        }

                    } else if (line.startsWith("players")) {
                        line = lineReader.readLine();

                        while (line != null && !line.startsWith("endplayers")){

                            String[] params = line.trim().split(":");

                            String name = params[0];

                            String[] pastPositionS = params[1].split(",");
                            String[] presentPositionS = params[2].split(",");
                            String[] futurePositionS = params[3].split(",");
                            String[] endFuturePositionS = params[4].split(",");

                            Vector2 pastPosition = new Vector2(Integer.parseInt(pastPositionS[0]),Integer.parseInt(pastPositionS[1]));
                            Vector2 presentPosition = new Vector2(Integer.parseInt(presentPositionS[0]),Integer.parseInt(presentPositionS[1]));
                            Vector2 futurePosition = new Vector2(Integer.parseInt(futurePositionS[0]),Integer.parseInt(futurePositionS[1]));
                            Vector2 endFuturePosition = new Vector2(Integer.parseInt(endFuturePositionS[0]),Integer.parseInt(endFuturePositionS[1]));

                            if (name.equals("peter")){
                                newLevel.players.add(new Peter(pastPosition, presentPosition, futurePosition, endFuturePosition, PlayerID.peter, newLevel));
                            }


                            line = lineReader.readLine();
                        }
                    }


                } while (line != null && !line.startsWith("endlevel"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        newLevel.resetLevel();
        return newLevel;
    }
}
