@file:Suppress("unused")

package org.teamvoided.world_foundry

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey


fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment

fun <V : Any, T : V> Registry<V>.register(id: Identifier, entry: T): T = Registry.register(this, id, entry)
fun <V : Any, T : V> Registry<T>.registerHolder(id: Identifier, entry: T): Holder.Reference<T> =
    Registry.registerForHolder(this, id, entry)

fun <T : Any, R : Registry<T>> ResourceKey<R>.tag(id: Identifier): TagKey<T> = TagKey.create(this, id)
fun <T : Any, R : Registry<T>> ResourceKey<R>.key(id: Identifier): ResourceKey<T> = ResourceKey.create(this, id)

fun packLong(first: Int, second: Int): Long = (first.toLong() shl 32) or (second.toLong() and 0xFFFFFFFFL)
