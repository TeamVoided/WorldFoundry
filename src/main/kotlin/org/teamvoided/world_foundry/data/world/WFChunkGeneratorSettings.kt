package org.teamvoided.world_foundry.data.world

import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import org.teamvoided.world_foundry.WorldFoundry.id

object WFChunkGeneratorSettings {
    val AMPLIFIED_MIXTURE: ResourceKey<NoiseGeneratorSettings> = ResourceKey.create(Registries.NOISE_SETTINGS, id("amplified_mixture"))
}
