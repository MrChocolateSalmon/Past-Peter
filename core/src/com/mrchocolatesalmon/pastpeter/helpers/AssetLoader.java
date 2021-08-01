package com.mrchocolatesalmon.pastpeter.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mrchocolatesalmon.pastpeter.gameworld.GameData;

import java.util.HashMap;

public class AssetLoader {
    private static Texture backgroundTexture, logoTexture, miscTexture, uiTexture;
    public static TextureRegion bgPast, bgPresent, bgFuture, logo;
    public static TextureRegion levelBtn;
    public static TextureRegion winBorder;

    private static Texture objectTexture;
    private static HashMap<String, Animation> objectTextureMap;

    private static Texture peterTexture;
    private static HashMap<String, Animation> playerTextureMap;

    public static void load() {

        loadBackgroundAndLogo();
        loadIngameAssets();
        loadPlayerAssets();
    }

    public static void dispose(){
        backgroundTexture.dispose();
        logoTexture.dispose();
        miscTexture.dispose();
        peterTexture.dispose();
        objectTexture.dispose();
    }

    private static void loadBackgroundAndLogo(){
        backgroundTexture = new Texture(Gdx.files.internal("data/backgroundtexturesky.png"));
        backgroundTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        bgPast = new TextureRegion(backgroundTexture, 0, 0, 352, 265);
        bgPast.flip(false, true);

        bgPresent = new TextureRegion(backgroundTexture, 353, 0, 352, 265);
        bgPresent.flip(false, true);

        bgFuture = new TextureRegion(backgroundTexture, 706, 0, 352, 265);
        bgFuture.flip(false, true);


        logoTexture = new Texture(Gdx.files.internal("data/logotexture.png"));
        logoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        logo = new TextureRegion(logoTexture, 0, 0, 412, 72);
        logo.flip(false, true);


        miscTexture = new Texture(Gdx.files.internal("data/misctexture.png"));
        miscTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        levelBtn = new TextureRegion(miscTexture, 96, 0, 32, 32);
        levelBtn.flip(false, true);

        uiTexture = new Texture(Gdx.files.internal("data/uitexture.png"));
        uiTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        winBorder = new TextureRegion(uiTexture, 0, 0, 160, 256);
        winBorder.flip(false, true);
    }

    private static void loadIngameAssets() {
        objectTextureMap = new HashMap<String, Animation>();

        objectTexture = new Texture(Gdx.files.internal("data/objecttexture.png"));
        objectTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        TextureRegion temp;

        objectTextureMap.put("leaf", makeAnimation(objectTexture,               0, 1, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("tree", makeAnimation(objectTexture,               0, 2, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("tree_cut", makeAnimation(objectTexture,           0, 3, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("shrub", makeAnimation(objectTexture,              0, 4, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("tree_future", makeAnimation(objectTexture,        0, 6, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("seed", makeAnimation(objectTexture,               1, 5, 2, 0.1f, PlayMode.LOOP));
        objectTextureMap.put("ladder", makeAnimation(objectTexture,             2, 1, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("axe", makeAnimation(objectTexture,                3, 1, 3, 0.2f, PlayMode.LOOP_PINGPONG));
        objectTextureMap.put("platform_brown", makeAnimation(objectTexture,     4, 1, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("platform_yellow", makeAnimation(objectTexture,    4, 2, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("lever_brown_off", makeAnimation(objectTexture,    4, 3, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("lever_brown_on", makeAnimation(objectTexture,     4, 4, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("lever_yellow_off", makeAnimation(objectTexture,   4, 5, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("lever_yellow_on", makeAnimation(objectTexture,    4, 6, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("stone", makeAnimation(objectTexture,              5, 1, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("fragilestone2", makeAnimation(objectTexture,      5, 2, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("fragilestone1", makeAnimation(objectTexture,      5, 3, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("pressureplate", makeAnimation(objectTexture,      5, 7, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("grass", makeAnimation(objectTexture,              6, 1, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("bird_stand", makeAnimation(objectTexture,         7, 3, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("bird_fly", makeAnimation(objectTexture,           7, 4, 2, 0.4f, PlayMode.NORMAL));
        objectTextureMap.put("rabbit", makeAnimation(objectTexture,             7, 5, 2, 0.4f, PlayMode.NORMAL));
        objectTextureMap.put("builder", makeAnimation(objectTexture,            7, 6, 2, 0.4f, PlayMode.NORMAL));
    }

    private static void loadPlayerAssets() {

        //CAN WE MAKE THESE TEXTURES BIGGER? AND SCALE THEM DOWN?? yes...

        playerTextureMap = new HashMap<String, Animation>();

        peterTexture = new Texture(Gdx.files.internal("data/playertexture.png"));
        peterTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        playerTextureMap.put("peter_past_idle", makeAnimation(peterTexture, 0, 1, 1, 0.2f, PlayMode.LOOP));
        playerTextureMap.put("peter_present_idle", makeAnimation(peterTexture, 1, 1, 1, 0.2f, PlayMode.LOOP));
        playerTextureMap.put("peter_future_idle", makeAnimation(peterTexture, 2, 1, 1, 0.2f, PlayMode.LOOP));

        playerTextureMap.put("peter_past_endpoint", makeAnimation(peterTexture, 0, 4, 1, 0.2f, PlayMode.LOOP));
        playerTextureMap.put("peter_present_endpoint", makeAnimation(peterTexture, 1, 4, 1, 0.2f, PlayMode.LOOP));
        playerTextureMap.put("peter_future_endpoint", makeAnimation(peterTexture, 2, 4, 1, 0.2f, PlayMode.LOOP));
    }

    private static Animation makeAnimation(Texture tex, int y, int startX, int count, float speed, PlayMode playMode) {

        int texSize = 64;

        TextureRegion[] frames = new TextureRegion[count];

        for (int i = 0; i < count; i++) {
            TextureRegion temp = new TextureRegion(tex, (startX+i)*texSize, y*texSize, texSize, texSize);
            temp.flip(false, true);

            frames[i] = temp;
        }

        Animation anim = new Animation(speed, frames);
        anim.setPlayMode(playMode);
        return anim;
    }

    public static Animation getIngameTexture(String key){
        return objectTextureMap.get(key);
    }

    public static Animation getPlayerTexture(String key){
        return playerTextureMap.get(key);
    }
}
