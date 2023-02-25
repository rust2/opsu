package kww.useless.buttons.toolbar;

import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;
import itdelatrisu.opsu.ui.UI;

public class ToolbarUserButton extends ToolbarButton {
    public ToolbarUserButton()
    {
        super(GameImage.TOOLBAR_PROFILE.getImage());
    }

    @Override
    public void pressed()
    {
        SoundController.playSound(SoundEffect.MENUCLICK);
        UI.getNotificationManager().sendBarNotification("todo");
    }
}
