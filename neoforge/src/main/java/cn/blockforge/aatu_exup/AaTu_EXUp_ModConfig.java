package cn.blockforge.aatu_exup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class AaTu_EXUp_ModConfig {
    public static final double ARMOR_SPEED_HARD_LIMIT = 0.05D;
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue ATTRIBUTE_BOOK_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue SOUL_INFUSION_BOOK_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue DROP_MINIMUM_HEALTH;

    public static final ModConfigSpec.ConfigValue<String> ENHANCEMENT_COST_ITEM;

    public static final ModConfigSpec.IntValue SOUL_INJECTION_MIN;
    public static final ModConfigSpec.IntValue SOUL_INJECTION_MAX;
    public static final ModConfigSpec.ConfigValue<String> SOUL_COST_ITEM;

    public static final ModConfigSpec.DoubleValue ATTACK_DAMAGE_LIMIT;
    public static final ModConfigSpec.DoubleValue ATTACK_DAMAGE_MIN;
    public static final ModConfigSpec.DoubleValue ATTACK_DAMAGE_MAX;
    public static final ModConfigSpec.DoubleValue ATTACK_SPEED_LIMIT;
    public static final ModConfigSpec.DoubleValue ATTACK_SPEED_MIN;
    public static final ModConfigSpec.DoubleValue ATTACK_SPEED_MAX;
    public static final ModConfigSpec.DoubleValue PENETRATION_LIMIT;
    public static final ModConfigSpec.DoubleValue PENETRATION_MIN;
    public static final ModConfigSpec.DoubleValue PENETRATION_MAX;
    public static final ModConfigSpec.DoubleValue PENETRATION_DAMAGE_RATIO;
    public static final ModConfigSpec.BooleanValue PENETRATION_AFFECTS_PLAYERS;
    public static final ModConfigSpec.DoubleValue ARMOR_LIMIT;
    public static final ModConfigSpec.DoubleValue ARMOR_MIN;
    public static final ModConfigSpec.DoubleValue ARMOR_MAX;
    public static final ModConfigSpec.DoubleValue ARMOR_TOUGHNESS_LIMIT;
    public static final ModConfigSpec.DoubleValue ARMOR_TOUGHNESS_MIN;
    public static final ModConfigSpec.DoubleValue ARMOR_TOUGHNESS_MAX;
    public static final ModConfigSpec.DoubleValue ARMOR_HEALTH_LIMIT;
    public static final ModConfigSpec.DoubleValue ARMOR_HEALTH_MIN;
    public static final ModConfigSpec.DoubleValue ARMOR_HEALTH_MAX;
    public static final ModConfigSpec.DoubleValue ARMOR_SPEED_LIMIT;
    public static final ModConfigSpec.DoubleValue ARMOR_SPEED_MIN;
    public static final ModConfigSpec.DoubleValue ARMOR_SPEED_MAX;

    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_BOOK_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_DROP_MINIMUM_HEALTH;

    public static final ModConfigSpec.ConfigValue<String> FORBIDDEN_KNOWLEDGE_ENHANCEMENT_COST_ITEM;

    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_POWER_LIMIT;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_POWER_MIN;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_POWER_MAX;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_HEALING_LIMIT;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_HEALING_MIN;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_HEALING_MAX;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_LIMIT;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MIN;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MAX;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_LIMIT;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MIN;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MAX;
    public static final ModConfigSpec.DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_DAMAGE_RATIO;
    public static final ModConfigSpec.BooleanValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_AFFECTS_PLAYERS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("drops");
        ATTRIBUTE_BOOK_DROP_CHANCE = builder.comment("仅玩家击杀且生命值达标的生物掉落属性强化书的概率").defineInRange(
                "attribute_book_drop_chance", 0.25D, 0.0D, 1.0D);
        SOUL_INFUSION_BOOK_DROP_CHANCE = builder.comment("仅玩家击杀且生命值达标的生物掉落注灵书的概率").defineInRange(
                "soul_infusion_book_drop_chance", 0.25D, 0.0D, 1.0D);
        DROP_MINIMUM_HEALTH = builder.comment("触发特殊书籍掉落所需的生物最大生命值（仅玩家击杀）").defineInRange(
                "drop_minimum_health", 400.0D, 1.0D, 1000000.0D);
        builder.pop();

        builder.push("enhancement");
        ENHANCEMENT_COST_ITEM = builder.comment("属性强化消耗的物品，格式为 namespace:item").define(
                "cost_item", "minecraft:nether_star", value -> value instanceof String string
                        && ResourceLocation.tryParse(string) != null);
        builder.pop();

        builder.push("soul");
        SOUL_INJECTION_MIN = builder.comment("注灵书单次注灵数的最小值").defineInRange(
                "injection_min", 1, 1, AttributeData.MAX_SOUL);
        SOUL_INJECTION_MAX = builder.comment("注灵书单次注灵数的最大值").defineInRange(
                "injection_max", 5, 1, AttributeData.MAX_SOUL);
        SOUL_COST_ITEM = builder.comment("注灵消耗的物品，格式为 namespace:item").define(
                "cost_item", "minecraft:diamond", value -> value instanceof String string
                        && ResourceLocation.tryParse(string) != null);
        builder.pop();

        builder.push("attributes");
        ATTACK_DAMAGE_LIMIT = defineLimit(builder, "attack_damage_limit", 233.0D, "攻击伤害累计上限");
        ATTACK_DAMAGE_MIN = defineDouble(builder, "attack_damage_min", 0.5D, "攻击伤害最小值");
        ATTACK_DAMAGE_MAX = defineDouble(builder, "attack_damage_max", 3.5D, "攻击伤害最大值");
        ATTACK_SPEED_LIMIT = defineLimit(builder, "attack_speed_limit", 1.2D, "攻击速度累计上限");
        ATTACK_SPEED_MIN = defineDouble(builder, "attack_speed_min", 0.05D, "攻击速度最小值");
        ATTACK_SPEED_MAX = defineDouble(builder, "attack_speed_max", 0.20D, "攻击速度最大值");
        PENETRATION_LIMIT = defineLimit(builder, "penetration_limit", 1.0D, 1.0D, "贯通累计上限");
        PENETRATION_MIN = defineDouble(builder, "penetration_min", 0.01D, 1.0D, "贯通最小值");
        PENETRATION_MAX = defineDouble(builder, "penetration_max", 0.05D, 1.0D, "贯通最大值");
        PENETRATION_DAMAGE_RATIO = defineDouble(builder, "penetration_damage_ratio", 0.5D,
                "贯通触发时造成的真实伤害占攻击力的比例（0.25 = 25%）");
        PENETRATION_AFFECTS_PLAYERS = builder.comment("贯通额外真实伤害是否作用于玩家（默认关闭）")
                .define("penetration_affects_players", false);
        ARMOR_LIMIT = defineLimit(builder, "armor_limit", 233.0D, "护甲累计上限");
        ARMOR_MIN = defineDouble(builder, "armor_min", 0.5D, "护甲最小值");
        ARMOR_MAX = defineDouble(builder, "armor_max", 2.0D, "护甲最大值");
        ARMOR_TOUGHNESS_LIMIT = defineLimit(builder, "armor_toughness_limit", 233.0D, "护甲韧性累计上限");
        ARMOR_TOUGHNESS_MIN = defineDouble(builder, "armor_toughness_min", 0.5D, "护甲韧性最小值");
        ARMOR_TOUGHNESS_MAX = defineDouble(builder, "armor_toughness_max", 1.5D, "护甲韧性最大值");
        ARMOR_HEALTH_LIMIT = defineLimit(builder, "armor_health_limit", 20.0D, "最大生命值累计上限");
        ARMOR_HEALTH_MIN = defineDouble(builder, "armor_health_min", 1.0D, "最大生命值最小值");
        ARMOR_HEALTH_MAX = defineDouble(builder, "armor_health_max", 1.0D, "最大生命值最大值");
        ARMOR_SPEED_LIMIT = defineLimit(builder, "armor_speed_limit", ARMOR_SPEED_HARD_LIMIT, ARMOR_SPEED_HARD_LIMIT, "护甲移动速度累计上限");
        ARMOR_SPEED_MIN = defineDouble(builder, "armor_speed_min", 0.01D, ARMOR_SPEED_HARD_LIMIT, "护甲移动速度最小值");
        ARMOR_SPEED_MAX = defineDouble(builder, "armor_speed_max", 0.02D, ARMOR_SPEED_HARD_LIMIT, "护甲移动速度最大值");
        builder.pop();

        builder.push("forbidden_knowledge");
        builder.push("drops");
        FORBIDDEN_KNOWLEDGE_BOOK_DROP_CHANCE = builder
                .comment("仅玩家击杀且生命值达标的生物掉落禁忌学识的概率")
                .defineInRange("book_drop_chance", 0.25D, 0.0D, 1.0D);
        FORBIDDEN_KNOWLEDGE_DROP_MINIMUM_HEALTH = builder
                .comment("触发禁忌学识掉落所需的生物最大生命值（仅玩家击杀）")
                .defineInRange("minimum_health", 400.0D, 1.0D, 1000000.0D);
        builder.pop();

        builder.push("enhancement");
        FORBIDDEN_KNOWLEDGE_ENHANCEMENT_COST_ITEM = builder
                .comment("禁忌学识锻造强化消耗物品，格式为 namespace:item")
                .define("cost_item", "minecraft:nether_star", value -> value instanceof String string
                        && ResourceLocation.tryParse(string) != null);
        builder.pop();

        builder.push("attributes");
        FORBIDDEN_KNOWLEDGE_SPELL_POWER_LIMIT = defineLimit(builder, "spell_power_limit", 233.0D,
                "禁忌学识每项法术强度累计上限（火焰、奥秘、寒冰）");
        FORBIDDEN_KNOWLEDGE_SPELL_POWER_MIN = defineDouble(builder, "spell_power_min", 0.5D,
                "禁忌学识单次法术强度强化最小值（火焰、奥秘、寒冰）");
        FORBIDDEN_KNOWLEDGE_SPELL_POWER_MAX = defineDouble(builder, "spell_power_max", 3.5D,
                "禁忌学识单次法术强度强化最大值（火焰、奥秘、寒冰）");
        FORBIDDEN_KNOWLEDGE_HEALING_LIMIT = defineLimit(builder, "healing_limit", 60.0D,
                "禁忌学识治疗强度累计上限");
        FORBIDDEN_KNOWLEDGE_HEALING_MIN = defineDouble(builder, "healing_min", 0.5D,
                "禁忌学识单次治疗强度强化最小值");
        FORBIDDEN_KNOWLEDGE_HEALING_MAX = defineDouble(builder, "healing_max", 2.5D,
                "禁忌学识单次治疗强度强化最大值");
        FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_LIMIT = defineLimit(builder, "ranged_damage_limit", 233.0D,
                "禁忌学识远程伤害累计上限");
        FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MIN = defineDouble(builder, "ranged_damage_min", 0.5D,
                "禁忌学识单次远程伤害强化最小值");
        FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MAX = defineDouble(builder, "ranged_damage_max", 3.5D,
                "禁忌学识单次远程伤害强化最大值");
        FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_LIMIT = defineLimit(builder, "spell_penetration_limit", 1.0D, 1.0D,
                "禁忌学识法术贯通累计上限（1.0 = 100%）");
        FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MIN = defineDouble(builder, "spell_penetration_min", 0.01D, 1.0D,
                "禁忌学识单次法术贯通强化最小值（0.03 = 3%）");
        FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MAX = defineDouble(builder, "spell_penetration_max", 0.05D, 1.0D,
                "禁忌学识单次法术贯通强化最大值（0.12 = 12%）");
        FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_DAMAGE_RATIO = defineDouble(builder,
                "spell_penetration_damage_ratio", 0.5D,
                "禁忌学识法术贯通伤害占对应法术强度的比例（0.25 = 25%）");
        FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_AFFECTS_PLAYERS = builder
                .comment("禁忌学识法术贯通额外真实伤害是否作用于玩家（默认关闭）")
                .define("spell_penetration_affects_players", false);
        builder.pop();
        builder.pop();

        SPEC = builder.build();
    }

    private AaTu_EXUp_ModConfig() {
    }

    private static ModConfigSpec.DoubleValue defineDouble(
            ModConfigSpec.Builder builder, String name, double defaultValue, String comment) {
        return defineDouble(builder, name, defaultValue, 1000000.0D, comment);
    }

    private static ModConfigSpec.DoubleValue defineDouble(
            ModConfigSpec.Builder builder, String name, double defaultValue, double maximum, String comment) {
        return builder.comment(comment).defineInRange(name, defaultValue, 0.0D, maximum);
    }

    private static ModConfigSpec.DoubleValue defineLimit(
            ModConfigSpec.Builder builder, String name, double defaultValue, String comment) {
        return defineLimit(builder, name, defaultValue, 1000000.0D, comment);
    }

    private static ModConfigSpec.DoubleValue defineLimit(
            ModConfigSpec.Builder builder, String name, double defaultValue, double maximum, String comment) {
        return builder.comment(comment).defineInRange(name, defaultValue, 0.0D, maximum);
    }

    public static Item enhancementCostItem() {
        return configuredItem(ENHANCEMENT_COST_ITEM, Items.NETHER_STAR);
    }

    public static Component enhancementCostItemName() {
        return new ItemStack(enhancementCostItem()).getHoverName();
    }

    public static Item forbiddenKnowledgeEnhancementCostItem() {
        return configuredItem(FORBIDDEN_KNOWLEDGE_ENHANCEMENT_COST_ITEM, Items.NETHER_STAR);
    }

    public static Component forbiddenKnowledgeEnhancementCostItemName() {
        return new ItemStack(forbiddenKnowledgeEnhancementCostItem()).getHoverName();
    }

    public static Item soulInfusionCostItem() {
        return configuredItem(SOUL_COST_ITEM, Items.DIAMOND);
    }

    public static Component soulInfusionCostItemName() {
        return new ItemStack(soulInfusionCostItem()).getHoverName();
    }

    private static Item configuredItem(ModConfigSpec.ConfigValue<String> value, Item fallback) {
        String configuredValue = value.get();
        ResourceLocation configuredId = configuredValue == null ? null : ResourceLocation.tryParse(configuredValue);
        if (configuredId == null) {
            return fallback;
        }

        return BuiltInRegistries.ITEM.getOptional(configuredId)
                .filter(item -> item != Items.AIR)
                .orElse(fallback);
    }

    public static double attributeLimit(String type) {
        return switch (type) {
            case AttributeEnhancement.ATTACK_DAMAGE -> ATTACK_DAMAGE_LIMIT.getAsDouble();
            case AttributeEnhancement.ATTACK_SPEED -> ATTACK_SPEED_LIMIT.getAsDouble();
            case AttributeEnhancement.PENETRATION -> PENETRATION_LIMIT.getAsDouble();
            case AttributeEnhancement.ARMOR -> ARMOR_LIMIT.getAsDouble();
            case AttributeEnhancement.ARMOR_TOUGHNESS -> ARMOR_TOUGHNESS_LIMIT.getAsDouble();
            case AttributeEnhancement.ARMOR_HEALTH -> ARMOR_HEALTH_LIMIT.getAsDouble();
            case AttributeEnhancement.ARMOR_SPEED -> Math.min(ARMOR_SPEED_HARD_LIMIT, ARMOR_SPEED_LIMIT.getAsDouble());
            default -> 0.0D;
        };
    }

    public static double forbiddenKnowledgeAttributeLimit(String type) {
        return switch (type) {
            case "spell_power:fire", "spell_power:arcane", "spell_power:frost" ->
                    FORBIDDEN_KNOWLEDGE_SPELL_POWER_LIMIT.get();
            case "spell_power:healing" -> FORBIDDEN_KNOWLEDGE_HEALING_LIMIT.get();
            case "ranged_weapon:damage" -> FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_LIMIT.get();
            case "forbidden_knowledge:spell_penetration" ->
                    FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_LIMIT.get();
            default -> 0.0D;
        };
    }

    public static int randomSoulAmount(RandomSource random) {
        int min = clampSoul(SOUL_INJECTION_MIN.getAsInt());
        int max = Math.max(min, clampSoul(SOUL_INJECTION_MAX.getAsInt()));
        return min + random.nextInt(max - min + 1);
    }

    public static double randomAttributeValue(String type, RandomSource random) {
        return switch (type) {
            case AttributeEnhancement.ATTACK_DAMAGE -> randomBetween(
                    ATTACK_DAMAGE_MIN.getAsDouble(), ATTACK_DAMAGE_MAX.getAsDouble(), random);
            case AttributeEnhancement.ATTACK_SPEED -> randomBetween(
                    ATTACK_SPEED_MIN.getAsDouble(), ATTACK_SPEED_MAX.getAsDouble(), random);
            case AttributeEnhancement.PENETRATION -> randomBetween(
                    PENETRATION_MIN.getAsDouble(), PENETRATION_MAX.getAsDouble(), random);
            case AttributeEnhancement.ARMOR -> randomBetween(
                    ARMOR_MIN.getAsDouble(), ARMOR_MAX.getAsDouble(), random);
            case AttributeEnhancement.ARMOR_TOUGHNESS -> randomBetween(
                    ARMOR_TOUGHNESS_MIN.getAsDouble(), ARMOR_TOUGHNESS_MAX.getAsDouble(), random);
            case AttributeEnhancement.ARMOR_HEALTH -> randomBetween(
                    ARMOR_HEALTH_MIN.getAsDouble(), ARMOR_HEALTH_MAX.getAsDouble(), random);
            case AttributeEnhancement.ARMOR_SPEED -> randomBetween(
                    ARMOR_SPEED_MIN.getAsDouble(), ARMOR_SPEED_MAX.getAsDouble(), random);
            default -> 0.0D;
        };
    }

    public static double randomForbiddenKnowledgeAttributeValue(String type, RandomSource random) {
        return switch (type) {
            case "spell_power:fire", "spell_power:arcane", "spell_power:frost" ->
                    randomBetween(FORBIDDEN_KNOWLEDGE_SPELL_POWER_MIN.get(),
                            FORBIDDEN_KNOWLEDGE_SPELL_POWER_MAX.get(), random);
            case "spell_power:healing" -> randomBetween(FORBIDDEN_KNOWLEDGE_HEALING_MIN.get(),
                    FORBIDDEN_KNOWLEDGE_HEALING_MAX.get(), random);
            case "ranged_weapon:damage" -> randomBetween(FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MIN.get(),
                    FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MAX.get(), random);
            case "forbidden_knowledge:spell_penetration" ->
                    randomBetween(FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MIN.get(),
                            FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MAX.get(), random);
            default -> 0.0D;
        };
    }

    private static int clampSoul(int value) {
        return Math.max(1, Math.min(AttributeData.MAX_SOUL, value));
    }

    private static double randomBetween(double configuredMin, double configuredMax, RandomSource random) {
        double min = Math.max(0.0D, Math.min(configuredMin, configuredMax));
        double max = Math.max(min, Math.max(0.0D, Math.max(configuredMin, configuredMax)));
        return min + random.nextDouble() * (max - min);
    }
}
