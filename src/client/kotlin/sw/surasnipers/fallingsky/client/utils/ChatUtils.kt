package sw.surasnipers.fallingsky.client.utils

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object ChatUtils {
    fun send(msg: String) {
        Minecraft.getInstance().player?.sendSystemMessage(Component.literal(msg))
    }
}
