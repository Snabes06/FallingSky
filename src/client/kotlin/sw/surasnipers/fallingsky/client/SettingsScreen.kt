package sw.surasnipers.fallingsky.client

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Formatting

class SettingsScreen : Screen(Text.literal("FallingSky Settings")) {
    
    private var panelX = 0
    private var panelY = 0
    private var panelWidth = 0
    private var panelHeight = 0

    override fun init() {
        panelWidth = (this.width * 0.75).toInt()
        panelHeight = (this.height * 0.65).toInt()
        panelX = (this.width - panelWidth) / 2
        panelY = (this.height - panelHeight) / 2

        val buttonWidth = 100
        val buttonHeight = 20
        val settings = FallingskySettings.instance.bingoHud
        
        val contentX = panelX + 20
        val startY = panelY + 40
        val spacingY = 50

        // Edit HUD Row
        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Edit HUD")) { button ->
                this.client?.setScreen(HudEditorScreen())
            }
            .dimensions(contentX, startY, buttonWidth, buttonHeight)
            .build()
        )

        // Toggle HUD Row
        this.addDrawableChild(
            ButtonWidget.builder(getToggleText(settings.enabled)) { button ->
                settings.enabled = !settings.enabled
                button.message = getToggleText(settings.enabled)
                FallingskySettings.save()
            }
            .dimensions(contentX, startY + spacingY, buttonWidth, buttonHeight)
            .build()
        )
    }

    private fun getToggleText(enabled: Boolean): Text {
        return if (enabled) {
            Text.literal("ON").formatted(Formatting.GREEN)
        } else {
            Text.literal("OFF").formatted(Formatting.RED)
        }
    }

    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // Draw background dim
        drawContext.fill(0, 0, this.width, this.height, 0x44000000.toInt())
        
        // Draw the black panel
        drawContext.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xCC000000.toInt())
        // Draw panel border
        val borderColor = 0xFFAAAAAA.toInt()
        drawContext.fill(panelX, panelY, panelX + panelWidth, panelY + 1, borderColor) // Top
        drawContext.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, borderColor) // Bottom
        drawContext.fill(panelX, panelY, panelX + 1, panelY + panelHeight, borderColor) // Left
        drawContext.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, borderColor) // Right

        drawContext.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, panelY + 15, 0xFFFFFFFF.toInt())
        
        val contentX = panelX + 20
        val startY = panelY + 40
        val spacingY = 50

        // Titles and Descriptions
        // Edit HUD Row
        drawContext.drawTextWithShadow(textRenderer, Text.literal("HUD Layout").formatted(Formatting.BOLD), contentX, startY - 12, 0xFFAAAAAA.toInt())
        drawContext.drawTextWithShadow(textRenderer, Text.literal("Change the position and size of the HUD"), contentX + 110, startY + 6, 0xFFFFFFFF.toInt())

        // Toggle HUD Row
        drawContext.drawTextWithShadow(textRenderer, Text.literal("Toggle HUD").formatted(Formatting.BOLD), contentX, startY + spacingY - 12, 0xFFAAAAAA.toInt())
        drawContext.drawTextWithShadow(textRenderer, Text.literal("Enable or disable the Bingo HUD display"), contentX + 110, startY + spacingY + 6, 0xFFFFFFFF.toInt())

        super.render(drawContext, mouseX, mouseY, delta)
    }
}
