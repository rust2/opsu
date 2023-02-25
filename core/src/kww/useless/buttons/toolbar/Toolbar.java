package kww.useless.buttons.toolbar;

import com.badlogic.gdx.utils.Array;
import fluddokt.opsu.fake.Color;
import fluddokt.opsu.fake.Graphics;
import kww.useless.interfaces.IResizable;
import kww.useless.Instances;
import kww.useless.UselessUtils;
import kww.useless.interfaces.IInitable;
import kww.useless.visuals.Color255;
import kww.useless.visuals.Colors;
import lombok.Getter;

/** Horizontal toolbar, you know? <br> Also, buttons overlapping is unhandled for now (or maybe forever...) */
@SuppressWarnings("FieldCanBeLocal")
public class Toolbar implements IResizable, IInitable {
    @Getter private float x, y, w, h;
    private final float baseHeight = 40;
    private final float tooltip_height = 100;
    private final Color255 black = Colors.Gray0.cpy().setAlpha(0.9f);
    private final Color255 black_transparent = Colors.Gray0.cpy().setAlpha(0f);
    public static final float baseIconSize = 26; // 26x26px
    private float lastKnownLeftPosition, lastKnownRightPosition;
    private final Array<ToolbarButton> buttons = new Array<>(8);
    private final Graphics g = Graphics.getGraphics();
    private boolean toolbarHovered;

    public ToolbarButton toolbarProfileButton;
    public ToolbarButton toolbarMusicButton;
    public ToolbarButton toolbarDownloadsButton;
    public ToolbarButton toolbarSettingsButton;
    public ToolbarButton toolbarAboutButton;

    public Toolbar()
    {
        Graphics.getGraphics().registerResizable(this);
    }

    @Override
    public void resize()
    {
        x = 0;
        y = 0;
        w = Instances.container.getWidth();
        h = baseHeight * UselessUtils.Options.getMobileUIScale();

        lastKnownLeftPosition = x;
        lastKnownRightPosition = w;

        for (ToolbarButton button : buttons)
        {
            switch (button.side)
            {
                case Left:
                    button.setX(lastKnownLeftPosition);
                    lastKnownLeftPosition += button.size.x;
                    break;
                case Right:
                    lastKnownRightPosition -= button.size.x;
                    button.setX(lastKnownRightPosition);
                    break;
            }
        }
    }

    @Override
    public void init()
    {
        resize();

        registerButtons();
    }

    private void registerButtons()
    {
        Instances.toolbar.addButton(
                toolbarProfileButton = new ToolbarUserButton(),
                Toolbar.Side.Right
        );
        Instances.toolbar.addButton(
                toolbarMusicButton = new ToolbarMusicButton(),
                Toolbar.Side.Right
        );
        Instances.toolbar.addButton(
                toolbarDownloadsButton = new ToolbarBeatmapListingButton(),
                Toolbar.Side.Right
        );

        Instances.toolbar.addButton(
                toolbarSettingsButton = new ToolbarSettingsButton(),
                Toolbar.Side.Left
        );
        Instances.toolbar.addButton(
                toolbarAboutButton = new ToolbarAboutButton(),
                Side.Left
        );
    }

    public enum Side {
        Left,
        Right
    }

    public void addButton(ToolbarButton button, Side side)
    {
        buttons.add(button);

        button.setParent(this);
        button.side = side;

        switch (side)
        {
            case Left:
                button.setX(lastKnownLeftPosition);
                lastKnownLeftPosition += button.size.x;
                break;
            case Right:
                lastKnownRightPosition -= button.size.x;
                button.setX(lastKnownRightPosition);
                break;
        }
    }

    public void addGap(int px, Side side)
    {
        switch (side)
        {
            case Left:
                lastKnownLeftPosition += px;
                break;
            case Right:
                lastKnownRightPosition -= px;
                break;
        }
    }

    public void draw()
    {
        render();
    }

    private void render()
    {
        g.setColor(Colors.ToolbarGray.fake());
        g.fillRect(0, 0, w, h);

        for (ToolbarButton button : buttons)
        {
            button.draw();
        }

        if (toolbarHovered)
        {
            g.fillRectGradient(x, y + h, w, tooltip_height, black, black, black_transparent, black_transparent);
        }
    }

    public boolean someButtonHasBeenPressed(float mouseX, float mouseY)
    {
        for (ToolbarButton button : buttons)
        {
            if (button.contains(mouseX, mouseY))
            {
                // If some button is pressed, exit the loop
                button.pressed();
                return true;
            }
        }

        return false;
    }

    public void hoverUpdate(int delta, int mouseX, int mouseY)
    {
        for (ToolbarButton button : buttons)
        {
            boolean hovered = button.contains(mouseX, mouseY);
            button.hoverUpdate(delta, hovered);
//            if(hovered)
//            {
//                button.showTooltip();
//                break;
//            }
        }

        //todo
        toolbarHovered = this.contains(mouseX, mouseY);
    }

    /**
     * Returns true if the coordinates are within the toolbar's bounds.
     *
     * @param cx the x coordinate
     * @param cy the y coordinate
     */
    public boolean contains(float cx, float cy)
    {
        return ((cx >= x && cx <= x + w) &&
                (cy >= y && cy <= y + h));
    }

    public float getHeight()
    {
        return h;
    }
}
