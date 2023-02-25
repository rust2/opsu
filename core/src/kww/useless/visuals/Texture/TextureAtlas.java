package kww.useless.visuals.Texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import kww.useless.math.Vector2I;

import java.util.ArrayList;
import java.util.List;

public class TextureAtlas {
    final int PADDING = 1; // (1 << IRenderer.MAX_MIPMAP_LEVELS) * Sprite.MAX_EDGE_SMOOTHNESS;
    final int WHITE_PIXEL_SIZE = 1;

    private final List<Rectangle> subTextureBounds = new ArrayList<>();
    private Texture atlasTexture;

    // private readonly IRenderer renderer;
    private int atlasWidth;
    private int atlasHeight;

    private int maxFittableWidth() {return atlasWidth - PADDING * 2;}

    private int maxFittableHeight() {return atlasHeight - PADDING * 2;}

    private Vector2I currentPosition;

    /*internal TextureWhitePixel WhitePixel
    {
        get
        {
            if (atlasTexture == null)
                Reset();

            Debug.Assert(atlasTexture != null, "Atlas texture should not be null after Reset().");

            return new TextureWhitePixel(atlasTexture);
        }
    }

    private readonly bool manualMipmaps;*/
    private final Texture.TextureFilter filteringMode;
    private final Object textureRetrievalLock = new Object();

    public TextureAtlas(int width, int height, Texture.TextureFilter filteringMode)
    {
        //this.renderer = renderer;
        atlasWidth = width;
        atlasHeight = height;
        //this.manualMipmaps = manualMipmaps;
        this.filteringMode = filteringMode;
    }

    private int exceedCount;

    /// <summary>
    /// Creates a new empty texture.
    /// </summary>
    /// <remarks>
    /// Existing textures created via <see cref="Add"/> are not cleared and remain accessible by usages.
    /// </remarks>
    public void Reset()
    {
        synchronized (textureRetrievalLock)
        {
            subTextureBounds.clear();
            currentPosition = Vector2I.Zero;

            // We pass PADDING/2 as opposed to PADDING such that the padded region of each individual texture
            // occupies half of the padded space.
            atlasTexture = new Texture(atlasWidth, atlasHeight, Pixmap.Format.RGBA8888);

            Rectangle bounds = new Rectangle(0, 0, WHITE_PIXEL_SIZE, WHITE_PIXEL_SIZE);
            subTextureBounds.add(bounds);

            /*using (var whiteTex = new TextureRegion(atlasTexture, bounds, WrapMode.Repeat, WrapMode.Repeat))
            // Generate white padding as if the white texture was wrapped, even though it isn't
            whiteTex.SetData(new TextureUpload(new Image<Rgba32>(SixLabors.ImageSharp.Configuration.Default, whiteTex.Width, whiteTex.Height, new Rgba32(Vector4.One))));*/

            currentPosition = new Vector2I(PADDING + WHITE_PIXEL_SIZE, PADDING);
        }
    }

    /// <summary>
    /// Add (allocate) a new texture in the atlas.
    /// </summary>
    /// <param name="width">The width of the requested texture.</param>
    /// <param name="height">The height of the requested texture.</param>
    /// <param name="wrapModeS">The horizontal wrap mode of the texture.</param>
    /// <param name="wrapModeT">The vertical wrap mode of the texture.</param>
    /// <returns>A texture, or null if the requested size exceeds the atlas' bounds.</returns>
    public TextureRegion Add(int width, int height)
    {
        if (!canFitEmptyTextureAtlas(width, height))
            return null;

        synchronized (textureRetrievalLock)
        {
            Vector2I position = findPosition(width, height);
            assert atlasTexture != null : "Atlas texture should not be null after findPosition().";

            Rectangle bounds = new Rectangle(position.x, position.y, width, height);
            subTextureBounds.add(bounds);

            return new TextureRegion(atlasTexture, bounds.x, bounds.y, bounds.height, bounds.width);
        }
    }

    /// <summary>
    /// Whether or not a texture of the given width and height could be placed into a completely empty texture atlas
    /// </summary>
    /// <param name="width">The width of the texture.</param>
    /// <param name="height">The height of the texture.</param>
    /// <returns>True if the texture could fit an empty texture atlas, false if it could not</returns>
    private boolean canFitEmptyTextureAtlas(int width, int height)
    {
        // exceeds bounds in one direction
        if (width > maxFittableWidth() || height > maxFittableHeight())
            return false;

        // exceeds bounds in both directions (in this one, we have to account for the white pixel)
        if (width + WHITE_PIXEL_SIZE > maxFittableWidth() && height + WHITE_PIXEL_SIZE > maxFittableHeight())
            return false;

        return true;
    }

    /// <summary>
    /// Locates a position in the current texture atlas for a new texture of the given size, or
    /// creates a new texture atlas if there is not enough space in the current one.
    /// </summary>
    /// <param name="width">The width of the requested texture.</param>
    /// <param name="height">The height of the requested texture.</param>
    /// <returns>The position within the texture atlas to place the new texture.</returns>
    private Vector2I findPosition(int width, int height)
    {
        if (atlasTexture == null)
        {
            System.out.println("TextureAtlas initialised (" + atlasWidth + "x" + atlasHeight + ")");
            Reset();
        }

        if (currentPosition.y + height + PADDING > atlasHeight)
        {
            System.out.println("TextureAtlas size exceeded " + ++exceedCount + " time(s); generating new texture (" + atlasWidth + "x" + atlasHeight + ")");
            Reset();
        }

        if (currentPosition.x + width + PADDING > atlasWidth)
        {
            int maxY = 0;

            for (Rectangle bounds : subTextureBounds)
            {
                maxY = (int) Math.max(maxY, bounds.y + bounds.height + PADDING);
            }

            subTextureBounds.clear();
            currentPosition = new Vector2I(PADDING, maxY);

            return findPosition(width, height);
        }

        Vector2I result = currentPosition;
        currentPosition.x += width + PADDING;

        return result;
    }
}
