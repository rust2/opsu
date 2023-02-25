package kww.useless.visuals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.deo.mvis.jtransforms.fft.FloatFFT_1D;
import fluddokt.opsu.fake.GameOpsu;
import fluddokt.opsu.fake.Graphics;
import itdelatrisu.opsu.audio.MusicController;
import itdelatrisu.opsu.beatmap.TimingPoint;
import itdelatrisu.opsu.ui.MenuButton;
import itdelatrisu.opsu.ui.UI;
import kww.useless.Instances;
import kww.useless.UselessUtils;
import kww.useless.UselessUtils.Samples;
import kww.useless.interfaces.IInitable;
import lombok.Getter;

import java.util.Arrays;

@SuppressWarnings({"FieldCanBeLocal", "FieldCanBeFinal"})
public class FFTScreen implements IInitable, Disposable {
    /** To change this color, buy opsu!supporter tag... */
    //private final Color255 theColorYouHaveToPayFor = new Color255().fromFake(Options.getSkin().getMenuGlowColor()).setAlpha(0.2f);
    private final Color255 theColorYouHaveToPayFor = new Color255(255, 255, 255, 0.2f);

    private FloatFFT_1D fft;
    private float[] audioData;
    private final int fftSize = 512; // 256; // AMPLITUDES_SIZE

    /** The number of bars to jump each decay iteration. */
    private final int index_change = 5;
    private int indexOffset;

    /** The maximum length of each bar in the visualiser. */
    private final float bar_length = 600f;

    /** The number of bars in one rotation of the visualiser. */
    private final int bars_per_visualiser = 200;//200;

    /** How many times we should stretch around the circumference (overlapping overselves). */
    private final int visualiser_rounds = 5;

    /** How much should each bar go down each millisecond (based on a full bar). */
    private final float decay_per_millisecond = 0.0024f;
    private final float decay = decay_per_millisecond * 1000;

    /** Number of seconds between each amplitude decay. */
    private final float time_between_updates = 0.050f;

    /** The minimum amplitude to show a bar. */
    private final float amplitude_dead_zone = 1f / bar_length;

    private final float[] frequencyAmplitudes = new float[fftSize];
    private final float[] temporalAmplitudes = new float[fftSize];
    public ShapeRenderer renderer;

    /** Cached variable */
    @Getter private boolean isKiaiTime = false;

    /** If the logo image does not actually touch image boundaries */
    private final int logoPadding = 9;

    private SpectrumStyle currentSpectrumStyle = SpectrumStyle.CIRCULAR;
    private int currentSpectrumStyleIndex = currentSpectrumStyle.ordinal();
    private boolean visualizerHasBeenChanged;

    private enum SpectrumStyle {
        CIRCULAR,
        PLAIN,
        NONE
    }

    Timer timer = new Timer();
    Timer.Task task_updateAmplitudes = new Timer.Task() {
        @Override
        public void run()
        {
            updateAmplitudes();
        }
    };

    // private jojo reference
    private MenuButton logo;

    private final int amplitudeModifier = 4194304; // note this is definitely NOT a random numba

    public FFTScreen()
    {
        GameOpsu.getInstance().registerDisposable(this);
    }

    @Override
    public void init()
    {
        if (Instances.mainMenu.getLogo() == null)
            throw new Error("init visualizer ONLY AFTER the logo button was created");

        //todo: change ShapeRenderer -> ShapeDrawer
        renderer = Graphics.getShapeRender();
        fft = new FloatFFT_1D(fftSize);
        audioData = new float[fftSize];

        logo = Instances.mainMenu.getLogo();

        timer.scheduleTask(task_updateAmplitudes, time_between_updates, time_between_updates);
    }

    public void updateAmplitudes()
    {
        if (UselessUtils.MusicController.isPaused())
        {
            Arrays.fill(temporalAmplitudes, 0);
        }
        else System.arraycopy(
                Samples.getSmoothedFFT(fftSize, UselessUtils.MusicController.getSamples(),
                                       2, fft), 0, temporalAmplitudes, 0, temporalAmplitudes.length
        );

        TimingPoint lastTimingPoint = MusicController.getLastTimingPoint();

        if (lastTimingPoint != null)
        {
            // invert variable
            if (isKiaiTime != lastTimingPoint.isKiaiTimeActive())
            {
                isKiaiTime = !isKiaiTime;
                System.out.println("Kiai now: " + isKiaiTime);
            }
        }

        if (fakeRotation)
        {
            for (int i = 0; i < bars_per_visualiser; i++)
            {
                float targetAmplitude = temporalAmplitudes[(i + indexOffset) % bars_per_visualiser] / amplitudeModifier * (isKiaiTime ? 1f : 0.75f);
                if (targetAmplitude > audioData[i])
                    audioData[i] = targetAmplitude;
            }
        }
        else
        {
            for (int i = 0; i < bars_per_visualiser; i++)
            {
                float targetAmplitude = temporalAmplitudes[i] / amplitudeModifier;
                if (targetAmplitude > audioData[i])
                    audioData[i] = targetAmplitude;
            }
        }

        indexOffset = (indexOffset + index_change) % bars_per_visualiser;
    }

    public void draw()
    {
        render(Gdx.graphics.getDeltaTime());
    }

    private final Color255 alpha = new Color255(.3f);
    private final float colorShift = 14.21f;
    private final float colorShift2 = 18.947f;
    private final float colorAmplitude = 3f;

    private void render(float delta)
    {
        Graphics.checkMode(Graphics.DrawMode.SHAPEFILLED); // workaround for this "shitty" graphics handling
        //renderer.begin(ShapeRenderer.ShapeType.Filled);

        float decayFactor = delta * decay * (isKiaiTime ? 1.25f : 1f);

        switch (currentSpectrumStyle)
        {
            case CIRCULAR:
            {
                final float x = logo.getX();
                final float y = logo.getY();

                // assuming logo is a circle
                // fixme: wrong naming/formula
                final float spriteRadius = (logo.getYRadius() * 0.96f) * Instances.mainMenu.getLogoScale();

                for (int i = 0; i < bars_per_visualiser; i++)
                {
                    float tmpAD = audioData[i];

                    // decay
                    audioData[i] -= (audioData[i] + 0.03) * decayFactor;
                    if (audioData[i] < 0)
                        audioData[i] = 0;

                    // don't draw if lower than dead zone
                    if (audioData[i] < amplitude_dead_zone)
                        continue;

                    //float barW = (float) (spriteRadius * Math.sqrt(2 * (1 - Math.cos(Math.toRadians(360f / bars_per_visualiser)))) / 2f) + 0.12f;
                    //float barW = (float) (spriteRadius * (Math.sqrt(2 * (1 - Math.cos(Math.toRadians(360f / bars_per_visualiser)))) / 2f + 0.01f));

                    float barW = spriteRadius * ((float) (Math.sqrt(2 * (1 - Math.cos(Math.toRadians(360f / bars_per_visualiser)))) / 2f) + 0.01f);
                    float barH = audioData[i] * bar_length * Instances.mainMenu.logoSizeMultiplier;

                    for (int j = 0; j < visualiser_rounds; j++)
                    {
                        float rotation_deg = i / (float) bars_per_visualiser * 360 + j * 360 / (float) visualiser_rounds;

                        //todo: Setting to choose between your defined color and hsv
                        //renderer.setColor(alpha.fromHsv(tmpAD * 256 * colorAmplitude + colorShift - colorShift2, 0.75f, 0.9f));
                        renderer.setColor(theColorYouHaveToPayFor);

                        renderer.rect(x, y + spriteRadius, 0f, -spriteRadius, barW, barH, 1f, 1f, rotation_deg); //negated rotation_deg
                    }
                }
                break;
            }
            case PLAIN:
            {
                for (int i = 0; i < bars_per_visualiser; i++)
                {
                    // decay
                    audioData[i] -= (audioData[i] + 0.03) * decayFactor;
                    if (audioData[i] < 0)
                        audioData[i] = 0;

                    // don't draw if lower than dead zone
                    if (audioData[i] < amplitude_dead_zone)
                        continue;

                    float barW = (float) (Instances.mainMenu.width / bars_per_visualiser);
                    float barH = audioData[i] * bar_length * 2;

                    renderer.setColor(theColorYouHaveToPayFor);
                    renderer.rect(barW * i, UselessUtils.container.height, barW, -barH);
                }
                break;
            }
        }
    }

    private boolean fakeRotation = true;

    public void toggleFakeRotation()
    {
        fakeRotation = !fakeRotation;
        UI.getNotificationManager().sendNotification("Fake rotation: " + fakeRotation, new fluddokt.opsu.fake.Color(255, 122, 30));
    }

    public void switchSpectrumStyle()
    {
        if (++currentSpectrumStyleIndex == SpectrumStyle.values().length)
            currentSpectrumStyleIndex = 0;
        currentSpectrumStyle = SpectrumStyle.values()[currentSpectrumStyleIndex];
        visualizerHasBeenChanged = true;

        UI.getNotificationManager().sendNotification("Spectrum style: " + currentSpectrumStyle, new fluddokt.opsu.fake.Color(52, 235, 189));
    }

    @Override
    public void dispose()
    {
        renderer.dispose();
    }
}
