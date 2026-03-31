package org.teamvoided.world_foundry.data.world

import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.key

object WFDensityFunctions {

    val AMPLIFIED_REGION = create("amplified_region")
    val NORMAL_REGION = create("normal_region")

    val TEMPERATURE_WF = create("temperature")
    val VEGETATION_WF = create("vegetation")
    val CONTINENTS_WF = create("continents")
    val EROSION_WF = create("erosion")

    val OFFSET_MIX = create("offset_mix")
    val FACTOR_MIX = create("factor_mix")
    val JAGGEDNESS_MIX = create("jaggedness_mix")
    val DEPTH_MIX = create("depth_mix")
    val SLOPED_CHEESE_MIX = create("sloped_cheese_mix")

    val INITIAL_DENSITY_WITHOUT_JAGGEDNESS = create("initial_density_without_jaggedness")
    val I_D_W_J_NORMAL = create("initial_density_without_jaggedness_normal")
    val I_D_W_J_AMPLIFIED = create("initial_density_without_jaggedness_amplified")
    val FINAL_DENSITY_MIX = create("final_density_mix")

    fun create(id: String) = Registries.DENSITY_FUNCTION.key(id(id))

}
