package kww.useless.visuals.blinker;

import fluddokt.opsu.fake.Color;
import fluddokt.opsu.fake.Graphics;
import itdelatrisu.opsu.ui.animations.AnimatedValue;
import itdelatrisu.opsu.ui.animations.AnimationEquation;
import kww.useless.visuals.Color255;
import kww.useless.visuals.Colors;

public class Flash {
    private float x, y, w, h;
    private Color255 topLeft, topRight, bottomLeft, bottomRight;
    private Color color = Colors.GrayF.cpy().fake();
    private AnimatedValue alpha = new AnimatedValue(500, .4f, 0f, AnimationEquation.LINEAR);
    Graphics g = Graphics.getGraphics();
    boolean needToDraw = false;

    public Flash(float x, float y, float w, float h, Color255 tL, Color255 tR, Color255 bR, Color255 bL)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.topLeft = tL;
        this.topRight = tR;
        this.bottomLeft = bL;
        this.bottomRight = bR;
    }

    public void draw()
    {
        needToDraw = true;
    }

    public void update(int delta)
    {
        if (needToDraw)
        {
            alpha.update(delta);
            color.a = alpha.getValue();
            g.setColor(color);
            g.fillRectGradient(x, y, w, h, topLeft, topRight, bottomRight, bottomLeft);
            if (alpha.isFinished())
                needToDraw = false;
        }
    }
}
