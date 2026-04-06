package org.teamvoided.world_foundry.datagen.assets

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class EnLangProvider(o: FabricDataOutput, p: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(o, p) {
    override fun generateTranslations(lookup: HolderLookup.Provider, gen: TranslationBuilder) {
        gen.add("generator.world_foundry.mixed_amplified", "Mixed Amplified")
        gen.add("generator.world_foundry.cave_world", "cave_world")
        gen.add("generator.world_foundry.noise_world", "noise_world")
    }
}