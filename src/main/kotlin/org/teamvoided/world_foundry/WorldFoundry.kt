package org.teamvoided.world_foundry

import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object WorldFoundry {
    const val MODID = "world_foundry"
    val log: Logger = LoggerFactory.getLogger(WorldFoundry::class.simpleName)

    fun commonInit() {
        log.info("Founding Worlds!")
        WFDensityFunction.init()
    }

    fun id(path: String): Identifier = Identifier.of(MODID, path)
    fun mc(path: String): Identifier = Identifier.ofDefault(path)
}
