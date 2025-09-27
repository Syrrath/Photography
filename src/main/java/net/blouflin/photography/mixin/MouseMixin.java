package net.blouflin.photography.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.blouflin.photography.client.PhotographyHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    // From Uku3lig's nowheel under MIT license: https://github.com/uku3lig/nowheel/blob/1.21.2/src/main/java/net/uku3lig/nowheel/mixin/MouseMixin.java
    @WrapWithCondition(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"))
    public boolean onHotbarScroll(PlayerInventory instance, int slot) {
        return !PhotographyHud.isUsingPhotographyCamera;
    }

    @Inject(at = @At("RETURN"), method = "onMouseScroll(JDD)V", cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        vertical = -vertical;

        if(PhotographyHud.isUsingPhotographyCamera) {
            ci.cancel();
            if (vertical > 0) {
                PhotographyHud.zoomAmount *= 1.1;
            } else if (vertical < 0) {
                PhotographyHud.zoomAmount *= 0.9;
            }
            PhotographyHud.zoomAmount = MathHelper.clamp(PhotographyHud.zoomAmount, 0.2, 2);

            client.options.getMouseSensitivity().setValue(PhotographyHud.defaultMouseSensitivity * (PhotographyHud.zoomAmount / 2));
        }
    }
}