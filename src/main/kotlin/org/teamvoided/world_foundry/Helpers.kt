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
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight
import net.minecraft.world.level.levelgen.placement.BiomeFilter
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import org.teamvoided.world_foundry.WorldFoundry.log
import org.teamvoided.world_foundry.worldgen.CountOnFixedLayersPlacement


fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment

fun <V : Any, T : V> Registry<V>.register(id: Identifier, entry: T): T = Registry.register(this, id, entry)
fun <V : Any, T : V> Registry<T>.registerHolder(id: Identifier, entry: T): Holder.Reference<T> =
    Registry.registerForHolder(this, id, entry)

fun <T : Any, R : Registry<T>> ResourceKey<R>.tag(id: Identifier): TagKey<T> = TagKey.create(this, id)
fun <T : Any, R : Registry<T>> ResourceKey<R>.key(id: Identifier): ResourceKey<T> = ResourceKey.create(this, id)


fun evilFunction(frozen: RegistryAccess.Frozen) {
//    return
//    val sw = Stopwatch.createStarted()

    val feat = frozen.lookupOrThrow(Registries.PLACED_FEATURE)
    val ops = frozen.createSerializationContext(JsonOps.INSTANCE)
    log.info("Processing features: {}", feat.size())

    val keys = feat.entrySet()
        .stream()
        .map { it.key }
        .sorted(Comparator.comparingInt { feat.getId(feat.getValueOrThrow(it)) })

    for (key in keys) {
        val feature = feat.getValueOrThrow(key)

        val otherList = feature.placement.toMutableList()
        var mod = false
        val shouldRemove = mutableListOf<PlacementModifier>()

        for (modifier in feature.placement) {
            if (modifier is HeightmapPlacement) {
                shouldRemove.add(modifier)
                mod = true
            }
        }

        if (mod) {
            otherList.addAll(getHeightPlaceMod(shouldRemove))
            if (feature.placement.contains(BiomeFilter.biome())) {
                otherList.remove(BiomeFilter.biome())
                otherList.add(BiomeFilter.biome())
            }
            feature.placement = otherList.toList()
            log.info("Modified: {}", key)
        }

    }
}

val CUSTOM_RANGE =
    HeightRangePlacement.of(TrapezoidHeight.of(VerticalAnchor.absolute(54), VerticalAnchor.absolute(90), 75))

fun getHeightPlaceMod(shouldRemove: MutableList<PlacementModifier>): List<PlacementModifier> {

    val heightmap = (shouldRemove[0] as? HeightmapPlacement)?.heightmap ?: return emptyList()

    return listOf(
        /*  EnvironmentScanPlacement.scanningFor(
              Direction.DOWN,
              BlockPredicate.solid(),
              BlockPredicate.ONLY_IN_AIR_PREDICATE,
              12
          ),*/
//        CountPlacement.of(20),
        CountOnFixedLayersPlacement.of(1, heightmap)
//        CountOnEveryLayerPlacement.of(1)
//        RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT
    )
//    return CountOnEveryLayerPlacement.of(1)
}
