package cn.blockforge.aatu_exup;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class AttributeEnhancementBookItem extends Item {
    private static final String DIVIDER = "-----------------------------------";

    public AttributeEnhancementBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.AQUA);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(DIVIDER).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.aatu_exup.attribute_enhancement_book.lore_flavor")
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.aatu_exup.attribute_enhancement_book.lore_use")
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(DIVIDER).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.aatu_exup.attribute_enhancement_book.lore_cost",
                AaTu_EXUp_ModConfig.enhancementCostItemName().copy().withStyle(ChatFormatting.WHITE))
                .withStyle(ChatFormatting.WHITE)
                .append(Component.translatable("item.aatu_exup.attribute_enhancement_book.lore_point")
                        .withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.translatable("item.aatu_exup.attribute_enhancement_book.lore_penetration")
                .withStyle(ChatFormatting.RED));
    }
}
