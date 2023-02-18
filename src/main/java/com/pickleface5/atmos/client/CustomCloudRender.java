package com.pickleface5.atmos.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.pickleface5.atmos.Atmos;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ICloudRenderHandler;

import javax.annotation.Nullable;

public class CustomCloudRender implements ICloudRenderHandler {
    private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
    private boolean generateClouds = true;
    private VertexBuffer cloudBuffer;
    private int prevCloudX = Integer.MIN_VALUE;
    private int prevCloudY = Integer.MIN_VALUE;
    private int prevCloudZ = Integer.MIN_VALUE;
    private Vec3 prevCloudColor = Vec3.ZERO;
    @Nullable
    private CloudStatus prevCloudsType;

    @Override
    public void render(int ticks, float partialTick, PoseStack poseStack, ClientLevel level, Minecraft minecraft, double camX, double camY, double camZ) {
        float f = level.effects().getCloudHeight();
        if (!Float.isNaN(f)) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            double d1 = ((float)ticks + partialTick) * 0.03F;
            double d2 = (camX + d1) / 12.0D;
            double d3 = f - (float)camY + 0.33F;
            double d4 = camZ / 12.0D + (double)0.33F;
            d2 -= Mth.floor(d2 / 2048.0D) * 2048;
            d4 -= Mth.floor(d4 / 2048.0D) * 2048;
            float f3 = (float)(d2 - (double)Mth.floor(d2));
            float f4 = (float)(d3 / 4.0D - (double)Mth.floor(d3 / 4.0D)) * 4.0F;
            float f5 = (float)(d4 - (double)Mth.floor(d4));
            Vec3 vec3 = level.getCloudColor(partialTick);
            int i = (int)Math.floor(d2);
            int j = (int)Math.floor(d3 / 4.0D);
            int k = (int)Math.floor(d4);
           if (i != this.prevCloudX || j != this.prevCloudY || k != this.prevCloudZ || minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(vec3) > 2.0E-4D) {
                this.prevCloudX = i;
                this.prevCloudY = j;
                this.prevCloudZ = k;
                this.prevCloudColor = vec3;
                this.prevCloudsType = minecraft.options.getCloudsType();
                this.generateClouds = true;
            }

            if (this.generateClouds) {
                this.generateClouds = false;
                BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
                if (this.cloudBuffer != null) {
                    this.cloudBuffer.close();
                }

                this.cloudBuffer = new VertexBuffer();
                RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
                this.buildClouds(bufferbuilder, d2, d3, d4, vec3);
                this.buildClouds(bufferbuilder, (d2 + 1500), d3 + 32, d4, vec3);
                this.buildClouds(bufferbuilder, (d2 + 500), d3 + 48, d4, vec3);
                bufferbuilder.end();
                this.cloudBuffer.upload(bufferbuilder);
            }

            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            RenderSystem.setShaderTexture(0, CLOUDS_LOCATION);
            FogRenderer.levelFogColor();
            poseStack.pushPose();
            poseStack.scale(12.0F, 1.0F, 12.0F);
            //poseStack.mulPose(Vector3f.YP.rotation(45)); TODO: Figure out algorithm for rotating clouds seamlessly
            poseStack.translate(-f3, f4, -f5);
            if (this.cloudBuffer != null) {
                int i1 = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1;

                for(int l = i1; l < 2; ++l) {
                    if (l == 0) {
                        RenderSystem.colorMask(false, false, false, false);
                    } else {
                        RenderSystem.colorMask(true, true, true, true);
                    }

                    ShaderInstance shaderinstance = RenderSystem.getShader();
                    assert shaderinstance != null;
                    this.cloudBuffer.drawWithShader(poseStack.last().pose(), RenderSystem.getProjectionMatrix(), shaderinstance);
                }
            }

            poseStack.popPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    private void buildClouds(BufferBuilder p_109579_, double p_109580_, double p_109581_, double p_109582_, Vec3 p_109583_) {
        Atmos.LOGGER.trace("buildClouds ran with cords of {} {} {}", p_109580_, p_109581_, p_109582_);
        float f = 4.0F;
        float f1 = 0.00390625F;
        int i = 8;
        float f2 = 9.765625E-4F;
        float f3 = (float)Mth.floor(p_109580_) * 0.00390625F;
        float f4 = (float)Mth.floor(p_109582_) * 0.00390625F;
        float f5 = (float)p_109583_.x;
        float f6 = (float)p_109583_.y;
        float f7 = (float)p_109583_.z;
        float f8 = f5 * 0.9F;
        float f9 = f6 * 0.9F;
        float f10 = f7 * 0.9F;
        float f11 = f5 * 0.7F;
        float f12 = f6 * 0.7F;
        float f13 = f7 * 0.7F;
        float f14 = f5 * 0.8F;
        float f15 = f6 * 0.8F;
        float f16 = f7 * 0.8F;
        float f17 = (float)Math.floor(p_109581_ / 4.0D) * 4.0F;
        if (this.prevCloudsType == CloudStatus.FANCY) {
            for(int k = -3; k <= 4; ++k) {
                for(int l = -3; l <= 4; ++l) {
                    float f18 = (float)(k * 8);
                    float f19 = (float)(l * 8);
                    if (f17 > -5.0F) {
                        p_109579_.vertex(f18 + 0.0F, f17 + 0.0F, f19 + i).uv((f18 + 0.0F) * f1 + f3, (f19 + i) * f1 + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        p_109579_.vertex(f18 + i, f17 + 0.0F, f19 + i).uv((f18 + i) * f1 + f3, (f19 + i) * f1 + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        p_109579_.vertex(f18 + i, f17 + 0.0F, f19 + 0.0F).uv((f18 + i) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        p_109579_.vertex(f18 + 0.0F, f17 + 0.0F, f19 + 0.0F).uv((f18 + 0.0F) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (f17 <= 5.0F) {
                        p_109579_.vertex(f18 + 0.0F, f17 + f - f2, f19 + i).uv((f18 + 0.0F) * f1 + f3, (f19 + i) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        p_109579_.vertex(f18 + i, f17 + f - f2, f19 + i).uv((f18 + i) * f1 + f3, (f19 + i) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        p_109579_.vertex(f18 + i, f17 + f - f2, f19 + 0.0F).uv((f18 + i) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        p_109579_.vertex(f18 + 0.0F, f17 + f - f2, f19 + 0.0F).uv((f18 + 0.0F) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    if (k > -1) {
                        for(int i1 = 0; i1 < 8; ++i1) {
                            p_109579_.vertex(f18 + (float)i1 + 0.0F, f17 + 0.0F, f19 + i).uv((f18 + (float)i1 + 0.5F) * f1 + f3, (f19 + i) * f1 + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            p_109579_.vertex(f18 + (float)i1 + 0.0F, f17 + f, f19 + i).uv((f18 + (float)i1 + 0.5F) * f1 + f3, (f19 + i) * f1 + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            p_109579_.vertex(f18 + (float)i1 + 0.0F, f17 + f, f19 + 0.0F).uv((f18 + (float)i1 + 0.5F) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            p_109579_.vertex(f18 + (float)i1 + 0.0F, f17 + 0.0F, f19 + 0.0F).uv((f18 + (float)i1 + 0.5F) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (k <= 1) {
                        for(int j2 = 0; j2 < 8; ++j2) {
                            p_109579_.vertex(f18 + (float)j2 + 1.0F - f2, f17 + 0.0F, f19 + i).uv((f18 + (float)j2 + 0.5F) * f1 + f3, (f19 + i) * f1 + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            p_109579_.vertex(f18 + (float)j2 + 1.0F - f2, f17 + f, f19 + i).uv((f18 + (float)j2 + 0.5F) * f1 + f3, (f19 + i) * f1 + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            p_109579_.vertex(f18 + (float)j2 + 1.0F - f2, f17 + f, f19 + 0.0F).uv((f18 + (float)j2 + 0.5F) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            p_109579_.vertex(f18 + (float)j2 + 1.0F - f2, f17 + 0.0F, f19 + 0.0F).uv((f18 + (float)j2 + 0.5F) * f1 + f3, (f19 + 0.0F) * f1 + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (l > -1) {
                        for(int k2 = 0; k2 < 8; ++k2) {
                            p_109579_.vertex(f18 + 0.0F, f17 + f, f19 + (float)k2 + 0.0F).uv((f18 + 0.0F) * f1 + f3, (f19 + (float)k2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            p_109579_.vertex(f18 + i, f17 + f, f19 + (float)k2 + 0.0F).uv((f18 + i) * f1 + f3, (f19 + (float)k2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            p_109579_.vertex(f18 + i, f17 + 0.0F, f19 + (float)k2 + 0.0F).uv((f18 + i) * f1 + f3, (f19 + (float)k2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            p_109579_.vertex(f18 + 0.0F, f17 + 0.0F, f19 + (float)k2 + 0.0F).uv((f18 + 0.0F) * f1 + f3, (f19 + (float)k2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (l <= 1) {
                        for(int l2 = 0; l2 < 8; ++l2) {
                            p_109579_.vertex(f18 + 0.0F, f17 + f, f19 + (float)l2 + 1.0F - f2).uv((f18 + 0.0F) * f1 + f3, (f19 + (float)l2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            p_109579_.vertex(f18 + i, f17 + f, f19 + (float)l2 + 1.0F - f2).uv((f18 + i) * f1 + f3, (f19 + (float)l2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            p_109579_.vertex(f18 + i, f17 + 0.0F, f19 + (float)l2 + 1.0F - f2).uv((f18 + i) * f1 + f3, (f19 + (float)l2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            p_109579_.vertex(f18 + 0.0F, f17 + 0.0F, f19 + (float)l2 + 1.0F - f2).uv((f18 + 0.0F) * f1 + f3, (f19 + (float)l2 + 0.5F) * f1 + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }
                }
            }
        } else {
            int k1 = 32;

            for(int l1 = -k1; l1 < k1; l1 += k1) {
                for(int i2 = -k1; i2 < k1; i2 += k1) {
                    p_109579_.vertex(l1, f17, i2 + k1).uv((float)(l1) * f1 + f3, (float)(i2 + k1) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    p_109579_.vertex(l1 + k1, f17, i2 + k1).uv((float)(l1 + k1) * f1 + f3, (float)(i2 + k1) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    p_109579_.vertex(l1 + k1, f17, i2).uv((float)(l1 + k1) * f1 + f3, (float)(i2) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    p_109579_.vertex(l1, f17, i2).uv((float)(l1) * f1 + f3, (float)(i2) * f1 + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                }
            }
        }

    }
}
