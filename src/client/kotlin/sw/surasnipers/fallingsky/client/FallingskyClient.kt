package sw.surasnipers.fallingsky.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import sw.surasnipers.fallingsky.client.commands.GlowColorCommand
import sw.surasnipers.fallingsky.client.commands.GlowCommand
import sw.surasnipers.fallingsky.client.commands.GlowEntityCommand
import sw.surasnipers.fallingsky.client.config.GlowConfig
import sw.surasnipers.fallingsky.client.systems.GlowSystem
import sw.surasnipers.fallingsky.client.utils.ChatUtils

class FallingskyClient : ClientModInitializer {

    private lateinit var toggleKey: KeyMapping

    override fun onInitializeClient() {
        toggleKey = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.fallingsky.toggle_glow",
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.MISC
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (toggleKey.consumeClick()) {
                GlowConfig.glowEnabled = !GlowConfig.glowEnabled
                ChatUtils.send("Glow: ${GlowConfig.glowEnabled}")
            }
            GlowSystem.tick(client)
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            GlowCommand.register(dispatcher)
            GlowColorCommand.register(dispatcher)
            GlowEntityCommand.register(dispatcher)
        }
    }
}
