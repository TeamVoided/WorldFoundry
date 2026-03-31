package org.teamvoided.world_foundry.data.world

import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.key

object WFNoises {

    val AMPLIFIED_TRANSITION_NOISE = create("amplified_transition")

    fun create(id: String) = Registries.NOISE.key(id(id))

}
