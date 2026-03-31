package org.teamvoided.world_foundry.data.gen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.registries.Registries
import net.minecraft.core.RegistrySetBuilder
import org.teamvoided.world_foundry.WorldFoundry.log
import org.teamvoided.world_foundry.data.gen.providers.ChunkGeneratorSettingsProvider
import org.teamvoided.world_foundry.data.gen.providers.DensityFunctionProvider
import org.teamvoided.world_foundry.data.gen.providers.EnLangProvider
import org.teamvoided.world_foundry.data.gen.providers.NoiseCreator
import org.teamvoided.world_foundry.data.gen.providers.tags.WorldPresetTagsProvider

class WorldFoundryData : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        log.info("Hello from DataGen")
        val pack = gen.createPack()

        pack.addProvider(::WorldgenProvider)
        pack.addProvider(::WorldPresetTagsProvider)
        pack.addProvider(::EnLangProvider)
    }

    override fun buildRegistry(gen: RegistrySetBuilder) {
        gen.add(Registries.NOISE, NoiseCreator::bootstrap)
        gen.add(Registries.DENSITY_FUNCTION, DensityFunctionProvider::bootstrap)
        gen.add(Registries.WORLD_PRESET, WorldPresetTagsProvider::bootstrap)
        gen.add(Registries.NOISE_SETTINGS, ChunkGeneratorSettingsProvider::bootstrap)
    }
}
