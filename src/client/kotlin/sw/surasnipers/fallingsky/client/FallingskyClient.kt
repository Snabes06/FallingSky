package sw.surasnipers.fallingsky.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import org.lwjgl.glfw.GLFW
import kotlin.math.acos

class FallingskyClient : ClientModInitializer {

    companion object {
        @JvmStatic
        var glowEnabled = false

        @JvmStatic
        fun isGlowEnabled(): Boolean {
            return glowEnabled
        }

        @JvmStatic
        fun isEntityInLineOfSight(entity: Entity): Boolean {
            val client = MinecraftClient.getInstance()
            val player = client.player ?: return false
            val world = client.world ?: return false
            val playerPos = player.getCameraPosVec(1.0f)
            val entityPos = entity.entityPos
            val lookVec = player.getRotationVec(1.0f)
            val toEntity = entityPos.subtract(playerPos).normalize()
            val dot = lookVec.dotProduct(toEntity)
            val angle = acos(dot)
            val maxAngle = Math.toRadians(client.options.fov.value.toDouble()/1.5)
            if (!(angle < maxAngle)) return false

            // Check for blocks in the way
            val raycastContext = RaycastContext(player.getCameraPosVec(1.0f), entityPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player)
            val hitResult = world.raycast(raycastContext)
            if (hitResult.type == HitResult.Type.BLOCK) {
                val hitPos = hitResult.pos
                val distToHit = playerPos.distanceTo(hitPos)
                val distToEntity = playerPos.distanceTo(entityPos)
                if (distToHit < distToEntity - 1.0) { // Allow some tolerance for entity size
                    return false
                }
            }
            return true
        }
    }

    private lateinit var toggleKey: KeyBinding

    override fun onInitializeClient() {

        toggleKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.fallingsky.toggle_glow",
                GLFW.GLFW_KEY_G,
                KeyBinding.Category.MISC
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client: MinecraftClient ->

            while (toggleKey.wasPressed()) {
                glowEnabled = !glowEnabled
                println("Glow toggled: $glowEnabled")
            }
        }
    }
}