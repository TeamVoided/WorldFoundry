package org.teamvoided.world_foundry.init

import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.PlacementModifierType
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.register
import org.teamvoided.world_foundry.worldgen.placement.OffsetSquarePlacement
import org.teamvoided.world_foundry.worldgen.placement.DynamicHeightRangePlacement
import org.teamvoided.world_foundry.worldgen.placement.WrappedRepeatingPlacement

object WFPlacementModifierTypes {

    val OFFSET_SQUARE = register("offset_square", OffsetSquarePlacement.CODEC)
    val WRAPPED_REPEATING = register("wrapped_repeating", WrappedRepeatingPlacement.CODEC)
    val DYNAMIC_HEIGHT_RANGE = register("dynamic_height_range", DynamicHeightRangePlacement.CODEC)

    fun init() = Unit

    fun <P : PlacementModifier> register(id: String, codec: MapCodec<P>): PlacementModifierType<P> {
        return BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.register(id(id), PlacementModifierType { codec })
    }

}