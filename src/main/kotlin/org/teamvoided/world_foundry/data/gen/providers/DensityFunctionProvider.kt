package org.teamvoided.world_foundry.data.gen.providers

import net.minecraft.registry.*
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler.NoiseParameters
import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.gen.DensityFunction
import net.minecraft.world.gen.DensityFunctions
import net.minecraft.world.gen.DensityFunctions.*
import net.minecraft.world.gen.OreVeinCreator.VeinType
import net.minecraft.world.gen.noise.NoiseParametersKeys
import net.minecraft.world.gen.noise.NoiseRouter
import net.minecraft.world.gen.noise.NoiseRouterData
import org.teamvoided.world_foundry.config.Config
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.AMPLIFIED_REGION
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.FINAL_DENSITY_MIX
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.CONTINENTS_WF
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.DEPTH_MIX
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.EROSION_WF
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.FACTOR_MIX
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.INITIAL_DENSITY_WITHOUT_JAGGEDNESS
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.I_D_W_J_AMPLIFIED
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.I_D_W_J_NORMAL
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.JAGGEDNESS_MIX
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.NORMAL_REGION
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.OFFSET_MIX
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.SLOPED_CHEESE_MIX
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.TEMPERATURE_WF
import org.teamvoided.world_foundry.data.world.WFDensityFunctions.VEGETATION_WF
import org.teamvoided.world_foundry.data.world.WFNoise.AMPLIFIED_TRANSITION_NOISE
import org.teamvoided.world_foundry.data.world.gen.density_functions.ConfigureDensityFunction
import org.teamvoided.world_foundry.data.world.gen.density_functions.ShiftierNoise
import java.util.stream.Stream

object DensityFunctionProvider {
    fun bootstrap(c: BootstrapContext<DensityFunction>) {
        val noiseParams = c.getRegistryLookup(RegistryKeys.NOISE_PARAMETERS)
        val densityFuns = c.getRegistryLookup(RegistryKeys.DENSITY_FUNCTION)
        val shiftX = denseHold(densityFuns, NoiseRouterData.SHIFT_X)
        val shiftZ = denseHold(densityFuns, NoiseRouterData.SHIFT_Z)
        c.register(
            AMPLIFIED_REGION,
            cache2D(
                multiply(
                    constant(3.0),
                    noise(
                        noiseParams.getHolderOrThrow(AMPLIFIED_TRANSITION_NOISE),
                        0.25, 0.0
                    )
                ).clamp(0.0, 1.0)
            )
        )
        c.register(
            NORMAL_REGION,
            cache2D(
                multiply(
                    constant(-1.0),
                    add(
                        constant(-1.0),
                        denseHold(densityFuns, AMPLIFIED_REGION)
                    )
                )
            )
        )

        c.register(
            TEMPERATURE_WF, ShiftierNoise(
                shiftX,
                shiftZ,
                multiply(constant(0.25), ConfigureDensityFunction(Config.TEMPERATURE)),
                noiseParams.getHolderOrThrow(NoiseParametersKeys.TEMPERATURE)
            )
        )
        c.register(
            VEGETATION_WF, ShiftierNoise(
                shiftX,
                shiftZ,
                multiply(constant(0.25), ConfigureDensityFunction(Config.HUMIDITY)),
                noiseParams.getHolderOrThrow(NoiseParametersKeys.VEGETATION)
            )
        )
        c.register(
            CONTINENTS_WF, ShiftierNoise(
                shiftX,
                shiftZ,
                multiply(constant(0.25), ConfigureDensityFunction(Config.CONTINENTALNESS)),
                noiseParams.getHolderOrThrow(NoiseParametersKeys.CONTINENTALNESS)
            )
        )
        c.register(
            EROSION_WF, ShiftierNoise(
                shiftX,
                shiftZ,
                multiply(constant(0.25), ConfigureDensityFunction(Config.EROSION)),
                noiseParams.getHolderOrThrow(NoiseParametersKeys.EROSION)
            )
        )


        val slopedCheeseFunction = denseHold(densityFuns, SLOPED_CHEESE_MIX)
        val caveEntrancesFunction = min(
            slopedCheeseFunction,
            multiply(
                constant(5.0),
                denseHold(densityFuns, NoiseRouterData.CAVES_ENTRANCES_OVERWORLD)
            )
        )
        val cavesMainFunction = rangeChoice(
            slopedCheeseFunction,
            -1000000.0,
            1.5625,
            caveEntrancesFunction,
            NoiseRouterData.underground(densityFuns, noiseParams, slopedCheeseFunction)
        )
        c.register(
            FINAL_DENSITY_MIX,
            min(
                NoiseRouterData.postProcess(
                    transitionAmplified(
                        densityFuns,
                        NoiseRouterData.surfaceSlide(
                            false,
                            cavesMainFunction
                        ),
                        NoiseRouterData.surfaceSlide(
                            true,
                            cavesMainFunction
                        )
                    )
                ), denseHold(densityFuns, NoiseRouterData.CAVES_NOODLE_OVERWORLD)
            )
        )



        c.register(
            INITIAL_DENSITY_WITHOUT_JAGGEDNESS,
            transitionAmplified(
                densityFuns,
                denseHold(densityFuns, I_D_W_J_NORMAL),
                denseHold(densityFuns, I_D_W_J_AMPLIFIED)
            )
        )

        val idwjFlesh = add(
            NoiseRouterData.noiseGradientDensity(
                cache2D(denseHold(densityFuns, FACTOR_MIX)),
                denseHold(densityFuns, DEPTH_MIX)
            ),
            constant(-0.703125)
        ).clamp(-64.0, 64.0)
        c.register(
            I_D_W_J_NORMAL,
            initialDensityWithoutJaggedness(
                false,
                idwjFlesh
            )
        )
        c.register(
            I_D_W_J_AMPLIFIED,
            initialDensityWithoutJaggedness(
                true,
                idwjFlesh
            )
        )

        createGenerationSplinesAmplifiedMixture(
            c,
            densityFuns,
            noise(noiseParams.getHolderOrThrow(NoiseParametersKeys.JAGGED), 1500.0, 0.0),
            densityFuns.getHolderOrThrow(CONTINENTS_WF),
            densityFuns.getHolderOrThrow(EROSION_WF),
            OFFSET_MIX,
            FACTOR_MIX,
            JAGGEDNESS_MIX,
            DEPTH_MIX,
            SLOPED_CHEESE_MIX
        )
    }

    private fun createGenerationSplinesAmplifiedMixture(
        c: BootstrapContext<DensityFunction>,
        densityFuns: HolderProvider<DensityFunction>,
        jaggedNoise: DensityFunction,
        continents: Holder<DensityFunction>,
        erosion: Holder<DensityFunction>,
        offset: RegistryKey<DensityFunction>,
        factor: RegistryKey<DensityFunction>,
        jaggedness: RegistryKey<DensityFunction>,
        depth: RegistryKey<DensityFunction>,
        cheese: RegistryKey<DensityFunction>
    ) {
        val continentsNoise = Spline.FunctionWrapper(continents)
        val erosionNoise = Spline.FunctionWrapper(erosion)
        val ridgesNoise =
            Spline.FunctionWrapper(densityFuns.getHolderOrThrow(NoiseRouterData.RIDGES_OVERWORLD))
        val ridgesFoldedNoise =
            Spline.FunctionWrapper(densityFuns.getHolderOrThrow(NoiseRouterData.RIDGES_FOLDED_OVERWORLD))
        val offsetSpline = NoiseRouterData.registerAndWrap(
            c,
            offset,
            NoiseRouterData.splineWithBlending(
                add(
                    constant(-0.50375),
                    transitionAmplified(
                        densityFuns,
                        copySpline(
                            VanillaTerrainParametersCreator.method_42056(
                                continentsNoise,
                                erosionNoise,
                                ridgesFoldedNoise,
                                false
                            )
                        ),
                        copySpline(
                            VanillaTerrainParametersCreator.method_42056(
                                continentsNoise,
                                erosionNoise,
                                ridgesFoldedNoise,
                                true
                            )
                        )
                    )
                ), getBlendOffset()
            )
        )
        val factorSpline = NoiseRouterData.registerAndWrap(
            c,
            factor,
            NoiseRouterData.splineWithBlending(
                transitionAmplified(
                    densityFuns,
                    copySpline(
                        VanillaTerrainParametersCreator.method_42055(
                            continentsNoise,
                            erosionNoise,
                            ridgesNoise,
                            ridgesFoldedNoise,
                            false
                        )
                    ),
                    copySpline(
                        VanillaTerrainParametersCreator.method_42055(
                            continentsNoise,
                            erosionNoise,
                            ridgesNoise,
                            ridgesFoldedNoise,
                            true
                        )
                    )
                ), NoiseRouterData.BLENDING_FACTOR
            )
        )
        val depthFunction = NoiseRouterData.registerAndWrap(
            c,
            depth,
            add(clampedGradientY(-64, 320, 1.5, -1.5), offsetSpline)
        )
        val jaggednessSpline = NoiseRouterData.registerAndWrap(
            c,
            jaggedness,
            NoiseRouterData.splineWithBlending(
                transitionAmplified(
                    densityFuns,
                    copySpline(
                        VanillaTerrainParametersCreator.method_42058(
                            continentsNoise,
                            erosionNoise,
                            ridgesNoise,
                            ridgesFoldedNoise,
                            false
                        )
                    ),
                    copySpline(
                        VanillaTerrainParametersCreator.method_42058(
                            continentsNoise,
                            erosionNoise,
                            ridgesNoise,
                            ridgesFoldedNoise,
                            true
                        )
                    )
                ), NoiseRouterData.BLENDING_JAGGEDNESS
            )
        )
        val jagged = multiply(jaggednessSpline, jaggedNoise.halfNegative())
        val depthAndJaggedness = NoiseRouterData.noiseGradientDensity(
            factorSpline,
            add(depthFunction, jagged)
        )
        c.register(
            cheese,
            add(
                depthAndJaggedness,
                denseHold(densityFuns, NoiseRouterData.BASE_3D_NOISE_OVERWORLD)
            )
        )
    }

    fun OverworldNoiseSettingsMakerAmplifiedMixture(
        densityFuns: HolderProvider<DensityFunction>,
        noiseParams: HolderProvider<NoiseParameters>
    ): NoiseRouter {
        val aquiferBarrierFunction =
            noise(noiseParams.getHolderOrThrow(NoiseParametersKeys.AQUIFER_BARRIER), 0.5)
        val aquiferFluidLevelFloodedness = noise(
            noiseParams.getHolderOrThrow(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS),
            0.67
        )
        val aquiferFluidLevelSpread = noise(
            noiseParams.getHolderOrThrow(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD),
            0.7142857142857143
        )
        val aquiferLava = noise(noiseParams.getHolderOrThrow(NoiseParametersKeys.AQUIFER_LAVA))
        val yLevel = denseHold(densityFuns, NoiseRouterData.Y)
        val veinTypeMinY = Stream.of(*VeinType.entries.toTypedArray()).mapToInt { type: VeinType -> type.minY }
            .min().orElse(-DimensionType.MIN_Y * 2)
        val veinTypeMaxY = Stream.of(*VeinType.entries.toTypedArray()).mapToInt { type: VeinType -> type.maxY }
            .max().orElse(-DimensionType.MIN_Y * 2)
        val oreVeininessFunction = NoiseRouterData.yLimitedInterpolatable(
            yLevel,
            noise(noiseParams.getHolderOrThrow(NoiseParametersKeys.ORE_VEININESS), 1.5, 1.5),
            veinTypeMinY,
            veinTypeMaxY,
            0
        )
        val veinScale = 4.0
        val veinFunctionA = NoiseRouterData.yLimitedInterpolatable(
            yLevel,
            noise(noiseParams.getHolderOrThrow(NoiseParametersKeys.ORE_VEIN_A), veinScale, veinScale),
            veinTypeMinY,
            veinTypeMaxY,
            0
        ).abs()
        val veinFunctionB = NoiseRouterData.yLimitedInterpolatable(
            yLevel,
            noise(noiseParams.getHolderOrThrow(NoiseParametersKeys.ORE_VEIN_B), veinScale, veinScale),
            veinTypeMinY,
            veinTypeMaxY,
            0
        ).abs()
        val veinFunction = add(
            constant(-0.07999999821186066),
            max(veinFunctionA, veinFunctionB)
        )
        val veinGapFunction = noise(noiseParams.getHolderOrThrow(NoiseParametersKeys.ORE_GAP))
        return NoiseRouter(
            aquiferBarrierFunction,
            aquiferFluidLevelFloodedness,
            aquiferFluidLevelSpread,
            aquiferLava,
            denseHold(densityFuns, TEMPERATURE_WF),
            denseHold(densityFuns, VEGETATION_WF),
            denseHold(densityFuns, CONTINENTS_WF),
            denseHold(densityFuns, EROSION_WF),
            denseHold(densityFuns, DEPTH_MIX),
            denseHold(densityFuns, NoiseRouterData.RIDGES_OVERWORLD),
            denseHold(densityFuns, INITIAL_DENSITY_WITHOUT_JAGGEDNESS),
            denseHold(densityFuns, FINAL_DENSITY_MIX),
            oreVeininessFunction,
            veinFunction,
            veinGapFunction
        )
    }

    private fun initialDensityWithoutJaggedness(amplified: Boolean, densityFunction: DensityFunction): DensityFunction {
        return createIDWJ(
            densityFunction,
            -64,
            384,
            if (amplified) 16 else 80,
            if (amplified) 0 else 64,
            -0.078125,
            0,
            24,
            if (amplified) 0.4 else 0.1171875
        )
    }

    private fun createIDWJ(
        function1: DensityFunction,
        yMin: Int,
        yHeight: Int,
        upperYRoof: Int,
        lowerYRoof: Int,
        clampOffset: Double,
        lowerYFloor: Int,
        upperYfloor: Int,
        addToAll: Double
    ): DensityFunction {
        val densityFunction =
            clampedGradientY(yMin + yHeight - upperYRoof, yMin + yHeight - lowerYRoof, 1.0, 0.0)
        val densityFunction2 = lerp(densityFunction, clampOffset, function1)
        val densityFunction3 = clampedGradientY(yMin + lowerYFloor, yMin + upperYfloor, 0.0, 1.0)
        return lerp(densityFunction3, addToAll, densityFunction2)
    }


    fun transitionAmplified(
        densityFuns: HolderProvider<DensityFunction>,
        normal: DensityFunction,
        amplified: DensityFunction
    ): DensityFunction {
        return add(
            multiply(
                normal,
                denseHold(densityFuns, NORMAL_REGION)
            ),
            multiply(
                amplified,
                denseHold(densityFuns, AMPLIFIED_REGION)
            )
        )
    }

    private fun denseHold(
        holderProvider: HolderProvider<DensityFunction>,
        key: RegistryKey<DensityFunction>
    ): DensityFunction {
        return HolderHolder(holderProvider.getHolderOrThrow(key))
    }
}
