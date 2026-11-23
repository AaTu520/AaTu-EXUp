package cn.blockforge.aatu_exup;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public final class AttributeEnhancementRecipe implements SmithingRecipe {
    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        ItemStack base = input.base();
        if (!input.template().is(AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_BOOK)
                || !input.addition().is(AaTu_EXUp_ModConfig.enhancementCostItem())
                || !AttributeEnhancement.isUpgradeable(base)) {
            return false;
        }

        AttributeData.Snapshot data = AttributeData.snapshot(base);
        return data.soul() > 0 && AttributeEnhancement.hasAvailableAttribute(base, data.enhancements());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack base = input.base();
        AttributeData.Snapshot data = AttributeData.snapshot(base);
        if (data.soul() <= 0) {
            return ItemStack.EMPTY;
        }

        AttributeEnhancement.Roll roll = AttributeEnhancement.nextRoll(base, data.enhancements());
        if (roll == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = base.copy();
        if (!AttributeData.consumeSoul(result, 1)) {
            return ItemStack.EMPTY;
        }
        if (AttributeData.add(result, roll.type(), roll.value()) <= 0.0D) {
            return ItemStack.EMPTY;
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.is(AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_BOOK);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return AttributeEnhancement.isUpgradeable(stack) && AttributeData.canEnhance(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.is(AaTu_EXUp_ModConfig.enhancementCostItem());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_BOOK),
                Ingredient.of(AaTu_EXUp_ModConfig.enhancementCostItem()));
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AaTu_EXUp_Mod.ATTRIBUTE_ENHANCEMENT_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static final class Serializer implements RecipeSerializer<AttributeEnhancementRecipe> {
        private static final MapCodec<AttributeEnhancementRecipe> CODEC = MapCodec.unit(AttributeEnhancementRecipe::new);
        private static final StreamCodec<RegistryFriendlyByteBuf, AttributeEnhancementRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::encode, Serializer::decode);

        @Override
        public MapCodec<AttributeEnhancementRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AttributeEnhancementRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void encode(RegistryFriendlyByteBuf buffer, AttributeEnhancementRecipe recipe) {
        }

        private static AttributeEnhancementRecipe decode(RegistryFriendlyByteBuf buffer) {
            return new AttributeEnhancementRecipe();
        }
    }
}
