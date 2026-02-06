package com.sobersquid.ridelock;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "ridelock", name = "Ride Lock", version = "1.0.1")
public class RideLock {
    public static KeyBinding toggleKey;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        toggleKey = new KeyBinding("key.ridelock.toggle", Keyboard.KEY_F9, "Ride Lock");
        ClientRegistry.registerKeyBinding(toggleKey);
        MinecraftForge.EVENT_BUS.register(new RideLockHandler());
    }
}