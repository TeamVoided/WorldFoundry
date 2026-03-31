package org.teamvoided.world_foundry

import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.KeyDispatchDataCodec
import net.minecraft.world.level.levelgen.DensityFunction
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.data.worldgen.density_functions.ConfigureDensityFunction
import org.teamvoided.world_foundry.data.worldgen.density_functions.ShiftierNoise

object WFDensityFunctionTypes {

    val SHIFTIER_NOISE = register("shiftier_noise", ShiftierNoise.CODEC)
    val CONFIGURE_DENSITY_FUNCTION = register("configure_density", ConfigureDensityFunction.CODEC)

    fun init() = Unit

    fun <C : DensityFunction, F : KeyDispatchDataCodec<C>> register(id: String, densityFunction: F): MapCodec<C> {
        return BuiltInRegistries.DENSITY_FUNCTION_TYPE.register(id(id), densityFunction.codec())
    }

}