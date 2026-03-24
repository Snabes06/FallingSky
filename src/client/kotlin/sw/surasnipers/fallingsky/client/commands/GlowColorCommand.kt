package sw.surasnipers.fallingsky.client.commands

import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import sw.surasnipers.fallingsky.client.config.GlowConfig
import sw.surasnipers.fallingsky.client.utils.ChatUtils

object GlowColorCommand {

    fun register(dispatcher: com.mojang.brigadier.CommandDispatcher<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("glowC")
                .then(
                    ClientCommandManager.argument("color", StringArgumentType.word())
                        .executes {
                            val input = StringArgumentType.getString(it, "color")

                            GlowConfig.currentColor = when (input.lowercase()) {
                                "green" -> 0xFF00FF00.toInt()
                                "yellow" -> 0xFFFFFF00.toInt()
                                "red" -> 0xFFFF0000.toInt()
                                else -> GlowConfig.currentColor
                            }

                            ChatUtils.send("Color set to $input")
                            1
                        }
                )
        )
    }
}