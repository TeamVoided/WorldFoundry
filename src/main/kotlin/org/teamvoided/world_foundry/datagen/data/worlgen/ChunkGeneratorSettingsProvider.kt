package org.teamvoided.world_foundry.datagen.data.worlgen

import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.SurfaceRuleData
import net.minecraft.world.level.biome.OverworldBiomeBuilder
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseSettings
import org.teamvoided.world_foundry.data.world.WFNoiseSettings

object ChunkGeneratorSettingsProvider {
    fun bootstrap(c: BootstrapContext<NoiseGeneratorSettings>) {
        c.register(
            WFNoiseSettings.AMPLIFIED_MIXTURE,
            createOverworldMixSettings(c)
        )
    }

    private fun createOverworldMixSettings(
        c: BootstrapContext<*>
    ): NoiseGeneratorSettings {
        return NoiseGeneratorSettings(
            NoiseSettings.OVERWORLD_NOISE_SETTINGS,
            Blocks.STONE.defaultBlockState(),
            Blocks.WATER.defaultBlockState(),
            DensityFunctionProvider.OverworldNoiseSettingsMakerAmplifiedMixture(
                c.lookup(Registries.DENSITY_FUNCTION),
                c.lookup(Registries.NOISE)
            ),
            SurfaceRuleData.overworld(),
            OverworldBiomeBuilder().spawnTarget(),
            63,
            false,
            true,
            true,
            false
        )
    }
}