package sw.surasnipers.fallingsky.client.utils

import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import java.util.Locale

/**
 * Gets the Hypixel extraAttributes from an ItemStack.
 */
val ItemStack.extraAttributes: NbtCompound
    get() = this.get(DataComponentTypes.CUSTOM_DATA)?.copyNbt() ?: NbtCompound()

/**
 * Gets the Hypixel internal item ID (e.g., "COAL", "ASPECT_OF_THE_VOID").
 */
fun ItemStack.getSkyblockId(): String? {
    val extraAttributes: NbtCompound = this.extraAttributes
    if (extraAttributes.contains("id")) {
        val id = extraAttributes.getString("id").orElse("")
        if (id.isEmpty()) return null
        return id.uppercase(Locale.ROOT).replace(":", "-")
    }
    return null
}

/**
 * Checks if the item is a Skyblock item (has an id in extraAttributes).
 */
fun ItemStack.isSkyblockItem(): Boolean = getSkyblockId() != null
