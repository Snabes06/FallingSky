package sw.surasnipers.fallingsky.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "getBlockLight", at = @At("RETURN"), cancellable = true)
    public <T extends Entity> void getLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        // Use the toggle from FallingskyClient
        boolean glow = sw.surasnipers.fallingsky.client.FallingskyClient.isGlowEnabled();
        if (!glow) return;
        if (
                sw.surasnipers.fallingsky.client.FallingskyClient.isEntityInLineOfSight(entity)
                        && sw.surasnipers.fallingsky.client.FallingskyClient.matchesSelectedMob(entity)
        ) {
            cir.setReturnValue(15);
        } else {
            cir.setReturnValue(0);
        }
    }
}
