package org.teamvoided.world_foundry.worldgen

import com.mojang.serialization.DataResult
import com.mojang.serialization.DataResult.error
import com.mojang.serialization.DataResult.success
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight
import net.minecraft.world.level.levelgen.placement.*
import org.teamvoided.world_foundry.init.WFPlacementModifierTypes
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class UnderPlacementFixer(val count: IntProvider, val filters: List<PlacementFilter>) : PlacementModifier() {

    override fun type(): PlacementModifierType<*> = WFPlacementModifierTypes.UNDER_PLACEMENT_FIXER

    override fun getPositions(context: PlacementContext, random: RandomSource, pos: BlockPos): Stream<BlockPos> {
        val set = mutableSetOf<BlockPos>()
        set.add(pos)

        val top = pos.y - 5
        if (top <= context.minY) {
            return set.stream()
        }
        val bottom = min(context.level.seaLevel - 32, max(top - 32, context.minY))

        val height =
            UniformHeight.of(VerticalAnchor.absolute(bottom), VerticalAnchor.absolute(top))

        val steps = (abs(bottom - top) / 1.1).toInt()

        val envScan = EnvironmentScanPlacement.scanningFor(
            Direction.DOWN,
            BlockPredicate.noFluid(),
            BlockPredicate.matchesTag(BlockTags.REPLACEABLE),
            32
        )

        val list = listOf(
            CountPlacement.of(steps),
            InSquarePlacement.spread(),
            HeightRangePlacement.of(height),
            envScan
        )

        val pos2 = ChunkPos(pos).worldPosition

        var extraStream = Stream.of(pos2)
        for (mod in list + filters) {
            extraStream = extraStream.flatMap { mod.getPositions(context, random, it) }
        }

        set.addAll(extraStream.toList())

        return set.stream()
    }


    companion object {

        val CODEC: MapCodec<UnderPlacementFixer> = RecordCodecBuilder.mapCodec { instance ->
            instance
                .group(
                    IntProvider.codec(0, 256).fieldOf("count").forGetter { it.count },
                    PlacementModifier.CODEC
                        .flatXmap({
                            if (it is PlacementFilter) success(it)
                            else error { "PlacementModifier needs to be a filter!" }
                        }, DataResult<PlacementFilter>::success)
                        .listOf().fieldOf("filters").forGetter { it.filters },
                )
                .apply(instance, ::UnderPlacementFixer)
        }

        fun of(provider: IntProvider, filters: List<PlacementFilter>): UnderPlacementFixer {
            return UnderPlacementFixer(provider, filters)
        }

        fun of(i: Int, filters: List<PlacementFilter>): UnderPlacementFixer {
            return of(ConstantInt.of(i), filters)
        }
    }
}
