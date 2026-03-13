package sw.surasnipers.fallingsky.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.entity.Entity
import org.lwjgl.glfw.GLFW

class FallingskyClient : ClientModInitializer {

    private var glowEnabled = false
    private val glowDistance = 50.0
    private lateinit var toggleKey: KeyBinding

    override fun onInitializeClient() {

        toggleKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.fallingsky.toggle_glow",
                GLFW.GLFW_KEY_G,
                KeyBinding.Category.MISC
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client: MinecraftClient ->

            while (toggleKey.wasPressed()) {
                glowEnabled = !glowEnabled
                println("Glow toggled: $glowEnabled")
            }

            if (!glowEnabled) return@register

            val player = client.player ?: return@register
            val world = client.world ?: return@register

            for (entity: Entity in world.entities) {
                val glow = entity.isGlowing
                println("Entity: $glow")
                if (entity == player) continue

                entity.isGlowing = true

            }
        }
    }
}
