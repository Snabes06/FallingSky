package sw.surasnipers.fallingsky.client

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

class WorldLineRenderer : WorldRenderEvents.EndMain {
    override fun endMain(context: WorldRenderContext) {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        
        // Specific block to point to (e.g., target block)
        val targetPos = Vec3d(21.0, -61.0, 21.0)
        
        val matrixStack = context.matrices() ?: return
        val worldState = context.worldState()
        val camPos = worldState.cameraRenderState.pos
        
        // Use the player's render state position for smoothness if available,
        // otherwise we could use lerp, but we need the tick delta.
        // In 1.21.11, cameraRenderState already has the interpolated positions we need
        // if the camera is the player.
        
        // Let's try to get the camera's entity position which is already interpolated
        val cameraEntityPos = worldState.cameraRenderState.entityPos
        
        // Player center position (using eye position or center)
        // We use cameraEntityPos as it is interpolated to the current frame
        val playerCenter = Vec3d(cameraEntityPos.x, cameraEntityPos.y + (player.height / 2.0), cameraEntityPos.z)
        
        val vertexConsumers = context.consumers()
        val matrices = matrixStack.peek().positionMatrix
        
        // Use (0,0,0) as player origin to start EXACTLY at camera if desired,
        // or calculate relative to current camera position.
        // Since the matrix is already translated to camera space,
        // we just need the relative vector in world space.
        val playerRel = Vec3d(
            playerCenter.x - camPos.x,
            playerCenter.y - camPos.y,
            playerCenter.z - camPos.z
        )
        
        val targetRel = Vec3d(
            targetPos.x - camPos.x,
            targetPos.y - camPos.y,
            targetPos.z - camPos.z
        )

        // Highlight the targeted block with 20% opacity red FIRST
        // Using linesTranslucent which creates a wireframe that is easier to see through blocks
        val boxConsumer = vertexConsumers.getBuffer(RenderLayers.linesTranslucent())
        drawBox(
            boxConsumer, matrices,
            targetRel.x.toFloat(),
            targetRel.y.toFloat(),
            targetRel.z.toFloat(),
            targetRel.x.toFloat() + 1f,
            targetRel.y.toFloat() + 1f,
            targetRel.z.toFloat() + 1f,
            1.0f, 0.0f, 0.0f, 1f // Red with 100% opacity
        )

        // Draw the red line AFTER to ensure it's "on top" if they share a buffer provider
        val lineConsumer = vertexConsumers.getBuffer(RenderLayers.lines())
        drawLine(
            lineConsumer, matrices,
            playerRel.x.toFloat(),
            playerRel.y.toFloat(),
            playerRel.z.toFloat(),
            targetRel.x.toFloat() + 0.5f,
            targetRel.y.toFloat() + 0.5f,
            targetRel.z.toFloat() + 0.5f,
            1.0f, 0.0f, 0.0f, 1.0f // Red
        )
    }

    private fun drawBox(
        vertexConsumer: VertexConsumer,
        matrices: Matrix4f,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        // Wireframe box using 12 lines (24 vertices)
        val normalX = 0f
        val normalY = 1f
        val normalZ = 0f
        val width = 3.0f

        // Bottom 4 edges
        addVertex(vertexConsumer, matrices, x1, y1, z1, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x2, y1, z1, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x2, y1, z1, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x2, y1, z2, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x2, y1, z2, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x1, y1, z2, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x1, y1, z2, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x1, y1, z1, r, g, b, a, normalX, normalY, normalZ, width)

        // Top 4 edges
        addVertex(vertexConsumer, matrices, x1, y2, z1, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x2, y2, z1, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x2, y2, z1, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x2, y2, z2, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x2, y2, z2, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x1, y2, z2, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x1, y2, z2, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x1, y2, z1, r, g, b, a, normalX, normalY, normalZ, width)

        // Vertical 4 edges
        addVertex(vertexConsumer, matrices, x1, y1, z1, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x1, y2, z1, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x2, y1, z1, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x2, y2, z1, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x2, y1, z2, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x2, y2, z2, r, g, b, a, normalX, normalY, normalZ, width)
        
        addVertex(vertexConsumer, matrices, x1, y1, z2, r, g, b, a, normalX, normalY, normalZ, width)
        addVertex(vertexConsumer, matrices, x1, y2, z2, r, g, b, a, normalX, normalY, normalZ, width)
    }

    private fun addVertex(
        vertexConsumer: VertexConsumer,
        matrices: Matrix4f,
        x: Float, y: Float, z: Float,
        r: Float, g: Float, b: Float, a: Float,
        nx: Float, ny: Float, nz: Float,
        lw: Float
    ) {
        vertexConsumer.vertex(matrices, x, y, z)
            .color(r, g, b, a)
            .normal(nx, ny, nz)
            .lineWidth(lw)
    }

    private fun drawLine(
        vertexConsumer: VertexConsumer,
        matrices: Matrix4f,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        vertexConsumer.vertex(matrices, x1, y1, z1)
            .color(r, g, b, a)
            .normal(0f, 1f, 0f)
            .lineWidth(3.0f) // Required element in 1.21.11 LINES format
        
        vertexConsumer.vertex(matrices, x2, y2, z2)
            .color(r, g, b, a)
            .normal(0f, 1f, 0f)
            .lineWidth(3.0f) // Required element in 1.21.11 LINES format
    }

}
