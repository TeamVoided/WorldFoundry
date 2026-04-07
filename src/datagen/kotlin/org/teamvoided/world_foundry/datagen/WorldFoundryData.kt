package org.teamvoided.world_foundry.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderLookup.RegistryLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry
import org.teamvoided.world_foundry.WorldFoundry.log
import org.teamvoided.world_foundry.datagen.assets.EnLangProvider
import org.teamvoided.world_foundry.datagen.data.tags.BiomeTagsProvider
import org.teamvoided.world_foundry.datagen.data.tags.BlockTagsProvider
import org.teamvoided.world_foundry.datagen.data.tags.PlacedFeatureTagsProvider
import org.teamvoided.world_foundry.datagen.data.tags.WorldPresetTagsProvider
import org.teamvoided.world_foundry.datagen.data.worlgen.ChunkGeneratorSettingsProvider
import org.teamvoided.world_foundry.datagen.data.worlgen.DensityFunctionProvider
import org.teamvoided.world_foundry.datagen.data.worlgen.NoiseCreator
import java.util.concurrent.CompletableFuture

class WorldFoundryData : DataGeneratorEntrypoint {

    override fun getEffectiveModId(): String = WorldFoundry.MODID

    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()
        log.info("Running \"${gen.modContainer.metadata.name}\" Datagen!")

        // Assets
        pack.addProvider(::EnLangProvider)
        // Data
        pack.addProvider(::BiomeTagsProvider)
        pack.addProvider(::BlockTagsProvider)
        pack.addProvider(::PlacedFeatureTagsProvider)
        pack.addProvider(::WorldPresetTagsProvider)
        pack.addProvider(::WorldgenProvider)
    }

    override fun buildRegistry(gen: RegistrySetBuilder) {
        gen.add(Registries.NOISE, NoiseCreator::bootstrap)
        gen.add(Registries.DENSITY_FUNCTION, DensityFunctionProvider::bootstrap)
        gen.add(Registries.WORLD_PRESET, WorldPresetTagsProvider::bootstrap)
        gen.add(Registries.NOISE_SETTINGS, ChunkGeneratorSettingsProvider::bootstrap)
    }

    class WorldgenProvider(o: FabricDataOutput, p: CompletableFuture<HolderLookup.Provider>) :
        FabricDynamicRegistryProvider(o, p) {
        override fun getName(): String = "Registry Gen"

        override fun configure(reg: HolderLookup.Provider, e: Entries) {
            e.addAll(reg.lookupOrThrow(Registries.NOISE))
            e.addAll(reg.lookupOrThrow(Registries.DENSITY_FUNCTION))
            e.addAll(reg.lookupOrThrow(Registries.WORLD_PRESET))
            e.addAll(reg.lookupOrThrow(Registries.NOISE_SETTINGS))
        }

        fun <T : Any> Entries.addEverything(registry: RegistryLookup<T>): MutableList<Holder<T>> {
            return registry.listElementIds().map { add(registry, it) }.toList()
        }
    }
}
