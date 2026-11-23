package cn.blockforge.aatu_exup.mixin.client;

import java.util.function.BiConsumer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import cn.blockforge.aatu_exup.client.WeaponAttributeTooltip;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.sweenus.simplytooltips.client.tooltip.GenericTooltipProvider", remap = false)
public abstract class SimplyTooltipsWeaponMixin {
    @WrapOperation(method = "extractMainHandStats", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/item/ItemStack;forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"),
            remap = false, require = 0)
    private static void aatu$showMainHandTotals(ItemStack stack, EquipmentSlotGroup group,
            BiConsumer<Holder<Attribute>, AttributeModifier> consumer, Operation<Void> original) {
        if (!WeaponAttributeTooltip.handles(stack)) {
            original.call(stack, group, consumer);
            return;
        }
        double attack;
        double speed;
        ResourceLocation attackId;
        ResourceLocation speedId;
        try {
            attack = WeaponAttributeTooltip.mainHandTotal(stack, Attributes.ATTACK_DAMAGE)
                    - Attributes.ATTACK_DAMAGE.value().getDefaultValue();
            speed = WeaponAttributeTooltip.mainHandTotal(stack, Attributes.ATTACK_SPEED)
                    - Attributes.ATTACK_SPEED.value().getDefaultValue();
            attackId = WeaponAttributeTooltip.preferredModifier(stack, group,
                    Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID);
            speedId = WeaponAttributeTooltip.preferredModifier(stack, group,
                    Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID);
        } catch (RuntimeException exception) {
            original.call(stack, group, consumer);
            return;
        }
        BiConsumer<Holder<Attribute>, AttributeModifier> merged = (attribute, modifier) -> {
            if (attribute.is(Attributes.ATTACK_DAMAGE) && attackId != null && modifier.is(attackId)) {
                modifier = new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attack, AttributeModifier.Operation.ADD_VALUE);
            } else if (attribute.is(Attributes.ATTACK_SPEED) && speedId != null && modifier.is(speedId)) {
                modifier = new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE);
            }
            consumer.accept(attribute, modifier);
        };
        original.call(stack, group, merged);
    }
}
