package br.com.ivanhd.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ModConfig {
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "reachmod.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final float MIN_REACH = 1.0f;
    public static final float MAX_REACH = 64.0f;

    public boolean enabled = true;
    public float entityReach = 3.0f;
    public float blockReach = 4.5f;

    private Map<String, Boolean> serverPermissions = new HashMap<>();

    public static ModConfig load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config.serverPermissions == null) config.serverPermissions = new HashMap<>();
                return config;
            } catch (Exception e) {
                return new ModConfig();
            }
        }
        return new ModConfig();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
