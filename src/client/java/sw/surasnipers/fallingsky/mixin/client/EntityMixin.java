package sw.surasnipers.fallingsky.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sw.surasnipers.fallingsky.client.systems.GlowSystem;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity == MinecraftClient.getInstance().player) return;
        // Use the toggle from FallingskyClient
        if (GlowSystem.INSTANCE.shouldGlow(entity)) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}
