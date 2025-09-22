package me.miki.shindo.hooks;

import me.miki.shindo.management.addons.patcher.PatcherAddon;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ContainerOpacityHook {

    public static void beginTransparency() {
        if (PatcherAddon.getInstance().isToggled()) {
            float containerOpacity = PatcherAddon.getInstance().getContainerOacitySetting().getValueFloat();
            if (containerOpacity == 1.0f) return;

            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.color(1, 1, 1, containerOpacity);
        }
    }

    public static void endTransparency() {
        if (PatcherAddon.getInstance().isToggled()) {
            if (PatcherAddon.getInstance().getContainerOacitySetting().getValueFloat() == 1.0f) return;

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.color(1, 1, 1, 1);
        }
    }
}