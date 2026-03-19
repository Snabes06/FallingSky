package sw.surasnipers.fallingsky.client

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.OrderedText
import net.minecraft.text.Text

class HudRenderer : HudElement {
    
    data class HudLayout(
        val totalHeight: Int,
        val elements: List<LayoutElement>
    )
    
    data class LayoutElement(
        val text: OrderedText,
        val x: Int,
        val y: Int,
        val color: Int
    )

    fun calculateLayout(textRenderer: TextRenderer): HudLayout {
        val settings = FallingskySettings.instance.bingoHud
        val goals = BingoRouter.currentGoals
        val headerHeight = if (BingoRouter.isUsingTemplates) 15 else 0
        
        val elements = mutableListOf<LayoutElement>()
        var currentY = 5
        
        val showTitle = settings.width >= 600
        val padding = if (settings.width < 200) 2 else 5
        
        if (BingoRouter.isUsingTemplates) {
            val templateText = Text.literal("Template Board (Debug Mode)")
            val wrapped = textRenderer.wrapLines(templateText, settings.width - (padding * 2))
            wrapped.forEach { line ->
                elements.add(LayoutElement(line, padding, currentY, 0xFFFFAA00.toInt()))
                currentY += 10
            }
            currentY += 2
        }
        
        goals.forEach { goal ->
            if (showTitle) {
                // Large layout: [Name]: [Description] - [Method]
                val fullText = if (goal.data != null) {
                    "${goal.name}: ${goal.data.description} - ${goal.data.primary_method}"
                } else {
                    goal.name
                }
                val wrapped = textRenderer.wrapLines(Text.literal(fullText), settings.width - (padding * 2))
                wrapped.forEach { orderedText ->
                    elements.add(LayoutElement(orderedText, padding, currentY, 0xFFFFFFFF.toInt()))
                    currentY += 10
                }
            } else {
                // Narrow layout: [Description] \n  [Method]
                if (goal.data != null) {
                    val descWrapped = textRenderer.wrapLines(Text.literal(goal.data.description), settings.width - (padding * 2))
                    descWrapped.forEach { orderedText ->
                        elements.add(LayoutElement(orderedText, padding, currentY, 0xFFAAAAAA.toInt()))
                        currentY += 10
                    }
                    val methodWrapped = textRenderer.wrapLines(Text.literal("- ${goal.data.primary_method}"), settings.width - (padding * 2) - 5)
                    methodWrapped.forEach { orderedText ->
                        elements.add(LayoutElement(orderedText, padding + 5, currentY, 0xFFFFFF55.toInt()))
                        currentY += 10
                    }
                } else {
                    val nameWrapped = textRenderer.wrapLines(Text.literal(goal.name), settings.width - (padding * 2))
                    nameWrapped.forEach { orderedText ->
                        elements.add(LayoutElement(orderedText, padding, currentY, 0xFFFFFFFF.toInt()))
                        currentY += 10
                    }
                }
            }
            currentY += 2 // Padding between goals
        }
        
        val totalHeight = (currentY + padding).coerceAtLeast(20)
        return HudLayout(totalHeight, elements)
    }

    override fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {
        val settings = FallingskySettings.instance.bingoHud
        if (!settings.enabled) return

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
                settings.x,
                settings.y,
                0xFFFF0000.toInt(),
                true
            )
            return
        }

        val layout = calculateLayout(textRenderer)
        
        // Dark grey color with 80% opacity for better visibility during debug
        val backgroundColor = 0xCC202020.toInt()
        val borderColor = 0xFFFFAA00.toInt() // Gold/Orange border
        
        drawContext.matrices.pushMatrix()
        drawContext.matrices.translate(settings.x.toFloat(), settings.y.toFloat())
        drawContext.matrices.scale(settings.scale)
        
        // Draw the background box and border starting from (0, 0)
        drawContext.fill(0, 0, settings.width, layout.totalHeight, backgroundColor)
        // Draw simple border lines
        drawContext.fill(0, 0, settings.width, 1, borderColor) // Top
        drawContext.fill(0, layout.totalHeight - 1, settings.width, layout.totalHeight, borderColor) // Bottom
        drawContext.fill(0, 0, 1, layout.totalHeight, borderColor) // Left
        drawContext.fill(settings.width - 1, 0, settings.width, layout.totalHeight, borderColor) // Right

        // Render each element from the layout
        layout.elements.forEach { element ->
            drawContext.drawText(
                textRenderer,
                element.text,
                element.x,
                element.y,
                element.color,
                true
            )
        }

        drawContext.matrices.popMatrix()
    }
}
