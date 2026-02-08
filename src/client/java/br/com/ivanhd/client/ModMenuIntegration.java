package br.com.ivanhd.client;

import br.com.ivanhd.ReachMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::createConfigScreen;
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.reachmod.config"))
                .setSavingRunnable(() -> ReachMod.config.save());

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.reachmod.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Configuração de Reach de Entidade (Item)
        general.addEntry(entryBuilder.startFloatField(Text.translatable("option.reachmod.entity_reach"), ReachMod.config.entityReach)
                .setDefaultValue(3.0f)
                .setSaveConsumer(newValue -> ReachMod.config.entityReach = newValue)
                .build());

        // Configuração de Reach de Bloco
        general.addEntry(entryBuilder.startFloatField(Text.translatable("option.reachmod.block_reach"), ReachMod.config.blockReach)
                .setDefaultValue(4.5f)
                .setSaveConsumer(newValue -> ReachMod.config.blockReach = newValue)
                .build());

        return builder.build();
    }
}
