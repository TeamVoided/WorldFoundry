@file:Suppress("unused")

package org.teamvoided.world_foundry

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import kotlin.jvm.optionals.getOrNull


fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment

fun <V : Any, T : V> Registry<V>.register(id: Identifier, entry: T): T = Registry.register(this, id, entry)
fun <V : Any, T : V> Registry<T>.registerHolder(id: Identifier, entry: T): Holder.Reference<T> =
    Registry.registerForHolder(this, id, entry)

fun <T : Any, R : Registry<T>> ResourceKey<R>.tag(id: Identifier): TagKey<T> = TagKey.create(this, id)
fun <T : Any, R : Registry<T>> ResourceKey<R>.key(id: Identifier): ResourceKey<T> = ResourceKey.create(this, id)


fun evilFunction(frozen: RegistryAccess.Frozen) {
//    val sw = Stopwatch.createStarted()

    val feat = frozen.lookupOrThrow(Registries.PLACED_FEATURE)
    println("Processing features: ${feat.size()}")

    val keys = feat.entrySet().stream()
        .map { it.key }
        .sorted(Comparator.comparingInt { feat.getId(feat.getValueOrThrow(it)) })
        .toList()

    for (key in keys) {
        val feature = feat.getOptional(key).getOrNull() ?: continue

        val otherList = feature.placement.toMutableList()
        var mod = false
        if (feature.placement.contains(PlacementUtils.HEIGHTMAP)) {
            otherList.remove(PlacementUtils.HEIGHTMAP)
            mod = true
        }
        if (feature.placement.contains(PlacementUtils.HEIGHTMAP_NO_LEAVES)) {
            otherList.remove(PlacementUtils.HEIGHTMAP_NO_LEAVES)
            mod = true
        }
        if (feature.placement.contains(PlacementUtils.HEIGHTMAP_TOP_SOLID)) {
            otherList.remove(PlacementUtils.HEIGHTMAP_TOP_SOLID)
            mod = true
        }
        if (feature.placement.contains(PlacementUtils.HEIGHTMAP_WORLD_SURFACE)) {
            otherList.remove(PlacementUtils.HEIGHTMAP_WORLD_SURFACE)
            mod = true
        }
        if (feature.placement.contains(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR)) {
            otherList.remove(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR)
            mod = true
        }

        if (mod) {
            otherList.add(PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT)
            feature.placement = otherList.toList()
        }

    }
}