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

import fluddokt.newdawn.slick.state.transition.EasedFadeOutTransition;
import fluddokt.newdawn.slick.state.transition.FadeInTransition;
import fluddokt.opsu.fake.*;
import itdelatrisu.opsu.audio.MusicController;
import itdelatrisu.opsu.db.DBController;
import itdelatrisu.opsu.downloads.DownloadList;
import itdelatrisu.opsu.downloads.Updater;
import itdelatrisu.opsu.options.Options;
import itdelatrisu.opsu.states.*;
import itdelatrisu.opsu.ui.UI;
import kww.opsu.states.MainMenu;
import kww.useless.Instances;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/*
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EasedFadeOutTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.util.ClasspathLocation;
import org.newdawn.slick.util.DefaultLogSystem;
import org.newdawn.slick.util.FileSystemLocation;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
*/
/**
 * Main class.
 * <p>
 * Creates game container, adds all other states, and initializes song data.
 */
public class Opsu extends StateBasedGame {
	/** Game states. */
	public static final int
		STATE_SPLASH        	= 0,
		STATE_MAINMENU      	= 1,
		STATE_BUTTONMENU		= 2,
		STATE_SONGMENU      	= 3,
		STATE_GAME          	= 4,
		STATE_GAMEPAUSEMENU 	= 5,
		STATE_GAMERANKING 	  	= 6,
		STATE_DOWNLOADSMENU 	= 7,
		STATE_CALIBRATEOFFSET 	= 8;

	/**
	 * Constructor.
	 * @param name the program name
	 */
	public Opsu(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		//edit kww: save instances
		addState(Instances.splash = new Splash(STATE_SPLASH));
		addState(Instances.mainMenu = new MainMenu(STATE_MAINMENU));
		addState(Instances.buttonMenu = new ButtonMenu(STATE_BUTTONMENU));
		addState(Instances.songMenu = new SongMenu(STATE_SONGMENU));
		addState(Instances.game = new Game(STATE_GAME));
		addState(Instances.gamePauseMenu = new GamePauseMenu(STATE_GAMEPAUSEMENU));
		addState(Instances.gameRanking = new GameRanking(STATE_GAMERANKING));
		addState(Instances.downloadsMenu = new DownloadsMenu(STATE_DOWNLOADSMENU));
		addState(Instances.calibrateOffsetMenu = new CalibrateOffsetMenu(STATE_CALIBRATEOFFSET));
	}

	/**
	 * Launches opsu!.
	 */
	public static Opsu opsu;

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
				ErrorHandler.error("** Uncaught Exception! **", e, true);
			}
		});
		
		// log all errors to a file
		Log.setVerbose(false);
		try {
			System.out.println("LOG FILE: "+ Options.LOG_FILE);
			DefaultLogSystem.out = new PrintStream(new FileOutputStream(Options.LOG_FILE, false));
			DefaultLogSystem.out.println("Run Date: "+ new java.util.Date());
			DefaultLogSystem.out.flush();
		} catch (FileNotFoundException e) {
			Log.error(e);
		}

		// parse configuration file
		try {
			Options.parseOptions();
		} catch (UnsatisfiedLinkError e) {
			Log.error(e);
		}

		/*
		// initialize databases
		try {
			DBController.init();
		} catch (SQLiteException e) {
			// probably locked by another instance
			if (e.getErrorCode() == SQLiteErrorCode.SQLITE_BUSY.code) {
				Log.error(e);
				errorAndExit(
					null,
					String.format(
						"%s could not be launched for one of these reasons:\n" +
						"- An instance of %s is already running.\n" +
						"- A database is locked for another reason (unlikely). ",
						OpsuConstants.PROJECT_NAME,
						OpsuConstants.PROJECT_NAME
					),
					false
				);
			} else
				errorAndExit(e, "The databases could not be initialized.", true);
		} catch (ClassNotFoundException e) {
			errorAndExit(e, "Could not load sqlite-JDBC driver.", true);
		} catch (Exception e) {
			errorAndExit(e, "The databases could not be initialized.", true);
		}
		*/

		/*
		File nativeDir;
		if (!Utils.isJarRunning() && (
		    (nativeDir = new File("./target/natives/")).isDirectory() ||
		    (nativeDir = new File("./build/natives/")).isDirectory()))
			;
		else {
			nativeDir = Options.NATIVE_DIR;
			try {
				new NativeLoader(nativeDir).loadNatives();
			} catch (IOException e) {
				Log.error("Error loading natives.", e);
			}
		}
		System.setProperty("org.lwjgl.librarypath", nativeDir.getAbsolutePath());
		System.setProperty("java.library.path", nativeDir.getAbsolutePath());
		FFmpeg.setNativeDir(nativeDir);
		 */
		
		// set the resource paths
		ResourceLoader.addResourceLocation(new FileSystemLocation(new File("res/"),true));
		/*
		ResourceLoader.addResourceLocation(new FileSystemLocation(new File("./res/")));
		*/
		
		//TODO initialize databases
		try {
			DBController.init();
		} catch (Exception e) {
			errorAndExit(e, "The databases could not be initialized.", true);
		}
		/*
		// check if just updated
		if (args.length >= 2)
			Updater.get().setUpdateInfo(args[0], args[1]);

		// check for updates
		Updater.get().getCurrentVersion();  // load this for the main menu
		if (!Options.isUpdaterDisabled()) {
			new Thread() {
				@Override
				public void run() {
					try {
						Updater.get().checkForUpdates();
					} catch (Exception e) {
						Log.warn("Check for updates failed.", e);
					}
				}
			}.start();
		}
		*/
		
		// disable jinput
		/*
		Input.disableControllers();
		*/
	}

	public static Opsu start() {
		// start the game
		try {
		
			// loop until force exit
		//	while (true) {
				opsu = new Opsu(OpsuConstants.PROJECT_NAME);
				Container app = new Container(opsu);

				// basic game settings
				Options.setDisplayMode(app);
				String[] icons = { "icon16.png", "icon32.png" };
				app.setIcons(icons);
				app.setForceExit(true);

				app.start();

				/*
				// run update if available
				if (Updater.get().getStatus() == Updater.Status.UPDATE_FINAL) {
					close();
					Updater.get().runUpdate();
					break;
				}
				*/
		//	}
		} catch (Exception e) {
			errorAndExit(e, "An error occurred while creating the game container.", true);
		}
		return opsu;
	}

	@Override
	public boolean closeRequested() {
		int id = this.getCurrentStateID();

		// intercept close requests in game-related states and return to song menu
		if (id == STATE_GAME || id == STATE_GAMEPAUSEMENU || id == STATE_GAMERANKING) {
			// start playing track at preview position
			SongMenu songMenu = (SongMenu) this.getState(Opsu.STATE_SONGMENU);
			if (id == STATE_GAMERANKING) {
				GameData data = ((GameRanking) this.getState(Opsu.STATE_GAMERANKING)).getGameData();
				if (data != null && data.isGameplay())
					songMenu.resetTrackOnLoad();
			} else {
				if (id == STATE_GAME) {
					MusicController.pause();
					MusicController.setPitch(1.0f);
					MusicController.resume();
				} else
					songMenu.resetTrackOnLoad();
			}

			// reset game data
			if (UI.getCursor().isBeatmapSkinned())
				UI.getCursor().reset();
			songMenu.resetGameDataOnLoad();

			this.enterState(Opsu.STATE_SONGMENU, new EasedFadeOutTransition(), new FadeInTransition());
			return false;
		}

		// show confirmation dialog if any downloads are active
		if (DownloadList.get().hasActiveDownloads() &&
		    UI.showExitConfirmation(DownloadList.EXIT_CONFIRMATION))
			return false;
		if (Updater.getInstance().getStatus() == Updater.Status.UPDATE_DOWNLOADING &&
		    UI.showExitConfirmation(Updater.EXIT_CONFIRMATION))
			return false;

		return true;
	}

	/**
	 * Closes all resources.
	 */
	public static void close() {
		// close databases
		DBController.closeConnections();

		// cancel all downloads
		DownloadList.get().cancelAllDownloads();
	}

	/**
	 * Throws an error and exits the application with the given message.
	 * @param e the exception that caused the crash
	 * @param message the message to display
	 * @param report whether to ask to report the error
	 */
	private static void errorAndExit(Throwable e, String message, boolean report) {
		// JARs will not run properly inside directories containing '!'
		// http://bugs.java.com/view_bug.do?bug_id=4523159
		if (Utils.isJarRunning() && Utils.getRunningDirectory() != null &&
		    Utils.getRunningDirectory().getAbsolutePath().indexOf('!') != -1)
			ErrorHandler.error(
				"JARs cannot be run from some paths containing the '!' character. " +
				"Please rename the file/directories and try again.\n\n" +
				"Path: " + Utils.getRunningDirectory().getAbsolutePath(),
				null,
				false
			);
		else
			ErrorHandler.error(message, e, report);
		System.exit(1);
	}
}
