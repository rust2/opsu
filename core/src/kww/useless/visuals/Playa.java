package kww.useless.visuals;

import com.badlogic.gdx.utils.Array;
import fluddokt.opsu.fake.Color;
import fluddokt.opsu.fake.Graphics;
import fluddokt.opsu.fake.Image;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.audio.MusicController;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;
import itdelatrisu.opsu.beatmap.Beatmap;
import itdelatrisu.opsu.ui.Fonts;
import itdelatrisu.opsu.ui.MenuButton;
import itdelatrisu.opsu.ui.UI;
import itdelatrisu.opsu.ui.animations.AnimatedValue;
import itdelatrisu.opsu.ui.animations.AnimationEquation;
import kww.useless.Instances;
import kww.useless.UselessUtils;
import kww.useless.interfaces.IInitable;
import kww.useless.interfaces.IResizable;
import lombok.Getter;
import lombok.var;

@SuppressWarnings("FieldCanBeLocal")
public class Playa implements IInitable, IResizable {

    @Getter private float x, y, width = 400, height = 130, margin = 5;
    private float widthCenter, heightCenter;
    private float musicBarX, musicBarY, musicBarWidth, musicBarHeight;
    private final int cornerRadius = 5;

    private final int beatmapTitleOffset = 15;
    private final int beatmapArtistOffset = 20;
    private float buttonBox_height; // 48 + 6?

    /** Point in time after which the current track will be restarted on triggering a "previous track" action. */
    private final int restart_cutoff_point = 5000;

    private final Color grayBg = new Color255(25, 25, 25, 0.4f).fake();
    private final Color grayBgBottom = new Color255(25, 25, 25, 0.5f).fake();
    private final Color whitePeopleBeLike = Colors.GrayF.cpy().fake(); // todo: should not create a waste copy
    private final Color yellowdarker = Colors.YellowDarker.cpy().setAlpha(.5f).fake();
    private final Color yellow = Colors.Yellow.cpy().fake();
    private final Graphics g = Graphics.getGraphics();

    /** Music control buttons. */
    private MenuButton musicPlay, musicPause, musicNext, musicPrevious;

    private String title = "title";
    private String artist = "artist";
    private Image background;

    private final Array<MoveableBg> bgArray = new Array<>(true, 4);

    /** Whether this player can be updated */
    public boolean active;
    /** Player is active but user still can't use it */
    public boolean canBeUsed;
    public LocalState state = LocalState.Closed;

    public enum LocalState {
        Closed,
        InOpen,
        Opened,
        InClose
    }

    private boolean isBgAnimated = true; //todo: settings?

//    public interface PlayaOverlayListener {
//        /** Notification that the overlay was closed. */
//        void close();
//    }
//
//    /** The event listener. */
//    private PlayaOverlayListener listener;

    private AnimatedValue verticalPos;

    public Playa()
    {
        Graphics.getGraphics().registerResizable(this);
    }

    @Override
    public void init()
    {
        resize();
        verticalPos = new AnimatedValue(350, y - 15, y, AnimationEquation.OUT_BACK);

        background = UselessUtils.Graphical.getScaledBg(GameImage.PLAYER_DEFAULT_BG.getImage());

        musicPlay = new MenuButton(GameImage.KWW_MUSIC_PLAY.getImage());
        musicPause = new MenuButton(GameImage.KWW_MUSIC_PAUSE.getImage());
        musicNext = new MenuButton(GameImage.KWW_MUSIC_NEXT.getImage());
        musicPrevious = new MenuButton(GameImage.KWW_MUSIC_PREVIOUS.getImage());

        repositionButtons();
    }

    @Override
    public void resize()
    {
        x = Instances.container.getWidth() - width - margin;
        y = Instances.toolbar.getHeight() + margin;
        widthCenter = x + width / 2f;
        heightCenter = y + height / 2f;

        musicBarX = x;
        musicBarY = y + height - cornerRadius * 2;
        musicBarWidth = width;
        musicBarHeight = cornerRadius * 2;

        // do not try to set buttons position if buttons are not created
        if (musicPlay != null)
            repositionButtons();
    }

    private void repositionButtons()
    {
        //too ugly, tbh...
        float buttonWidth = GameImage.KWW_MUSIC_PLAY.getImage().getWidth();
        float buttonHeight = GameImage.KWW_MUSIC_PLAY.getImage().getHeight();
        buttonBox_height = buttonHeight + 3 * 2;
        float buttonBox_center = buttonBox_height / 2f;

        float buttonY = musicBarY - buttonBox_center;

        musicPlay.setX(widthCenter).setY(buttonY);
        musicPause.setX(widthCenter).setY(buttonY);
        musicNext.setX(widthCenter + buttonWidth).setY(buttonY);
        musicPrevious.setX(widthCenter - buttonWidth).setY(buttonY);
    }

    private void updateButtonsPosition()
    {
        y = verticalPos.getValue();
        musicBarY = y + height - cornerRadius * 2; // it has to be updated, too

        float btnY = musicBarY - buttonBox_height / 2f;
        musicPlay.setY(btnY);
        musicPause.setY(btnY);
        musicNext.setY(btnY);
        musicPrevious.setY(btnY);
    }

    public void draw()
    {
        if (!active)
            return;

        renderDrawer();
    }

    public void renderDrawer()
    {
        // background
        g.setColor(whitePeopleBeLike);
        Graphics.Masked.shapeMaskBegin();
        Graphics.Masked.fillRoundRectUnsafe(x, y, width, height, cornerRadius);
        Graphics.Masked.spriteMaskedBegin();

        if (isBgAnimated)
        {
            Graphics.setUnsafe(true);

            // render all moving images, otherwise render static picture...
            if (!bgArray.isEmpty())
            {
                for (MoveableBg curBg : bgArray)
                {
                    g.drawTextureUpsideDown(
                            curBg.bg.getTextureRegion().getTexture(),
                            x + curBg.x.getValue(), y, curBg.bg.getWidth(), curBg.bg.getHeight()
                    );
                }
            }
            else
            {
                g.drawTextureUpsideDown(
                        background.getTextureRegion().getTexture(),
                        x, y, background.getWidth(), background.getHeight()
                );
            }
        }
        else
        {
            g.roundRectMask(x, y, width, height, cornerRadius);
            g.drawTextureUpsideDown(
                    background.getTextureRegion().getTexture(),
                    x, y, background.getWidth(), background.getHeight()
            );
            Graphics.setUnsafe(true);
        }

        //todo: some sizes' refactoring

        // tint: layer 1
        g.setColor(grayBg);
        Graphics.getShapeDrawer().filledRectangle(x, y, width, height);

        // tint: layer 2
        g.setColor(grayBgBottom);
        Graphics.getShapeDrawer().filledRectangle(x, musicBarY - buttonBox_height, width, buttonBox_height + musicBarHeight);

        // metadata
        g.setColor(whitePeopleBeLike);
        UselessUtils.Text.drawCentered(Fonts.MEDIUM, widthCenter, y + beatmapTitleOffset, title);
        UselessUtils.Text.drawCentered(Fonts.SMALLBOLD, widthCenter, y + beatmapTitleOffset + beatmapArtistOffset, artist);

        // buttons
        if (MusicController.isPlaying()) musicPause.draw();
        else musicPlay.draw();
        musicNext.draw();
        musicPrevious.draw();

        // bar: bg
        g.setColor(yellowdarker);
        Graphics.getShapeDrawer().filledRectangle(x, musicBarY, width, musicBarHeight);

        // bar: fill
        float musicBarPosition = Math.min((float) MusicController.getPosition(false) / MusicController.getDuration(), 1f);
        if (musicBarPosition > 0f)
        {
            g.setColor(yellow);
            Graphics.getShapeDrawer().filledRectangle(x, musicBarY, width * musicBarPosition, musicBarHeight);
        }
        else UselessUtils.Text.drawCentered(Fonts.SMALL, widthCenter, y + height - Fonts.SMALL.getLineHeight(), "Seeking unavailable...", whitePeopleBeLike);

        Graphics.setUnsafe(false);
        Graphics.Masked.spriteMaskedEnd();
    }

    public void update(int delta)
    {
        switch (state)
        {
            case InOpen:
            {
                if (verticalPos.update(delta))
                {
                    active = true;
                }
                else
                {
                    state = LocalState.Opened;
                    canBeUsed = true;
                }
                break;
            }
            case InClose:
            {
                if (!verticalPos.update(-delta))
                {
                    verticalPos.setTime(0);
                    state = LocalState.Closed;
                    canBeUsed = false;
                    active = false;
                }
                break;
            }
            case Opened:
            {
                //active = true;
                break;
            }
            case Closed:
            {
                //active = false;
                break;
            }
        }

        updateButtonsPosition();

        for (int i = 0; i < bgArray.size; i++)
        {
            var curBg = bgArray.get(i);

            if (curBg == null)
                continue;

            curBg.x.update(delta);

            if (curBg.x.isFinished())
                bgArray.removeIndex(i);
        }
    }

    /**
     * Returns true if the coordinates are within the music position bar bounds
     * and the player is actually visible.
     *
     * @param cx the x coordinate
     * @param cy the y coordinate
     */
    public boolean isMusicBarHovered(float cx, float cy)
    {
        if (!active)
            return false;

        if (!canBeUsed)
            return false;

        return ((cx > musicBarX && cx < musicBarX + musicBarWidth) &&
                (cy > musicBarY && cy < musicBarY + musicBarHeight));
    }

    /** Returns true if this event was consumed by my hook */
    public boolean mousePressed(int button, int x, int y)
    {
        if (!active)
            return false;

        if (!canBeUsed)
            return false;

        // music position bar
        if (isMusicBarHovered(x, y))
        {
            int duration = MusicController.getDuration();
            if (duration > 1f)
            {
                //Instances.blinker.resetCounters();
                Instances.mainMenu.setLastMeasureProgress(0f);
                float pos = (x - musicBarX) / musicBarWidth;
                MusicController.setPosition((int) (pos * duration));
            }
            return true;
        }

        // music button actions
        if (musicPlay.contains(x, y))
        {
            SoundController.playSound(SoundEffect.MENUCLICK);
            togglePause();
            return true;
        }
        else if (musicNext.contains(x, y))
        {
            SoundController.playSound(SoundEffect.MENUCLICK);
            nextTrack(true);
            return true;
        }
        else if (musicPrevious.contains(x, y))
        {
            SoundController.playSound(SoundEffect.MENUCLICK);
            prevTrack();
            return true;
        }

        return false;
    }

    public void trackChanged(TrackChangeDirection direction)
    {
        trackChanged(MusicController.getBeatmap(), direction);
    }

    public void trackChanged(Beatmap beatmap, TrackChangeDirection direction)
    {
        updateMeta(beatmap);
        updateBg(beatmap, direction);
    }

    public void updateMeta(Beatmap beatmap)
    {
        title = beatmap.getTitle();
        artist = beatmap.getArtist();
    }

    private void updateBg(Beatmap beatmap, TrackChangeDirection direction)
    {
        var newBackground = UselessUtils.Graphical.getScaledBg(beatmap);

        if (active)
        {
            switch (direction)
            {
                case Next:
                    bgArray.add(new MoveableBg(newBackground, width, 0));
                    bgArray.add(new MoveableBg(background, 0, -width));
                    break;

                case Prev:
                    bgArray.add(new MoveableBg(newBackground, -width, 0));
                    bgArray.add(new MoveableBg(background, 0, width));
                    break;
            }
        }

        background = newBackground;
    }

    public void deactivate()
    {
        active = false;
        canBeUsed = false;
        state = LocalState.Closed;
        Instances.toolbar.toolbarMusicButton.enabled = false; // todo: maybe we should disable all toolbar buttons on scene change?
    }


    private class MoveableBg {
        AnimatedValue x;
        Image bg;

        public MoveableBg(Image bg, float from, float to)
        {
            this.bg = bg;
            this.x = new AnimatedValue(500, from, to, AnimationEquation.OUT_CUBIC);
        }
    }

    public enum TrackChangeDirection {
        None,
        Next,
        Prev
    }

    public void togglePause()
    {
        if (MusicController.isPlaying())
        {
            MusicController.pause();
            UI.getNotificationManager().sendBarNotification("Pause");
        }
        else if (!MusicController.isTrackLoading())
        {
            MusicController.resume();
            UI.getNotificationManager().sendBarNotification("Play");
        }
    }

    public void prevTrack()
    {
        if (MusicController.getPosition(false) > restart_cutoff_point)
        {
            MusicController.setPosition(0);
        }
        else
        {
            Instances.mainMenu.previousTrack();
            trackChanged(TrackChangeDirection.Prev);
            UI.getNotificationManager().sendBarNotification("<< Prev");
        }
    }

    public void nextTrack(boolean isUserInitiated)
    {
        Instances.mainMenu.nextTrack(isUserInitiated);
        trackChanged(TrackChangeDirection.Next);
        UI.getNotificationManager().sendBarNotification(">> Next");
    }
}
