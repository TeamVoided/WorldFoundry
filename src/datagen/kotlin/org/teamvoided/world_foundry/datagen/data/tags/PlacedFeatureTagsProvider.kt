package org.teamvoided.world_foundry.datagen.data.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.placement.AquaticPlacements
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements
import net.minecraft.data.worldgen.placement.VegetationPlacements
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.teamvoided.world_foundry.data.tags.WFPlacesFeatureTags
import java.util.concurrent.CompletableFuture

class PlacedFeatureTagsProvider(o: FabricDataOutput, p: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider<PlacedFeature>(o, Registries.PLACED_FEATURE, p) {

    override fun addTags(arg: HolderLookup.Provider) {
        builder(WFPlacesFeatureTags.DENY_UNDER_PLACEMENT)
            .add(
                AquaticPlacements.SEAGRASS_WARM,
                AquaticPlacements.SEAGRASS_NORMAL,
                AquaticPlacements.SEAGRASS_COLD,
                AquaticPlacements.SEAGRASS_RIVER,
                AquaticPlacements.SEAGRASS_SWAMP,
                AquaticPlacements.SEAGRASS_DEEP_WARM,
                AquaticPlacements.SEAGRASS_DEEP,
                AquaticPlacements.SEAGRASS_DEEP_COLD,
                AquaticPlacements.SEA_PICKLE,
                AquaticPlacements.KELP_COLD,
                AquaticPlacements.KELP_WARM,
                AquaticPlacements.WARM_OCEAN_VEGETATION,
            )
            .add(MiscOverworldPlacements.LAKE_LAVA_SURFACE)
            .add(
                VegetationPlacements.FLOWER_FOREST_FLOWERS,
                VegetationPlacements.FOREST_FLOWERS,
            )


        builder(WFPlacesFeatureTags.SMALL_VEGETATION)
            .add(
                VegetationPlacements.FLOWER_DEFAULT,
                VegetationPlacements.PATCH_GRASS_BADLANDS,
                VegetationPlacements.BROWN_MUSHROOM_NORMAL,
                VegetationPlacements.RED_MUSHROOM_NORMAL,
                VegetationPlacements.PATCH_PUMPKIN,
                VegetationPlacements.PATCH_SUGAR_CANE,
                VegetationPlacements.PATCH_FIREFLY_BUSH_NEAR_WATER,
                VegetationPlacements.PATCH_BUSH,
                VegetationPlacements.PATCH_GRASS_FOREST,
                VegetationPlacements.WILDFLOWERS_BIRCH_FOREST,
                VegetationPlacements.PATCH_LEAF_LITTER,
                VegetationPlacements.PATCH_LARGE_FERN,
                VegetationPlacements.PATCH_GRASS_TAIGA,
                VegetationPlacements.PATCH_DEAD_BUSH,
                VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH,
                VegetationPlacements.RED_MUSHROOM_OLD_GROWTH,
                VegetationPlacements.PATCH_BERRY_COMMON,
                VegetationPlacements.BAMBOO_LIGHT,
                VegetationPlacements.FLOWER_WARM,
                VegetationPlacements.PATCH_GRASS_JUNGLE,
                VegetationPlacements.PATCH_MELON,
                VegetationPlacements.FLOWER_FLOWER_FOREST,
                VegetationPlacements.PATCH_TALL_GRASS,
                VegetationPlacements.PATCH_GRASS_SAVANNA,
                VegetationPlacements.PATCH_TALL_GRASS_2,
                VegetationPlacements.PALE_MOSS_PATCH,
                VegetationPlacements.PALE_GARDEN_FLOWERS,
                VegetationPlacements.FLOWER_PALE_GARDEN,
                VegetationPlacements.FLOWER_SWAMP,
                VegetationPlacements.PATCH_GRASS_NORMAL,
                VegetationPlacements.PATCH_WATERLILY,
//                VegetationPlacements.BROWN_MUSHROOM_SWAMP,
//                VegetationPlacements.RED_MUSHROOM_SWAMP,
                VegetationPlacements.PATCH_SUGAR_CANE_SWAMP,
                VegetationPlacements.PATCH_FIREFLY_BUSH_SWAMP,
                VegetationPlacements.PATCH_FIREFLY_BUSH_NEAR_WATER_SWAMP,
                VegetationPlacements.PATCH_DRY_GRASS_BADLANDS,
                VegetationPlacements.PATCH_DEAD_BUSH_BADLANDS,
                VegetationPlacements.PATCH_SUGAR_CANE_BADLANDS,
                VegetationPlacements.PATCH_CACTUS_DECORATED,
                VegetationPlacements.BAMBOO,
                VegetationPlacements.BAMBOO_VEGETATION,
                VegetationPlacements.FLOWER_PLAINS,
                VegetationPlacements.PATCH_GRASS_PLAIN,
                VegetationPlacements.MUSHROOM_ISLAND_VEGETATION,
                VegetationPlacements.BROWN_MUSHROOM_TAIGA,
                VegetationPlacements.RED_MUSHROOM_TAIGA,
                VegetationPlacements.PATCH_GRASS_TAIGA_2,
                VegetationPlacements.PATCH_BERRY_RARE,
                VegetationPlacements.PATCH_MELON_SPARSE,
                VegetationPlacements.PATCH_GRASS_MEADOW,
                VegetationPlacements.FLOWER_MEADOW,
                VegetationPlacements.WILDFLOWERS_MEADOW,
                VegetationPlacements.PATCH_DRY_GRASS_DESERT,
                VegetationPlacements.PATCH_DEAD_BUSH_2,
                VegetationPlacements.PATCH_SUGAR_CANE_DESERT,
                VegetationPlacements.PATCH_CACTUS_DESERT,
                VegetationPlacements.FLOWER_CHERRY,
                VegetationPlacements.PATCH_SUNFLOWER,
            )

    }
}
