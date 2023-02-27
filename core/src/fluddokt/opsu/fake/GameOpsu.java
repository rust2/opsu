package fluddokt.opsu.fake;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import itdelatrisu.opsu.ErrorHandler;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Opsu;
import itdelatrisu.opsu.options.Options;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GameOpsu extends ApplicationAdapter {
    public final static String VERSION = "0.16.1a";
    public StateBasedGame game;

    private Stage stage;
    private Table table;
    private Skin skin;
    private static GameOpsu gameOpsu;

    private boolean inited = false;

    private int dialogCnt;

    private Label loadingLabel;

    //kww
    /** {@link Disposable}s those have to be disposed */
    private final Array<Disposable> disposables = new Array<>();

    public GameOpsu()
    {
        gameOpsu = this;
    }

    public static GameOpsu getInstance()
    {
        return gameOpsu;
    }

    @Override
    public void create()
    {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                ErrorHandler.error("** Uncaught Exception! **", e, true);
            }
        });

        if (!Gdx.files.isExternalStorageAvailable())
        {
            if (!Gdx.files.isLocalStorageAvailable())
            {
                error("No storage is available ... ????", null);
            }
            else
            {
                error("External Storage is not available. \n"
                              + "Using Local Storage instead.\n"
                              + Gdx.files.getLocalStoragePath(), null);
            }
        }

        Gdx.graphics.setVSync(false);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Graphics.init();

        loadingLabel = new Label("Loading...", skin);
        table.addActor(loadingLabel);

        Opsu.main(new String[0]);
    }

    @Override
    public void resize(int width, int height)
    {
        //kww: todo: find the appropriate way to set the minimal size of window with lwjgl2 being used
        Gdx.graphics.setWindowedMode(
                width < 800 ? width = 800 : width,
                height < 600 ? height = 600 : height
        );

        //stage.getViewport().setCamera(Graphics.camera);
        stage.getViewport().update(width, height, true);
        table.invalidate();
        if (!inited)
            return;
        game.container.width = width;
        game.container.height = height;

        Graphics.resize(width, height);

        System.out.println("Game resized: " + width + "x" + height);
    }

    int delayLoad = 0;

    @Override
    public void render()
    {
        if (delayLoad > 2 && dialogCnt == 0)
        {
            try
            {
                if (game == null)
                {
                    if (Gdx.graphics.getWidth() > Gdx.graphics.getHeight())
                    {
                        game = Opsu.start();
                        game.container.width = Gdx.graphics.getWidth();
                        game.container.height = Gdx.graphics.getHeight();

                        try
                        {
                            game.init();
                            kww.useless.Init.preInit(); //kww: ugly hook at game start
                        }
                        catch (SlickException e)
                        {
                            e.printStackTrace();
                            error("SlickErrorInit", e);
                        }

                        File dataDir = Options.DATA_DIR;
                        System.out.println("dataDir :" + dataDir + " " + dataDir.isExternal() + " " + dataDir.exists());
                        if (dataDir.isExternal())
                        {
                            File nomediafile = new File(dataDir, ".nomedia");
                            if (!nomediafile.exists())
                                new FileOutputStream(nomediafile.getIOFile()).close();
                        }
                        System.out.println("Local Dir:" + Gdx.files.getLocalStoragePath());
                        Gdx.input.setInputProcessor(new InputMultiplexer(stage, game));
                        inited = true;
                        table.removeActor(loadingLabel);

                        //on android it is never resized so we have to call it at least once
                        //let's keep this temporary fix
                        if(Gdx.app.getType() == Application.ApplicationType.Android)
                           Graphics.resize(game.container.width, game.container.height);
                    }
                }
                else
                {
                    Color bgcolor = Graphics.bgcolor;
                    if (bgcolor != null)
                        Gdx.gl.glClearColor(bgcolor.r, bgcolor.g, bgcolor.b, 1);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    try
                    {
                        if (game.container.exited)
                        {
                            game = null;
                            delayLoad = 0;
                            table.addActor(loadingLabel);
                        }
                        else
                            game.render();
                    }
                    catch (SlickException e)
                    {
                        e.printStackTrace();
                        error("SlickErrorRender", e);
                    }

                    Graphics.checkMode(Graphics.DrawMode.NONE);
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                error("RenderError", e);
            }
        }
        else
        {
            if (delayLoad <= 2)
                delayLoad++;
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        //table.debugAll();
    }

    @Override
    public void pause()
    {
        if (!inited)
            return;

        game.container.loseFocus();
        try
        {
            game.render();
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
        game.container.lostFocus();

        System.out.println("Game pause");
    }

    @Override
    public void resume()
    {
        if (!inited)
            return;

        game.container.focus();

        System.out.println("Game resume");
    }

    @Override
    public void dispose()
    {
        if (!inited)
            return;

        for (GameImage img : GameImage.values())
        {
            try
            {
                img.dispose();
            }
            catch (SlickException e)
            {
                e.printStackTrace();
            }
        }

        for (Disposable disposable : disposables)
        {
            disposable.dispose();
        }

        game.container.closing();

        System.out.println("We are closing game.\nSee you later!");
    }

    /** Automatically (???) dispose objects */
    public void registerDisposable(Disposable disposable)
    {
        disposables.add(disposable);
    }

    public static void error(String string, Throwable e)
    {
        gameOpsu.errorDialog(string, e);
    }

    private void errorDialog(final String string, final Throwable e)
    {
        dialogCnt++;
        String tbodyString = "X";
        if (e != null)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            tbodyString = sw.toString();
        }
        final String bodyString = tbodyString;
        Dialog dialog = new Dialog("ERROR", skin) {
            final String title = string;
            final String body = bodyString;

            @Override
            protected void result(Object object)
            {
                System.out.println(object);

                if ("CloseOpsu".equals(object))
                {
                    System.exit(0);
                }

                if ("R".equals(object))
                {
                    try
                    {
                        System.out.println("Reporting");
                        Desktop.getDesktop().browse(
                                ErrorHandler.getIssueURI(title, e, body)
                        );
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if ("S".equals(object))
                {

                }

                dialogCnt--;
                System.out.println("Dialog count:" + dialogCnt);
            }

        }.button("Ignore and Continue", "S")
                .button("Report on github", "R")
                .button("Close Opsu", "CloseOpsu");
        dialog.getContentTable().row();
        Label tex = new Label(string + "\n" + bodyString, skin);

        dialog.getContentTable().add(new ScrollPane(tex))
                .width(Gdx.graphics.getWidth())
                .height(Gdx.graphics.getHeight() - 60);
        dialog.pack();
        table.addActor(dialog);

        table.validate();
    }
}
