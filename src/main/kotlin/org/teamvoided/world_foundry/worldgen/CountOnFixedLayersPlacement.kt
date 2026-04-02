package org.teamvoided.world_foundry.worldgen

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
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

class CountOnFixedLayersPlacement(val count: IntProvider, val heightmap: Heightmap.Types) : PlacementModifier() {

    override fun type(): PlacementModifierType<*> = WFPlacementModifierTypes.COUNT_ON_FIXED_LAYERS

    override fun getPositions(context: PlacementContext, random: RandomSource, pos: BlockPos): Stream<BlockPos> {
        val builder = Stream.builder<BlockPos>()

        val x = pos.x
        val z = pos.z
        val y = context.getHeight(this.heightmap, x, z)
        if (y > context.minY) {
            builder.add(BlockPos(x, y, z))
        }
//        return  builder.build()

        var iteration = 0

        var shouldLoop: Boolean
        do {
            shouldLoop = false

            for (j in 0..<this.count.sample(random)) {
//                val x = random.nextInt(16) + pos.x
//                val z = random.nextInt(16) + pos.z
//                val y = context.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z)
                val sampledY = findOnGroundYPosition(context, x, y - 2, z, iteration)
                if (sampledY != null) {
                    builder.add(BlockPos(x, sampledY, z))
                    shouldLoop = true
                }
            }

            iteration++
        } while (shouldLoop)

        return builder.build()
    }


    companion object {

        val CODEC: MapCodec<CountOnFixedLayersPlacement> = RecordCodecBuilder.mapCodec { instance ->
            instance
                .group(
                    IntProvider.codec(0, 256).fieldOf("count").forGetter { it.count },
                    Heightmap.Types.CODEC.fieldOf("heightmap").forGetter { it.heightmap }
                )
                .apply(instance, ::CountOnFixedLayersPlacement)
        }


        fun of(provider: IntProvider, heightmap: Heightmap.Types): CountOnFixedLayersPlacement {
            return CountOnFixedLayersPlacement(provider, heightmap)
        }

        fun of(i: Int, heightmap: Heightmap.Types) = of(ConstantInt.of(i), heightmap)

        fun findOnGroundYPosition(context: PlacementContext, x: Int, y: Int, z: Int, iteration: Int): Int? {
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

            return null
        }

        fun isEmpty(blockState: BlockState): Boolean {
            return blockState.isAir || blockState.`is`(Blocks.WATER)
        }
    }
}
