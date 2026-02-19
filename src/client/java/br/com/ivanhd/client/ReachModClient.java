package br.com.ivanhd.client;

import br.com.ivanhd.ReachMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ReachModClient implements ClientModInitializer {
    public static KeyBinding configKey;
    public static final KeyBinding.Category REACH_CATEGORY =
            KeyBinding.Category.create(Identifier.of("reachmod", "general"));

    @Override
    public void onInitializeClient() {
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachmod.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_8,
                REACH_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && ReachMod.config.isEnabled()) {
                applySafeReach(client);
            }

            while (configKey.wasPressed()) {
                client.setScreen(ModMenuIntegration.createConfigScreen(client.currentScreen));
            }
        });
    }

    private void applySafeReach(MinecraftClient client) {
        var entityAttr = client.player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
        var blockAttr = client.player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);

        if (entityAttr != null) {
            double current = entityAttr.getBaseValue();
            // Se o reach for o padrão (3.0) ou o que o mod colocou, ele aplica.
            // Se for 64.0 (servidor), ele NÃO mexe.
            if (current == 3.0 || current == ReachMod.config.getEntityReach()) {
                entityAttr.setBaseValue(ReachMod.config.getEntityReach());
            }
        }

        if (blockAttr != null) {
            double current = blockAttr.getBaseValue();
            if (current == 4.5 || current == ReachMod.config.getBlockReach()) {
                blockAttr.setBaseValue(ReachMod.config.getBlockReach());
            }
        }
    }

    private void resetToVanilla(MinecraftClient client) {
        var entityAttr = client.player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
        var blockAttr = client.player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);

        // Se desligar o mod, volta para o padrão apenas se o mod estivesse controlando
        if (entityAttr != null && entityAttr.getBaseValue() == ReachMod.config.getEntityReach()) {
            entityAttr.setBaseValue(3.0);
        }
        if (blockAttr != null && blockAttr.getBaseValue() == ReachMod.config.getBlockReach()) {
            blockAttr.setBaseValue(4.5);
        }
    }
}
