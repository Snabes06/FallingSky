package sw.surasnipers.fallingsky.mixin.client;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sw.surasnipers.fallingsky.client.systems.GlowSystem;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isCurrentlyGlowing", at = @At("RETURN"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (GlowSystem.INSTANCE.shouldGlow((Entity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
