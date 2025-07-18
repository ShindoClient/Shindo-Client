package me.miki.shindo.ui.comp.impl.field;

import java.awt.Color;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.color.ColorManager;
import me.miki.shindo.management.color.palette.ColorPalette;
import me.miki.shindo.management.color.palette.ColorType;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.utils.TimerUtils;
import me.miki.shindo.utils.animation.simple.SimpleAnimation;

public class CompTextBox extends CompTextBoxBase {

	private String defaultText = null;
	private TimerUtils timer = new TimerUtils();
	private SimpleAnimation animation = new SimpleAnimation();
	
	public CompTextBox(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	public CompTextBox() {
		super(0, 0, 0, 0);
	}
	
	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		
		Shindo instance = Shindo.getInstance();
		NanoVGManager nvg = instance.getNanoVGManager();
		ColorManager colorManager = instance.getColorManager();
		ColorPalette palette = colorManager.getPalette();
		
		float height = this.getHeight();
		int selectionEnd = this.getSelectionEnd();
		int cursorPosition = this.getCursorPosition();
		String text = this.getText();
		boolean focused = this.isFocused();
		
		float addX = 0;
		float halfHeight = height / 2F;
		
		int outTextSize = 0;
		String resultText = "";
		
		for(char c : this.getText().toCharArray()) {
			
			resultText = resultText + Character.toString(c);
			
			if(nvg.getTextWidth(resultText, halfHeight, Fonts.REGULAR) + halfHeight + 5 > this.getWidth()) {
				outTextSize++;
				
			    addX = this.getWidth() - nvg.getTextWidth(resultText, halfHeight, Fonts.REGULAR) - halfHeight - 5;
			}
		}
		
	    if(selectionEnd < outTextSize) {
	    	
			StringBuilder reversedText = new StringBuilder(this.getText()).reverse();
			
	    	addX = this.getWidth() - nvg.getTextWidth(reversedText.toString().substring(outTextSize - selectionEnd), halfHeight, Fonts.REGULAR) - halfHeight - 5;
	    }
	    
		nvg.drawRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 6, palette.getBackgroundColor(ColorType.NORMAL));
		
		nvg.save();
		nvg.scissor(this.getX() + 1, this.getY(), this.getWidth() - 2, this.getHeight());
		
		if(cursorPosition != selectionEnd) {
			
			int start = selectionEnd > cursorPosition ? cursorPosition : selectionEnd;
			int end = selectionEnd > cursorPosition ? selectionEnd : cursorPosition;
			
			float selectionWidth = nvg.getTextWidth(this.getText().substring(start, end), halfHeight, Fonts.REGULAR);
			float offset = nvg.getTextWidth(this.getText().substring(0, start), halfHeight, Fonts.REGULAR);
			
			if(selectionWidth != 0) {
				nvg.drawRect(this.getX() + 4 + offset + addX, this.getY() + (this.getHeight() / 2) - (nvg.getTextHeight(text, halfHeight, Fonts.REGULAR) / 2), selectionWidth, nvg.getTextHeight(text, halfHeight, Fonts.REGULAR), new Color(0, 135, 247));
			}
		}
		
		animation.setAnimation(!focused && this.getText().isEmpty() ? 1.0F : 0.0F, 16);
		
		if(this.getText().isEmpty() && defaultText != null) {
			nvg.save();
			nvg.translate((animation.getValue() * 8) - 8, 0);
			nvg.drawText(defaultText, this.getX() + 5, this.getY() + (this.getHeight() / 2) - (nvg.getTextHeight(text, halfHeight, Fonts.REGULAR) / 2) + 1, palette.getFontColor(ColorType.DARK, (int) (animation.getValue() * 255)), halfHeight, Fonts.REGULAR);
			nvg.restore();
		}
		
		nvg.drawText(this.getText(), this.getX() + 5 + addX, this.getY() + (this.getHeight() / 2) - (nvg.getTextHeight(text, halfHeight, Fonts.REGULAR) / 2) + 1, palette.getFontColor(ColorType.DARK), halfHeight, Fonts.REGULAR);
		
		if(timer.delay(600)) {
			
			float position = nvg.getTextWidth(this.getText(), halfHeight, Fonts.REGULAR) - nvg.getTextWidth(this.getText().substring(cursorPosition), halfHeight, Fonts.REGULAR);
			
			if(focused && cursorPosition == selectionEnd) {
				nvg.drawRect(this.getX() + 5 + addX + position, 
						this.getY() + (this.getHeight() / 2) - (nvg.getTextHeight(text, halfHeight, Fonts.REGULAR) / 2), 
						0.7F, 10, palette.getFontColor(ColorType.DARK));
			}
			
			if(timer.delay(1200)) {
				timer.reset();
			}
		}
		
		nvg.restore();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
	}

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}
}
