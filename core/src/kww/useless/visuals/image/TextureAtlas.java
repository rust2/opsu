package kww.useless.visuals.image;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import fluddokt.opsu.fake.Image;
import kww.useless.UselessUtils;
import lombok.Getter;

import java.util.LinkedHashSet;

public class TextureAtlas {
    //public static final int ATLAS_SIZE = ImageUtils.getMaxTextureSize();
    public static final int ATLAS_SIZE = 1024;
    public static final int ATLAS_PADDING = 1;

    /** This atlas' texture */
    private final Texture texture;

    /** Set containing all of the atlases */
    @Getter static LinkedHashSet<TextureAtlas> atlasSet = new LinkedHashSet<>();


    @Getter private AtlasTextureData textureData;

    //HashSet<ImageRefInfo> images = new HashSet<>();
    public int atlasWidth, atlasHeight;

    public static int atlasCount = 0;

    /** Creates a new empty atlas page. */
    public TextureAtlas()
    {
        atlasCount++;
        System.out.println(atlasCount + " -> TextureAtlas: New ");
        atlasSet.add(this);
        textureData = new AtlasTextureData(ATLAS_SIZE, ATLAS_SIZE);
        texture = new Texture(textureData);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        atlasWidth = textureData.getPixmapWidth();
        atlasHeight = textureData.getPixmapHeight();
    }

    /** Creates an atlas page specifically for this image. */
    public TextureAtlas(String fileName, FileHandle fileHandle, Pixmap pixmap)
    {
        //atlasCount++;
        System.out.println(atlasCount + " -> TextureAtlas: Created exclusive for: " + fileName);
        atlasSet.add(this);
        textureData = new AtlasTextureData(nextmultipleof4(pixmap.getWidth()), nextmultipleof4(pixmap.getHeight()));
        texture = new Texture(textureData);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        atlasWidth = textureData.getPixmapWidth();
        atlasHeight = textureData.getPixmapHeight();
        add(fileName, fileHandle, pixmap, 0, 0);
    }

    public void setFull()
    {
        //saveThisAtlas();
        textureData.setFull(true);
        textureData.removePixmap();
    }

    ReferenceInfo lastInfo;

    public void add(String fileName, FileHandle fileHandle, Pixmap pixmap, int x, int y)
    {
        ReferenceInfo info = new ReferenceInfo(fileName, fileHandle, new TextureRegion(texture, x, y, pixmap.getWidth(), pixmap.getHeight()), this);
        textureData.addImage(info, pixmap);
        lastInfo = info;
        texture.load(textureData);
        System.out.println("\tAdded: " + fileName);
    }

    public void remove(ReferenceInfo imageReferenceInfo)
    {
        textureData.removeImage(imageReferenceInfo);
        if (textureData.isFull() && textureData.getImages().size() <= 0)
        {
            atlasSet.remove(this);
            texture.dispose();
            textureData.dispose();
        }
    }

    public ReferenceInfo getLastInfo()
    {
        return lastInfo;
    }

    public static void info()
    {
        System.out.println("All Atlas start:");
        for (TextureAtlas atlas : atlasSet)
        {
            System.out.println(atlas + " " + atlas.textureData.isFull() + " " + atlas.textureData.isDestroyed());

            for (ReferenceInfo info : atlas.textureData.getImages())
            {
                System.out.println("\t" + info.getFileHandle() + " " + info.getRegion().getRegionWidth() + " " + info.getRegion().getRegionHeight());
            }
        }
        System.out.println("All Atlas size:" + atlasSet.size() + " " + Image.getImages().size());
    }

    public void saveThisAtlas()
    {
        String path = "C:\\\\unlucky\\removedAtlas" + atlasCount + ".png";
        UselessUtils.savePixmapTo(path, this.getTextureData().getPixmap(), false);
    }

    private int nextmultipleof4(int n)
    {
        return ((n + 3) / 4) * 4;
    }
}
