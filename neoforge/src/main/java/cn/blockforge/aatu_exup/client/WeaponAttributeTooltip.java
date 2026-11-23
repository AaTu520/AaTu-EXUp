package cn.blockforge.aatu_exup.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;
import cn.blockforge.aatu_exup.AttributeEnhancement;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;

public final class WeaponAttributeTooltip {
    private static final double PLAYER_BASE_ATTACK_DAMAGE = 1.0D;
    private static final boolean INTEGRATION_AVAILABLE = integrationAvailable();

    private WeaponAttributeTooltip() {
    }

    /** Avoid claiming armor, block hats, or items without a native weapon base modifier. */
    public static boolean handles(ItemStack stack) {
        if (!INTEGRATION_AVAILABLE || stack.isEmpty()
                || stack.getItem() instanceof ArmorItem || stack.getItem() instanceof BlockItem) {
            return false;
        }
        try {
            ItemAttributeModifiers defaults = originalModifiers(stack);
            boolean damage = false;
            boolean speed = false;
            for (ItemAttributeModifiers.Entry entry : defaults.modifiers()) {
                if (!isHandGroup(entry.slot())) continue;
                if (isBase(entry.attribute(), entry.modifier(), Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID)
                        || isBase(entry.attribute(), entry.modifier(), Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID)) {
                    return true;
                }
                damage |= entry.attribute().is(Attributes.ATTACK_DAMAGE);
                speed |= entry.attribute().is(Attributes.ATTACK_SPEED);
            }
            return damage && speed || (AttributeEnhancement.isWeapon(stack) && (damage || speed));
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private static boolean integrationAvailable() {
        if (!WeaponTooltipIntegrationMarker.class.isAssignableFrom(AttributeUtil.class)) return false;
        try {
            AttributeUtil.class.getDeclaredMethod("applyTextFor", ItemStack.class, Consumer.class,
                    Multimap.class, AttributeTooltipContext.class);
            return true;
        } catch (NoSuchMethodException | LinkageError exception) {
            return false;
        }
    }

    public static boolean render(ItemStack stack, Consumer<Component> tooltip,
                                 Multimap<Holder<Attribute>, AttributeModifier> modifiers,
                                 AttributeTooltipContext context) {
        if (!handles(stack)) return false;
        List<Holder<Attribute>> replaced = new ArrayList<>();
        List<Component> totals = new ArrayList<>();
        try {
            ItemAttributeModifiers defaults = originalModifiers(stack);
            addTotal(modifiers, defaults, context, Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID, replaced, totals);
            addTotal(modifiers, defaults, context, Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID, replaced, totals);
        } catch (RuntimeException exception) {
            return false;
        }
        if (totals.isEmpty()) return false;

        for (Holder<Attribute> attribute : replaced) {
            modifiers.get(attribute).removeIf(entry -> entry.operation() == AttributeModifier.Operation.ADD_VALUE);
        }
        totals.forEach(tooltip);
        if (!modifiers.isEmpty()) AttributeUtil.applyTextFor(stack, tooltip, modifiers, context);
        return true;
    }

    private static void addTotal(Multimap<Holder<Attribute>, AttributeModifier> modifiers,
                                 ItemAttributeModifiers defaults, AttributeTooltipContext context, Holder<Attribute> target,
                                 ResourceLocation baseId, List<Holder<Attribute>> replaced,
                                 List<Component> totals) {
        for (Holder<Attribute> attribute : modifiers.keySet()) {
            if (!attribute.is(target)) continue;
            List<AttributeModifier> entries = modifiers.get(attribute).stream()
                    .filter(entry -> entry.operation() == AttributeModifier.Operation.ADD_VALUE)
                    .toList();
            if (entries.isEmpty()) return;
            boolean hasBase = entries.stream().anyMatch(entry -> entry.is(baseId));
            boolean hasOriginal = defaults.modifiers().stream().anyMatch(original -> original.attribute().is(target)
                    && entries.stream().anyMatch(entry -> entry.id().equals(original.modifier().id())));
            if (!hasBase && !hasOriginal) return;
            double base = context.player() != null
                    ? context.player().getAttributeBaseValue(attribute) : playerBase(attribute);
            AttributeInstance total = instance(attribute, base, entries);
            totals.add(Component.literal(" ").append(attribute.value().toBaseComponent(
                    total.getValue(), base, false, context.flag())).withStyle(ChatFormatting.DARK_GREEN));
            replaced.add(attribute);
            return;
        }
    }

    /** Keep SimplyTooltips' base summary consistent with the flat-value tooltip total. */
    public static double mainHandTotal(ItemStack stack, Holder<Attribute> target) {
        List<AttributeModifier> modifiers = new ArrayList<>();
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(target) && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                modifiers.add(modifier);
            }
        });
        var player = Minecraft.getInstance().player;
        double base = player != null ? player.getAttributeBaseValue(target) : playerBase(target);
        return instance(target, base, modifiers).getValue();
    }

    public static ResourceLocation preferredModifier(ItemStack stack, EquipmentSlotGroup group,
                                                       Holder<Attribute> target, ResourceLocation baseId) {
        ResourceLocation[] selected = { null };
        int[] priority = { 0 };
        stack.forEachModifier(group, (attribute, modifier) -> {
            if (!attribute.is(target) || modifier.operation() != AttributeModifier.Operation.ADD_VALUE) return;
            int candidate = modifier.is(baseId) ? 2 : 1;
            if (candidate > priority[0]) {
                selected[0] = modifier.id();
                priority[0] = candidate;
            }
        });
        return selected[0];
    }

    private static ItemAttributeModifiers originalModifiers(ItemStack stack) {
        ItemAttributeModifiers defaults = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY);
        return defaults.modifiers().isEmpty() ? stack.getItem().getDefaultAttributeModifiers(stack) : defaults;
    }

    private static AttributeInstance instance(Holder<Attribute> attribute, double base,
                                              Collection<AttributeModifier> modifiers) {
        AttributeInstance result = new AttributeInstance(attribute, ignored -> {});
        result.setBaseValue(base);
        for (AttributeModifier modifier : modifiers) result.addOrUpdateTransientModifier(modifier);
        return result;
    }

    private static double playerBase(Holder<Attribute> attribute) {
        return attribute.is(Attributes.ATTACK_DAMAGE)
                ? PLAYER_BASE_ATTACK_DAMAGE : attribute.value().getDefaultValue();
    }

    private static boolean isHandGroup(EquipmentSlotGroup group) {
        return group == EquipmentSlotGroup.MAINHAND || group == EquipmentSlotGroup.OFFHAND
                || group == EquipmentSlotGroup.HAND || group == EquipmentSlotGroup.ANY;
    }

    private static boolean isBase(Holder<Attribute> attribute, AttributeModifier modifier,
                                  Holder<Attribute> expected, ResourceLocation baseId) {
        return attribute.is(expected) && modifier.is(baseId);
    }
}
