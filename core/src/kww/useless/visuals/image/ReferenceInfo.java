package kww.useless.visuals.image;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;

/** Represents an {@link fluddokt.opsu.fake.Image} inside of a {@link TextureAtlas} */
public class ReferenceInfo {
    @Getter private int referenceCount = 0;
    @Getter private FileHandle fileHandle;
    @Getter private String name;
    @Getter private TextureRegion region;

    /** The atlas containing this image */
    @Getter private TextureAtlas atlas;

    /** Image position in the atlas */
    @Getter private int atlasX, atlasY;

    public ReferenceInfo(String name, FileHandle fileHandle, TextureRegion region, TextureAtlas textureAtlas)
    {
        this.fileHandle = fileHandle;
        this.name = name;
        this.region = region;
        this.atlasX = region.getRegionX();
        this.atlasY = region.getRegionY();
        this.atlas = textureAtlas;
        region.flip(false, true);
    }

    public void add(fluddokt.opsu.fake.Image img)
    {
        // set.add(img);
        referenceCount++;
    }

    public void remove(fluddokt.opsu.fake.Image img)
    {
        referenceCount--;
        System.out.println("RefCnt: " + fileHandle + " " + referenceCount);
        if (referenceCount <= 0)
        {
            System.out.println("Remove TextureInfo: " + fileHandle);
            fluddokt.opsu.fake.Image.getImages().remove(name);
            atlas.remove(this);
        }
    }

    public void removeAll()
    {
        referenceCount = 0;
        fluddokt.opsu.fake.Image.getImages().remove(name);
        atlas.remove(this);
    }
}
