package com.pickleface5.atmos.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ClientConfigHandler {
    public final ForgeConfigSpec.BooleanValue disableSunRGB;
    public final ForgeConfigSpec.BooleanValue overrideSunRGB;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> sunColor;
    public final ForgeConfigSpec.IntValue sunSize;
    public final ForgeConfigSpec.BooleanValue rainbowSun;
    public final ForgeConfigSpec.BooleanValue disableMoonRGB;
    public final ForgeConfigSpec.BooleanValue overrideMoonRGB;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> moonColor;
    public final ForgeConfigSpec.IntValue moonSize;
    public final ForgeConfigSpec.BooleanValue rainbowMoon;
    public final ForgeConfigSpec.IntValue minimumStarAmount;
    public final ForgeConfigSpec.IntValue maximumStarAmount;
    public final ForgeConfigSpec.BooleanValue disableStarRGB;
    public final ForgeConfigSpec.BooleanValue disableStars;
    public final ForgeConfigSpec.BooleanValue rainbowStars;
    public final ForgeConfigSpec.BooleanValue naturalStars;
    public final ForgeConfigSpec.BooleanValue overrideStarRGB;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starColor1;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starColor2;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starColor3;
    public final ForgeConfigSpec.BooleanValue customStarGradient;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starGradient1;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starGradient2;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> starGradient3;
    public final ForgeConfigSpec.BooleanValue disableGalaxies;


    public ClientConfigHandler(final ForgeConfigSpec.Builder builder) {
        builder.push("sun");
        disableSunRGB = buildBoolean(builder, "vanillaSun", false, "Turn this on if you want the sun to be the same as vanilla minecraft (lame)");
        overrideSunRGB = buildBoolean(builder, "overrideSunRGB", false, "Overrides the sun's color code, allowing you to change the suns color to your will.");
        sunColor = buildTuple(builder, "sunColor", Arrays.asList(0, 0, 0, 255), "The suns' color (in RGB + alpha). Only used when overrideSunRGB is enabled.");
        sunSize = buildInt(builder, "sunSize", 30, 1, 2147483646, "The size of the sun. 30 is the default. Any (normal) number greater than 0 works.");
        rainbowSun = buildBoolean(builder, "rainbowSun", false, "Overrides overrideSunRGB and adds an animated rainbow effect to the sun.");
        builder.pop();
        builder.push("moon");
        disableMoonRGB = buildBoolean(builder, "vanillaMoon", false, "Disables the custom moon. (turn this on if you want the moon to be the same as vanilla minecraft)");
        overrideMoonRGB = buildBoolean(builder, "overrideMoonRGB", false, "Overrides the moon's color code, allowing you to change the moons color to your will.");
        moonColor = buildTuple(builder, "moonColor", Arrays.asList(0, 0, 0, 255), "The moons' color (in RGB + alpha). Only used when overrideMoonRGB is enabled.");
        moonSize = buildInt(builder, "moonSize", 20, 1, 2147483646, "The size of the moon. 20 is the default. Any (normal) number greater than 0 works.");
        rainbowMoon = buildBoolean(builder, "rainbowMoon", false, "Overrides overrideMoonRGB and adds an animated rainbow effect to the moon.");
        builder.pop();
        builder.comment("Star colors are prioritized as disableStars <- naturalStars <- vanilla(disableStarRGB) <- customStarGradient <- customStarRGB <- rainbowStars <- Mod default");
        builder.push("stars");
        minimumStarAmount = buildInt(builder, "minimumStarAmount", 1500, 0, 10000000, "The minimum amount of stars that can appear in a world.");
        maximumStarAmount = buildInt(builder, "maximumStarAmount", 3500, 1, 10000000, "The maximum amount of stars that can appear in a world. I recommend only using value lower than at least 100000, but you do what you want. Don't do anything too drastic :)");

        disableStarRGB = buildBoolean(builder, "disableStarRGB", false, "Disables colored stars. (Turn this on if you want the stars to be the same as vanilla minecraft)");
        disableStars = buildBoolean(builder, "disableStars", false, "Disables stars altogether.");
        rainbowStars = buildBoolean(builder, "rainbowStars", false, "Rainbow stars :)");
        naturalStars = buildBoolean(builder, "naturalStars", true, "Uses a natural gradient to determine the stars.");

        customStarGradient = buildBoolean(builder, "customStarGradient", false, "Overrides the star's color code, allowing you to create a custom range of colors for stars.");
        starGradient1 = buildTuple(builder, "starGradient1", Arrays.asList(0, 0, 0), "Gradient starter. Remember, ALL 3 GRADIENT VALUES NEED TO BE FILLED IN.");
        starGradient2 = buildTuple(builder, "starGradient2", Arrays.asList(0, 0, 0), "Gradient mid-point.");
        starGradient3 = buildTuple(builder, "starGradient3", Arrays.asList(0, 0, 0), "Gradient end.");

        overrideStarRGB = buildBoolean(builder, "overrideStarRGB", false, "Overrides the star's color code, allowing you to change the star colors to your will.");
        starColor1 = buildTuple(builder, "starColor1", Arrays.asList(-1, -1, -1), "The 1st custom color for stars (in RGB). Only used when overrideStarRGB is enabled.");
        starColor2 = buildTuple(builder, "starColor2", Arrays.asList(-1, -1, -1), "The 2nd custom color for stars (in RGB). Only used when overrideStarRGB is enabled. Change this if you want 2+ static colors for stars.");
        starColor3 = buildTuple(builder, "starColor3", Arrays.asList(-1, -1, -1), "The 3rd custom color for stars (in RGB). Only used when overrideStarRGB is enabled. Change this if you want 3 static colors for stars.");
        builder.pop();
        builder.push("galaxies");
        disableGalaxies = buildBoolean(builder, "disableGalaxies", true, "Disables galaxies. Will probably add more customization in the future, but for now I'll add this. They're currently randomly colored stars, so it's disabled by default.");
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
