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
    private static Texture backgroundTexture, logoTexture, miscTexture;
    public static TextureRegion bgPast, bgPresent, bgFuture, logo;
    public static TextureRegion levelBtn;

    private static Texture objectTexture;
    private static HashMap<String, Animation> objectTextureMap;

    private static Texture peterTexture;
    private static HashMap<String, TextureRegion> playerTextureMap;

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
    }

    private static void loadIngameAssets() {
        objectTextureMap = new HashMap<String, Animation>();

        objectTexture = new Texture(Gdx.files.internal("data/objecttexture.png"));
        objectTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        TextureRegion temp;

        objectTextureMap.put("tree", makeAnimation(objectTexture, 0, 2, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("tree_cut", makeAnimation(objectTexture, 0, 3, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("shrub", makeAnimation(objectTexture, 0, 4, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("tree_future", makeAnimation(objectTexture, 0, 6, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("axe", makeAnimation(objectTexture, 3, 1, 3, 0.2f, PlayMode.LOOP_PINGPONG));
        objectTextureMap.put("grass", makeAnimation(objectTexture, 6, 1, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("bird_stand", makeAnimation(objectTexture, 7, 3, 1, 0f, PlayMode.NORMAL));
        objectTextureMap.put("bird_fly", makeAnimation(objectTexture, 7, 4, 2, 0.4f, PlayMode.NORMAL));
    }

    private static Animation makeAnimation(Texture tex, int y, int startX, int count, float speed, PlayMode playMode) {

        int texSize = 64;

        TextureRegion[] frames = new TextureRegion[count];

        for (int i = 0; i < count; i++) {
            TextureRegion temp = new TextureRegion(objectTexture, (startX+i)*texSize, y*texSize, texSize, texSize);
            temp.flip(false, true);

            frames[i] = temp;
        }

        Animation anim = new Animation(speed, frames);
        anim.setPlayMode(playMode);
        return anim;
    }

    private static void loadPlayerAssets() {

        //CAN WE MAKE THESE TEXTURES BIGGER? AND SCALE THEM DOWN?? yes...

        playerTextureMap = new HashMap<String, TextureRegion>();

        peterTexture = new Texture(Gdx.files.internal("data/playertexture.png"));
        peterTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        TextureRegion peter1 = new TextureRegion(peterTexture, 0, 0, 32, 32);
        playerTextureMap.put("peter_idle", peter1);
    }

    public static Animation getIngameTexture(String key){
        return objectTextureMap.get(key);
    }

    public static TextureRegion getPlayerTexture(String key){
        return playerTextureMap.get(key);
    }
}
