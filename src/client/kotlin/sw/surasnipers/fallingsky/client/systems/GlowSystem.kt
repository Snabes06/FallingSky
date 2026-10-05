package sw.surasnipers.fallingsky.client.systems

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import sw.surasnipers.fallingsky.client.config.GlowConfig

object GlowSystem {

    private var glowing: Set<Int> = emptySet()

    fun shouldGlow(entity: Entity): Boolean = entity.id in glowing

    fun tick(client: Minecraft) {
        val level = client.level
        val player = client.player
        if (!GlowConfig.glowEnabled || level == null || player == null) {
            glowing = emptySet()
            return
        }

        glowing = level.entitiesForRendering()
            .filter { it !== player && it !is ArmorStand && matches(it) && player.hasLineOfSight(it) }
            .map { it.id }
            .toSet()
    }

    private fun matches(entity: Entity): Boolean {
        val nameTag = entity.level().getEntity(entity.id + 1) as? ArmorStand ?: return false
        if (nameTag.distanceToSqr(entity) > 16.0) return false
        return GlowConfig.targetRegex.containsMatchIn(nameTag.name.string)
    }
}
