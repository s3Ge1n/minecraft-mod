package com.s3Ge1n;

import com.mojang.brigadier.CommandDispatcher;
import com.s3Ge1n.mixin.client.ClientConnectionInvoker;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class PortCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        Mod.LOGGER.info("Registering client command /pt");
        dispatcher.register(literal("pt")
            .then(argument("distance", integer())
                .executes(ctx -> {
                    int distance = ctx.getArgument("distance", Integer.class);
                    PlayerEntity player = ctx.getSource().getPlayer();
                    ctx.getSource().sendFeedback(Text.literal("Porting " + distance + " blocks forwards"));

                    Vec3d target = player.getPos().add(player.getRotationVector().multiply(distance));
                    Packet<?> currentPosPacket = new PlayerMoveC2SPacket.PositionAndOnGround(player.getPos().x, player.getPos().y, player.getPos().z, player.isOnGround());
                    Packet<?> targetPosPacket = new PlayerMoveC2SPacket.PositionAndOnGround(target.x, target.y, target.z, player.isOnGround());
                    for (int i = 0; i < 30; i++) {
                        ((ClientConnectionInvoker) com.s3Ge1n.ModClient.networkHandler.getConnection())._sendImmediately(currentPosPacket, null);
                    }
                    // now send teleport target position
                    ((ClientConnectionInvoker) com.s3Ge1n.ModClient.networkHandler.getConnection())._sendImmediately(targetPosPacket, null);
                    player.setPosition(target);
                    return 1;
                })
            )
        );
        Mod.LOGGER.info("Registered client command /pt");
    }
}
