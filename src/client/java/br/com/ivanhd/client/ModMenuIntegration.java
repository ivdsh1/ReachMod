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

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.reachmod.main"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Ativar/Desativar Mod
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.reachmod.enabled"), ReachMod.config.isEnabled())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ReachMod.config.setEnabled(newValue))
                .build());

        // Entity Reach com limites (Botão de reset incluso no Cloth Config)
        general.addEntry(entryBuilder.startFloatField(Text.translatable("option.reachmod.entity_reach"), ReachMod.config.getEntityReach())
                .setDefaultValue(3.0f)
                .setMin(ModConfig.MIN_REACH) // Limite Mínimo
                .setMax(ModConfig.MAX_REACH) // Limite Máximo
                .setSaveConsumer(newValue -> ReachMod.config.setEntityReach(newValue))
                .build());

        // Block Reach com limites
        general.addEntry(entryBuilder.startFloatField(Text.translatable("option.reachmod.block_reach"), ReachMod.config.getBlockReach())
                .setDefaultValue(4.5f)
                .setMin(ModConfig.MIN_REACH)
                .setMax(ModConfig.MAX_REACH)
                .setSaveConsumer(newValue -> ReachMod.config.setBlockReach(newValue))
                .build());

        return builder.build();
    }
}