package com.s3Ge1n;

import static com.s3Ge1n.ModClient.*;
import static com.s3Ge1n.Utils.insertToCenter;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Reach {
    public void message(String message) {
        if (client.player == null) return;
        message = "§7" + message.replace("§r", "§7");  // Make white text gray
        client.player.sendMessage(Text.of(message), true);
    }

    public void hitEntity(Entity target) {
        if (client.player == null) return;
        if (packetQueue.size() > 0) {
            return; // may take multiple ticks
        }
        
        Vec3d pos = client.player.getPos();
        Vec3d targetPos = target.getPos().subtract( // subtract a bit from the end
            target.getPos().subtract(pos).normalize().multiply(2)
        );
        // if player is still too far away move closer
        while (target.squaredDistanceTo(pos.add(0, client.player.getStandingEyeHeight(), 0)) >= MathHelper.square(6.0)) {
            Vec3d movement = targetPos.subtract(pos);

            boolean lastPacket = false;
            if (movement.lengthSquared() >= 100) { // length squared is max 100 otherwise moved too quickly
                // normalise to length 10
                movement = movement.normalize().multiply(9.9);
            } else { // if short enough this is the last packet
                lastPacket = true;
            }
            pos = pos.add(movement);

            // add forward and backward packets
            insertToCenter(packetQueue, new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, true));
            if (!lastPacket) { // if not last packet add a backwards packet (only need one at the entity)
                insertToCenter(packetQueue, new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, true));
            }
        }
        // add hit packet in the middle and origional position at the end
        insertToCenter(packetQueue, PlayerInteractEntityC2SPacket.attack(target, client.player.isSneaking()));
        packetQueue.add(new PlayerMoveC2SPacket.PositionAndOnGround(client.player.getX(), client.player.getY(), client.player.getZ(), true));
        packetQueue.add(new HandSwingC2SPacket(client.player.getActiveHand())); // serverside animation
        client.player.resetLastAttackedTicks(); // reset attack cooldown
    }
}
