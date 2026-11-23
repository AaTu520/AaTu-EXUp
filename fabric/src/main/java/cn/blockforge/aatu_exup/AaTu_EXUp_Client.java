package cn.blockforge.aatu_exup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class AaTu_EXUp_Client implements ClientModInitializer {
    private static final String ENHANCEMENT_TOTAL = "tooltip.aatu_exup.enhancement_total";

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> addEnhancementTooltip(stack, lines));
    }

    private static void addEnhancementTooltip(ItemStack stack, List<Component> lines) {
        if (!AttributeEnhancement.isUpgradeable(stack)) {
            return;
        }

        var enhancements = AttributeData.getEnhancements(stack);
        int headerIndex = enhancementHeaderIndex(lines);
        Component soul = Component.translatable("tooltip.aatu_exup.soul", AttributeData.getSoul(stack))
                .withStyle(ChatFormatting.GOLD);
        if (headerIndex >= 0) {
            lines.add(headerIndex, soul);
        } else {
            lines.add(soul);
        }
        if (enhancements.isEmpty()) {
            return;
        }

        headerIndex = enhancementHeaderIndex(lines);
        if (headerIndex < 0) {
            lines.add(Component.translatable(ENHANCEMENT_TOTAL).withStyle(ChatFormatting.GOLD));
            headerIndex = lines.size() - 1;
        }
        for (AttributeData.Enhancement enhancement : enhancements) {
            lines.add(++headerIndex, Component.translatable(
                    "tooltip.aatu_exup.attribute_total",
                    AttributeEnhancement.formatValue(enhancement.type(), enhancement.value()),
                    Component.translatable(AttributeEnhancement.attributeNameKey(enhancement.type())))
                    .withStyle(ChatFormatting.AQUA));
        }
    }

    private static int enhancementHeaderIndex(List<Component> lines) {
        for (int index = 0; index < lines.size(); index++) {
            if (lines.get(index).getContents() instanceof TranslatableContents contents
                    && ENHANCEMENT_TOTAL.equals(contents.getKey())) {
                return index;
            }
        }
        return -1;
    }
}
