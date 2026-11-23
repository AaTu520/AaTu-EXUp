package cn.blockforge.aatu_exup.compat;

import cn.blockforge.aatu_exup.AaTu_EXUp_Mod;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;

import java.util.ArrayList;
import java.util.List;

public final class ConfiguredConfigCompatibility {
    private ConfiguredConfigCompatibility() {
    }

    public static void applyChanges(String modId, CommentedConfig original, UnmodifiableConfig changes) {
        if (!AaTu_EXUp_Mod.MOD_ID.equals(modId)) {
            original.putAll(changes);
            return;
        }

        // Configured's patch only contains changed values. A shallow putAll would
        // replace whole sections, and NeoForge would default their missing values.
        if (original instanceof ConcurrentCommentedConfig concurrent) {
            concurrent.bulkCommentedUpdate(config -> {
                applyValues(config, changes, new ArrayList<>());
                return null;
            });
        } else {
            applyValues(original, changes, new ArrayList<>());
        }
    }

    private static void applyValues(CommentedConfig original, UnmodifiableConfig changes, List<String> path) {
        for (UnmodifiableConfig.Entry entry : changes.entrySet()) {
            path.add(entry.getKey());
            Object value = entry.getRawValue();
            if (value instanceof UnmodifiableConfig section) {
                applyValues(original, section, path);
            } else {
                original.set(path, value);
            }
            path.remove(path.size() - 1);
        }
    }
}
