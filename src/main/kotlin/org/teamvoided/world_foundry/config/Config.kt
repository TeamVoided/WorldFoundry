package org.teamvoided.world_foundry.config

import net.minecraft.util.Identifier
import org.teamvoided.world_foundry.WorldFoundry.id

object Config {
    val TEMPERATURE = id("temperature")
    val HUMIDITY = id("humidity")
    val EROSION = id("erosion")
    val CONTINENTALNESS = id("continentalness")
    val AMPLIFIED_SIZE = id("amplified_size")

    val densityConfig = mapOf<Identifier, Double>(
        TEMPERATURE to 1.0,
        HUMIDITY to 1.0,
        EROSION to 1.0,
        CONTINENTALNESS to 1.0,
    )
}