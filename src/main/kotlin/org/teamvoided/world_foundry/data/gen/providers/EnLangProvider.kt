package org.teamvoided.world_foundry.data.gen.providers

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class EnLangProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(o, r) {
    override fun generateTranslations(lookup: HolderLookup.Provider, gen: TranslationBuilder) {
        gen.add("generator.world_foundry.mixed_amplified", "Mixed Amplified")
    }
}
