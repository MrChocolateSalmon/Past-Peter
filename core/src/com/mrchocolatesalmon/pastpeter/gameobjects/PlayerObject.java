package com.mrchocolatesalmon.pastpeter.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrchocolatesalmon.pastpeter.datastructures.*;
import com.mrchocolatesalmon.pastpeter.enums.PlayerID;
import com.mrchocolatesalmon.pastpeter.enums.TimeID;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;
import com.mrchocolatesalmon.pastpeter.gameworld.Level;
import com.mrchocolatesalmon.pastpeter.helpers.AssetLoader;

import java.util.HashMap;
import java.util.LinkedList;

public class PlayerObject {

    protected PlayerID playerID;

    protected HashMap<TimeID,TimePosition[]> positionArray = new HashMap<TimeID, TimePosition[]>();
    protected HashMap<TimeID, CommandInfo[]> commands = new HashMap<TimeID, CommandInfo[]>();
    protected LinkedList<PlayerInterrupt> playerInterrupts = new LinkedList<PlayerInterrupt>();

    public HashMap<TimeID, HashMap<String, String>> textureMap = new HashMap<TimeID, HashMap<String, String>>();

    protected Level level;
    protected HashMap<TimeID, Vector2> targets = new HashMap<TimeID, Vector2>();

    protected LinkedList<AnimationCommand> animationList = new LinkedList<AnimationCommand>(), animationBacklog = new LinkedList<AnimationCommand>();
    protected AnimationCommand currentAnimation = new AnimationCommand(new Vector2(), null, "");
    protected float renderLerp = 0;

    protected TimePosition lastTimeUpdatePosition = new TimePosition(0,0,0);

    public PlayerObject(Vector2 startPast, Vector2 startPresent, Vector2 startFuture, Vector2 endFuture,
                            PlayerID id, Level level){

        playerID = id;
        this.level = level;

        textureMap.put(TimeID.past, new HashMap<String, String>());
        textureMap.put(TimeID.present, new HashMap<String, String>());
        textureMap.put(TimeID.future, new HashMap<String, String>());

        //Past
        TimePosition[] positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        TimePosition startPosition = new TimePosition((int)startPast.x, (int)startPast.y, 1);
        CommandInfo[] commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandInfo.CommandID.wait, null);
        }
        positionArray.put(TimeID.past, positions);
        commands.put(TimeID.past, commandsArray);

        //Present
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startPresent.x, (int)startPresent.y, 1);
        commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandInfo.CommandID.wait, null);
        }
        positionArray.put(TimeID.present, positions);
        commands.put(TimeID.present, commandsArray);

        //Future
        positions = new TimePosition[GameData.MAXMOVES + GameData.FILLERSIZE];
        startPosition = new TimePosition((int)startFuture.x, (int)startFuture.y, 1);
        commandsArray = new CommandInfo[GameData.MAXMOVES + GameData.FILLERSIZE];
        for (int i = 0; i < GameData.MAXMOVES + GameData.FILLERSIZE; i++){
            positions[i] = startPosition.Clone();
            commandsArray[i] = new CommandInfo(CommandInfo.CommandID.wait, null);
        }
        positionArray.put(TimeID.future, positions);
        commands.put(TimeID.future, commandsArray);

        targets.put(TimeID.past, startPresent);
        targets.put(TimeID.present, startFuture);
        targets.put(TimeID.future, endFuture);
    }

    public String playerIDToString(){
        return playerID.toString();
    }

    public boolean isAtTarget(TimeID timeID){
        TimePosition position = getPosition(timeID, level.getCurrentTime(timeID));
        Vector2 targetPosition = targets.get(timeID);

        if (position.aliveStatus <= 0){ return false; }

        return position.x == targetPosition.x && position.y == targetPosition.y;
    }

    public void setCommand(TimeID timeID, int time, CommandInfo command){
        commands.get(timeID)[time] = command;
    }

    public TimePosition getPosition(TimeID timeID, int time){
        return positionArray.get(timeID)[time];
    }

    public IngameObject getHolding(TimeID timeID, int time){ return getPosition(timeID,time).holding; }

    private void interact(TimeID timeID, int time) {

        IngameObject interactable = level.getObjectWithParameter("interact", getPosition(timeID, time).vector2(), timeID, time);

        if (interactable != null){
            interactable.sendInterrupt(new IngameInterrupt(Interrupt.InterruptID.interact, this), timeID, time);
            //Gdx.app.log("PlayerObject", "Send interact interrupt to: " + interactable.nameID);
        }
    }

    private void drop(TimeID timeID, int time){
        IngameObject obj = getHolding(timeID, time);

        if (obj != null){
            obj.sendInterrupt(new IngameInterrupt(Interrupt.InterruptID.drop, this), timeID, time);
            positionArray.get(timeID)[time].holding = null;
        }
    }

    //Only drop the object if it's the object being requested to drop
    public void drop(TimeID timeID, int time, IngameObject dropObj){
        IngameObject obj = getHolding(timeID, time);

        if (obj == dropObj && obj != null){
            obj.sendInterrupt(new IngameInterrupt(Interrupt.InterruptID.drop, this), timeID, time);
            positionArray.get(timeID)[time].holding = null;
        }
    }

    public void hold(IngameObject obj, TimeID timeID, int time){
        IngameObject currentHolding = positionArray.get(timeID)[time].holding;

        if (currentHolding != null && currentHolding != obj){ drop(timeID, time); } //Drop anything the player is already holding
        positionArray.get(timeID)[time].holding = obj; //Carry this new object
    }

    public void useItem(TimeID timeID, int time, Vector2 targetPos){
        IngameObject obj = getHolding(timeID, time);

        if (obj != null) {
            obj.sendInterrupt(new IngameInterrupt(Interrupt.InterruptID.use, this, targetPos), timeID, time);
        }
    }

    public void sendInterrupt(TimeID timeID, int time, PlayerInterrupt interrupt) {
        playerInterrupts.add(0, interrupt);

        Gdx.app.log("PlayerObject", playerID.toString() + ": received " + interrupt.interruptID.toString() + " interrupt at " + timeID.toString() + " " + String.valueOf(time));
    }

    public void clearInterrupts(){
        playerInterrupts.clear();
    }

    public void prepareForInterrupts(TimeID timeID, int time) {

        TimePosition[] currentPositions = positionArray.get(timeID);
        TimePosition currentPosition = currentPositions[time];
        currentPosition.copyValues(lastTimeUpdatePosition);
    }

    public void processInterrupts(TimeID timeID, int time, boolean lastLoop, boolean isCurrentMoment){

        TimePosition[] currentPositions = positionArray.get(timeID);
        TimePosition currentPosition = currentPositions[time];

        for (int i = playerInterrupts.size() - 1; i >= 0; i--) {

            PlayerInterrupt interrupt = playerInterrupts.get(0);

            switch (interrupt.interruptID) {
                case setAliveStatus:
                    currentPosition.aliveStatus = interrupt.value;
                    if (interrupt.targetPos.x >= 0) {
                        currentPosition.x = (int) interrupt.targetPos.x;
                        currentPosition.y = (int) interrupt.targetPos.y;
                    }

                    Gdx.app.log("PlayerObject", "lastLoop = " + lastLoop);
                    Gdx.app.log("PlayerObject", "isCurrentMoment = " + isCurrentMoment);
                    if (lastLoop && isCurrentMoment) {
                        if (interrupt.value == 0) {
                           animationBacklog.add(new AnimationCommand(currentPosition.vector2(), AnimationCommand.TransitionType.none, textureMap.get(timeID).get("dead")));
                           Gdx.app.log("PlayerObject", "dead");
                        }
                    }
            }

            playerInterrupts.remove(i);
        }
    }

    public void timeUpdate(TimeID timeID, int time, TimeID previousTimeID, boolean currentMoment){

        TimePosition[] currentPositions = positionArray.get(timeID);
        TimePosition[] earlierTimePositions = positionArray.get(previousTimeID);

        TimePosition currentPosition = currentPositions[time];

        if (currentPosition.aliveStatus > 0) {

            if (time > 0) {
                TimePosition previousPosition = currentPositions[time - 1];
                currentPosition.copyValues(previousPosition);

                CommandInfo command = commands.get(timeID)[time];
                if (time > level.getCurrentTime(timeID)) {
                    command.commandID = CommandInfo.CommandID.wait;
                }

                switch (command.commandID) {
                    case wait:
                        break;
                    case move:
                        currentPosition.x += (int) command.pos.x;
                        currentPosition.y += (int) command.pos.y;
                        if (currentMoment) {
                            animationBacklog.add(new AnimationCommand(previousPosition.vector2(), AnimationCommand.TransitionType.lerp, textureMap.get(timeID).get("idle")));
                            animationBacklog.add(new AnimationCommand(currentPosition.vector2(), AnimationCommand.TransitionType.none, textureMap.get(timeID).get("idle")));
                        }
                        break;
                    case pickup:
                        IngameObject potentialHolding = level.getObjectWithParameter("pickup", new Vector2(currentPosition.x, currentPosition.y), timeID, time);
                        if (potentialHolding != null) {
                            potentialHolding.sendInterrupt(new IngameInterrupt(Interrupt.InterruptID.pickup, this), timeID, time);
                        }
                        break;
                    case drop:
                        drop(timeID, time);
                        break;
                    case interact:
                        interact(timeID, time);
                        break;
                    case use:
                        useItem(timeID, time, command.pos);
                        break;
                }

            } else if (timeID != TimeID.past) {
                TimePosition previousPosition = earlierTimePositions[level.getCurrentTime(previousTimeID) + GameData.FILLERSIZE - 1];
                currentPosition.aliveStatus = previousPosition.aliveStatus;
                currentPosition.holding = previousPosition.holding;
            }
        }

        lastTimeUpdatePosition.copyValues(currentPosition);
    }

    public void pushAnimationList(){
        for (int i = 0; i < animationBacklog.size(); i++) {
            animationList.add(animationBacklog.get(i));
        }

        animationBacklog.clear();
    }

    public void flushAnimationList(TimeID timeID, int time, boolean completeFlush){

        AnimationCommand anim0 = null;
        if (!completeFlush){
            anim0 = animationList.get(0);
            anim0.transition = AnimationCommand.TransitionType.lerp;
        }

        animationList.clear();

        if (!completeFlush){ animationList.add(anim0); }

        TimePosition position = getPosition(timeID, level.getCurrentTime());
        animationList.add(new AnimationCommand(position.vector2(), AnimationCommand.TransitionType.none, textureMap.get(timeID).get("idle")));

        if (completeFlush){ currentAnimation.copyVariables(animationList.get(0)); }
    }

    public void render(SpriteBatch batcher, float delta) {

        TimeID timeID = level.getCurrentTimeID();

        Vector2 targetPosition = targets.get(timeID);

        if (animationList.size() < 1){ flushAnimationList(level.getCurrentTimeID(), level.getCurrentTime(), true); }

        if (animationList.size() > 1){
            AnimationCommand thisAnimation = animationList.get(0);
            AnimationCommand nextAnimation = animationList.get(1);

            switch (thisAnimation.transition) {
                case lerp:
                    currentAnimation.position.x = thisAnimation.position.x;
                    currentAnimation.position.y = thisAnimation.position.y;
                    currentAnimation.position.lerp(nextAnimation.position, renderLerp);

                    renderLerp = Math.min(1, renderLerp + (delta*3f));
                    if (renderLerp >= 1) {
                        renderLerp = 0;
                        animationList.remove(0);
                        currentAnimation.copyVariables(animationList.get(0));
                    }
                    break;

                default:
                    renderLerp = 0;
                    animationList.remove(0);
                    currentAnimation.copyVariables(animationList.get(0));
                    break;
            }
        }

        if (currentAnimation.animName != null) {
            Animation anim = AssetLoader.getPlayerTexture(currentAnimation.animName);

            //Gdx.app.log("PlayerObject", "animName = " + animName);
            //Gdx.app.log("PlayerObject", "anim = " + anim.toString());

            if (anim != null) {
                batcher.draw((TextureRegion) anim.getKeyFrame(level.levelAge), currentAnimation.position.x * GameData.GAMESIZE, currentAnimation.position.y * GameData.GAMESIZE);
            }
        }

        //Draw end point
        Animation endPointAnim = AssetLoader.getPlayerTexture(textureMap.get(timeID).get("endpoint"));
        batcher.draw((TextureRegion) endPointAnim.getKeyFrame(level.levelAge), targetPosition.x * GameData.GAMESIZE, targetPosition.y * GameData.GAMESIZE);
    }

    public boolean checkCollision(Vector2 checkPosition, TimeID timeID, int time){

        TimePosition myPos = positionArray.get(timeID)[time];

        //Check for collisions with other characters
        for (int p = 0; p < level.players.size(); p++){
            PlayerObject tempPlayer = level.players.get(p);

            if (tempPlayer != this){
                TimePosition tempPos = tempPlayer.positionArray.get(timeID)[time];

                if (tempPos.x == checkPosition.x && tempPos.y == checkPosition.y) {
                    return true;
                }
            }
        }

        for (int i = 0; i < level.objects.size(); i++){
            IngameObject obj = level.objects.get(i);

            TimePosition objPos = obj.positionArray.get(timeID)[time];

            //If object is at position
            if (objPos.x == checkPosition.x && objPos.y == checkPosition.y){

                int wallType = obj.parameterValue("wall");

                //If object is currently a wall (blocking movement)
                if (wallType != 0 && objPos.aliveStatus >= wallType){
                    return true;
                }
            }
        }

        return false;
    }

}
