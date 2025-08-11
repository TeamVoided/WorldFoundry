package org.teamvoided.world_foundry.data.gen.providers

import net.minecraft.registry.BootstrapContext
import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler
import org.teamvoided.world_foundry.data.world.WFNoise.AMPLIFIED_TRANSITION_NOISE

object NoiseCreator {
    fun bootstrap(c: BootstrapContext<DoublePerlinNoiseSampler.NoiseParameters>) {
        c.register(AMPLIFIED_TRANSITION_NOISE, -9, 2.0, 0.0, 2.0, 1.0, 1.0)
    }

    private fun BootstrapContext<DoublePerlinNoiseSampler.NoiseParameters>.register(
        key: RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>,
        firstOctave: Int,
        firstAmplitude: Double,
        vararg amplitudes: Double
    ) {
        this.register(key, DoublePerlinNoiseSampler.NoiseParameters(firstOctave, firstAmplitude, *amplitudes))
    }
}
