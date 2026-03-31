package org.teamvoided.world_foundry.config

import me.fzzyhmstrs.fzzy_config.annotations.Comment
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.resources.Identifier
import org.teamvoided.world_foundry.WorldFoundry.MODID
import org.teamvoided.world_foundry.WorldFoundry.id

class WFConfig : Config(id(MODID)) {

    companion object {

        val TEMPERATURE = id("temperature")
        val HUMIDITY = id("humidity")
        val EROSION = id("erosion")
        val CONTINENTALNESS = id("continentalness")
        val AMPLIFIED_SIZE = id("amplified_size")

    }

    @Comment("DO NOT REMOVE ANY OF THEM FROM THE LIST, THINGS WILL BREAK. Smaller values mean smaller biomes. 0 means the whole world will be the same.")
    var densityConfig = ValidatedMap.Builder<Identifier, Double>()
        .keyHandler(ValidatedIdentifier())
        .valueHandler(ValidatedDouble(1.0, 256.0, 0.0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS))
        .defaults(
            TEMPERATURE to 1.0,
            HUMIDITY to 1.0,
            EROSION to 1.0,
            CONTINENTALNESS to 1.0,
            AMPLIFIED_SIZE to 1.0,
        )
        .build()

}