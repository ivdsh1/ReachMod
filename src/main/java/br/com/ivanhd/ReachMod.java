package br.com.ivanhd;

import br.com.ivanhd.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

public class ReachMod implements ModInitializer {
    public static ModConfig config;

    @Override
    public void onInitialize() {
        config = ModConfig.load();

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof PlayerEntity player) {
                updateReach(player);
            }
        });
    }

    public static void updateReach(PlayerEntity player) {
        if (player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE) != null) {
            player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE).setBaseValue(config.entityReach);
        }
        if (player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE) != null) {
            player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE).setBaseValue(config.blockReach);
        }
    }
}
