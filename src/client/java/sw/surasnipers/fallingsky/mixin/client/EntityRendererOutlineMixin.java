package sw.surasnipers.fallingsky.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sw.surasnipers.fallingsky.client.systems.GlowSystem;
import sw.surasnipers.fallingsky.client.config.GlowConfig;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererOutlineMixin<T extends Entity> {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    public void updateRenderState(T entity, EntityRenderState state, float tickDelta, CallbackInfo ci) {
        // Custom outline color when glow is enabled
        if (GlowSystem.INSTANCE.shouldGlow(entity)) {
            // Set a custom outline color with full alpha
            state.outlineColor = GlowConfig.INSTANCE.getCurrentColor();
        }
    }
}
