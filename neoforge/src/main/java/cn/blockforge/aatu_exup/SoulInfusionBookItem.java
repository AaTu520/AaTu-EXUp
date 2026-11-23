package cn.blockforge.aatu_exup;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class SoulInfusionBookItem extends Item {
    private static final String DIVIDER = "-----------------------------------";

    public SoulInfusionBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.GOLD);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int minimum = AaTu_EXUp_ModConfig.SOUL_INJECTION_MIN.getAsInt();
        int maximum = Math.max(minimum, AaTu_EXUp_ModConfig.SOUL_INJECTION_MAX.getAsInt());
        String amount = minimum == maximum ? Integer.toString(minimum) : minimum + "-" + maximum;

        tooltip.add(Component.literal(DIVIDER).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.aatu_exup.soul_infusion_book.lore_flavor")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.aatu_exup.soul_infusion_book.lore_use", amount)
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(DIVIDER).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.aatu_exup.soul_infusion_book.lore_cost",
                AaTu_EXUp_ModConfig.soulInfusionCostItemName().copy().withStyle(ChatFormatting.WHITE))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.aatu_exup.soul_infusion_book.lore_limit", AttributeData.MAX_SOUL)
                .withStyle(ChatFormatting.AQUA));
    }
}
