package kww.useless.buttons.toolbar;

import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;
import kww.useless.Instances;
import kww.useless.visuals.Playa;

public class ToolbarMusicButton extends ToolbarButton {
    public ToolbarMusicButton()
    {
        super(GameImage.TOOLBAR_PLAYER.getImage());
    }

    @Override
    public void pressed()
    {
        super.toggle();
        SoundController.playSound(SoundEffect.MENUCLICK);

        switch (Instances.player.state)
        {
            case Opened:
            case InOpen:
                Instances.player.state = Playa.LocalState.InClose;
                break;
            case Closed:
            case InClose:
                Instances.player.state = Playa.LocalState.InOpen;
                break;
        }
    }
}
