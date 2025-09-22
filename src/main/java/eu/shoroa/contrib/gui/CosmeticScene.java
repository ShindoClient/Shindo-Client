package eu.shoroa.contrib.gui;


import me.miki.shindo.gui.modmenu.category.impl.CosmeticsCategory;
import me.miki.shindo.management.language.TranslateText;

public class CosmeticScene {

    private final String icon;
    private final TranslateText nameTranslate;
    private final TranslateText descriptionTranslate;
    protected CosmeticsCategory parent;

    public CosmeticScene(CosmeticsCategory parent, TranslateText nameTranslate, TranslateText descriptionTranslate, String icon) {
        this.parent = parent;
        this.nameTranslate = nameTranslate;
        this.descriptionTranslate = descriptionTranslate;
        this.icon = icon;
    }

    public void initGui() {
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return nameTranslate.getText();
    }

    public String getDescription() {
        return descriptionTranslate.getText();
    }

    public int getX() {
        return parent.getSceneX();
    }

    public int getY() {
        return parent.getSceneY();
    }

    public int getWidth() {
        return parent.getSceneWidth();
    }

    public int getHeight() {
        return parent.getSceneHeight();
    }
}
