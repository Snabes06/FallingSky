package sw.surasnipers.fallingsky.client.commands

import com.mojang.brigadier.arguments.BoolArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import sw.surasnipers.fallingsky.client.config.GlowConfig
import sw.surasnipers.fallingsky.client.utils.ChatUtils

object GlowCommand {

    fun register(dispatcher: com.mojang.brigadier.CommandDispatcher<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommands.literal("glow")
                .then(
                    ClientCommands.argument("value", BoolArgumentType.bool())
                        .executes {
                            GlowConfig.glowEnabled = BoolArgumentType.getBool(it, "value")
                            ChatUtils.send("Glow: ${GlowConfig.glowEnabled}")
                            1
                        }
                )
                .executes {
                    GlowConfig.glowEnabled = !GlowConfig.glowEnabled
                    ChatUtils.send("Glow: ${GlowConfig.glowEnabled}")
                    1
                }
        )
    }
}
