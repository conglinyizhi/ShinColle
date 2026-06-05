package org.trp.shincolle.server

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import java.util.*
import java.util.function.BiFunction
import java.util.function.Supplier

class UnattackableTargetData : SavedData() {
    private val classNames: MutableSet<String> = TreeSet<String>()

    fun toggle(className: String?): Boolean {
        if (className == null || className.isBlank()) {
            return false
        }

        val added: Boolean
        if (this.classNames.contains(className)) {
            this.classNames.remove(className)
            added = false
        } else {
            this.classNames.add(className)
            added = true
        }

        setDirty()
        return added
    }

    fun contains(className: String?): Boolean {
        return className != null && this.classNames.contains(className)
    }

    fun entries(): MutableCollection<String?> {
        return Collections.unmodifiableSet<String?>(this.classNames)
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val list = ListTag()
        for (className in this.classNames) {
            list.add(StringTag.valueOf(className))
        }
        tag.put("ClassNames", list)
        return tag
    }

    companion object {
        private const val DATA_ID = "shincolle_unattackable_targets"

        fun get(level: ServerLevel): UnattackableTargetData {
            return level.getServer().overworld().getDataStorage().computeIfAbsent<UnattackableTargetData>(
                Factory<UnattackableTargetData?>(
                    Supplier { UnattackableTargetData() },
                    BiFunction { tag: CompoundTag?, registries: HolderLookup.Provider? ->
                        Companion.load(
                            tag!!,
                            registries
                        )
                    },
                    null
                ),
                DATA_ID
            )
        }

        private fun load(tag: CompoundTag, registries: HolderLookup.Provider?): UnattackableTargetData {
            val data = UnattackableTargetData()
            val list = tag.getList("ClassNames", Tag.TAG_STRING.toInt())
            for (i in list.indices) {
                val name = list.getString(i)
                if (!name.isBlank()) {
                    data.classNames.add(name)
                }
            }
            return data
        }
    }
}
