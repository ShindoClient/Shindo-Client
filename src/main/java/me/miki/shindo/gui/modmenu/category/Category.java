package me.miki.shindo.gui.modmenu.category;

import me.miki.shindo.gui.modmenu.GuiModMenu;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.ui.comp.impl.field.CompSearchBox;
import me.miki.shindo.utils.animation.ColorAnimation;
import me.miki.shindo.utils.animation.simple.SimpleAnimation;
import me.miki.shindo.utils.mouse.Scroll;
import net.minecraft.client.Minecraft;

public class Category {

	public Minecraft mc = Minecraft.getMinecraft();
	
	private String icon;
	private GuiModMenu parent;
	
	private TranslateText nameTranslate;
	
	private SimpleAnimation textAnimation = new SimpleAnimation();
	private ColorAnimation textColorAnimation = new ColorAnimation();
	private SimpleAnimation categoryAnimation = new SimpleAnimation();
	
	private boolean initialized;
	
	public Scroll scroll;
	
	private boolean showSearchBox;
	private boolean showTitle;

	public Category(GuiModMenu parent, TranslateText nameTranslate, String icon, boolean showSearchBox, boolean showTitle) {
		this.nameTranslate = nameTranslate;
		this.parent = parent;
		this.icon = icon;
		this.initialized = false;
		this.scroll = parent.getScroll();
		this.showSearchBox = showSearchBox;
		this.showTitle = showTitle;
	}
	
	public void initGui() {}
	
	public void initCategory() {}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}
	
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {}
	
	public void keyTyped(char typedChar, int keyCode) {}
	
	public String getName() {
		return nameTranslate.getText();
	}

	public String getNameKey() {
		return nameTranslate.getKey();
	}

	public String getIcon() {
		return icon;
	}

	public int getX() {
		return parent.getX() + 32;
	}
	
	public int getY() {
		int yOff = (showTitle) ? 31 : 0;
		return parent.getY() + yOff;
	}
	
	public int getWidth() {
		return parent.getWidth() - 32;
	}
	
	public int getHeight() {
		int yOff = (showTitle) ? 31 : 0;
		return parent.getHeight() - yOff;
	}

	public ColorAnimation getTextColorAnimation() {
		return textColorAnimation;
	}

	public SimpleAnimation getTextAnimation() {
		return textAnimation;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public SimpleAnimation getCategoryAnimation() {
		return categoryAnimation;
	}

	public boolean isShowSearchBox() {
		return showSearchBox;
	}

	public boolean isShowTitle() {return showTitle;}
	
	public CompSearchBox getSearchBox() {
		return parent.getSearchBox();
	}
	
	public boolean isCanClose() {
		return parent.isCanClose();
	}
	
	public void setCanClose(boolean canClose) {
		parent.setCanClose(canClose);
	}
}
