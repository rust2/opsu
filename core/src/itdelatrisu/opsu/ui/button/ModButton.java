package itdelatrisu.opsu.ui.button;

import fluddokt.opsu.fake.Image;
import itdelatrisu.opsu.ui.BaseButton;
import itdelatrisu.opsu.ui.animations.AnimationEquation;

public class ModButton extends BaseButton {
    public ModButton(Image img, float x, float y)
    {
        super(img, x, y);
        this.setHoverAnimationDuration(300);
        this.setHoverAnimationEquation(AnimationEquation.IN_OUT_BACK);
        this.setHoverExpand(1.2f);
        this.setHoverRotate(10f);
    }
}
