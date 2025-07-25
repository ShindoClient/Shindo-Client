package me.miki.shindo.management.addons.rpo;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.addons.rpo.repository.ResourcePackRepositoryCustom;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.List;

public class RPOConfig {
    private static ConfigHandler config;

    public static void init() {
        config = new ConfigHandler(new File(Shindo.getInstance().getFileManager().getAddonsDir(), "rpo.json"));
        List<String> enabled = config.options.getEnabledPacks();

        ResourcePackRepositoryCustom.overrideRepository(enabled);

        Minecraft.getMinecraft().gameSettings.resourcePacks.clear();
        Minecraft.getMinecraft().gameSettings.resourcePacks.addAll(enabled);
        Minecraft.getMinecraft().gameSettings.saveOptions();
        Minecraft.getMinecraft().refreshResources();
    }

    public static ConfigHandler get() {
        return config;
    }
}
