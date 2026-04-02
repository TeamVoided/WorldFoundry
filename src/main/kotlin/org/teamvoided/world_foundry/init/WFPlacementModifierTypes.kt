package org.teamvoided.world_foundry.init

import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.PlacementModifierType
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.register
import org.teamvoided.world_foundry.worldgen.CountOnFixedLayersPlacement

object WFPlacementModifierTypes {

    val COUNT_ON_FIXED_LAYERS = register("count_on_fixed_layers", CountOnFixedLayersPlacement.CODEC)

    fun init() = Unit

    fun <P : PlacementModifier> register(id: String, codec: MapCodec<P>): PlacementModifierType<P> {
        return BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.register(id(id), PlacementModifierType { codec })
    }

}