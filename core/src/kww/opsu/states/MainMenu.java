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

package kww.opsu.states;

import fluddokt.newdawn.slick.state.transition.EasedFadeOutTransition;
import fluddokt.newdawn.slick.state.transition.FadeInTransition;
import fluddokt.opsu.fake.*;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Opsu;
import itdelatrisu.opsu.OpsuConstants;
import itdelatrisu.opsu.Utils;
import itdelatrisu.opsu.audio.MusicController;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;
import itdelatrisu.opsu.beatmap.Beatmap;
import itdelatrisu.opsu.beatmap.BeatmapSetList;
import itdelatrisu.opsu.beatmap.BeatmapSetNode;
import itdelatrisu.opsu.downloads.Updater;
import itdelatrisu.opsu.options.OptionGroup;
import itdelatrisu.opsu.options.Options;
import itdelatrisu.opsu.options.OptionsOverlay;
import itdelatrisu.opsu.states.ButtonMenu;
import itdelatrisu.opsu.states.ButtonMenu.MenuState;
import itdelatrisu.opsu.states.DownloadsMenu;
import itdelatrisu.opsu.states.SongMenu;
import itdelatrisu.opsu.ui.*;
import itdelatrisu.opsu.ui.NotificationManager.NotificationListener;
import itdelatrisu.opsu.ui.animations.AnimatedValue;
import itdelatrisu.opsu.ui.animations.AnimationEquation;
import itdelatrisu.opsu.user.UserButton;
import itdelatrisu.opsu.user.UserList;
import itdelatrisu.opsu.user.UserSelectOverlay;
import kww.useless.Instances;
import kww.useless.Parallax;
import kww.useless.UselessUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

/**
 * "Main Menu" state.
 * <p>
 * Players are able to enter the song menu or downloads menu from this state.
 */
public class MainMenu extends BasicGameState {
    /** Idle time, in milliseconds, before returning the logo to its original position. */
    private static final short LOGO_IDLE_DELAY = 6000;

    /** Max alpha level of the menu background. */
    private static final float BG_MAX_ALPHA = 0.9f;

    /** Logo button that reveals other buttons on click. */
    @Getter private MenuButton logo;
    @Getter private float logoScale = 0f;

    /** Current logo state. */
    private LogoState logoState = LogoState.DEFAULT;

    /** Delay timer, in milliseconds, before starting to move the logo back to the center. */
    private int logoTimer = 0;

    /** Logo horizontal offset for opening and closing actions. */
    private AnimatedValue logoOpen, logoClose;

    /** Logo button alpha levels. */
    private AnimatedValue logoButtonAlpha;

    /** Main "Play" and "Exit" buttons. */
    private MenuButton playButton, exitButton;

//    /** Music control buttons. */
//    private MenuButton musicPlay, musicPause, musicNext, musicPrevious;

//    /** Button linking to Downloads menu. */
//    private MenuButton downloadsButton;
//    public ToolbarButton toolbarDownloadsButton;
//    public ToolbarButton toolbarSettingsButton;
//    public ToolbarButton toolbarProfileButton;

//    /** Button linking to repository. */
//    private MenuButton repoButton;

    /** Buttons for installing updates. */
    private MenuButton updateButton, restartButton;

    /** Application start time, for drawing the total running time. */
    private long programStartTime;

    /** Indexes of previous songs. */
    private Stack<Integer> previous;

    /** Background alpha level (for fade-in effect). */
    private final AnimatedValue bgAlpha = new AnimatedValue(1100, 0f, BG_MAX_ALPHA, AnimationEquation.LINEAR);

    /** Whether or not a notification was already sent upon entering. */
    private boolean enterNotification = false;

//    /** Music position bar coordinates and dimensions. */
//    private float musicBarX, musicBarY, musicBarWidth, musicBarHeight;

    /** Last measure progress value. */
    @Setter private float lastMeasureProgress = 0f;

    /** The star fountain. */
    private StarFountain starFountain;

//    /** Music info bar "Now Playing" image. */
//    private Image musicInfoImg;

//    /** Music info bar rectangle. */
//    private Rectangle musicInfoRect;

    /** Music info bar fill. */
    //TODO private GradientFill musicInfoFill;

//    /** Music info bar animation progress. */
//    private final AnimatedValue musicInfoProgress = new AnimatedValue(600, 0f, 1f, AnimationEquation.OUT_CUBIC);

    /** Options overlay. */
    private OptionsOverlay optionsOverlay;

    /** Whether the options overlay is being shown. */
    private boolean showOptionsOverlay = false;

    /** The options overlay show/hide animation progress. */
    private final AnimatedValue optionsOverlayProgress = new AnimatedValue(500, 0f, 1f, AnimationEquation.LINEAR);

    /** The user button. */
    private UserButton userButton;

    /** Whether the user button has been flashed. */
    private boolean userButtonFlashed = false;

    /** User selection overlay. */
    private UserSelectOverlay userOverlay;

    /** Whether the user overlay is being shown. */
    private boolean showUserOverlay = false;

    /** The user overlay show/hide animation progress. */
    private final AnimatedValue userOverlayProgress = new AnimatedValue(750, 0f, 1f, AnimationEquation.OUT_CUBIC);

    // game-related variables
//    private GameContainer container;
//    private StateBasedGame game;
//    private Input input;
//    private final int state;

    // edited: kww
    /** Container dimensions */
    public int width, height;
    public float widthCenter, heightCenter;
    public float logoSizeMultiplier = 1f;

    /** Footer */
    private final String version = OpsuConstants.PROJECT_NAME + " " + Updater.getInstance().getCurrentVersion();

    public MainMenu(int state)
    {
        this.state = state;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException
    {
        Graphics.getGraphics().registerResizable(this);

        this.container = container;
        this.game = game;
        this.input = container.getInput();

        programStartTime = System.currentTimeMillis();
        previous = new Stack<>();

        /* Initial resize */
        resize();

        // initialize menu buttons
        Image logoImg = GameImage.MENU_LOGO.getImage();
        Image playImg = GameImage.MENU_PLAY.getImage();
        Image exitImg = GameImage.MENU_EXIT.getImage();
        float exitOffset = (playImg.getWidth() - exitImg.getWidth()) / 3f;
        final int logoAnimationDuration = 350;
        final AnimationEquation logoAnimationEquation = AnimationEquation.IN_OUT_BACK;
        final float logoHoverScale = 1.08f;
        logo = new MenuButton(logoImg, widthCenter, heightCenter)
                .setHoverAnimationDuration(logoAnimationDuration)
                .setHoverAnimationEquation(logoAnimationEquation)
                .setHoverExpand(logoHoverScale);
        playButton = new MenuButton(playImg, width * 0.75f, heightCenter - (logoImg.getHeight() / 5f))
                .setHoverAnimationDuration(logoAnimationDuration)
                .setHoverAnimationEquation(logoAnimationEquation)
                .setHoverExpand(logoHoverScale);
        exitButton = new MenuButton(exitImg, width * 0.75f - exitOffset, heightCenter + (exitImg.getHeight() / 2f))
                .setHoverAnimationDuration(logoAnimationDuration)
                .setHoverAnimationEquation(logoAnimationEquation)
                .setHoverExpand(logoHoverScale);

        //kww
        //main menu toolbar
        Instances.toolbar.init();

        //player init...
        Instances.player.init();

//        // initialize repository button
//        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
//        {  // only if a webpage can be opened
//            Image repoImg = GameImage.REPOSITORY.getImage();
//            int repoMargin = (int) (height * 0.01f);
//            float repoScale = 1.25f;
//            repoButton = new MenuButton(repoImg, repoMargin + repoImg.getWidth() * repoScale / 2,
//                                        height - repoMargin - repoImg.getHeight() * repoScale / 2)
//                    .setHoverAnimationDuration(350)
//                    .setHoverAnimationEquation(AnimationEquation.IN_OUT_BACK)
//                    .setHoverExpand(repoScale);
//        }

        // initialize update buttons
        float updateX = widthCenter, updateY = height * 17 / 18f;
        updateButton = new MenuButton(GameImage.DOWNLOAD.getImage(), updateX, updateY)
                .setHoverAnimationDuration(400)
                .setHoverAnimationEquation(AnimationEquation.IN_OUT_QUAD)
                .setHoverExpand(1.1f);
        restartButton = new MenuButton(GameImage.UPDATE.getImage(), updateX, updateY)
                .setHoverAnimationDuration(2000)
                .setHoverAnimationEquation(AnimationEquation.LINEAR)
                .setHoverRotate(360);

        // initialize star fountain
        //Instances.blinker.init(container);
        starFountain = new StarFountain(width, height);

        // logo animations
        float centerOffsetX = width / 5f;
        logoOpen = new AnimatedValue(400, 0, centerOffsetX, AnimationEquation.OUT_QUAD);
        logoClose = new AnimatedValue(2200, centerOffsetX, 0, AnimationEquation.OUT_QUAD);
        logoButtonAlpha = new AnimatedValue(200, 0f, 1f, AnimationEquation.LINEAR);

        // options overlay
        optionsOverlay = new OptionsOverlay(container, OptionGroup.ALL_OPTIONS, new OptionsOverlay.OptionsOverlayListener() {
            @Override
            public void close()
            {
                showOptionsOverlay = false;
                optionsOverlay.deactivate();
                optionsOverlay.reset();
                optionsOverlayProgress.setTime(0);
            }
        });
        optionsOverlay.setConsumeAndClose(true);
        optionsOverlay.setLocation(0, (int) Instances.toolbar.getHeight()); //kww: todo: variable Y position

        // initialize user button
        userButton = new UserButton(0, 100, Color.white);

        // initialize user selection overlay
        userOverlay = new UserSelectOverlay(container, new UserSelectOverlay.UserSelectOverlayListener() {
            @Override
            public void close(boolean userChanged)
            {
                showUserOverlay = false;
                userOverlay.deactivate();
                userOverlayProgress.setTime(0);
                if (userChanged)
                    userButton.flash();
            }
        });
        userOverlay.setConsumeAndClose(true);

        //kww: init visualizer ONLY AFTER logo button was created
        Instances.visualizer.init();

        reset();
//        musicInfoProgress.setTime(0);
    }

    @Override
    public void render(StateBasedGame game, Graphics g) throws SlickException
    {
        int mouseX = input.getMouseX(), mouseY = input.getMouseY();

        Beatmap beatmap = MusicController.getBeatmap();

        // draw background
        //region oldcode
        /*
        float parallaxX = 0, parallaxY = 0;
        if (Options.isParallaxEnabled())
        {
            int offset = (int) (height * (GameImage.PARALLAX_SCALE - 1f));
            parallaxX = -offset / 2f * (mouseX - width / 2f) / (width / 2f);
            parallaxY = -offset / 2f * (mouseY - height / 2f) / (height / 2f);
        }
        */
        //endregion
        float parallaxX = Parallax.getBgX(mouseX), parallaxY = Parallax.getBgY(mouseY);

        if (Options.isDynamicBackgroundEnabled() && beatmap != null &&
                beatmap.drawBackground(width, height, parallaxX, parallaxY, bgAlpha.getValue(), true))
            ;
        else
        {
            Image bg = GameImage.MENU_BG.getImage();
            if (Options.isParallaxEnabled())
            {
                bg = bg.getScaledCopy(GameImage.PARALLAX_SCALE);
                bg.setAlpha(bgAlpha.getValue());
                bg.drawCentered(widthCenter + parallaxX, height / 2f + parallaxY);
            }
            else
            {
                bg.setAlpha(bgAlpha.getValue());
                bg.drawCentered(widthCenter, height / 2f);
            }
        }

        //top/bottom horizontal bars
        //region oldcode
        /*
        // top/bottom horizontal bars
        g.setColor(Colors.BLACK_ALPHA);
        g.fillRect(0, 0, width, height * Options.getMobileUIScale(0.5f) / 9f);
        g.fillRect(0, height * 8 / 9f, width, height / 9f);
        */
        //endregion
//        g.setColor(Colors.BLACK_BARS);
//        g.fillRect(0, 0, width, blackBarsHeight);
//        g.fillRect(0, height - blackBarsHeight, width, blackBarsHeight);

        // draw star fountain
        starFountain.draw();

//        // draw downloads button
//        downloadsButton.draw();

        // draw buttons
        if (logoState == LogoState.OPEN || logoState == LogoState.CLOSING)
        {
            playButton.draw();
            exitButton.draw();
        }

        //spectrum?
        Instances.visualizer.draw();

        // draw logo (pulsing)
        float beatPosition = MusicController.getBeatProgress();

        //region oldcode
        /*
        float scale = 1f + position * 0.025f;
        logo.draw(Color.white, scale);

        float ghostScale = logo.getLastScale() / scale * (1.05f - position * 0.025f);
        */
        //endregion
        logoScale = logoSizeMultiplier * (1f + beatPosition * 0.04f);
        logo.draw(Color.white, logoScale);

//        float ghostScale = logo.getLastScale() / logoScale * (1.08f - beatPosition * 0.04f);
//        Image ghostLogo = GameImage.MENU_LOGO.getImage().getScaledCopy(ghostScale);
//        ghostLogo.drawCentered(logo.getX(), logo.getY(), Colors.GHOST_LOGO);

        Instances.toolbar.draw();
        Instances.player.draw();

        // draw update button
        if (Updater.getInstance().showButton())
        {
            Updater.Status status = Updater.getInstance().getStatus();
            if (status == Updater.Status.UPDATE_AVAILABLE || status == Updater.Status.UPDATE_DOWNLOADING)
                updateButton.draw();
            else if (status == Updater.Status.UPDATE_DOWNLOADED)
                restartButton.draw();
        }

        // draw user button
        userButton.setUser(UserList.get().getCurrentUser());
        userButton.draw(g);

        // draw text
        float textAlpha;
        if (logoState == LogoState.DEFAULT)
            textAlpha = 0f;
        else if (logoState == LogoState.OPEN)
            textAlpha = 1f;
        else if (logoState == LogoState.OPENING)
            textAlpha = logoOpen.getEquation().calc((float) logoOpen.getTime() / logoOpen.getDuration());
        else //if (logoState == LogoState.CLOSING)
            textAlpha = 1f - logoClose.getEquation().calc(Math.min(logoClose.getTime() * 2f / logoClose.getDuration(), 1f));
        float oldWhiteAlpha = Colors.WHITE_FADE.a;
        Colors.WHITE_FADE.a = textAlpha;
        float marginX = UserButton.getWidth() + 8, topMarginY = 4;
        Fonts.MEDIUM.drawString(marginX, topMarginY,
                                String.format("Loaded %d sets with %d beatmaps.",
                                              BeatmapSetList.get().getMapSetCount(), BeatmapSetList.get().getMapCount()),
                                Colors.WHITE_FADE
        );
        float lineHeight = Fonts.MEDIUM.getLineHeight() * 0.925f;
        Fonts.MEDIUM.drawString(marginX, topMarginY + lineHeight,
                                String.format("%s has been running for %s.",
                                              OpsuConstants.PROJECT_NAME,
                                              Utils.getTimeString((int) (System.currentTimeMillis() - programStartTime) / 1000)),
                                Colors.WHITE_FADE
        );
        lineHeight += Fonts.MEDIUM.getLineHeight() * 0.925f;
        Fonts.MEDIUM.drawString(marginX, topMarginY + lineHeight,
                                String.format("It is currently %s.",
                                              new SimpleDateFormat("h:mm a").format(new Date())),
                                Colors.WHITE_FADE
        );
        Colors.WHITE_FADE.a = oldWhiteAlpha;

        //kww: todo: maybe extract this text to outer class?
        UselessUtils.Text.drawCentered(
                Fonts.DEFAULT,
                widthCenter,
                height - Fonts.DEFAULT.getLineHeight(),
                version,
                kww.useless.visuals.Colors.GrayF.fake()
        );

        // options overlay
        if (showOptionsOverlay || !optionsOverlayProgress.isFinished())
            optionsOverlay.render(container, g);

        // user overlay
        if (showUserOverlay || !userOverlayProgress.isFinished())
            userOverlay.render(container, g);

        UI.draw(g);
    }

    @Override
    public void update(StateBasedGame game, int delta) throws SlickException
    {
        UI.update(delta);

        if (MusicController.trackEnded())
            Instances.player.nextTrack(false);  // end of track: go to next track

        int mouseX = input.getMouseX(), mouseY = input.getMouseY();
        if (showOptionsOverlay || showUserOverlay)
        {
            logo.hoverUpdate(delta, false);
            playButton.hoverUpdate(delta, false);
            exitButton.hoverUpdate(delta, false);
        }
        else
        {
            logo.hoverUpdate(delta, mouseX, mouseY, 0.25f);
            playButton.hoverUpdate(delta, mouseX, mouseY, 0.25f);
            exitButton.hoverUpdate(delta, mouseX, mouseY, 0.25f);
        }

        if (Updater.getInstance().showButton())
        {
            updateButton.autoHoverUpdate(delta, true);
            restartButton.autoHoverUpdate(delta, false);
        }
//        downloadsButton.hoverUpdate(delta, mouseX, mouseY);
        starFountain.update(delta);
        if (!userButtonFlashed)
        {  // flash user button once
            userButton.flash();
            userButtonFlashed = true;
        }
        userButton.hoverUpdate(delta, userButton.contains(mouseX, mouseY));
//        if (MusicController.trackExists())
//            musicInfoProgress.update(delta);

        // window focus change: increase/decrease theme song volume
        if (MusicController.isTrackDimmed() == container.hasFocus())
            MusicController.toggleTrackDimmed(0.33f);

        Instances.player.update(delta);
        Instances.toolbar.hoverUpdate(delta, mouseX, mouseY);

        // fade in background
        Beatmap beatmap = MusicController.getBeatmap();
        if (!(Options.isDynamicBackgroundEnabled() && beatmap != null && beatmap.isBackgroundLoading()))
            bgAlpha.update(delta);

        // check measure progress
        Float measureProgress = MusicController.getMeasureProgress(2);
        if (measureProgress != null)
        {
            if (measureProgress < lastMeasureProgress)
                starFountain.burst(true);
            lastMeasureProgress = measureProgress;
        }

        // options overlay
        if (optionsOverlayProgress.update(delta))
        {
            // slide in/out
            float t = optionsOverlayProgress.getValue();
            float navigationAlpha;
            if (!showOptionsOverlay)
            {
                navigationAlpha = 1f - AnimationEquation.IN_CIRC.calc(t);
                t = 1f - t;
            }
            else
                navigationAlpha = Utils.clamp(t * 10f, 0f, 1f);
            t = AnimationEquation.OUT_CUBIC.calc(t);
            optionsOverlay.setWidth((int) (optionsOverlay.getTargetWidth() * t));
            optionsOverlay.setAlpha(t, navigationAlpha);
        }
        else if (showOptionsOverlay)
            optionsOverlay.update(delta);

        // user overlay
        if (userOverlayProgress.update(delta))
        {
            // fade in/out
            float t = userOverlayProgress.getValue();
            userOverlay.setAlpha(showUserOverlay ? t : 1f - t);
        }
        else if (showUserOverlay)
            userOverlay.update(delta);

        // buttons
        float parallaxX = Parallax.getLogoX(mouseX);
        float parallaxY = Parallax.getLogoY(mouseY);
        float centerX = widthCenter + parallaxX;
        float centerY = heightCenter + parallaxY;
        float currentLogoButtonAlpha;
        switch (logoState)
        {
            case DEFAULT:
            {
                logo.setX(centerX);
                logo.setY(centerY);
                break;
            }
            case OPENING:
            {
                if (logoOpen.update(delta)) // shifting to left
                {
                    logo.setX(centerX - logoOpen.getValue());
                }
                else
                {
                    logoState = LogoState.OPEN;
                    logoTimer = 0;
                    logoButtonAlpha.setTime(0);
                }

                break;
            }
            case OPEN:
            {
                if (logoButtonAlpha.update(delta)) // fade in buttons
                {
                    currentLogoButtonAlpha = logoButtonAlpha.getValue();
                    playButton.getImage().setAlpha(currentLogoButtonAlpha);
                    exitButton.getImage().setAlpha(currentLogoButtonAlpha);
                }
                else if (logoTimer >= LOGO_IDLE_DELAY) // timer over: shift back to center
                {
                    logoState = LogoState.CLOSING;
                    logoClose.setTime(0);
                    logoTimer = 0;
                }
                else // increment timer
                {
                    logoTimer += delta;
                }

                break;
            }
            case CLOSING:
            {
                if (logoButtonAlpha.update(-delta)) // fade out buttons
                {
                    currentLogoButtonAlpha = logoButtonAlpha.getValue();
                    playButton.getImage().setAlpha(currentLogoButtonAlpha);
                    exitButton.getImage().setAlpha(currentLogoButtonAlpha);
                }

                if (logoClose.update(delta)) // shifting to right
                {
                    logo.setX(centerX - logoClose.getValue());
                }
                else
                {
                    logoState = LogoState.DEFAULT;
                }

                break;
            }
        }

        // tooltips
        if (Instances.toolbar.toolbarMusicButton.contains(mouseX, mouseY))
            UI.updateTooltip(delta, "now playing\nmanage the currently playing track", true);
//        else if (Instances.toolbar.toolbarDownloadsButton.contains(mouseX, mouseY))
//            UI.updateTooltip(delta, "beatmap listing\nbrowse for new beatmaps", true);
//        else if (Instances.toolbar.toolbarSettingsButton.contains(mouseX, mouseY))
//            UI.updateTooltip(delta, "settings\nchange the way " + OpsuConstants.PROJECT_NAME + " behaves", true);
//        else if (Instances.toolbar.toolbarProfileButton.contains(mouseX, mouseY))
//            UI.updateTooltip(delta, "profile overlay", false);
//        else if (repoButton != null && repoButton.contains(mouseX, mouseY))
//        {
//            String version = Updater.getInstance().getCurrentVersion();
//            String tooltip = String.format(
//                    "running %s %s\ncreated by %s",
//                    OpsuConstants.PROJECT_NAME,
//                    (version == null) ? "(unknown version)" : "v" + version,
//                    OpsuConstants.PROJECT_AUTHOR
//            );
//            UI.updateTooltip(delta, tooltip, true);
//        }
        else if (Updater.getInstance().showButton())
        {
            Updater.Status status = Updater.getInstance().getStatus();
            if (((status == Updater.Status.UPDATE_AVAILABLE || status == Updater.Status.UPDATE_DOWNLOADING) && updateButton.contains(mouseX, mouseY)) ||
                    (status == Updater.Status.UPDATE_DOWNLOADED && restartButton.contains(mouseX, mouseY)))
                UI.updateTooltip(delta, status.getDescription(), true);
        }
    }

    @Override
    public int getID()
    {
        return state;
    }

    @Override
    public void enter(StateBasedGame game) throws SlickException
    {
        float t = com.badlogic.gdx.Gdx.graphics.getWidth() / com.badlogic.gdx.Gdx.graphics.getPpiX();
        System.out.println("screen size = " + t);
        UI.enter();
        if (!enterNotification)
        {
            if (Updater.getInstance().getStatus() == Updater.Status.UPDATE_AVAILABLE)
            {
                UI.getNotificationManager().sendNotification("A new update is available!", Colors.GREEN);
                enterNotification = true;
            }
            else if (Updater.getInstance().justUpdated())
            {
                String updateMessage = OpsuConstants.PROJECT_NAME + " is now up to date!";
                final String version = Updater.getInstance().getCurrentVersion();
                if (version != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
                {
                    updateMessage += "\nClick to see what changed!";
                    UI.getNotificationManager().sendNotification(updateMessage, Colors.GREEN, new NotificationListener() {
                        @Override
                        public void click()
                        {
                            try
                            {
                                Desktop.getDesktop().browse(OpsuConstants.getChangelogURI(version));
                            }
                            catch (IOException e)
                            {
                                UI.getNotificationManager().sendBarNotification("The web page could not be opened.");
                            }
                        }
                    });
                }
                else
                {
                    UI.getNotificationManager().sendNotification(updateMessage);
                }

                enterNotification = true;
            }
        }

        // reset measure info
        //Instances.blinker.resetCounters();
        lastMeasureProgress = 0f;
        starFountain.clear();

        // reset button hover states if mouse is not currently hovering over the button
        int mouseX = input.getMouseX(), mouseY = input.getMouseY();
        if (!logo.contains(mouseX, mouseY, 0.25f))
            logo.resetHover();
        if (!playButton.contains(mouseX, mouseY, 0.25f))
            playButton.resetHover();
        if (!exitButton.contains(mouseX, mouseY, 0.25f))
            exitButton.resetHover();
//        if (!musicPlay.contains(mouseX, mouseY))
//            musicPlay.resetHover();
//        if (!musicPause.contains(mouseX, mouseY))
//            musicPause.resetHover();
//        if (!musicNext.contains(mouseX, mouseY))
//            musicNext.resetHover();
//        if (!musicPrevious.contains(mouseX, mouseY))
//            musicPrevious.resetHover();
//        if (repoButton != null && !repoButton.contains(mouseX, mouseY))
//            repoButton.resetHover();
        updateButton.resetHover();
        restartButton.resetHover();
//        if (!downloadsButton.contains(mouseX, mouseY))
//            downloadsButton.resetHover();
        if (!userButton.contains(mouseX, mouseY))
            userButton.resetHover();

        // reset overlays
        optionsOverlay.deactivate();
        optionsOverlay.reset();
        showOptionsOverlay = false;
        optionsOverlayProgress.setTime(optionsOverlayProgress.getDuration());
        userOverlay.deactivate();
        showUserOverlay = false;
        userOverlayProgress.setTime(userOverlayProgress.getDuration());
    }

    @Override
    public void leave(StateBasedGame game) throws SlickException
    {
        if (MusicController.isTrackDimmed())
            MusicController.toggleTrackDimmed(1f);

        // reset overlays
        optionsOverlay.deactivate();
        optionsOverlay.reset();
        showOptionsOverlay = false;
        userOverlay.deactivate();
        showUserOverlay = false;

        //kww
        Instances.player.deactivate();
    }

    @Override
    //public void mousePressed(int button, int x, int y)
    public void mouseReleased(int button, int x, int y)
    {
        // check mouse button
        if (button == Input.MOUSE_MIDDLE_BUTTON)
            return;

        if (showOptionsOverlay || !optionsOverlayProgress.isFinished() ||
                showUserOverlay || !userOverlayProgress.isFinished())
            return;

        // edit kww: place a hook here for "my" player
        if (Instances.player.mousePressed(button, x, y))
            return;

        // music position bar
//        if (MusicController.isPlaying())
//        {
//            if (musicPositionBarContains(x, y))
//            {
//                lastMeasureProgress = 0f;
//                float pos = (x - musicBarX) / musicBarWidth;
//                MusicController.setPosition((int) (pos * MusicController.getDuration()));
//                return;
//            }
//        }

        // music button actions
//        if (musicPlay.contains(x, y))
//        {
//            Instances.player.playPauseTrack();
//            return;
//        }
//        else if (musicNext.contains(x, y))
//        {
//            Instances.player.nextTrack(true);
//            return;
//        }
//        else if (musicPrevious.contains(x, y))
//        {
//            Instances.player.prevTrack();
//            return;
//        }

//        // downloads button actions
//        if (downloadsButton.contains(x, y))
//        {
//            SoundController.playSound(SoundEffect.MENUHIT);
//            game.enterState(Opsu.STATE_DOWNLOADSMENU, new EasedFadeOutTransition(), new FadeInTransition());
//            return;
//        }

        if (Instances.toolbar.someButtonHasBeenPressed(x, y))
            return;

//        // downloads button actions (modern)
//        if (toolbarDownloadsButton.contains(x, y))
//        {
//            toolbarDownloadsButton.pressed();
//            return;
//        }
//
//        // settings button
//        if (toolbarSettingsButton.contains(x, y))
//        {
//            toolbarSettingsButton.pressed();
//            return;
//        }
//
//        // user profile button
//        if (toolbarProfileButton.contains(x, y))
//        {
//            toolbarProfileButton.pressed();
//            return;
//        }

//        // repository button actions
//        if (repoButton != null && repoButton.contains(x, y))
//        {
//            SoundController.playSound(SoundEffect.MENUHIT);
//            ((ButtonMenu) game.getState(Opsu.STATE_BUTTONMENU)).setMenuState(MenuState.ABOUT);
//            game.enterState(Opsu.STATE_BUTTONMENU);
//            return;
//        }

        // update button actions
        if (Updater.getInstance().showButton())
        {
            Updater.Status status = Updater.getInstance().getStatus();
            if (updateButton.contains(x, y) && status == Updater.Status.UPDATE_AVAILABLE)
            {
                SoundController.playSound(SoundEffect.MENUHIT);
                Updater.getInstance().startDownload();
                updateButton.removeHoverEffects();
                updateButton.setHoverAnimationDuration(800);
                updateButton.setHoverAnimationEquation(AnimationEquation.IN_OUT_QUAD);
                updateButton.setHoverFade(0.6f);
                return;
            }
            else if (restartButton.contains(x, y) && status == Updater.Status.UPDATE_DOWNLOADED)
            {
                SoundController.playSound(SoundEffect.MENUHIT);
                Updater.getInstance().prepareUpdate();
                container.setForceExit(false);
                container.exit();
                return;
            }
        }

        // user button actions
        if (userButton.contains(x, y))
        {
            SoundController.playSound(SoundEffect.MENUCLICK);
            showUserOverlay = true;
            userOverlayProgress.setTime(0);
            userOverlay.activate();
            return;
        }

        // start moving logo (if clicked)
        if (logoState == LogoState.DEFAULT || logoState == LogoState.CLOSING)
        {
            if (logo.contains(x, y, 0.25f))
            {
                SoundController.playSound(SoundEffect.MENUHIT);
                openLogoMenu();
            }
        }

        // other button actions (if visible)
        else if (logoState == LogoState.OPEN || logoState == LogoState.OPENING)
        {
            if (logo.contains(x, y, 0.25f) || playButton.contains(x, y, 0.25f))
            {
                SoundController.playSound(SoundEffect.MENUHIT);
                enterSongMenu();
            }
            else if (exitButton.contains(x, y, 0.25f))
            {
                container.exit();
            }
        }
    }

    @Override
    public void mouseWheelMoved(int newValue)
    {
        UI.globalMouseWheelMoved(newValue, false);
    }

    @Override
    public void keyPressed(int key, char c)
    {
        if (UI.globalKeyPressed(key))
            return;

        switch (key)
        {
            case Input.KEY_ESCAPE:
            case Input.KEY_Q:
            case Input.ANDROID_BACK:
                ((ButtonMenu) game.getState(Opsu.STATE_BUTTONMENU)).setMenuState(MenuState.EXIT);
                game.enterState(Opsu.STATE_BUTTONMENU);
                break;
            case Input.KEY_P:
                SoundController.playSound(SoundEffect.MENUHIT);
                if (logoState == LogoState.DEFAULT || logoState == LogoState.CLOSING)
                    openLogoMenu();
                else
                    enterSongMenu();
                break;
            case Input.KEY_O:
                SoundController.playSound(SoundEffect.MENUHIT);
                if ((logoState == LogoState.DEFAULT || logoState == LogoState.CLOSING) &&
                        !(input.isKeyDown(Input.KEY_RCONTROL) || input.isKeyDown(Input.KEY_LCONTROL)))
                {
                    openLogoMenu();
                }
                else
                {
                    openSettingsMenu();
                    //TODO input.consumeEvent();  // don't let options overlay consume this keypress
                }
                break;
            case Input.KEY_F:
                Options.toggleFPSCounter();
                break;
            // Music controls
            case Input.KEY_F1:
//            case Input.KEY_Z:
                Instances.player.prevTrack();
                break;
//            case Input.KEY_X:
//                if (MusicController.isPlaying())
//                {
//                    lastMeasureProgress = 0f;
//                    MusicController.setPosition(0);
//                }
//                else if (!MusicController.isTrackLoading())
//                    MusicController.resume();
//                UI.getNotificationManager().sendBarNotification("Play");
//                break;
            case Input.KEY_F3:
//            case Input.KEY_C:
                Instances.player.togglePause();
                break;
            case Input.KEY_F5:
//            case Input.KEY_V:
                Instances.player.nextTrack(true);
                break;
//            case Input.KEY_R:
//                nextTrack(true);
//                break;
            // Volume control
            case Input.KEY_UP:
                UI.changeVolume(1);
                break;
            case Input.KEY_DOWN:
                UI.changeVolume(-1);
                break;
            // Other shit (kww)
            case Input.KEY_B:
                Instances.visualizer.toggleFakeRotation();
                break;
            case Input.KEY_N:
                Instances.visualizer.switchSpectrumStyle();
                break;
        }
    }

    /**
     * Returns true if the coordinates are within the music position bar bounds.
     *
     * @param cx the x coordinate
     * @param cy the y coordinate
     */
//    private boolean musicPositionBarContains(float cx, float cy)
//    {
//        return ((cx > musicBarX && cx < musicBarX + musicBarWidth) &&
//                (cy > musicBarY && cy < musicBarY + musicBarHeight));
//    }

    /**
     * Resets the button states.
     */
    public void reset()
    {
        // reset logo
        logo.setX(widthCenter);
        logoOpen.setTime(0);
        logoClose.setTime(0);
        logoButtonAlpha.setTime(0);
        logoTimer = 0;
        logoState = LogoState.DEFAULT;

//        musicInfoProgress.setTime(musicInfoProgress.getDuration());
        optionsOverlay.deactivate();
        optionsOverlay.reset();
        showOptionsOverlay = false;
        optionsOverlayProgress.setTime(optionsOverlayProgress.getDuration());
        userOverlay.deactivate();
        showUserOverlay = false;
        userOverlayProgress.setTime(userOverlayProgress.getDuration());

        logo.resetHover();
        playButton.resetHover();
        exitButton.resetHover();
//        musicPlay.resetHover();
//        musicPause.resetHover();
//        musicNext.resetHover();
//        musicPrevious.resetHover();
//        if (repoButton != null)
//            repoButton.resetHover();
        updateButton.resetHover();
        restartButton.resetHover();
//        downloadsButton.resetHover();
        userButton.resetHover();
    }

    /**
     * Opens the logo menu.
     */
    private void openLogoMenu()
    {
        logoState = LogoState.OPENING;
        logoOpen.setTime(0);
        logoTimer = 0;
        playButton.getImage().setAlpha(0f);
        exitButton.getImage().setAlpha(0f);
    }

    /**
     * Opens the settings overlay.
     */
    public void openSettingsMenu()
    {
        showOptionsOverlay = true;
        optionsOverlayProgress.setTime(0);
        optionsOverlay.activate();
    }

    /**
     * Plays the next track, and adds the previous one to the stack.
     *
     * @param userInitiated {@code true} if this was user-initiated, false otherwise (track end)
     */
    public void nextTrack(boolean userInitiated)
    {
        //Instances.blinker.resetCounters();
        lastMeasureProgress = 0f;
        boolean isTheme = MusicController.isThemePlaying();
        if (isTheme && !userInitiated)
        {
            // theme was playing, restart
            // NOTE: not looping due to inaccurate track positions after loop
            MusicController.playAt(0, false);
            return;
        }
        SongMenu menu = (SongMenu) game.getState(Opsu.STATE_SONGMENU);
        BeatmapSetNode node = menu.setFocus(BeatmapSetList.get().getRandomNode(), -1, true, false);
        boolean sameAudio = false;
        if (node != null)
        {
            sameAudio = MusicController.getBeatmap().audioFilename.equals(node.getBeatmapSet().get(0).audioFilename);
            if (!isTheme && !sameAudio)
                previous.add(node.index);
        }
        if (Options.isDynamicBackgroundEnabled() && !sameAudio && !MusicController.isThemePlaying())
            bgAlpha.setTime(0);
//        musicInfoProgress.setTime(0);
    }

    /**
     * Plays the previous track, or does nothing if the stack is empty.
     */
    public void previousTrack()
    {
        if (!previous.isEmpty())
        {
            SongMenu menu = (SongMenu) game.getState(Opsu.STATE_SONGMENU);
            menu.setFocus(BeatmapSetList.get().getBaseNode(previous.pop()), -1, true, false);
            //Instances.blinker.resetCounters();
            lastMeasureProgress = 0f;
            if (Options.isDynamicBackgroundEnabled())
                bgAlpha.setTime(0);
        }
//        musicInfoProgress.setTime(0);
    }

    /**
     * Enters the song menu, or the downloads menu if no beatmaps are loaded.
     */
    private void enterSongMenu()
    {
        int state = Opsu.STATE_SONGMENU;
        if (BeatmapSetList.get().getMapSetCount() == 0)
        {
            ((DownloadsMenu) game.getState(Opsu.STATE_DOWNLOADSMENU)).notifyOnLoad("Download some beatmaps to get started!");
            state = Opsu.STATE_DOWNLOADSMENU;
        }
        game.enterState(state, new EasedFadeOutTransition(), new FadeInTransition());
    }

    @Override
    public void resize()
    {
        width = container.getWidth();
        height = container.getHeight();
        widthCenter = (int) (width / 2f);
        heightCenter = (int) (height / 2f);

        logoSizeMultiplier = height / GameImage.MENU_LOGO.getImage().getHeight() * 0.5f;
    }

    /** Logo states. */
    private enum LogoState {
        DEFAULT,
        OPENING,
        OPEN,
        CLOSING
    }
}
