package cn.blockforge.aatu_exup.mixin.common;

import cn.blockforge.aatu_exup.ArmorAttributeSlots;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CommonHooks.class, remap = false)
public abstract class ArmorAttributeSlotsMixin {
    // Resolve after other mods have added modifiers to the item attribute event.
    @ModifyReturnValue(method = "computeModifiedAttributes", at = @At("RETURN"), remap = false, require = 0)
    private static ItemAttributeModifiers aatu$useArmorSlot(ItemAttributeModifiers current, ItemStack stack,
                                                            ItemAttributeModifiers defaults) {
        return ArmorAttributeSlots.matchArmorSlot(stack, current);
    }
}
