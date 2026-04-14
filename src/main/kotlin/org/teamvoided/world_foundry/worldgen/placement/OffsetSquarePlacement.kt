package org.teamvoided.world_foundry.worldgen.placement

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.placement.PlacementContext
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import org.teamvoided.world_foundry.init.WFPlacementModifierTypes
import java.util.stream.Stream

class OffsetSquarePlacement : PlacementModifier() {

    override fun type() = WFPlacementModifierTypes.OFFSET_SQUARE

    override fun getPositions(context: PlacementContext, random: RandomSource, blockPos: BlockPos): Stream<BlockPos> {
        val chunkPos = ChunkPos(blockPos)
        var pos = blockPos

        if (!chunkPos.contains(pos.north())) {
            pos = pos.south()
        } else if (!chunkPos.contains(pos.south())) {
            pos = pos.north()
        }

        if (!chunkPos.contains(pos.east())) {
            pos = pos.west()
        } else if (!chunkPos.contains(pos.west())) {
            pos = pos.east()
        }

        return Stream.of(BlockPos(random.nextInt(2) + pos.x, pos.y, random.nextInt(2) + pos.z))
    }

    companion object {

        val INSTANCE = OffsetSquarePlacement()

        val CODEC: MapCodec<OffsetSquarePlacement> = MapCodec.unit { INSTANCE }

        @Suppress("unused")
        fun spread(): OffsetSquarePlacement = INSTANCE

    }
}