package org.teamvoided.world_foundry.data.gen.providers

import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.synth.NormalNoise
import org.teamvoided.world_foundry.data.world.WFNoises.AMPLIFIED_TRANSITION_NOISE

object NoiseCreator {
    fun bootstrap(c: BootstrapContext<NormalNoise.NoiseParameters>) {
        c.register(AMPLIFIED_TRANSITION_NOISE, -9, 2.0, 0.0, 2.0, 1.0, 1.0)
    }

    private fun BootstrapContext<NormalNoise.NoiseParameters>.register(
        key: ResourceKey<NormalNoise.NoiseParameters>,
        firstOctave: Int,
        firstAmplitude: Double,
        vararg amplitudes: Double
    ) {
        this.register(key, NormalNoise.NoiseParameters(firstOctave, firstAmplitude, *amplitudes))
    }
}
