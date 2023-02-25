package kww.useless.visuals.image;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

public class AtlasTextureData implements TextureData {
    @Getter private Pixmap pixmap;
    @Getter private int pixmapWidth, pixmapHeight;
    /*overridden*/ private final Pixmap.Format pixmapFormat;
    @Getter @Setter private boolean full = false;
    @Getter private boolean destroyed = false;

    @Getter private LinkedHashSet<ReferenceInfo> images = new LinkedHashSet<>();

    public AtlasTextureData(int width, int height)
    {
        this.pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        this.pixmapWidth = this.pixmap.getWidth();
        this.pixmapHeight = this.pixmap.getHeight();
        this.pixmapFormat = this.pixmap.getFormat();
    }

    public void addImage(ReferenceInfo referenceInfo, Pixmap pixmap)
    {
        this.pixmap.setBlending(Pixmap.Blending.None);
        this.pixmap.drawPixmap(pixmap, referenceInfo.getAtlasX(), referenceInfo.getAtlasY());
        this.images.add(referenceInfo);
    }

    public void removeImage(ReferenceInfo referenceInfo)
    {
        this.images.remove(referenceInfo);
    }

    public void removePixmap()
    {
        this.pixmap.dispose();
        this.pixmap = null;
    }

    @Override
    public TextureDataType getType()
    {
        return TextureDataType.Pixmap;
    }

    private void loadPixmap()
    {
        this.pixmap = new Pixmap(this.pixmapWidth, this.pixmapHeight, Pixmap.Format.RGBA8888);
        this.pixmap.setBlending(Pixmap.Blending.None);

        for (ReferenceInfo info : images)
        {
            Pixmap p = new Pixmap(info.getFileHandle());
            this.pixmap.drawPixmap(p, info.getAtlasX(), info.getAtlasY());
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
        if (this.pixmap == null)
            loadPixmap();
        return this.pixmap;
    }

    @Override
    public boolean disposePixmap()
    {
        if (full)
            this.pixmap = null;
        return full;
    }

    @Override
    public void consumeCustomData(int target)
    {
        throw new GdxRuntimeException("prepare() must not be called on a PixmapTextureData instance as it is already prepared.");
    }

    @Override
    public int getWidth()
    {
        return this.pixmapWidth;
    }

    @Override
    public int getHeight()
    {
        return this.pixmapHeight;
    }

    @Override
    public Pixmap.Format getFormat()
    {
        return this.pixmapFormat;
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
        if (this.pixmap != null)
        {
            this.pixmap.dispose();
            this.pixmap = null;
        }
        this.destroyed = true;
    }
}
