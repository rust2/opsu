package kww.useless;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.ShortArray;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ShapeDrawerEx extends ShapeDrawer {

    public static final EarClippingTriangulator tri = new EarClippingTriangulator();

    public ShapeDrawerEx(Batch batch)
    {
        super(batch);
    }

    public ShapeDrawerEx(Batch batch, TextureRegion region)
    {
        super(batch, region);
    }

    /** Draw 2 triangles :D */
    public void wedge(float x, float y, float width, float height, int edgeOffset)
    {
        // Clockwise
        float x1, x2, x3, x4;
        float y1, y2, y3, y4;
        x1 = x + edgeOffset;
        y1 = y2 = y;
        x2 = x1 + width;
        x3 = x + width - edgeOffset;
        y3 = y4 = y + height;
        x4 = x - edgeOffset;

        filledTriangle(x1, y1, x2, y2, x3, y3);
        filledTriangle(x3, y3, x4, y4, x1, y1);
    }

    public void wedge2(float x, float y, float width, float height, int edgeOffset)
    {
        // Clockwise
        float x1, x2, x3, x4;
        float y1, y2, y3, y4;
        x1 = x + edgeOffset;
        x2 = x1 + width;
        y1 = y2 = y;
        x3 = x + width - edgeOffset;
        x4 = x - edgeOffset;
        y3 = y4 = y + height;

        float[] vertices = {
                x1, y1, x2, y2, x3, y3,
                x3, y3, x4, y4, x1, y1
        };

        ShortArray t = tri.computeTriangles(vertices, 0, vertices.length);
        filledPolygon(vertices, t);
    }
}
