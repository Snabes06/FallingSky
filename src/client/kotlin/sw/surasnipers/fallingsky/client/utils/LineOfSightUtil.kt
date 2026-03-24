package sw.surasnipers.fallingsky.client.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import kotlin.math.acos

object LineOfSightUtil {

    fun isEntityVisible(entity: Entity): Boolean {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return false
        val world = client.world ?: return false

        val playerPos = player.getCameraPosVec(1.0f)
        val entityPos = entity.entityPos

        val lookVec = player.getRotationVec(1.0f)
        val toEntity = entityPos.subtract(playerPos).normalize()

        val angle = acos(lookVec.dotProduct(toEntity))
        val maxAngle = Math.toRadians(client.options.fov.value.toDouble() / 1.5)

        if (angle >= maxAngle) return false

        val raycast = world.raycast(
            RaycastContext(
                playerPos,
                entityPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
            )
        )

        if (raycast.type == HitResult.Type.BLOCK) {
            val distToHit = playerPos.distanceTo(raycast.pos)
            val distToEntity = playerPos.distanceTo(entityPos)
            if (distToHit < distToEntity - 1.0) return false
        }

        return true
    }
}