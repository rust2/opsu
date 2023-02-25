package kww.useless.visuals;

import fluddokt.opsu.fake.GameContainer;
import itdelatrisu.opsu.audio.MusicController;
import kww.useless.Instances;
import kww.useless.interfaces.IInitable;
import kww.useless.visuals.blinker.Flash;

public class Blinker implements IInitable {
    private float width, height;
    private Flash leftFlash, rightFlash;
    private GameContainer container;

    /** Last measure progress value. */
    private float lastMeasureProgress = 0f;
    private int beatCounter = 0;

    public Blinker() {}

    @Override
    public void init(GameContainer container)
    {
        this.container = container;
        resize();
    }

    public void resize()
    {
        height = container.getHeight();
        width = height * .5f;

        leftFlash = new Flash(
                0, 0, width, height,
                Colors.GrayF, Colors.WhiteTransparent, Colors.WhiteTransparent, Colors.GrayF
        );

        rightFlash = new Flash(
                container.getWidth() - width, 0, width, height,
                Colors.WhiteTransparent, Colors.GrayF, Colors.GrayF, Colors.WhiteTransparent
        );
    }

    public void update(int delta)
    {
        Float measureProgress = MusicController.getMeasureProgress(2);
        if (measureProgress != null)
        {
            if (measureProgress < lastMeasureProgress)
            {
                if (++beatCounter == 4)
                    beatCounter = 0;

                if (!Instances.visualizer.isKiaiTime())
                {
                    /*
                     * 0 - left
                     * 1 - right
                     * 2 - left
                     * 3 - both
                     */
                    switch (beatCounter)
                    {
                        case 0:
                        case 2:
                            leftFlash.draw();
                            break;
                        case 3:
                            leftFlash.draw();
                        case 1:
                            rightFlash.draw();
                            break;
                    }
                }

                System.out.println("beat " + beatCounter);
            }

            leftFlash.update(delta);
            rightFlash.update(delta);

            lastMeasureProgress = measureProgress;
        }
    }

    public void resetCounters()
    {
        lastMeasureProgress = 0f;
        beatCounter = 0;
    }

    public void draw()
    {

    }
}
