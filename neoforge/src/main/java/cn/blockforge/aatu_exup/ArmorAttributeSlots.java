package cn.blockforge.aatu_exup;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class ArmorAttributeSlots {
    private ArmorAttributeSlots() {
    }

    public static EquipmentSlot armorSlot(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem armor)) return null;
        EquipmentSlot slot = stack.getEquipmentSlot();
        if (slot == null) slot = armor.getEquipmentSlot();
        return slot != null && slot.isArmor() ? slot : null;
    }

    public static ItemAttributeModifiers matchArmorSlot(ItemStack stack, ItemAttributeModifiers modifiers) {
        EquipmentSlot slot = armorSlot(stack);
        if (slot == null) return modifiers;
        if (modifiers.modifiers().stream().noneMatch(entry -> entry.slot() == EquipmentSlotGroup.ARMOR)) {
            return modifiers;
        }
        EquipmentSlotGroup specificSlot = EquipmentSlotGroup.bySlot(slot);
        List<ItemAttributeModifiers.Entry> entries = new ArrayList<>(modifiers.modifiers().size());
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            entries.add(entry.slot() == EquipmentSlotGroup.ARMOR
                    ? new ItemAttributeModifiers.Entry(entry.attribute(), entry.modifier(), specificSlot)
                    : entry);
        }
        return new ItemAttributeModifiers(List.copyOf(entries), modifiers.showInTooltip());
    }

    public static AttributeModifier withSlotId(AttributeModifier modifier, EquipmentSlotGroup slot) {
        ResourceLocation id = modifier.id();
        String suffix = "/" + EquipmentSlotGroup.ARMOR.getSerializedName();
        if (!id.getPath().endsWith(suffix)) return modifier;
        ResourceLocation adjusted = id.withPath(path -> path.substring(0, path.length() - suffix.length())
                + "/" + slot.getSerializedName());
        return new AttributeModifier(adjusted, modifier.amount(), modifier.operation());
    }

    public record ModifierKey(Holder<Attribute> attribute, ResourceLocation id) {
    }
}
