package org.teamvoided.world_foundry.data.tags

import net.minecraft.core.registries.Registries
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.tag

object WFPlacesFeatureTags {

    val DENY_UNDER_PLACEMENT = create("deny_under_placement")
    val ALLOW_UNDER_PLACEMENT = create("allow_under_placement")

    val SMALL_VEGETATION = create("small_vegetation")

    fun create(id: String) = Registries.PLACED_FEATURE.tag(id(id))

}
