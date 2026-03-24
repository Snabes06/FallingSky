package sw.surasnipers.fallingsky.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sw.surasnipers.fallingsky.client.systems.GlowSystem;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "getBlockLight", at = @At("RETURN"), cancellable = true)
    public <T extends Entity> void getLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        // Use the toggle from systems GlowSystem
        if (GlowSystem.INSTANCE.shouldGlow(entity)) {
            cir.setReturnValue(15);
        } else {
            cir.setReturnValue(0);
        }
    }
}
