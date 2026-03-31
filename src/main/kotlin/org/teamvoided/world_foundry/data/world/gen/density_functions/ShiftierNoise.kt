package org.teamvoided.world_foundry.data.world.gen.density_functions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.util.KeyDispatchDataCodec
import net.minecraft.world.level.levelgen.synth.NormalNoise
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.DensityFunction.*
import net.minecraft.world.level.levelgen.DensityFunctions

class ShiftierNoise(
    val shiftX: DensityFunction,
    val shiftY: DensityFunction,
    val shiftZ: DensityFunction,
    val scaleXZ: DensityFunction,
    val scaleY: DensityFunction,
    val noise: NoiseHolder
) : DensityFunction {

    constructor(
        shiftX: DensityFunction,
        shiftZ: DensityFunction,
        scaleXZ: DensityFunction,
        noise: Holder<NormalNoise.NoiseParameters>
    ) : this(
        shiftX,
        DensityFunctions.constant(0.0),
        shiftZ,
        scaleXZ,
        DensityFunctions.constant(0.0),
        NoiseHolder(noise)
    )

    constructor(
        scaleXZ: DensityFunction,
        noise: Holder<NormalNoise.NoiseParameters>
    ) : this(
        DensityFunctions.constant(0.0),
        DensityFunctions.constant(0.0),
        scaleXZ,
        noise
    )

    override fun compute(context: FunctionContext): Double {
        val d = context.blockX().toDouble() * this.scaleXZ.compute(context) + shiftX.compute(context)
        val e = context.blockY().toDouble() * this.scaleY.compute(context) + shiftY.compute(context)
        val f = context.blockZ().toDouble() * this.scaleXZ.compute(context) + shiftZ.compute(context)
        return noise.getValue(d, e, f)
    }

    override fun fillArray(array: DoubleArray, context: ContextProvider) {
        context.fillAllDirectly(array, this)
    }

    override fun mapAll(visitor: Visitor): DensityFunction {
        return visitor.apply(
            ShiftierNoise(
                shiftX.mapAll(visitor),
                shiftY.mapAll(visitor),
                shiftZ.mapAll(visitor),
                scaleXZ.mapAll(visitor),
                scaleY.mapAll(visitor),
                visitor.visitNoise(this.noise)
            )
        )
    }

    override fun minValue(): Double = -this.maxValue()

    override fun maxValue(): Double = noise.maxValue()

    override fun codec(): KeyDispatchDataCodec<out DensityFunction> = CODEC

    companion object {
        private val DATA_CODEC: MapCodec<ShiftierNoise> =
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<ShiftierNoise> ->
                instance.group(
                    HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter { it.shiftX },
                    HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter { it.shiftY },
                    HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter { it.shiftZ },
                    HOLDER_HELPER_CODEC.fieldOf("scale_xz").forGetter { it.scaleXZ },
                    HOLDER_HELPER_CODEC.fieldOf("scale_y").forGetter { it.scaleY },
                    NoiseHolder.CODEC.fieldOf("noise").forGetter { it.noise }
                ).apply(instance, ::ShiftierNoise)
            }
        val CODEC: KeyDispatchDataCodec<ShiftierNoise> = KeyDispatchDataCodec.of(DATA_CODEC)
    }
}