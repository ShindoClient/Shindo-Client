package me.miki.shindo.management.mods.settings.impl;

import java.awt.Color;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.mods.Mod;
import me.miki.shindo.management.mods.settings.Setting;
import me.miki.shindo.utils.ColorUtils;

public class ColorSetting extends Setting {

	private float hue, saturation, brightness;
	private int alpha;
	private Color defaultColor, color;
	private boolean showAlpha;
	
	public ColorSetting(TranslateText text, Mod parent, Color color, boolean showAlpha) {
		super(text, parent);
		
		this.color = color;
		this.defaultColor = color;
		this.hue = ColorUtils.getHue(color);
		this.saturation = ColorUtils.getSaturation(color);
		this.brightness = ColorUtils.getBrightness(color);
		this.alpha = color.getAlpha();
		this.showAlpha = showAlpha;
		
		Shindo.getInstance().getModManager().addSettings(this);
	}
	
	@Override
	public void reset() {
		this.color = defaultColor;
		this.hue = ColorUtils.getHue(color);
		this.saturation = ColorUtils.getSaturation(color);
		this.brightness = ColorUtils.getBrightness(color);
		this.alpha = color.getAlpha();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public float getHue() {
		return hue;
	}

	public void setHue(float hue) {
		this.hue = hue;
		this.color = ColorUtils.applyAlpha(Color.getHSBColor(hue, saturation, brightness), alpha);
	}

	public float getSaturation() {
		return saturation;
	}

	public void setSaturation(float saturation) {
		this.saturation = saturation;
		this.color = ColorUtils.applyAlpha(Color.getHSBColor(hue, saturation, brightness), alpha);
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
		this.color = ColorUtils.applyAlpha(Color.getHSBColor(hue, saturation, brightness), alpha);
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
		this.color = ColorUtils.applyAlpha(color, alpha);
	}

	public boolean isShowAlpha() {
		return showAlpha;
	}
}
