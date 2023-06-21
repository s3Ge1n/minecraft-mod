package com.s3Ge1n.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(PlayerEntity.class)
public class FlyMixin {
    
    int toggle = 0;
    int MAX_SPEED = 3;
    double FALL_SPEED = -0.04;
    double acceleration = 0.1;
    double slow_acceleration = 0.09;

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player!=null) {
            boolean jumpPressed = client.options.jumpKey.isPressed();
            boolean forwardPressed = client.options.forwardKey.isPressed();
            boolean leftPressed = client.options.leftKey.isPressed();
            boolean rightPressed = client.options.rightKey.isPressed();
            boolean backPressed = client.options.backKey.isPressed();
            boolean fly = client.options.pickItemKey.isPressed();
            Boolean sneakPressed = client.options.sneakKey.isPressed();

            Entity object = client.player;
            if (client.player.hasVehicle()) {
                object = client.player.getVehicle();
            }

            Vec3d velocity = object.getVelocity();
            Vec3d newVelocity = new Vec3d(velocity.x, -FALL_SPEED, velocity.z);

            if (jumpPressed && fly) {

                if (forwardPressed && !sneakPressed) {
                    newVelocity = client.player.getRotationVector().multiply(acceleration);
                } else if (forwardPressed && sneakPressed) {
                    newVelocity = client.player.getRotationVector().multiply(slow_acceleration);
                }

                if (leftPressed && !client.player.hasVehicle() && !sneakPressed) {
                    newVelocity = client.player.getRotationVector().multiply(acceleration).rotateY(3.1415927F/2);
                    newVelocity = new Vec3d(newVelocity.x, 0, newVelocity.z);
                } else if (leftPressed && !client.player.hasVehicle() && sneakPressed) {
                    newVelocity = client.player.getRotationVector().multiply(slow_acceleration).rotateY(3.1415927F/2);
                    newVelocity = new Vec3d(newVelocity.x, 0, newVelocity.z);
                }

                if (rightPressed && !client.player.hasVehicle() && !sneakPressed) {
                    newVelocity = client.player.getRotationVector().multiply(acceleration).rotateY(-3.1415927F/2);
                    newVelocity = new Vec3d(newVelocity.x, 0, newVelocity.z);
                } else if (rightPressed && !client.player.hasVehicle() && sneakPressed) {
                    newVelocity = client.player.getRotationVector().multiply(slow_acceleration).rotateY(-3.1415927F/2);
                    newVelocity = new Vec3d(newVelocity.x, 0, newVelocity.z);
                }

                if (backPressed && !sneakPressed) {
                    newVelocity = client.player.getRotationVector().negate().multiply(acceleration);
                } else if (backPressed && sneakPressed) {
                    newVelocity = client.player.getRotationVector().negate().multiply(slow_acceleration);
                }

                newVelocity = new Vec3d(newVelocity.x, (toggle==0 && newVelocity.y>FALL_SPEED) ? FALL_SPEED : newVelocity.y, newVelocity.z);
                object.setVelocity(newVelocity);

                if (forwardPressed || leftPressed || rightPressed || backPressed) {
                    if (acceleration<MAX_SPEED) {
                        acceleration += 0.1;
                    }
                    else if (acceleration>0.2) {
                        acceleration -= 0.2;
                    }

                }

                if (toggle == 0 || newVelocity.y <= FALL_SPEED) {
                    toggle = 40;
                }
                toggle--;
            }
        }
    }
}
