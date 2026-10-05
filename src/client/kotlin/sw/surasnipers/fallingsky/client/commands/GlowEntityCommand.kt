package sw.surasnipers.fallingsky.client.commands

import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import sw.surasnipers.fallingsky.client.config.GlowConfig
import sw.surasnipers.fallingsky.client.utils.ChatUtils

object GlowEntityCommand {

    fun register(dispatcher: com.mojang.brigadier.CommandDispatcher<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommands.literal("glowE")
                .then(
                    ClientCommands.argument("regex", StringArgumentType.greedyString())
                        .executes {
                            val input = StringArgumentType.getString(it, "regex")

                            try {
                                GlowConfig.targetRegex = Regex(input, RegexOption.IGNORE_CASE)
                                ChatUtils.send("Regex set: $input")
                            } catch (e: Exception) {
                                ChatUtils.send("Invalid regex!")
                            }

                            1
                        }
                )
        )
    }
}