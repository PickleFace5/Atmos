package com.pickleface5.atmos.packets;

import com.pickleface5.atmos.client.CustomSkyRender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ConfigSyncPacket {
    private static final Logger LOGGER = LogManager.getLogger(ConfigSyncPacket.class);

    boolean overrideClientSun;
    int[] sunColor;
    int sunSize;
    boolean rainbowSun;
    boolean overrideClientMoon;
    int[] moonColor;
    int moonSize;
    boolean rainbowMoon;
    boolean overrideClientStarAmount;
    int starAmount;
    boolean overrideClientStarConfig;
    boolean customStarGradient;
    int[] starGradient1;
    int[] starGradient2;
    int[] starGradient3;
    boolean customStarRGB;
    int[] starRGB1;
    int[] starRGB2;
    int[] starRGB3;
    long seed;

    public ConfigSyncPacket(boolean overrideClientSun, int[] sunColor, int sunSize, boolean rainbowSun,
                            boolean overrideClientMoon, int[] moonColor, int moonSize, boolean rainbowMoon,
                            boolean overrideClientStarAmount, int starAmount, boolean overrideClientStarConfig,
                            boolean customStarGradient, int[] starGradient1, int[] starGradient2, int[] starGradient3,
                            boolean customStarRGB, int[] starRGB1, int[] starRGB2, int[] starRGB3, long seed) {
        this.overrideClientSun = overrideClientSun;
        this.sunColor = sunColor;
        this.sunSize = sunSize;
        this.rainbowSun = rainbowSun;
        this.overrideClientMoon = overrideClientMoon;
        this.moonColor = moonColor;
        this.moonSize = moonSize;
        this.rainbowMoon = rainbowMoon;
        this.overrideClientStarAmount = overrideClientStarAmount;
        this.starAmount = starAmount;
        this.overrideClientStarConfig = overrideClientStarConfig;
        this.customStarGradient = customStarGradient;
        this.starGradient1 = starGradient1;
        this.starGradient2 = starGradient2;
        this.starGradient3 = starGradient3;
        this.customStarRGB = customStarRGB;
        this.starRGB1 = starRGB1;
        this.starRGB2 = starRGB2;
        this.starRGB3 = starRGB3;
        this.seed = seed;
    }

    public static void encode(ConfigSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.overrideClientSun);
        buf.writeVarIntArray(packet.sunColor);
        buf.writeInt(packet.sunSize);
        buf.writeBoolean(packet.rainbowSun);

        buf.writeBoolean(packet.overrideClientMoon);
        buf.writeVarIntArray(packet.moonColor);
        buf.writeInt(packet.moonSize);
        buf.writeBoolean(packet.rainbowMoon);

        buf.writeBoolean(packet.overrideClientStarAmount);
        buf.writeInt(packet.starAmount);

        buf.writeBoolean(packet.overrideClientStarConfig);
        buf.writeBoolean(packet.customStarGradient);
        buf.writeVarIntArray(packet.starGradient1);
        buf.writeVarIntArray(packet.starGradient2);
        buf.writeVarIntArray(packet.starGradient3);

        buf.writeBoolean(packet.customStarRGB);
        buf.writeVarIntArray(packet.starRGB1);
        buf.writeVarIntArray(packet.starRGB2);
        buf.writeVarIntArray(packet.starRGB3);

        buf.writeLong(packet.seed);
    }

    public static ConfigSyncPacket decode(FriendlyByteBuf buf) {
        return new ConfigSyncPacket(buf.readBoolean(), buf.readVarIntArray(), buf.readInt(), buf.readBoolean(),
                buf.readBoolean(), buf.readVarIntArray(), buf.readInt(), buf.readBoolean(),
                buf.readBoolean(), buf.readInt(),
                buf.readBoolean(), buf.readBoolean(), buf.readVarIntArray(), buf.readVarIntArray(), buf.readVarIntArray(),
                buf.readBoolean(), buf.readVarIntArray(), buf.readVarIntArray(), buf.readVarIntArray(), buf.readLong());
    }

    public static void handle(ConfigSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> {
            LOGGER.info("Syncing server config with client...");
            LOGGER.info("Got seed: {}", msg.seed);
            CustomSkyRender.seed = Math.abs(msg.seed);
            if (msg.overrideClientSun) {
                CustomSkyRender.sunColor = turnRGBArrayIntoAList(msg.sunColor);
                CustomSkyRender.sunSize = msg.sunSize;
                CustomSkyRender.rainbowSun = msg.rainbowSun;
            }
            if (msg.overrideClientMoon) {
                CustomSkyRender.moonColor = turnRGBArrayIntoAList(msg.moonColor);
                CustomSkyRender.moonSize = msg.moonSize;
                CustomSkyRender.rainbowMoon = msg.rainbowMoon;
            }
            if (msg.overrideClientStarAmount) {
                CustomSkyRender.minimumStarAmount = msg.starAmount;
                CustomSkyRender.maximumStarAmount = msg.starAmount + 1;
            }
            if (msg.overrideClientStarConfig) {
                CustomSkyRender.customStarGradient = msg.customStarGradient;
                CustomSkyRender.starGradient1 = turnRGBArrayIntoAList(msg.starGradient1);
                CustomSkyRender.starGradient2 = turnRGBArrayIntoAList(msg.starGradient2);
                CustomSkyRender.starGradient3 = turnRGBArrayIntoAList(msg.starGradient3);

                CustomSkyRender.overrideStarRGB = msg.customStarRGB;
                CustomSkyRender.starColor1 = turnRGBArrayIntoAList(msg.starRGB1);
                CustomSkyRender.starColor2 = turnRGBArrayIntoAList(msg.starRGB2);
                CustomSkyRender.starColor3 = turnRGBArrayIntoAList(msg.starRGB3);
            }
        });
    }

    private static List<? extends Integer> turnRGBArrayIntoAList(int[] array) { // I'm incredibly lazy, so this works terribly lmao
        return Arrays.asList(array[0], array[1], array[2], array[3]);
    }
}
