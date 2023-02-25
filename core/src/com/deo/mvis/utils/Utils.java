package com.deo.mvis.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Utils {
    private static final Preferences prefs = Gdx.app.getPreferences("MvisPrefs");
    
    private final int step;
    private final int FPS;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final MusicWave musicWave;
    private final float[] smoothedSamples;
    
    public Utils(int FPS, int step, MusicWave musicWave, float[] smoothedSamples, int bloomPasses, float bloomIntensity, float bloomSaturation, boolean enableBloom, SpriteBatch batch) {
        this.step = step;
        this.FPS = FPS;
        this.musicWave = musicWave;
        this.batch = batch;
        this.smoothedSamples = smoothedSamples;
        font = new BitmapFont(Gdx.files.internal("font2(old).fnt"));
    }
    
    private float computeTime(int recorderFrame) {
        return Gdx.graphics.getDeltaTime() * (smoothedSamples.length / (float) step - recorderFrame) / 3600;
    }
    
    public static int getRandomInRange(int min, int max) {
        return (MathUtils.random(max - min) + min);
    }
    
    public static float getFloat(String key) {
        return prefs.getFloat(key);
    }
    
    public static void putFloat(String key, float val) {
        prefs.putFloat(key, val);
        prefs.flush();
    }
    
    public static boolean getBoolean(String key) {
        return (prefs.getBoolean(key));
    }
    
    public static void putBoolean(String key, boolean val) {
        prefs.putBoolean(key, val);
        prefs.flush();
    }
    
    public static void putInteger(String key, int val) {
        prefs.putInteger(key, val);
        prefs.flush();
    }
    
    public static int getInteger(String key) {
        return (prefs.getInteger(key));
    }
    
    public static void deleteKey(String key) {
        prefs.remove(key);
        prefs.flush();
    }
    
    public void dispose() {
        font.dispose();
    }
    
}
