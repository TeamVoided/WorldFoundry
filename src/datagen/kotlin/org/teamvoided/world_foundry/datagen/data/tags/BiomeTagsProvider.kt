package org.teamvoided.world_foundry.datagen.data.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.biome.Biome
import org.teamvoided.world_foundry.data.tags.WFBiomeTags
import java.util.concurrent.CompletableFuture

class BiomeTagsProvider(o: FabricDataOutput, p: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider<Biome>(o, Registries.BIOME, p) {

    override fun addTags(arg: HolderLookup.Provider) {
        builder(WFBiomeTags.DENY_UNDER_PLACEMENT)
            .forceAddTag(ConventionalBiomeTags.IS_OCEAN)
    }
}
