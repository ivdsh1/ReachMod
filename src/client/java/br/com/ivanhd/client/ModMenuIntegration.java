package br.com.ivanhd.client;

import br.com.ivanhd.ReachMod;
import br.com.ivanhd.config.ModConfig;
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
                .setSavingRunnable(ReachMod.config::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.reachmod.main"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Limpar Servidores
        general.addEntry(entryBuilder.startTextDescription(Text.translatable("option.reachmod.reset_info")).build());
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.reachmod.reset_servers"), false)
                .setSaveConsumer(reset -> {
                    if (reset) {
                        ReachMod.config.getServerPermissions().clear();
                        ReachMod.config.save();
                    }
                }).build());

        general.addEntry(entryBuilder.startFloatField(Text.translatable("option.reachmod.entity_reach"), ReachMod.config.getEntityReach())
                .setDefaultValue(3.0f).setMin(1.0f).setMax(64.0f)
                .setSaveConsumer(ReachMod.config::setEntityReach).build());

        general.addEntry(entryBuilder.startFloatField(Text.translatable("option.reachmod.block_reach"), ReachMod.config.getBlockReach())
                .setDefaultValue(4.5f).setMin(1.0f).setMax(64.0f)
                .setSaveConsumer(ReachMod.config::setBlockReach).build());

        return builder.build();
    }
}