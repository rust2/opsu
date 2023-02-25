package fluddokt.opsu.fake;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import fluddokt.opsu.fake.gui.GInputListener;
import fluddokt.opsu.fake.gui.GUIContext;

public class GameContainer extends GUIContext {
    public static StateBasedGame sbg;

    public int width = 800;
    public int height = 600;
    public boolean hasFocus = true;

    protected boolean running;
    protected boolean forceExit = true;
    boolean exited = false;

    public GameContainer(StateBasedGame game)
    {
        sbg = (StateBasedGame) game;
        sbg.setContainer(this);
    }

    protected boolean running()
    {
        return false;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void closing()
    {
        if (music != null)
        {
            music.dispose();
        }
        close_sub();
    }

    public void exit()
    {
        closing();
        if (forceExit)
        {
            Gdx.app.exit();
            System.exit(0);
        }
        exited = true;
    }

    protected void close_sub()
    {
    }


    public boolean hasFocus()
    {
        return hasFocus;
    }

    public void setTargetFrameRate(int targetFPS)
    {
        Gdx.graphics.setForegroundFPS(targetFPS);
    }

    static float musvolume;

    public void setMusicVolume(float musicVolume)
    {
        musvolume = musicVolume;
        if (music != null)
            music.setMusicVolume(musvolume);
    }

    public void setShowFPS(boolean b)
    {
        // TODO Auto-generated method stub

    }

    public void setAlwaysRender(boolean b)
    {
        // TODO Auto-generated method stub

    }

    public int getFPS()
    {
        return Gdx.graphics.getFramesPerSecond();
    }

    public Graphics getGraphics()
    {
        return Graphics.getGraphics();
    }

    public int getScreenWidth()
    {
        //region oldcode
		/*
		return Gdx.graphics.getDesktopDisplayMode().width;
		*/
        //endregion
        return Gdx.graphics.getDisplayMode().width;
    }

    public int getScreenHeight()
    {
        //region oldcode
		/*
		return Gdx.graphics.getDesktopDisplayMode().height;
		*/
        //endregion
        return Gdx.graphics.getDisplayMode().height;
    }

    public void setVSync(boolean b)
    {
        Gdx.graphics.setVSync(b);
    }

    public void start() throws SlickException
    {
        // TODO Auto-generated method stub

    }

    protected void updateAndRender(int delta) throws SlickException
    {
        // TODO Auto-generated method stub

    }

    static Music music;

    public static synchronized void setMusic(Music imusic)
    {
        if (music != null)
            music.dispose();
        music = imusic;
        music.setVolume(musvolume);
    }

    boolean musicWasPlaying = false;

    public void loseFocus()
    {
        hasFocus = false;
    }

    public void lostFocus()
    {
        if (music != null)
        {
            musicWasPlaying = music.playing();
            if (Gdx.app.getType() == ApplicationType.Android)
            {
                music.pause();
            }
        }
    }

    public void focus()
    {
        hasFocus = true;
        if (music != null && musicWasPlaying)
        {
            if (Gdx.app.getType() == ApplicationType.Android)
            {
                music.resume();
            }
        }
    }

    public void setForceExit(boolean b)
    {
        this.forceExit = b;
    }

    @Override
    public void addInputListener(GInputListener listener)
    {
        sbg.addInputListener(listener);
    }

    @Override
    public void removeInputListener(GInputListener listener)
    {
        sbg.removeInputListener(listener);
    }

    public void setUpdateOnlyWhenVisible(boolean b)
    {
        // TODO Auto-generated method stub

    }

    public void setDefaultMouseCursor()
    {
        //region oldcode
		/*
		Gdx.input.setCursorImage(null, 0, 0);
		*/
        //endregion
//        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
//        pixmap.setColor(0f, 0f, 0f, 0f);
//        pixmap.fill();
//        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, 0, 0));
//        pixmap.dispose();
        Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
    }

    public void setMouseCursor(Cursor cursor, int x, int y) throws SlickException
    {
        //region oldcode
		/*
		Gdx.input.setCursorImage(cursor.getPixmap(), x, y)
		*/
        //endregion
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor.getPixmap(), x, y));
    }

    /** @author kww */
    public void hideCursor()
    {
        Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.None);
    }

    public double getAspectRatio()
    {
        return (double) width / height;
    }

}
