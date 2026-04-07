package org.teamvoided.world_foundry.datagen.data.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.placement.AquaticPlacements
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.teamvoided.world_foundry.data.tags.WFPlacesFeatureTags
import java.util.concurrent.CompletableFuture

class PlacedFeatureTagsProvider(o: FabricDataOutput, p: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider<PlacedFeature>(o, Registries.PLACED_FEATURE, p) {

    override fun addTags(arg: HolderLookup.Provider) {
        builder(WFPlacesFeatureTags.DENY_UNDER_PLACEMENT)
            .add(AquaticPlacements.SEAGRASS_WARM)
            .add(AquaticPlacements.SEAGRASS_NORMAL)
            .add(AquaticPlacements.SEAGRASS_COLD)
            .add(AquaticPlacements.SEAGRASS_RIVER)
            .add(AquaticPlacements.SEAGRASS_SWAMP)
            .add(AquaticPlacements.SEAGRASS_DEEP_WARM)
            .add(AquaticPlacements.SEAGRASS_DEEP)
            .add(AquaticPlacements.SEAGRASS_DEEP_COLD)
            .add(AquaticPlacements.SEA_PICKLE)
            .add(AquaticPlacements.KELP_COLD)
            .add(AquaticPlacements.KELP_WARM)
            .add(AquaticPlacements.WARM_OCEAN_VEGETATION)
            .add(MiscOverworldPlacements.LAKE_LAVA_SURFACE)

    }
}
