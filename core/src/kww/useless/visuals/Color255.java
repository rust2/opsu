package kww.useless.visuals;

import com.badlogic.gdx.graphics.Color;

public class Color255 extends Color {
    public int ri, gi, bi, ai; // 'i' stands for int
    private fluddokt.opsu.fake.Color fake;

    /** This is the place where wierd things happens... */
    public Color255(int r, int g, int b, int a)
    {
        super(r / 255f, g / 255f, b / 255f, a / 255f);

        this.ri = r;
        this.gi = g;
        this.bi = b;
        this.ai = a;
    }

    /** This is the place where wierd things happens... */
    public Color255(float r, float g, float b, float a)
    {
        super(r, g, b, a);

        this.ri = (int) r * 255;
        this.gi = (int) g * 255;
        this.bi = (int) b * 255;
        this.ai = (int) a * 255;
    }

    /** This is the place where wierd things happens... */
    public Color255(int r, int g, int b, float a)
    {
        super(r / 255f, g / 255f, b / 255f, a);

        this.ri = r;
        this.gi = g;
        this.bi = b;
        this.ai = (int) (a * 255); // what a mess...
    }

    /** This is the place where wierd things happens... */
    public Color255(int r, int g, int b)
    {
        super(r / 255f, g / 255f, b / 255f, 255);

        this.ri = r;
        this.gi = g;
        this.bi = b;
        this.ai = 255;
    }

    public Color255() {}

    public Color255(float a)
    {
        super();

        this.ai = (int) (a * 255);
        super.a = a;
    }

    /** Do a copy of this {@link Color255} */
    public Color255 cpy()
    {
        return new Color255(this);
    }

    public Color255(Color255 color)
    {
        set(color);
        this.ri = color.ri;
        this.gi = color.gi;
        this.bi = color.bi;
        this.ai = color.ai;
        this.fake = color.fake;
    }

    /** It is basically {@link Color#valueOf(String)} */
    public Color255 fromHex(String hex)
    {
        //todo: better implementation for 0xFFF-like hex codes
        hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;

        if (hex.length() == 3 || hex.length() == 6 || hex.length() == 8)
        {
            if (hex.length() == 3)
            {
                hex = String.valueOf(
                        hex.charAt(0)) +
                        hex.charAt(0) +
                        hex.charAt(1) +
                        hex.charAt(1) +
                        hex.charAt(2) +
                        hex.charAt(2);
            }

            this.ri = Integer.parseInt(hex.substring(0, 2), 16);
            this.gi = Integer.parseInt(hex.substring(2, 4), 16);
            this.bi = Integer.parseInt(hex.substring(4, 6), 16);
            this.ai = hex.length() != 8 ? 255 : Integer.parseInt(hex.substring(6, 8), 16);

            super.r = this.ri / 255f;
            super.g = this.gi / 255f;
            super.b = this.bi / 255f;
            super.a = this.ai / 255f;
        }
        else
        {
            try
            {
                throw new Exception("Hex string is malformed");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return this;
    }

    /**
     * Returns the "fake" color
     */
    public fluddokt.opsu.fake.Color fake()
    {
        return fake != null
               ? fake
               : (fake = new fluddokt.opsu.fake.Color(ri, gi, bi, ai));
    }

    /**
     * Sets alpha component of a {@link Color}
     *
     * @param a [0..1]
     */
    public Color255 setAlpha(float a)
    {
        this.ai = (int) (a * 255);
        super.a = a;
        return this;
    }

    public Color255 fromFake(fluddokt.opsu.fake.Color fakeColor)
    {
        return new Color255(fakeColor.r, fakeColor.g, fakeColor.b, fakeColor.a);
    }
}
