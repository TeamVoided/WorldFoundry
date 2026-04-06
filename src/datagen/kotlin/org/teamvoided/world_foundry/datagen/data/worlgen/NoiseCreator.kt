package org.teamvoided.world_foundry.datagen.data.worlgen

import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.synth.NormalNoise
import org.teamvoided.world_foundry.data.worldgen.WFNoises

object NoiseCreator {
    fun bootstrap(c: BootstrapContext<NormalNoise.NoiseParameters>) {
        c.register(WFNoises.AMPLIFIED_TRANSITION_NOISE, -10, 2.0, 0.0, 2.0, 1.0, 1.0)
        c.register(WFNoises.LAVA_RIVER, -7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)
    }

    private fun BootstrapContext<NormalNoise.NoiseParameters>.register(
        key: ResourceKey<NormalNoise.NoiseParameters>,
        firstOctave: Int,
        firstAmplitude: Double,
        vararg amplitudes: Double,
    ) {
        this.register(key, NormalNoise.NoiseParameters(firstOctave, firstAmplitude, *amplitudes))
    }
}