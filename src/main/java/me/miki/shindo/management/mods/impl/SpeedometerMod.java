package me.miki.shindo.management.mods.impl;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.event.EventTarget;
import me.miki.shindo.management.event.impl.EventRender2D;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.mods.SimpleHUDMod;
import me.miki.shindo.management.mods.settings.impl.BooleanSetting;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.LegacyIcon;
import me.miki.shindo.utils.ColorUtils;
import me.miki.shindo.utils.GlUtils;
import me.miki.shindo.utils.PlayerUtils;
import net.minecraft.client.renderer.GlStateManager;

public class SpeedometerMod extends SimpleHUDMod {

	private BooleanSetting iconSetting = new BooleanSetting(TranslateText.ICON, this, true);
	private BooleanSetting graphSetting = new BooleanSetting(TranslateText.GRAPH, this, true);
	
	private int speedCount = 200;
	private double[] speeds = new double[speedCount];
	private long lastUpdate;
	private DecimalFormat speedFormat = new DecimalFormat("0.00");
	
	public SpeedometerMod() {
		super(TranslateText.SPEEDOMETER, TranslateText.SPEEDOMETER_DESCRIPTION);
	}

	@EventTarget
	public void onRender2D(EventRender2D event) {
		
		NanoVGManager nvg = Shindo.getInstance().getNanoVGManager();
		
		if(graphSetting.isToggled()) {
			nvg.setupAndDraw(() -> drawNanoVG());
			
			GlUtils.startTranslate(this.getX() - 3, this.getY());
			
			GL11.glLineWidth(1.5F);
			if(!mc.isGamePaused() && (lastUpdate == -1 || (System.currentTimeMillis() - lastUpdate) > 30)) {
				addSpeed(PlayerUtils.getSpeed() / 5);
				lastUpdate = System.currentTimeMillis();
			}
			
			GlStateManager.enableBlend();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glShadeModel(GL11.GL_SMOOTH);

			GL11.glBegin(GL11.GL_LINE_STRIP);
			
			ColorUtils.setColor(this.getFontColor().getRGB());
			
			for(int i = 0; i < speedCount; i++) {
				GL11.glVertex2d((this.getWidth() + 1) * i / (double) speedCount + 3,
						this.getHeight() - (speeds[i] * (16)) - 10);
			}

			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			ColorUtils.resetColor();
			
			GlUtils.stopTranslate();
			
			this.setWidth(155);
			this.setHeight(100);
		}else {
			this.draw();
		}
	}
	
	private void drawNanoVG() {
		this.drawBackground(155, 100);
		this.drawRect(0, 17.5F, 155, 1);
		this.drawText("Speed: " + speedFormat.format(PlayerUtils.getSpeed()) + " m/s", 5.5F, 6F, 10.5F, getHudFont(2));
	}
	
	@Override
	public String getText() {
		return "Speed: " + speedFormat.format(PlayerUtils.getSpeed()) + " m/s";
	}
	
	@Override
	public String getIcon() {
		return iconSetting.isToggled() ? LegacyIcon.ACTIVITY : null;
	}
	
	private void addSpeed(double speed) {
		
		if(speed > 3.8) {
			speed = 3.8;
		}
		
		System.arraycopy(speeds, 1, speeds, 0, speedCount - 1);
		speeds[speedCount - 1] = speed;
	}
}
