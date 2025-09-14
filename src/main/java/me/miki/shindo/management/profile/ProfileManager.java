package me.miki.shindo.management.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.miki.shindo.Shindo;
import me.miki.shindo.logger.ShindoLogger;
import me.miki.shindo.management.color.ColorManager;
import me.miki.shindo.management.color.Theme;
import me.miki.shindo.management.file.FileManager;
import me.miki.shindo.management.language.Language;
import me.miki.shindo.management.mods.HUDMod;
import me.miki.shindo.management.mods.Mod;
import me.miki.shindo.management.mods.ModManager;
import me.miki.shindo.management.mods.settings.Setting;
import me.miki.shindo.management.mods.settings.impl.*;
import me.miki.shindo.management.profile.mainmenu.BackgroundManager;
import me.miki.shindo.utils.ColorUtils;
import me.miki.shindo.utils.JsonUtils;
import me.miki.shindo.utils.file.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class ProfileManager {

    private final CopyOnWriteArrayList<Profile> profiles = new CopyOnWriteArrayList<Profile>();
    private final BackgroundManager backgroundManager;

    public ProfileManager() {

        backgroundManager = new BackgroundManager();

        this.loadProfiles(true);
    }

    public void loadProfiles(boolean loadDefaultProfile) {

        try {
            File profileDir = Shindo.getInstance().getFileManager().getProfileDir();
            int id = 0;

            profiles.clear();

            for (File f : Objects.requireNonNull(profileDir.listFiles())) {

                if (FileUtils.getExtension(f).equals("json")) {

                    if (f.getName().equals("Default.json")) {
                        if (loadDefaultProfile) {
                            load(f);
                        }
                    } else {
                        try (FileReader reader = new FileReader(f)) {

                            String serverIp = "";
                            ProfileIcon icon = ProfileIcon.GRASS;
                            ProfileType type = ProfileType.ALL;

                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                            JsonObject profileData = JsonUtils.getObjectProperty(jsonObject, "Profile Data");

                            serverIp = JsonUtils.getStringProperty(profileData, "Server", "");
                            icon = ProfileIcon.getIconById(JsonUtils.getIntProperty(profileData, "Icon", ProfileIcon.GRASS.getId()));
                            type = ProfileType.getTypeById(JsonUtils.getIntProperty(profileData, "Type", ProfileType.ALL.getId()));

                            Profile p = new Profile(id, serverIp, f, icon);

                            p.setType(type);

                            profiles.add(p);

                            id++;
                        } catch (Exception e) {
                            ShindoLogger.error("Failed to load profile", e);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        profiles.add(new Profile(999, "", null, null));
    }

    public void save(File file, String serverIp, ProfileType type, ProfileIcon icon) {

        Shindo instance = Shindo.getInstance();
        ModManager modManager = instance.getModManager();
        ColorManager colorManager = instance.getColorManager();

        try (FileWriter writer = new FileWriter(file)) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            JsonObject jsonObject = new JsonObject();
            JsonObject appJsonObject = new JsonObject();
            JsonObject modJsonObject = new JsonObject();
            JsonObject profileData = new JsonObject();

            profileData.addProperty("Icon", icon.getId());
            profileData.addProperty("Type", type.getId());
            profileData.addProperty("Server", serverIp);

            jsonObject.add("Profile Data", profileData);

            appJsonObject.addProperty("Accent Color", colorManager.getCurrentColor().getName());
            appJsonObject.addProperty("Theme", colorManager.getTheme().getId());
            appJsonObject.addProperty("Background", backgroundManager.getCurrentBackground().getId());
            appJsonObject.addProperty("Language", instance.getLanguageManager().getCurrentLanguage().getId());

            jsonObject.add("Appearance", appJsonObject);

            for (Mod m : modManager.getMods()) {

                JsonObject mJsonObject = new JsonObject();

                mJsonObject.addProperty("Toggle", m.isToggled());

                if (m instanceof HUDMod) {

                    HUDMod hMod = (HUDMod) m;

                    mJsonObject.addProperty("Toggle", hMod.isToggled());
                    mJsonObject.addProperty("X", hMod.getX());
                    mJsonObject.addProperty("Y", hMod.getY());
                    mJsonObject.addProperty("Width", hMod.getWidth());
                    mJsonObject.addProperty("Height", hMod.getHeight());
                    mJsonObject.addProperty("Scale", hMod.getScale());
                }

                modJsonObject.add(m.getNameKey(), mJsonObject);

                if (modManager.getSettingsByMod(m) != null) {

                    JsonObject sJsonObject = new JsonObject();

                    for (Setting s : modManager.getSettingsByMod(m)) {

                        if (s instanceof ColorSetting) {

                            ColorSetting cSetting = (ColorSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), cSetting.getColor().getRGB());
                        }

                        if (s instanceof BooleanSetting) {

                            BooleanSetting bSetting = (BooleanSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), bSetting.isToggled());
                        }

                        if (s instanceof ComboSetting) {

                            ComboSetting cSetting = (ComboSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), cSetting.getOption().getNameKey());
                        }

                        if (s instanceof NumberSetting) {

                            NumberSetting nSetting = (NumberSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), nSetting.getValue());
                        }

                        if (s instanceof TextSetting) {

                            TextSetting tSetting = (TextSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), tSetting.getText());
                        }

                        if (s instanceof KeybindSetting) {

                            KeybindSetting kSetting = (KeybindSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), kSetting.getKeyCode());
                        }

                        if (s instanceof ImageSetting) {

                            ImageSetting iSetting = (ImageSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), iSetting.getImage() == null ? "null" : iSetting.getImage().getName());
                        }

                        if (s instanceof SoundSetting) {

                            SoundSetting sSetting = (SoundSetting) s;

                            sJsonObject.addProperty(s.getNameKey(), sSetting.getSound() == null ? "null" : sSetting.getSound().getName());
                        }

                        if (s instanceof CellGridSetting) {
                            CellGridSetting cgSetting = (CellGridSetting) s;

                            JsonArray outerArray = new JsonArray();

                            boolean[][] cells = cgSetting.getCells();

                            for (boolean[] row : cells) {
                                JsonArray innerArray = new JsonArray();
                                for (boolean cell : row) {
                                    innerArray.add(cell);
                                }
                                outerArray.add(innerArray);
                            }


                            sJsonObject.add(s.getNameKey(), outerArray);
                        }
                    }

                    mJsonObject.add("Settings", sJsonObject);
                }
            }

            jsonObject.add("Mods", modJsonObject);

            gson.toJson(jsonObject, writer);

        } catch (Exception e) {
            ShindoLogger.error("Failed to save profile", e);
        }
    }

    public void save() {
        save(new File(Shindo.getInstance().getFileManager().getProfileDir(), "Default.json"), "", ProfileType.ALL, ProfileIcon.GRASS);
    }

    public void load(File file) {

        Shindo instance = Shindo.getInstance();
        ModManager modManager = instance.getModManager();
        ColorManager colorManager = instance.getColorManager();
        FileManager fileManager = instance.getFileManager();

        if (file == null) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonObject appJsonObject = JsonUtils.getObjectProperty(jsonObject, "Appearance");
            JsonObject modJsonObject = JsonUtils.getObjectProperty(jsonObject, "Mods");

            colorManager.setCurrentColor(colorManager.getColorByName(JsonUtils.getStringProperty(appJsonObject, "Accent Color", "Teal Love")));
            colorManager.setTheme(Theme.getThemeById(JsonUtils.getIntProperty(appJsonObject, "Theme", Theme.LIGHT.getId())));
            backgroundManager.setCurrentBackground(backgroundManager.getBackgroundById(JsonUtils.getIntProperty(appJsonObject, "Background", 0)));
            instance.getLanguageManager().setCurrentLanguage(Language.getLanguageById(JsonUtils.getStringProperty(appJsonObject, "Language", Language.ENGLISH.getId())));

            for (Mod m : modManager.getMods()) {

                JsonObject mJsonObject = JsonUtils.getObjectProperty(modJsonObject, m.getNameKey());

                if (mJsonObject != null) {

                    m.setToggled(JsonUtils.getBooleanProperty(mJsonObject, "Toggle", false));

                    if (m instanceof HUDMod) {

                        HUDMod hMod = (HUDMod) m;

                        hMod.setX(JsonUtils.getIntProperty(mJsonObject, "X", 100));
                        hMod.setY(JsonUtils.getIntProperty(mJsonObject, "Y", 100));
                        hMod.setWidth(JsonUtils.getIntProperty(mJsonObject, "Width", 100));
                        hMod.setHeight(JsonUtils.getIntProperty(mJsonObject, "Height", 100));
                        hMod.setScale(JsonUtils.getFloatProperty(mJsonObject, "Scale", 1));
                    }

                    if (modManager.getSettingsByMod(m) != null) {

                        JsonObject sJsonObject = JsonUtils.getObjectProperty(mJsonObject, "Settings");

                        if (sJsonObject != null) {
                            for (Setting s : modManager.getSettingsByMod(m)) {

                                if (s instanceof ColorSetting) {

                                    ColorSetting cSetting = (ColorSetting) s;

                                    cSetting.setColor(ColorUtils.getColorByInt(JsonUtils.getIntProperty(sJsonObject, s.getNameKey(), Color.RED.getRGB())));
                                }

                                if (s instanceof BooleanSetting) {

                                    BooleanSetting bSetting = (BooleanSetting) s;

                                    bSetting.setToggled(JsonUtils.getBooleanProperty(sJsonObject, s.getNameKey(), false));
                                }

                                if (s instanceof ComboSetting) {

                                    ComboSetting cSetting = (ComboSetting) s;

                                    cSetting.setOption(cSetting.getOptionByNameKey(JsonUtils.getStringProperty(sJsonObject, s.getNameKey(), cSetting.getDefaultOption().getNameKey())));
                                }

                                if (s instanceof NumberSetting) {

                                    NumberSetting nSetting = (NumberSetting) s;

                                    nSetting.setValue(JsonUtils.getDoubleProperty(sJsonObject, s.getNameKey(), nSetting.getDefaultValue()));
                                }

                                if (s instanceof TextSetting) {

                                    TextSetting tSetting = (TextSetting) s;

                                    tSetting.setText(JsonUtils.getStringProperty(sJsonObject, s.getNameKey(), tSetting.getDefaultText()));
                                }

                                if (s instanceof KeybindSetting) {

                                    KeybindSetting kSetting = (KeybindSetting) s;

                                    kSetting.setKeyCode(JsonUtils.getIntProperty(sJsonObject, s.getNameKey(), kSetting.getDefaultKeyCode()));
                                }

                                if (s instanceof ImageSetting) {

                                    ImageSetting iSetting = (ImageSetting) s;

                                    File cacheDir = new File(fileManager.getCacheDir(), "custom-image");

                                    if (cacheDir.exists()) {

                                        File image = new File(cacheDir, JsonUtils.getStringProperty(sJsonObject, s.getNameKey(), null));

                                        if (image != null && image.exists()) {
                                            iSetting.setImage(image);
                                        }
                                    }
                                }

                                if (s instanceof SoundSetting) {

                                    SoundSetting sSetting = (SoundSetting) s;

                                    File cacheDir = new File(fileManager.getCacheDir(), "custom-sound");

                                    if (cacheDir.exists()) {

                                        File image = new File(cacheDir, JsonUtils.getStringProperty(sJsonObject, s.getNameKey(), null));

                                        if (image != null && image.exists()) {
                                            sSetting.setSound(image);
                                        }
                                    }
                                }

                                if (s instanceof CellGridSetting) {
                                    CellGridSetting cgSetting = (CellGridSetting) s;

                                    JsonArray outerArray = sJsonObject.getAsJsonArray(s.getNameKey());
                                    boolean[][] cells = new boolean[outerArray.size()][];

                                    for (int i = 0; i < outerArray.size(); i++) {
                                        JsonArray innerArray = outerArray.get(i).getAsJsonArray();
                                        cells[i] = new boolean[innerArray.size()];

                                        for (int j = 0; j < innerArray.size(); j++) {
                                            cells[i][j] = innerArray.get(j).getAsBoolean();
                                        }
                                    }
                                    cgSetting.setCells(cells);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ShindoLogger.error("Failed to load profile", e);
        }
    }

    public void delete(Profile profile) {
        profiles.remove(profile);
        profile.getJsonFile().delete();
    }

}
