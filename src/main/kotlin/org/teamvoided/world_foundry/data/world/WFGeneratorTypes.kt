package org.teamvoided.world_foundry.data.world

import net.minecraft.world.level.levelgen.presets.WorldPreset
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id

object WFGeneratorTypes {
    val MIXED_AMPLIFIED = create("mixed_amplified")
    // (ender) this is my world type ill do things with it later okay
    val ENDIFIED = create("endified")

    fun create(id: String): ResourceKey<WorldPreset> = ResourceKey.create(Registries.WORLD_PRESET, id(id))
}
