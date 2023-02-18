package com.pickleface5.atmos.events;

import com.pickleface5.atmos.Atmos;
import com.pickleface5.atmos.client.CustomCloudRender;
import com.pickleface5.atmos.client.CustomSnowAndRainRender;
import com.pickleface5.atmos.client.CustomWeatherParticleRender;
import com.pickleface5.atmos.client.CustomSkyRender;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Atmos.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    public static long seed = 0;

    @SubscribeEvent
    public static void renderBetterSky(WorldEvent.Load event) {
        Atmos.LOGGER.debug("World Loaded, attempting to set SkyRenderHandler...");
        if (event.getWorld().isClientSide()) {
            ClientLevel level = (ClientLevel) event.getWorld();
            if (level.dimension().equals(ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation("minecraft", "overworld")))) {
                level.effects().setSkyRenderHandler(new CustomSkyRender(seed));
                Atmos.LOGGER.debug("CustomSkyRender set successfully.");
            } else {
                Atmos.LOGGER.debug("Dimension is not overworld, ignoring WorldEvent.Load");
            }
        } else { // Get seed if on server-side with this ungodly amount of methods
            if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
                seed = Objects.requireNonNull(Objects.requireNonNull(event.getWorld().getServer()).getLevel(ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation("minecraft", "overworld")))).getSeed();
            }
        }
        Atmos.LOGGER.debug(String.valueOf(seed));
    }

    @SubscribeEvent
    public static void renderBetterClouds(WorldEvent.Load event) {
        Atmos.LOGGER.debug("World Loaded, attempting to set CloudRenderHandler...");
        if (event.getWorld().isClientSide()) {
            ClientLevel level = (ClientLevel) event.getWorld();
            if (level.dimension().equals(ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation("minecraft", "overworld")))) {
                level.effects().setCloudRenderHandler(new CustomCloudRender());
                Atmos.LOGGER.debug("CustomCloudRender set successfully.");
            } else {
                Atmos.LOGGER.debug("Dimension is not overworld, ignoring WorldEvent.Load");
            }
        }
    }

    //@SubscribeEvent TODO: ADD WEATHER STUFF
    public static void addBetterWeatherParticleRender(WorldEvent.Load event) {
        Atmos.LOGGER.debug("World Loaded, attempting to set CustomWeatherParticleRender...");
        if (event.getWorld().isClientSide()) {
            ClientLevel level = (ClientLevel) event.getWorld();
            if (level.dimension().equals(ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation("minecraft", "overworld")))) {
                level.effects().setWeatherParticleRenderHandler(new CustomWeatherParticleRender());
                Atmos.LOGGER.debug("CustomWeatherParticleRender set successfully.");
            } else {
                Atmos.LOGGER.debug("Dimension is not overworld, ignoring WorldEvent.Load");
            }
        }
    }

    //@SubscribeEvent
    public static void addBetterSnowAndRainRender(WorldEvent.Load event) {
        Atmos.LOGGER.debug("World Loaded, attempting to set CustomSnowAndRainRender...");
        if (event.getWorld().isClientSide()) {
            ClientLevel level = (ClientLevel) event.getWorld();
            if (level.dimension().equals(ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation("minecraft", "overworld")))) {
                level.effects().setWeatherRenderHandler(new CustomSnowAndRainRender());
                Atmos.LOGGER.debug("CustomSnowAndRainRender set successfully.");
            } else {
                Atmos.LOGGER.debug("Dimension is not overworld, ignoring WorldEvent.Load");
            }
        }
    }
}
