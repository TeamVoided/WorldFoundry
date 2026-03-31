package org.teamvoided.world_foundry.data.world

import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.levelgen.synth.NormalNoise
import org.teamvoided.world_foundry.WorldFoundry.id

object WFNoises {
    val AMPLIFIED_TRANSITION_NOISE = create("amplified_transition")

    fun create(id: String): ResourceKey<NormalNoise.NoiseParameters> =
        ResourceKey.create(Registries.NOISE, id(id))
}
