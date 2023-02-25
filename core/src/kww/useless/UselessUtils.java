package kww.useless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.deo.mvis.jtransforms.fft.FloatFFT_1D;
import fluddokt.opsu.fake.*;
import itdelatrisu.opsu.ErrorHandler;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Opsu;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;
import itdelatrisu.opsu.beatmap.Beatmap;
import itdelatrisu.opsu.beatmap.ImageLoader;
import itdelatrisu.opsu.options.Options;
import itdelatrisu.opsu.ui.UI;
import kww.useless.visuals.Profiling;

import java.beans.beancontext.BeanContext;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.zip.Deflater;

import static java.lang.Math.abs;

public class UselessUtils {
    public static GameContainer container = Opsu.opsu.container;
    public static StateBasedGame game = Opsu.opsu;
    public static final Random RANDOM = new Random();
    public static final Color hitboxColorGreen = new Color(
            0, 255, 12, Math.round(50 * 2.55f) // 60%
    );

    public static class Hitbox {
        //todo maybe should use gdx batch drawing instead of faked one?
        public static void drawFilledHitbox(Graphics g, float x, float y, float width, float height)
        {
            g.setColor(hitboxColorGreen);
            g.fillRect(x, y, width, height);
        }

        public static void drawOutlineHitbox(Graphics g, float x, float y, float width, float height)
        {
            g.setColor(hitboxColorGreen);
            g.drawRect(x, y, width, height);
        }
    }

    public static class Text {
        public static void drawCentered(Font font, float x, float y, String text)
        {
            font.drawString(x - font.getWidth(text) / 2f, y, text);
        }

        public static void drawCentered(Font font, float x, float y, String text, Color color)
        {
            font.drawString(x - font.getWidth(text) / 2f, y, text, color);
        }

        public static void drawRightAligned(Font font, float x, float y, String text, Color color)
        {
            font.drawString(x - font.getWidth(text), y, text, color);
        }
    }

    public static class Options {
        public static float getMobileUIScale()
        {
            return itdelatrisu.opsu.options.Options.getMobileUIScale(0.5f);
        }
    }

    public static class Game {
        public static void restart(GameContainer container)
        {
            container.setForceExit(false);
            container.exit();
        }

        public static void toggleDebug()
        {
            Profiling.toggle();
        }
    }

    public static class MusicController {
        static short[] empty = new short[1024];

        public static short[] getSamples()
        {
            try
            {
                return itdelatrisu.opsu.audio.MusicController.getPlayer().getMusic().getPlayer().getRbuf();
            }
            catch (NullPointerException e)
            {
                System.err.println("kww.useless.UselessUtils.MusicController.getSamples: " + e.getMessage());
                return empty;
            }
        }

        //weird, ya?
        public static boolean isPaused()
        {
            try
            {
                return itdelatrisu.opsu.audio.MusicController.getPlayer().getMusic().getPlayer().isPaused();
            }
            catch (NullPointerException e)
            {
                System.err.println("kww.useless.UselessUtils.MusicController.isPaused: " + e.getMessage());
                return true;
            }
        }
    }

    public static class Samples {
        public static float[] getSmoothedFFT(int numSamples, short[] samplesIN)
        {
            float[] fullSamples = new float[numSamples];
            for (int i = 0; i < numSamples; i++)
            {
                fullSamples[i] = samplesIN[i];
            }
            Fft.transformRadix2float(fullSamples);
            return fullSamples;
        }

        public static float[] getSmoothedFFT(int numSamples, short[] samplesIN, int fftDirty, FloatFFT_1D fft_1D)
        {
            int len = numSamples + fftDirty * 2;
            float[] fullSamples = new float[len];
            float[] cutSamples = new float[numSamples];
            //System.arraycopy(samplesIN, 0, fullSamples, 0, len);
            for (int i = 0; i < len; i++)
            {
                fullSamples[i] = samplesIN[i];
            }
            fft_1D.realForward(fullSamples);
            for (int i = fullSamples.length - fftDirty * 2; i < fullSamples.length; i++)
            {
                fullSamples[i] = fullSamples[fullSamples.length - fftDirty * 2 - 1];
            }
            smoothSamples(fullSamples, 1, 1, true);
            System.arraycopy(fullSamples, fftDirty, cutSamples, 0, numSamples);
            return cutSamples;
        }

        public static float[] getSmoothedFFT(int pos, int numSamples, float[] samplesIN, int fftDirty, FloatFFT_1D fft_1D)
        {
            float[] fullSamples = new float[numSamples + fftDirty * 2];
            float[] cutSamples = new float[numSamples];
            System.arraycopy(samplesIN, pos, fullSamples, 0, numSamples + fftDirty * 2);
            fft_1D.realForward(fullSamples);
            for (int i = fullSamples.length - fftDirty * 2; i < fullSamples.length; i++)
            {
                fullSamples[i] = fullSamples[fullSamples.length - fftDirty * 2 - 1];
            }
            smoothSamples(fullSamples, 2, 2, true);
            System.arraycopy(fullSamples, fftDirty, cutSamples, 0, numSamples);
            return cutSamples;
        }

        public static void smoothSamples(float[] samples, int smoothingFactor, int smoothingSampleRange, boolean absolute)
        {
            for (int i = 0; i < smoothingFactor; i++)
            {
                for (int i2 = smoothingSampleRange; i2 < samples.length - smoothingSampleRange; i2++)
                {
                    float sum = absolute ? abs(samples[i2]) : samples[i2]; // middle sample
                    for (int i3 = 1; i3 <= smoothingSampleRange; i3++)
                    {
                        sum += (absolute ? abs(samples[i2 + i3]) : samples[i2 + i3]);
                        sum += (absolute ? abs(samples[i2 - i3]) : samples[i2 - i3]);
                        // samples to the left and to the right
                    }
                    samples[i2] = sum / (float) (smoothingSampleRange * 2 + 1); //smooth out the sample
                }
            }
        }
    }

    public static class Maths {
        public static float rad0 = (float) Math.toRadians(0f);
        public static float rad90 = (float) Math.toRadians(90f);
        public static float rad180 = (float) Math.toRadians(180f);
        public static float rad270 = (float) Math.toRadians(270f);

        /** Whether num is between start and end. (Inclusive) */
        public static boolean inRange(float start, float num, float end)
        {
            //endTimeDiff > 1500 && endTimeDiff < 2000
            return (num >= start && num <= end);
        }

        /**
         * Source: <a href="https://stackoverflow.com/a/35833800">StackOverflow</a> (round2)
         *
         * @param value a float number you wanna round
         * @param scale how much numbers after a point you wanna save
         */
        public static float round(float value, int scale)
        {
            int pow = 10;
            for (int i = 1; i < scale; i++)
            {
                pow *= 10;
            }
            float tmp = value * pow;
            return ((float) ((int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp))) / pow;
        }

        /**
         * Round the value to 2 numbers after the point
         * <p>
         * Source: <a href="https://stackoverflow.com/a/46734064">StackOverflow</a>
         *
         * @param value a float number you wanna round
         */
        public static float round(float value)
        {
            return ((int) ((value + 0.005f) * 100)) / 100f;
        }
    }

    public static class Graphical {
        /**
         * @return beatmap background {@link Image} or a fallback one
         */
        public static Image getScaledBg(Beatmap beatmap)
        {
            Image bgImage;

            if (beatmap != null)
            {
                bgImage = beatmap.getBackground();
            }
            else
            {
                if (Instances.fallback_bg == null)
                    Instances.fallback_bg = GameImage.PLAYER_DEFAULT_BG.getImage();
                bgImage = Instances.fallback_bg;
            }

            float imgWidth = bgImage.getWidth();
            float imgHeight = bgImage.getHeight();
            float factor;

            if (imgWidth > imgHeight)
            {
                factor = imgWidth / Instances.player.getWidth();
            }
            else
            {
                factor = imgHeight / Instances.player.getHeight();
            }

            return bgImage.getScaledCopy(1 / factor);
        }

        public static Image getScaledBg(Image bgImage)
        {
            if (bgImage == null)
            {
                if (Instances.fallback_bg == null)
                    Instances.fallback_bg = GameImage.PLAYER_DEFAULT_BG.getImage();
                bgImage = Instances.fallback_bg;
            }

            float imgWidth = bgImage.getWidth();
            float imgHeight = bgImage.getHeight();
            float factor;

            if (imgWidth > imgHeight)
            {
                factor = imgWidth / Instances.player.getWidth();
            }
            else
            {
                factor = imgHeight / Instances.player.getHeight();
            }

            return bgImage.getScaledCopy(1 / factor);
        }
    }

    public static void savePixmapTo(String absolutePath, Pixmap pixmapToSave, boolean flip)
    {
        System.out.println("Writing to: " + absolutePath);
        PixmapIO.writePNG(new FileHandle(absolutePath), pixmapToSave, Deflater.DEFAULT_COMPRESSION, flip);
    }

    public static void takeScreenshot()
    {
        // create screen shot
        final Pixmap source = Pixmap.createFromFrameBuffer(0, 0, container.getWidth(), container.getHeight());

        // make it opaque
        ByteBuffer pixels = source.getPixels();

        // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
        for (int i = 3; i < size; i += 4)
        {
            pixels.put(i, (byte) 255);
        }

        // check for directory
        FileHandle screenshotDir = itdelatrisu.opsu.options.Options.getScreenshotDir().getFileHandle();
        if (!screenshotDir.isDirectory())
        {
            ErrorHandler.error("Failed to create screenshot at '" + screenshotDir.name() + "'.", null, false);
            return;
        }

        // create file name
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String name = "screenshot_" + date.format(new Date()) + ".png";

        // play screamer sound
        SoundController.playSound(SoundEffect.SHUTTER);
        UI.getNotificationManager().sendNotification("Screenshot saved");

        // save a picture
        savePixmapTo(screenshotDir.child(name).path(), source, true);

        // dispose disposable
        source.dispose();
    }
}