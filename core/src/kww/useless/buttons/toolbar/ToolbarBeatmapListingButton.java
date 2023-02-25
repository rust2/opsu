package kww.useless.buttons.toolbar;

import fluddokt.newdawn.slick.state.transition.EasedFadeOutTransition;
import fluddokt.newdawn.slick.state.transition.FadeInTransition;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Opsu;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;

import static kww.useless.UselessUtils.game;

public class ToolbarBeatmapListingButton extends ToolbarButton {
    public ToolbarBeatmapListingButton()
    {
        super(GameImage.TOOLBAR_DOWNLOADS.getImage());
    }

    @Override
    public void pressed()
    {
        SoundController.playSound(SoundEffect.MENUHIT);
        game.enterState(Opsu.STATE_DOWNLOADSMENU, new EasedFadeOutTransition(), new FadeInTransition());
    }
}
