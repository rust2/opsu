package kww.useless.visuals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import itdelatrisu.opsu.ui.Fonts;
import itdelatrisu.opsu.ui.UI;

/** Some sort of ugly wrapper for Gdx's {@link GLProfiler} */
@SuppressWarnings("FieldCanBeLocal")
public class Profiling {
    private final int x = 10, y = 400, offset = Fonts.SMALL.getLineHeight();
    private static GLProfiler profiler;
    private static boolean enabled;

    public Profiling()
    {
        profiler = new GLProfiler(Gdx.graphics);
    }

    public void draw()
    {
        if(!enabled)
            return;

        //btw profiler info can't be retrieved on android...
        Fonts.SMALL.drawString(x, y, "calls: " + profiler.getCalls());
        Fonts.SMALL.drawString(x, y + offset, "textureBindings: " + profiler.getTextureBindings());
        Fonts.SMALL.drawString(x, y + offset * 2, "drawCalls: " + profiler.getDrawCalls());
        Fonts.SMALL.drawString(x, y + offset * 3, "shaderSwitches: " + profiler.getShaderSwitches());
        Fonts.SMALL.drawString(x, y + offset * 4, "fps: " + Gdx.graphics.getFramesPerSecond());
        /*                                        5                                                     */
        Fonts.SMALL.drawString(x, y + offset * 6, "heap size: " + Runtime.getRuntime().totalMemory());
        Fonts.SMALL.drawString(x, y + offset * 7, "free heap size: " + Runtime.getRuntime().freeMemory());
        Fonts.SMALL.drawString(x, y + offset * 8, "heap utilization: " + Gdx.app.getJavaHeap()); // Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }

    public void reset()
    {
        if(!enabled)
            return;

        profiler.reset();
    }

    public static void toggle()
    {
        enabled = !enabled;

        if (enabled) profiler.enable();
        else profiler.disable();

        UI.getNotificationManager().sendBarNotification("Debug: " + enabled);
    }
}
