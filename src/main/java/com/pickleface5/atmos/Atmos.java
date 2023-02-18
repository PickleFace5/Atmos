package com.pickleface5.atmos;

import com.mojang.logging.LogUtils;
import com.pickleface5.atmos.config.ConfigHolder;
import com.pickleface5.atmos.packets.AtmosPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Atmos.MOD_ID)
public class Atmos {
    public static final String MOD_ID = "atmos";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Atmos() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC, "atmos-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC, "atmos-server.toml");

        AtmosPacketHandler.register();



        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) { }

    private void enqueueIMC(final InterModEnqueueEvent event) { }

    private void processIMC(final InterModProcessEvent event) { }
}
