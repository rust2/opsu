package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fluddokt.opsu.fake.GameOpsu;

public class DesktopLauncher {
    static LwjglApplicationConfiguration config;

    public static void main(String[] arg)
    {
        config = new LwjglApplicationConfiguration();
        //config.resizable = false;
        config.addIcon("res/icon32.png", Files.FileType.Internal);
        config.addIcon("res/icon16.png", Files.FileType.Internal);
        config.vSyncEnabled = false;
        config.foregroundFPS = 240;
        config.backgroundFPS = 30;
        //config.samples = 2;
        //config.audioDeviceBufferCount=240;
        /*DeviceInfo.info = new DeviceInfo() {
            @Override
            public void setFPS(int targetFPS) {
                config.foregroundFPS = targetFPS;
            }
        };*/
        new LwjglApplication(new GameOpsu(), config);
    }
}
