package org.teamvoided.world_foundry.datagen.data.worlgen

import net.minecraft.core.Holder
import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.TerrainProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.util.CubicSpline
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.*
import net.minecraft.world.level.levelgen.DensityFunctions.*
import net.minecraft.world.level.levelgen.OreVeinifier.VeinType
import net.minecraft.world.level.levelgen.synth.NormalNoise
import org.teamvoided.world_foundry.config.WFConfig
import org.teamvoided.world_foundry.data.worldgen.WFDensityFunctions
import org.teamvoided.world_foundry.data.worldgen.WFNoises
import org.teamvoided.world_foundry.data.worldgen.density_functions.ConfigureDensityFunction
import org.teamvoided.world_foundry.data.worldgen.density_functions.ShiftierNoise

object DensityFunctionProvider {
    fun bootstrap(c: BootstrapContext<DensityFunction>) {
        val noiseParams = c.lookup(Registries.NOISE)
        val densityFuns = c.lookup(Registries.DENSITY_FUNCTION)
        val shiftX = densityFuns.holder(NoiseRouterData.SHIFT_X)
        val shiftZ = densityFuns.holder(NoiseRouterData.SHIFT_Z)
        c.register(
            WFDensityFunctions.AMPLIFIED_REGION,
            cache2d(
                mul(
                    constant(3.0),
                    ShiftierNoise(
                        mul(
                            constant(0.25),
                            ConfigureDensityFunction(WFConfig.AMPLIFIED_SIZE)
                        ),
                        noiseParams.getOrThrow(WFNoises.AMPLIFIED_TRANSITION_NOISE)
                    )
                ).clamp(0.0, 1.0)
            )
        )
        c.register(
            WFDensityFunctions.NORMAL_REGION,
            cache2d(
                mul(
                    constant(-1.0),
                    add(
                        constant(-1.0),
                        densityFuns.holder(WFDensityFunctions.AMPLIFIED_REGION)
                    )
                )
            )
        )

        c.register(
            WFDensityFunctions.TEMPERATURE_WF,
            ShiftierNoise(
                shiftX,
                shiftZ,
                mul(constant(0.25), ConfigureDensityFunction(WFConfig.TEMPERATURE)),
                noiseParams.getOrThrow(Noises.TEMPERATURE)
            )
        )
        c.register(
            WFDensityFunctions.VEGETATION_WF,
            ShiftierNoise(
                shiftX,
                shiftZ,
                mul(constant(0.25), ConfigureDensityFunction(WFConfig.HUMIDITY)),
                noiseParams.getOrThrow(Noises.VEGETATION)
            )
        )
        c.register(
            WFDensityFunctions.CONTINENTS_WF,
            ShiftierNoise(
                shiftX,
                shiftZ,
                mul(
                    constant(0.25),
                    ConfigureDensityFunction(WFConfig.CONTINENTALNESS)
                ),
                noiseParams.getOrThrow(Noises.CONTINENTALNESS)
            )
        )
        c.register(
            WFDensityFunctions.EROSION_WF,
            ShiftierNoise(
                shiftX,
                shiftZ,
                mul(constant(0.25), ConfigureDensityFunction(WFConfig.EROSION)),
                noiseParams.getOrThrow(Noises.EROSION)
            )
        )


        val slopedCheeseFunction = densityFuns.holder(WFDensityFunctions.SLOPED_CHEESE_MIX)
        val caveEntrancesFunction = min(
            slopedCheeseFunction,
            mul(
                constant(5.0),
                densityFuns.holder(NoiseRouterData.ENTRANCES)
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
            WFDensityFunctions.FINAL_DENSITY_MIX,
            min(
//                min(
                NoiseRouterData.postProcess(
                    transitionAmplified(
                        densityFuns,
                        NoiseRouterData.slideOverworld(false, cavesMainFunction),
                        NoiseRouterData.slideOverworld(true, cavesMainFunction)
                    ),
                ),
//                    densityFuns.denseHold(WFDensityFunctions.LAVA_CAVE)
//                ),
                densityFuns.holder(NoiseRouterData.NOODLE)
            )
        )



        c.register(
            WFDensityFunctions.INITIAL_DENSITY_WITHOUT_JAGGEDNESS,
            transitionAmplified(
                densityFuns,
                densityFuns.holder(WFDensityFunctions.I_D_W_J_NORMAL),
                densityFuns.holder(WFDensityFunctions.I_D_W_J_AMPLIFIED)
            )
        )

        val idwjFlesh = add(
            NoiseRouterData.noiseGradientDensity(
                cache2d(densityFuns.holder(WFDensityFunctions.FACTOR_MIX)),
                densityFuns.holder(WFDensityFunctions.DEPTH_MIX)
            ),
            constant(-0.703125)
        ).clamp(-64.0, 64.0)

        val offset = densityFuns.holder(NoiseRouterData.OFFSET)
        val factor = densityFuns.holder(NoiseRouterData.FACTOR)
        val prelimSurf = preliminarySurfaceLevel(offset, factor, false)

        c.register(WFDensityFunctions.I_D_W_J_NORMAL, prelimSurf)
        c.register(
            WFDensityFunctions.I_D_W_J_AMPLIFIED,
            initialDensityWithoutJaggedness(true, idwjFlesh)
        )

        c.createGenerationSplinesAmplifiedMixture(
            densityFuns,
            noise(noiseParams.getOrThrow(Noises.JAGGED), 1500.0, 0.0),
            densityFuns.getOrThrow(WFDensityFunctions.CONTINENTS_WF),
            densityFuns.getOrThrow(WFDensityFunctions.EROSION_WF),
            WFDensityFunctions.OFFSET_MIX,
            WFDensityFunctions.FACTOR_MIX,
            WFDensityFunctions.JAGGEDNESS_MIX,
            WFDensityFunctions.DEPTH_MIX,
            WFDensityFunctions.SLOPED_CHEESE_MIX
        )

        c.lavaCaves(noiseParams)

    }

    fun preliminarySurfaceLevel(
        offset: DensityFunction, factor: DensityFunction, amplified: Boolean,
    ): DensityFunction {
        val factor2D = cache2d(factor)
        val offset2D = cache2d(offset)
        var remappedClamp = remap(
            add(
                mul(constant(0.2734375), factor2D.invert()),
                mul(constant(-1.0), offset2D)
            ),
            1.5,
            -1.5,
            -64.0,
            320.0
        )
        remappedClamp = remappedClamp.clamp(-40.0, 320.0)
        val slide = add(
            NoiseRouterData.slideOverworld(
                amplified,
                add(
                    NoiseRouterData.noiseGradientDensity(factor2D, offsetToDepth(offset2D)),
                    constant(-0.703125)
                ).clamp(-64.0, 64.0)
            ),
            constant(-0.390625)
        )
        return findTopSurface(
            slide,
            remappedClamp,
            -64,
            NoiseSettings.OVERWORLD_NOISE_SETTINGS.cellHeight
        )
    }

    fun remap(densityFunction: DensityFunction, d: Double, e: Double, f: Double, g: Double): DensityFunction {
        val h = (g - f) / (e - d)
        val i = f - d * h
        return add(mul(densityFunction, constant(h)), constant(i))
    }

    fun offsetToDepth(densityFunction: DensityFunction): DensityFunction {
        return add(yClampedGradient(-64, 320, 1.5, -1.5), densityFunction)
    }


    fun BootstrapContext<DensityFunction>.lavaCaves(noiseParams: HolderGetter<NormalNoise.NoiseParameters>) {
        register(
            WFDensityFunctions.LAVA_CAVE,
            add(
                mul(
                    constant(10.0),
                    noise(noiseParams.getOrThrow(WFNoises.LAVA_RIVER), 0.25, 0.0).abs()
                ),
                spline(
                    CubicSpline.builder(Spline.Coordinate(Holder.direct(yClampedGradient(-60, -40, -1.0, 1.0))))
                        .addPoint(-1f, 0f, 0f)
                        .addPoint(-0.85f, -0.02f, -0.1f)
                        .addPoint(-0.6f, -0.05f, -0.35f)
                        .addPoint(-0.5f, -0.08f, -0.25f)
                        .addPoint(-0.4f, -0.15f, -0.75f)
                        .addPoint(-0.1f, -0.08f, 0f)
                        .addPoint(0.0f, 0f, 0.4f)
                        .build()
                )
            ).squeeze()
        )
    }

    fun BootstrapContext<DensityFunction>.createGenerationSplinesAmplifiedMixture(
        densityFuns: HolderGetter<DensityFunction>,
        jaggedNoise: DensityFunction,
        continents: Holder<DensityFunction>,
        erosion: Holder<DensityFunction>,
        offset: ResourceKey<DensityFunction>,
        factor: ResourceKey<DensityFunction>,
        jaggedness: ResourceKey<DensityFunction>,
        depth: ResourceKey<DensityFunction>,
        cheese: ResourceKey<DensityFunction>,
    ) {
        val continentsNoise = Spline.Coordinate(continents)
        val erosionNoise = Spline.Coordinate(erosion)
        val ridgesNoise =
            Spline.Coordinate(densityFuns.getOrThrow(NoiseRouterData.RIDGES))
        val ridgesFoldedNoise =
            Spline.Coordinate(densityFuns.getOrThrow(NoiseRouterData.RIDGES_FOLDED))
        val offsetSpline = NoiseRouterData.registerAndWrap(
            this,
            offset,
            NoiseRouterData.splineWithBlending(
                add(
                    constant(-0.50375),
                    transitionAmplified(
                        densityFuns,
                        spline(
                            TerrainProvider.overworldOffset(
                                continentsNoise,
                                erosionNoise,
                                ridgesFoldedNoise,
                                false
                            )
                        ),
                        spline(
                            TerrainProvider.overworldOffset(
                                continentsNoise,
                                erosionNoise,
                                ridgesFoldedNoise,
                                true
                            )
                        )
                    )
                ), blendOffset()
            )
        )
        val factorSpline = NoiseRouterData.registerAndWrap(
            this,
            factor,
            NoiseRouterData.splineWithBlending(
                transitionAmplified(
                    densityFuns,
                    spline(
                        TerrainProvider.overworldFactor(
                            continentsNoise,
                            erosionNoise,
                            ridgesNoise,
                            ridgesFoldedNoise,
                            false
                        )
                    ),
                    spline(
                        TerrainProvider.overworldFactor(
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
            this,
            depth,
            add(yClampedGradient(-64, 320, 1.5, -1.5), offsetSpline)
        )
        val jaggednessSpline = NoiseRouterData.registerAndWrap(
            this,
            jaggedness,
            NoiseRouterData.splineWithBlending(
                transitionAmplified(
                    densityFuns,
                    spline(
                        TerrainProvider.overworldJaggedness(
                            continentsNoise,
                            erosionNoise,
                            ridgesNoise,
                            ridgesFoldedNoise,
                            false
                        )
                    ),
                    spline(
                        TerrainProvider.overworldJaggedness(
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
        val jagged = mul(jaggednessSpline, jaggedNoise.halfNegative())
        val depthAndJaggedness = NoiseRouterData.noiseGradientDensity(
            factorSpline,
            add(depthFunction, jagged)
        )
        register(
            cheese,
            add(
                depthAndJaggedness,
                densityFuns.holder(NoiseRouterData.BASE_3D_NOISE_OVERWORLD)
            )
        )
    }

    fun overworldNoiseSettingsMakerAmplifiedMixture(
        densityFuns: HolderGetter<DensityFunction>,
        noises: HolderGetter<NormalNoise.NoiseParameters>,
    ): NoiseRouter {
        val aquiferBarrierFunction = noise(noises.getOrThrow(Noises.AQUIFER_BARRIER), 0.5)
        val aquiferFluidLevelFloodedness = noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67)
        val aquiferFluidLevelSpread = noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143)
        val yLevel = densityFuns.holder(NoiseRouterData.Y)
        val veinTypeMinY = VeinType.entries.minOfOrNull { it.minY } ?: (-DimensionType.MIN_Y * 2)
        val veinTypeMaxY = VeinType.entries.maxOfOrNull { it.maxY } ?: (-DimensionType.MIN_Y * 2)
        val oreVeininessFunction = NoiseRouterData.yLimitedInterpolatable(
            yLevel,
            noise(noises.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5),
            veinTypeMinY,
            veinTypeMaxY,
            0
        )
        val veinScale = 4.0
        val veinFunctionA = NoiseRouterData.yLimitedInterpolatable(
            yLevel,
            noise(noises.getOrThrow(Noises.ORE_VEIN_A), veinScale, veinScale),
            veinTypeMinY,
            veinTypeMaxY,
            0
        ).abs()
        val veinFunctionB = NoiseRouterData.yLimitedInterpolatable(
            yLevel,
            noise(noises.getOrThrow(Noises.ORE_VEIN_B), veinScale, veinScale),
            veinTypeMinY,
            veinTypeMaxY,
            0
        ).abs()
        val veinFunction = add(
            constant(-0.07999999821186066),
            max(veinFunctionA, veinFunctionB)
        )
        return NoiseRouter(
            aquiferBarrierFunction,
            aquiferFluidLevelFloodedness,
            aquiferFluidLevelSpread,
            noise(noises.getOrThrow(Noises.AQUIFER_LAVA)),
            densityFuns.holder(WFDensityFunctions.TEMPERATURE_WF),
            densityFuns.holder(WFDensityFunctions.VEGETATION_WF),
            densityFuns.holder(WFDensityFunctions.CONTINENTS_WF),
            densityFuns.holder(WFDensityFunctions.EROSION_WF),
            densityFuns.holder(WFDensityFunctions.DEPTH_MIX),
            densityFuns.holder(NoiseRouterData.RIDGES),
            densityFuns.holder(WFDensityFunctions.I_D_W_J_NORMAL),
            densityFuns.holder(WFDensityFunctions.FINAL_DENSITY_MIX),
            oreVeininessFunction,
            veinFunction,
            noise(noises.getOrThrow(Noises.ORE_GAP))
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

    @Suppress("SameParameterValue")
    private fun createIDWJ(
        function1: DensityFunction,
        yMin: Int,
        yHeight: Int,
        upperYRoof: Int,
        lowerYRoof: Int,
        clampOffset: Double,
        lowerYFloor: Int,
        upperYfloor: Int,
        addToAll: Double,
    ): DensityFunction {
        val densityFunction = yClampedGradient(yMin + yHeight - upperYRoof, yMin + yHeight - lowerYRoof, 1.0, 0.0)
        val densityFunction2 = lerp(densityFunction, clampOffset, function1)
        val densityFunction3 = yClampedGradient(yMin + lowerYFloor, yMin + upperYfloor, 0.0, 1.0)
        return lerp(densityFunction3, addToAll, densityFunction2)
    }


    fun transitionAmplified(
        densityFuns: HolderGetter<DensityFunction>,
        normal: DensityFunction,
        amplified: DensityFunction,
    ): DensityFunction {
        return add(
            mul(
                normal,
                densityFuns.holder(WFDensityFunctions.NORMAL_REGION)
            ),
            mul(
                amplified,
                densityFuns.holder(WFDensityFunctions.AMPLIFIED_REGION)
            )
        )
    }


    fun HolderGetter<DensityFunction>.holder(key: ResourceKey<DensityFunction>) = HolderHolder(getOrThrow(key))

}