package org.teamvoided.world_foundry.data.world

import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler
import org.teamvoided.world_foundry.WorldFoundry.id

object WFNoise {
    val AMPLIFIED_TRANSITION_NOISE = create("amplified_transition")

    fun create(id: String): RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> =
        RegistryKey.of(RegistryKeys.NOISE_PARAMETERS, id(id))
}
