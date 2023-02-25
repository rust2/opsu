/*
 * opsu! - an open-source osu! client
 * Copyright (C) 2014-2017 Jeffrey Han
 *
 * opsu! is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opsu! is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opsu!.  If not, see <http://www.gnu.org/licenses/>.
 */

package itdelatrisu.opsu.user;
//import fluddokt.opsu.fake.*;

import fluddokt.opsu.fake.Color;
import fluddokt.opsu.fake.Graphics;
import fluddokt.opsu.fake.Image;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.ui.Colors;
import itdelatrisu.opsu.ui.Fonts;
import itdelatrisu.opsu.ui.animations.AnimatedValue;
import itdelatrisu.opsu.ui.animations.AnimationEquation;
import kww.useless.UselessUtils;
import lombok.Getter;

/**
 * User button.
 */
public class UserButton {
    /** Button margin (by kww) */
    @Getter private static int margin = 3; // outside

    /** Button padding (by kww) */
    @Getter private static int padding = 3; // inside

    /** Button dimensions. */
    @Getter private static int width, height;

    /** User icon size. */
    @Getter private static int iconSize;
    private static int iconBoxSize;

    /** Button coordinates. */
    private int x, y;

    /** Background color. */
    private Color bgColor;

    /** Background alpha level. */
    private AnimatedValue bgAlpha = new AnimatedValue(200, 0f, 1f, AnimationEquation.OUT_QUAD);

    /** Bar colors. */
    private final Color
            barBgColor = new Color(Color.darkGray),
            barBorderColor = new Color(Color.lightGray),
            barFillColor = new Color(Colors.YELLOW_FILL);

    /** Flash color. */
    private final Color flashColor = new Color(218, 28, 63);

    /** Whether the button is currently flashing. */
    private boolean flashing = false;

    /** The user. */
    @Getter private User user;

    /** Placeholder text to display if {@link #user} is null. */
    @Getter private String placeholderText;

    /**
     * Initializes the user buttons.
     */
    public static void init()
    {
        // 252x61
        // 322x78
        // 4.13

        initMarginAndPadding();

        int fontsHeight = Fonts.LARGE.getLineHeight() + Fonts.MEDIUM.getLineHeight() + Fonts.SMALL.getLineHeight() * 2;
        height = Math.round(fontsHeight * UselessUtils.Options.getMobileUIScale());
        width = Math.round(height * 4f * UselessUtils.Options.getMobileUIScale()); //3.89f

        iconBoxSize = height;
        iconSize = iconBoxSize - padding * 2;
    }

    private static void initMarginAndPadding()
    {
        int target = 4; // margin and padding are expected to be the same?

        int fl;
        fl = Math.round(target * UselessUtils.Options.getMobileUIScale());
        if(fl > target)
            margin = fl;
        fl = Math.round(target * UselessUtils.Options.getMobileUIScale());
        if(fl > target)
            padding = fl;
    }

    /**
     * Returns the user icon image with the given identifier,
     * or the default image if invalid.
     */
    public static Image getIconImage(int id)
    {
        int i = (id < 0 || id >= getIconCount()) ? 0 : id;
        return GameImage.USER.getImages()[i].getScaledCopy(iconSize, iconSize);
    }

    /** Returns the total number of user icons. */
    public static int getIconCount()
    {
        return GameImage.USER.getImages().length;
    }

    /**
     * Creates a user button.
     *
     * @param x            the top-left x coordinate
     * @param y            the top-left y coordinate
     * @param focusedColor the background color (when focused)
     */
    public UserButton(int x, int y, Color focusedColor)
    {
        setPosition(x, y);
        this.bgColor = new Color(focusedColor);
    }

    /**
     * Sets the button position.
     *
     * @param x the top-left x coordinate
     * @param y the top-left y coordinate
     */
    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /** Sets the user. */
    public void setUser(User user)
    {
        this.user = user;
        Fonts.loadGlyphs(Fonts.MEDIUM, user.getName());
    }

    /** Sets the text to display if no user is set. */
    public void setPlaceholderText(String placeholderText)
    {
        this.placeholderText = placeholderText;
    }

    /**
     * Returns true if the coordinates are within the button bounds.
     *
     * @param cx the x coordinate
     * @param cy the y coordinate
     */
    public boolean contains(int cx, int cy)
    {
        return ((cx > x && cx < x + width) && (cy > y && cy < y + height));
    }

    /**
     * Draws a user button.
     *
     * @param g the graphics context
     */
    public void draw(Graphics g)
    {
        draw(g, 1f);
    }

    /**
     * Draws a user button.
     *
     * @param g     the graphics context
     * @param alpha the alpha multiplier
     */
    @SuppressWarnings("DefaultLocale")
    public void draw(Graphics g, float alpha)
    {
//        int actualX = margin + x;
//        int actualY = margin + y;
//        int actualW = width - margin;
//        int actualH = height - margin;
        int actualX = margin + x;
        int actualY = margin + y;
        int actualW = width;
        int actualH = height;

        float uiScale = UselessUtils.Options.getMobileUIScale();

        float t = bgAlpha.getValue();
        float oldWhiteAlpha = Colors.WHITE_FADE.a;
        Colors.WHITE_FADE.a = alpha;

        // rectangle
        Color bg;
        if (flashing)
        {
            bg = flashColor;
            bg.a = t * alpha;
        }
        else
        {
            bg = bgColor;
            bg.a = (0.5f * t) * alpha;
        }
        g.setColor(bg);
        g.fillRoundRect3(actualX, actualY, actualW, actualH, 8);

        // no user?
        // no bitches?
        if (user == null && placeholderText != null)
        {
            Fonts.LARGE.drawString(
                    actualX + (width - Fonts.LARGE.getWidth(placeholderText)) / 2f,
                    actualY + (height - Fonts.LARGE.getLineHeight()) / 2f,
                    placeholderText, Colors.WHITE_FADE
            );
            Colors.WHITE_FADE.a = oldWhiteAlpha;
            return;
        }

        // icon
        Image img = getIconImage(user.getIconId()).getScaledCopy(iconSize, iconSize);
        img.setAlpha(alpha);
        img.draw(actualX + padding, actualY + padding);
        img.drawFilledHitbox(actualX + padding, actualY + padding, iconSize, iconSize);

        // text
        int textX = actualX + padding + iconSize + padding;
        int textY = actualY + padding;
        Fonts.MEDIUM.drawString(textX, textY, user.getName(), Colors.WHITE_FADE);
        textY += Fonts.MEDIUM.getLineHeight() - 3;
        Fonts.SMALL.drawString(textX, textY, String.format("Score: %,d", user.getScore()), Colors.WHITE_FADE);
        textY += Fonts.SMALL.getLineHeight() - 2;
        Fonts.SMALL.drawString(textX, textY, String.format("Accuracy: %.2f%%", user.getAccuracy()), Colors.WHITE_FADE);
        textY += Fonts.SMALL.getLineHeight() - 2;
        Fonts.SMALL.drawString(textX, textY, String.format("Lv%d", user.getLevel()), Colors.WHITE_FADE);

        // progress bar
        float barWidth = width / 1.71f * uiScale;
        float barHeight = height / 6.5f * uiScale;
        float barX = actualX + width - margin - padding - barWidth;
        float barY = actualY + height - margin - padding - barHeight;
        int barRadius = 8;
        float barAlpha = (0.75f + 0.25f * t) * alpha;
        barBgColor.a = barBorderColor.a = barFillColor.a = barAlpha;

        g.setColor(barBgColor);
        g.fillRoundRect(barX, barY, barWidth, barHeight, barRadius);

        g.setClip(barX, barY, (float) (barWidth * user.getNextLevelProgress()), barHeight);
        g.setColor(barFillColor);
        g.fillRoundRect(barX, barY, barWidth, barHeight, barRadius);
        g.clearClip();

        g.setAntiAlias(true);
        g.setColor(barBorderColor);
        g.setLineWidth(2f);
        g.drawRoundRect(barX, barY, barWidth, barHeight, barRadius);
        g.resetLineWidth();
        g.setAntiAlias(false);

        Colors.WHITE_FADE.a = oldWhiteAlpha;
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
        if (flashing)
        {
            if (!bgAlpha.update(-delta / 2))
                flashing = false;
        }
        else
        {
            int d = delta * (isHover ? 1 : -1);
            bgAlpha.update(d);
        }
    }

    /**
     * Sets the hover animation duration.
     *
     * @param duration the duration, in milliseconds
     */
    public void setHoverAnimationDuration(int duration)
    {
        bgAlpha.setDuration(duration);
    }

    /**
     * Sets the hover animation equation.
     *
     * @param eqn the equation to use
     */
    public void setHoverAnimationEquation(AnimationEquation eqn)
    {
        bgAlpha.setEquation(eqn);
    }

    /**
     * Sets the hover animation base value.
     *
     * @param base the base value
     */
    public void setHoverAnimationBase(float base)
    {
        AnimatedValue value = new AnimatedValue(bgAlpha.getDuration(), base, 1f, bgAlpha.getEquation());
        value.setTime(bgAlpha.getTime());
        bgAlpha = value;
    }

    /** Resets the hover fields for the button. */
    public void resetHover()
    {
        bgAlpha.setTime(0);
        flashing = false;
    }

    /** Flashes the button. */
    public void flash()
    {
        bgAlpha.setTime(bgAlpha.getDuration());
        flashing = true;
    }
}
