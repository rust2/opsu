package kww.useless;

import fluddokt.opsu.fake.GameContainer;
import fluddokt.opsu.fake.GameOpsu;
import fluddokt.opsu.fake.Image;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Opsu;
import itdelatrisu.opsu.states.*;
import kww.opsu.states.MainMenu;
import kww.useless.buttons.toolbar.Toolbar;
import kww.useless.visuals.FFTScreen;
import kww.useless.visuals.FPSMeter;
import kww.useless.visuals.Playa;
import kww.useless.visuals.Profiling;
import lombok.SneakyThrows;

public class Instances {
    /** itdelatrisu aka Jeffrey Han */
    /* Game container */
    public static GameContainer container = Opsu.opsu.container;

    /* States */
    public static Splash splash;
    public static MainMenu mainMenu;
    public static ButtonMenu buttonMenu;
    public static SongMenu songMenu;
    public static Game game;
    public static GamePauseMenu gamePauseMenu;
    public static GameRanking gameRanking;
    public static DownloadsMenu downloadsMenu;
    public static CalibrateOffsetMenu calibrateOffsetMenu;

    /** kww */
    /* Objects */
    public static FFTScreen visualizer;
    public static FPSMeter fpsMeter; // init: itdelatrisu.opsu.ui.UI.java
    public static Playa player;

    public static Toolbar toolbar;
    public static Profiling profiler;

    /* Other instances */
    public static Image fallback_bg;

    /** Executed somewhere in {@link GameOpsu} init */
    @SneakyThrows
    public static void earlyInit() {
//        new AnyShitGenerator().render();
//        Thread.sleep(5000);
//        Gdx.app.exit();
    }

    public static void init()
    {
        /* Opsu! states has been initialized in Opsu main class */
        visualizer = new FFTScreen();
        toolbar = new Toolbar();
        player = new Playa();

        profiler = new Profiling();

        /* Other instances */
        fallback_bg = GameImage.PLAYER_DEFAULT_BG.getImage();
    }
}
