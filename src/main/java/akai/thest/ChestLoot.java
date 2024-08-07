package akai.thest;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ChestLoot {
    private static final Gson GSON = new Gson();
    private static final String DEFAULT_KEY = "chest-test:default";
    private static final JsonArray EMPTY_ARRAY = new JsonArray();
    private static Map<String, JsonArray> map;

    public static void loadEntries() {
        ImmutableMap.Builder<String, JsonArray> builder = ImmutableMap.builder();
        BiConsumer<String, JsonArray> biConsumer = builder::put;

        try {
            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("chests_loot.json");

            if (Files.exists(configPath)) {
                BufferedReader reader = Files.newBufferedReader(configPath);
                JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);

                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String key = entry.getKey();
                    JsonArray loot_table = JsonHelper.asArray(entry.getValue(), key);
                    ChestTest.LOGGER.info("Registered Entry: {} Loot Table: {}", key, loot_table.toString());
                    biConsumer.accept(key, loot_table);
                }

                reader.close();
            }
        } catch (JsonParseException | IOException var8) {
            ChestTest.LOGGER.error("Couldn't read strings[] from {}", "/config/chests_loot.json", var8);
        }

        map = builder.build();
    }

    public static JsonArray get(String key) {
        return hasBiome(key) ? map.get(key) : map.getOrDefault(DEFAULT_KEY, EMPTY_ARRAY);
    }

    public static boolean hasBiome(String key) {
        return map.containsKey(key);
    }
}
