package me.miki.shindo.ui.elements.impl;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.management.nanovg.font.Fonts;
import me.miki.shindo.ui.elements.Element;
import me.miki.shindo.utils.MathUtils;
import me.miki.shindo.utils.mouse.MouseUtils;

import java.awt.*;

public class SliderElement extends Element {
    private float value, min, max;
    private boolean dragging = false;
    private String label;

    public SliderElement(int x, int y, int width, int height, float value, float min, float max, String label) {
        super(x, y, width, height);
        this.value = value;
        this.min = min;
        this.max = max;
        this.label = label;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        NanoVGManager nvg = Shindo.getInstance().getNanoVGManager();
        float percent = (value - min) / (max - min);
        float knobX = x + percent * width;

        nvg.setupAndDraw(() -> {
            nvg.drawRoundedRect(x, y, width, height, 4, new Color(230, 230, 230, 80));
            nvg.drawRoundedRect(x, y, knobX - x, height, 4, new Color(230, 230, 230, 120));
            nvg.drawText(label + ": " + String.format("%.1f", value),x + width + 5, y + 3, Color.WHITE, 9.5F, Fonts.REGULAR);
        });

        if (dragging) {
            float newPercent = MathUtils.clamp((mouseX - x) / (float) width, 0f, 1f);
            value = min + (max - min) * newPercent;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height)) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        dragging = false;
    }

    public float getValue() {
        return value;
    }
}
