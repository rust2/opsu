package fluddokt.opsu.fake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import itdelatrisu.opsu.ui.UI;
import kww.useless.interfaces.IResizable;
import kww.useless.visuals.Color255;
import lombok.Getter;
import lombok.Setter;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.LinkedList;

import static kww.useless.UselessUtils.Maths.*;

public class Graphics {

    public static SpriteBatch batch;
    @Getter static ShapeRenderer shapeRender;
    @Getter static ShapeDrawer shapeDrawer;

    static Font curFont;
    @Getter static OrthographicCamera camera;
    final static Matrix4 transform = new Matrix4();
    public final static Matrix4 transformcombined = new Matrix4();

    public enum DrawMode {
        NONE,
        SPRITE,
        SHAPELINE,
        SHAPEFILLED,
        SPRITEMASKED,   //kww
        SHAPEMASK,      //kww
    }

    //    final static int NONE = 0;
//    final static int SPRITE = 3;
//    final static int SHAPELINE = 5;
//    final static int SHAPEFILLED = 6;
//    final static int SPRITEMASKED = 7; // kww
//    final static int SHAPEMASK = 8; // kww
    public static final int MODE_NORMAL = 1;
    public static final int MODE_ALPHA_MAP = 2;
    public static final int MODE_ALPHA_BLEND = 3;

    static DrawMode mode = DrawMode.NONE;
    static int width, height;
    public static Color bgcolor = Color.black;
    static Color fgcolor = Color.white;
    static float lineWidth = 1;

    //kww
    private final Array<IResizable> resizables = new Array<>();

    public static void init()
    {
        Image.getImages().clear();
        batch = new SpriteBatch();
        shapeRender = new ShapeRenderer();
        shapeRender.setAutoShapeType(true);
        mode = DrawMode.NONE;

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(0xffffffff);
        p.fill();
        shapeDrawer = new ShapeDrawer(batch, new TextureRegion(new Texture(p)));
        p.dispose();
    }

    public static void resize(int w, int h)
    {
        width = w;
        height = h;
        camera = new OrthographicCamera(w, h);
        camera.setToOrtho(true, w, h);

        updateCamera();

        for (IResizable resizable : g.resizables)
        {
            resizable.resize();
        }

        UI.getNotificationManager().sendBarNotification("Window size: " + w + "x" + h);
    }

    public void registerResizable(IResizable resizable)
    {
        resizables.add(resizable);
    }

    public void setBackground(Color ncolor)
    {
        bgcolor = ncolor;
    }

    public void setFont(Font font)
    {
        curFont = font;
    }

    public void drawString(String str, float x, float y)
    {
        drawString(curFont, str, x, y);
    }

    public void drawString(Font font, String str, float x, float y)
    {
        if (str == null)
            return;
        checkMode(DrawMode.SPRITE);
        font.dynFont.draw(batch, str, x, y);
    }

    public void setColor(Color ncolor)
    {
        fgcolor = ncolor;
        batch.setColor(fgcolor.r, fgcolor.g, fgcolor.b, fgcolor.a);
        shapeRender.setColor(fgcolor.r, fgcolor.g, fgcolor.b, fgcolor.a);
        shapeDrawer.setColor(fgcolor.r, fgcolor.g, fgcolor.b, fgcolor.a);
    }

    public void setColorAlpha(Color ncolor, float alpha)
    {
        fgcolor = ncolor;
        batch.setColor(fgcolor.r, fgcolor.g, fgcolor.b, fgcolor.a * alpha);
        shapeRender.setColor(fgcolor.r, fgcolor.g, fgcolor.b, fgcolor.a * alpha);
        shapeDrawer.setColor(fgcolor.r, fgcolor.g, fgcolor.b, fgcolor.a * alpha);
    }

    public void setAntiAlias(boolean b)
    {
        // TODO Auto-generated method stub
    }

    public void setLineWidth(float f)
    {
        checkMode(DrawMode.NONE);
        lineWidth = f;
    }

    public void resetLineWidth()
    {
        checkMode(DrawMode.NONE);
        lineWidth = 1;
    }

    public void fillRect(float x, float y, float w, float h)
    {
        checkMode(DrawMode.SPRITE);
        shapeDrawer.filledRectangle(x, y, w, h);
    }

    /**
     * @param topLeft     The color at (x, y).
     * @param topRight    The color at (x + width, y).
     * @param bottomRight The color at (x + width, y + height).
     * @param bottomLeft  The color at (x, y + height).
     */
    public void fillRectGradient(float x, float y, float w, float h,
                                 Color255 topLeft, Color255 topRight, Color255 bottomRight, Color255 bottomLeft)
    {
        checkMode(DrawMode.SHAPEFILLED);
        shapeRender.rect(x, y, w, h, topLeft, topRight, bottomRight, bottomLeft);
    }

    public void drawRect(float x, float y, float w, float h)
    {
        checkMode(DrawMode.SPRITE);
        shapeDrawer.rectangle(x, y, w, h, lineWidth);
    }

    public void fillOval(float x, float y, float w, float h)
    {
        checkMode(DrawMode.SPRITE);
        float cx = x + w / 2f;
        float cy = y + h / 2f;
        shapeDrawer.filledEllipse(cx, cy, w / 2f, h / 2f);
    }

    public void drawOval(float x, float y, float w, float h)
    {
        checkMode(DrawMode.SPRITE);
        float cx = x + w / 2f;
        float cy = y + h / 2f;
        shapeDrawer.ellipse(cx, cy, w / 2f, h / 2f, 0, lineWidth);
    }

    public void fillArc(float x, float y, float w, float h, float start, float end)
    {
        checkMode(DrawMode.SHAPEFILLED);
        if (w != h)
            throw new Error("fillArc Not implemented for w!=h");
        while (start < 0)
            start += 360;
        start %= 360;
        end %= 360;
        while (end < start)
            end += 360;
        shapeRender.arc(x + w / 2, y + h / 2, w / 2, start, end - start);// 36);

//todo drawer
    }

    // kww
    public void fillArc(float x, float y, float radius, float start, float degrees)
    {
        checkMode(DrawMode.SPRITE);
        shapeDrawer.sector(x, y, radius, start, degrees);
    }

    public void fillRoundRect(float x, float y, float w, float h, float m)
    {
        // TODO Auto-generated method stub
        checkMode(DrawMode.SPRITE);
        shapeDrawer.filledRectangle(x, y, w, h);
    }

    /**
     * Like material buttons...
     *
     * @author kww
     */
    public void fillRoundRect2(float x, float y, float w, float h)
    {
        checkMode(DrawMode.SPRITE);

        float radius = h / 2f;

        shapeDrawer.sector(x + radius, y + radius, radius, rad90, rad180);
        shapeDrawer.filledRectangle(x + radius, y, w - radius * 2, h);
        shapeDrawer.sector(x + w - radius, y + radius, radius, rad270, rad180);
    }

    /**
     * Like material buttons...
     *
     * @author kww
     */
    public void fillRoundRect2(float x, float y, float w, float h, float radius)
    {
        checkMode(DrawMode.SPRITE);

        shapeDrawer.sector(x + radius, y + radius, radius, rad90, rad180);
        shapeDrawer.filledRectangle(x + radius, y, w - radius * 2, h);
        shapeDrawer.sector(x + w - radius, y + radius, radius, rad270, rad180);
    }

    // kww, StackOverflow
    public void fillRoundRect3(float x, float y, float w, float h, float radius)
    {
        checkMode(DrawMode.SPRITE);

        // Central rectangle
        shapeDrawer.filledRectangle(x + radius, y + radius, w - 2 * radius, h - 2 * radius);

        // Four side rectangles, in clockwise order
        shapeDrawer.filledRectangle(x + radius, y, w - 2 * radius, radius);
        shapeDrawer.filledRectangle(x + w - radius, y + radius, radius, h - 2 * radius);
        shapeDrawer.filledRectangle(x + radius, y + h - radius, w - 2 * radius, radius);
        shapeDrawer.filledRectangle(x, y + radius, radius, h - 2 * radius);

        // Four arches, clockwise too
        shapeDrawer.sector(x + radius, y + radius, radius, rad180, rad90);
        shapeDrawer.sector(x + w - radius, y + radius, radius, rad270, rad90);
        shapeDrawer.sector(x + w - radius, y + h - radius, radius, rad0, rad90);
        shapeDrawer.sector(x + radius, y + h - radius, radius, rad90, rad90);
    }

    public void drawRoundRect(float x, float y, float w, float h, float radius)
    {
        // TODO Auto-generated method stub
        checkMode(DrawMode.SPRITE);
        shapeDrawer.rectangle(x, y, w, h, lineWidth);
    }

    /**
     * Like material buttons...
     *
     * @author kww
     */
    public void drawRoundRect2(float x, float y, float w, float h)
    {
        checkMode(DrawMode.SPRITE);

        float radius = h / 2f;

        shapeDrawer.arc(x + radius, y + radius, radius, rad90, rad180, lineWidth);
        shapeDrawer.rectangle(x + radius, y, w - radius * 2, h, lineWidth);
        shapeDrawer.arc(x + w - radius, y + radius, radius, rad270, rad180, lineWidth);
    }

    /**
     * Like material buttons...
     *
     * @author kww
     */
    public void drawRoundRect2(float x, float y, float w, float h, int radius)
    {
        checkMode(DrawMode.SPRITE);

        shapeDrawer.arc(x + radius, y + radius, radius, rad90, rad180);
        shapeDrawer.rectangle(x + radius, y, w - radius * 2, h);
        shapeDrawer.arc(x + w - radius, y + radius, radius, rad270, rad180);
    }

    public void drawLine(float x1, float y1, float x2, float y2)
    {
        checkMode(DrawMode.SPRITE);
        shapeDrawer.line(x1, y1, x2, y2, lineWidth);
    }

    //kww
    //https://github.com/mattdesl/lwjgl-basics/wiki/LibGDX-Masking#masking-with-depth-buffer

    /** Safe variant of masking stuff... <br> Allows only 1 image to be drawn */
    public void roundRectMask(float x, float y, float w, float h, float radius)
    {
        checkMode(DrawMode.SHAPEMASK);

        // Central rectangle
        shapeRender.rect(x + radius, y + radius, w - 2 * radius, h - 2 * radius);

        // Four side rectangles, in clockwise order
        shapeRender.rect(x + radius, y, w - 2 * radius, radius);
        shapeRender.rect(x + w - radius, y + radius, radius, h - 2 * radius);
        shapeRender.rect(x + radius, y + h - radius, w - 2 * radius, radius);
        shapeRender.rect(x, y + radius, radius, h - 2 * radius);

        // Four arches, clockwise too
        shapeRender.arc(x + radius, y + radius, radius, 180f, 90f);
        shapeRender.arc(x + w - radius, y + radius, radius, 270f, 90f);
        shapeRender.arc(x + w - radius, y + h - radius, radius, 0f, 90f);
        shapeRender.arc(x + radius, y + h - radius, radius, 90f, 90f);
    }

    public void fillRectDrawer(float x, float y, float w, float h)
    {
        checkMode(DrawMode.SPRITE);
        shapeDrawer.filledRectangle(x, y, w, h);
    }

    /** Safe variant of masking stuff... <br> Allows only 1 image to be drawn */
    public void drawTextureCenteredMasked(Texture tex, float x, float y, float width, float height)
    {
        //todo: reduce unnecessary calls
        if (tex != null)
        {
            checkMode(DrawMode.SPRITEMASKED);

            //it appears rotated so we manually mirror it
            batch.draw(tex, x - width / 2f, y - height / 2f + height, width, -height);

            // must end the batch call but because of this implementation we immediately start new batch
            checkMode(DrawMode.SPRITE);
            Gdx.gl.glFlush();
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }
        else /* texture was not applied, finish the mask */
        {
            checkMode(DrawMode.SPRITEMASKED);
            checkMode(DrawMode.SPRITE);
            Gdx.gl.glFlush();
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            throw new Error("Texture is null");
        }
    }

    /** Safe variant of masking stuff... <br> Allows only 1 image to be drawn */
    public void drawTextureMasked(Texture tex, float x, float y, float width, float height)
    {
        //todo: reduce unnecessary calls
        if (tex != null)
        {
            checkMode(DrawMode.SPRITEMASKED);

            //it appears rotated so we manually mirror it
            batch.draw(tex, x, y + height, width, -height);

            // must end the batch call but because of this implementation we immediately start new batch
            checkMode(DrawMode.SPRITE);
            Gdx.gl.glFlush();
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }
        else /* texture was not applied, finish the mask */
        {
            checkMode(DrawMode.SPRITEMASKED);
            checkMode(DrawMode.SPRITE);
            Gdx.gl.glFlush();
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            throw new Error("Texture is null");
        }
    }

    /**
     * To draw multiple sprites on a shaped mask, follow these instructions:
     * <li>call {@link Masked#shapeMaskBegin()} to start specifying mask's shape</li>
     * <li>draw your shapes via "unsafe" methods in this class</li>
     * <li>call {@link Masked#spriteMaskedBegin()} to be able draw sprites on that mask</li>
     * <li>at last, call {@link Masked#spriteMaskedEnd()} to finish the work (otherwise everything will break)</li>
     *
     * @author kww
     * @apiNote Everything was based on <a href="https://github.com/mattdesl/lwjgl-basics/wiki/LibGDX-Masking#masking-with-depth-buffer">Mattdesl's article</a>
     */
    public static class Masked {
        public static void shapeMaskBegin()
        {
            checkMode(DrawMode.SHAPEMASK);
        }

        public static void spriteMaskedBegin()
        {
            checkMode(DrawMode.SPRITEMASKED);
        }

        public static void spriteMaskedEnd()
        {
            // must end the batch call but because of this implementation we immediately start new batch
            checkMode(DrawMode.SPRITE);
            Gdx.gl.glFlush();
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }

        /* 'Unsafe' means it does not perform any mode checks... */

        public static void fillRectUnsafe(float x, float y, float w, float h)
        {
            shapeRender.rect(x, y, w, h);
        }

        public static void fillArcUnsafe(float x, float y, float w, float h, float start, float end)
        {
            //todo: will not implement until needed...
            if (w != h)
                throw new Error("fillArc Not implemented for w!=h");
            while (start < 0)
                start += 360;
            start %= 360;
            end %= 360;
            while (end < start)
                end += 360;
            shapeRender.arc(x + w / 2, y + h / 2, w / 2, start, end - start);// 36);
        }

        // kww, StackOverflow
        public static void fillRoundRectUnsafe(float x, float y, float w, float h, float radius)
        {
            // Central rectangle
            shapeRender.rect(x + radius, y + radius, w - 2 * radius, h - 2 * radius);

            // Four side rectangles, in clockwise order
            shapeRender.rect(x + radius, y, w - 2 * radius, radius);
            shapeRender.rect(x + w - radius, y + radius, radius, h - 2 * radius);
            shapeRender.rect(x + radius, y + h - radius, w - 2 * radius, radius);
            shapeRender.rect(x, y + radius, radius, h - 2 * radius);

            // Four arches, clockwise too
            shapeRender.arc(x + radius, y + radius, radius, 180f, 90f);
            shapeRender.arc(x + w - radius, y + radius, radius, 270f, 90f);
            shapeRender.arc(x + w - radius, y + h - radius, radius, 0f, 90f);
            shapeRender.arc(x + radius, y + h - radius, radius, 90f, 90f);
        }

        public static void drawTextureUnsafe(Texture tex, float x, float y, float width, float height)
        {
            if (tex == null)
                throw new Error("Texture is null");
            //it appears rotated so we manually mirror it
            batch.draw(tex, x, y + height, width, -height);
        }

        public static void drawTextureCenteredUnsafe(Texture tex, float x, float y, float width, float height)
        {
            if (tex == null)
                throw new Error("Texture is null");
            //it appears rotated so we manually mirror it
            batch.draw(tex, x - width / 2f, y - height / 2f + height, width, -height);
        }

        public static void fillRectUnsafeDrawer(float x, float y, float w, float h)
        {
            shapeDrawer.filledRectangle(x, y, w, h);
        }
    }

    /** Prevents all mode changes. Use it carefully! */
    @Setter static boolean unsafe = false;

    public static void checkMode(DrawMode nmode)
    {
        if (unsafe)
            return;

        if (mode != nmode)
        {
            Gdx.gl20.glLineWidth(lineWidth);
            //end
            switch (mode)
            {
                case SPRITE:
                case SPRITEMASKED:
                    batch.end(); // finish the existing batch
                    break;
                case SHAPEFILLED:
                case SHAPELINE:
                case SHAPEMASK:
                    shapeRender.end(); // finish the existing shapeRender
                    break;
            }
            //begin
            switch (nmode)
            {
                case SPRITE:
                    batch.begin();
                    break;
                case SHAPEFILLED:
                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    shapeRender.begin(ShapeType.Filled);
                    break;
                case SHAPELINE:
                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    shapeRender.begin(ShapeType.Line);
                    break;
                case SHAPEMASK:
                    //1. clear screen
                    //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                    //2. clear our depth buffer with 1.0
                    Gdx.gl.glClearDepthf(1f);
                    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

                    //3. set the function to LESS
                    Gdx.gl.glDepthFunc(GL20.GL_LESS);

                    //4. enable depth writing
                    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

                    //5. Enable depth writing, disable RGBA color writing
                    Gdx.gl.glDepthMask(true);
                    Gdx.gl.glColorMask(false, false, false, false);
                    shapeRender.begin(ShapeType.Filled);
                    break;
                case SPRITEMASKED:
                    batch.begin();

                    //8. Enable RGBA color writing
                    //   (SpriteBatch.begin() will disable depth mask)
                    Gdx.gl.glColorMask(true, true, true, true);

                    //9. Make sure testing is enabled.
                    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

                    //10. Now depth discards pixels outside our masked shapes
                    Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
                    break;
            }
            mode = nmode;
        }
    }

    public void drawTexture(TextureRegion tex, float x, float y, float wid, float hei, float rotation)
    {
        checkMode(DrawMode.SPRITE);
        if (tex == null)
            throw new Error("Texture is null");
        // draw(TextureRegion region, float x, float y, float originX, float
        // originY, float width, float height, float scaleX, float scaleY, float
        // rotation, boolean clockwise)

        batch.draw(tex, x, y, wid / 2, hei / 2, wid, hei, 1, 1, rotation);
    }

    public void drawTexture(Texture tex, float x, float y, float wid, float hei)
    {
        checkMode(DrawMode.SPRITE);
        if (tex == null)
            throw new Error("Texture is null");
        batch.draw(tex, x, y, wid, hei);
    }

    public void drawTextureUpsideDown(Texture tex, float x, float y, float width, float height)
    {
        checkMode(DrawMode.SPRITE);
        if (tex == null)
            throw new Error("Texture is null");
        //it appears rotated so we manually mirror it
        batch.draw(tex, x, y + height, width, -height);
    }

    static Graphics g = new Graphics();
    static Graphics current = g;

    public static Graphics getGraphics()
    {
        return g;
    }

    Rectangle scissor = new Rectangle();
    Rectangle clip = new Rectangle();
    boolean hasScissor = false;

    public void setClip(float x, float y, float w, float h)
    {
        clearClip();
        if (w <= 0f)
            w = 1f;
        if (h <= 0f)
            h = 1f;
        clip.set(x, y, w, h);
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clip, scissor);
        hasScissor = ScissorStack.pushScissors(scissor);
    }

    public void clearClip()
    {
        checkMode(DrawMode.NONE);
        if (hasScissor)
        {
            hasScissor = false;
            ScissorStack.popScissors();
        }
    }

    public static void setCurrent(Graphics g2)
    {
        if (current != g2)
        {
            checkMode(DrawMode.NONE);
            current.unbind();
            current = g2;
            current.bind();
        }
    }

    public void flush()
    {
        checkMode(DrawMode.NONE);
        Gdx.gl.glFlush();
    }

    public void drawImage(Image image, float x, float y)
    {
        image.draw(x, y);
    }

    public void clearAlphaMap()
    {
        // TODO Auto-generated method stub
        //Gdx.gl.glColorMask(false, false, false, true);
        //fillRect(0, 0, width, height);
        //Gdx.gl.glColorMask(true, true, true, true);

        //clear();
    }

    protected void bind()
    {

    }

    protected void unbind()
    {

    }

    public void clear()
    {
        if (current != this)
            bind();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (current != this)
            unbind();

    }

    public void setDrawMode(int mode)
    {
        //TODO
        checkMode(DrawMode.NONE);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        batch.enableBlending();
        //Gdx.gl.glEnable(GL20.GL_BLEND);
        if (mode == MODE_NORMAL)
        {
            Gdx.gl.glColorMask(true, true, true, true);
            //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            //Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ZERO);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        else if (mode == MODE_ALPHA_MAP)
        {
            //Gdx.gl.glDisable(GL20.GL_BLEND);
            //batch.disableBlending();
            Gdx.gl.glColorMask(false, false, true, true);
            //Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ZERO);
            Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ZERO);
            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        }
        else if (mode == MODE_ALPHA_BLEND)
        {
            Gdx.gl.glColorMask(true, true, false, false);
            //Gdx.gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
            //Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE);
            batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
        }
        else
        {
            throw new Error("Unknown Draw Mode");
        }
        //batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        //batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ZERO);
        //Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE);
    }

    public Color getColor()
    {
        return fgcolor;
    }

    public float getLineWidth()
    {
        return 1;
    }

    public void pushTransform()
    {
        checkMode(DrawMode.NONE);
        Matrix4 state = getNewTranformState();
        state.set(transform);
        transformStack.addLast(state);
    }

    public void rotate(float x, float y, float a)
    {
        checkMode(DrawMode.NONE);
        transform.translate(x, y, 0);
        transform.rotate(Vector3.Z, a);
        transform.translate(-x, -y, 0);
        updateCamera();
    }

    public void translate(float x, float y)
    {
        checkMode(DrawMode.NONE);
        transform.translate(x, y, 0);
        updateCamera();
    }

    public void scale(float xs, float ys)
    {
        checkMode(DrawMode.NONE);
        transform.scale(xs, ys, 1);
        updateCamera();
    }

    public void popTransform()
    {
        checkMode(DrawMode.NONE);
        Matrix4 state = transformStack.removeLast();
        transform.set(state);
        updateCamera();
        transformPool.addFirst(state);

    }

    public static void updateCamera()
    {
        camera.update();
        transformcombined.set(camera.combined);
        transformcombined.mul(transform);
        batch.setProjectionMatrix(transformcombined);
        shapeRender.setProjectionMatrix(transformcombined);
    }

    private Matrix4 getNewTranformState()
    {
        if (transformPool.size() > 0)
            return transformPool.removeFirst();
        return new Matrix4();
    }

    private LinkedList<Matrix4> transformStack = new LinkedList<Matrix4>();
    private LinkedList<Matrix4> transformPool = new LinkedList<Matrix4>();

    public void resetTransform()
    {
        transform.idt();
        updateCamera();
    }
}
