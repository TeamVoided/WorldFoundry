@file:Suppress("unused")

package org.teamvoided.world_foundry

import com.mojang.serialization.JsonOps
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.level.levelgen.placement.BiomeFilter
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.placement.PlacementFilter
import org.teamvoided.world_foundry.WorldFoundry.log
import org.teamvoided.world_foundry.worldgen.UnderPlacementFixer


fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment

fun <V : Any, T : V> Registry<V>.register(id: Identifier, entry: T): T = Registry.register(this, id, entry)
fun <V : Any, T : V> Registry<T>.registerHolder(id: Identifier, entry: T): Holder.Reference<T> =
    Registry.registerForHolder(this, id, entry)

fun <T : Any, R : Registry<T>> ResourceKey<R>.tag(id: Identifier): TagKey<T> = TagKey.create(this, id)
fun <T : Any, R : Registry<T>> ResourceKey<R>.key(id: Identifier): ResourceKey<T> = ResourceKey.create(this, id)


fun evilFunction(frozen: RegistryAccess.Frozen) {
//    val sw = Stopwatch.createStarted()

    val feat = frozen.lookupOrThrow(Registries.PLACED_FEATURE)
    log.info("Processing features: {}", feat.size())

    val keys = feat.entrySet()
        .stream()
        .map { it.key }
        .sorted(Comparator.comparingInt { feat.getId(feat.getValueOrThrow(it)) })

    for (key in keys) {
        processFeatures(feat, key)
    }
}

fun processFeatures(feat: Registry<PlacedFeature>, key: ResourceKey<PlacedFeature>) {
    val feature = feat.getValueOrThrow(key)

    val outputList = feature.placement.toMutableList()
    var mod = false

    val filters = mutableListOf<PlacementFilter>()
    for ((idx, modifier) in feature.placement.withIndex()) {
        if (modifier is PlacementFilter && modifier !is BiomeFilter) {
            filters.add(modifier)
        }
        if (modifier is HeightmapPlacement) {
            outputList.add(idx + 1, UnderPlacementFixer.of(1, filters))
            mod = true
        }
    }

    if (mod) {
        feature.placement = outputList.toList()
        log.info("Modified: {}", key)
    }
}
