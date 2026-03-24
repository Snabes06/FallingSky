package sw.surasnipers.fallingsky.client.config

object GlowConfig {
    var glowEnabled = false
    var currentColor: Int = 0xFF00FF00.toInt()
    var targetRegex: Regex = Regex("Crypt Ghoul", RegexOption.IGNORE_CASE)
}