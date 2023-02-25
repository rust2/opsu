package kww.useless.buttons.toolbar;

import com.badlogic.gdx.math.Vector2;
import fluddokt.opsu.fake.Color;
import fluddokt.opsu.fake.Graphics;
import fluddokt.opsu.fake.Image;
import itdelatrisu.opsu.ui.animations.AnimatedValue;
import itdelatrisu.opsu.ui.animations.AnimationEquation;
import kww.useless.interfaces.IButton;
import kww.useless.visuals.Colors;
import lombok.Getter;

public class ToolbarButtonV1 implements IButton {
    /** Bounds: position<br><h2>Top-left point</h2> */
    @Getter protected final Vector2 position = new Vector2();
    /** Bounds: size */
    @Getter protected final Vector2 size = new Vector2();

    /** The icon of this button */
    private Image icon;
    /** The icon coordinates. */
    private final Vector2 iconPosition = new Vector2();

    private final Color bgColor = Colors.GrayF.cpy().fake();
    private final AnimatedValue bgAlpha = new AnimatedValue(100, 0f, .24f, AnimationEquation.LINEAR);

    public boolean enabled;

    /**
     * Creates the empty {@link Toolbar} button instance with no position, nor size set
     */
    public ToolbarButtonV1() {}

    /**
     * Creates the {@link Toolbar} button instance with no position, nor size set
     *
     * @param img the icon of this button
     */
    public ToolbarButtonV1(Image img)
    {
        this.icon = img;
    }

    /**
     * Sets this button's position with the anchor point at the top-left corner
     *
     * @param x the button's position on the X axis
     * @param y the button's position on the Y axis
     *
     * @apiNote You don't have to change Y position for this type button
     */
    public void setPosition(float x, float y)
    {
        position.x = x;
        position.y = y;
        updateIconPosition();
    }

    /**
     * Shifts this {@link ToolbarButtonV1} by the desired offset
     *
     * @param offset the offset by which this button has to be shifted
     */
    public void shiftX(float offset)
    {
        position.x += offset;
        iconPosition.x += offset;
    }

    /** Same as {@link ToolbarButtonV1#shiftX(float)} but for Y axis */
    public void shiftY(float offset)
    {
        position.y += offset;
        iconPosition.y += offset;
    }

    /**
     * Sets this button's size
     *
     * @param a the size of a button's edge
     */
    public void setSize(float a)
    {
        setSize(a, a);
    }

    /**
     * Sets this button's size
     *
     * @param x the size of a button on the X axis
     * @param y the size of a button on the Y axis
     */
    public void setSize(float x, float y)
    {
        size.x = x;
        size.y = y;
        updateIconPosition();
    }

    /** Updates icon position<br>This has to be called whenever the button's size or position has been changed */
    public void updateIconPosition()
    {
        // The icon has to be at the center of a button
        iconPosition.x = position.x + size.x / 2f;
        iconPosition.y = position.y + size.y / 2f;
    }

    /**
     * Draw the button.
     */
    public void draw()
    {
        bgColor.a = bgAlpha.getValue();
        Graphics.getGraphics().setColor(bgColor);
        Graphics.getGraphics().fillRect(position.x, position.y, size.x, size.y);
        if (icon != null)
            icon.drawCentered(iconPosition.x, iconPosition.y, Colors.GrayF.fake());
    }

    /**
     * Returns true if the coordinates are within the button bounds.
     *
     * @param cx the x coordinate
     * @param cy the y coordinate
     */
    public boolean contains(float cx, float cy)
    {
        return ((cx > position.x && cx < position.x + size.x) &&
                (cy > position.y && cy < position.y + size.y));
    }

    /**
     * Processes a hover action depending on whether or not the cursor
     * is hovering over the button.
     *
     * @param delta the delta interval
     * @param cx    the x coordinate
     * @param cy    the y coordinate
     */
    public boolean hoverUpdate(int delta, float cx, float cy)
    {
        return hoverUpdate(delta, contains(cx, cy));
    }

    /**
     * Processes a hover action depending on whether or not the cursor
     * is hovering over the button.
     *
     * @param delta   the delta interval
     * @param isHover true if the cursor is currently hovering over the button
     */
    public boolean hoverUpdate(int delta, boolean isHover)
    {
        // Do nothing if button is active
        if (enabled)
            return false;

        int d = delta * (isHover ? 1 : -1);
        bgAlpha.update(d);

        return true;
    }

    @Override
    public void pressed()
    {
        // No action for default button
    }

    public void toggle()
    {
        enabled = !enabled;
    }
}
