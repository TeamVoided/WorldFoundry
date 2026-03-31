package org.teamvoided.world_foundry

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.world_foundry.config.WFConfig

@Suppress("unused")
object WorldFoundry {
    const val MODID = "world_foundry"
    val log: Logger = LoggerFactory.getLogger(WorldFoundry::class.simpleName)

    var CONFIG = ConfigApi.registerAndLoadConfig(::WFConfig)

    fun init() {
        log.info("Founding Worlds!")
        WFDensityFunctionTypes.init()
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)
    fun mc(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)
}
