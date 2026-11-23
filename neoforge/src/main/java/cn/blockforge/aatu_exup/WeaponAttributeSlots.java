package cn.blockforge.aatu_exup;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

/** Resolves the weapon's existing slot without recursively querying its attribute event. */
public final class WeaponAttributeSlots {
    private WeaponAttributeSlots() {
    }

    public static EquipmentSlotGroup resolve(ItemAttributeModifierEvent event, Holder<Attribute> attribute,
                                              EquipmentSlotGroup fallback) {
        List<ItemAttributeModifiers.Entry> defaults = event.getDefaultModifiers().modifiers();
        EquipmentSlotGroup slot = matchingAttribute(defaults, attribute);
        if (slot != null) return slot;
        List<ItemAttributeModifiers.Entry> external = event.getModifiers().stream()
                .filter(WeaponAttributeSlots::isExternalModifier).toList();
        slot = matchingAttribute(external, attribute);
        if (slot == null) slot = weaponSlot(defaults);
        if (slot == null) slot = weaponSlot(external);
        return slot != null ? slot : fallback;
    }

    private static EquipmentSlotGroup weaponSlot(List<ItemAttributeModifiers.Entry> entries) {
        EquipmentSlotGroup slot = matchingAttribute(entries, Attributes.ATTACK_DAMAGE);
        if (slot == null) slot = matchingAttribute(entries, Attributes.ATTACK_SPEED);
        if (slot != null) return slot;
        return combine(entries.stream().map(ItemAttributeModifiers.Entry::slot).toList());
    }

    private static EquipmentSlotGroup matchingAttribute(List<ItemAttributeModifiers.Entry> entries,
                                                         Holder<Attribute> attribute) {
        if (attribute == null) return null;
        List<ItemAttributeModifiers.Entry> matches = entries.stream()
                .filter(entry -> entry.attribute().equals(attribute)).toList();
        ResourceLocation baseId = attribute.value().getBaseId();
        if (baseId != null) {
            List<ItemAttributeModifiers.Entry> base = matches.stream()
                    .filter(entry -> entry.modifier().is(baseId)).toList();
            if (!base.isEmpty()) matches = base;
        }
        return combine(matches.stream().map(ItemAttributeModifiers.Entry::slot).toList());
    }

    private static EquipmentSlotGroup combine(List<EquipmentSlotGroup> slots) {
        if (slots.isEmpty()) return null;
        if (slots.contains(EquipmentSlotGroup.ANY)) return EquipmentSlotGroup.ANY;
        boolean main = slots.contains(EquipmentSlotGroup.MAINHAND) || slots.contains(EquipmentSlotGroup.HAND);
        boolean off = slots.contains(EquipmentSlotGroup.OFFHAND) || slots.contains(EquipmentSlotGroup.HAND);
        if (main && off) return EquipmentSlotGroup.HAND;
        if (main) return EquipmentSlotGroup.MAINHAND;
        if (off) return EquipmentSlotGroup.OFFHAND;
        return slots.get(0);
    }

    private static boolean isExternalModifier(ItemAttributeModifiers.Entry entry) {
        String namespace = entry.modifier().id().getNamespace();
        return !namespace.equals("aatu_exup") && !namespace.equals("aatu_ut")
                && !namespace.equals("forbidden_knowledge");
    }
}
