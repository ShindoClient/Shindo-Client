package me.miki.shindo.management.mods.impl;

import org.lwjgl.input.Keyboard;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.event.EventTarget;
import me.miki.shindo.management.event.impl.EventRender2D;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.mods.HUDMod;
import me.miki.shindo.management.mods.settings.impl.BooleanSetting;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.utils.animation.simple.SimpleAnimation;

import java.awt.*;

public class KeystrokesMod extends HUDMod {

	private BooleanSetting spaceSetting = new BooleanSetting(TranslateText.SPACE, this, true);
	private BooleanSetting unmarkedSetting = new BooleanSetting(TranslateText.UNMARKED, this, false);
	
	private SimpleAnimation[] animations = new SimpleAnimation[5];
	
	public KeystrokesMod() {
		super(TranslateText.KEYSTROKES, TranslateText.KEYSTROKES_DESCRIPTION);
		
	    for (int i = 0; i < 5; i++) {
	        animations[i] = new SimpleAnimation();
	    }
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		
		NanoVGManager nvg = Shindo.getInstance().getNanoVGManager();
		
		nvg.setupAndDraw(() -> drawNanoVG());
	}
	
	private void drawNanoVG() {
		
		boolean openGui = mc.currentScreen != null;
		
		animations[0].setAnimation(!openGui && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()) ? 1.0F : 0.0F, 16);
		animations[1].setAnimation(!openGui && Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()) ? 1.0F : 0.0F, 16);
		animations[2].setAnimation(!openGui && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()) ? 1.0F : 0.0F, 16);
		animations[3].setAnimation(!openGui && Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()) ? 1.0F : 0.0F, 16);
		animations[4].setAnimation(!openGui && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) ? 1.0F : 0.0F, 16);
		
		// W
		this.drawBackground(32, 0, 28, 28);
		
		// A
		this.drawBackground(0, 32, 28, 28);
		
		// S
		this.drawBackground(32, 32, 28, 28);
		
		// D
		this.drawBackground(64, 32, 28, 28);

		// W
		this.save();
		this.scale(32, 0, 28, 28, animations[0].getValue());
		this.drawHighlight(32, 0, 28, 28, 6, this.getFontColor((int) (120 * animations[0].getValue())));
		this.restore();
		
		// A
		this.save();
		this.scale(0, 32, 28, 28, animations[1].getValue());
		this.drawHighlight(0, 32, 28, 28, 6, this.getFontColor((int) (120 * animations[1].getValue())));
		this.restore();
		
		// S
		this.save();
		this.scale(32, 32, 28, 28, animations[2].getValue());
		this.drawHighlight(32, 32, 28, 28, 6, this.getFontColor((int) (120 * animations[2].getValue())));
		this.restore();
		
		// D
		this.save();
		this.scale(64, 32, 28, 28, animations[3].getValue());
		this.drawHighlight(64, 32, 28, 28, 6, this.getFontColor((int) (120 * animations[3].getValue())));
		this.restore();
		
		if(!unmarkedSetting.isToggled()) {
			this.drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindForward.getKeyCode()), 32 + (28 / 2), (28 / 2) - 4, 12, getHudFont(1));
			this.drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindLeft.getKeyCode()), 0 + (28 / 2), 32 + (28 / 2) - 4, 12, getHudFont(1));
			this.drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindBack.getKeyCode()), 32 + (28 / 2), 32 + (28 / 2) - 4, 12, getHudFont(1));
			this.drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindRight.getKeyCode()), 64 + (28 / 2), 32 + (28 / 2) - 4, 12, getHudFont(1));
		}
		
		if(spaceSetting.isToggled()) {
			
			this.drawBackground(0, 64, (28 * 3) + 8, 22);
			
			this.save();
			this.scale(0, 64, (28 * 3) + 8, 22, animations[4].getValue());
			this.drawHighlight(0, 64, (28 * 3) + 8, 22, 6, this.getFontColor((int) (120 * animations[4].getValue())));
			this.restore();
			
			if(!unmarkedSetting.isToggled()) {
				this.drawRoundedRect(10, 74F, (26 * 3) - 6, 2, 1);
			}
		}
		
		this.setWidth(28 * 3 + 8);
		this.setHeight(spaceSetting.isToggled() ? 64 + 22 : 32 + 28);
	}
	private void drawHighlight(float addX, float addY, float width, float height, float radius, Color color){
		boolean rect = InternalSettingsMod.getInstance().getModThemeSetting().getOption().getTranslate().equals(TranslateText.RECT);
		if (!rect) this.drawRoundedRect(addX, addY, width, height, radius, color); else  this.drawRect(addX, addY, width, height, color);
	}
}
