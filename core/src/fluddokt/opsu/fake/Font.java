package fluddokt.opsu.fake;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.LinkedList;

public class Font {
    // This actually does nothing :D
    public enum FontStyle {
        Normal,
        Bold,
        Italic,
        BoldItalic
    }

    //    public static final int PLAIN = 0;
//    public static final int BOLD = 1;
    String name;
    FontStyle style;
    float size;
    FileHandle file;

    static LinkedList<Font> allFonts = new LinkedList<>();

    public Font(String name)
    {
        this(name, FontStyle.Normal, 12);
    }

    /**
     * @param name  font family
     * @param style font style to which the font belongs to
     * @param size  font size, px
     */
    public Font(String name, FontStyle style, float size)
    {
        this(name, style, size, ResourceLoader.getFileHandle(name));
    }

    public Font(String name, FontStyle style, float size, FileHandle fileHandle)
    {
        this.name = name;
        this.style = style;
        this.size = size;
        file = fileHandle;
        dynFont = new DynamicFreeTypeFont(file, this);
        allFonts.add(this);
        System.out.println(name + " " + size);
    }

    public Font(Font font)
    {
        this(font.name, font.style, font.size, font.file);
    }

    public void addGlyphs(char c, char c2)
    {
        // TODO Auto-generated method stub

    }

    public void addBackupFont(Font backup)
    {
        dynFont.addBackupFace(backup);
    }

    public Font deriveFont(FontStyle style, float size)
    {
        return new Font(name, style, size, file);
    }

    public Font deriveFont(FontStyle style)
    {
        return new Font(name, style, size, file);
    }

    public Font deriveFont(float size)
    {
        return new Font(name, style, size, file);
    }

    LinkedList<Effect> colorEffects = new LinkedList<>();
    public BitmapFont bitmap;
    int padbottom = 0, padtop = 0;
    //StringBuilder chars = new StringBuilder();
    //HashSet<Character> set = new HashSet<Character>();
    //boolean glythsAdded = false;

    DynamicFreeTypeFont dynFont;

    public void addAsciiGlyphs() {}

    public void drawString(float x, float y, String string, Color textColor)
    {
        Graphics.getGraphics().setColor(textColor);
        drawString(x, y, string);
    }

    public void drawString(float x, float y, String string)
    {
        //checkString(string);
        Graphics.getGraphics().drawString(this, string, x, y + padtop);
    }

    public LinkedList<Effect> getEffects()
    {
        return colorEffects;
    }

    public int getHeight(String str)
    {
        return dynFont.getHeight(str) + padtop + padbottom;
    }

    public int getLineHeight()
    {
        return dynFont.getLineHeight() + padtop + padbottom;
    }

    public int getWidth(String str)
    {
        return dynFont.getWidth(str);
    }

    public float getSize()
    {
        return size;
    }

    public void addGlyphs(String string)
    {
        //checkString(string);
    }

	/*
	private void checkString(String string) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (!set.contains(c)) {
				set.add(c);
				chars.append(c);
				glythsAdded = true;
			}
		}
	}
	*/

    public void loadGlyphs() throws SlickException {}

    public void setPaddingBottom(int padding)
    {
        padbottom = padding;
    }

    public void setPaddingTop(int padding)
    {
        padtop = padding;
    }

    public int getPaddingTop()
    {
        return padtop;
    }

    public int getPaddingBottom()
    {
        return padbottom;
    }

    public void destroy()
    {
        dynFont.destroy();
    }

    public static void destroyAll()
    {
        System.out.print("Destroying fonts... ");
        for (Font font : allFonts)
        {
            font.destroy();
            //System.out.println("Destroy font :"+font);
        }
        allFonts.clear();
        System.out.println("OK");
    }

}
