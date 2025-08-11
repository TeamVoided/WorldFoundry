package org.teamvoided.world_foundry

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.world_foundry.config.WFConfig

@Suppress("unused")
object WorldFoundry {
    const val MODID = "world_foundry"
    val log: Logger = LoggerFactory.getLogger(WorldFoundry::class.simpleName)

    var CONFIG = ConfigApi.registerAndLoadConfig(::WFConfig)


    fun commonInit() {
        log.info("Founding Worlds!")
        WFDensityFunction.init()
    }

    fun id(path: String): Identifier = Identifier.of(MODID, path)
    fun mc(path: String): Identifier = Identifier.ofDefault(path)
}
