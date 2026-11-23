package cn.blockforge.aatu_exup.mixin.common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import cn.blockforge.aatu_exup.ArmorAttributeSlots;
import cn.blockforge.aatu_exup.ArmorAttributeSlots.ModifierKey;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Enchantment attributes bypass the item attribute event used by other armor bonuses. */
@Mixin(ItemStack.class)
public abstract class ArmorEnchantmentSlotsMixin {
    @WrapOperation(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"),
            require = 0)
    private void aatu$groupEnchantments(ItemStack stack, EquipmentSlotGroup group,
            BiConsumer<Holder<Attribute>, AttributeModifier> consumer, Operation<Void> original) {
        EquipmentSlot slot = ArmorAttributeSlots.armorSlot(stack);
        if (slot == null) {
            original.call(stack, group, consumer);
            return;
        }
        if (group == EquipmentSlotGroup.ARMOR) return;
        EquipmentSlotGroup specificSlot = EquipmentSlotGroup.bySlot(slot);
        if (group != specificSlot) {
            original.call(stack, group, consumer);
            return;
        }

        Set<ModifierKey> emitted = new HashSet<>();
        original.call(stack, group, (BiConsumer<Holder<Attribute>, AttributeModifier>) (attribute, modifier) -> {
            emitted.add(new ModifierKey(attribute, modifier.id()));
            consumer.accept(attribute, modifier);
        });
        original.call(stack, EquipmentSlotGroup.ARMOR,
                (BiConsumer<Holder<Attribute>, AttributeModifier>) (attribute, modifier) -> {
                    AttributeModifier adjusted = ArmorAttributeSlots.withSlotId(modifier, specificSlot);
                    if (emitted.add(new ModifierKey(attribute, adjusted.id()))) consumer.accept(attribute, adjusted);
                });
    }

    @WrapOperation(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"),
            require = 0)
    private void aatu$restrictEnchantments(ItemStack stack, EquipmentSlot requested,
            BiConsumer<Holder<Attribute>, AttributeModifier> consumer, Operation<Void> original) {
        EquipmentSlot slot = ArmorAttributeSlots.armorSlot(stack);
        if (slot == null || requested == slot || !requested.isArmor()) {
            original.call(stack, requested, consumer);
            return;
        }

        Set<ModifierKey> restricted = new HashSet<>();
        Set<ModifierKey> allowed = new HashSet<>();
        EnchantmentHelper.runIterationOnItem(stack, (holder, level) -> {
            var enchantment = holder.value();
            if (!enchantment.matchingSlot(requested)) return;
            var groups = enchantment.definition().slots();
            boolean onlyGeneralArmor = groups.contains(EquipmentSlotGroup.ARMOR)
                    && groups.stream().noneMatch(group -> group != EquipmentSlotGroup.ARMOR && group.test(requested));
            for (var effect : enchantment.getEffects(EnchantmentEffectComponents.ATTRIBUTES)) {
                ModifierKey key = new ModifierKey(effect.attribute(), effect.getModifier(level, requested).id());
                (onlyGeneralArmor ? restricted : allowed).add(key);
            }
        });
        // Shared modifier IDs may belong to a separate enchantment which explicitly permits this slot.
        restricted.removeAll(allowed);
        if (restricted.isEmpty()) {
            original.call(stack, requested, consumer);
            return;
        }
        original.call(stack, requested, (BiConsumer<Holder<Attribute>, AttributeModifier>) (attribute, modifier) -> {
            if (!restricted.contains(new ModifierKey(attribute, modifier.id()))) consumer.accept(attribute, modifier);
        });
    }
}
