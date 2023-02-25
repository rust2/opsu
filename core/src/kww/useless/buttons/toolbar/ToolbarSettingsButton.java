package kww.useless.buttons.toolbar;

import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;
import kww.useless.Instances;

public class ToolbarSettingsButton extends ToolbarButton {
    public ToolbarSettingsButton()
    {
        super(GameImage.TOOLBAR_SETTINGS.getImage());
    }

    @Override
    public ToolbarButton setParent(Toolbar parent)
    {
        super.setParent(parent);
        size.x *= 1.4f;

        return this;
    }

    @Override
    public void pressed()
    {
        SoundController.playSound(SoundEffect.MENUCLICK);
        Instances.mainMenu.openSettingsMenu();
    }
}
