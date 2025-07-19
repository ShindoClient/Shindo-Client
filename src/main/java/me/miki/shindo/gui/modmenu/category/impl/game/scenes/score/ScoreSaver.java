package me.miki.shindo.gui.modmenu.category.impl.game.scenes.score;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.miki.shindo.Shindo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ScoreSaver {
    private static final File file = new File(Shindo.getInstance().getFileManager().getGamesDir(), "scores.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveScore(String game, int score) {
        try {
            JsonObject root = file.exists()
                    ? gson.fromJson(new FileReader(file), JsonObject.class)
                    : new JsonObject();

            JsonArray array = root.has(game)
                    ? root.getAsJsonArray(game)
                    : new JsonArray();

            array.add(score);
            root.add(game, array);

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(root, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getScores(String game) {
        List<Integer> list = new ArrayList<>();
        try {
            if (!file.exists()) return list;
            JsonObject root = gson.fromJson(new FileReader(file), JsonObject.class);
            if (root.has(game)) {
                JsonArray array = root.getAsJsonArray(game);
                for (int i = 0; i < array.size(); i++) {
                    list.add(array.get(i).getAsInt());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}