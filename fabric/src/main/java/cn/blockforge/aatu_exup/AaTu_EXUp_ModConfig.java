package cn.blockforge.aatu_exup;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

public final class AaTu_EXUp_ModConfig {
    public static final double ARMOR_SPEED_HARD_LIMIT = 0.05D;

    private static final Logger LOGGER = LoggerFactory.getLogger(AaTu_EXUp_Mod.MOD_ID);
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("aatu_exup.properties");
    private static final List<Value> VALUES = new ArrayList<>();

    public static final DoubleValue ATTRIBUTE_BOOK_DROP_CHANCE = doubleValue(
            "drops.attribute_book_drop_chance", 0.12D, 0.0D, 1.0D);
    public static final DoubleValue SOUL_INFUSION_BOOK_DROP_CHANCE = doubleValue(
            "drops.soul_infusion_book_drop_chance", 0.08D, 0.0D, 1.0D);
    public static final DoubleValue DROP_MINIMUM_HEALTH = doubleValue(
            "drops.drop_minimum_health", 40.0D, 1.0D, 1000000.0D);

    public static final StringValue ENHANCEMENT_COST_ITEM = itemValue(
            "enhancement.cost_item", "minecraft:nether_star");

    public static final IntValue SOUL_INJECTION_MIN = intValue(
            "soul.injection_min", 1, 1, AttributeData.MAX_SOUL);
    public static final IntValue SOUL_INJECTION_MAX = intValue(
            "soul.injection_max", 3, 1, AttributeData.MAX_SOUL);
    public static final StringValue SOUL_COST_ITEM = itemValue(
            "soul.cost_item", "minecraft:diamond");

    public static final DoubleValue ATTACK_DAMAGE_LIMIT = doubleValue(
            "attributes.attack_damage_limit", 100.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ATTACK_DAMAGE_MIN = doubleValue(
            "attributes.attack_damage_min", 0.5D, 0.0D, 1000000.0D);
    public static final DoubleValue ATTACK_DAMAGE_MAX = doubleValue(
            "attributes.attack_damage_max", 2.5D, 0.0D, 1000000.0D);
    public static final DoubleValue ATTACK_SPEED_LIMIT = doubleValue(
            "attributes.attack_speed_limit", 20.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ATTACK_SPEED_MIN = doubleValue(
            "attributes.attack_speed_min", 0.05D, 0.0D, 1000000.0D);
    public static final DoubleValue ATTACK_SPEED_MAX = doubleValue(
            "attributes.attack_speed_max", 0.20D, 0.0D, 1000000.0D);
    public static final DoubleValue PENETRATION_LIMIT = doubleValue(
            "attributes.penetration_limit", 1.0D, 0.0D, 1.0D);
    public static final DoubleValue PENETRATION_MIN = doubleValue(
            "attributes.penetration_min", 0.03D, 0.0D, 1.0D);
    public static final DoubleValue PENETRATION_MAX = doubleValue(
            "attributes.penetration_max", 0.12D, 0.0D, 1.0D);
    public static final DoubleValue PENETRATION_DAMAGE_RATIO = doubleValue(
            "attributes.penetration_damage_ratio", 0.25D, 0.0D, 1000000.0D);
    public static final BooleanValue PENETRATION_AFFECTS_PLAYERS = booleanValue(
            "attributes.penetration_affects_players", false);
    public static final DoubleValue ARMOR_LIMIT = doubleValue(
            "attributes.armor_limit", 100.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_MIN = doubleValue(
            "attributes.armor_min", 1.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_MAX = doubleValue(
            "attributes.armor_max", 3.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_TOUGHNESS_LIMIT = doubleValue(
            "attributes.armor_toughness_limit", 100.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_TOUGHNESS_MIN = doubleValue(
            "attributes.armor_toughness_min", 0.5D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_TOUGHNESS_MAX = doubleValue(
            "attributes.armor_toughness_max", 1.5D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_HEALTH_LIMIT = doubleValue(
            "attributes.armor_health_limit", 100.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_HEALTH_MIN = doubleValue(
            "attributes.armor_health_min", 1.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_HEALTH_MAX = doubleValue(
            "attributes.armor_health_max", 3.0D, 0.0D, 1000000.0D);
    public static final DoubleValue ARMOR_SPEED_LIMIT = doubleValue(
            "attributes.armor_speed_limit", ARMOR_SPEED_HARD_LIMIT, 0.0D, ARMOR_SPEED_HARD_LIMIT);
    public static final DoubleValue ARMOR_SPEED_MIN = doubleValue(
            "attributes.armor_speed_min", 0.01D, 0.0D, ARMOR_SPEED_HARD_LIMIT);
    public static final DoubleValue ARMOR_SPEED_MAX = doubleValue(
            "attributes.armor_speed_max", ARMOR_SPEED_HARD_LIMIT, 0.0D, ARMOR_SPEED_HARD_LIMIT);

    public static final DoubleValue FORBIDDEN_KNOWLEDGE_BOOK_DROP_CHANCE = doubleValue(
            "forbidden_knowledge.drops.book_drop_chance", 0.12D, 0.0D, 1.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_DROP_MINIMUM_HEALTH = doubleValue(
            "forbidden_knowledge.drops.minimum_health", 40.0D, 1.0D, 1000000.0D);

    public static final StringValue FORBIDDEN_KNOWLEDGE_ENHANCEMENT_COST_ITEM = itemValue(
            "forbidden_knowledge.enhancement.cost_item", "minecraft:nether_star");

    public static final DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_POWER_LIMIT = doubleValue(
            "forbidden_knowledge.attributes.spell_power_limit", 20.0D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_POWER_MIN = doubleValue(
            "forbidden_knowledge.attributes.spell_power_min", 0.5D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_POWER_MAX = doubleValue(
            "forbidden_knowledge.attributes.spell_power_max", 1.5D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_HEALING_LIMIT = doubleValue(
            "forbidden_knowledge.attributes.healing_limit", 20.0D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_HEALING_MIN = doubleValue(
            "forbidden_knowledge.attributes.healing_min", 0.5D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_HEALING_MAX = doubleValue(
            "forbidden_knowledge.attributes.healing_max", 1.5D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_LIMIT = doubleValue(
            "forbidden_knowledge.attributes.ranged_damage_limit", 20.0D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MIN = doubleValue(
            "forbidden_knowledge.attributes.ranged_damage_min", 0.5D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MAX = doubleValue(
            "forbidden_knowledge.attributes.ranged_damage_max", 1.5D, 0.0D, 1000000.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_LIMIT = doubleValue(
            "forbidden_knowledge.attributes.spell_penetration_limit", 1.0D, 0.0D, 1.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MIN = doubleValue(
            "forbidden_knowledge.attributes.spell_penetration_min", 0.03D, 0.0D, 1.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MAX = doubleValue(
            "forbidden_knowledge.attributes.spell_penetration_max", 0.12D, 0.0D, 1.0D);
    public static final DoubleValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_DAMAGE_RATIO = doubleValue(
            "forbidden_knowledge.attributes.spell_penetration_damage_ratio", 0.25D, 0.0D, 1000000.0D);
    public static final BooleanValue FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_AFFECTS_PLAYERS = booleanValue(
            "forbidden_knowledge.attributes.spell_penetration_affects_players", false);

    private AaTu_EXUp_ModConfig() {
    }

    public static void load() {
        Properties properties = new OrderedProperties();
        if (Files.isRegularFile(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                properties.load(reader);
            } catch (IOException exception) {
                LOGGER.warn("Unable to read configuration file {}. Using defaults.", CONFIG_PATH, exception);
            }
        }

        for (Value value : VALUES) {
            value.load(properties);
            properties.setProperty(value.key, value.serialized());
        }

        save(properties);
    }

    public static List<EditableValue> editableValues() {
        return VALUES.stream()
                .map(value -> new EditableValue(value.key, value.serialized()))
                .toList();
    }

    /** Applies all configuration-screen values using the same rules as the properties file. */
    public static void saveEditedValues(Map<String, String> editedValues) {
        Properties properties = new OrderedProperties();
        for (Value value : VALUES) {
            String configuredValue = editedValues == null ? null : editedValues.get(value.key);
            properties.setProperty(value.key, configuredValue == null ? value.serialized() : configuredValue);
        }

        for (Value value : VALUES) {
            value.load(properties);
            properties.setProperty(value.key, value.serialized());
        }
        save(properties);
    }

    private static void save(Properties properties) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                properties.store(writer, "属性强化书 Fabric 配置");
            }
        } catch (IOException exception) {
            LOGGER.warn("Unable to save configuration file {}.", CONFIG_PATH, exception);
        }
    }

    /** Properties.store sorts ordinary properties by key; retain the config screen's field order. */
    private static final class OrderedProperties extends Properties {
        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            Map<String, String> remaining = new TreeMap<>();
            for (Map.Entry<Object, Object> entry : super.entrySet()) {
                if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
                    remaining.put(key, value);
                }
            }

            Set<Map.Entry<Object, Object>> ordered = new LinkedHashSet<>();
            for (Value value : VALUES) {
                String serialized = remaining.remove(value.key);
                if (serialized != null) {
                    ordered.add(Map.entry((Object) value.key, (Object) serialized));
                }
            }
            for (Map.Entry<String, String> entry : remaining.entrySet()) {
                ordered.add(Map.entry((Object) entry.getKey(), (Object) entry.getValue()));
            }
            return ordered;
        }
    }

    private static DoubleValue doubleValue(String key, double defaultValue, double minimum, double maximum) {
        DoubleValue value = new DoubleValue(key, defaultValue, minimum, maximum);
        VALUES.add(value);
        return value;
    }

    private static IntValue intValue(String key, int defaultValue, int minimum, int maximum) {
        IntValue value = new IntValue(key, defaultValue, minimum, maximum);
        VALUES.add(value);
        return value;
    }

    private static BooleanValue booleanValue(String key, boolean defaultValue) {
        BooleanValue value = new BooleanValue(key, defaultValue);
        VALUES.add(value);
        return value;
    }

    private static StringValue itemValue(String key, String defaultValue) {
        StringValue value = new StringValue(key, defaultValue,
                valueToCheck -> valueToCheck != null && ResourceLocation.tryParse(valueToCheck) != null);
        VALUES.add(value);
        return value;
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

    private static Item configuredItem(StringValue value, Item fallback) {
        ResourceLocation configuredId = ResourceLocation.tryParse(value.get());
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
                    FORBIDDEN_KNOWLEDGE_SPELL_POWER_LIMIT.getAsDouble();
            case "spell_power:healing" -> FORBIDDEN_KNOWLEDGE_HEALING_LIMIT.getAsDouble();
            case "ranged_weapon:damage" -> FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_LIMIT.getAsDouble();
            case "forbidden_knowledge:spell_penetration" ->
                    FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_LIMIT.getAsDouble();
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
                    randomBetween(FORBIDDEN_KNOWLEDGE_SPELL_POWER_MIN.getAsDouble(),
                            FORBIDDEN_KNOWLEDGE_SPELL_POWER_MAX.getAsDouble(), random);
            case "spell_power:healing" -> randomBetween(FORBIDDEN_KNOWLEDGE_HEALING_MIN.getAsDouble(),
                    FORBIDDEN_KNOWLEDGE_HEALING_MAX.getAsDouble(), random);
            case "ranged_weapon:damage" -> randomBetween(FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MIN.getAsDouble(),
                    FORBIDDEN_KNOWLEDGE_RANGED_DAMAGE_MAX.getAsDouble(), random);
            case "forbidden_knowledge:spell_penetration" ->
                    randomBetween(FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MIN.getAsDouble(),
                            FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_MAX.getAsDouble(), random);
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

    private abstract static class Value {
        protected final String key;

        private Value(String key) {
            this.key = key;
        }

        abstract void load(Properties properties);

        abstract String serialized();
    }

    public static final class DoubleValue extends Value {
        private final double defaultValue;
        private final double minimum;
        private final double maximum;
        private double value;

        private DoubleValue(String key, double defaultValue, double minimum, double maximum) {
            super(key);
            this.defaultValue = defaultValue;
            this.minimum = minimum;
            this.maximum = maximum;
            this.value = defaultValue;
        }

        @Override
        void load(Properties properties) {
            try {
                value = clamp(Double.parseDouble(properties.getProperty(key, Double.toString(defaultValue))), minimum, maximum);
            } catch (NumberFormatException exception) {
                value = defaultValue;
            }
        }

        @Override
        String serialized() {
            return Double.toString(value);
        }

        public double getAsDouble() {
            return value;
        }
    }

    public static final class IntValue extends Value {
        private final int defaultValue;
        private final int minimum;
        private final int maximum;
        private int value;

        private IntValue(String key, int defaultValue, int minimum, int maximum) {
            super(key);
            this.defaultValue = defaultValue;
            this.minimum = minimum;
            this.maximum = maximum;
            this.value = defaultValue;
        }

        @Override
        void load(Properties properties) {
            try {
                value = Math.clamp(Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue))), minimum, maximum);
            } catch (NumberFormatException exception) {
                value = defaultValue;
            }
        }

        @Override
        String serialized() {
            return Integer.toString(value);
        }

        public int getAsInt() {
            return value;
        }
    }

    public static final class BooleanValue extends Value {
        private final boolean defaultValue;
        private boolean value;

        private BooleanValue(String key, boolean defaultValue) {
            super(key);
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        @Override
        void load(Properties properties) {
            String configured = properties.getProperty(key, Boolean.toString(defaultValue)).trim();
            if ("true".equalsIgnoreCase(configured)) {
                value = true;
            } else if ("false".equalsIgnoreCase(configured)) {
                value = false;
            } else {
                value = defaultValue;
            }
        }

        @Override
        String serialized() {
            return Boolean.toString(value);
        }

        public boolean getAsBoolean() {
            return value;
        }
    }

    public static final class StringValue extends Value {
        private final String defaultValue;
        private final Predicate<String> validator;
        private String value;

        private StringValue(String key, String defaultValue, Predicate<String> validator) {
            super(key);
            this.defaultValue = defaultValue;
            this.validator = validator;
            this.value = defaultValue;
        }

        @Override
        void load(Properties properties) {
            String configured = properties.getProperty(key, defaultValue).trim();
            value = validator.test(configured) ? configured : defaultValue;
        }

        @Override
        String serialized() {
            return value;
        }

        public String get() {
            return value;
        }
    }

    public record EditableValue(String key, String value) {
    }

    private static double clamp(double value, double minimum, double maximum) {
        if (!Double.isFinite(value)) {
            return minimum;
        }
        return Math.max(minimum, Math.min(maximum, value));
    }
}
