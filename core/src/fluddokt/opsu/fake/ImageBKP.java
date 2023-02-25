package fluddokt.opsu.fake;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import kww.useless.UselessUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ImageBKP {

    private final static int ATLAS_SIZE = 512; //kww: 512 -> 2048
    private final static int ATLAS_PAD = 1;

    TextureRegion tex;
    float width, height;
    private float alpha = 1f, rotation = 0;
    //String name;
    String filename;
    ImageRefInfo imginfo;
    static LinkedHashMap<String, ImageRefInfo> imgmap = new LinkedHashMap<String, ImageRefInfo>();


    static LinkedHashSet<TextureAtlas> allAtlas = new LinkedHashSet<>();
    static TextureAtlas currentAtlas = null;
    static int atlasX, atlasY, atlasHighY;

    static TextureRegion blank = new TextureRegion(new Texture(32, 32, Format.RGBA8888));

    ImageBKP parentImage;
    FrameBuffer fb;
    FBGraphics fbg;
    Pixmap pixmap;

    private class FBGraphics extends Graphics {
        FrameBuffer fb;

        public FBGraphics(FrameBuffer fb)
        {
            this.fb = fb;
        }

        @Override
        protected void bind()
        {
            fb.bind();
        }

        @Override
        protected void unbind()
        {
            FrameBuffer.unbind();
        }
    }

    private class ImageRefInfo {
        int refcnt = 0;
        FileHandle fh;
        String name;
        TextureRegion region;

        TextureAtlas atlas;

        //Atlas
        int ax, ay;

        public ImageRefInfo(String name, FileHandle fh, TextureRegion region, TextureAtlas textureAtlas)
        {
            this.fh = fh;
            this.name = name;
            this.region = region;
            this.ax = region.getRegionX();
            this.ay = region.getRegionY();
            this.atlas = textureAtlas;
            region.flip(false, true);
        }

        public void add(ImageBKP img)
        {
            // set.add(img);
            refcnt++;
        }

        public void remove(ImageBKP img)
        {
            refcnt--;
            System.out.println("RefCnt: " + fh + " " + refcnt);
            if (refcnt <= 0)
            {
                System.out.println("Remove TextureInfo: " + fh);
                imgmap.remove(name);
                atlas.remove(this);
            }
        }

        public void removeAll()
        {
            refcnt = 0;
            imgmap.remove(name);
            atlas.remove(this);
        }
    }


    public ImageBKP(String filename) throws SlickException
    {
        this.filename = filename;
        //this.name = filename;
        imginfo = imgmap.get(filename);
        tex = blank;
        //if(true)
        //	return;
        if (imginfo == null)
        {
            Pixmap p;
            FileHandle fh = ResourceLoader.getFileHandle(filename);
            try
            {
                p = new Pixmap(fh);
            }
            catch (GdxRuntimeException e)
            {
                e.printStackTrace();
                //failed to load -> use a blank image.
                tex = blank;
                return;
            }

            if (p.getWidth() >= ATLAS_SIZE || p.getHeight() >= ATLAS_SIZE)
            {
                //creates its own texture
                TextureAtlas tatlas = new TextureAtlas(filename, fh, p);
                tatlas.setFull();
                imginfo = tatlas.getLastInfo();

            }
            else
            {
                //try to add to current atlas
                if (currentAtlas == null)
                {
                    currentAtlas = new TextureAtlas();
                    atlasX = 0;
                    atlasY = 0;
                }
                if (atlasX + p.getWidth() + ATLAS_PAD > currentAtlas.wid)
                {
                    atlasX = 0;
                    atlasY += atlasHighY + ATLAS_PAD;
                    atlasHighY = 0;
                }
                if (atlasY + p.getHeight() + ATLAS_PAD > currentAtlas.hei)
                {
                    currentAtlas.setFull();
                    currentAtlas = new TextureAtlas();
                    atlasY = 0;
                    atlasHighY = 0;
                    atlasX = 0;
                }
                currentAtlas.add(filename, fh, p, atlasX, atlasY);
                imginfo = currentAtlas.getLastInfo();
                atlasX += p.getWidth() + ATLAS_PAD;
                atlasHighY = Math.max(atlasHighY, p.getHeight());
            }
            //System.out.println("LastInfo: "+imginfo+" "+filename);
            imgmap.put(filename, imginfo);
            p.dispose();
        }
        tex = imginfo.region;
        width = imginfo.region.getRegionWidth();
        height = imginfo.region.getRegionHeight();
        imginfo.add(this);
    }

    private class TextureAtlas {
        Texture tex;
        AtlasTextureData data;

        //HashSet<ImageRefInfo> images = new HashSet<>();
        public int wid, hei;

        public TextureAtlas(String filename, FileHandle fh, Pixmap p)
        {
            allAtlas.add(this);
            int pw4 = nextmultipleof4(p.getWidth());
            int ph4 = nextmultipleof4(p.getHeight());
            data = new AtlasTextureData(pw4, ph4);
            tex = new Texture(data);
            tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            wid = data.pw;
            hei = data.ph;
            add(filename, fh, p, 0, 0);
        }

        public void setFull()
        {
            data.isFull = true;
            data.p.dispose();
            data.p = null;
        }

        public TextureAtlas()
        {
            allAtlas.add(this);
            data = new AtlasTextureData(ATLAS_SIZE, ATLAS_SIZE);
            tex = new Texture(data);
            tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            wid = data.pw;
            hei = data.ph;
        }

        public void remove(ImageRefInfo imageRefInfo)
        {
            data.remove(imageRefInfo);
            if (data.isFull && data.imgs.size() <= 0)
            {
                allAtlas.remove(this);
                tex.dispose();
                data.dispose();
            }
        }

        ImageRefInfo lastInfo;

        public void add(String filename, FileHandle fh, Pixmap p, int x, int y)
        {
            ImageRefInfo info = new ImageRefInfo(filename, fh, new TextureRegion(tex, x, y, p.getWidth(), p.getHeight()), this);
            data.addImage(info, p);
            lastInfo = info;
            tex.load(data);
            //System.out.println("TA ADD:"+fh+" "+x+" "+y+" "+p.getWidth()+" "+p.getHeight()+" ");
        }

        public ImageRefInfo getLastInfo()
        {
            return lastInfo;
        }

    }

    private class AtlasTextureData implements TextureData {
        Pixmap p;
        int pw, ph;
        Format pformat;
        boolean isFull = false;
        boolean isDestroyed = false;

        LinkedHashSet<ImageRefInfo> imgs = new LinkedHashSet<>();

        public AtlasTextureData(int wid, int hei)
        {
            p = new Pixmap(wid, hei, Format.RGBA8888);
            pw = p.getWidth();
            ph = p.getHeight();
            pformat = p.getFormat();
        }

        public void remove(ImageRefInfo imageRefInfo)
        {
            imgs.remove(imageRefInfo);
        }

        public void addImage(ImageRefInfo info, Pixmap p)
        {
            this.p.setBlending(Pixmap.Blending.None);

            this.p.drawPixmap(p, info.ax, info.ay);
            imgs.add(info);
        }

        @Override
        public TextureDataType getType()
        {
            return TextureDataType.Pixmap;
        }

        private void loadPixmap()
        {
            this.p = new Pixmap(pw, ph, Format.RGBA8888);
            this.p.setBlending(Pixmap.Blending.None);

            for (ImageRefInfo info : imgs)
            {
                Pixmap p = new Pixmap(info.fh);
                this.p.drawPixmap(p, info.ax, info.ay);
                p.dispose();
            }
        }

        @Override
        public boolean isPrepared()
        {
            return true;
        }

        @Override
        public void prepare()
        {
            throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
        }

        @Override
        public Pixmap consumePixmap()
        {
            if (p == null)
                loadPixmap();
            return p;
        }

        @Override
        public boolean disposePixmap()
        {
            if (isFull)
                p = null;
            return isFull;
        }

        @Override
        public void consumeCustomData(int target)
        {
            throw new GdxRuntimeException("prepare() must not be called on a PixmapTextureData instance as it is already prepared.");
        }

        @Override
        public int getWidth()
        {
            return pw;
        }

        @Override
        public int getHeight()
        {
            return ph;
        }

        @Override
        public Format getFormat()
        {
            return pformat;
        }

        @Override
        public boolean useMipMaps()
        {
            return false;
        }

        @Override
        public boolean isManaged()
        {
            return true;
        }

        public void dispose()
        {
            if (p != null)
            {
                p.dispose();
                p = null;
            }
            isDestroyed = true;
        }
    }

    private int gpow2(int n)
    {
        int pow2 = 1;
        while (pow2 < n)
        {
            pow2 <<= 1;
        }
        return pow2;
    }

    private int nextmultipleof4(int n)
    {
        return ((n + 3) / 4) * 4;
    }

    public ImageBKP(ImageBKP copy)
    {
        //texinfo = copy.texinfo;
        //texinfo.add(this);
        parentImage = copy;

        tex = copy.tex;
        width = copy.width;
        height = copy.height;
        filename = copy.filename;
        //name = copy.name+"[c]";
    }

    public ImageBKP(ImageBKP copy, float wid, float hei)
    {
        //texinfo = copy.texinfo;
        //texinfo.add(this);
        parentImage = copy;

        tex = copy.tex;
        width = wid;
        height = hei;
        filename = copy.filename;
        //name = copy.name + " s " + wid + " " + hei;
    }

    public ImageBKP(ImageBKP copy, int x, int y, int wid, int hei)
    {
        //texinfo = copy.texinfo;
        //texinfo.add(this);
        parentImage = copy;

        float dx = copy.tex.getRegionWidth() / (float) copy.width;
        float dy = copy.tex.getRegionHeight() / (float) copy.height;
        tex = new TextureRegion(copy.tex,
                                Math.round(x * dy),
                                Math.round((hei + y) * dy) - copy.tex.getRegionHeight(),
                                Math.round(wid * dx),
                                -Math.round(hei * dy));
        //tex.flip(false, true);
        width = (tex.getRegionWidth() / dx);
        height = (tex.getRegionHeight() / dy);
        filename = copy.filename;
        //name = copy.name + " r " + x + " " + y + " " + wid + " " + hei;
    }

    public ImageBKP()
    {
    }

    public ImageBKP(int width, int height)
    {
        this.width = width;
        this.height = height;
        fb = new FrameBuffer(Format.RGBA8888, width, height, false);
        fbg = new FBGraphics(fb);
        tex = new TextureRegion(fb.getColorBufferTexture());
        //name = "FrameBuffer image";
    }

    public int getHeight()
    {
        return (int) height;
    }

    public int getWidth()
    {
        return (int) width;
    }

    public ImageBKP getScaledCopy(float w, float h)
    {
        return new ImageBKP(this, w, h);
    }

    public ImageBKP getScaledCopy(float f)
    {
        return new ImageBKP(this, width * f, height * f);
    }

    public void setAlpha(float f)
    {
        this.alpha = MathUtils.clamp(f, 0, 1);
    }

	/*public float clamp(float val, float low, float high) {
		return MathUtils.clamp(val ,low, high);
	}*/

    @Getter
    @Setter
    boolean destroyed = false;

    public void destroy() throws SlickException
    {
        if (!isDestroyed())
        {
            if (parentImage != null)
                parentImage.destroy();
            if (imginfo != null)
                imginfo.remove(this);
            if (pixmap != null)
            {
                pixmap.dispose();
                pixmap = null;
            }
            if (fb != null)
            {
                fb.dispose();
            }
        }

        setDestroyed(true);
    }

    public float getAlpha()
    {
        return alpha;
    }

    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }

    static Color tempColor = new Color();

    public void draw()
    {
        draw(0, 0, Color.white);
    }

    public void draw(float x, float y)
    {
        draw(x, y, Color.white);
    }

    public void draw(float x, float y, Color color)
    {
        Graphics.getGraphics().setColorAlpha(color, alpha);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y, getWidth(), getHeight(), rotation);
    }


    public void drawCentered(float x, float y, Color color)
    {
        draw(x - getWidth() / 2f, y - getHeight() / 2f, color);
    }

    public void draw(float x, float y, float w, float h)
    {
        Graphics.getGraphics().setColorAlpha(Color.white, alpha);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y,
                                           w, h, rotation);
    }

    public void draw(float x, float y, float w,
                     float h, Color color)
    {
        Graphics.getGraphics().setColorAlpha(color, alpha);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y,
                                           w, h, rotation);
    }

    public void drawCentered(float x, float y)
    {
        drawCentered(x, y, Color.white);
    }

    public TextureRegion getTextureRegion()
    {
        return tex;
    }

    public ImageBKP getSubImage(int x, int y, int w, int h)
    {
        return new ImageBKP(this, x, y, w, h);
    }

    public float getRotation()
    {
        return rotation;
    }

    public void rotate(float f)
    {
        rotation += f;
    }

    public String getResourceReference()
    {
        return filename;
    }

    public float getAlphaAt(int x, int y)
    {
        if (pixmap == null)
            pixmap = new Pixmap(ResourceLoader.getFileHandle(filename));
        return pixmap.getPixel((int) (x * pixmap.getWidth() / width), (int) (y * pixmap.getHeight() / height)) & 0xff;
    }

    public Graphics getGraphics()
    {
        if (fb != null && fbg != null)
        {
            return fbg;
        }
        else
        {
            throw new Error("Getting graphics for non framebuffer image");
        }
    }

    //Color singGetColor = new Color();
    public Color getColor(int x, int y)
    {
        if (pixmap == null)
            pixmap = new Pixmap(ResourceLoader.getFileHandle(filename));
        return new Color(pixmap.getPixel((int) (x * pixmap.getWidth() / width), (int) (y * pixmap.getHeight() / height)));
    }

    public ImageBKP copy()
    {
        return new ImageBKP(this);
    }

    public void startUse()
    {
        // TODO Auto-generated method stub

    }

    public void endUse()
    {
        // TODO Auto-generated method stub

    }

    Color imageColor = Color.white;

    public void setImageColor(float r, float g, float b, float a)
    {
        if (imageColor == Color.white)
            imageColor = new Color(r, g, b, a);
        else
            imageColor.init(r, g, b, a);
    }

    public void drawEmbedded(float x, float y, int w, int h, float r)
    {
        Graphics.getGraphics().setColor(imageColor);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y,
                                           w, h, r);

    }

    public void drawEmbedded(float x, float y, float w, float h, int angle)
    {
        Graphics.getGraphics().setColor(imageColor);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y,
                                           w, h, angle);
    }

    public void setFlipped(boolean x, boolean y)
    {
		/*    isFlipped false true
		flip? false     false true
		      true      true  false
		*/
        tex.flip(x ^ tex.isFlipX(), y ^ !tex.isFlipY());
    }

    public static void clearAll()
    {
        //info();
        HashSet<String> nameSet = new HashSet<String>();
        nameSet.addAll(imgmap.keySet());
        for (String t : nameSet)
        {
            ImageRefInfo tinfo = imgmap.get(t);
            //System.out.println("Removing: "+t+" "+tinfo);
            if (tinfo != null)
                tinfo.removeAll();
        }
        //System.out.println("All Atlas size:" + allAtlas.size());
        //info();
    }

    public static void info()
    {
        System.out.println("All Atlas start:");
        for (TextureAtlas atlas : allAtlas)
        {
            System.out.println(atlas + " " + atlas.data.isFull + " " + atlas.data.isDestroyed);
            for (ImageRefInfo info : atlas.data.imgs)
            {
                System.out.println("\t" + info.fh + " " + info.region.getRegionWidth() + " " + info.region.getRegionHeight());
            }
        }
        System.out.println("All Atlas size:" + allAtlas.size() + " " + imgmap.size());
    }

    // edit:kww
    public void drawFilledHitbox(float x, float y, float w, float h)
    {
        UselessUtils.Hitbox.drawFilledHitbox(Graphics.getGraphics(), x, y, w, h);
    }

    // edit:kww
    public void drawOutlineHitbox(float x, float y, float w, float h)
    {
        UselessUtils.Hitbox.drawOutlineHitbox(Graphics.getGraphics(), x, y, w, h);
    }
}
