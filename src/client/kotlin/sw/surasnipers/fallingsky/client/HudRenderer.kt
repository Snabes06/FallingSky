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

        // Dark grey color with 33% opacity
        // 0x54 is approximately 33% of 255 (84/255 = 0.33)
        val backgroundColor = 0x54404040.toInt()
        
        // Draw the background box (enlarged to fit text)
        drawContext.fill(10, 10, 120, 60, backgroundColor)

        // Draw 3 lines of temporary text in white
        val textColor = 0xFFFFFFFF.toInt()
        
        // Using drawText(textRenderer, text, x, y, color, shadow)
        drawContext.drawText(textRenderer, Text.literal("Line 1: Item 1"), 15, 15, textColor, true)
        drawContext.drawText(textRenderer, Text.literal("Line 2: Item 2"), 15, 30, textColor, true)
        drawContext.drawText(textRenderer, Text.literal("Line 3: Item 3"), 15, 45, textColor, true)
    }
}
