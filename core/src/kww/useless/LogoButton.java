package kww.useless;

import com.badlogic.gdx.math.Circle;
import fluddokt.opsu.fake.Graphics;
import fluddokt.opsu.fake.Image;
import fluddokt.opsu.fake.Input;
import itdelatrisu.opsu.GameImage;
import kww.useless.interfaces.IButton;
import kww.useless.interfaces.IResizable;
import kww.useless.visuals.Colors;

import static kww.useless.Instances.container;

//Todo: Visualizer should be a child of a logo button
public class LogoButton extends Circle implements IButton, IResizable {
    /**
     * Logo icon
     * <p> You can't just change logo image, right?...
     */
    private Image logoImage;

    //todo: better naming
    private float aspectRatio = 1f;

    private boolean isHovered;

    private Image tmp;

    public LogoButton(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.logoImage = GameImage.MENU_LOGO.getImage().copy();
        this.radius = logoImage.getHeight(); // Assuming logo is a circle...
    }

    public void draw(float sizeMultiplier)
    {
        this.radius = sizeMultiplier;
        drawDebug();

        //logoImage = logoImage.getScaledCopy(sizeMultiplier);
        //logoImage.drawCentered(x, y, Colors.GrayF.fake());

        float xScaleOffset = 0f, yScaleOffset = 0f;
        if (sizeMultiplier != 1f)
        {
            tmp = logoImage.getScaledCopy(sizeMultiplier);
            xScaleOffset = tmp.getWidth() / 2f - radius;
            yScaleOffset = tmp.getHeight() / 2f - radius;
        }

        tmp.draw(x - radius - xScaleOffset, y - radius - yScaleOffset, Colors.GrayF.fake());


        Graphics.checkMode(Graphics.DrawMode.SPRITE);
        Graphics.getGraphics().setColor(Colors.SomeDebugColor1.fake());
        Graphics.getShapeDrawer().filledCircle(x, y, radius);
    }

    public void update()
    {

    }

    public void hoverUpdate(int delta, float cx, float cy)
    {
        isHovered = contains(cx, cy);
        hoverUpdate(delta, contains(cx, cy));
    }

    @Override
    public boolean contains(float x, float y)
    {
        x = x - this.x;
        y = y - this.y;
        return x * x + y * y <= radius * radius;
    }

    /**
     * Processes a hover action depending on whether or not the cursor
     * is hovering over the button.
     *
     * @param delta   the delta interval
     * @param isHover true if the cursor is currently hovering over the button
     */
    public void hoverUpdate(int delta, boolean isHover)
    {
        if (isHover)
            System.out.println("hover");
        int d = delta * (isHover ? 1 : -1);

        // scale the button
//        if (scale.update(d))
//            updateHoverRadius();
    }

    public void mousePressed(int button)
    {
        if (!isHovered || button != Input.MOUSE_RIGHT_BUTTON)
            return;

        pressed();
    }

    public void drawDebug()
    {
        Graphics.checkMode(Graphics.DrawMode.SPRITE);
        Graphics.getGraphics().setColor(Colors.SomeDebugColor1.fake());
        Graphics.getShapeDrawer().rectangle(
                x - radius,
                y - radius,
                x + radius,
                y + radius
        );
    }

    @Override
    public void pressed()
    {
        System.out.println("You press da button");
    }

    @Override
    public void resize()
    {
        if (container.height < container.width)
            aspectRatio = (float) container.height / logoImage.getHeight() * 0.65f;
        else // Are you against me???
            aspectRatio = (float) container.width / logoImage.getWidth() * 0.65f;
    }
}
