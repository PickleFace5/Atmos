package com.pickleface5.atmos.utils;

import com.pickleface5.atmos.Atmos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Hours wasted in this file alone: 32

public class GradientGenerator {
    public static List<Color> generateGradientList(int amount, Color color1, Color color2) {
        amount = amount - 1;
        double ratio = 0;
        if (amount == 1) ratio = 0.5d;
        double ratioIncrement = 1d / amount;
        List<Color> colors;
        try {
            colors = new ArrayList<>(amount);
        } catch (Exception e) { // In case I'm stupid
            return new ArrayList<>(0);
        }
        for (int i = 0; i < amount; i++) {
            Atmos.LOGGER.trace(String.valueOf(ratio));
            int r = (int) Math.ceil(color1.getRed() * ratio + color2.getRed() * (1 - ratio));
            if (r > 255) r = 255;
            Atmos.LOGGER.trace(String.valueOf(r));
            int g = (int) Math.ceil(color1.getGreen() * ratio + color2.getGreen() * (1 - ratio));
            if (g > 255) g = 255;
            Atmos.LOGGER.trace(String.valueOf(g));
            int b = (int) Math.ceil(color1.getBlue() * ratio + color2.getBlue() * (1 - ratio));
            if (b > 255) b = 255;
            Atmos.LOGGER.trace(String.valueOf(b));
            try {
                colors.add(new Color(r, g, b));
            } catch (IllegalArgumentException exception) {
                Atmos.LOGGER.warn("Issues generating Color containing ({}, {}, {}), skipping...", r, g, b);
            } finally {
                ratio = ratio + ratioIncrement;
            }
        }
        colors.add(color1);
        return colors;
    }

    public static List<Color> createLargeGradient(int amount, Color color1, Color color2, Color color3) {
        List<Color> colors = new ArrayList<>(amount);
        List<Color> redToWhite = GradientGenerator.generateGradientList(amount / 2, color1, color2);
        List<Color> whiteToBlue = GradientGenerator.generateGradientList(amount / 2, color2, color3);
        Stream.of(redToWhite, whiteToBlue).forEach(colors::addAll);
        return colors;
    }
}
