package sw.surasnipers.fallingsky.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.util.Identifier

class FallingskyClient : ClientModInitializer {

    override fun onInitializeClient() {
        HudElementRegistry.addLast(Identifier.of("fallingsky", "hud_renderer"), HudRenderer())
        
        // Register the 3D line renderer
        WorldRenderEvents.END_MAIN.register(WorldLineRenderer())
    }
}
