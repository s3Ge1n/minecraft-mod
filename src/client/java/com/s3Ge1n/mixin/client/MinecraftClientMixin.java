package com.s3Ge1n.mixin.client;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    private void updateCrosshairTarget() {
        MinecraftClient client = MinecraftClient.getInstance();
        Optional<Entity> entity = DebugRenderer.getTargetedEntity(client.player, 100);
        entity.ifPresent(e -> client.crosshairTarget = new EntityHitResult(e));
    }

    // extend reach for attack
    @Inject(method = "doAttack", at = @At(value = "HEAD"))
    private void doAttack(CallbackInfoReturnable<Boolean> cir) {
        updateCrosshairTarget();
    }
}
