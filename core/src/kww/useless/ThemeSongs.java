package kww.useless;

import itdelatrisu.opsu.beatmap.Beatmap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ThemeSongs {
    private final static Map<String, FakeBeatmap> trackList = new HashMap<>();

    /**
     * @param fakeBeatmap the fake beatmap for theme song.
     *
     * @Note If the fake beatmap was not constructed or was constructed unsuccessfully,
     * the theme song will not be added to the listing
     */
    public static void add(@NotNull FakeBeatmap fakeBeatmap)
    {
        if (fakeBeatmap.isConstructed())
        {
            System.out.println("Fake beatmap '" + fakeBeatmap.getName() + "' successfully constructed");
            trackList.put(fakeBeatmap.getName(), fakeBeatmap);
        }
        else
        {
            System.err.println("Internal error: Fake beatmap '" + fakeBeatmap.getName() + "' was not constructed");
            System.err.println("Possible cause: " + fakeBeatmap.getCause());
        }
    }

    public static @Nullable Beatmap get()
    {
        if (!trackList.isEmpty())
        {
            FakeBeatmap[] array = new FakeBeatmap[trackList.size()];
            trackList.values().toArray(array);
            return array[UselessUtils.RANDOM.nextInt(array.length)].getBeatmap();
        }
        return null;
    }

    /**
     * Returns {@code null} there are no "fake" beatmaps, otherwise {@link Beatmap}
     */
    public static @Nullable Beatmap get(String name)
    {
        if (!trackList.isEmpty())
        {
            return trackList.get(name).getBeatmap();
        }
        return null;
    }

    /**
     * Prints all of the fake beatmaps registered (if present)
     */
    public static void printAll()
    {
        if (!trackList.isEmpty())
        {
            FakeBeatmap[] array = new FakeBeatmap[trackList.size()];
            trackList.values().toArray(array);
            System.out.println(Arrays.toString(array));
        }
    }
}
