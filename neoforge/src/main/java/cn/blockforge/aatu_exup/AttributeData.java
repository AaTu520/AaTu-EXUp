package cn.blockforge.aatu_exup;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AttributeData {
    public static final int DEFAULT_SOUL = 3;
    public static final int MAX_SOUL = 99;

    private static final String ROOT = "aatu_exup_attribute_enhancement";
    private static final String SOUL = "soul";
    private static final String ATTRIBUTES = "attributes";
    private static final String LEGACY_ENHANCEMENTS = "enhancements";
    private static final String TYPE = "type";
    private static final String VALUE = "value";

    private AttributeData() {
    }

    public record Enhancement(String type, double value) {
    }

    public record Snapshot(int soul, List<Enhancement> enhancements) {
    }

    public static boolean has(ItemStack stack) {
        return !getEnhancements(stack).isEmpty();
    }

    public static boolean hasData(ItemStack stack) {
        return getTag(stack) != null;
    }

    public static int getSoul(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (tag == null || !tag.contains(SOUL, Tag.TAG_INT)) {
            return DEFAULT_SOUL;
        }
        return clampSoul(tag.getInt(SOUL));
    }

    public static boolean canEnhance(ItemStack stack) {
        return getSoul(stack) > 0;
    }

    public static boolean canInjectSoul(ItemStack stack) {
        return getSoul(stack) < MAX_SOUL;
    }

    public static boolean consumeSoul(ItemStack stack, int amount) {
        if (amount <= 0 || getSoul(stack) < amount) {
            return false;
        }
        setData(stack, getSoul(stack) - amount, getEnhancements(stack));
        return true;
    }

    public static int addSoul(ItemStack stack, int amount) {
        if (amount <= 0) {
            return 0;
        }
        int current = getSoul(stack);
        int added = Math.min(amount, MAX_SOUL - current);
        if (added <= 0) {
            return 0;
        }
        setData(stack, current + added, getEnhancements(stack));
        return added;
    }

    public static List<Enhancement> getEnhancements(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (tag == null) {
            return List.of();
        }

        Map<String, Double> totals = new LinkedHashMap<>();
        if (tag.contains(ATTRIBUTES, Tag.TAG_LIST)) {
            addEntries(totals, tag.getList(ATTRIBUTES, Tag.TAG_COMPOUND));
        } else if (tag.contains(LEGACY_ENHANCEMENTS, Tag.TAG_LIST)) {
            addEntries(totals, tag.getList(LEGACY_ENHANCEMENTS, Tag.TAG_COMPOUND));
        } else if (tag.contains(TYPE) && tag.contains(VALUE)) {
            addEntry(totals, tag);
        }

        List<Enhancement> enhancements = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            if (entry.getValue() > 0.0D) {
                enhancements.add(new Enhancement(entry.getKey(), entry.getValue()));
            }
        }
        return List.copyOf(enhancements);
    }

    public static Snapshot snapshot(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (tag == null) {
            return new Snapshot(DEFAULT_SOUL, List.of());
        }

        int soul = tag.contains(SOUL, Tag.TAG_INT) ? clampSoul(tag.getInt(SOUL)) : DEFAULT_SOUL;
        Map<String, Double> totals = new LinkedHashMap<>();
        if (tag.contains(ATTRIBUTES, Tag.TAG_LIST)) {
            addEntries(totals, tag.getList(ATTRIBUTES, Tag.TAG_COMPOUND));
        } else if (tag.contains(LEGACY_ENHANCEMENTS, Tag.TAG_LIST)) {
            addEntries(totals, tag.getList(LEGACY_ENHANCEMENTS, Tag.TAG_COMPOUND));
        } else if (tag.contains(TYPE) && tag.contains(VALUE)) {
            addEntry(totals, tag);
        }

        List<Enhancement> enhancements = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            if (entry.getValue() > 0.0D) {
                enhancements.add(new Enhancement(entry.getKey(), entry.getValue()));
            }
        }
        return new Snapshot(soul, List.copyOf(enhancements));
    }

    public static double getEnhancementValue(ItemStack stack, String type) {
        for (Enhancement enhancement : getEnhancements(stack)) {
            if (enhancement.type().equals(type)) {
                return enhancement.value();
            }
        }
        return 0.0D;
    }

    public static double add(ItemStack stack, String type, double value) {
        if (!AttributeEnhancement.isKnownType(type) || !Double.isFinite(value) || value <= 0.0D) {
            return 0.0D;
        }

        List<Enhancement> enhancements = new ArrayList<>(getEnhancements(stack));
        double current = getEnhancementValue(stack, type);
        double limit = enhancementLimit(type);
        double total = Math.min(limit, current + value);
        double added = total - current;
        if (!Double.isFinite(added) || added <= 0.0D) {
            return 0.0D;
        }

        boolean replaced = false;
        for (int index = 0; index < enhancements.size(); index++) {
            if (enhancements.get(index).type().equals(type)) {
                enhancements.set(index, new Enhancement(type, total));
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            enhancements.add(new Enhancement(type, total));
        }
        setData(stack, getSoul(stack), enhancements);
        return added;
    }

    private static void addEntries(Map<String, Double> totals, ListTag entries) {
        for (int index = 0; index < entries.size(); index++) {
            addEntry(totals, entries.getCompound(index));
        }
    }

    private static void addEntry(Map<String, Double> totals, CompoundTag entry) {
        String type = entry.getString(TYPE);
        double value = entry.getDouble(VALUE);
        if (!AttributeEnhancement.isKnownType(type) || !Double.isFinite(value) || value <= 0.0D) {
            return;
        }

        double limit = enhancementLimit(type);
        double current = totals.getOrDefault(type, 0.0D);
        double total = Math.min(limit, current + value);
        if (Double.isFinite(total) && total > 0.0D) {
            totals.put(type, total);
        }
    }

    private static void setData(ItemStack stack, int soul, List<Enhancement> enhancements) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> {
            CompoundTag data = new CompoundTag();
            data.putInt(SOUL, clampSoul(soul));
            ListTag entries = new ListTag();
            for (Enhancement enhancement : enhancements) {
                String type = enhancement.type();
                double value = clampEnhancement(type, enhancement.value());
                if (!AttributeEnhancement.isKnownType(type) || !Double.isFinite(value) || value <= 0.0D) {
                    continue;
                }
                CompoundTag entry = new CompoundTag();
                entry.putString(TYPE, type);
                entry.putDouble(VALUE, value);
                entries.addTag(entries.size(), entry);
            }
            data.put(ATTRIBUTES, entries);
            root.put(ROOT, data);
        });
    }

    private static double enhancementLimit(String type) {
        double limit = AaTu_EXUp_ModConfig.attributeLimit(type);
        return Double.isFinite(limit) ? Math.max(0.0D, limit) : 0.0D;
    }

    private static double clampEnhancement(String type, double value) {
        if (!Double.isFinite(value)) {
            return 0.0D;
        }
        return Math.min(enhancementLimit(type), Math.max(0.0D, value));
    }

    private static int clampSoul(int soul) {
        return Math.max(0, Math.min(MAX_SOUL, soul));
    }

    private static CompoundTag getTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return null;
        }

        CompoundTag root = data.copyTag();
        if (!root.contains(ROOT, Tag.TAG_COMPOUND)) {
            return null;
        }

        CompoundTag tag = root.getCompound(ROOT);
        return tag.isEmpty() ? null : tag;
    }
}
