package com.sobersquid.ridelock;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RideLockHandler {
    private double lastX, lastY, lastZ;
    private float lastYaw;
    private boolean wasRiding = false;
    private boolean isEnabled = true;
    private long lastTime = System.currentTimeMillis();

    private float smoothedYawDelta = 0;
    private float smoothedPitch = 0;

    private static final float LERP_SENSITIVITY = 10.0f;
    private static final float PITCH_STRENGTH = 0.4f;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
    	Minecraft mc = Minecraft.getMinecraft();
        // Keybind enable/disable logic
        while (RideLock.toggleKey.isPressed()) {
            isEnabled = !isEnabled;
        	String keystatus = isEnabled ? "§aEnabled" : "§cDisabled";
            String keytext = "§7Ride Lock: " + keystatus;
            net.minecraft.util.text.ITextComponent message = new net.minecraft.util.text.TextComponentString(keytext);
            mc.player.sendStatusMessage(message, true);
        }
        // Safety Check to stop if player doesn't yet exist
        if (mc.player == null || mc.isGamePaused()) return;
        
        if (!isEnabled) {
            wasRiding = false;
            return;
        }

        Entity vehicle = mc.player.getRidingEntity();
        if (vehicle == null) {
            wasRiding = false;
            return;
        }
        // Tracking ride vehicle in space
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastTime) / 1000.0f;
        lastTime = currentTime;

        float partialTicks = (float) event.getRenderPartialTicks();
        double curX = vehicle.lastTickPosX + (vehicle.posX - vehicle.lastTickPosX) * partialTicks;
        double curY = vehicle.lastTickPosY + (vehicle.posY - vehicle.lastTickPosY) * partialTicks;
        double curZ = vehicle.lastTickPosZ + (vehicle.posZ - vehicle.lastTickPosZ) * partialTicks;

        double dx = curX - lastX;
        double dy = curY - lastY;
        double dz = curZ - lastZ;
        double horizontalDistSq = dx * dx + dz * dz;
        // determine camera to match vehicle movement
        float currentYaw = lastYaw;
        float currentPitch = 0;

        if (horizontalDistSq > 0.000001) {
            currentYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            currentPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(horizontalDistSq)));
        }

        if (!wasRiding) {
            lastX = curX; lastY = curY; lastZ = curZ;
            lastYaw = currentYaw;
            wasRiding = true;
            return;
        }
        // Smooth the view out to mitigate jitters
        float lerpFactor = MathHelper.clamp(deltaTime * LERP_SENSITIVITY, 0.0f, 1.0f);
        float yawDelta = MathHelper.wrapDegrees(currentYaw - lastYaw);
        // Final Camera Movement
        smoothedYawDelta = smoothedYawDelta + (yawDelta - smoothedYawDelta) * lerpFactor;
        smoothedPitch = smoothedPitch + (currentPitch - smoothedPitch) * lerpFactor;
        
        if (Math.abs(smoothedYawDelta) < 15.0f && Math.abs(smoothedYawDelta) > 0.001f) {
            mc.player.rotationYaw = MathHelper.wrapDegrees(mc.player.rotationYaw + smoothedYawDelta);
            mc.player.prevRotationYaw = mc.player.rotationYaw - smoothedYawDelta;
        }

        event.setPitch(event.getPitch() + (smoothedPitch * PITCH_STRENGTH));

        lastX = curX; lastY = curY; lastZ = curZ;
        lastYaw = currentYaw;
    }
}