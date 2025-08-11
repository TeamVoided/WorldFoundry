package org.teamvoided.world_foundry

import com.mojang.serialization.MapCodec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.dynamic.CodecHolder
import net.minecraft.world.gen.DensityFunction
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.data.world.gen.density_functions.ConfigureDensityFunction
import org.teamvoided.world_foundry.data.world.gen.density_functions.ShiftierNoise

object WFDensityFunction {

    val SHIFTIER_NOISE = register("shiftier_noise", ShiftierNoise.CODEC)
    val CONFIGURE_DENSITY_FUNCTION = register("configure_density", ConfigureDensityFunction.CODEC)

    fun init() {}
    private fun <C : DensityFunction, F : CodecHolder<C>> register(id: String, densityFunction: F): MapCodec<C> =
        Registry.register(Registries.DENSITY_FUNCTION, id(id), densityFunction.codec())
}