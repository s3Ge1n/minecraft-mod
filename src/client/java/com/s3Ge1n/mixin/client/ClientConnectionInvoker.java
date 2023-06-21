package com.s3Ge1n.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;

@Mixin(ClientConnection.class)
public interface ClientConnectionInvoker {
    @Invoker("sendImmediately")
    void _sendImmediately(Packet<?> packet, PacketCallbacks callbacks);
}
