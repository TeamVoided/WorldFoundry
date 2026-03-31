package org.teamvoided.world_foundry.data.gen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import java.util.concurrent.CompletableFuture

class WorldgenProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
    FabricDynamicRegistryProvider(o, r) {
    override fun getName(): String = "world_eater"

    override fun configure(reg: HolderLookup.Provider, e: Entries) {
        e.addAll(reg.lookupOrThrow(Registries.NOISE))
        e.addAll(reg.lookupOrThrow(Registries.DENSITY_FUNCTION))
        e.addAll(reg.lookupOrThrow(Registries.WORLD_PRESET))
        e.addAll(reg.lookupOrThrow(Registries.NOISE_SETTINGS))
    }
}
