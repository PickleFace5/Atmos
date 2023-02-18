package com.pickleface5.atmos.packets;

import com.pickleface5.atmos.Atmos;
import com.pickleface5.atmos.config.ConfigHolder;
import com.pickleface5.atmos.config.ServerConfigHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

public class AtmosPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Atmos.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, ConfigSyncPacket.class, ConfigSyncPacket::encode, ConfigSyncPacket::decode, ConfigSyncPacket::handle);
    }

    public static void sendPacketToClient(long seed) {
        ServerConfigHandler serverConfig = ConfigHolder.SERVER;

        INSTANCE.send(PacketDistributor.ALL.noArg(), new ConfigSyncPacket(
                serverConfig.overrideClientSun.get(), getRGBAFromList(serverConfig.sunColor.get()), serverConfig.sunSize.get(), serverConfig.rainbowSun.get(),
                serverConfig.overrideClientMoon.get(), getRGBAFromList(serverConfig.moonColor.get()), serverConfig.moonSize.get(), serverConfig.rainbowMoon.get(),
                serverConfig.overrideClientStarAmount.get(), serverConfig.starAmount.get(), serverConfig.overrideClientStarConfig.get(),
                serverConfig.customStarGradient.get(), getRGBFromList(serverConfig.starGradient1.get()), getRGBFromList(serverConfig.starGradient2.get()), getRGBFromList(serverConfig.starGradient3.get()),
                serverConfig.overrideStarRGB.get(), getRGBFromList(serverConfig.starColor1.get()), getRGBFromList(serverConfig.starColor2.get()), getRGBFromList(serverConfig.starColor3.get()), seed)
        );
    }

    private static int[] getRGBFromList(List<? extends Integer> list) {
        return new int[] {list.get(0), list.get(1), list.get(2)};
    }

    private static int[] getRGBAFromList(List<? extends Integer> list) {
        return new int[] {list.get(0), list.get(1), list.get(2), list.get(3)};
    }
}
