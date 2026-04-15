package org.teamvoided.world_foundry.worldgen

import com.google.common.base.Stopwatch
import net.minecraft.SharedConstants
import net.minecraft.core.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.*
import net.minecraft.world.level.levelgen.feature.FeatureCountTracker
import net.minecraft.world.level.levelgen.placement.*
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement.scanningFor
import org.apache.commons.lang3.mutable.MutableBoolean
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.teamvoided.world_foundry.WorldFoundry.CONFIG
import org.teamvoided.world_foundry.WorldFoundry.log
import org.teamvoided.world_foundry.data.tags.WFBiomeTags
import org.teamvoided.world_foundry.data.tags.WFBlockTags.WORLDGEN_REPLACEABLE
import org.teamvoided.world_foundry.data.tags.WFPlacesFeatureTags
import org.teamvoided.world_foundry.data.tags.WFPlacesFeatureTags.ALLOW_UNDER_PLACEMENT
import org.teamvoided.world_foundry.data.tags.WFPlacesFeatureTags.DENY_UNDER_PLACEMENT
import org.teamvoided.world_foundry.isDev
import org.teamvoided.world_foundry.worldgen.placement.DynamicHeightRangePlacement
import org.teamvoided.world_foundry.worldgen.placement.WrappedRepeatingPlacement
import java.util.stream.Stream

// TODO clean up
object FeatureModifier {

    val featuresToModify = mutableSetOf<PlacedFeature>()

    @JvmStatic
    fun featureModificationStep(access: RegistryAccess) {
        if (!CONFIG.underPlacementFixer.enabled) {
            return
        }

        featuresToModify.clear()
        PLACEMENT_CACHE.clear()

        val sw = Stopwatch.createStarted()
        val features = getFeaturesFromBiomes(access)

        if (isDev()) log.info("Collected features {} in {}", features.size, sw)

        if (features.isEmpty()) {
            return
        }

        val registry = access.lookupOrThrow(Registries.PLACED_FEATURE)

        var count = 0
        for (key in features) {
            if (tryModifyFeature(registry, key)) {
                count++
            }
        }
        if (count > 0) {
            log.info("Modified {} features, in {} ", count, sw)
        }
    }

    fun getFeaturesFromBiomes(access: RegistryAccess): Set<ResourceKey<PlacedFeature>> {
        val features = mutableSetOf<ResourceKey<PlacedFeature>>()
        val registry = access.lookupOrThrow(Registries.BIOME)

        for (key in registry.registryKeySet()) {
            val holder = registry.getOrThrow(key)
            if (!holder.`is`(WFBiomeTags.ALLOW_UNDER_PLACEMENT) || holder.`is`(WFBiomeTags.DENY_UNDER_PLACEMENT)) {
                continue
            }

            val biome = holder.value()
            for (step in CONFIG.underPlacementFixer.generationStepsToModify) {
                for (feature in biome.generationSettings.features()[step.ordinal]) {
                    features.add(feature.unwrapKey().get())
                }
            }
        }

        return features
    }

    fun tryModifyFeature(registry: Registry<PlacedFeature>, key: ResourceKey<PlacedFeature>): Boolean {
        val holder = registry.get(key).get()

        if (!holder.`is`(ALLOW_UNDER_PLACEMENT) && holder.`is`(DENY_UNDER_PLACEMENT)) {
            return false
        }

        val feature = holder.value()

//        val modifiedPlacements = feature.placement.toMutableList()
        var isModified = false

//        val filters = mutableListOf<PlacementFilter>()
        for (modifier in feature.placement) {
            /*  if (modifier is PlacementFilter && modifier !is BiomeFilter) {
                  filters.add(modifier)
              }*/
            if (modifier is HeightmapPlacement) {
//                modifiedPlacements.add(idx + 1, UnderPlacementFixer.of(filters))
                isModified = true
            }
        }

        if (isModified) {
//            feature.placement = modifiedPlacements.toList()
            featuresToModify.add(feature)
            if (isDev() || CONFIG.longModifiedFeatureNames) log.info("Modified: {}", key.identifier())
            return true
        }
        return false
    }

    @JvmStatic
    fun customPlaceFeature(
        feature: PlacedFeature,
        context: PlacementContext,
        random: RandomSource,
        pos: BlockPos,
        cir: CallbackInfoReturnable<Boolean>,
        placed: MutableBoolean,
    ) {
        if (!CONFIG.underPlacementFixer.enabled) {
            return
        }

        if (!featuresToModify.contains(feature)) {
            return
        }

        var stream = Stream.of(pos)
        val lookup = context.level.level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE)
        val holder = lookup.get(lookup.getResourceKey(feature).get()).get()
        val remappedPlacements = modifySecondaryPlacements(feature, holder)
        for (modifier in remappedPlacements) {
            stream = stream.flatMap { modifier.getPositions(context, random, it) }
        }

        val configuredFeature = feature.feature.value()
        val placedAdditional = MutableBoolean()
        stream.forEach {
            if (configuredFeature.place(context.level, context.generator(), random, it)) {
                placedAdditional.setTrue()
                if (SharedConstants.DEBUG_FEATURE_COUNT) {
                    FeatureCountTracker.featurePlaced(context.level.level, configuredFeature, context.topFeature())
                }
            }
        }

        if (placed.isFalse && placedAdditional.isTrue) {
            cir.returnValue = true
        }
    }

    var PLACEMENT_CACHE = mutableMapOf<PlacedFeature, List<PlacementModifier>>()

    val ENV_SCAN = scanningFor(
        Direction.DOWN,
        allOf(noFluid(), not(matchesTag(Vec3i(0, -1, 0), WORLDGEN_REPLACEABLE))),
        matchesTag(WORLDGEN_REPLACEABLE),
        CONFIG.underPlacementFixer.envScanSteps.get()
    )

    fun modifySecondaryPlacements(
        feature: PlacedFeature, holder: Holder.Reference<PlacedFeature>,
    ): List<PlacementModifier> {
        val placements = PLACEMENT_CACHE[feature]
        if (placements != null) {
            return placements
        }

        val modifiedPlacements = feature.placement.toMutableList()
        for ((idx, modifier) in feature.placement.withIndex()) {
            if (modifier is CountPlacement) {
                modifiedPlacements[idx] = WrappedRepeatingPlacement.of(modifier, getMultiplier(holder))
            }

            if (modifier is HeightmapPlacement) {
                modifiedPlacements[idx] = transformedHeightmap(modifier)
                modifiedPlacements.add(idx + 1, DynamicHeightRangePlacement)
                modifiedPlacements.add(idx + 2, ENV_SCAN)
            }
        }

        val resultList = modifiedPlacements.toList()
        PLACEMENT_CACHE[feature] = resultList
        return resultList
    }

    fun getMultiplier(holder: Holder.Reference<PlacedFeature>): Int {
        var multi = if (holder.`is`(WFPlacesFeatureTags.SMALL_VEGETATION)) {
            CONFIG.underPlacementFixer.smallVegetationCountMultiplier.get()
        } else {
            CONFIG.underPlacementFixer.countMultiplier.get()
        }
        if (CONFIG.underPlacementFixer.darkOakFeatures.contains(holder.key())) {
            multi -= CONFIG.underPlacementFixer.darkOakPenalty.get()
        }

        return multi
    }

    fun transformedHeightmap(modifier: HeightmapPlacement): HeightmapPlacement {
        return when (modifier.heightmap) {
            Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES ->
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)

            Heightmap.Types.OCEAN_FLOOR -> HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)

            else -> modifier
        }
    }

}
