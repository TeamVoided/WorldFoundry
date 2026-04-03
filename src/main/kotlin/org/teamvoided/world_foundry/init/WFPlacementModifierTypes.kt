package org.teamvoided.world_foundry.init

import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.PlacementModifierType
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.register
import org.teamvoided.world_foundry.worldgen.UnderPlacementFixer

object WFPlacementModifierTypes {

    val UNDER_PLACEMENT_FIXER = register("under_placement_fixer", UnderPlacementFixer.CODEC)

    fun init() = Unit

    fun <P : PlacementModifier> register(id: String, codec: MapCodec<P>): PlacementModifierType<P> {
        return BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.register(id(id), PlacementModifierType { codec })
    }

}