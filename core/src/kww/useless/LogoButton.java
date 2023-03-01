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
    private float baseLogoRadius;

    //todo: better naming
    private float aspectRatio = 1f;

    private boolean isHovered;

    private Image tmp;

    public LogoButton(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.logoImage = GameImage.MENU_LOGO.getImage().copy();
        this.radius = this.baseLogoRadius = logoImage.getHeight() / 2f; // Assuming logo is a circle...

        Graphics.getGraphics().registerResizable(this);
    }

    public void draw(float sizeMultiplier)
    {
        this.radius = this.baseLogoRadius * aspectRatio * sizeMultiplier;
        drawDebug();

        logoImage.resizeTo(this.radius * 2, this.radius * 2).drawCentered(x, y);
    }

    public void update()
    {

    }

    public void hoverUpdate(int delta, float cx, float cy)
    {
        isHovered = contains(cx, cy);
        hoverUpdate(delta, contains(cx, cy));
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
        int d = delta * (isHover ? 1 : -1);

        // scale the button
//        if (scale.update(d))
//            updateHoverRadius();
    }

    public void mousePressed(int button)
    {
        if (!isHovered)
            return;

        if (button == Input.MOUSE_RIGHT_BUTTON)
            return;

        pressed();
    }

    public void drawDebug()
    {
        float x, y, w, h;
        x = this.x - radius;
        y = this.y - radius;
        w = h = radius * 2;
        Graphics.checkMode(Graphics.DrawMode.SPRITE);
        Graphics.getGraphics().setColor(Colors.SomeDebugColor1.fake());
        Graphics.getShapeDrawer().rectangle(x, y, w, h);
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
