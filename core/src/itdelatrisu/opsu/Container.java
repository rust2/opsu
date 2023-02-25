/*
 * opsu! - an open-source osu! client
 * Copyright (C) 2014-2017 Jeffrey Han
 *
 * opsu! is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opsu! is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opsu!.  If not, see <http://www.gnu.org/licenses/>.
 */

package itdelatrisu.opsu;

import fluddokt.opsu.fake.AppGameContainer;
import fluddokt.opsu.fake.Game2;
import fluddokt.opsu.fake.InternalTextureLoader;
import fluddokt.opsu.fake.SlickException;
import itdelatrisu.opsu.audio.MusicController;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.beatmap.*;
import itdelatrisu.opsu.downloads.DownloadList;
import itdelatrisu.opsu.downloads.Updater;
import itdelatrisu.opsu.options.Options;
import itdelatrisu.opsu.render.CurveRenderState;
import itdelatrisu.opsu.render.LegacyCurveRenderState;
import itdelatrisu.opsu.ui.UI;

/*
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
*/

/**
 * AppGameContainer extension that sends critical errors to ErrorHandler.
 */
public class Container extends AppGameContainer {
	/** Exception causing game failure. */
	protected Exception e = null;

	/** Whether the game is running with software-only OpenGL context. */
	private boolean softwareMode = false;

	/**
	 * Create a new container wrapping a game
	 *
	 * @param game The game to be wrapped
	 * @throws SlickException Indicates a failure to initialise the display
	 */
	public Container(Game2 game) throws SlickException {
		super(game);
	}

	/**
	 * Create a new container wrapping a game
	 *
	 * @param game The game to be wrapped
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 * @throws SlickException Indicates a failure to initialise the display
	 */
	public Container(Game2 game, int width, int height, boolean fullscreen) throws SlickException {
		super(game, width, height, fullscreen);
	}

	/**
	 * Returns whether the game is running with software-only OpenGL context.
	 */
	public boolean isSoftwareMode() { return softwareMode; }

	/*
	@Override
	protected void gameLoop() throws SlickException {
		int delta = getDelta();
		if (!Display.isVisible() && updateOnlyOnVisible) {
			try { Thread.sleep(100); } catch (Exception e) {}
		} else {
			try {
				updateAndRender(delta);
			} catch (SlickException e) {
				this.e = e;  // store exception to display later
				running = false;
				return;
			}
		}
		updateFPS();
		Display.update();
		if (Display.isCloseRequested()) {
			if (game.closeRequested())
				running = false;
		}
	}
	*/
	
	/**
	 * Actions to perform before destroying the game container.
	 */
	@Override
	protected void close_sub() {
		// save user options
		Options.saveOptions();

		// reset cursor
		UI.getCursor().reset();

		// destroy images
		InternalTextureLoader.clear();

		// reset image references
		GameImage.clearReferences();
		GameData.Grade.clearReferences();
		Beatmap.clearBackgroundImageCache();

		// prevent loading tracks from re-initializing OpenAL
		MusicController.reset();

		// stop any playing track
		SoundController.stopTrack();

		// reset BeatmapSetList data
		BeatmapGroup.set(BeatmapGroup.ALL);
		BeatmapSortOrder.set(BeatmapSortOrder.TITLE);
		if (BeatmapSetList.get() != null)
			BeatmapSetList.get().reset();

		// delete OpenGL objects involved in the Curve rendering
		CurveRenderState.shutdown();
		LegacyCurveRenderState.shutdown();

		// destroy watch service
		if (!Options.isWatchServiceEnabled())
			BeatmapWatchService.destroy();
		BeatmapWatchService.removeListeners();

		// delete temporary directory
		Utils.deleteDirectory(Options.TEMP_DIR);
	}

	@Override
	public void exit() {
		// show confirmation dialog if any downloads are active
		if (forceExit) {
			if (DownloadList.get().hasActiveDownloads() &&
			    UI.showExitConfirmation(DownloadList.EXIT_CONFIRMATION))
				return;
			if (Updater.getInstance().getStatus() == Updater.Status.UPDATE_DOWNLOADING &&
			    UI.showExitConfirmation(Updater.EXIT_CONFIRMATION))
				return;
		}

		super.exit();
	}
}
