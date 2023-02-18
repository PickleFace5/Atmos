package com.pickleface5.atmos.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.IWeatherParticleRenderHandler;

import java.util.Random;

public class CustomWeatherParticleRender implements IWeatherParticleRenderHandler { //TODO: Angle rain during thunder storms (AND MAKE THIS LIKE WORK PLS)
    private int rainSoundTime;

    @Override
    public void render(int ticks, ClientLevel level, Minecraft minecraft, Camera camera) { // Hours wasted: 2
        assert minecraft.level != null;
        float f = minecraft.level.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
        if (!(f <= 0.0F)) {
            Random random = new Random((long)ticks * 312987231L);
            LevelReader levelreader = minecraft.level;
            BlockPos blockpos = new BlockPos(camera.getPosition());
            BlockPos blockpos1 = null;
            int i = (int)(100.0F * f * f) / (minecraft.options.particles == ParticleStatus.DECREASED ? 2 : 1);

            for(int j = 0; j < i; ++j) {
                int k = random.nextInt(21) - 10;
                int l = random.nextInt(21) - 10;
                BlockPos blockpos2 = levelreader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos.offset(k, 0, l));
                Biome biome = levelreader.getBiome(blockpos2).value();
                if (blockpos2.getY() > levelreader.getMinBuildHeight() && blockpos2.getY() <= blockpos.getY() + 10 && blockpos2.getY() >= blockpos.getY() - 10 && biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.warmEnoughToRain(blockpos2)) {
                    blockpos1 = blockpos2.below();
                    if (minecraft.options.particles == ParticleStatus.MINIMAL) {
                        break;
                    }
                    double d0 = random.nextDouble();
                    double d1 = random.nextDouble();
                    BlockState blockstate = levelreader.getBlockState(blockpos1);
                    FluidState fluidstate = levelreader.getFluidState(blockpos1);
                    VoxelShape voxelshape = blockstate.getCollisionShape(levelreader, blockpos1);
                    double d2 = voxelshape.max(Direction.Axis.Y, d0, d1);
                    double d3 = fluidstate.getHeight(levelreader, blockpos1);
                    double d4 = Math.max(d2, d3);
                    ParticleOptions particleoptions = !fluidstate.is(FluidTags.LAVA) && !blockstate.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockstate) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
                    minecraft.level.addParticle(particleoptions, (double)blockpos1.getX() + d0, (double)blockpos1.getY() + d4, (double)blockpos1.getZ() + d1, 0.0D, 0.0D, 0.0D);
                }
            }

            if (blockpos1 != null && random.nextInt(3) < this.rainSoundTime++) {
                this.rainSoundTime = 0;
                if (blockpos1.getY() > blockpos.getY() + 1 && levelreader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos).getY() > Mth.floor((float)blockpos.getY())) {
                    minecraft.level.playLocalSound(blockpos1, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
                } else {
                    minecraft.level.playLocalSound(blockpos1, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
                }
            }
        }
    }
}
