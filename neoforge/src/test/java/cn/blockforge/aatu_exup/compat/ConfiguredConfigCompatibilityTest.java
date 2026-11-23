package cn.blockforge.aatu_exup.compat;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguredConfigCompatibilityTest {
    private static final ModConfigSpec SPEC = AaTu_EXUp_ModConfig.SPEC;
    private static final Map<List<String>, ModConfigSpec.ConfigValue<?>> VALUES = configValues(SPEC.getValues());

    @Test
    void builtInDefaultsMatchRequestedConfiguration() throws IOException {
        String toml;
        try (InputStream input = getClass().getResourceAsStream("/aatu_exup-custom-defaults.toml")) {
            assertNotNull(input);
            toml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        CommentedConfig expected = TomlFormat.instance().createParser().parse(toml);
        CommentedConfig generated = CommentedConfig.inMemory();
        SPEC.correct(generated);

        assertEquals(47, VALUES.size());
        assertEquals(VALUES.size(), countValues(expected));
        Map<List<String>, Object> requested = snapshot(expected);
        VALUES.forEach((path, value) -> {
            Object parsed = requested.get(path);
            // TOML integers are read as Long; NeoForge's bounded IntValue uses Integer.
            if (parsed instanceof Long number && value.getSpec().getDefault() instanceof Integer) {
                parsed = Math.toIntExact(number);
                requested.put(path, parsed);
            }
            assertTrue(value.getSpec().test(parsed), "Invalid requested value at " + path);
        });
        assertEquals(requested, snapshot(generated));
    }

    @Test
    void originalSaveResetsAttackDamageWhenOnlySpeedIsEdited() {
        CommentedConfig original = customizedConfig();
        CommentedConfig changes = CommentedConfig.inMemory();
        changes.set("attributes.attack_speed_limit", 35.0D);
        original.putAll(changes);
        SPEC.correct(original);
        assertEquals(AaTu_EXUp_ModConfig.ATTACK_DAMAGE_LIMIT.getDefault(),
                original.<Double>get("attributes.attack_damage_limit"));
        assertEquals(35.0D, original.<Double>get("attributes.attack_speed_limit"));
    }

    @TestFactory
    Stream<DynamicTest> editingEachSettingPreservesAllOtherSettings() {
        assertEquals(47, VALUES.size());
        return VALUES.entrySet().stream().map(entry -> DynamicTest.dynamicTest(
                String.join(".", entry.getKey()), () -> {
                    CommentedConfig original = customizedConfig();
                    Map<List<String>, Object> expected = snapshot(original);
                    // An explicit reset of one value must also be applied.
                    Object replacement = entry.getValue().getSpec().getDefault();
                    CommentedConfig changes = new SynchronizedConfig();
                    changes.set(entry.getKey(), replacement);
                    expected.put(entry.getKey(), replacement);

                    ConfiguredConfigCompatibility.applyChanges("aatu_exup", original, changes);
                    SPEC.correct(original);

                    assertEquals(expected, snapshot(original));
                    assertTrue(SPEC.isCorrect(original));
                }));
    }

    @Test
    void sequentialAttackDamageAndSpeedEditsSurviveSavingAndReloading() {
        CommentedConfig original = customizedConfig();
        Map<List<String>, Object> expected = snapshot(original);
        edit(original, "attributes.attack_damage_limit", 250.0D);
        edit(original, "attributes.attack_speed_limit", 30.0D);
        edit(original, "attributes.attack_damage_limit", 300.0D);
        expected.put(List.of("attributes", "attack_damage_limit"), 300.0D);
        expected.put(List.of("attributes", "attack_speed_limit"), 30.0D);

        String toml = TomlFormat.instance().createWriter().writeToString(original);
        CommentedConfig reloaded = TomlFormat.instance().createParser().parse(toml);
        SPEC.correct(reloaded);
        assertEquals(expected, snapshot(reloaded));
    }

    @Test
    void multipleChangesPreserveOtherValuesAcrossNestedSections() {
        CommentedConfig original = customizedConfig();
        Map<List<String>, Object> expected = snapshot(original);
        CommentedConfig changes = new SynchronizedConfig();
        changes.set("attributes.attack_damage_limit", 250.0D);
        changes.set("attributes.attack_speed_limit", 30.0D);
        changes.set("drops.attribute_book_drop_chance", 0.5D);
        changes.set("forbidden_knowledge.attributes.spell_power_limit", 50.0D);
        changes.set("forbidden_knowledge.drops.book_drop_chance", 0.8D);
        expected.put(List.of("attributes", "attack_damage_limit"), 250.0D);
        expected.put(List.of("attributes", "attack_speed_limit"), 30.0D);
        expected.put(List.of("drops", "attribute_book_drop_chance"), 0.5D);
        expected.put(List.of("forbidden_knowledge", "attributes", "spell_power_limit"), 50.0D);
        expected.put(List.of("forbidden_knowledge", "drops", "book_drop_chance"), 0.8D);

        ConfiguredConfigCompatibility.applyChanges("aatu_exup", original, changes);
        SPEC.correct(original);
        assertEquals(expected, snapshot(original));
    }

    @Test
    void restoringAllDefaultsStillWorks() {
        CommentedConfig original = customizedConfig();
        CommentedConfig defaults = CommentedConfig.inMemory();
        SPEC.correct(defaults);
        ConfiguredConfigCompatibility.applyChanges("aatu_exup", original, defaults);
        assertEquals(snapshot(defaults), snapshot(original));
    }

    @Test
    void unrelatedModsKeepTheirOriginalSaveBehavior() {
        CommentedConfig original = customizedConfig();
        CommentedConfig changes = CommentedConfig.inMemory();
        changes.set("attributes.attack_speed_limit", 35.0D);
        ConfiguredConfigCompatibility.applyChanges("another_mod", original, changes);
        assertFalse(original.contains("attributes.attack_damage_limit"));
        assertEquals(35.0D, original.<Double>get("attributes.attack_speed_limit"));
    }

    private static void edit(CommentedConfig original, String path, Object value) {
        CommentedConfig changes = new SynchronizedConfig();
        changes.set(path, value);
        ConfiguredConfigCompatibility.applyChanges("aatu_exup", original, changes);
        SPEC.correct(original);
    }

    private static CommentedConfig customizedConfig() {
        CommentedConfig result = new SynchronizedConfig();
        SPEC.correct(result);
        VALUES.forEach((path, configValue) -> {
            Object defaultValue = configValue.getSpec().getDefault();
            Object value;
            if (defaultValue instanceof Boolean bool) {
                value = !bool;
            } else if (defaultValue instanceof Integer number) {
                value = number + 1;
            } else if (defaultValue instanceof Double number) {
                value = number > 1.0D ? number + 1.0D : number * 0.75D;
            } else if (defaultValue instanceof String) {
                value = "minecraft:emerald";
            } else {
                throw new AssertionError("Unexpected configuration type at " + path);
            }
            assertTrue(configValue.getSpec().test(value), "Invalid fixture value at " + path);
            assertNotEquals(defaultValue, value, "Fixture must customize " + path);
            result.set(path, value);
        });
        return result;
    }

    private static Map<List<String>, Object> snapshot(CommentedConfig config) {
        Map<List<String>, Object> values = new LinkedHashMap<>();
        VALUES.keySet().forEach(path -> values.put(path, config.get(path)));
        return values;
    }

    private static Map<List<String>, ModConfigSpec.ConfigValue<?>> configValues(UnmodifiableConfig config) {
        Map<List<String>, ModConfigSpec.ConfigValue<?>> values = new LinkedHashMap<>();
        gatherValues(config, new ArrayList<>(), values);
        return values;
    }

    private static int countValues(UnmodifiableConfig config) {
        int count = 0;
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            count += entry.getRawValue() instanceof UnmodifiableConfig section ? countValues(section) : 1;
        }
        return count;
    }

    private static void gatherValues(UnmodifiableConfig config, List<String> path,
                                     Map<List<String>, ModConfigSpec.ConfigValue<?>> values) {
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            path.add(entry.getKey());
            if (entry.getRawValue() instanceof UnmodifiableConfig section) {
                gatherValues(section, path, values);
            } else if (entry.getRawValue() instanceof ModConfigSpec.ConfigValue<?> value) {
                values.put(List.copyOf(path), value);
            }
            path.remove(path.size() - 1);
        }
    }
}
