package org.teamvoided.world_foundry.data.worldgen

import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.key

object WFNoiseSettings {

    val AMPLIFIED_MIXTURE = Registries.NOISE_SETTINGS.key(id("amplified_mixture"))
    val CAVE_WORLD = Registries.NOISE_SETTINGS.key(id("cave_world"))
    val NOISE = Registries.NOISE_SETTINGS.key(id("noise"))

}
