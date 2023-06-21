package com.s3Ge1n;

import java.util.LinkedList;

import com.mojang.brigadier.CommandDispatcher;
import com.s3Ge1n.mixin.client.ClientConnectionInvoker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

public class ModClient implements ClientModInitializer {
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static ClientPlayNetworkHandler networkHandler;
    public static final Reach reachHack = new Reach();
    // public static int globalTimer = 0;
    public static final LinkedList<Packet<?>> packetQueue = new LinkedList<>();
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        // ClientTickEvents.END_CLIENT_TICK.register(Reach::tick); ??
        ClientTickEvents.END_CLIENT_TICK.register(ModClient::tickEnd);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerClientCommands(dispatcher));  // Client Commands
        // ClientCommandRegistrationCallback.EVENT.register(ModClient::registerCommands);  // Client Commands
    }

    public static void tickEnd(MinecraftClient client) {
        // Update variables
        networkHandler = client.getNetworkHandler();
        // globalTimer++;

        // Send packets from queue (max 5)
        int movementPacketsLeft = 5;
        while (packetQueue.size() > 0 && movementPacketsLeft > 0) {
            Packet<?> packet = packetQueue.remove(0);
            if (packet instanceof PlayerMoveC2SPacket || packet instanceof VehicleMoveC2SPacket) {
                movementPacketsLeft--;
            }
            ((ClientConnectionInvoker) networkHandler.getConnection())._sendImmediately(packet, null);
        }
    }

    public static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Register commands here
        Mod.LOGGER.info("Registering client commands");
        PortCommand.register(dispatcher);
    }
}