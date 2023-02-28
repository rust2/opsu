package fluddokt.opsu.fake;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import kww.useless.UselessUtils;
import kww.useless.visuals.image.ReferenceInfo;
import kww.useless.visuals.image.TextureAtlas;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static kww.useless.visuals.image.TextureAtlas.*;

/** I did some changes here */
public class Image {
    @Getter TextureRegion textureRegion;

    @Getter int width, height;

    @Getter @Setter float alpha = 1f, rotation = 0f;

    String filename;
    ReferenceInfo referenceInfo;
    @Getter static LinkedHashMap<String, ReferenceInfo> images = new LinkedHashMap<>();

    public static void printReferences()
    {
        System.out.println("Trying to print all references...");
        for (Map.Entry<String, ReferenceInfo> entry : images.entrySet())
        {
            String key = entry.getKey();
            ReferenceInfo value = entry.getValue();

            System.out.println("\t" + key + " - " + value);
        }
    }

    static TextureAtlas currentAtlas = null;
    static int atlasX, atlasY, atlasHighY;

    static TextureRegion blank = new TextureRegion(new Texture(32, 32, Pixmap.Format.RGBA8888));

    Image parentImage;
    Pixmap pixmap;

    public Image(String filename) throws SlickException
    {
        this.filename = filename;
        //this.name = filename;
        referenceInfo = images.get(filename);
        textureRegion = blank;
        //if(true)
        //	return;
        if (referenceInfo == null)
        {
            Pixmap pixmap;
            FileHandle fileHandle = ResourceLoader.getFileHandle(filename);

            try
            {
                pixmap = new Pixmap(fileHandle);
            }
            catch (GdxRuntimeException e)
            {
                e.printStackTrace();
                //failed to load -> use a blank image.
                textureRegion = blank;
                return;
            }

            if (pixmap.getWidth() >= ATLAS_SIZE || pixmap.getHeight() >= ATLAS_SIZE)
            {
                if (currentAtlas != null)
                {
                    currentAtlas.setFull();
                    currentAtlas = new TextureAtlas();
                }

                // if this image can't be fit inside of an atlas, create its own texture
                System.out.println("Image can't be fit inside of an atlas, creating a new page... (" + atlasCount + ")");
                TextureAtlas textureAtlas = new TextureAtlas(filename, fileHandle, pixmap);
                textureAtlas.setFull();
                referenceInfo = textureAtlas.getLastInfo();
            }
            else
            {
                //try to add to the current atlas
                if (currentAtlas == null)
                {
                    currentAtlas = new TextureAtlas();
                    atlasX = 0;
                    atlasY = 0;
                }

                if (atlasX + pixmap.getWidth() + ATLAS_PADDING > currentAtlas.atlasWidth)
                {
                    atlasX = 0;
                    atlasY += atlasHighY + ATLAS_PADDING;
                    atlasHighY = 0;
                }

                if (atlasY + pixmap.getHeight() + ATLAS_PADDING > currentAtlas.atlasHeight)
                {
                    currentAtlas.setFull();
                    currentAtlas = new TextureAtlas();
                    atlasY = 0;
                    atlasHighY = 0;
                    atlasX = 0;
                }

                currentAtlas.add(filename, fileHandle, pixmap, atlasX, atlasY);
                referenceInfo = currentAtlas.getLastInfo();
                atlasX += pixmap.getWidth() + ATLAS_PADDING;
                atlasHighY = Math.max(atlasHighY, pixmap.getHeight());
            }

            //System.out.println("LastInfo: "+imginfo+" "+filename);
            images.put(filename, referenceInfo);
            pixmap.dispose();
        }

        textureRegion = referenceInfo.getRegion();
        width = referenceInfo.getRegion().getRegionWidth();
        height = referenceInfo.getRegion().getRegionHeight();
        referenceInfo.add(this);
    }

    public Image(Image copy)
    {
        //texinfo = copy.texinfo;
        //texinfo.add(this);
        parentImage = copy;

        textureRegion = copy.textureRegion;
        width = copy.width;
        height = copy.height;
        filename = copy.filename;
        //name = copy.name+"[c]";
    }

    public Image(Image copy, float width, float height)
    {
        //texinfo = copy.texinfo;
        //texinfo.add(this);
        parentImage = copy;

        textureRegion = copy.textureRegion;
        this.width = (int) width;
        this.height = (int) height;
        filename = copy.filename;
        //name = copy.name + " s " + width + " " + height;
    }

    public Image(Image copy, int x, int y, int wid, int hei)
    {
        //texinfo = copy.texinfo;
        //texinfo.add(this);
        parentImage = copy;

        float dx = copy.textureRegion.getRegionWidth() / (float) copy.width;
        float dy = copy.textureRegion.getRegionHeight() / (float) copy.height;
        textureRegion = new TextureRegion(copy.textureRegion,
                                          Math.round(x * dy),
                                          Math.round((hei + y) * dy) - copy.textureRegion.getRegionHeight(),
                                          Math.round(wid * dx),
                                          -Math.round(hei * dy));
        //tex.flip(false, true);
        width = (int) (textureRegion.getRegionWidth() / dx);
        height = (int) (textureRegion.getRegionHeight() / dy);
        filename = copy.filename;
        //name = copy.name + " r " + x + " " + y + " " + wid + " " + hei;
    }

    public Image() {}

    public Image(int width, int height)
    {
        this.width = width;
        this.height = height;
        textureRegion = new TextureRegion(new Texture(width, height, Pixmap.Format.RGBA8888));
        //name = "FrameBuffer image";
    }

    public Image getScaledCopy(float width, float height)
    {
        return new Image(this, width, height);
    }

    public Image getScaledCopy(float scaleMultiplier)
    {
        return new Image(this, width * scaleMultiplier, height * scaleMultiplier);
    }

    public Image getSubImage(int x, int y, int w, int h)
    {
        return new Image(this, x, y, w, h);
    }

    public Image setAlpha(float alpha)
    {
        this.alpha = MathUtils.clamp(alpha, 0, 1);
        return this;
    }

    @Getter boolean destroyed;

    public void destroy() throws SlickException
    {
        if (isDestroyed())
            return;

        if (parentImage != null)
            parentImage.destroy();
        if (referenceInfo != null)
            referenceInfo.remove(this);
        if (pixmap != null)
        {
            pixmap.dispose();
            pixmap = null;
        }

        this.destroyed = true;
    }

    // =================================================================== //

    /* Draw this Image */

    public void draw()
    {
        draw(0, 0, Color.white);
    }

    public void draw(float x, float y)
    {
        draw(x, y, Color.white);
    }

    public void drawCentered(float x, float y)
    {
        drawCentered(x, y, Color.white);
    }

    public void drawCentered(float x, float y, Color color)
    {
        draw(x - getWidth() / 2f, y - getHeight() / 2f, color);
    }

    public void draw(float x, float y, Color color)
    {
        Graphics.getGraphics().setColorAlpha(color, alpha);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y, getWidth(), getHeight(), rotation);
    }

    // =================================================================== //

    /* Draw a region of this Image */

    public void draw(float x, float y, float w, float h)
    {
        Graphics.getGraphics().setColorAlpha(Color.white, alpha);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y, w, h, rotation);
    }

    public void draw(float x, float y, float w, float h, Color color)
    {
        Graphics.getGraphics().setColorAlpha(color, alpha);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y, w, h, rotation);
    }

    // =================================================================== //

    public void rotate(float angle)
    {
        this.rotation += angle;
        this.rotation %= 360f;
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

    public Color getColor(int x, int y)
    {
        if (pixmap == null)
            pixmap = new Pixmap(ResourceLoader.getFileHandle(filename));
        return new Color(pixmap.getPixel((int) (x * pixmap.getWidth() / width), (int) (y * pixmap.getHeight() / height)));
    }

    public Image copy()
    {
        return new Image(this);
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
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y, w, h, r);

    }

    public void drawEmbedded(float x, float y, float w, float h, int angle)
    {
        Graphics.getGraphics().setColor(imageColor);
        Graphics.getGraphics().drawTexture(getTextureRegion(), x, y, w, h, angle);
    }

    public void setFlipped(boolean x, boolean y)
    {
		/*    isFlipped false true
		flip? false     false true
		      true      true  false
		*/
        textureRegion.flip(x ^ textureRegion.isFlipX(), y ^ !textureRegion.isFlipY());
    }

    public static void clearAll()
    {
        //info();
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(images.keySet());
        for (String t : nameSet)
        {
            ReferenceInfo tinfo = images.get(t);
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
        for (TextureAtlas atlas : TextureAtlas.getAtlasSet())
        {
            System.out.println(atlas + " " + atlas.getTextureData().isFull() + " " + atlas.getTextureData().isDestroyed());
            for (ReferenceInfo info : atlas.getTextureData().getImages())
            {
                System.out.println("\t" + info.getFileHandle() + " " + info.getRegion().getRegionWidth() + "x" + info.getRegion().getRegionHeight());
            }
        }
        System.out.println("All Atlas size:" + TextureAtlas.getAtlasSet().size() + " " + images.size());
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
