package cn.blockforge.aatu_exup.gametest;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@GameTestHolder("aatu_exup")
@PrefixGameTestTemplate(false)
public class ConfiguredConfigGameTests {
    private static final ModConfigSpec SPEC = AaTu_EXUp_ModConfig.SPEC;

    @GameTest(template = "config_test_empty", timeoutTicks = 200)
    public static void actualConfiguredSavesPreserveOtherSettings(GameTestHelper helper) throws Exception {
        helper.assertTrue(SPEC.isLoaded(), "AaTu configuration must load without Configured");
        if (!ModList.get().isLoaded("configured")) {
            helper.assertTrue(AaTu_EXUp_ModConfig.ATTACK_DAMAGE_LIMIT.getAsDouble() > 0.0D,
                    "Gameplay configuration must remain usable without Configured");
            System.out.println("CONFIGURED_COMPAT: startup without Configured passed");
            helper.succeed();
            return;
        }

        Class<?> bridge = Class.forName("com.mrcrayfish.configured.util.NeoForgeConfigHelper");
        ModConfig config = (ModConfig) bridge.getMethod("getModConfig", String.class)
                .invoke(null, "aatu_exup-common.toml");
        helper.assertTrue(config != null, "Configured must find the registered common configuration");
        Class<?> configuredType = Class.forName("com.mrcrayfish.configured.impl.neoforge.NeoForgeConfig");
        Object configured = configuredType.getConstructor(ModConfig.class).newInstance(config);
        Map<List<String>, ModConfigSpec.ConfigValue<?>> values = new LinkedHashMap<>();
        gatherValues(SPEC.getValues(), new ArrayList<>(), values);
        helper.assertTrue(values.size() == 47, "Expected all 47 configuration values");
        Map<List<String>, Object> previous = snapshot(values);

        try {
            values.forEach((path, value) -> setValue(value, customized(value.getDefault())));
            SPEC.save();
            Map<List<String>, Object> expected = snapshot(values);
            for (Map.Entry<List<String>, ModConfigSpec.ConfigValue<?>> entry : values.entrySet()) {
                edit(configured, entry.getKey(), entry.getValue().getDefault());
                expected.put(entry.getKey(), entry.getValue().getDefault());
                helper.assertTrue(expected.equals(snapshot(values)), "Saved edit reset another value: " + entry.getKey());
                assertSavedValues(helper, config, expected);
            }

            edit(configured, List.of("attributes", "attack_damage_limit"), 250.0D);
            edit(configured, List.of("attributes", "attack_speed_limit"), 30.0D);
            edit(configured, List.of("attributes", "attack_damage_limit"), 300.0D);
            helper.assertTrue(AaTu_EXUp_ModConfig.ATTACK_DAMAGE_LIMIT.getAsDouble() == 300.0D,
                    "Attack damage must survive editing speed");
            helper.assertTrue(AaTu_EXUp_ModConfig.ATTACK_SPEED_LIMIT.getAsDouble() == 30.0D,
                    "Attack speed must survive editing damage");
            assertSavedValues(helper, config, snapshot(values));

            Optional<?> reset = (Optional<?>) configuredType.getMethod("restoreDefaultsTask").invoke(configured);
            helper.assertTrue(reset.isPresent(), "Configured must still support restoring defaults");
            ((Runnable) reset.orElseThrow()).run();
            SPEC.save();
            for (ModConfigSpec.ConfigValue<?> value : values.values()) {
                helper.assertTrue(value.getDefault().equals(value.get()), "Restoring defaults failed: " + value.getPath());
            }
            assertSavedValues(helper, config, snapshot(values));
            System.out.println("CONFIGURED_COMPAT: actual Configured verified 47 edits, sequential damage/speed, saved TOML, and full reset");
        } finally {
            previous.forEach((path, value) -> setValue(values.get(path), value));
            SPEC.save();
        }
        helper.succeed();
    }

    private static void assertSavedValues(GameTestHelper helper, ModConfig config,
                                         Map<List<String>, Object> expected) throws Exception {
        CommentedConfig saved = TomlFormat.instance().createParser().parse(Files.readString(config.getFullPath()));
        for (Map.Entry<List<String>, Object> entry : expected.entrySet()) {
            Object actual = saved.get(entry.getKey());
            Object wanted = entry.getValue();
            boolean equal = actual instanceof Number actualNumber && wanted instanceof Number wantedNumber
                    ? actualNumber.doubleValue() == wantedNumber.doubleValue() : wanted.equals(actual);
            helper.assertTrue(equal, "On-disk configuration differs at " + entry.getKey());
        }
    }

    private static void edit(Object configured, List<String> path, Object replacement) throws Exception {
        Class<?> configType = configured.getClass();
        Class<?> entryType = Class.forName("com.mrcrayfish.configured.api.IConfigEntry");
        Object root = configType.getMethod("createRootEntry").invoke(configured);
        Object value = findValue(root, path);
        if (value == null) throw new AssertionError("Configured entry missing: " + path);
        Class.forName("com.mrcrayfish.configured.api.IConfigValue").getMethod("set", Object.class)
                .invoke(value, replacement);
        Object result = configType.getMethod("update", entryType).invoke(configured, root);
        if (!(boolean) result.getClass().getMethod("asBoolean").invoke(result)) {
            throw new AssertionError("Configured failed to save " + path);
        }
    }

    private static Object findValue(Object entry, List<String> path) throws Exception {
        Class<?> entryType = Class.forName("com.mrcrayfish.configured.api.IConfigEntry");
        if ((boolean) entryType.getMethod("isLeaf").invoke(entry)) {
            Object value = entryType.getMethod("getValue").invoke(entry);
            ModConfigSpec.ConfigValue<?> configValue = (ModConfigSpec.ConfigValue<?>) value.getClass()
                    .getField("configValue").get(value);
            return configValue.getPath().equals(path) ? value : null;
        }
        for (Object child : (List<?>) entryType.getMethod("getChildren").invoke(entry)) {
            Object value = findValue(child, path);
            if (value != null) return value;
        }
        return null;
    }

    private static Object customized(Object defaultValue) {
        if (defaultValue instanceof Boolean bool) return !bool;
        if (defaultValue instanceof Integer number) return number + 1;
        if (defaultValue instanceof Double number) return number > 1.0D ? number + 1.0D : number * 0.75D;
        if (defaultValue instanceof String) return "minecraft:emerald";
        throw new AssertionError("Unexpected configuration value " + defaultValue);
    }

    @SuppressWarnings("unchecked")
    private static void setValue(ModConfigSpec.ConfigValue<?> configValue, Object value) {
        ((ModConfigSpec.ConfigValue<Object>) configValue).set(value);
    }

    private static Map<List<String>, Object> snapshot(Map<List<String>, ModConfigSpec.ConfigValue<?>> values) {
        Map<List<String>, Object> result = new LinkedHashMap<>();
        values.forEach((path, value) -> result.put(path, value.get()));
        return result;
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
