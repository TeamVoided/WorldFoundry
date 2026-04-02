package org.teamvoided.world_foundry.data.worldgen

import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.key

object WFWorldPresets {

    val MIXED_AMPLIFIED = create("mixed_amplified")
    val CAVE_WORLD = create("cave_world")

    // (ender) this is my world type ill do things with it later okay
    val ENDIFIED = create("endified")

    fun create(id: String) = Registries.WORLD_PRESET.key(id(id))

}
