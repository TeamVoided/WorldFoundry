package org.teamvoided.world_foundry.worldgen

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.util.RandomSource
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.placement.PlacementContext
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.PlacementModifierType
import org.teamvoided.world_foundry.init.WFPlacementModifierTypes
import java.util.stream.Stream

class CountOnFixedLayersPlacement private constructor(private val count: IntProvider) : PlacementModifier() {

    override fun type(): PlacementModifierType<*> = WFPlacementModifierTypes.COUNT_ON_FIXED_LAYERS

    override fun getPositions(context: PlacementContext, random: RandomSource, pos: BlockPos): Stream<BlockPos> {
        val builder = Stream.builder<BlockPos>()
        var iteration = 0

        var shouldLoop: Boolean
        do {
            shouldLoop = false

            for (j in 0..<this.count.sample(random)) {
                val x = random.nextInt(16) + pos.x
                val z = random.nextInt(16) + pos.z
                val y = context.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z)
                val sampledY = findOnGroundYPosition(context, x, y, z, iteration)
                if (sampledY != Int.MAX_VALUE) {
                    builder.add(BlockPos(x, sampledY, z))
                    shouldLoop = true
                }
            }

            iteration++
        } while (shouldLoop)

        return builder.build()
    }


    companion object {
        val CODEC: MapCodec<CountOnFixedLayersPlacement> = IntProvider.codec(0, 256)
            .fieldOf("count")
            .xmap(::CountOnFixedLayersPlacement) { it.count }

        fun of(provider: IntProvider): CountOnFixedLayersPlacement = CountOnFixedLayersPlacement(provider)

        fun of(i: Int): CountOnFixedLayersPlacement = of(ConstantInt.of(i))

        fun findOnGroundYPosition(context: PlacementContext, x: Int, y: Int, z: Int, iteration: Int): Int {
            val mutPos = MutableBlockPos(x, y, z)
            var m = 0
            var originState = context.getBlockState(mutPos)

            for (currentY in y downTo context.minY + 1) {
                mutPos.setY(currentY - 1)
                val state = context.getBlockState(mutPos)
                if (!isEmpty(state) && isEmpty(originState) && !state.`is`(Blocks.BEDROCK)) {
                    if (m == iteration) {
                        return mutPos.y + 1
                    }

                    m++
                }

                originState = state
            }

            return Int.MAX_VALUE
        }

        fun isEmpty(blockState: BlockState): Boolean {
            return blockState.isAir || blockState.`is`(Blocks.WATER)
        }
    }
}
