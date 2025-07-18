package me.miki.shindo.gui.mainmenu.impl.login;

import me.miki.shindo.Shindo;
import me.miki.shindo.gui.mainmenu.GuiShindoMainMenu;
import me.miki.shindo.gui.mainmenu.MainMenuScene;
import me.miki.shindo.gui.mainmenu.impl.MainScene;
import me.miki.shindo.gui.mainmenu.impl.welcome.LastMessageScene;
import me.miki.shindo.management.account.AccountManager;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.utils.animation.normal.Animation;
import me.miki.shindo.utils.animation.normal.Direction;
import me.miki.shindo.utils.animation.normal.other.DecelerateAnimation;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;


public class MicrosoftLoginScene extends MainMenuScene {

	private Animation fadeAnimation;

	public MicrosoftLoginScene(GuiShindoMainMenu parent) {
		super(parent);
	}

	@Override
	public void initScene() {
		Shindo.getInstance().getAccountManager().getAuthenticator().loginWithPopUpWindow(afterMicrosoftLogin());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		ScaledResolution sr = new ScaledResolution(mc);
		NanoVGManager nvg = Shindo.getInstance().getNanoVGManager();
		String message = "Logging in...";

		if(fadeAnimation == null && this.getParent().isDoneBackgroundAnimation()) {
			fadeAnimation = new DecelerateAnimation(800, 1);
			fadeAnimation.setDirection(Direction.FORWARDS);
			fadeAnimation.reset();
		}

		if(fadeAnimation != null) {

			nvg.setupAndDraw(() -> {
				nvg.drawCenteredText(message, sr.getScaledWidth() / 2F,
						(sr.getScaledHeight() / 2F) - (nvg.getTextHeight(message, 26, Fonts.REGULAR) / 2),
						new Color(255, 255, 255, (int) (fadeAnimation.getValueFloat() * 255)), 26, Fonts.REGULAR);
			});

			if(fadeAnimation.isDone(Direction.BACKWARDS)) {
				if(Shindo.getInstance().getShindoAPI().isFirstLogin()) {
					setCurrentScene(getSceneByClass(LastMessageScene.class));
				} else {
					setCurrentScene(getSceneByClass(MainScene.class));
				}
			}


		}
	}

	public Runnable afterMicrosoftLogin() {

		AccountManager accountManager = Shindo.getInstance().getAccountManager();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if(accountManager.getCurrentAccount() != null) {
					fadeAnimation.setDirection(Direction.BACKWARDS);
				}
			}
		};

		return runnable;
	}

	@Override
	public void onSceneClosed() {
		fadeAnimation.reset();
	}
}
