package cn.blockforge.aatu_exup;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AttributeEnhancement {
    public static final String ATTACK_DAMAGE = "attack_damage";
    public static final String ATTACK_SPEED = "attack_speed";
    public static final String PENETRATION = "penetration";
    public static final String ARMOR = "armor";
    public static final String ARMOR_TOUGHNESS = "armor_toughness";
    public static final String ARMOR_HEALTH = "armor_health";
    public static final String ARMOR_SPEED = "armor_speed";

    private static final Class<?> STAFF_ITEM_CLASS = resolveStaffItemClass();

    private AttributeEnhancement() {
    }

    public record Roll(String type, double value) {
    }

    public static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof DiggerItem
                || stack.getItem() instanceof MaceItem || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem;
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public static boolean isUpgradeable(ItemStack stack) {
        return isWeapon(stack) || isArmor(stack);
    }

    public static boolean isSoulInfusable(ItemStack stack) {
        return isUpgradeable(stack) || (STAFF_ITEM_CLASS != null && STAFF_ITEM_CLASS.isInstance(stack.getItem()));
    }

    public static boolean hasAvailableAttribute(ItemStack stack) {
        return hasAvailableAttribute(stack, AttributeData.getEnhancements(stack));
    }

    static boolean hasAvailableAttribute(ItemStack stack, List<AttributeData.Enhancement> enhancements) {
        for (String type : supportedTypes(stack)) {
            if (remainingCapacity(stack, type, enhancements) > 0.0D) {
                return true;
            }
        }
        return false;
    }

    public static Roll nextRoll(ItemStack stack) {
        return nextRoll(stack, AttributeData.getEnhancements(stack));
    }

    static Roll nextRoll(ItemStack stack, List<AttributeData.Enhancement> enhancements) {
        RandomSource random = RandomSource.create(seed(stack));
        List<String> available = new ArrayList<>();
        for (String type : supportedTypes(stack)) {
            if (remainingCapacity(stack, type, enhancements) > 0.0D) {
                available.add(type);
            }
        }
        if (available.isEmpty()) {
            return null;
        }

        String type = available.get(random.nextInt(available.size()));
        double value = Math.min(AaTu_EXUp_ModConfig.randomAttributeValue(type, random),
                remainingCapacity(stack, type, enhancements));
        return value > 0.0D ? new Roll(type, value) : null;
    }

    private static List<String> supportedTypes(ItemStack stack) {
        if (isArmor(stack)) {
            return List.of(ARMOR, ARMOR_TOUGHNESS, ARMOR_HEALTH, ARMOR_SPEED);
        }
        return List.of(ATTACK_DAMAGE, ATTACK_SPEED, PENETRATION);
    }

    private static double remainingCapacity(
            ItemStack stack, String type, List<AttributeData.Enhancement> enhancements) {
        return Math.max(0.0D, AaTu_EXUp_ModConfig.attributeLimit(type) - enhancementValue(enhancements, type));
    }

    private static double enhancementValue(List<AttributeData.Enhancement> enhancements, String type) {
        for (AttributeData.Enhancement enhancement : enhancements) {
            if (enhancement.type().equals(type)) {
                return enhancement.value();
            }
        }
        return 0.0D;
    }

    public static void apply(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        List<AttributeData.Enhancement> enhancements = AttributeData.getEnhancements(stack);
        EquipmentSlotGroup slotGroup = stack.getItem() instanceof ArmorItem armor
                ? armorSlot(stack, armor) : EquipmentSlotGroup.HAND;
        for (AttributeData.Enhancement enhancement : enhancements) {
            Holder<Attribute> attribute = attributeFor(enhancement.type());
            if (attribute == null) {
                continue;
            }
            EquipmentSlotGroup fallback = PENETRATION.equals(enhancement.type())
                    ? EquipmentSlotGroup.MAINHAND : EquipmentSlotGroup.HAND;
            EquipmentSlotGroup enhancementSlot = stack.getItem() instanceof ArmorItem
                    ? slotGroup : WeaponAttributeSlots.resolve(event, attribute, fallback);
            event.addModifier(attribute,
                    modifier(modifierId(stack, enhancement.type()), enhancement.value()), enhancementSlot);
        }
    }

    private static EquipmentSlotGroup armorSlot(ItemStack stack, ArmorItem armor) {
        EquipmentSlot slot = stack.getEquipmentSlot();
        return EquipmentSlotGroup.bySlot(slot != null ? slot : armor.getEquipmentSlot());
    }

    public static boolean isKnownType(String type) {
        return switch (type) {
            case ATTACK_DAMAGE, ATTACK_SPEED, PENETRATION, ARMOR,
                    ARMOR_TOUGHNESS, ARMOR_HEALTH, ARMOR_SPEED -> true;
            default -> false;
        };
    }

    public static Holder<Attribute> attributeFor(String type) {
        return switch (type) {
            case ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case PENETRATION -> AaTu_EXUp_Mod.PENETRATION;
            case ARMOR -> Attributes.ARMOR;
            case ARMOR_TOUGHNESS -> Attributes.ARMOR_TOUGHNESS;
            case ARMOR_HEALTH -> Attributes.MAX_HEALTH;
            case ARMOR_SPEED -> Attributes.MOVEMENT_SPEED;
            default -> null;
        };
    }

    public static String attributeNameKey(String type) {
        return switch (type) {
            case ATTACK_DAMAGE -> "attribute.name.generic.attack_damage";
            case ATTACK_SPEED -> "attribute.name.generic.attack_speed";
            case PENETRATION -> "attribute.name.aatu_exup.penetration";
            case ARMOR -> "attribute.name.generic.armor";
            case ARMOR_TOUGHNESS -> "attribute.name.generic.armor_toughness";
            case ARMOR_HEALTH -> "attribute.name.generic.max_health";
            case ARMOR_SPEED -> "attribute.name.generic.movement_speed";
            default -> "attribute.name.generic.max_health";
        };
    }

    public static String formatValue(String type, double value) {
        if (PENETRATION.equals(type)) {
            return String.format(Locale.ROOT, "%.1f%%", value * 100.0D);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static Class<?> resolveStaffItemClass() {
        try {
            return Class.forName("net.spell_engine.api.item.weapon.StaffItem", false,
                    AttributeEnhancement.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            return null;
        }
    }

    private static AttributeModifier modifier(ResourceLocation id, double value) {
        return new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static ResourceLocation modifierId(ItemStack stack, String type) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String itemPath = itemId == null ? "unknown_item" : itemId.getNamespace() + "_" + itemId.getPath();
        return ResourceLocation.fromNamespaceAndPath(AaTu_EXUp_Mod.MOD_ID, "enhanced_" + itemPath + "_" + type);
    }

    private static long seed(ItemStack stack) {
        long seed = Integer.toUnsignedLong(ItemStack.hashItemAndComponents(stack));
        seed ^= 0x9E3779B97F4A7C15L;
        seed = (seed ^ (seed >>> 30)) * 0xBF58476D1CE4E5B9L;
        seed = (seed ^ (seed >>> 27)) * 0x94D049BB133111EBL;
        return seed ^ (seed >>> 31);
    }
}
