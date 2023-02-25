package kww.useless.visuals;

import com.badlogic.gdx.Gdx;
import fluddokt.opsu.fake.Color;
import fluddokt.opsu.fake.Font;
import fluddokt.opsu.fake.Graphics;
import itdelatrisu.opsu.options.Options;
import itdelatrisu.opsu.ui.Fonts;
import kww.useless.Instances;
import kww.useless.interfaces.IResizable;

import static kww.useless.Instances.container;

/** Based on yugecin's approach */
public class FPSMeter implements IResizable {

    private final static Color255 GREEN = new Color255(171, 218, 25);
    private final static Color255 ORANGE = new Color255(255, 204, 34);
    private final static Color255 DARKORANGE = new Color255(255, 149, 24);

    private int x, y;

    int delta;
    int screenWidth, screenHeight;

    int width = 65;
    int widthOffset = 0;
    int height = 20;
    int heightOffset = 0; // 35

    Font font = Fonts.MEDIUM;

    /** Not only FPS meter... */
    public FPSMeter() {
        Graphics.getGraphics().registerResizable(this);
    }

    @Override
    public void resize()
    {
        screenWidth = container.getWidth();
        screenHeight = container.getHeight();

        x = screenWidth - 3 - widthOffset;
        y = screenHeight - 3 - heightOffset;
    }

    public void render(Graphics g)
    {
        if (!Options.isFPSCounterEnabled())
            return;

        delta = (int) Gdx.graphics.getDeltaTime() * 1000;

        int fpsDeviation = delta % Options.getTargetFPS(); // ?
        int targetFps = (int) (Options.getTargetFPS() * 0.9f) - fpsDeviation;
        int currentFps = Gdx.graphics.getFramesPerSecond();

        int xo;
        xo = drawText(g, getColor(targetFps, currentFps), currentFps + " fps", this.x, this.y);
        drawText(g, ORANGE, Instances.visualizer.rendered_rectangles + "", xo, this.y);
    }

    private Color255 getColor(int targetValue, int realValue)
    {
        if (realValue >= targetValue)
            return GREEN;
        if (realValue >= targetValue * 0.85f)
            return ORANGE;

        return DARKORANGE;
    }

    /**
     * @return x position where the next block can be drawn (right aligned)
     */
    private int drawText(Graphics g, Color255 color, String text, int x, int y)
    {
        g.setColor(color.fake());
        g.fillRoundRect3(x - width, y - height, width, height, 6);
        int fw = font.getWidth(text);
        int fh = font.getHeight(text);
        font.drawString(x - width / 2f - fw / 2f,
                        y - height / 2f - fh / 2f,
                        text, Color.black);
//        font.drawString(x - width + 3, y + 3, text, Color.black);

        return x - width - 6;
    }
}
