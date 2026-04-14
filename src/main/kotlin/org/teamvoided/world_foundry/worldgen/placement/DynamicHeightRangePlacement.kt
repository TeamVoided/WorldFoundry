package org.teamvoided.world_foundry.worldgen.placement

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight
import net.minecraft.world.level.levelgen.placement.PlacementContext
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.PlacementModifierType
import org.teamvoided.world_foundry.WorldFoundry.CONFIG
import org.teamvoided.world_foundry.init.WFPlacementModifierTypes.DYNAMIC_HEIGHT_RANGE
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.min

object DynamicHeightRangePlacement : PlacementModifier() {
    override fun getPositions(context: PlacementContext, random: RandomSource, pos: BlockPos): Stream<BlockPos> {
        val top = pos.y - CONFIG.underPlacementFixer.startingDepth.get()
        if (top <= context.minY) {
            return Stream.of()
        }

        val bottom = max(
            min(context.level.seaLevel, top) - CONFIG.underPlacementFixer.bottomLayerOffset.get(),
            context.minY
        )
        val range = UniformHeight.of(VerticalAnchor.absolute(bottom), VerticalAnchor.absolute(top))

        return Stream.of(pos.atY(range.sample(random, context)))
    }

    override fun type(): PlacementModifierType<*> = DYNAMIC_HEIGHT_RANGE

    val CODEC: MapCodec<DynamicHeightRangePlacement> = MapCodec.unit { DynamicHeightRangePlacement }
}
