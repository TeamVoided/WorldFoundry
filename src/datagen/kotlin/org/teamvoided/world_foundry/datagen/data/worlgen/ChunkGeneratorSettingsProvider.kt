package org.teamvoided.world_foundry.datagen.data.worlgen

import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.SurfaceRuleData
import net.minecraft.world.level.biome.OverworldBiomeBuilder
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.*
import org.teamvoided.world_foundry.data.worldgen.WFDensityFunctions
import org.teamvoided.world_foundry.data.worldgen.WFNoiseSettings
import org.teamvoided.world_foundry.datagen.data.worlgen.DensityFunctionProvider.holder

object ChunkGeneratorSettingsProvider {
    fun bootstrap(c: BootstrapContext<NoiseGeneratorSettings>) {
        c.register(WFNoiseSettings.AMPLIFIED_MIXTURE, c.createOverworldMixSettings())

        c.register(
            WFNoiseSettings.CAVE_WORLD,
            NoiseGeneratorSettings(
                NoiseSettings.create(-128, 256, 1, 2),
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                NoiseRouterData.caves(c.lookup(Registries.DENSITY_FUNCTION), c.lookup(Registries.NOISE)),
                SurfaceRuleData.overworld(),
                OverworldBiomeBuilder().spawnTarget(),
                12,
                false, // disableMobGeneration
                false, // aquifersEnabled
                true, // oreVeinsEnabled
                false // useLegacyRandomSource
            )
        )

        c.register(
            WFNoiseSettings.NOISE,
            NoiseGeneratorSettings(
                NoiseSettings.OVERWORLD_NOISE_SETTINGS,
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                router(c.lookup(Registries.DENSITY_FUNCTION)),
                SurfaceRuleData.overworld(),
                OverworldBiomeBuilder().spawnTarget(),
                63,
                true, // disableMobGeneration
                true, // aquifersEnabled
                false, // oreVeinsEnabled
                false // useLegacyRandomSource
            )
        )
    }

    fun router(holder: HolderGetter<DensityFunction>): NoiseRouter {
        return NoiseRouter(
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            holder.holder(WFDensityFunctions.LAVA_CAVE),
            DensityFunctions.zero(),
            DensityFunctions.zero(),
            DensityFunctions.zero()
        )
    }

    private fun BootstrapContext<NoiseGeneratorSettings>.createOverworldMixSettings(): NoiseGeneratorSettings {
        return NoiseGeneratorSettings(
            NoiseSettings.OVERWORLD_NOISE_SETTINGS,
            Blocks.STONE.defaultBlockState(),
            Blocks.WATER.defaultBlockState(),
            DensityFunctionProvider.overworldNoiseSettingsMakerAmplifiedMixture(
                lookup(Registries.DENSITY_FUNCTION),
                lookup(Registries.NOISE)
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