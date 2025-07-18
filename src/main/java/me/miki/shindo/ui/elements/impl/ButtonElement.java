package me.miki.shindo.ui.elements.impl;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.ui.elements.Element;
import me.miki.shindo.utils.mouse.MouseUtils;
import java.lang.Runnable;

import java.awt.Color;

public class ButtonElement extends Element {
    private String label;
    private Runnable action;

    public ButtonElement(int x, int y, int width, int height, String label, Runnable action) {
        super(x, y, width, height);
        this.label = label;
        this.action = action;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        NanoVGManager nvg = Shindo.getInstance().getNanoVGManager();
        boolean hovered = MouseUtils.isInside(mouseX, mouseY, x, y, width, height);
        Color color = hovered ? new Color(230, 230, 230, 80) : new Color(230, 230, 230, 120);

        nvg.setupAndDraw(() -> {
            nvg.drawRoundedRect(x, y, width, height, 4, color);
            nvg.drawCenteredText(label, x + width / 2F, y + 6, Color.WHITE, 9.5F, Fonts.REGULAR);
        });
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height)) {
            action.run();
        }
    }
}
