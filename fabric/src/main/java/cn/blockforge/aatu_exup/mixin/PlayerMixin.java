package cn.blockforge.aatu_exup.mixin;

import cn.blockforge.aatu_exup.AaTu_EXUp_Mod;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void aatu_exup$addPenetrationAttribute(
            CallbackInfoReturnable<AttributeSupplier.Builder> callbackInfo) {
        callbackInfo.getReturnValue().add(AaTu_EXUp_Mod.PENETRATION, 0.0D);
    }
}
