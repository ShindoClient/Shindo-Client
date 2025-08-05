package me.miki.shindo.management.mods.impl;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.event.EventTarget;
import me.miki.shindo.management.event.impl.EventKey;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.mods.Mod;
import me.miki.shindo.management.mods.ModCategory;
import me.miki.shindo.management.mods.settings.impl.*;
import me.miki.shindo.management.mods.settings.impl.combo.Option;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

public class InternalSettingsMod extends Mod {

    private static InternalSettingsMod instance;

    private final ComboSetting modThemeSetting = new ComboSetting(TranslateText.HUD_THEME, this, TranslateText.NORMAL, new ArrayList<Option>(Arrays.asList(
            new Option(TranslateText.NORMAL), new Option(TranslateText.GLOW), new Option(TranslateText.OUTLINE), new Option(TranslateText.VANILLA),
            new Option(TranslateText.OUTLINE_GLOW), new Option(TranslateText.VANILLA_GLOW), new Option(TranslateText.SHADOW),
            new Option(TranslateText.DARK), new Option(TranslateText.LIGHT), new Option(TranslateText.RECT), new Option(TranslateText.MODERN),
            new Option(TranslateText.TEXT), new Option(TranslateText.GRADIENT_SIMPLE))));

    private final BooleanSetting blurSetting = new BooleanSetting(TranslateText.UI_BLUR, this, true);

    private final BooleanSetting mcFontSetting = new BooleanSetting(TranslateText.MC_FONT, this, false);

    private final NumberSetting volumeSetting = new NumberSetting(TranslateText.VOLUME, this, 0.8, 0, 1, false);

    private final KeybindSetting modMenuKeybindSetting = new KeybindSetting(TranslateText.KEYBIND, this, Keyboard.KEY_RSHIFT);

    private final TextSetting capeNameSetting = new TextSetting(TranslateText.CUSTOM_CAPE, this, "None");

    private final BooleanSetting clickEffectsSetting = new BooleanSetting(TranslateText.CLICK_EFFECT, this, true);

    private final BooleanSetting soundsUISetting = new BooleanSetting(TranslateText.UI_SOUNDS, this, true);

    public InternalSettingsMod() {
        super(TranslateText.NONE, TranslateText.NONE, ModCategory.OTHER);

        instance = this;
    }

    public static InternalSettingsMod getInstance() {
        return instance;
    }

    @Override
    public void setup() {
        this.setHide(true);
        this.setToggled(true);
    }

    @EventTarget
    public void onKey(EventKey event) {
        if (event.getKeyCode() == modMenuKeybindSetting.getKeyCode()) {
            mc.displayGuiScreen(Shindo.getInstance().getShindoAPI().getModMenu());
        }

//		Uncomment to enable the ability to change the theme of the mod menu using the down arrow key
        if (event.getKeyCode() == Keyboard.KEY_DOWN) {
            int max = modThemeSetting.getOptions().size();
            int modeIndex = modThemeSetting.getOptions().indexOf(modThemeSetting.getOption());

            if (modeIndex > 0) {
                modeIndex--;
            } else {
                modeIndex = max - 1;
            }

            //mcFontSetting.setToggled(!mcFontSetting.isToggled());

            modThemeSetting.setOption(modThemeSetting.getOptions().get(modeIndex));

        }
    }

    public BooleanSetting getClickEffectsSetting() {
        return clickEffectsSetting;
    }

    public BooleanSetting getSoundsUISetting() {
        return soundsUISetting;
    }

    public NumberSetting getVolumeSetting() {
        return volumeSetting;
    }

    public ComboSetting getModThemeSetting() {
        return modThemeSetting;
    }

    public BooleanSetting getBlurSetting() {
        return blurSetting;
    }

    public KeybindSetting getModMenuKeybindSetting() {
        return modMenuKeybindSetting;
    }

    public BooleanSetting getMCHUDFont() {
        return mcFontSetting;
    }

    public String getCapeConfigName() {
        return capeNameSetting.getText();
    }

    public void setCapeConfigName(String a) {
        capeNameSetting.setText(a);
    }
}
