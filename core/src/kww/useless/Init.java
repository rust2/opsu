package kww.useless;

import itdelatrisu.opsu.ui.Fonts;

public class Init {

    public static void preInit()
    {
        Instances.earlyInit();
    }

    public static void init()
    {
        Instances.init();

        System.out.println("Fonts.DEFAULT size: " + Fonts.DEFAULT.getSize());
        System.out.println("Fonts.BOLD size: " + Fonts.BOLD.getSize());
        System.out.println("Fonts.XLARGE size: " + Fonts.XLARGE.getSize());
        System.out.println("Fonts.LARGE size: " + Fonts.LARGE.getSize());
        System.out.println("Fonts.MEDIUM size: " + Fonts.MEDIUM.getSize());
        System.out.println("Fonts.MEDIUMBOLD size: " + Fonts.MEDIUMBOLD.getSize());
        System.out.println("Fonts.SMALL size: " + Fonts.SMALL.getSize());
        System.out.println("Fonts.SMALLBOLD size: " + Fonts.SMALLBOLD.getSize());
        System.out.println("");
        System.out.println("Fonts.DEFAULT line height: " + Fonts.DEFAULT.getLineHeight());
        System.out.println("Fonts.BOLD line height: " + Fonts.BOLD.getLineHeight());
        System.out.println("Fonts.XLARGE line height: " + Fonts.XLARGE.getLineHeight());
        System.out.println("Fonts.LARGE line height: " + Fonts.LARGE.getLineHeight());
        System.out.println("Fonts.MEDIUM line height: " + Fonts.MEDIUM.getLineHeight());
        System.out.println("Fonts.MEDIUMBOLD line height: " + Fonts.MEDIUMBOLD.getLineHeight());
        System.out.println("Fonts.SMALL line height: " + Fonts.SMALL.getLineHeight());
        System.out.println("Fonts.SMALLBOLD line height: " + Fonts.SMALLBOLD.getLineHeight());
    }

    //todo: finish
    // I'm soooo lazy to finish it, but I will
    private static void constructThemeBeatmaps()
    {
        ThemeSongs.add(new FakeBeatmap("circles", "nekodex - circles! (peppy).osu"));
        ThemeSongs.add(new FakeBeatmap("triangles", "cYsmix - triangles (peppy) [peppy].osu"));
        ThemeSongs.add(new FakeBeatmap("welcome", "nekodex - welcome to osu! (peppy).osu"));
        ThemeSongs.printAll();
    }
}
