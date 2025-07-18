package me.miki.shindo.gui.mainmenu.impl.login;

import me.miki.shindo.Shindo;
import me.miki.shindo.gui.mainmenu.GuiShindoMainMenu;
import me.miki.shindo.gui.mainmenu.MainMenuScene;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.utils.TimerUtils;
import me.miki.shindo.utils.animation.normal.Animation;
import me.miki.shindo.utils.animation.normal.Direction;
import me.miki.shindo.utils.animation.normal.other.DecelerateAnimation;
import me.miki.shindo.utils.render.BlurUtils;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class LoginErrorScene extends MainMenuScene {

    private Animation fadeAnimation;
    private int step;
    private String message;

    private String loginError;

    private TimerUtils timer = new TimerUtils();

    public LoginErrorScene(GuiShindoMainMenu parent) {
        super(parent);

        step = 0;
    }

    @Override
    public void initScene() {

        step = 0;

        if (fadeAnimation != null) {
            fadeAnimation.reset();
            timer.reset();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution sr = new ScaledResolution(mc);
        NanoVGManager nvg = Shindo.getInstance().getNanoVGManager();
        String error = "An Error Occurred!";
        String errorMessage = "Error: " + loginError;
        String redirectMessage = "Redirecting to Login Screen ...";

        BlurUtils.drawBlurScreen(14);

        if(fadeAnimation == null && this.getParent().isDoneBackgroundAnimation()) {
            fadeAnimation = new DecelerateAnimation(800, 1);
            fadeAnimation.setDirection(Direction.FORWARDS);
            fadeAnimation.reset();
            timer.reset();
        }

        if(fadeAnimation != null) {

            switch(step) {
                case 0:
                    message = error;
                    break;
                case 1:
                    message = errorMessage;
                    break;
                case 2:
                    message = redirectMessage;
                    break;
            }

            nvg.setupAndDraw(() -> {
                nvg.drawCenteredText(message, sr.getScaledWidth() / 2F,
                        (sr.getScaledHeight() / 2F) - (nvg.getTextHeight(message, 26, Fonts.REGULAR) / 2),
                        new Color(255, 255, 255, (int) (fadeAnimation.getValueFloat() * 255)), 26, Fonts.REGULAR);
            });

            if(timer.delay(2500) && fadeAnimation.getDirection().equals(Direction.FORWARDS)) {
                fadeAnimation.setDirection(Direction.BACKWARDS);
                timer.reset();
            }

            if(fadeAnimation.isDone(Direction.BACKWARDS)) {

                if(step == 2) {
                    this.setCurrentScene(this.getSceneByClass(AccountScene.class));
                    return;
                }

                step++;
                fadeAnimation.setDirection(Direction.FORWARDS);
            }
        }
    }

    @Override
    public void setErrorMessage(String error) {
        this.loginError = error;
    }
}
