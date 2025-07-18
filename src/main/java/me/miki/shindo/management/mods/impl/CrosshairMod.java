package me.miki.shindo.management.mods.impl;

import me.miki.shindo.management.event.EventTarget;
import me.miki.shindo.management.event.impl.EventRender2D;
import me.miki.shindo.management.event.impl.EventRenderCrosshair;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.mods.Mod;
import me.miki.shindo.management.mods.ModCategory;
import me.miki.shindo.management.mods.impl.crosshair.LayoutManager;
import me.miki.shindo.management.mods.settings.impl.BooleanSetting;
import me.miki.shindo.management.mods.settings.impl.CellGridSetting;
import me.miki.shindo.management.mods.settings.impl.ColorSetting;
import me.miki.shindo.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class CrosshairMod extends Mod {

    public static final LayoutManager layoutManager = new LayoutManager();

    private final ColorSetting colorSetting = new ColorSetting(TranslateText.COLOR, this, Color.RED, false);
    private final BooleanSetting hideThirdPersonViewSetting = new BooleanSetting(TranslateText.HIDE_THIRD_PERSON_VIEW, this, false);
    private final CellGridSetting cellGridSetting = new CellGridSetting(TranslateText.DESIGN, this, layoutManager.getLayout(0));

    public CrosshairMod() {
        super(TranslateText.CROSSHAIR, TranslateText.CROSSHAIR_DESCRIPTION, ModCategory.RENDER);
    }

    @EventTarget
    public void onRender(EventRender2D event) {

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if ((hideThirdPersonViewSetting.isToggled() && mc.gameSettings.thirdPersonView != 0)) {
            event.setCancelled(true);
        }

        if ((!hideThirdPersonViewSetting.isToggled()) || (hideThirdPersonViewSetting.isToggled() && mc.gameSettings.thirdPersonView == 0)) {

            for (int row = 0; row < 11; row++) {
                for (int col = 0; col < 11; col++) {
                    if (cellGridSetting.getCells()[row][col] && isToggled()) {
                        RenderUtils.drawRect(
                                sr.getScaledWidth() / 2F - 5 + col,
                                sr.getScaledHeight() / 2F - 5 + row,
                                1, 1, colorSetting.getColor()
                        );
                    }
                }
            }
        }
    }

    @EventTarget
    public void onRender2D(EventRenderCrosshair event) {
        event.setCancelled(true);
    }
}
