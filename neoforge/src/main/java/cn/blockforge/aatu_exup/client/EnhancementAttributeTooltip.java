package cn.blockforge.aatu_exup.client;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Multimap;
import cn.blockforge.aatu_exup.AaTu_EXUp_Mod;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.util.AttributeUtil;

public final class EnhancementAttributeTooltip {
    private EnhancementAttributeTooltip() {
    }

    public static void mergeEnhancements(Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
        // AttributeUtil supplies a temporary tooltip map for one equipment slot group.
        for (Holder<Attribute> attribute : new ArrayList<>(modifiers.keySet())) {
            Collection<AttributeModifier> entries = modifiers.get(attribute);
            int additiveCount = 0;
            double total = 0.0D;
            boolean hasEnhancement = false;
            ResourceLocation mergedId = AttributeUtil.FAKE_MERGED_ID;
            for (AttributeModifier modifier : entries) {
                if (modifier.operation() != AttributeModifier.Operation.ADD_VALUE) continue;
                additiveCount++;
                total += modifier.amount();
                hasEnhancement |= isEnhancement(modifier.id());
                if (modifier.id().equals(attribute.value().getBaseId())) {
                    mergedId = modifier.id();
                }
            }
            if (additiveCount < 2 || !hasEnhancement || !Double.isFinite(total)) continue;

            // Flat item bonuses can be summed without player base values or attribute caps.
            // Percentage operations remain separate so their meaning stays unchanged.
            entries.removeIf(modifier -> modifier.operation() == AttributeModifier.Operation.ADD_VALUE);
            modifiers.put(attribute, new AttributeModifier(mergedId, total, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static boolean isEnhancement(ResourceLocation id) {
        String namespace = id.getNamespace();
        String path = id.getPath();
        if (AaTu_EXUp_Mod.MOD_ID.equals(namespace) || "forbidden_knowledge".equals(namespace)) {
            return path.startsWith("enhanced_");
        }
        return "aatu_ut".equals(namespace)
                && (path.startsWith("transferred_") || path.startsWith("base_") || path.startsWith("hat_"));
    }
}
