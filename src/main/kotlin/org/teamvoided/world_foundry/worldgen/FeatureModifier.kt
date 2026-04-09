package org.teamvoided.world_foundry.worldgen

import com.google.common.base.Stopwatch
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.placement.BiomeFilter
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.placement.PlacementFilter
import org.teamvoided.world_foundry.WorldFoundry.CONFIG
import org.teamvoided.world_foundry.WorldFoundry.log
import org.teamvoided.world_foundry.data.tags.WFBiomeTags
import org.teamvoided.world_foundry.data.tags.WFPlacesFeatureTags.ALLOW_UNDER_PLACEMENT
import org.teamvoided.world_foundry.data.tags.WFPlacesFeatureTags.DENY_UNDER_PLACEMENT
import org.teamvoided.world_foundry.isDev

object FeatureModifier {

    @JvmStatic
    fun featureModificationStep(access: RegistryAccess) {
        if (!CONFIG.underPlacementFixer.enabled) {
            return
        }

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
            for (step in CONFIG.underPlacementFixer.generationStepToModify) {
                for (feature in biome.generationSettings.features()[step.ordinal]) {
                    features.add(feature.unwrapKey().get())
                }
            }
        }

        return features
    }

    fun tryModifyFeature(feat: Registry<PlacedFeature>, key: ResourceKey<PlacedFeature>): Boolean {
        val holder = feat.get(key).get()

        if (!holder.`is`(ALLOW_UNDER_PLACEMENT) && holder.`is`(DENY_UNDER_PLACEMENT)) {
            return false
        }

        val feature = holder.value()

        val modifiedPlacements = feature.placement.toMutableList()
        var isModified = false

        val filters = mutableListOf<PlacementFilter>()
        for ((idx, modifier) in feature.placement.withIndex()) {
            if (modifier is PlacementFilter && modifier !is BiomeFilter) {
                filters.add(modifier)
            }
            if (modifier is HeightmapPlacement) {
                modifiedPlacements.add(idx + 1, UnderPlacementFixer.of(filters))
                isModified = true
            }
        }

        if (isModified) {
            feature.placement = modifiedPlacements.toList()
            if (isDev() || CONFIG.longModifiedFeatureNames) log.info("Modified: {}", key.identifier())
            return true
        }
        return false
    }
}
