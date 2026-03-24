package sw.surasnipers.fallingsky.client.commands

import com.mojang.brigadier.arguments.BoolArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import sw.surasnipers.fallingsky.client.config.GlowConfig

object GlowCommand {

    fun register(dispatcher: com.mojang.brigadier.CommandDispatcher<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("glow")
                .then(
                    ClientCommandManager.argument("value", BoolArgumentType.bool())
                        .executes {
                            GlowConfig.glowEnabled = BoolArgumentType.getBool(it, "value")
                            MinecraftClient.getInstance().player?.sendMessage(
                                Text.of("Glow: ${GlowConfig.glowEnabled}"),
                                false
                            )
                            1
                        }
                )
                .executes {
                    GlowConfig.glowEnabled = !GlowConfig.glowEnabled
                    MinecraftClient.getInstance().player?.sendMessage(
                        Text.of("Glow toggled: ${GlowConfig.glowEnabled}"),
                        false
                    )
                    1
                }
        )
    }
}