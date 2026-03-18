package sw.surasnipers.fallingsky.client

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text

class HudRenderer : HudElement {
    override fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {
        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer

        // If not in world, don't render (though InGameHud usually handles this)
        if (client.world == null) return

        // Update goals if the bingo card is open
        if (BingoRouter.isBingoCardOpen()) {
            BingoRouter.updateGoals()
        }

        val goals = BingoRouter.currentGoals
        if (goals.isEmpty()) {
            // Debug text to show HUD is registered but goals are missing
            drawContext.drawText(
                textRenderer,
                Text.literal("[Bingo Debug] No Goals Loaded"),
                10,
                10,
                0xFFFF0000.toInt(),
                true
            )
            return
        }

        // Dark grey color with 80% opacity for better visibility during debug
        val backgroundColor = 0xCC202020.toInt()
        val borderColor = 0xFFFFAA00.toInt() // Gold/Orange border
        
        // Calculate the height needed (15 pixels per goal + padding)
        val headerHeight = if (BingoRouter.isUsingTemplates) 15 else 0
        val boxHeight = (goals.size * 15 + 10 + headerHeight).coerceAtLeast(20)
        val boxWidth = 850
        
        // Draw the background box and border
        drawContext.fill(10, 10, 10 + boxWidth, 10 + boxHeight, backgroundColor)
        // Draw simple border lines
        drawContext.fill(10, 10, 10 + boxWidth, 11, borderColor) // Top
        drawContext.fill(10, 10 + boxHeight - 1, 10 + boxWidth, 10 + boxHeight, borderColor) // Bottom
        drawContext.fill(10, 10, 11, 10 + boxHeight, borderColor) // Left
        drawContext.fill(10 + boxWidth - 1, 10, 10 + boxWidth, 10 + boxHeight, borderColor) // Right

        val textColor = 0xFFFFFFFF.toInt()

        if (BingoRouter.isUsingTemplates) {
            drawContext.drawText(
                textRenderer,
                Text.literal("Template Board (Debug Mode)"),
                15,
                15,
                0xFFFFAA00.toInt(), // Gold/Orange
                true
            )
        }
        
        // Render each goal name
        goals.forEachIndexed { index, goal ->
            val yOffset = 15 + headerHeight + (index * 15)
            
            // Draw the goal name
            drawContext.drawText(
                textRenderer, 
                Text.literal(goal.name), 
                15, 
                yOffset, 
                textColor, 
                true
            )

            // Show the goal data (description and primary method) if it's there
            if (goal.data != null) {
                // Draw description (the "actual goal")
                drawContext.drawText(
                    textRenderer,
                    Text.literal(": ${goal.data.description}"),
                    170, // Offset for description
                    yOffset,
                    0xFFAAAAAA.toInt(), // Light Gray
                    true
                )

                // Show the primary method
                drawContext.drawText(
                    textRenderer,
                    Text.literal("- ${goal.data.primary_method}"),
                    520, // Offset for primary method
                    yOffset,
                    0xFFFFFF55.toInt(), // Light Yellow
                    true
                )
            }
        }
    }
}
