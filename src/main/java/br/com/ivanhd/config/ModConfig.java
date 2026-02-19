package br.com.ivanhd.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Getter @Setter
public class ModConfig {
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "reachmod.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final float MIN_REACH = 1.0f;
    public static final float MAX_REACH = 64.0f;

    private boolean enabled = true;
    private float entityReach = 3.0f;
    private float blockReach = 4.5f;

    public static ModConfig load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                return GSON.fromJson(reader, ModConfig.class);
            } catch (Exception e) { return new ModConfig(); }
        }
        return new ModConfig();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
