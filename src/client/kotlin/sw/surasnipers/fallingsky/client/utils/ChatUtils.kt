package sw.surasnipers.fallingsky.client.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object ChatUtils {
    fun send(msg: String) {
        MinecraftClient.getInstance().player?.sendMessage(Text.of(msg), false)
    }
}