package com.pickleface5.atmos.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.pickleface5.atmos.Atmos;
import com.pickleface5.atmos.config.ConfigHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ISkyRenderHandler;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static com.pickleface5.atmos.utils.GradientGenerator.createLargeGradient;
import static java.lang.Math.round;

public class CustomSkyRender implements ISkyRenderHandler {
    private static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
    private final Color naturalRed = new Color(255, 180, 180);
    private final Color naturalWhite = new Color(255, 255, 255);
    private final Color naturalBlue = new Color(176, 229, 234);
    
    public static boolean vanillaSun = ConfigHolder.CLIENT.disableSunRGB.get();
    public static boolean overrideSunRGB = ConfigHolder.CLIENT.overrideSunRGB.get();
    public static List<? extends Integer> sunColor = ConfigHolder.CLIENT.sunColor.get();
    public static int sunSize = ConfigHolder.CLIENT.sunSize.get();
    public static boolean rainbowSun = ConfigHolder.CLIENT.rainbowSun.get();
    public static boolean vanillaMoon = ConfigHolder.CLIENT.disableMoonRGB.get();
    public static boolean overrideMoonRGB = ConfigHolder.CLIENT.overrideMoonRGB.get();
    public static List<? extends Integer> moonColor = ConfigHolder.CLIENT.moonColor.get();
    public static int moonSize = ConfigHolder.CLIENT.moonSize.get();
    public static boolean rainbowMoon = ConfigHolder.CLIENT.rainbowMoon.get();
    public static int minimumStarAmount = ConfigHolder.CLIENT.minimumStarAmount.get();
    public static int maximumStarAmount = ConfigHolder.CLIENT.maximumStarAmount.get();
    public static boolean disableStarRGB = ConfigHolder.CLIENT.disableStarRGB.get();
    public static boolean disableStars = ConfigHolder.CLIENT.disableStars.get();
    public static boolean rainbowStars = ConfigHolder.CLIENT.rainbowStars.get();
    public static boolean naturalStars = ConfigHolder.CLIENT.naturalStars.get();
    public static boolean customStarGradient = ConfigHolder.CLIENT.customStarGradient.get();
    public static List<? extends Integer> starGradient1 = ConfigHolder.CLIENT.starGradient1.get();
    public static List<? extends Integer> starGradient2 = ConfigHolder.CLIENT.starGradient2.get();
    public static List<? extends Integer> starGradient3 = ConfigHolder.CLIENT.starGradient3.get();
    public static boolean overrideStarRGB = ConfigHolder.CLIENT.overrideStarRGB.get();
    public static List<? extends Integer> starColor1 = ConfigHolder.CLIENT.starColor1.get();
    public static List<? extends Integer> starColor2 = ConfigHolder.CLIENT.starColor2.get();
    public static List<? extends Integer> starColor3 = ConfigHolder.CLIENT.starColor3.get();

    private VertexBuffer skyBuffer;
    private VertexBuffer starBuffer;
    private VertexBuffer darkBuffer;
    private VertexBuffer galaxyBuffer;
    public static long seed;
    private static int Sr;
    private static int Sg;
    private static int Sb;
    private double Sh = 0;
    private static int Mr;
    private static int Mg;
    private static int Mb;
    private double Mh = 0;


    public CustomSkyRender(long seed) {
        CustomSkyRender.seed = Math.abs(seed);
        this.createLightSky();
        this.createDarkSky();
        this.createStars();
        this.createGalaxies();
        setRandomValues(Math.abs(seed));
    }

    public static void setRandomValues(long seed) {
        Random random = new Random(seed);
        Sr = random.nextInt(256);
        Sg = random.nextInt(256);
        Sb = random.nextInt(256);
        Mr = random.nextInt(256);
        Mg = random.nextInt(256);
        Mb = random.nextInt(256);
    }

    private void createLightSky() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        if (this.skyBuffer != null) {
            this.skyBuffer.close();
        }
        this.skyBuffer = new VertexBuffer();
        buildSkyDisc(bufferbuilder, 16.0F);
        this.skyBuffer.upload(bufferbuilder);
    }

    private void createDarkSky() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        if (this.darkBuffer != null) {
            this.darkBuffer.close();
        }
        this.darkBuffer = new VertexBuffer();
        buildSkyDisc(bufferbuilder, -16.0F);
        this.darkBuffer.upload(bufferbuilder);
    }

    private static void buildSkyDisc(BufferBuilder p_172948_, float p_172949_) {
        float f = Math.signum(p_172949_) * 512.0F;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        p_172948_.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        p_172948_.vertex(0.0D, p_172949_, 0.0D).endVertex();
        for (int i = -180; i <= 180; i += 45) {
            p_172948_.vertex(f * Mth.cos((float) i * ((float) Math.PI / 180F)), p_172949_, 512.0F * Mth.sin((float) i * ((float) Math.PI / 180F))).endVertex();
        }
        p_172948_.end();
    }

    private void createStars() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        if (!disableStarRGB) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionShader);
        }
        if (this.starBuffer != null) {
            this.starBuffer.close();
        }
        if (!disableStars) {
            this.starBuffer = new VertexBuffer();
            this.drawStars(bufferbuilder);
            bufferbuilder.end();
            this.starBuffer.upload(bufferbuilder);
        } else {
            Atmos.LOGGER.info("disableStars selected, skipping star rendering...");
        }
    }

    private void drawStars(BufferBuilder p_109555_) {
        Random random = new Random(seed);

        int[] sC = getCustomStarColors();
        boolean allCanceled = isCustomColorCanceled(sC[0], sC[1], sC[2]) && isCustomColorCanceled(sC[3], sC[4], sC[5]) && isCustomColorCanceled(sC[6], sC[7], sC[8]);

        int min = minimumStarAmount;
        int max = random.nextInt(maximumStarAmount);
        int am;
        am = random.nextInt(random.nextInt(max) - random.nextInt(min)) + min;

        List<Color> naturalColors = createLargeGradient((am / 3 + max), naturalRed, naturalWhite, naturalBlue);
        List<? extends Integer> gradient1 = starGradient1;
        List<? extends Integer> gradient2 = starGradient2;
        List<? extends Integer> gradient3 = starGradient3;
        List<Color> customGradient = createLargeGradient((am / 3 + max), new Color(gradient1.get(0), gradient1.get(1), gradient1.get(2)), new Color(gradient2.get(0), gradient2.get(1), gradient2.get(2)), new Color(gradient3.get(0), gradient3.get(1), gradient3.get(2)));
        p_109555_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int canceledColors = 0;
        if (isCustomColorCanceled(sC[3], sC[4], sC[5])) canceledColors++;
        if (isCustomColorCanceled(sC[6], sC[7], sC[8])) canceledColors++;
        Atmos.LOGGER.debug("Amount of stars needed to be rendered: {}", am);
        for (int i = 0; i < am; ++i) {
            double d0 = random.nextFloat() * 2.0F - 1.0F;
            double d1 = random.nextFloat() * 2.0F - 1.0F;
            double d2 = random.nextFloat() * 2.0F - 1.0F;
            double d3 = 0.15F + random.nextFloat() * 0.1F;
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                int c1 = random.nextInt(256);
                int c2 = random.nextInt(256);
                int c3 = random.nextInt(256);
                int gradientIndex = random.nextInt(am / 3 + max);
                int sT = random.nextInt(3 - canceledColors) + (1 + canceledColors);
                for (int j = 0; j < 4; ++j) {
                    double d18 = (double) ((j & 2) - 1) * d3;
                    double d19 = (double) ((j + 1 & 2) - 1) * d3;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    if (naturalStars) {
                        p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).color(naturalColors.get(gradientIndex).getRed(), naturalColors.get(gradientIndex).getGreen(), naturalColors.get(gradientIndex).getBlue(), 255).endVertex();
                        continue;
                    } if (disableStarRGB) {
                        p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).endVertex();
                        continue;
                    } if (customStarGradient) {
                        p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).color(customGradient.get(gradientIndex).getRed(), customGradient.get(gradientIndex).getGreen(), customGradient.get(gradientIndex).getBlue(), 255).endVertex();
                        //Atmos.LOGGER.trace("Star created with color ({}, {}, {}, 255)", customGradient.get(gradientIndex).getRed(), customGradient.get(gradientIndex).getGreen(), customGradient.get(gradientIndex).getBlue());
                        continue;
                    } if (overrideStarRGB && !allCanceled) {
                        if (sT - canceledColors == 1) {
                            p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).color(sC[0], sC[1], sC[2], 255).endVertex();
                        } else if (sT - canceledColors == 2) {
                            p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).color(sC[3], sC[4], sC[5], 255).endVertex();
                        } else {
                            p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).color(sC[6], sC[7], sC[8], 255).endVertex();
                        }
                        continue;
                    } if (rainbowStars) {
                        p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).color(c1, c2, c3, 255).endVertex();
                        continue;
                    }
                    p_109555_.vertex(d5 + d25, d6 + d23, d7 + d26).color(round(c1 * 1.5F), round(c2 * 0.15F), round(c3 * 1.5F), 255).endVertex();
                }
            }
        }
        Atmos.LOGGER.info("Successfully drew {} star(s)", am);
    }

    private float getStarBrightnessBasedOnPlayerHeight(Minecraft minecraft, float time) {
        assert minecraft.level != null;
        assert minecraft.player != null;
        float starBrightness = minecraft.level.getStarBrightness(time);
        float eyePos = (float)((minecraft.player.getEyePosition().y() + 38 + 64) * 0.01);
        eyePos = Mth.clamp(eyePos, 1, 2);
        float rainLevel = 1 - minecraft.level.getRainLevel(time);
        return (starBrightness * eyePos) * rainLevel;
    }

    private int[] getCustomStarColors() {
        return new int[]{starColor1.get(0), starColor1.get(1), starColor1.get(2), starColor2.get(0), starColor2.get(1), starColor2.get(2), starColor3.get(0), starColor3.get(1), starColor3.get(2)};
    }

    private boolean isCustomColorCanceled(int r, int g, int b) {
        int counter = 0;
        if (r == -1) counter++;
        if (g == -1) counter++;
        if (b == -1) counter++;
        return counter != 0;
    }

    private void createGalaxies() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (this.galaxyBuffer != null) {
            this.galaxyBuffer.close();
        }
        this.galaxyBuffer = new VertexBuffer();
        this.drawGalaxies(bufferBuilder);
        bufferBuilder.end();
        this.galaxyBuffer.upload(bufferBuilder);
    }

    private void drawGalaxies(BufferBuilder bufferBuilder) {
        Random random = new Random(seed);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        if (!ConfigHolder.CLIENT.disableGalaxies.get()) {
            for (int i = 0; i < 50 + random.nextInt(451); ++i) {
                double d0 = random.nextFloat() * 2.0F - 1.0F;
                double d1 = random.nextFloat() * 2.0F - 1.0F;
                double d2 = random.nextFloat() * 2.0F - 1.0F;
                double d3 = 0.15F + random.nextFloat() * 0.1F;
                double d4 = 1.0D / Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);
                for (int j = 0; j < 4; ++j) {
                    double d18 = (double) ((j & 2) - 1) * d3;
                    double d19 = (double) ((j + 1 & 2) - 1) * d3;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    bufferBuilder.vertex(d5 * 2.5F + d25 * 2.5F, d6 * 2.5F + d23 * 2.5F, d7 * 2.5F + d26 * 2.5F).color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 200).endVertex();
                }
            }
        }
    }

    private void renderEndSky(PoseStack p_109781_) { // Might be an issue with other dimensions
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        for (int i = 0; i < 6; ++i) {
            p_109781_.pushPose();
            if (i == 1) {
                p_109781_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                p_109781_.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                p_109781_.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                p_109781_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                p_109781_.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = p_109781_.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(40, 40, 40, 255).endVertex();
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(40, 40, 40, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(40, 40, 40, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(40, 40, 40, 255).endVertex();
            tesselator.end();
            p_109781_.popPose();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public void render(int ticks, float p_202426_, PoseStack p_202424_, ClientLevel level, Minecraft minecraft) {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Runnable setupFog = () -> {
            assert minecraft.level != null;
            FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, minecraft.gameRenderer.getRenderDistance(), minecraft.level.effects().isFoggyAt(Mth.floor(camera.getPosition().x()), Mth.floor(camera.getPosition().y())) || minecraft.gui.getBossOverlay().shouldCreateWorldFog(), p_202426_);
        };
        Matrix4f p_202425_ = RenderSystem.getProjectionMatrix();
        setupFog.run();
        assert minecraft.level != null;
        if (!minecraft.level.effects().isFoggyAt(Mth.floor(camera.getPosition().x()), Mth.floor(camera.getPosition().y())) || minecraft.gui.getBossOverlay().shouldCreateWorldFog()) {
            FogType fogtype = camera.getFluidInCamera();
            if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA) {
                Entity $$9 = camera.getEntity();
                if ($$9 instanceof LivingEntity livingentity) {
                    if (livingentity.hasEffect(MobEffects.BLINDNESS)) {
                        return;
                    }
                }
                if (minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
                    this.renderEndSky(p_202424_);
                } else if (minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
                    RenderSystem.disableTexture(); // Sky Color Stuff
                    Vec3 vec3 = level.getSkyColor(minecraft.gameRenderer.getMainCamera().getPosition(), p_202426_);
                    float f10 = (float) vec3.x;
                    float f = (float) vec3.y;
                    float f1 = (float) vec3.z;
                    FogRenderer.levelFogColor();
                    BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
                    RenderSystem.depthMask(false);
                    RenderSystem.setShaderColor(f10, f, f1, 1.0F);
                    ShaderInstance shaderinstance = RenderSystem.getShader();
                    assert shaderinstance != null;
                    this.skyBuffer.drawWithShader(p_202424_.last().pose(), p_202425_, shaderinstance);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    float[] afloat = level.effects().getSunriseColor(level.getTimeOfDay(p_202426_), p_202426_);
                    if (afloat != null) {
                        RenderSystem.setShader(GameRenderer::getPositionColorShader);
                        RenderSystem.disableTexture();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        p_202424_.pushPose();
                        p_202424_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                        float f2 = Mth.sin(level.getSunAngle(p_202426_)) < 0.0F ? 180.0F : 0.0F;
                        p_202424_.mulPose(Vector3f.ZP.rotationDegrees(f2));
                        p_202424_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                        float f3 = afloat[0];
                        float f4 = afloat[1];
                        float f5 = afloat[2];
                        Matrix4f matrix4f = p_202424_.last().pose();
                        bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                        bufferbuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(f3, f4, f5, afloat[3]).endVertex();
                        for (int j = 0; j <= 16; ++j) {
                            float f6 = (float) j * ((float) Math.PI * 2F) / 16.0F;
                            float f7 = Mth.sin(f6);
                            float f8 = Mth.cos(f6);
                            bufferbuilder.vertex(matrix4f, f7 * 120.0F, f8 * 120.0F, -f8 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                        }
                        bufferbuilder.end();
                        BufferUploader.end(bufferbuilder);
                        p_202424_.popPose();
                    }
                    RenderSystem.enableTexture();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    p_202424_.pushPose();
                    float f11 = 1.0F - level.getRainLevel(p_202426_);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
                    p_202424_.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
                    p_202424_.mulPose(Vector3f.XP.rotationDegrees(level.getTimeOfDay(p_202426_) * 360.0F));
                    Matrix4f matrix4f1 = p_202424_.last().pose();
                    float f12 = sunSize;
                    RenderSystem.setShaderTexture(0, SUN_LOCATION);
                    if ((!vanillaSun)) {
                        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                    } else {
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    }
                    if (rainbowSun) {
                        Sh = Sh + 0.0027777777777778;
                        if (Sh > 1) Sh = 0;
                        Color RGBSun = Color.getHSBColor((float) Sh, 1, 1);
                        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).color(RGBSun.getRed(), RGBSun.getGreen(), RGBSun.getBlue(), 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).color(RGBSun.getRed(), RGBSun.getGreen(), RGBSun.getBlue(), 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).color(RGBSun.getRed(), RGBSun.getGreen(), RGBSun.getBlue(), 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).color(RGBSun.getRed(), RGBSun.getGreen(), RGBSun.getBlue(), 255).endVertex();
                    } else if (overrideSunRGB) {
                        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).color(sunColor.get(0), sunColor.get(1), sunColor.get(2), sunColor.get(3)).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).color(sunColor.get(0), sunColor.get(1), sunColor.get(2), sunColor.get(3)).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).color(sunColor.get(0), sunColor.get(1), sunColor.get(2), sunColor.get(3)).endVertex();
                        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).color(sunColor.get(0), sunColor.get(1), sunColor.get(2), sunColor.get(3)).endVertex();
                    } else {
                        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).color(Sr, Sg, Sb, 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).color(Sr, Sg, Sb, 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).color(Sr, Sg, Sb, 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).color(Sr, Sg, Sb, 255).endVertex();
                    }
                    bufferbuilder.end();
                    BufferUploader.end(bufferbuilder);
                    f12 = moonSize;
                    int k = level.getMoonPhase();
                    int l = k % 4;
                    int i1 = k / 4 % 2;
                    float f13 = (float) (l) / 4.0F;
                    float f14 = (float) (i1) / 2.0F;
                    float f15 = (float) (l + 1) / 4.0F;
                    float f16 = (float) (i1 + 1) / 2.0F;
                    RenderSystem.setShaderTexture(0, MOON_LOCATION);
                    if ((!vanillaMoon)) {
                        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                    } else {
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    }
                    if (rainbowMoon) {
                        Mh = Mh + 0.0027777777777778;
                        if (Mh > 1) Mh = 0;
                        Color RGBMoon = Color.getHSBColor((float) Mh, 1, 1);
                        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, f12).uv(f15, f16).color(RGBMoon.getRed(), RGBMoon.getGreen(), RGBMoon.getBlue(), 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, -100.0F, f12).uv(f13, f16).color(RGBMoon.getRed(), RGBMoon.getGreen(), RGBMoon.getBlue(), 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, -100.0F, -f12).uv(f13, f14).color(RGBMoon.getRed(), RGBMoon.getGreen(), RGBMoon.getBlue(), 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, -f12).uv(f15, f14).color(RGBMoon.getRed(), RGBMoon.getGreen(), RGBMoon.getBlue(), 255).endVertex();
                    } else if (overrideMoonRGB) {
                        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, f12).uv(f15, f16).color(moonColor.get(0), moonColor.get(1), moonColor.get(2), moonColor.get(3)).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, -100.0F, f12).uv(f13, f16).color(moonColor.get(0), moonColor.get(1), moonColor.get(2), moonColor.get(3)).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, -100.0F, -f12).uv(f13, f14).color(moonColor.get(0), moonColor.get(1), moonColor.get(2), moonColor.get(3)).endVertex();
                        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, -f12).uv(f15, f14).color(moonColor.get(0), moonColor.get(1), moonColor.get(2), moonColor.get(3)).endVertex();
                    } else {
                        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, f12).uv(f15, f16).color(Mr, Mg, Mb, 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, -100.0F, f12).uv(f13, f16).color(Mr, Mg, Mb, 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, f12, -100.0F, -f12).uv(f13, f14).color(Mr, Mg, Mb, 255).endVertex();
                        bufferbuilder.vertex(matrix4f1, -f12, -100.0F, -f12).uv(f15, f14).color(Mr, Mg, Mb, 255).endVertex();
                    }
                    bufferbuilder.end();
                    BufferUploader.end(bufferbuilder);
                    RenderSystem.disableTexture();
                    assert minecraft.player != null;
                    float f9 = getStarBrightnessBasedOnPlayerHeight(minecraft, p_202426_);
                    if (f9 > 0.0F) {
                        if (f9 > 100.0F) f9 = 100.0F;
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f9);
                        FogRenderer.setupNoFog();
                        assert GameRenderer.getPositionColorShader() != null;
                        this.starBuffer.drawWithShader(p_202424_.last().pose(), p_202425_, GameRenderer.getPositionColorShader());
                        setupFog.run();
                        RenderSystem.setShaderColor(f9 * 1.25F, f9 * 1.25F, f9 * 1.25F, 1.0F);
                        assert GameRenderer.getPositionColorShader() != null;
                        this.galaxyBuffer.drawWithShader(p_202424_.last().pose(), p_202425_, GameRenderer.getPositionColorShader());
                    }
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                    p_202424_.popPose();
                    RenderSystem.disableTexture();
                    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                    assert minecraft.player != null;
                    double d0 = minecraft.player.getEyePosition(p_202426_).y - level.getLevelData().getHorizonHeight(level);
                    if (d0 < 0.0D) {
                        p_202424_.pushPose();
                        p_202424_.translate(0.0D, 12.0D, 0.0D);
                        this.darkBuffer.drawWithShader(p_202424_.last().pose(), p_202425_, shaderinstance);
                        p_202424_.popPose();
                    }
                    if (level.effects().hasGround()) {
                        RenderSystem.setShaderColor(f10 * 0.2F + 0.04F, f * 0.2F + 0.04F, f1 * 0.6F + 0.1F, 1.0F);
                    } else {
                        RenderSystem.setShaderColor(f10, f, f1, 1.0F);
                    }
                    RenderSystem.enableTexture();
                    RenderSystem.depthMask(true);
                }
            }
        }
    }
}
