package eu.shoroa.contrib.gui.cosmetics;

import eu.shoroa.contrib.gui.CosmeticScene;
import me.miki.shindo.Shindo;
import me.miki.shindo.gui.modmenu.category.impl.CosmeticsCategory;
import me.miki.shindo.management.color.AccentColor;
import me.miki.shindo.management.color.ColorManager;
import me.miki.shindo.management.color.palette.ColorPalette;
import me.miki.shindo.management.color.palette.ColorType;
import me.miki.shindo.management.cosmetic.cape.CapeCategory;
import me.miki.shindo.management.cosmetic.cape.CapeManager;
import me.miki.shindo.management.cosmetic.cape.impl.Cape;
import me.miki.shindo.management.cosmetic.cape.impl.CustomCape;
import me.miki.shindo.management.cosmetic.cape.impl.NormalCape;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.management.nanovg.font.LegacyIcon;
import me.miki.shindo.management.notification.NotificationType;
import me.miki.shindo.utils.ColorUtils;
import me.miki.shindo.utils.SearchUtils;
import me.miki.shindo.utils.mouse.MouseUtils;
import me.miki.shindo.utils.mouse.Scroll;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class CapesScene extends CosmeticScene {
    private final Scroll scroll;
    Color noColor = new Color(0, 0, 0, 0);
    private CapeCategory currentCategory = CapeCategory.ALL;

    public CapesScene(CosmeticsCategory parent) {
        super(parent, TranslateText.CAPES, TranslateText.CAPES_DESCRIPTION, LegacyIcon.STAR);
        this.scroll = parent.scroll;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Shindo instance = Shindo.getInstance();
        NanoVGManager nvg = instance.getNanoVGManager();
        ColorManager colorManager = instance.getColorManager();
        AccentColor accentColor = colorManager.getCurrentColor();
        ColorPalette palette = colorManager.getPalette();
        CapeManager capeManager = instance.getCapeManager();
        Color defaultColor = palette.getBackgroundColor(ColorType.DARK);

        int offsetX = 0;
        int capeCount = 0;
        float offsetY = 3;
        int index = 1;
        int prevIndex = 1;

        nvg.save();
        nvg.translate(0, scroll.getValue());

        for (CapeCategory c : CapeCategory.values()) {

            float textWidth = nvg.getTextWidth(c.getName(), 9, Fonts.MEDIUM);
            boolean isCurrentCategory = c.equals(currentCategory);

            c.getBackgroundAnimation().setAnimation(isCurrentCategory ? 1.0F : 0.0F, 16);

            Color color1 = ColorUtils.applyAlpha(accentColor.getColor1(), (int) (c.getBackgroundAnimation().getValue() * 255));
            Color color2 = ColorUtils.applyAlpha(accentColor.getColor2(), (int) (c.getBackgroundAnimation().getValue() * 255));
            Color textColor = c.getTextColorAnimation().getColor(isCurrentCategory ? Color.WHITE : palette.getFontColor(ColorType.DARK), 20);

            nvg.drawRoundedRect(this.getX() + offsetX, this.getY() + offsetY - 3, textWidth + 20, 16, 6, defaultColor);
            nvg.drawGradientRoundedRect(this.getX() + offsetX, this.getY() + offsetY - 3, textWidth + 20, 16, 6, color1, color2);

            nvg.drawText(c.getName(), this.getX() + offsetX + ((textWidth + 20) - textWidth) / 2, this.getY() + offsetY + 1.5F, textColor, 9, Fonts.MEDIUM);

            offsetX += (int) (textWidth + 28);
        }

        offsetX = 0;
        offsetY = offsetY + 23;

        for (Cape cape : capeManager.getCapes()) {

            if (filterCape(cape)) {
                continue;
            }

            cape.getAnimation().setAnimation(cape.equals(capeManager.getCurrentCape()) ? 1.0F : 0.0F, 16);
            nvg.drawGradientRoundedRect(this.getX() + offsetX - 2, this.getY() + offsetY - 2, 88 + 4, 135 + 4, 8.5F, ColorUtils.applyAlpha(accentColor.getColor1(), (int) (cape.getAnimation().getValue() * 255)), ColorUtils.applyAlpha(accentColor.getColor2(), (int) (cape.getAnimation().getValue() * 255)));
            nvg.drawRoundedRect(this.getX() + offsetX, this.getY() + offsetY, 88, 135, 8, palette.getBackgroundColor(ColorType.DARK));

            if (cape instanceof NormalCape) {

                NormalCape c = ((NormalCape) cape);

                if (c.getSample() != null) {
                    nvg.drawRoundedImage(c.getSample(), this.getX() + 9 + offsetX, this.getY() + offsetY + 9, 70, 105, 8);
                }
            } else if (cape instanceof CustomCape) {

                CustomCape c = ((CustomCape) cape);

                if (c.getSample() != null) {
                    nvg.drawRoundedImage(c.getSample(), this.getX() + 9 + offsetX, this.getY() + offsetY + 9, 70, 105, 8);
                }
            }
            Color cColour = palette.getFontColor(ColorType.DARK);
            nvg.drawCenteredText(cape.getName(), this.getX() + offsetX + 44, this.getY() + offsetY + 120.5F, cColour, 10, Fonts.MEDIUM);

            offsetX += 100;

            if (index % 4 == 0) {
                offsetX = 0;
                offsetY += 147;
                prevIndex++;
            }

            index++;
            capeCount++;
        }

        scroll.setMaxScroll(prevIndex == 1 ? 0 : offsetY - (147 / 1.48F));

        nvg.restore();
        if (currentCategory.equals(CapeCategory.CUSTOM)) {
            if (capeCount == 0) {
                nvg.drawCenteredText("You have no custom capes.", getX() + (getWidth() / 2f), getY() + (getHeight() / 2f) - 14, palette.getFontColor(ColorType.DARK), 12, Fonts.SEMIBOLD);
                nvg.drawCenteredText("You can click the folder button at the top to open the folder!", getX() + (getWidth() / 2f), getY() + (getHeight() / 2f), palette.getFontColor(ColorType.DARK), 9, Fonts.MEDIUM);
                nvg.drawCenteredText("(Shindo only supports capes that are PNG using Minecraft layout. You may need to restart Shindo!)", getX() + (getWidth() / 2f), getY() + (getHeight() / 2f) + 12, palette.getFontColor(ColorType.NORMAL), 7, Fonts.REGULAR);
            }
            // you may need to reload the game
        }

        nvg.drawVerticalGradientRect(getX(), this.getY() - 15, getWidth(), 12, palette.getBackgroundColor(ColorType.NORMAL), noColor); //top
        nvg.drawVerticalGradientRect(getX(), this.getY() + this.getHeight() + 3, getWidth(), 12, noColor, palette.getBackgroundColor(ColorType.NORMAL)); // bottom
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (!MouseUtils.isInside(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) return;

        Shindo instance = Shindo.getInstance();
        NanoVGManager nvg = instance.getNanoVGManager();

        int offsetX = 0;
        float offsetY = 3 + scroll.getValue();
        CapeManager capeManager = instance.getCapeManager();
        int index = 1;

        for (CapeCategory c : CapeCategory.values()) {

            float textWidth = nvg.getTextWidth(c.getName(), 9, Fonts.MEDIUM);

            if (MouseUtils.isInside(mouseX, mouseY, this.getX() + offsetX, this.getY() + offsetY - 3, textWidth + 20, 16) && mouseButton == 0) {
                currentCategory = c;
            }

            offsetX += (int) (textWidth + 28);
        }

        offsetX = 0;
        offsetY = offsetY + 23;

        for (Cape cape : capeManager.getCapes()) {

            if (filterCape(cape)) {
                continue;
            }

            if (MouseUtils.isInside(mouseX, mouseY, this.getX() + offsetX, this.getY() + offsetY, 88, 135) && mouseButton == 0) {
                if (capeManager.canUseCape(Minecraft.getMinecraft().getSession().getProfile().getId(), cape)) {
                    capeManager.setCurrentCape(cape);
                } else {
                    instance.getNotificationManager().post(TranslateText.ERROR, capeManager.getTranslateError(cape.getRequiredRole()), NotificationType.ERROR);
                }
            }

            offsetX += 100;

            if (index % 4 == 0) {
                offsetX = 0;
                offsetY += 147;
            }

            index++;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        scroll.onKey(keyCode);
        if (keyCode != 0xD0 && keyCode != 0xC8 && keyCode != Keyboard.KEY_ESCAPE)
            parent.getSearchBox().setFocused(true);
    }

    private boolean filterCape(Cape cape) {

        if (!currentCategory.equals(CapeCategory.ALL) && !currentCategory.equals(cape.getCategory())) {
            return true;
        }

        return !parent.getSearchBox().getText().isEmpty() && !SearchUtils.isSimilar(cape.getName(), parent.getSearchBox().getText());
    }
}
