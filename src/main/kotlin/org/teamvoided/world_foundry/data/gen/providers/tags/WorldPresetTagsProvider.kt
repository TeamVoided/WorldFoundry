package org.teamvoided.world_foundry.data.gen.providers.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.world.level.levelgen.presets.WorldPreset
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.tags.WorldPresetTags
import net.minecraft.world.level.biome.MultiNoiseBiomeSource
import net.minecraft.world.level.biome.TheEndBiomeSource
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import org.teamvoided.world_foundry.data.world.WFChunkGeneratorSettings
import org.teamvoided.world_foundry.data.world.WFGeneratorTypes
import java.util.concurrent.CompletableFuture

class WorldPresetTagsProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider<WorldPreset>(o, Registries.WORLD_PRESET, r) {
    override fun addTags(arg: HolderLookup.Provider) {
        tag(WorldPresetTags.NORMAL)
            .add(WFGeneratorTypes.MIXED_AMPLIFIED)
    }

    companion object {
        fun bootstrap(c: BootstrapContext<WorldPreset>) {
            val dim = c.lookup(Registries.DIMENSION_TYPE)
            val cgs = c.lookup(Registries.NOISE_SETTINGS)
            val biome = c.lookup(Registries.BIOME)
            val mnbpsl = c.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
            c.register(
                WFGeneratorTypes.MIXED_AMPLIFIED,
                WorldPreset(
                    mapOf(
                        LevelStem.OVERWORLD to
                                LevelStem(
                                    dim.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                                    NoiseBasedChunkGenerator(
                                        MultiNoiseBiomeSource.createFromPreset(
                                            mnbpsl.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD),
                                        ),
                                        cgs.getOrThrow(WFChunkGeneratorSettings.AMPLIFIED_MIXTURE)
                                    )
                                ),
                        LevelStem.NETHER to LevelStem(
                            dim.getOrThrow(BuiltinDimensionTypes.NETHER),
                            NoiseBasedChunkGenerator(
                                MultiNoiseBiomeSource.createFromPreset(mnbpsl.getOrThrow(
                                    MultiNoiseBiomeSourceParameterLists.NETHER)),
                                cgs.getOrThrow(NoiseGeneratorSettings.NETHER)
                            )
                        ),
                        LevelStem.END to LevelStem(
                            dim.getOrThrow(BuiltinDimensionTypes.END),
                            NoiseBasedChunkGenerator(
                                TheEndBiomeSource.create(biome),
                                cgs.getOrThrow(NoiseGeneratorSettings.END)
                            )
                        )
                    )
                )
            )
        }
    }
}
