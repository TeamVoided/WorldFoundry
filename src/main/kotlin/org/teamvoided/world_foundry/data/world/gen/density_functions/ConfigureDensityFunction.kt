package org.teamvoided.world_foundry.data.world.gen.density_functions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.CodecHolder
import net.minecraft.world.gen.DensityFunction
import net.minecraft.world.gen.DensityFunction.*
import org.teamvoided.world_foundry.config.Config
import java.util.*

class ConfigureDensityFunction(val configId: Identifier) : SimpleFunction {
    private val value: Double = Config.densityConfig[configId] ?: 1.0

    override fun compute(context: FunctionContext): Double = this.value

    override fun fillArray(array: DoubleArray, context: ContextProvider) {
        Arrays.fill(array, this.value)
    }

    override fun minValue(): Double = this.value

    override fun maxValue(): Double = this.value

    override fun codec(): CodecHolder<out DensityFunction> = CODEC

    companion object {
        private val DATA_CODEC: MapCodec<ConfigureDensityFunction> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Identifier.CODEC.fieldOf("config_id").forGetter { it.configId })
                    .apply(instance, ::ConfigureDensityFunction)
            }
        val CODEC: CodecHolder<ConfigureDensityFunction> = CodecHolder.method_42116(DATA_CODEC)

        //val ZERO: ConfigureDensityFunction = ConfigureDensityFunction()
    }
}