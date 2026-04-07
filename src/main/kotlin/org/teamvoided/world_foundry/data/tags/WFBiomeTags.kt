package org.teamvoided.world_foundry.data.tags

import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.tag

object WFBiomeTags {

    val DENY_UNDER_PLACEMENT = create("deny_under_placement")

    fun create(id: String) = Registries.BIOME.tag(id(id))

}
