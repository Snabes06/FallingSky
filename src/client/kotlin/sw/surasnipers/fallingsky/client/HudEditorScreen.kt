package sw.surasnipers.fallingsky.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import net.minecraft.client.render.RenderTickCounter

import net.minecraft.client.gui.Click
import net.minecraft.client.input.KeyInput

class HudEditorScreen : Screen(Text.literal("HUD Editor")) {
    private var dragging = false
    private var resizingWidth = false
    private var scaling = false
    
    private var lastMouseX = 0.0
    private var lastMouseY = 0.0

    private val hudRenderer = HudRenderer()

    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // Draw background dim
        drawContext.fill(0, 0, this.width, this.height, 0x44000000.toInt())
        
        val settings = FallingskySettings.instance.bingoHud
        val layout = hudRenderer.calculateLayout(textRenderer)
        val boxHeight = layout.totalHeight
        val boxWidth = settings.width
        
        val scaledWidth = (boxWidth * settings.scale).toInt()
        val scaledHeight = (boxHeight * settings.scale).toInt()

        // Render the HUD itself
        hudRenderer.render(drawContext, MinecraftClient.getInstance().renderTickCounter)

        // Draw selection highlight around the box
        val isOver = isMouseOverHud(mouseX.toDouble(), mouseY.toDouble())
        val borderColor = if (isOver) 0xFFFFFF00.toInt() else 0xFFAAAAAA.toInt()
        
        // Top
        drawContext.fill(settings.x - 1, settings.y - 1, settings.x + scaledWidth + 1, settings.y, borderColor) 
        // Bottom
        drawContext.fill(settings.x - 1, settings.y + scaledHeight, settings.x + scaledWidth + 1, settings.y + scaledHeight + 1, borderColor) 
        // Left
        drawContext.fill(settings.x - 1, settings.y, settings.x, settings.y + scaledHeight, borderColor) 
        // Right
        drawContext.fill(settings.x + scaledWidth, settings.y, settings.x + scaledWidth + 1, settings.y + scaledHeight, borderColor) 

        // Draw resize handles
        // Right edge handle
        drawContext.fill(settings.x + scaledWidth - 2, settings.y, settings.x + scaledWidth, settings.y + scaledHeight, 0xFF00FF00.toInt())
        // Corner handle (scale)
        drawContext.fill(settings.x + scaledWidth - 4, settings.y + scaledHeight - 4, settings.x + scaledWidth, settings.y + scaledHeight, 0xFFFF0000.toInt())

        drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("Drag to move, Right edge to resize width, Corner to scale"), this.width / 2, 20, 0xFFFFFFFF.toInt())
        drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("Press ESC to save and close"), this.width / 2, 35, 0xFFFFFFFF.toInt())
    }

    private fun getBoxHeight(): Int {
        return hudRenderer.calculateLayout(textRenderer).totalHeight
    }

    private fun isMouseOverHud(mx: Double, my: Double): Boolean {
        val settings = FallingskySettings.instance.bingoHud
        val boxHeight = getBoxHeight()
        val boxWidth = settings.width
        val scaledWidth = boxWidth * settings.scale
        val scaledHeight = boxHeight * settings.scale
        return mx >= settings.x && mx <= settings.x + scaledWidth && my >= settings.y && my <= settings.y + scaledHeight
    }

    private fun isMouseOverResizeEdge(mx: Double, my: Double): Boolean {
        val settings = FallingskySettings.instance.bingoHud
        val boxHeight = getBoxHeight()
        val boxWidth = settings.width
        val scaledWidth = boxWidth * settings.scale
        val scaledHeight = boxHeight * settings.scale
        return mx >= settings.x + scaledWidth - 5 && mx <= settings.x + scaledWidth && my >= settings.y && my <= settings.y + scaledHeight - 5
    }

    private fun isMouseOverCorner(mx: Double, my: Double): Boolean {
        val settings = FallingskySettings.instance.bingoHud
        val boxHeight = getBoxHeight()
        val boxWidth = settings.width
        val scaledWidth = boxWidth * settings.scale
        val scaledHeight = boxHeight * settings.scale
        return mx >= settings.x + scaledWidth - 5 && mx <= settings.x + scaledWidth && my >= settings.y + scaledHeight - 5 && my <= settings.y + scaledHeight
    }

    override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
        val mx = click.x
        val my = click.y
        val button = click.button()
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isMouseOverCorner(mx, my)) {
                scaling = true
            } else if (isMouseOverResizeEdge(mx, my)) {
                resizingWidth = true
            } else if (isMouseOverHud(mx, my)) {
                dragging = true
            }
            lastMouseX = mx
            lastMouseY = my
            return true
        }
        return super.mouseClicked(click, doubled)
    }

    override fun mouseReleased(click: Click): Boolean {
        val button = click.button()
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = false
            resizingWidth = false
            scaling = false
            FallingskySettings.save()
        }
        return super.mouseReleased(click)
    }

    override fun mouseDragged(click: Click, deltaX: Double, deltaY: Double): Boolean {
        val mx = click.x
        val my = click.y
        val button = click.button()
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            val settings = FallingskySettings.instance.bingoHud
            if (dragging) {
                settings.x += (mx - lastMouseX).toInt()
                settings.y += (my - lastMouseY).toInt()
            } else if (resizingWidth) {
                val newScaledWidth = (mx - settings.x).coerceAtLeast(50.0)
                settings.width = (newScaledWidth / settings.scale).toInt().coerceAtLeast(50)
            } else if (scaling) {
                val boxWidth = settings.width.coerceAtLeast(50)
                val newScale = (mx - settings.x) / boxWidth
                settings.scale = newScale.toFloat().coerceIn(0.2f, 5.0f)
            }
            lastMouseX = mx
            lastMouseY = my
            return true
        }
        return super.mouseDragged(click, deltaX, deltaY)
    }

    override fun keyPressed(input: KeyInput): Boolean {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            FallingskySettings.save()
            this.close()
            return true
        }
        return super.keyPressed(input)
    }
    
    override fun shouldPause(): Boolean = false
}
