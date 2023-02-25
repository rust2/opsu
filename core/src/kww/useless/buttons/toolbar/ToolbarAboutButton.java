package kww.useless.buttons.toolbar;

import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Opsu;
import itdelatrisu.opsu.audio.SoundController;
import itdelatrisu.opsu.audio.SoundEffect;
import itdelatrisu.opsu.states.ButtonMenu;

import static kww.useless.UselessUtils.game;

public class ToolbarAboutButton extends ToolbarButton {
    public ToolbarAboutButton()
    {
        super(GameImage.TOOLBAR_ABOUT.getImage());
    }

    @Override
    public void pressed()
    {
        SoundController.playSound(SoundEffect.MENUHIT);
        ((ButtonMenu) game.getState(Opsu.STATE_BUTTONMENU)).setMenuState(ButtonMenu.MenuState.ABOUT);
        game.enterState(Opsu.STATE_BUTTONMENU);
    }
}
