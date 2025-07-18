package me.miki.shindo.gui.mainmenu.impl.welcome;

import java.awt.Color;

import me.miki.shindo.Shindo;
import me.miki.shindo.gui.mainmenu.GuiShindoMainMenu;
import me.miki.shindo.gui.mainmenu.MainMenuScene;
import me.miki.shindo.management.color.AccentColor;
import me.miki.shindo.management.color.Theme;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.utils.animation.normal.Animation;
import me.miki.shindo.utils.animation.normal.Direction;
import me.miki.shindo.utils.animation.normal.other.DecelerateAnimation;
import me.miki.shindo.utils.buffer.ScreenAlpha;
import me.miki.shindo.utils.mouse.MouseUtils;
import me.miki.shindo.utils.mouse.Scroll;
import me.miki.shindo.utils.render.BlurUtils;
import net.minecraft.client.gui.ScaledResolution;

public class ThemeSelectScene extends MainMenuScene {

	private int x, y, width, height;
	
	private Animation fadeAnimation;
	private ScreenAlpha screenAlpha = new ScreenAlpha();
	private Scroll scroll = new Scroll();
	private Theme currentTheme = Theme.DARK;
	
	public ThemeSelectScene(GuiShindoMainMenu parent) {
		super(parent);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		width = 280;
		height = 146;
		x = sr.getScaledWidth() / 2 - (width / 2);
		y = sr.getScaledHeight() / 2 - (height / 2);
		
		if(fadeAnimation == null) {
			fadeAnimation = new DecelerateAnimation(800, 1);
			fadeAnimation.setDirection(Direction.FORWARDS);
			fadeAnimation.reset();
		}
		
		BlurUtils.drawBlurScreen(14);
		
		screenAlpha.wrap(() -> drawNanoVG(), fadeAnimation.getValueFloat());
		
		if(fadeAnimation.isDone(Direction.BACKWARDS)) {
			this.setCurrentScene(this.getSceneByClass(AccentColorSelectScene.class));
		}
	}
	
	private void drawNanoVG() {
		
		Shindo instance = Shindo.getInstance();
		NanoVGManager nvg = instance.getNanoVGManager();
		AccentColor currentColor = instance.getColorManager().getCurrentColor();
		
		int offsetX = 0;
		int index = 1;
		
		nvg.drawRoundedRect(x, y, width, height, 8, this.getBackgroundColor());
		nvg.drawCenteredText("Choose a theme", x + (width / 2), y + 10, Color.WHITE, 16, Fonts.MEDIUM);
		nvg.drawRect(x, y + 27, width, 1, Color.WHITE);
		
		scroll.onScroll();
		scroll.onAnimation();
		
		nvg.save();
		nvg.scissor(x, y + 27, width, height - 27);
		nvg.translate(scroll.getValue(), 0);
		
		for(Theme t : Theme.values()) {
			
			t.getAnimation().setAnimation(currentTheme.equals(t) ? 1.0F : 0.0F, 16);
			
			drawModMenuExample(x + offsetX + 14, y + 42, t);
			nvg.save();
			nvg.setAlpha(t.getAnimation().getValue());
			nvg.drawGradientOutlineRoundedRect(x + offsetX + 14, y + 42, 90, 56, 6, 1, currentColor.getColor1(), currentColor.getColor2());
			nvg.restore();
			nvg.drawCenteredText(t.getName(), x + offsetX + 14 + (90 / 2), y + 104, Color.WHITE, 9.5F, Fonts.REGULAR);
			
			offsetX+=102;
			index++;
		}
		
		scroll.setMaxScroll((index - 3.58F) * 102);
		
		nvg.restore();
		
		nvg.drawRoundedRect(x + width - 86, y + height - 26, 80, 20, 6, this.getBackgroundColor());
		nvg.drawCenteredText("Next", x + width - 86 + (80 / 2), y + height - 20, Color.WHITE, 10, Fonts.REGULAR);
	}
	
	private void drawModMenuExample(float x, float y, Theme theme) {
		
		NanoVGManager nvg = Shindo.getInstance().getNanoVGManager();
		
		float width = 90;
		float height = 56;
		float offsetY = 0;
		
		nvg.drawRoundedRect(x, y, width, height, 6, theme.getNormalBackgroundColor());
		nvg.drawRoundedRectVarying(x, y, 12, height, 6, 0, 6, 0, theme.getDarkBackgroundColor());
		
		for(int i = 0; i < 3; i++) {
			
			nvg.drawRoundedRect(x + (15), y + offsetY + (6), width - (20), 12, 2.5F, theme.getDarkBackgroundColor());
			nvg.drawRoundedRect(x + (17), y + offsetY + (7.5F), 9, 9, 2, theme.getNormalBackgroundColor());
			
			offsetY+=16;
		}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		float offsetX = scroll.getValue();
		
		for(Theme t : Theme.values()) {
			
			if(MouseUtils.isInside(mouseX, mouseY, x + offsetX + 14, y + 42, 90, 56) && mouseButton == 0) {
				currentTheme = t;
			}
			
			offsetX+=102;
		}
		
		if(MouseUtils.isInside(mouseX, mouseY, x + width - 86, y + height - 26, 80, 20) && mouseButton == 0) {
			Shindo.getInstance().getColorManager().setTheme(currentTheme);
			fadeAnimation.setDirection(Direction.BACKWARDS);
		}
	}
}
