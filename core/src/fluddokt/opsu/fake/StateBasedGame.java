package fluddokt.opsu.fake;

import java.util.HashMap;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;

import fluddokt.newdawn.slick.state.transition.Transition;
import fluddokt.opsu.fake.gui.GInputListener;
import itdelatrisu.opsu.ui.Fonts;
import kww.useless.Instances;
import kww.useless.UselessUtils;

public abstract class StateBasedGame extends Game2 implements InputProcessor {

    public GameContainer container;
    final static BasicGameState EMPTY_STATE = new BasicGameState() {};
    BasicGameState currentState = EMPTY_STATE;
    BasicGameState nextState = null;
    BasicGameState oldState = null;
    HashMap<Integer, BasicGameState> stateList = new HashMap<>();
    LinkedList<BasicGameState> orderedStateList = new LinkedList<>();
    String title;
    LinkedList<GInputListener> inputListener = new LinkedList<>();
    boolean rightIsPressed;
    int touchX = 0;
    int touchY = 0;
    long touchTime;

    Transition enterTransition, leaveTransition;

    public StateBasedGame(String name)
    {
        this.title = name;
        Display.setTitle(name);
    }

    public BasicGameState getState(int state)
    {
        return stateList.get(state);
    }

    public void enterState(int newState)
    {
        enterState(newState, null, null);
    }

    public void enterState(int newState, Transition leaveT, Transition enterT)
    {
        this.enterTransition = enterT;
        this.leaveTransition = leaveT;
        oldState = currentState;
        currentState = EMPTY_STATE;
        nextState = stateList.get(newState);
    }

    private boolean enterNextState() throws SlickException
    {
        if (nextState != null)
        {
            if (container == null)
            {
                throw new Error("");
            }

            oldState.leave(this);
            currentState = nextState;
            nextState = null;
            touchX = 0;
            touchY = 0;

            if (!currentState.inited)
            {
                currentState.init(container, this);
                currentState.inited = true;
            }

            currentState.enter(this);

            return true;
        }
        return false;
    }

    public int getCurrentStateID()
    {
        return currentState.getID();
    }

    public String getTitle()
    {
        return title;
    }

    public void addState(BasicGameState gameState) throws SlickException
    {
        stateList.put(gameState.getID(), gameState);
        orderedStateList.add(gameState);
        if (gameState.getID() == 0)
            enterState(0);
        // gameState.init(gc, this);
    }

    int lastEnteredState = 0;

    public void render() throws SlickException
    {
        int deltaTime = (int) (Gdx.graphics.getDeltaTime() * 1000);

        if (lastEnteredState > 0)
        {
            if (deltaTime > 32)
            {
                lastEnteredState--;
            }
            else
            {
                lastEnteredState = 0;
            }
        }

        if (leaveTransition == null)
            enterNextState();
        {
            if (leaveTransition != null)
            {
                if (leaveTransition.isComplete())
                {
                    leaveTransition = null;
                }
                else
                {
                    leaveTransition.update(this, container, deltaTime);
                    //oldState.update(gc, this, deltaTime);
                }
            }
            else
            {
                if (enterTransition != null)
                {
                    if (enterTransition.isComplete())
                    {
                        enterTransition = null;
                    }
                    else
                    {
                        enterTransition.update(this, container, deltaTime);
                    }
                }
                if (currentState != null && lastEnteredState == 0)
                    currentState.update(this, deltaTime);
            }

            Graphics g = Graphics.getGraphics();

            Instances.profiler.reset();

            if (leaveTransition != null)
            {
                leaveTransition.preRender(this, container, g);
                oldState.render(this, g);
                leaveTransition.postRender(this, container, g);
            }
            else if (enterTransition != null)
            {
                enterTransition.preRender(this, container, g);
                currentState.render(this, g);
                enterTransition.postRender(this, container, g);
            }
            else
            {
                currentState.render(this, g);
            }

            Instances.profiler.draw();
        }
    }

    public void init() throws SlickException
    {
        initStatesList(container);
        for (BasicGameState state : orderedStateList)
        {
            if (!state.inited)
            {
                state.init(container, this);
                state.inited = true;
            }
        }
    }

    @Override
    public boolean keyDown(int keycode)
    {
        // System.out.println("Key:"+keycode);
        for (GInputListener listener : inputListener)
        {
            listener.consumeEvent = false;
            listener.keyPressed(keycode, (char) 0);
            if (listener.consumeEvent)
                return true;
        }
        currentState.keyPressed(keycode, (char) 0);
        //com.badlogic.gdx.Input.Keys.toString(keycode).charAt(0));

        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        for (GInputListener listener : inputListener)
        {
            listener.consumeEvent = false;
            listener.keyReleased(keycode, (char) 0);
            if (listener.consumeEvent)
                return true;
        }
        currentState.keyReleased(keycode, (char) 0);
        //com.badlogic.gdx.Input.Keys.toString(keycode).charAt(0));

        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        for (GInputListener listener : inputListener)
        {
            listener.consumeEvent = false;
            listener.keyType(character);
            if (listener.consumeEvent)
                return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        try
        {
            if (pointer > 0)
            {
                if (rightIsPressed)
                {
                    mouseReleased(Input.MOUSE_RIGHT_BUTTON, oldx, oldy);
                }
                mousePressed(Input.MOUSE_RIGHT_BUTTON, oldx, oldy);
                container.getInput().setMouseRightButtontDown(true);
                rightIsPressed = true;
                touchX = oldx;
                touchY = oldy;
            }
            else
            {
                mousePressed(button, screenX, screenY);
                oldx = screenX;
                oldy = screenY;
                touchX = screenX;
                touchY = screenY;
            }
            touchTime = TimeUtils.millis();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            GameOpsu.error("touchDown", e);
        }
        return false;
    }

    private void mousePressed(int button, int x, int y)
    {
        Input.x = x;
        Input.y = y;
        for (GInputListener listener : inputListener)
        {
            listener.consumeEvent = false;
            listener.mousePressed(button, x, y);
            if (listener.consumeEvent)
                return;
        }
        currentState.mousePressed(button, x, y);
    }

    private void mouseReleased(int button, int x, int y)
    {
        for (GInputListener listener : inputListener)
        {
            listener.consumeEvent = false;
            listener.mouseReleased(button, x, y);
            if (listener.consumeEvent)
                return;
        }
        currentState.mouseReleased(button, x, y);
    }

    private void mouseClicked(int button, int x, int y, int clickCount)
    {
        for (GInputListener listener : inputListener)
        {
            listener.consumeEvent = false;
            listener.mouseClicked(button, x, y, clickCount);
            if (listener.consumeEvent)
                return;
        }
        currentState.mouseClicked(button, x, y, clickCount);
    }

    private void mouseDragged(int oldx, int oldy, int newx, int newy)
    {
        for (GInputListener listener : inputListener)
        {
            listener.consumeEvent = false;
            listener.mouseDragged(oldx, oldy, newx, newy);
            if (listener.consumeEvent)
                return;
        }
        currentState.mouseDragged(oldx, oldy, newx, newy);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if (pointer > 0)
        {
            int dx = oldx - touchX;
            int dy = oldy - touchY;
            if (TimeUtils.timeSinceMillis(touchTime) < 500 && dx * dx + dy * dy < 10 * 10)
            {
                mouseClicked(Input.MOUSE_RIGHT_BUTTON, oldx, oldy, 1);
            }
            mouseReleased(Input.MOUSE_RIGHT_BUTTON, oldx, oldy);

            container.getInput().setMouseRightButtontDown(false);
            rightIsPressed = false;
        }
        else
        {
            int dx = screenX - touchX;
            int dy = screenY - touchY;
            if (TimeUtils.timeSinceMillis(touchTime) < 500 && dx * dx + dy * dy < 10 * 10)
            {
                mouseClicked(button, screenX, screenY, 1);
            }
            mouseReleased(button, screenX, screenY);

            oldx = screenX;
            oldy = screenY;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        if (pointer == 0)
        {
            Input.x = screenX;
            Input.y = screenY;
            mouseDragged(oldx, oldy, screenX, screenY);
            oldx = screenX;
            oldy = screenY;
        }
        return false;
    }

    int oldx, oldy;

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        Input.x = screenX;
        Input.y = screenY;
        currentState.mouseMoved(oldx, oldy, screenX, screenX);
        oldx = screenX;
        oldy = screenY;
        return false;
    }

    //region oldcode
	/*
	@Override
	public boolean scrolled(int amount) {
		for (GInputListener keylis : inputListener) {
			keylis.consumeEvent = false;
			keylis.mouseWheelMoved(-amount*120);
			if (keylis.consumeEvent)
				return true;
		}
		currentState.mouseWheelMoved(-amount);
		return false;
	}
	*/
    //endregion
    @Override
    public boolean scrolled(float amountX, float amountY)
    {
        /* Scrolling can either be horizontal or vertical */
        if (amountY != 0f)
        {
            for (GInputListener listener : inputListener)
            {
                listener.consumeEvent = false;
                listener.mouseWheelMoved((int) -amountY * 120);
                if (listener.consumeEvent)
                    return true;
            }
            currentState.mouseWheelMoved((int) -amountY);
        }
        else
        {
            //kww: todo log.error
            System.out.println("fluddokt.opsu.fake.StateBasedGame.scrolled: Horizontal scrolling is ignored in this build");
        }

        return false;
    }

    public boolean closeRequested()
    {
        //kww: something was here?
        return false;
    }

    public abstract void initStatesList(GameContainer container) throws SlickException;

    public GameContainer getContainer()
    {
        return container;
    }

    public void setContainer(GameContainer gameContainer)
    {
        container = gameContainer;
    }

    public void addInputListener(GInputListener listener)
    {
        if (!inputListener.contains(listener))
            inputListener.addFirst(listener);
    }

    public void removeInputListener(GInputListener listener)
    {
        inputListener.remove(listener);
    }
}
