package org.teamvoided.world_foundry.worldgen

import com.mojang.serialization.DataResult
import com.mojang.serialization.DataResult.error
import com.mojang.serialization.DataResult.success
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.*
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight
import net.minecraft.world.level.levelgen.placement.*
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement.scanningFor
import org.teamvoided.world_foundry.WorldFoundry.CONFIG
import org.teamvoided.world_foundry.data.tags.WFBlockTags.WORLDGEN_REPLACEABLE
import org.teamvoided.world_foundry.init.WFPlacementModifierTypes
import org.teamvoided.world_foundry.packLong
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class UnderPlacementFixer(val filters: List<PlacementFilter>) : PlacementModifier() {

    override fun type(): PlacementModifierType<*> = WFPlacementModifierTypes.UNDER_PLACEMENT_FIXER

    override fun getPositions(context: PlacementContext, random: RandomSource, pos: BlockPos): Stream<BlockPos> {
        val positions = mutableSetOf<BlockPos>()
        positions.add(pos)

        val top = pos.y - CONFIG.underPlacementFixer.startingDepth.get()
        if (top <= context.minY) {
            return positions.stream()
        }

        val bottom = max(
            min(context.level.seaLevel, top) - CONFIG.underPlacementFixer.bottomLayerOffset.get(),
            context.minY
        )
        val steps = abs(bottom - top) / CONFIG.underPlacementFixer.stepDivider.get()

        val modifiers = getModifierList(steps, bottom, top)

        var additionalPos = Stream.of(pos)
        for (mod in modifiers) {
            additionalPos = additionalPos.flatMap { mod.getPositions(context, random, it) }
        }

        positions.addAll(additionalPos.toList())

        return positions.stream()
    }

    companion object {

        var COUNT_CACHE = mutableMapOf<Int, CountPlacement>()
        var HEIGHT_CACHE = mutableMapOf<Long, HeightRangePlacement>()
        val ENV_SCAN = scanningFor(
            Direction.DOWN,
            allOf(noFluid(), not(matchesTag(Vec3i(0, -1, 0), WORLDGEN_REPLACEABLE))),
            matchesTag(WORLDGEN_REPLACEABLE),
            CONFIG.underPlacementFixer.envScanSteps.get()
        )

        fun UnderPlacementFixer.getModifierList(steps: Int, bottom: Int, top: Int): List<PlacementModifier> {
            var count = COUNT_CACHE[steps]
            if (count == null) {
                val place = CountPlacement.of(steps)
                COUNT_CACHE[steps] = place
                count = place
            }

            val heightIdx = packLong(bottom, top)
            var heightPlacement = HEIGHT_CACHE[heightIdx]
            if (heightPlacement == null) {
                val place = HeightRangePlacement.of(
                    UniformHeight.of(VerticalAnchor.absolute(bottom), VerticalAnchor.absolute(top))
                )
                HEIGHT_CACHE[heightIdx] = place
                heightPlacement = place
            }

            return listOf(
                count,
//                OffsetSquarePlacement.spread(),
                heightPlacement,
                ENV_SCAN
            ) + filters
        }

        val CODEC: MapCodec<UnderPlacementFixer> = RecordCodecBuilder.mapCodec { instance ->
            instance
                .group(
                    PlacementModifier.CODEC
                        .flatXmap({
                            if (it is PlacementFilter) success(it)
                            else error { "PlacementModifier needs to be a filter!" }
                        }, DataResult<PlacementFilter>::success)
                        .listOf().fieldOf("filters").forGetter { it.filters },
                )
                .apply(instance, ::UnderPlacementFixer)
        }

        fun of(filters: List<PlacementFilter>): UnderPlacementFixer = UnderPlacementFixer(filters)

    }
}
