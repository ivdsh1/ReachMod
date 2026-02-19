package br.com.ivanhd.client;

import br.com.ivanhd.ReachMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ReachModClient implements ClientModInitializer {
    public static KeyBinding configKey;
    public static final KeyBinding.Category REACH_CATEGORY =
            KeyBinding.Category.create(Identifier.of("reachmod", "general"));
    private static String currentServerIp = "";
    private static boolean pendingConfirmation = false;

    @Override
    public void onInitializeClient() {
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachmod.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_8,
                REACH_CATEGORY
        ));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isInSingleplayer() && client.getCurrentServerEntry() != null) {
                currentServerIp = client.getCurrentServerEntry().address;

                // Se o IP não estiver na lista, marca para perguntar
                if (!ReachMod.config.getServerPermissions().containsKey(currentServerIp)) {
                    pendingConfirmation = true;
                } else if (Boolean.TRUE.equals(ReachMod.config.getServerPermissions().get(currentServerIp))) {
                    sendAuthorizedMessage(client);
                }
            } else {
                currentServerIp = "singleplayer";
                pendingConfirmation = false;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Se houver uma confirmação pendente e o jogador já estiver no mundo (sem telas de loading)
            if (pendingConfirmation && client.player != null && client.currentScreen == null) {
                pendingConfirmation = false;
                client.setScreen(new ConfirmScreen(
                        (authorized) -> handleChoice(authorized, client),
                        Text.translatable("reachmod.confirm.title"),
                        Text.translatable("reachmod.confirm.message", currentServerIp)
                ));
            }

            // Aplica o Reach se estiver habilitado
            if (client.player != null && ReachMod.config.isEnabled()) {
                boolean isAllowed = client.isInSingleplayer() ||
                        Boolean.TRUE.equals(ReachMod.config.getServerPermissions().get(currentServerIp));

                if (isAllowed) {
                    applySafeReach(client);
                }
            }

            while (configKey.wasPressed()) {
                client.setScreen(ModMenuIntegration.createConfigScreen(client.currentScreen));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && ReachMod.config.isEnabled()) {
                applySafeReach(client);
            }

            while (configKey.wasPressed()) {
                client.setScreen(ModMenuIntegration.createConfigScreen(client.currentScreen));
            }
        });
    }

    private void handleChoice(boolean authorized, MinecraftClient client) {
        ReachMod.config.getServerPermissions().put(currentServerIp, authorized);
        ReachMod.config.save();

        if (authorized) {
            sendAuthorizedMessage(client);
        }
        client.setScreen(null);
    }

    private void sendAuthorizedMessage(MinecraftClient client) {
        if (client.player != null) {
            client.player.sendMessage(Text.translatable("reachmod.msg.authorized")
                    .formatted(Formatting.GREEN), false);
        }
    }

    private void onTick(MinecraftClient client) {
        if (client.player != null && ReachMod.config.isEnabled()) {
            boolean isAllowed = client.isInSingleplayer() ||
                    Boolean.TRUE.equals(ReachMod.config.getServerPermissions().get(currentServerIp));

            if (isAllowed) {
                applySafeReach(client);
            }
        }

        while (configKey.wasPressed()) {
            client.setScreen(ModMenuIntegration.createConfigScreen(client.currentScreen));
        }
    }

    private void applySafeReach(MinecraftClient client) {
        var entityAttr = client.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        var blockAttr = client.player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);

        // Lógica de não conflito (Server Reach 64)
        if (entityAttr != null) {
            double current = entityAttr.getBaseValue();
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
        var entityAttr = client.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        var blockAttr = client.player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);

        // Se desligar o mod, volta para o padrão apenas se o mod estivesse controlando
        if (entityAttr != null && entityAttr.getBaseValue() == ReachMod.config.getEntityReach()) {
            entityAttr.setBaseValue(3.0);
        }
        if (blockAttr != null && blockAttr.getBaseValue() == ReachMod.config.getBlockReach()) {
            blockAttr.setBaseValue(4.5);
        }
    }
}
