package sw.surasnipers.fallingsky.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW
import sw.surasnipers.fallingsky.client.commands.GetGlowEntityCommand
import sw.surasnipers.fallingsky.client.commands.GlowColorCommand
import sw.surasnipers.fallingsky.client.commands.GlowCommand

class FallingskyClient : ClientModInitializer {

    private lateinit var toggleKey: KeyBinding

    override fun onInitializeClient() {


        toggleKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.fallingsky.toggle_glow",
                GLFW.GLFW_KEY_G,
                KeyBinding.Category.MISC
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register {
            while (toggleKey.wasPressed()) {
                sw.surasnipers.fallingsky.client.config.GlowConfig.glowEnabled =
                    !sw.surasnipers.fallingsky.client.config.GlowConfig.glowEnabled
            }
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            GlowCommand.register(dispatcher)
            GlowColorCommand.register(dispatcher)
            GetGlowEntityCommand.register(dispatcher)
        }
    }
}