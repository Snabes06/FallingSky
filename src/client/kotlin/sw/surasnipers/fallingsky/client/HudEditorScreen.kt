package sw.surasnipers.fallingsky.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import net.minecraft.client.render.RenderTickCounter

import net.minecraft.client.gui.Click
import net.minecraft.client.input.KeyInput
import kotlin.math.round

class HudEditorScreen : Screen(Text.literal("HUD Editor")) {
    private var dragging = false
    private var resizingWidth = false
    private var scaling = false
    
    private var lastMouseX = 0.0
    private var lastMouseY = 0.0

    private var dragStartX = 0.0
    private var dragStartY = 0.0
    private var initialHudX = 0
    private var initialHudY = 0
    private var initialHudWidth = 0
    private var initialHudScale = 1.0f

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

        // Draw transparent grid around HUD elements
        if (settings.gridSnap) {
            val gridSize = 10
            val gridColor = 0x20FFFFFF.toInt()
            val margin = 100 // Distance grid extends around HUD
            
            val startX = ((settings.x - margin) / gridSize) * gridSize
            val endX = ((settings.x + scaledWidth + margin) / gridSize) * gridSize
            val startY = ((settings.y - margin) / gridSize) * gridSize
            val endY = ((settings.y + scaledHeight + margin) / gridSize) * gridSize
            
            for (x in startX..endX step gridSize) {
                drawContext.fill(x, startY, x + 1, endY, gridColor)
            }
            for (y in startY..endY step gridSize) {
                drawContext.fill(startX, y, endX, y + 1, gridColor)
            }
        }

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

        drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("Drag to move, Right edge: resize width, Corner/Scroll: scale"), this.width / 2, 20, 0xFFFFFFFF.toInt())
        drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("Press ESC to save and close"), this.width / 2, 35, 0xFFFFFFFF.toInt())
        
        val snapStatus = if (settings.gridSnap) "ON" else "OFF"
        drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("Grid Snap: $snapStatus (Press G to toggle)"), this.width / 2, 50, 0xFFFFFFFF.toInt())
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
            val settings = FallingskySettings.instance.bingoHud
            if (isMouseOverCorner(mx, my)) {
                scaling = true
                initialHudScale = settings.scale
                dragStartX = mx
            } else if (isMouseOverResizeEdge(mx, my)) {
                resizingWidth = true
                initialHudWidth = settings.width
                dragStartX = mx
            } else if (isMouseOverHud(mx, my)) {
                dragging = true
                initialHudX = settings.x
                initialHudY = settings.y
                dragStartX = mx
                dragStartY = my
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

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        if (isMouseOverHud(mouseX, mouseY)) {
            val settings = FallingskySettings.instance.bingoHud
            var newScale = settings.scale + (verticalAmount * 0.1).toFloat()
            
            if (settings.gridSnap) {
                newScale = (round(newScale.toDouble() * 10.0) / 10.0).toFloat()
            }
            
            settings.scale = newScale.coerceIn(0.2f, 5.0f)
            FallingskySettings.save()
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun mouseDragged(click: Click, deltaX: Double, deltaY: Double): Boolean {
        val mx = click.x
        val my = click.y
        val button = click.button()
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            val settings = FallingskySettings.instance.bingoHud
            if (dragging) {
                var newX = initialHudX + (mx - dragStartX).toInt()
                var newY = initialHudY + (my - dragStartY).toInt()
                
                if (settings.gridSnap) {
                    newX = (newX / 10) * 10
                    newY = (newY / 10) * 10
                }
                settings.x = newX
                settings.y = newY
            } else if (resizingWidth) {
                var newScaledWidth = (mx - settings.x).coerceAtLeast(50.0)
                if (settings.gridSnap) {
                    newScaledWidth = round(newScaledWidth / 10.0) * 10.0
                }
                settings.width = (newScaledWidth / settings.scale).toInt().coerceAtLeast(50)
            } else if (scaling) {
                val boxWidth = settings.width.coerceAtLeast(50)
                var newScale = (mx - settings.x) / boxWidth
                
                if (settings.gridSnap) {
                    newScale = round(newScale * 10.0) / 10.0
                }
                
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
        } else if (input.key() == GLFW.GLFW_KEY_G) {
            // Toggle grid snap when G is pressed
            val settings = FallingskySettings.instance.bingoHud
            settings.gridSnap = !settings.gridSnap
            FallingskySettings.save()
        }
        return super.keyPressed(input)
    }
    
    override fun shouldPause(): Boolean = false
}
