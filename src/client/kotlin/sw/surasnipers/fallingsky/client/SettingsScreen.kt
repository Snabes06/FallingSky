package sw.surasnipers.fallingsky.client

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Formatting

class SettingsScreen : Screen(Text.literal("FallingSky Settings")) {
    
    override fun init() {
        val buttonWidth = 200
        val buttonHeight = 20
        val settings = FallingskySettings.instance.bingoHud
        
        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Edit HUD")) { button ->
                this.client?.setScreen(HudEditorScreen())
            }
            .dimensions(this.width / 2 - buttonWidth / 2, 50, buttonWidth, buttonHeight)
            .build()
        )

        this.addDrawableChild(
            ButtonWidget.builder(getToggleText(settings.enabled)) { button ->
                settings.enabled = !settings.enabled
                button.message = getToggleText(settings.enabled)
                FallingskySettings.save()
            }
            .dimensions(this.width / 2 - buttonWidth / 2, 80, buttonWidth, buttonHeight)
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
        // In 1.21.1+, renderBackground signature might be different
        // I'll try to use what I have or use drawContext.fill
        drawContext.fill(0, 0, this.width, this.height, 0x44000000.toInt())
        
        drawContext.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 20, 0xFFFFFFFF.toInt())
        drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("Bingo HUD Toggle:"), this.width / 2, 70, 0xFFAAAAAA.toInt())
        super.render(drawContext, mouseX, mouseY, delta)
    }
}
