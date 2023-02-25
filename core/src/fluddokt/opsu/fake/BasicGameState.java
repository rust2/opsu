package fluddokt.opsu.fake;

import kww.useless.interfaces.IResizable;

public abstract class BasicGameState implements IResizable {

    //kww: pulled them from children classes
    protected GameContainer container;
    protected StateBasedGame game;
    protected Input input;
    protected int state;

    public int getID()
    {
        return state;
    }

    boolean inited = false;

    public void keyPressed(int key, char c) {}

    public void init(GameContainer container, StateBasedGame game) throws SlickException
    {
        Graphics.getGraphics().registerResizable(this);
    }

    public void render(StateBasedGame game, Graphics g) throws SlickException {}

    public void update(StateBasedGame game, int delta) throws SlickException {}

    public void mousePressed(int button, int x, int y) {}

    public void enter(StateBasedGame game) throws SlickException {}

    public void mouseDragged(int oldx, int oldy, int newx, int newy) {}

    public void mouseWheelMoved(int newValue) {}

    public void leave(StateBasedGame game) throws SlickException {}

    public void keyReleased(int key, char c) {}

    public void mouseClicked(int button, int x, int y, int clickCount) {}

    public void mouseMoved(int oldx, int oldy, int newx, int newy) {}

    public void mouseReleased(int button, int x, int y) {}

    //kww
    public void resize() {}
}
