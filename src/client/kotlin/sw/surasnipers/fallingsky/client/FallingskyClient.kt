package sw.surasnipers.fallingsky.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier

class FallingskyClient : ClientModInitializer {

    override fun onInitializeClient() {
        HudElementRegistry.addLast(Identifier.of("fallingsky", "hud_renderer"), HudRenderer())
        
        // Register the 3D line renderer
        WorldRenderEvents.END_MAIN.register(WorldLineRenderer())

        // Load bingo goals from resources
        loadBingoGoals()
        
        // Ensure template goals are loaded even if resource loading fails or is delayed
        if (BingoRouter.currentGoals.isEmpty()) {
            BingoRouter.loadTemplateGoals()
        }
    }

    private fun loadBingoGoals() {
        try {
            // Try using the ClassLoader first as it's more reliable during early startup
            val stream = FallingskyClient::class.java.getResourceAsStream("/assets/fallingsky/bingo/goals.json")
            if (stream != null) {
                stream.use { s ->
                    val json = s.bufferedReader().readText()
                    BingoRouter.loadGoals(json)
                    println("[FallingSky] Successfully loaded bingo goals from classpath.")
                }
                return
            }

            val client = MinecraftClient.getInstance()
            val resourceManager = client.resourceManager
            val goalsIdentifier = Identifier.of("fallingsky", "bingo/goals.json")
            val resource = resourceManager.getResource(goalsIdentifier)
            
            if (resource.isPresent) {
                resource.get().inputStream.use { s ->
                    val json = s.bufferedReader().readText()
                    BingoRouter.loadGoals(json)
                    println("[FallingSky] Successfully loaded bingo goals from resource manager.")
                }
            } else {
                println("[FallingSky] Bingo goals resource not found.")
            }
        } catch (e: Exception) {
            println("[FallingSky] Error loading bingo goals: ${e.message}")
            e.printStackTrace()
        }
    }
}
