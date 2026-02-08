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
            if (client.player != null) {
                // Forçamos o alcance no seu cliente
                var entityAttr = client.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
                if (entityAttr != null) {
                    entityAttr.setBaseValue(ReachMod.config.entityReach);
                }

                var blockAttr = client.player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
                if (blockAttr != null) {
                    blockAttr.setBaseValue(ReachMod.config.blockReach);
                }
            }

            while (configKey.wasPressed()) {
                client.setScreen(ModMenuIntegration.createConfigScreen(client.currentScreen));
            }
        });
    }
}
