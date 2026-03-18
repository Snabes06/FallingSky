package sw.surasnipers.fallingsky.client.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

object InventoryUtils {

    /**
     * Gets the title of the currently open inventory.
     */
    fun getInventoryName(): String? {
        val screen = MinecraftClient.getInstance().currentScreen
        return screen?.title?.string
    }

    /**
     * Checks if any container (chest, etc.) is open.
     */
    fun isContainerOpen(): Boolean {
        val screen = MinecraftClient.getInstance().currentScreen
        return screen is HandledScreen<*> && screen !is InventoryScreen
    }

    /**
     * Gets all slots in the currently open container (excluding player inventory).
     */
    fun getContainerSlots(): List<Slot> {
        val screen = MinecraftClient.getInstance().currentScreen as? HandledScreen<*> ?: return emptyList()
        val handler = screen.screenHandler
        // In most Hypixel menus, the first X slots are the container, and the rest are player inventory.
        // We can filter by checking if the slot's inventory is NOT the player inventory.
        val playerInventory = MinecraftClient.getInstance().player?.inventory
        return handler.slots.filter { it.inventory != playerInventory }
    }

    /**
     * Gets all items in the currently open container.
     */
    fun getContainerItems(): List<ItemStack> {
        return getContainerSlots().map { it.stack }
    }
}
