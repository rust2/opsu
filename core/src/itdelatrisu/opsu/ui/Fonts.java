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

package itdelatrisu.opsu.ui;

import fluddokt.opsu.fake.Color;
import fluddokt.opsu.fake.Font;
import fluddokt.opsu.fake.*;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.options.Options;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static fluddokt.opsu.fake.Font.FontStyle;

/*
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
*/

/**
 * Fonts used for drawing.
 */
public class Fonts {
    public static Font DEFAULT, BOLD, LARGE, XLARGE, MEDIUM, MEDIUMBOLD, SMALL, SMALLBOLD;
    private static float baseSize = 14f;

    /** Set of all Unicode strings already loaded per font. */
    private static final HashMap<Font, HashSet<String>> loadedGlyphs = new HashMap<>();

    // This class should not be instantiated.
    private Fonts() {}

    /**
     * Initializes all fonts.
     *
     * @throws SlickException      if ASCII glyphs could not be loaded
     * @throws FontFormatException if any font stream data does not contain the required font tables
     * @throws IOException         if a font stream cannot be completely read
     */
    public static void init() throws SlickException, FontFormatException, IOException
    {
        float fontBase = baseSize * GameImage.getUIscale() * Options.getMobileUIScale(0.5f);

        Font javaFontMain = new Font(Options.FONT_MAIN, FontStyle.Normal, baseSize);
        Font javaFontBold = new Font(Options.FONT_BOLD, FontStyle.Bold, baseSize);
        Font javaFontCJK = new Font(Options.FONT_CJK, FontStyle.Normal, baseSize);

        Font fontMain = javaFontMain.deriveFont(FontStyle.Normal, (int) (fontBase * 4 / 3));
        Font fontBold = javaFontBold.deriveFont(FontStyle.Bold, (int) (fontBase * 4 / 3));
        Font fontCJK = javaFontCJK.deriveFont(FontStyle.Normal, (int) (fontBase * 4 / 3));

//        DEFAULT = new Font(fontMain);
//        BOLD = new Font(fontBold);
//        XLARGE = new Font(fontMain.deriveFont(fontBase * 3));
//        LARGE = new Font(fontMain.deriveFont(fontBase * 2));
//        MEDIUM = new Font(fontMain.deriveFont(fontBase * 1.5f));
//        MEDIUMBOLD = new Font(fontBold.deriveFont(fontBase * 1.5f));
//        SMALL = new Font(fontMain.deriveFont(fontBase * 0.9f));
//        SMALLBOLD = new Font(fontBold.deriveFont(fontBase * 0.9f));
        DEFAULT = fontMain;
        BOLD = fontBold;
        LARGE = fontMain.deriveFont(fontBase * 2);
        XLARGE = fontMain.deriveFont(fontBase * 3);
        MEDIUM = fontMain.deriveFont(fontBase * 1.5f);
        MEDIUMBOLD = fontBold.deriveFont(fontBase * 1.5f);
        SMALL = fontMain.deriveFont(fontBase);
        SMALLBOLD = fontBold.deriveFont(fontBase);

        ColorEffect colorEffect = new ColorEffect();
        loadFont(DEFAULT, colorEffect, new Font(fontCJK));
        loadFont(BOLD, colorEffect, new Font(fontCJK.deriveFont(FontStyle.Bold)));
        loadFont(LARGE, colorEffect, new Font(fontCJK.deriveFont(fontBase * 2)));
        loadFont(XLARGE, colorEffect, new Font(fontCJK.deriveFont(fontBase * 3)));
        loadFont(MEDIUM, colorEffect, new Font(fontCJK.deriveFont(fontBase * 1.5f)));
        loadFont(MEDIUMBOLD, colorEffect, new Font(fontCJK.deriveFont(FontStyle.Bold, fontBase * 1.5f)));
        loadFont(SMALL, colorEffect, new Font(fontCJK.deriveFont(fontBase)));
        loadFont(SMALLBOLD, colorEffect, new Font(fontCJK.deriveFont(FontStyle.Bold, fontBase)));
    }

    /**
     * Loads a Unicode font and its ASCII glyphs.
     *
     * @param font   the font to load
     * @param effect the font effect
     * @param backup the backup font
     *
     * @throws SlickException if the glyphs could not be loaded
     */
    private static void loadFont(Font font, Effect effect, Font backup) throws SlickException
    {
        font.addBackupFont(backup);
        font.addAsciiGlyphs();
        font.getEffects().add(effect);
        font.loadGlyphs();
    }

    /**
     * Adds and loads glyphs for a font.
     *
     * @param font the font to add the glyphs to
     * @param s    the string containing the glyphs to load
     */
    public static void loadGlyphs(Font font, String s)
    {
        if (s == null || s.isEmpty())
            return;

        // get set of added strings
        HashSet<String> set = loadedGlyphs.get(font);
        if (set == null)
        {
            set = new HashSet<String>();
            loadedGlyphs.put(font, set);
        }
        else if (set.contains(s))
            return;  // string already in set

        // load glyphs
        font.addGlyphs(s);
        set.add(s);
        try
        {
            font.loadGlyphs();
        }
        catch (SlickException e)
        {
            Log.warn(String.format("Failed to load glyphs for string '%s'.", s), e);
        }
    }

    /**
     * Adds and loads glyphs for a font.
     *
     * @param font the font to add the glyphs to
     * @param c    the character to load
     */
    public static void loadGlyphs(Font font, char c)
    {
        font.addGlyphs(c, c);
        try
        {
            font.loadGlyphs();
        }
        catch (SlickException e)
        {
            Log.warn(String.format("Failed to load glyphs for codepoint '%d'.", (int) c), e);
        }
    }

    /**
     * Wraps the given string into a list of split lines based on the width.
     *
     * @param font     the font used to draw the string
     * @param text     the text to split
     * @param width    the maximum width of a line
     * @param newlines true if the "\n" character should break a line
     *
     * @return the list of split strings
     *
     * @author davedes (http://slick.ninjacave.com/forum/viewtopic.php?t=3778)
     */
    public static List<String> wrap(Font font, String text, int width, boolean newlines)
    {
        List<String> list = new ArrayList<String>();
        String str = text;
        String line = "";
        int i = 0;
        int lastSpace = -1;
        while (i < str.length())
        {
            char c = str.charAt(i);
            if (Character.isWhitespace(c))
                lastSpace = i;
            String append = line + c;
            if (font.getWidth(append) > width || (newlines && c == '\n'))
            {
                int split = (lastSpace != -1) ? lastSpace : i;
                int splitTrimmed = split;
                if (lastSpace != -1 && split < str.length() - 1)
                    splitTrimmed++;
                list.add(str.substring(0, split));
                str = str.substring(splitTrimmed);
                line = "";
                i = 0;
                lastSpace = -1;
            }
            else
            {
                line = append;
                i++;
            }
        }
        if (str.length() != 0)
            list.add(str);
        return list;
    }


    /**
     * Draws the string with a pixel border
     *
     * @param font the Font
     * @param x    x Location
     * @param y    y Location
     * @param str  string to display
     * @param fg   foreground Color
     * @param bg   background Color
     */
    public static void drawBorderedString(Font font, float x, float y, String str, Color fg, Color bg)
    {
        //kww: todo: something better than drawing all glyphs 5 times
        font.drawString(x + 1, y + 1, str, bg);
        font.drawString(x + 1, y - 1, str, bg);
        font.drawString(x - 1, y + 1, str, bg);
        font.drawString(x - 1, y - 1, str, bg);

        font.drawString(x, y, str, fg);
    }

}
