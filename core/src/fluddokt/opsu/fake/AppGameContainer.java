package fluddokt.opsu.fake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

public class AppGameContainer extends GameContainer {

	public static int containerWidth, containerHeight;
	
	public AppGameContainer(Game2 game) {
		super((StateBasedGame) game);
	}

	public AppGameContainer(Game2 game, int width, int height, boolean fullscreen) {
		super((StateBasedGame) game);
	}

	public void setDisplayMode(int containerWidth, int containerHeight, boolean isFullscreen) throws SlickException {
		System.out.println("setDisplayMode :" + containerWidth + " " + containerHeight);
		AppGameContainer.containerWidth = containerWidth;
		AppGameContainer.containerHeight = containerHeight;

		//region oldcode
		/*
		Gdx.graphics.setDisplayMode(containerWidth, containerHeight, isFullscreen);
		*/
		//endregion
		if (isFullscreen)
		{
			Graphics.Monitor currMonitor = Gdx.graphics.getMonitor();
			Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode(currMonitor);
			if (!Gdx.graphics.setFullscreenMode(displayMode))
			{
				// switching to full-screen mode failed
				System.err.println("setDisplayMode: FAILED");
			}
		}
		else
		{
			Gdx.graphics.setWindowedMode(containerWidth, containerHeight);
		}

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
	}

	public void setIcons(String[] icons) {
		// TODO Auto-generated method stub
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}

}
