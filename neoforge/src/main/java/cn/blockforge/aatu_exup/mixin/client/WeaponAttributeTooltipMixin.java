package cn.blockforge.aatu_exup.mixin.client;

import java.util.function.Consumer;

import com.google.common.collect.Multimap;
import cn.blockforge.aatu_exup.client.EnhancementAttributeTooltip;
import cn.blockforge.aatu_exup.client.WeaponAttributeTooltip;
import cn.blockforge.aatu_exup.client.WeaponTooltipIntegrationMarker;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttributeUtil.class, remap = false)
public abstract class WeaponAttributeTooltipMixin implements WeaponTooltipIntegrationMarker {
    @Inject(method = "applyTextFor", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void aatu$showEquipmentTotals(ItemStack stack, Consumer<Component> tooltip,
                                             Multimap<Holder<Attribute>, AttributeModifier> modifiers,
                                             AttributeTooltipContext context, CallbackInfo ci) {
        // Weapon totals need the original modifier IDs; merge remaining flat bonuses afterwards.
        if (WeaponAttributeTooltip.render(stack, tooltip, modifiers, context)) {
            ci.cancel();
        } else {
            EnhancementAttributeTooltip.mergeEnhancements(modifiers);
        }
    }
}
