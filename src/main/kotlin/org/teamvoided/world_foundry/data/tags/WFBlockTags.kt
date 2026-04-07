package org.teamvoided.world_foundry.data.tags

import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.tag

object WFBlockTags {

    val WORLDGEN_REPLACEABLE = create("worldgen_replaceable")

    fun create(id: String) = Registries.BLOCK.tag(id(id))

}
