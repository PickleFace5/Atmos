package com.pickleface5.atmos.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ServerConfigHandler {
    public final ForgeConfigSpec.BooleanValue syncValuesWithServer;
    public final ForgeConfigSpec.BooleanValue overrideClientSun;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> sunColor;
    public final ForgeConfigSpec.IntValue sunSize;
    public final ForgeConfigSpec.BooleanValue rainbowSun;
    public final ForgeConfigSpec.BooleanValue overrideClientMoon;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> moonColor;
    public final ForgeConfigSpec.IntValue moonSize;
    public final ForgeConfigSpec.BooleanValue rainbowMoon;
    public final ForgeConfigSpec.BooleanValue overrideClientStarAmount;
    public final ForgeConfigSpec.IntValue starAmount;
    public final ForgeConfigSpec.BooleanValue overrideClientStarConfig;
    public final ForgeConfigSpec.BooleanValue customStarGradient;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starGradient1;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starGradient2;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starGradient3;
    public final ForgeConfigSpec.BooleanValue overrideStarRGB;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starColor1;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starColor2;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starColor3;


    public ServerConfigHandler(final ForgeConfigSpec.Builder builder) {
        builder.push("multiplayer");
        syncValuesWithServer = buildBoolean(builder, "syncValuesWithServer", true, "When enabled, IF the server has this mod downloaded, the server will send its config to your client, ignoring your settings. Disable if your settings are better.");
        builder.pop();
        builder.push("sun");
        overrideClientSun = buildBoolean(builder, "overrideClientSun", false, "Overrides connected clients' sun config, replacing it with yours.");
        sunColor = buildTuple(builder, "sunColor", Arrays.asList(0, 0, 0, 255), "The suns' color (in RGB + alpha). Only used when overrideSunRGB is enabled.");
        sunSize = buildInt(builder, "sunSize", 30, 1, 2147483646, "The size of the sun. 30 is the default. Any number greater than 0 (up to 2147483646, but wouldn't try it personally :)) works.");
        rainbowSun = buildBoolean(builder, "rainbowSun", false, "Overrides sunColor and adds an animated rainbow effect to the sun.");
        builder.pop();
        builder.push("moon");
        overrideClientMoon = buildBoolean(builder, "overrideClientMoon", false, "Overrides connected clients' moon config, replacing it with yours.");
        moonColor = buildTuple(builder, "moonColor", Arrays.asList(0, 0, 0, 255), "The moons' color (in RGB + alpha). Only used when overrideMoonRGB is enabled.");
        moonSize = buildInt(builder, "moonSize", 20, 1, 2147483646, "The size of the moon. 20 is the default. Any (normal) number greater than 0 works.");
        rainbowMoon = buildBoolean(builder, "rainbowMoon", false, "Overrides moonColor and adds an animated rainbow effect to the moon.");
        builder.pop();
        builder.push("stars");
        overrideClientStarAmount = buildBoolean(builder, "overrideClientStarAmount", false, "Allows you to control the amount of stars clients see.");
        starAmount = buildInt(builder, "starAmount", 2000, 1, 10000000, "Amount of stars in the world.");
        overrideClientStarConfig = buildBoolean(builder, "overrideClientStarConfig", false, "Allows you to control the clients' star settings (e.g. star color, star gradient, etc)");
        customStarGradient = buildBoolean(builder, "customStarGradient", false, "Enables/disables custom star gradients for connected clients. Only works if overrideStarConfig is also enabled.");
        starGradient1 = buildTuple(builder, "starGradient1", Arrays.asList(0, 0, 0), "Gradient starter. Remember, ALL 3 GRADIENT VALUES NEED TO BE FILLED IN.");
        starGradient2 = buildTuple(builder, "starGradient2", Arrays.asList(0, 0, 0), "Gradient mid-point.");
        starGradient3 = buildTuple(builder, "starGradient3", Arrays.asList(0, 0, 0), "Gradient end.");
        overrideStarRGB = buildBoolean(builder, "overrideStarRGB", false, "Overrides the star's color code, allowing you to change the star colors to your will.");
        starColor1 = buildTuple(builder, "starColor1", Arrays.asList(-1, -1, -1), "The 1st custom color for stars (in RGB). Only used when overrideStarRGB is enabled.");
        starColor2 = buildTuple(builder, "starColor2", Arrays.asList(-1, -1, -1), "The 2nd custom color for stars (in RGB). Only used when overrideStarRGB is enabled. Change this if you want 2+ static colors for stars.");
        starColor3 = buildTuple(builder, "starColor3", Arrays.asList(-1, -1, -1), "The 3rd custom color for stars (in RGB). Only used when overrideStarRGB is enabled. Change this if you want 3 static colors for stars.");
        builder.pop();
    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.DoubleValue buildDouble(ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> buildTuple(ForgeConfigSpec.Builder builder, String name, List<? extends Integer> defaultValue, String comment) {
        return builder.comment(comment).translation(name).defineList(name, defaultValue, it -> it instanceof Integer);
    }
}
