package org.teamvoided.world_foundry.datagen.data.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import org.teamvoided.world_foundry.data.tags.WFBlockTags
import java.util.concurrent.CompletableFuture

class BlockTagsProvider(o: FabricDataOutput, p: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.BlockTagProvider(o, p) {

    override fun addTags(arg: HolderLookup.Provider) {
        builder(WFBlockTags.WORLDGEN_REPLACEABLE)
            .forceAddTag(BlockTags.REPLACEABLE)
            .forceAddTag(BlockTags.REPLACEABLE_BY_TREES)
    }

}
