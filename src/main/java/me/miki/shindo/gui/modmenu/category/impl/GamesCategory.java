package me.miki.shindo.gui.modmenu.category.impl;

import me.miki.shindo.Shindo;
import me.miki.shindo.gui.modmenu.GuiModMenu;
import me.miki.shindo.gui.modmenu.category.Category;
import me.miki.shindo.gui.modmenu.category.impl.game.GameScene;
import me.miki.shindo.gui.modmenu.category.impl.game.impl.BirdScene;
import me.miki.shindo.gui.modmenu.category.impl.game.impl.ClickyCatScene;
import me.miki.shindo.management.color.palette.ColorPalette;
import me.miki.shindo.management.color.palette.ColorType;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.management.nanovg.font.LegacyIcon;
import me.miki.shindo.utils.animation.normal.Animation;
import me.miki.shindo.utils.animation.normal.Direction;
import me.miki.shindo.utils.animation.normal.other.SmoothStepAnimation;
import me.miki.shindo.utils.mouse.MouseUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class GamesCategory extends Category {

	private Animation sceneAnimation;

	private ArrayList<GameScene> scenes = new ArrayList<>();
	private GameScene currentScene;

	public GamesCategory(GuiModMenu parent) {
		super(parent, TranslateText.SETTINGS, LegacyIcon.GAME, false, false);

		scenes.add(new BirdScene(this));
		scenes.add(new ClickyCatScene(this));
		
	}
	
	@Override
	public void initGui() {
		sceneAnimation = new SmoothStepAnimation(260, 1.0);
		sceneAnimation.setValue(1.0);
		
		for(GameScene scene : scenes) {
			scene.initGui();
		}
	}

	@Override
	public void initCategory() {
		scroll.resetAll();
		sceneAnimation = new SmoothStepAnimation(260, 1.0);
		sceneAnimation.setValue(1.0);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		Shindo instance = Shindo.getInstance();
		NanoVGManager nvg = instance.getNanoVGManager();
		ColorPalette palette = instance.getColorManager().getPalette();
		
		float offsetY = 15;
		
		if(sceneAnimation.isDone(Direction.FORWARDS)) {
			this.setCanClose(true);
			currentScene = null;
		}
		
		nvg.save();
		nvg.translate((float) -(600 - (sceneAnimation.getValue() * 600)), 0);
		nvg.translate(0, scroll.getValue());
		for(GameScene scene : scenes) {
			
			nvg.drawRoundedRect(this.getX() + 15, this.getY() + offsetY, this.getWidth() - 30, 40, 8, palette.getBackgroundColor(ColorType.DARK));
			//nvg.drawRoundedRect(this.getX() + 15, this.getY() + offsetY + 19.5F, this.getWidth() - 30, 1F, 0, new Color(255, 200, 10));
			nvg.drawText(scene.getIcon(), this.getX() + 26, this.getY() + offsetY + 13F, palette.getFontColor(ColorType.DARK), 14, Fonts.LEGACYICON);
			nvg.drawText(scene.getName(), this.getX() + 47, this.getY() + offsetY + 9F, palette.getFontColor(ColorType.DARK), 12.5F, Fonts.MEDIUM);
			nvg.drawText(scene.getDescription(), this.getX() + 47, this.getY() + offsetY + 23, palette.getFontColor(ColorType.NORMAL), 7.5F, Fonts.REGULAR);
			nvg.drawText(LegacyIcon.CHEVRON_RIGHT, this.getX() + this.getWidth() - 32, this.getY() + offsetY + 15, palette.getFontColor(ColorType.NORMAL), 10, Fonts.LEGACYICON);
			
			offsetY+=50;
		}
		
		nvg.restore();
		
		nvg.save();
		nvg.translate((float) (sceneAnimation.getValue() * 600), 0);
		
		if(currentScene != null) {
			currentScene.drawScreen(mouseX, mouseY, partialTicks);
		}
		
		nvg.restore();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		float offsetY = 15;
		
		for(GameScene scene : scenes) {
			
			if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 15, this.getY() + offsetY + scroll.getValue(), this.getWidth() - 30, 40) && mouseButton == 0 && currentScene == null) {
				currentScene = scene;
				this.setCanClose(false);
				sceneAnimation.setDirection(Direction.BACKWARDS);
			}
			
			offsetY+=50;
		}
		
		if(currentScene != null && sceneAnimation.isDone(Direction.BACKWARDS)) {
			currentScene.mouseClicked(mouseX, mouseY, mouseButton);
		}

		if (!MouseUtils.isInside(mouseX, mouseY, this.getX(), this.getY() ,  this.getWidth(), this.getHeight()) && mouseButton == 0) {
			sceneAnimation.setDirection(Direction.FORWARDS);
		}

		if(currentScene != null && mouseButton == 3) {
			sceneAnimation.setDirection(Direction.FORWARDS);
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		
		if(currentScene != null && sceneAnimation.isDone(Direction.BACKWARDS)) {
			currentScene.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		
		if(currentScene != null && keyCode == Keyboard.KEY_ESCAPE) {
			sceneAnimation.setDirection(Direction.FORWARDS);
		}
		if(currentScene != null && sceneAnimation.isDone(Direction.BACKWARDS)) {
			currentScene.keyTyped(typedChar, keyCode);
		}
	}
	
	public int getSceneX() {
		return getX() + 15;
	}
	
	public int getSceneY() {
		return getY() + 15;
	}
	
	public int getSceneWidth() {
		return getWidth() - 30;
	}
	
	public int getSceneHeight() {
		return getHeight() - 30;
	}
}
