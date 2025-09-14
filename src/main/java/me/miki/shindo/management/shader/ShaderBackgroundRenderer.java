package me.miki.shindo.management.shader;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.nanovg.NanoVGManager;
import me.miki.shindo.utils.ColorUtils;

import java.awt.*;
import java.io.File;


public class ShaderBackgroundRenderer {

    private static int clampColor(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static float clampFloat(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    public static void renderShaderBackground(NanoVGManager nvg, File shaderFile, float x, float y, float width, float height) {
        // Try to load and render the actual shader first
        Shindo instance = Shindo.getInstance();
        if (instance.getShaderManager() != null) {
            // First try to load from resources (texture pack support)
            int shaderId = instance.getShaderManager().loadShader(new net.minecraft.util.ResourceLocation("shindo/shaders/menu.fsh"));

            // If resource loading fails, try from file
            if (shaderId == -1 && shaderFile != null && shaderFile.exists()) {
                shaderId = instance.getShaderManager().loadShader(shaderFile);
            }

            if (shaderId != -1) {
                // Save current NanoVG state and OpenGL state
                nvg.save();

                // Push OpenGL attributes to preserve state
                org.lwjgl.opengl.GL11.glPushAttrib(org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS);
                org.lwjgl.opengl.GL11.glPushMatrix();

                // Render the actual shader with proper state isolation
                instance.getShaderManager().renderShader(shaderId, x, y, width, height);

                // Restore OpenGL state
                org.lwjgl.opengl.GL11.glPopMatrix();
                org.lwjgl.opengl.GL11.glPopAttrib();

                // Restore NanoVG state
                nvg.restore();
                return;
            }
        }

        // Fallback to animated gradients if shader loading fails
        renderFallbackBackground(nvg, shaderFile, x, y, width, height);
    }

    private static void renderFallbackBackground(NanoVGManager nvg, File shaderFile, float x, float y, float width, float height) {
        // For a seamless experience, create different gradient effects based on shader file content
        // This is a fallback until full shader support is implemented

        String shaderName = shaderFile.getName().toLowerCase();
        float time = (System.currentTimeMillis() % 10000) / 1000.0f;

        if (shaderName.contains("rainbow") || shaderName.contains("colorful")) {
            // Rainbow effect
            Color color1 = ColorUtils.getRainbow((int) (time * 100), 10, 255);
            Color color2 = ColorUtils.getRainbow((int) (time * 100 + 180), 10, 255);
            nvg.drawGradientRoundedRect(x, y, width, height, 5.0f, color1, color2);

        } else if (shaderName.contains("wave") || shaderName.contains("ocean")) {
            // Ocean wave effect
            int blue1 = Math.max(0, Math.min(255, (int) (128 + 50 * Math.sin(time))));
            int green1 = Math.max(0, Math.min(255, (int) (64 + 30 * Math.cos(time))));
            Color color1 = new Color(0, blue1, 255);
            Color color2 = new Color(0, green1, 200);
            nvg.drawGradientRoundedRect(x, y, width, height, 5.0f, color1, color2);

        } else if (shaderName.contains("fire") || shaderName.contains("flame")) {
            // Fire effect
            int green = Math.max(0, Math.min(255, (int) (100 + 50 * Math.sin(time * 2))));
            Color color1 = new Color(255, green, 0);
            Color color2 = new Color(200, 50, 0);
            nvg.drawGradientRoundedRect(x, y, width, height, 5.0f, color1, color2);

        } else {
            // Default animated gradient (beautiful and smooth)
            float r1 = Math.max(0.0f, Math.min(1.0f, (float) (0.5 + 0.3 * Math.sin(time * 0.5))));
            float g1 = Math.max(0.0f, Math.min(1.0f, (float) (0.5 + 0.3 * Math.sin(time * 0.7 + 2))));
            float b1 = Math.max(0.0f, Math.min(1.0f, (float) (0.5 + 0.3 * Math.sin(time * 0.9 + 4))));

            float r2 = Math.max(0.0f, Math.min(1.0f, (float) (0.3 + 0.4 * Math.cos(time * 0.6 + 1))));
            float g2 = Math.max(0.0f, Math.min(1.0f, (float) (0.3 + 0.4 * Math.cos(time * 0.8 + 3))));
            float b2 = Math.max(0.0f, Math.min(1.0f, (float) (0.3 + 0.4 * Math.cos(time * 1.0 + 5))));

            Color color1 = new Color(r1, g1, b1);
            Color color2 = new Color(r2, g2, b2);
            nvg.drawGradientRoundedRect(x, y, width, height, 5.0f, color1, color2);
        }
    }

    public static void renderShaderPreview(NanoVGManager nvg, File shaderFile, float x, float y, float width, float height) {
        // For preview, always use the fallback gradient to avoid performance issues
        // The fallback gradients are already rounded, so no clipping needed!

        // Render the fallback background (which is already using rounded gradients)
        renderFallbackBackground(nvg, shaderFile, x, y, width, height);

        // Add a subtle overlay border to indicate it's a shader and give it a nice rounded look
        nvg.drawRoundedRect(x, y, width, height, 6, new Color(255, 255, 255, 30));

        // Add a subtle border to define the rounded edges clearly
        nvg.drawOutlineRoundedRect(x, y, width, height, 6, 1, new Color(255, 255, 255, 60));
    }
}