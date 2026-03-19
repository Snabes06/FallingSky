package sw.surasnipers.fallingsky.client

import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import java.io.File
import com.google.gson.Gson

data class HudConfig(
    var x: Int = 10,
    var y: Int = 10,
    var width: Int = 850,
    var scale: Float = 1.0f,
    var enabled: Boolean = true
)

class FallingskySettings {
    var bingoHud = HudConfig()

    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().create()
        private val configFile: File by lazy {
            File(MinecraftClient.getInstance().runDirectory, "config/fallingsky.json")
        }
        var instance = FallingskySettings()

        fun load() {
            if (configFile.exists()) {
                try {
                    instance = GSON.fromJson(configFile.readText(), FallingskySettings::class.java)
                } catch (e: Exception) {
                    println("[FallingSky] Error loading config: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                save()
            }
        }

        fun save() {
            try {
                if (!configFile.parentFile.exists()) {
                    configFile.parentFile.mkdirs()
                }
                configFile.writeText(GSON.toJson(instance))
            } catch (e: Exception) {
                println("[FallingSky] Error saving config: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
