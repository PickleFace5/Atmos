package com.pickleface5.atmos.events;

import com.pickleface5.atmos.Atmos;
import com.pickleface5.atmos.packets.AtmosPacketHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Atmos.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ForgeServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide()) AtmosPacketHandler.sendPacketToClient(Objects.requireNonNull(event.getPlayer().getServer()).getWorldData().worldGenSettings().seed());
    }
}
