package kww.useless;

import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.options.Options;

/** Useless class for parallax calculation */
public class Parallax {

    static final float bgParallaxScale = GameImage.PARALLAX_SCALE;
    static final float logoParallaxScale = 1.016f;

    /**
     * Current backround image x offset
     *
     * @param mouseX mouse {@code x} coordinate
     *
     * @return backround image offset
     */
    public static float getBgX(int mouseX)
    {
        if (!Options.isParallaxEnabled())
            return 0f;

        float offsetX = Instances.mainMenu.width * (bgParallaxScale - 1f);
        return -offsetX / 2f * (mouseX - Instances.mainMenu.widthCenter) / Instances.mainMenu.widthCenter;
    }

    /**
     * Current backround image y offset
     *
     * @param mouseY mouse {@code y} coordinate
     *
     * @return backround image offset
     */
    public static float getBgY(int mouseY)
    {
        if (!Options.isParallaxEnabled())
            return 0f;

        float offsetY = Instances.mainMenu.height * (bgParallaxScale - 1f);
        return -offsetY / 2f * (mouseY - Instances.mainMenu.heightCenter) / Instances.mainMenu.heightCenter;
    }

    /**
     * Current logo x offset
     *
     * @param mouseX mouse {@code x} coordinate
     *
     * @return backround image offset
     */
    public static float getLogoX(int mouseX)
    {
        if (!Options.isParallaxEnabled())
            return 0f;

        float offsetX = Instances.mainMenu.width * (logoParallaxScale - 1f);
        return -offsetX / 2f * (mouseX - Instances.mainMenu.widthCenter) / Instances.mainMenu.widthCenter;
    }

    /**
     * Current logo y offset
     *
     * @param mouseY mouse {@code y} coordinate
     *
     * @return backround image offset
     */
    public static float getLogoY(int mouseY)
    {
        if (!Options.isParallaxEnabled())
            return 0f;

        float offsetY = Instances.mainMenu.height * (logoParallaxScale - 1f);
        return -offsetY / 2f * (mouseY - Instances.mainMenu.heightCenter) / Instances.mainMenu.heightCenter;
    }
}
