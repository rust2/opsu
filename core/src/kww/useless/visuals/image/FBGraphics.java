package kww.useless.visuals.image;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import fluddokt.opsu.fake.Graphics;

public class FBGraphics extends Graphics {
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
