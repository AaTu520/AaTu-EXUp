package cn.blockforge.aatu_exup.mixin;

import cn.blockforge.aatu_exup.AaTu_EXUp_Mod;
import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Inject(
            method = "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F",
            at = @At("RETURN"),
            cancellable = true)
    private void aatu_exup$applyPenetrationDamage(
            DamageSource source, float amount, CallbackInfoReturnable<Float> callbackInfo) {
        if (!(source.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        if ((Object) this instanceof Player && !AaTu_EXUp_ModConfig.PENETRATION_AFFECTS_PLAYERS.getAsBoolean()) {
            return;
        }

        double penetration = player.getAttributeValue(AaTu_EXUp_Mod.PENETRATION);
        if (penetration <= 0.0D || player.getRandom().nextDouble() >= penetration) {
            return;
        }

        double penetrationDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE)
                * AaTu_EXUp_ModConfig.PENETRATION_DAMAGE_RATIO.getAsDouble();
        if (!Double.isFinite(penetrationDamage) || penetrationDamage <= 0.0D) {
            return;
        }

        float newDamage = callbackInfo.getReturnValue() + (float) Math.min(penetrationDamage, Float.MAX_VALUE);
        callbackInfo.setReturnValue(newDamage);
    }
}
