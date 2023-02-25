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

@SuppressWarnings("UnusedReturnValue")
public class ToolbarButton implements IButton {
    /** Bounds: position<br><h2>Top-left point</h2> */
    @Getter protected final Vector2 position = new Vector2();
    /** Bounds: size */
    @Getter protected final Vector2 size = new Vector2();

    /** The icon of this button */
    private Image icon;
    /** The icon coordinates. */
    private final Vector2 iconPosition = new Vector2();

    /** Button's parent object.<br>Button will inherit its parent's size */
    private Toolbar parent;

    /** The side at which this button should be drawn */
    public Toolbar.Side side = Toolbar.Side.Left;

    Graphics g = Graphics.getGraphics();

    private final Color bgColor = Colors.GrayF.cpy().fake();
    private final AnimatedValue bgAlpha = new AnimatedValue(100, 0f, .24f, AnimationEquation.LINEAR);

    /** Is this button enabled now? */
    public boolean enabled;

    /**
     * Creates the empty {@link Toolbar} button with no position, size, or icon set
     */
    public ToolbarButton() {}

    /**
     * Creates the {@link Toolbar} button instance with no position or size set
     *
     * @param img the icon of this button
     */
    public ToolbarButton(Image img)
    {
        this.icon = img;
    }

    /**
     * Sets parent of this {@link ToolbarButton}.
     * <br>
     * The button will inherit parent's Y position and a height as its default size
     */
    public ToolbarButton setParent(Toolbar parent)
    {
        this.parent = parent;
        this.position.y = this.parent.getY();
        this.size.x = this.parent.getHeight();
        this.size.y = this.parent.getHeight();

        return this;
    }

    /**
     * Sets this button's position
     *
     * @param x the button's position on the X axis
     */
    public ToolbarButton setX(float x)
    {
        this.position.x = x;
        updateIconPosition();

        return this;
    }

    /**
     * Shifts this {@link ToolbarButton} by the desired offset
     *
     * @param offset the offset by which this button has to be shifted
     */
    public ToolbarButton shiftX(float offset)
    {
        this.position.x += offset;
        this.iconPosition.x += offset;

        return this;
    }

    /**
     * Sets this button's size
     *
     * @param x the size of a button on the X axis
     *
     * @apiNote size of a button on the Y axis is inherited from parent object, if present
     */
    public ToolbarButton setWidth(float x)
    {
        this.size.x = x;
        updateIconPosition();

        return this;
    }

    /** Updates icon position<br>This has to be called whenever the button's size or position has been changed */
    public void updateIconPosition()
    {
        // The icon has to be at the center of a button
        this.iconPosition.x = this.position.x + this.size.x / 2f;
        this.iconPosition.y = this.position.y + this.size.y / 2f;
    }

    /**
     * Draw the button.
     */
    public void draw()
    {
        bgColor.a = bgAlpha.getValue();
        g.setColor(bgColor);
        g.fillRect(position.x, position.y, size.x, size.y);
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
