package sw.surasnipers.fallingsky.client.systems

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import sw.surasnipers.fallingsky.client.config.GlowConfig
import sw.surasnipers.fallingsky.client.utils.LineOfSightUtil

object GlowSystem {

    fun shouldGlow(entity: Entity): Boolean {
        if (!GlowConfig.glowEnabled) return false
        if (!LineOfSightUtil.isEntityVisible(entity)) return false
        if (!matches(entity)) return false
        return true
    }

    fun matches(entity: Entity): Boolean {
        val world = MinecraftClient.getInstance().world ?: return false
        val armorStand = world.getEntityById(entity.id + 1) ?: return false

        if (armorStand is ArmorStandEntity) {
            val name = armorStand.name.string
            return GlowConfig.targetRegex.containsMatchIn(name)
        }

        return false
    }
}