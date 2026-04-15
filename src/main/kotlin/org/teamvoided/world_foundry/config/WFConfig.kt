package org.teamvoided.world_foundry.config

import me.fzzyhmstrs.fzzy_config.annotations.Action
import me.fzzyhmstrs.fzzy_config.annotations.Comment
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.util.AllowableIdentifiers
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.placement.VegetationPlacements
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.GenerationStep.Decoration
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.teamvoided.world_foundry.WorldFoundry.MODID
import org.teamvoided.world_foundry.WorldFoundry.id
import org.teamvoided.world_foundry.key

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
            TEMPERATURE to 1.5,
            HUMIDITY to 1.5,
            EROSION to 1.0,
            CONTINENTALNESS to 1.0,
            AMPLIFIED_SIZE to 0.1,
        )
        .build()

    var underPlacementFixer = UnderPlacementFixerSection()

    class UnderPlacementFixerSection : ConfigSection() {
        @RequiresAction(Action.RESTART)
        var enabled = true

        var countMultiplier = ValidatedInt(15, 128, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var smallVegetationCountMultiplier = ValidatedInt(25, 128, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var darkOakPenalty = ValidatedInt(7, 120, 1, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var darkOakFeatures = registryKey(VegetationPlacements.PALE_MOSS_PATCH, Registries.PLACED_FEATURE)
            .toSet(
                VegetationPlacements.DARK_FOREST_VEGETATION,
                VegetationPlacements.PALE_GARDEN_VEGETATION,
            )

        fun registryKey(
            default: ResourceKey<PlacedFeature>,
            registry: ResourceKey<Registry<PlacedFeature>>,
        ): ValidatedField<ResourceKey<PlacedFeature>> {
            return ValidatedIdentifier(
                default.identifier(),
                AllowableIdentifiers({ _ -> true }, { listOf() }, true)
            ).map(registry::key, ResourceKey<PlacedFeature>::identifier)
        }

        var generationStepsToModify =
            ValidatedSet(setOf(Decoration.VEGETAL_DECORATION), ValidatedEnum(Decoration::class.java))

        @RequiresAction(Action.RESTART)
        var envScanSteps = ValidatedInt(16, 64, 2)
        var startingDepth = ValidatedInt(8, 32, 0)
        var bottomLayerOffset = ValidatedInt(40, 128, 0)
//        var stepDivider = ValidatedInt(8, 16, 0)

    }

    @Suppress("unused")
    var debug = ConfigGroup("debug", true)

    @ConfigGroup.Pop
    var longModifiedFeatureNames = false

}