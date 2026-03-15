package sw.surasnipers.fallingsky.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererOutlineMixin<T extends Entity> {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    public void updateRenderState(T entity, EntityRenderState state, float tickDelta, CallbackInfo ci) {
        // Custom outline color when glow is enabled
        boolean glow = sw.surasnipers.fallingsky.client.FallingskyClient.isGlowEnabled();
        if (!glow) return;
        if (sw.surasnipers.fallingsky.client.FallingskyClient.isEntityInLineOfSight(entity)) {
            // Set a custom outline color, e.g., red with full alpha
            state.outlineColor = 0xFFFF0000; // Red outline
        }
    }
}
