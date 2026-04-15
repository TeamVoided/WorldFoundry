package org.teamvoided.world_foundry.worldgen.placement

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DataResult.success
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.levelgen.placement.CountPlacement
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.PlacementModifierType
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement
import org.teamvoided.world_foundry.init.WFPlacementModifierTypes.WRAPPED_REPEATING
import org.teamvoided.world_foundry.mixin.RepeatingPlacementAccessor

class WrappedRepeatingPlacement(val repeatingPlacement: RepeatingPlacement, val multiplier: Int) :
    RepeatingPlacement() {
    override fun count(randomSource: RandomSource, blockPos: BlockPos): Int {
        return (repeatingPlacement as RepeatingPlacementAccessor).wf_count(randomSource, blockPos) * multiplier
    }

    override fun type(): PlacementModifierType<*> = WRAPPED_REPEATING

    companion object {
        val CODEC: MapCodec<WrappedRepeatingPlacement> = RecordCodecBuilder.mapCodec { instance ->
            instance
                .group(
                    PlacementModifier.CODEC
                        .flatXmap({
                            when (it) {
                                is WrappedRepeatingPlacement -> DataResult.error { "Can't nest WrappedRepeatingPlacement!" }
                                is RepeatingPlacement -> success(it)
                                else -> DataResult.error { "PlacementModifier needs to be a RepeatingPlacement!" }
                            }
                        }, DataResult<RepeatingPlacement>::success)
                        .orElse(CountPlacement.of(1))
                        .fieldOf("repeating_placement").forGetter { it.repeatingPlacement },
                    Codec.intRange(1, 128).fieldOf("multiplier").forGetter { it.multiplier }
                )
                .apply(instance, ::WrappedRepeatingPlacement)
        }

        fun of(placement: RepeatingPlacement, multiplier: Int) = WrappedRepeatingPlacement(placement, multiplier)
    }
}