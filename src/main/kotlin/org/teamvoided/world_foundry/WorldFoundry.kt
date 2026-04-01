package org.teamvoided.world_foundry

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.fabric.api.biome.v1.BiomeModification
import net.minecraft.resources.Identifier
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

    fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)
    fun mc(path: String): Identifier = Identifier.withDefaultNamespace(path)
}
